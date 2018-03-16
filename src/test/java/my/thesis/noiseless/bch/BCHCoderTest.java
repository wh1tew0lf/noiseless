package my.thesis.noiseless.bch;

import org.junit.Test;

import java.util.Random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class BCHCoderTest {

    @Test
    public void simpleTest() {
        int dataLenMax = 4094;
        int blockSize = 255;
        Random rnd = new Random();
        for (int i = 0; i < 100; ++i) {
            System.out.println(i);
            System.out.println(1 + (rnd.nextInt(50) % dataLenMax));
            byte[] data1 = new byte[1 + (rnd.nextInt(50) % dataLenMax)];
            for (int j = 0; j < data1.length; ++j) {
                data1[j] = (byte) (rnd.nextInt(50) % 256);
            }

            int t = (0 == blockSize)
                    ? (1 + rnd.nextInt(50) % ((int) Math.round(data1.length * 0.17)))
                    : (1 + rnd.nextInt(50) % ((int) Math.round(blockSize * 0.17)));

            byte[] data2 = null;
            try {
                data2 = BCHCoder.encode_systematic(data1, t, blockSize);
            } catch (BchException e) {
                fail(e.getMessage());
            }
            assertNotNull(data2);
            int errCnt = 0;
            for (int j = 0; j < data1.length; ++j) {
                //if (data1[j] != data2[j + sizeof(int)])
                if (data1[j] != data2[j + 4]) {
                    //break;
                    ++errCnt;
                }
            }
            if (errCnt > 0) {
                System.out.print("Errors cnt " + errCnt + " " + data1.length + " !");
            }
            byte[] data3 = null;
            try {
                data3 = BCHCoder.decode_systematic(data2, t, blockSize);
            } catch (BchException e) {
                /*BCHCoder.WriteBits(data1);
                for (int k = 0; k < data1.length; ++k) {
                    System.out.println(data1[k]);
                }
                System.out.println("");
                BCHCoder.WriteBits(data2);
                for (int k = 0; k < data2.length; ++k) {
                    System.out.println(data2[k]);
                }
                System.out.println("");*/

                fail(e.getMessage());
            }

            assertNotNull(data3);
            assertEquals(data3.length, data1.length);

            for (int j = 0; j < data3.length; ++j) {
                int zero = (int) Math.pow(2, Byte.SIZE - 1);
                int mod = (int) Math.pow(2, Byte.SIZE);
                int first = (zero + data3[j]) % mod;
                int second = (zero + data1[j]) % mod;
                assertEquals("Incorrect in byte-to-byte comparing", first, second);
            }
        }
    }
}
