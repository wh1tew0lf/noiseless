package my.thesis.noiseless.adamar;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CoderTest {

    @Test
    public void EncodeDecodeTest() {
        byte blockSize = (byte) 5;
        byte[] initial = {1, 15, 127, 6, 45};
        byte[] encoded = Coder.encode(initial, blockSize);
        byte[] decoded = Coder.decode(encoded, blockSize);
        assertEquals(initial.length, decoded.length);

        for (int i = 0; i < initial.length; i++) {
            assertEquals(initial[i], decoded[i]);
        }
    }

    @Test
    public void EncodeDecodeWithErrorsTest() {
        byte blockSize = (byte) 5;
        byte[] initial = {1, 15, 127, 6, 45};
        byte[] encoded = Coder.encode(initial, blockSize);

        BitArray withErrors = new BitArray(encoded);
        int errorCount = 4;
        for (int i = 0; i < errorCount; ++i) {
            withErrors.swapBit(i);
        }
        withErrors.CopyTo(encoded);

        byte[] decoded = Coder.decode(encoded, blockSize);
        assertEquals(initial.length, decoded.length);

        for (int i = 0; i < initial.length; i++) {
            assertEquals(initial[i], decoded[i]);
        }
    }
}
