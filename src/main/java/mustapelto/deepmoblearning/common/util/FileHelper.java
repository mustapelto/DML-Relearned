package mustapelto.deepmoblearning.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import mustapelto.deepmoblearning.DMLRelearned;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Helper methods for file reading
 */
public class FileHelper {
    public static File configRoot;
    public static File configDML;

    public static void init(FMLPreInitializationEvent event) {
        configRoot = event.getModConfigurationDirectory();
        configDML = new File(configRoot, "dml_relearned");

        if (!configDML.exists() && !configDML.mkdirs()) {
            DMLRelearned.logger.error("Could not create config directory: {}! This will cause problems.", configDML.toString());
        }
    }

    public static void copyFromJar(String internalPath, Path target) {
        try (InputStream input = DMLRelearned.class.getResourceAsStream(internalPath)) {
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            DMLRelearned.logger.error("Error extracting default config file from mod jar: {}! Error message: {}", internalPath, e.getMessage());
        }
    }

    public static Optional<JsonObject> readObject(File file) {
        return readRootElement(file).map(jsonElement -> jsonElement.isJsonObject() ? jsonElement.getAsJsonObject() : null);
    }

    public static Optional<JsonArray> readArray(File file) {
        return readRootElement(file).map(jsonElement -> jsonElement.isJsonArray() ? jsonElement.getAsJsonArray() : null);
    }

    private static Optional<JsonElement> readRootElement(File file) {
        JsonElement result;
        try (JsonReader reader = new JsonReader(new FileReader(file))){
            JsonParser parser = new JsonParser();
            reader.setLenient(true);
            result = parser.parse(reader);
        } catch (Exception e) {
            DMLRelearned.logger.error("Error reading JSON from file {}! Error message: {}", file.getName(), e.getMessage());
            return Optional.empty();
        }
        return Optional.of(result);
    }
}
