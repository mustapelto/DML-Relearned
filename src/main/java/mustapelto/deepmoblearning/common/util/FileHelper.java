package mustapelto.deepmoblearning.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import mustapelto.deepmoblearning.DMLRelearned;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.*;

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

    public static JsonObject readObject(File file) throws IOException {
        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            JsonParser parser = new JsonParser();
            reader.setLenient(true);
            JsonElement element = parser.parse(reader);
            return element.getAsJsonObject();
        }
    }

    public static JsonObject readObject(String internalFile) throws IOException {
        try (
                InputStream in = DMLRelearned.class.getResourceAsStream(internalFile);
                JsonReader reader = new JsonReader(new InputStreamReader(in))
                ){
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

    public static JsonArray readArray(String internalFile) throws IOException {
        try (
                InputStream in = DMLRelearned.class.getResourceAsStream(internalFile);
                JsonReader reader = new JsonReader(new InputStreamReader(in))
        ){
            JsonParser parser = new JsonParser();
            reader.setLenient(true);
            JsonElement element = parser.parse(reader);
            return element.getAsJsonArray();
        }
    }

    public static void writeObject(JsonObject object, File file) throws IOException {
        try (JsonWriter writer = new JsonWriter(new FileWriter(file))) {
            writer.setIndent("  ");
            Streams.write(object, writer);
            writer.flush();
        }
    }

    public static void writeArray(JsonArray array, File file) throws IOException {
        try (JsonWriter writer = new JsonWriter(new FileWriter(file))) {
            writer.setIndent("  ");
            Streams.write(array, writer);
            writer.flush();
        }
    }
}
