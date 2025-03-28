package type.property.optional;

/**
 * Defines values for UnionFloatLiteralPropertyProperty.
 */
public enum UnionFloatLiteralPropertyProperty {
    /**
     * Enum value 1.25.
     */
    ONE_TWO_FIVE(1.25),

    /**
     * Enum value 2.375.
     */
    TWO_THREE_SEVEN_FIVE(2.375);

    /**
     * The actual serialized value for a UnionFloatLiteralPropertyProperty instance.
     */
    private final double value;

    UnionFloatLiteralPropertyProperty(double value) {
        this.value = value;
    }

    /**
     * Parses a serialized value to a UnionFloatLiteralPropertyProperty instance.
     * 
     * @param value the serialized value to parse.
     * @return the parsed UnionFloatLiteralPropertyProperty object, or null if unable to parse.
     */
    public static UnionFloatLiteralPropertyProperty fromDouble(double value) {
        UnionFloatLiteralPropertyProperty[] items = UnionFloatLiteralPropertyProperty.values();
        for (UnionFloatLiteralPropertyProperty item : items) {
            if (Double.doubleToLongBits(item.toDouble()) == Double.doubleToLongBits(value)) {
                return item;
            }
        }
        return null;
    }

    /**
     * De-serializes the instance to double value.
     * 
     * @return the double value.
     */
    public double toDouble() {
        return this.value;
    }
}
