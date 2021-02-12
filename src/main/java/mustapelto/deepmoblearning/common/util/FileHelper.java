package mustapelto.deepmoblearning.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import mustapelto.deepmoblearning.DMLRelearned;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileHelper {
    public static File configRoot;
    public static File configDML;

    public static void init(FMLPreInitializationEvent event) {
        configRoot = event.getModConfigurationDirectory();
        configDML = new File(configRoot, "dml_relearned");

        if (!configDML.exists() && !configDML.mkdirs()) {
            DMLRelearned.logger.error(String.format("Could not create config directory (%s). Mod will not function properly.", configDML.toString()));
        }
    }

    public static void copyFromJar(String internalPath, Path target) {
        try (InputStream input = DMLRelearned.class.getResourceAsStream(internalPath)) {
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            DMLRelearned.logger.error("Error extracting default file from mod jar!");
            DMLRelearned.logger.error(e);
        }
    }

    public static JsonObject readObject(File file) throws IOException {
        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            JsonParser parser = new JsonParser();
            reader.setLenient(true);
            JsonElement element = parser.parse(reader);
            return element.getAsJsonObject();
        }
    }

    public static JsonArray readArray(File file) throws IOException {
        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            JsonParser parser = new JsonParser();
            reader.setLenient(true);
            JsonElement element = parser.parse(reader);
            return element.getAsJsonArray();
        }
    }
}
