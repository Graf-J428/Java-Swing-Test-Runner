package Tests;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Testklasse03 extends Testsammelklasse {
    public Testklasse03(){

    }
    int erg =0;

    @Test
    public void testMethode01(){
        erg=+5;
    }
    @Test
    public void testMethode02(){

        erg=-5;
        assertEquals("Erwartet wurde 5, aber erg war: " + erg, 5, erg);
    }
}
