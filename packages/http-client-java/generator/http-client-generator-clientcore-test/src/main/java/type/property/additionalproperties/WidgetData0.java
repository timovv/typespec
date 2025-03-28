package type.property.additionalproperties;

import io.clientcore.core.annotations.Metadata;
import io.clientcore.core.annotations.TypeConditions;
import io.clientcore.core.serialization.json.JsonReader;
import io.clientcore.core.serialization.json.JsonSerializable;
import io.clientcore.core.serialization.json.JsonToken;
import io.clientcore.core.serialization.json.JsonWriter;
import java.io.IOException;

/**
 * The WidgetData0 model.
 */
@Metadata(conditions = { TypeConditions.IMMUTABLE })
public final class WidgetData0 implements JsonSerializable<WidgetData0> {
    /*
     * The kind property.
     */
    @Metadata(generated = true)
    private final String kind = "kind0";

    /*
     * The fooProp property.
     */
    @Metadata(generated = true)
    private final String fooProp;

    /**
     * Creates an instance of WidgetData0 class.
     * 
     * @param fooProp the fooProp value to set.
     */
    @Metadata(generated = true)
    public WidgetData0(String fooProp) {
        this.fooProp = fooProp;
    }

    /**
     * Get the kind property: The kind property.
     * 
     * @return the kind value.
     */
    @Metadata(generated = true)
    public String getKind() {
        return this.kind;
    }

    /**
     * Get the fooProp property: The fooProp property.
     * 
     * @return the fooProp value.
     */
    @Metadata(generated = true)
    public String getFooProp() {
        return this.fooProp;
    }

    /**
     * {@inheritDoc}
     */
    @Metadata(generated = true)
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeStringField("kind", this.kind);
        jsonWriter.writeStringField("fooProp", this.fooProp);
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of WidgetData0 from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of WidgetData0 if the JsonReader was pointing to an instance of it, or null if it was
     * pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the WidgetData0.
     */
    @Metadata(generated = true)
    public static WidgetData0 fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            String fooProp = null;
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("fooProp".equals(fieldName)) {
                    fooProp = reader.getString();
                } else {
                    reader.skipChildren();
                }
            }
            return new WidgetData0(fooProp);
        });
    }
}
