package Tests;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Testklasse01 extends Testsammelklasse {

    public Testklasse01() {

    }
    @Test
    public void testAddition() throws InterruptedException {
        Thread.sleep(2000);
        int sum = 2 + 3;
        assertEquals(5, sum);
    }

    @Test
    public void testStringLength() throws InterruptedException {
        Thread.sleep(2000);

        String text = "JUnit";
        assertEquals(5, text.length());

    }
}
