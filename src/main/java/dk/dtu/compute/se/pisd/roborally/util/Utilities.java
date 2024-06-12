package dk.dtu.compute.se.pisd.roborally.util;

public class Utilities {
    public static <T extends Enum<T>> T toEnum(Class<T> enumType, String enumString) {
        try {
            return Enum.valueOf(enumType, enumString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown enum type: " + enumString);
        }
    }
}
