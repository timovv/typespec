// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package type.property.valuetypes.models;

import com.azure.core.annotation.Generated;
import com.azure.core.annotation.Immutable;
import com.azure.json.JsonReader;
import com.azure.json.JsonSerializable;
import com.azure.json.JsonToken;
import com.azure.json.JsonWriter;
import java.io.IOException;
import java.util.List;

/**
 * Model with collection string properties.
 */
@Immutable
public final class CollectionsStringProperty implements JsonSerializable<CollectionsStringProperty> {
    /*
     * Property
     */
    @Generated
    private final List<String> property;

    /**
     * Creates an instance of CollectionsStringProperty class.
     * 
     * @param property the property value to set.
     */
    @Generated
    public CollectionsStringProperty(List<String> property) {
        this.property = property;
    }

    /**
     * Get the property property: Property.
     * 
     * @return the property value.
     */
    @Generated
    public List<String> getProperty() {
        return this.property;
    }

    /**
     * {@inheritDoc}
     */
    @Generated
    @Override
    public JsonWriter toJson(JsonWriter jsonWriter) throws IOException {
        jsonWriter.writeStartObject();
        jsonWriter.writeArrayField("property", this.property, (writer, element) -> writer.writeString(element));
        return jsonWriter.writeEndObject();
    }

    /**
     * Reads an instance of CollectionsStringProperty from the JsonReader.
     * 
     * @param jsonReader The JsonReader being read.
     * @return An instance of CollectionsStringProperty if the JsonReader was pointing to an instance of it, or null if
     * it was pointing to JSON null.
     * @throws IllegalStateException If the deserialized JSON object was missing any required properties.
     * @throws IOException If an error occurs while reading the CollectionsStringProperty.
     */
    @Generated
    public static CollectionsStringProperty fromJson(JsonReader jsonReader) throws IOException {
        return jsonReader.readObject(reader -> {
            List<String> property = null;
            while (reader.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = reader.getFieldName();
                reader.nextToken();

                if ("property".equals(fieldName)) {
                    property = reader.readArray(reader1 -> reader1.getString());
                } else {
                    reader.skipChildren();
                }
            }
            return new CollectionsStringProperty(property);
        });
    }
}
