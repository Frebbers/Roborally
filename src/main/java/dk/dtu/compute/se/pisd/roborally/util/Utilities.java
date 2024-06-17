package dk.dtu.compute.se.pisd.roborally.util;

import javafx.scene.control.TextField;

public class Utilities {
    public static <T extends Enum<T>> T toEnum(Class<T> enumType, String enumString) {
        try {
            return Enum.valueOf(enumType, enumString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown enum type: " + enumString);
        }
    }

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

    public static void restrictToNumbersDotsAndColons(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9.:]*")) {
                textField.setText(oldValue);
            }
        });
    }
}
