package my.thesis.noiseless.bch;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static my.thesis.noiseless.bch.BCHCoder.decode_systematic;
import static my.thesis.noiseless.bch.BCHCoder.encode_systematic;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class BCHCoderTest {

@Test
    public void randomTest() {
    int dataLenMax = 4094;
    int blockSize = 255;
    Random rnd = new Random();
    for (int i = 0; i < 100; ++i) {
        System.out.println(i);
        byte[] data1 = new byte[1 + (rnd.nextInt(100) % dataLenMax)];
        System.out.println("Длина массива данных" + data1.length + " Двоичная сс " + Integer.toBinaryString(data1.length));
        for (int j = 0; j < data1.length; ++j) {
            data1[j] = (byte) (rnd.nextInt(100));
        }
        System.out.println("DATA1->\n" + Arrays.toString(data1));////////

        int t = (3 + rnd.nextInt(50));
        System.out.println("Количество ошибок: " + t);
        try {
            byte[] data2 = encode_systematic(data1, t, blockSize);
            System.out.println("DATA2->\n" + Arrays.toString(data2));////////
            int errCnt = 0;

            for (int j = 0; j < data1.length; ++j) {
                if (data1[j] != data2[j + Integer.SIZE / Byte.SIZE]) {
                    ++errCnt;
                }
            }
            byte[] data3;
            data3 = decode_systematic(data2, t, blockSize);
            if (data3.length != data1.length) {
                System.out.println(Arrays.toString(data1));
                System.out.println(Arrays.toString(data3));
                throw new BchException("Incorrect len!");

            }
            boolean error = false;
            for (int j = 0; j < data3.length; ++j) {
                if (data1[j] != data3[j]) {
                    System.out.println(Arrays.toString(data1));
                    System.out.println(Arrays.toString(data3));
                    System.out.println("Позиция " + j);
                    error = true;

                }
            }
            if (error) throw new BchException("Incorrect value!");
        } catch (BchException e){
            fail(e.getMessage());
        }
    }

    }
@Test
    public void tEquals2ArrayLenEquals91Integer65(){
        int dataLenMax = 4094;
        int blockSize = 255;
        Random rnd = new Random();
        for (int i = 0; i < 1000; ++i) {
            System.out.println(i);
            //  System.out.println(1 + (rnd.nextInt(50) % dataLenMax));
            //  byte[] data1 = new byte[1 + (rnd.nextInt(100) % dataLenMax)];
            byte[] data1 = new byte[91];
            System.out.println("Длина массива данных"+data1.length+" Двоичная сс "+Integer.toBinaryString(data1.length));
            for (int j = 0; j < data1.length; ++j) {
                //  data1[j] = (byte) (rnd.nextInt(100));
                data1[j] = (byte) (65);
                //  data1[j] = (byte) (65);
            }
            System.out.println("DATA1->\n"+Arrays.toString(data1));////////

            int t = (0 == blockSize)
                    ? (1 + rnd.nextInt(50) % ((int) Math.round(data1.length * 0.17)))
                    //: (1 + rnd.nextInt(50) % ((int) Math.round(blockSize * 0.17)));
                    // : (1 + rnd.nextInt(50));
                    : (2);
            System.out.println("Количество ошибок: " + t);
            try {
                byte[] data2 = encode_systematic(data1, t, blockSize);
                System.out.println("DATA2->\n" + Arrays.toString(data2));////////
                int errCnt = 0;

                for (int j = 0; j < data1.length; ++j) {
                    if (data1[j] != data2[j + Integer.SIZE / Byte.SIZE]) {
                        ++errCnt;
                    }
                }
                byte[] data3;
                data3 = decode_systematic(data2, t, blockSize);
                if (data3.length != data1.length) {
                    System.out.println(Arrays.toString(data1));
                    System.out.println(Arrays.toString(data3));
                    throw new BchException("Incorrect len!");

                }
                boolean error = false;
                for (int j = 0; j < data3.length; ++j) {
                    if (data1[j] != data3[j]) {
                        System.out.println(Arrays.toString(data1));
                        System.out.println(Arrays.toString(data3));
                        System.out.println("Позиция " + j);
                        error = true;

                    }
                }
                if (error) throw new BchException("Incorrect value!");
            } catch (BchException e){
                fail(e.getMessage());
            }
        }
    }

    public void tEquals2ArrayLenEquals95Integer84(){
        int dataLenMax = 4094;
        int blockSize = 255;
        Random rnd = new Random();
        for (int i = 0; i < 1000; ++i) {
            System.out.println(i);
            //  System.out.println(1 + (rnd.nextInt(50) % dataLenMax));
            // byte[] data1 = new byte[1 + (rnd.nextInt(100) % dataLenMax)];
            byte[] data1 = new byte[95];
            System.out.println("Длина массива данных"+data1.length+" Двоичная сс "+Integer.toBinaryString(data1.length));
            for (int j = 0; j < data1.length; ++j) {
                //data1[j] = (byte) (rnd.nextInt(100));
                data1[j] = (byte) (84);
                //  data1[j] = (byte) (65);
            }
            System.out.println("DATA1->\n"+Arrays.toString(data1));////////

            int t = (0 == blockSize)
                    ? (1 + rnd.nextInt(50) % ((int) Math.round(data1.length * 0.17)))
                    //: (1 + rnd.nextInt(50) % ((int) Math.round(blockSize * 0.17)));
                    // : (1 + rnd.nextInt(50));
                    : (2);
            System.out.println("Количество ошибок: " + t);
            try {
                byte[] data2 = encode_systematic(data1, t, blockSize);
                System.out.println("DATA2->\n" + Arrays.toString(data2));////////
                int errCnt = 0;

                for (int j = 0; j < data1.length; ++j) {
                    if (data1[j] != data2[j + Integer.SIZE / Byte.SIZE]) {
                        ++errCnt;
                    }
                }
                byte[] data3;
                data3 = decode_systematic(data2, t, blockSize);
                if (data3.length != data1.length) {
                    System.out.println(Arrays.toString(data1));
                    System.out.println(Arrays.toString(data3));
                    throw new BchException("Incorrect len!");

                }
                boolean error = false;
                for (int j = 0; j < data3.length; ++j) {
                    if (data1[j] != data3[j]) {
                        System.out.println(Arrays.toString(data1));
                        System.out.println(Arrays.toString(data3));
                        System.out.println("Позиция " + j);
                        error = true;

                    }
                }
                if (error) throw new BchException("Incorrect value!");
            } catch (BchException e){
                fail(e.getMessage());
            }
        }
    }
@Test
    public void tEquals2ArrayLenEquals93IntegerRandom(){
        int dataLenMax = 4094;
        int blockSize = 255;
        Random rnd = new Random();
        for (int i = 0; i < 1000; ++i) {
            System.out.println(i);
            //  System.out.println(1 + (rnd.nextInt(50) % dataLenMax));
            ///byte[] data1 = new byte[1 + (rnd.nextInt(100) % dataLenMax)];
            byte[] data1 = new byte[93];
            System.out.println("Длина массива данных"+data1.length+" Двоичная сс "+Integer.toBinaryString(data1.length));
            for (int j = 0; j < data1.length; ++j) {
                data1[j] = (byte) (rnd.nextInt(100));
                //   data1[j] = (byte) (84);
                //  data1[j] = (byte) (33);
            }
            System.out.println("DATA1->\n"+Arrays.toString(data1));////////

            int t = (0 == blockSize)
                    ? (1 + rnd.nextInt(50) % ((int) Math.round(data1.length * 0.17)))
                    //: (1 + rnd.nextInt(50) % ((int) Math.round(blockSize * 0.17)));
                    // : (1 + rnd.nextInt(50));
                    : (2);
            System.out.println("Количество ошибок: " + t);
            try {
                byte[] data2 = encode_systematic(data1, t, blockSize);
                System.out.println("DATA2->\n" + Arrays.toString(data2));////////
                int errCnt = 0;

                for (int j = 0; j < data1.length; ++j) {
                    if (data1[j] != data2[j + Integer.SIZE / Byte.SIZE]) {
                        ++errCnt;
                    }
                }
                byte[] data3;
                data3 = decode_systematic(data2, t, blockSize);
                if (data3.length != data1.length) {
                    System.out.println(Arrays.toString(data1));
                    System.out.println(Arrays.toString(data3));
                    throw new BchException("Incorrect len!");

                }
                boolean error = false;
                for (int j = 0; j < data3.length; ++j) {
                    if (data1[j] != data3[j]) {
                        System.out.println(Arrays.toString(data1));
                        System.out.println(Arrays.toString(data3));
                        System.out.println("Позиция " + j);
                        error = true;

                    }
                }
                if (error) throw new BchException("Incorrect value!");
            } catch (BchException e){
                fail(e.getMessage());
            }
        }
    }

    @Test
    public void tEquals2ArrayLenEquals99IntegerRandom(){
        int dataLenMax = 4094;
        int blockSize = 255;
        Random rnd = new Random();
        for (int i = 0; i < 1000; ++i) {
            System.out.println(i);
            //  System.out.println(1 + (rnd.nextInt(50) % dataLenMax));
            ///byte[] data1 = new byte[1 + (rnd.nextInt(100) % dataLenMax)];
            byte[] data1 = new byte[99];
            System.out.println("Длина массива данных"+data1.length+" Двоичная сс "+Integer.toBinaryString(data1.length));
            for (int j = 0; j < data1.length; ++j) {
                data1[j] = (byte) (rnd.nextInt(100));
                //   data1[j] = (byte) (84);
                //  data1[j] = (byte) (33);
            }
            System.out.println("DATA1->\n"+Arrays.toString(data1));////////

            int t = (0 == blockSize)
                    ? (1 + rnd.nextInt(50) % ((int) Math.round(data1.length * 0.17)))
                    //: (1 + rnd.nextInt(50) % ((int) Math.round(blockSize * 0.17)));
                    // : (1 + rnd.nextInt(50));
                    : (2);
            System.out.println("Количество ошибок: " + t);
            try {
                byte[] data2 = encode_systematic(data1, t, blockSize);
                System.out.println("DATA2->\n" + Arrays.toString(data2));////////
                int errCnt = 0;

                for (int j = 0; j < data1.length; ++j) {
                    if (data1[j] != data2[j + Integer.SIZE / Byte.SIZE]) {
                        ++errCnt;
                    }
                }
                byte[] data3;
                data3 = decode_systematic(data2, t, blockSize);
                if (data3.length != data1.length) {
                    System.out.println(Arrays.toString(data1));
                    System.out.println(Arrays.toString(data3));
                    throw new BchException("Incorrect len!");

                }
                boolean error = false;
                for (int j = 0; j < data3.length; ++j) {
                    if (data1[j] != data3[j]) {
                        System.out.println(Arrays.toString(data1));
                        System.out.println(Arrays.toString(data3));
                        System.out.println("Позиция " + j);
                        error = true;

                    }
                }
                if (error) throw new BchException("Incorrect value!");
            } catch (BchException e){
                fail(e.getMessage());
            }
        }

    }
}
