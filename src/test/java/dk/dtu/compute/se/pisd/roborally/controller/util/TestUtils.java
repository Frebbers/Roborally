package dk.dtu.compute.se.pisd.roborally.controller.util;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TestUtils {

    /**
     * Test if a file in 'src/test/expected/[name].png' contains the image.
     * Writes the image to that file if it does not exist, and
     * saves a 'src/test/expected/[name].err.png' file if it does not match.
     * @param name the name of the test.
     * @param image the image to test.
     * @throws IOException
     */
    public static void goldenTest(String name, Image image) throws IOException {
        File f = new File("src/test/expected/" + name + ".png");
        Image before = null;
        try {
            before = ImageUtils.readPNG(f);
        } catch (IOException e) {
            // The image did not exist. Save the current one.
            ImageUtils.writePNG(f, image);
            return;
        }

        if (!before.equals(image)) {
            File ferr = new File("src/test/expected/" + name + ".err.png");
            ImageUtils.writePNG(ferr, image);
            throw new AssertionError("Golden test does not match: " + f + " and " + ferr);
        }
    }
}
