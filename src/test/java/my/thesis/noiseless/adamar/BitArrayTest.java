package my.thesis.noiseless.adamar;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BitArrayTest {

    @Test
    public void firstConstructorTest() {
        int expectedLength = 5;
        BitArray b = new BitArray(expectedLength);
        assertEquals(b.length(), expectedLength);
    }

    @Test(expected = IllegalArgumentException.class)
    public void firstConstructorWithIncorrectSizeTest() {
        int expectedLength = 0;
        new BitArray(expectedLength);
        fail();
    }

    @Test
    public void secondConstructorTest() {
        byte[] bits = {1, 2, 3, 4, 5};
        int expectedLength = Byte.SIZE * bits.length;
        BitArray b = new BitArray(bits);
        assertEquals(b.length(), expectedLength);
    }

    @Test
    public void secondConstructorNegativeTest() {
        byte[] bits = {0, -1, -23, -42, -128};
        int expectedLength = Byte.SIZE * bits.length;
        BitArray b = new BitArray(bits);
        assertEquals(b.length(), expectedLength);
    }

    @Test
    public void copyToTest() {
        byte[] bits = {1, 2, 4, 5, 0, -1, -42, -128};
        BitArray b = new BitArray(bits);
        byte[] result = new byte[bits.length];
        b.CopyTo(result, 0);
        for(int i = 0; i < bits.length; ++i) {
            assertEquals(bits[i], result[i]);
        }
    }

}