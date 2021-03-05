package mustapelto.deepmoblearning.common.util;

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

    public static JsonObject readObject(File file) throws IOException {
        JsonReader reader = new JsonReader(new FileReader(file));
        JsonParser parser = new JsonParser();
        reader.setLenient(true);
        JsonElement json;
        try {
            json = parser.parse(reader);
        } catch (Exception e) {
            DMLRelearned.logger.error("Error reading JSON from file {}! Error message: {}", file.getName(), e.getMessage());
            reader.close();
            return null;
        }

        if (!json.isJsonObject()) {
            DMLRelearned.logger.error("Invalid JSON data in file: {}", file.getName());
            return null;
        }

        return json.getAsJsonObject();
    }
}
