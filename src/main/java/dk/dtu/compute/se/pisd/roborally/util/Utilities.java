package dk.dtu.compute.se.pisd.roborally.util;

import javafx.scene.control.TextField;

public class Utilities {

    /**
     * Converts a string to the corresponding enum constant of the specified enum type.
     *
     * @param <T>       the type of the enum.
     * @param enumType  the class of the enum type.
     * @param enumString the string to convert to an enum constant.
     * @return the corresponding enum constant.
     * @throws IllegalArgumentException if the string does not match any enum constant.
     */
    public static <T extends Enum<T>> T toEnum(Class<T> enumType, String enumString) {
        try {
            return Enum.valueOf(enumType, enumString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown enum type: " + enumString);
        }
    }

    /**
     * Converts an integer to the corresponding enum constant of the specified enum type by matching the value returned
     * by a "getValue" method in the enum type.
     *
     * @param <T>       the type of the enum.
     * @param enumType  the class of the enum type.
     * @param enumValue the integer value to convert to an enum constant.
     * @return the corresponding enum constant.
     * @throws IllegalArgumentException if the value does not match any enum constant or if reflection fails.
     */
    public static <T extends Enum<T>> T toEnum(Class<T> enumType, int enumValue) {
        try {
            T[] enumConstants = enumType.getEnumConstants();
            for (T enumConstant : enumConstants) {
                Integer value = (Integer) enumType.getMethod("getValue").invoke(enumConstant);
                if (value.equals(enumValue)) {
                    return enumConstant;
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Enum " + enumType.getName() + " does not have a reachable getValue method or another reflection issue occurred.", e);
        }
        throw new IllegalArgumentException("Unknown enum value: " + enumValue);
    }

    /**
     * Restricts the input of a TextField to only allow numbers, dots, and colons.
     *
     * @param textField the TextField to restrict input for.
     */
    public static void restrictToNumbersDotsAndColons(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9.:]*")) {
                textField.setText(oldValue);
            }
        });
    }

    /**
     * Restricts the length of input in a TextField to a specified maximum amount of characters.
     *
     * @param textField the TextField to restrict input length for.
     * @param amount    the maximum number of characters allowed.
     */
    public static void restrictTextLength(TextField textField, int amount) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > amount) {
                textField.setText(oldValue);
            }
        });
    }
}
