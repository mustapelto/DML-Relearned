package mustapelto.deepmoblearning.common.metadata;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mustapelto.deepmoblearning.DMLRelearned;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class MetadataStore<T extends Metadata> {
    protected ImmutableMap<String, T> data;
    protected Class<T> metadataClass;

    protected MetadataStore(Class<T> metadataClass) {
        this.metadataClass = metadataClass;
        data = ImmutableMap.of();
    }

    public void init(JsonArray json) {
        ImmutableMap.Builder<String, T> builder = ImmutableMap.builder();

        for (int i = 0; i < json.size(); i++) {
            JsonElement entry = json.get(i);
            if (!entry.isJsonObject()) {
                DMLRelearned.logger.warn("Invalid entry at index {} in Data Model config (root array elements must be objects)", i);
                continue;
            }

            T metadata;
            try {
                Constructor<T> constructor = metadataClass.getConstructor(JsonObject.class);
                metadata = constructor.newInstance(entry.getAsJsonObject());
            } catch (IllegalArgumentException e) {
                DMLRelearned.logger.warn("Invalid object structure at index {} in Data Model config (invalid or missing keys)", i);
                continue;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                DMLRelearned.logger.error("Error creating instance of metadata class {}", metadataClass.getName());
                continue;
            }

            builder.put(metadata.getID(), metadata);
        }

        data = builder.build();
    }

    public void finalizeData() {
        data.values().forEach(Metadata::finalizeData);
    }

    public ImmutableList<T> getMetadataList() {
        return data.values().asList();
    }

    public Optional<T> get(String key) {
        T value = data.get(key);
        return (value != null) ? Optional.of(value) : Optional.empty();
    }

    public int size() {
        return data.size();
    }
}
