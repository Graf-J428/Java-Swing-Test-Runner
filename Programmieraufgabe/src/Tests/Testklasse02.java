package Tests;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Testklasse02 extends Testsammelklasse {

    public Testklasse02() {

    }
    @Test
    public void testFailureExample() {

        assertTrue("Dieser Test schlägt absichtlich fehl", false);
    }

    @Test
    public void testUppercase() {
        String text = "java";
        assertEquals("JAVA", text.toUpperCase());
    }

    @Ignore
    @Test
    public void testIgnored() {
        // wird ignoriert
    }
}
