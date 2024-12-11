// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) TypeSpec Code Generator.

package type.property.valuetypes.models;

/**
 * Defines values for UnionIntLiteralPropertyProperty.
 */
public enum UnionIntLiteralPropertyProperty {
    /**
     * Enum value 42.
     */
    FOUR_TWO(42),

    /**
     * Enum value 43.
     */
    FOUR_THREE(43);

    /**
     * The actual serialized value for a UnionIntLiteralPropertyProperty instance.
     */
    private final int value;

    UnionIntLiteralPropertyProperty(int value) {
        this.value = value;
    }

    /**
     * Parses a serialized value to a UnionIntLiteralPropertyProperty instance.
     * 
     * @param value the serialized value to parse.
     * @return the parsed UnionIntLiteralPropertyProperty object, or null if unable to parse.
     */
    public static UnionIntLiteralPropertyProperty fromInt(int value) {
        UnionIntLiteralPropertyProperty[] items = UnionIntLiteralPropertyProperty.values();
        for (UnionIntLiteralPropertyProperty item : items) {
            if (item.toInt() == value) {
                return item;
            }
        }
        return null;
    }

    /**
     * De-serializes the instance to int value.
     * 
     * @return the int value.
     */
    public int toInt() {
        return this.value;
    }
}
