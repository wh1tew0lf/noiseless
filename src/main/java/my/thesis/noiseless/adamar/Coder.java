package my.thesis.noiseless.adamar;

public class Coder {

    public static byte[] encode(byte[] data, byte blockSize) {
        if (blockSize <= 1) {
            throw new IllegalArgumentException();
        }
        int size = (int) Math.pow(2, blockSize - 1);
        BitArray[] RM = new BitArray[size];
        for (int i = 0; i < RM.length; i++) {
            RM[i] = new BitArray(blockSize);
            RM[i].setBoolValue(0, true);
            for (int j = 1; j < blockSize; j++) {
                int p = (int) Math.pow(2, blockSize - j - 1);
                RM[i].setBoolValue(j, ((i & p) > 0));
            }
        }

        BitArray code = new BitArray(data);
        int blocksCount = (int) Math.ceil((double) (code.length() / blockSize));
        BitArray buf = new BitArray(blocksCount * RM.length, false);

        for (int i = 0; i < blocksCount; i++) {
            for (int j = 0; j < RM.length; j++) {
                for (int k = 0; k < blockSize; k++) {
                    boolean temp = buf.getBoolValue(i * RM.length + j);
                    temp ^= (RM[j].getBoolValue(k) &
                            (code.length() > i * blockSize + k) && code.getBoolValue(i * blockSize + k));
                    buf.setBoolValue(i * RM.length + j, temp);
                }
            }
        }

        byte[] result = new byte[(int) Math.ceil(buf.length() / 8.0)];
        buf.CopyTo(result, 0);

        return result;
    }

    public static byte[] decode(byte[] data, byte blockSize) {
        int size = (int) Math.pow(2, blockSize - 1);
        byte[][] A = new byte[size][size];
        A[0][0] = A[0][1] = A[1][0] = 1;
        A[1][1] = -1;

        for (int sz = 1; sz < size; sz *= 2) {
            for (int i = 0; i < sz; i++) {
                for (int j = 0; j < sz; j++) {
                    A[sz + i][j] = A[i][sz + j] = A[i][j];
                    A[sz + i][sz + j] = (byte) -A[i][j];
                }
            }
        }

        BitArray code = new BitArray(data);


        byte[] vect = new byte[code.length()];
        for (int i = 0; i < vect.length; i++)
            vect[i] = (byte) (code.getBoolValue(i) ? 1 : -1);

        int blocksCount = (int) Math.ceil((double) (code.length() / size));


        //Fix Errors
        for (int i = 0; i < blocksCount; i++) {
            int[] rvect = new int[size];
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++)
                    rvect[j] += A[j][k] * vect[i * size + k];
            }

            int maxInd = 0;
            for (int j = 1; j < rvect.length; j++) {
                if (Math.abs(rvect[j]) > Math.abs(rvect[maxInd]))
                    maxInd = j;
            }

            for (int k = 0; k < size; k++)
                code.getBoolArray()[i * size + k] = rvect[maxInd] * A[maxInd][k] > 0;
        }

        //Decode
        BitArray value = new BitArray(blockSize * blocksCount, false);

        for (int i = 0; i < blocksCount; i++) {
            value.setBoolValue((i * blockSize), code.getBoolValue((i * size)));
            for (int j = 1; j < blockSize; j++) {
                value.setBoolValue(((1 + i) * blockSize - j),
                        (code.getBoolValue((i * size + (int) Math.pow(2, j - 1))) ^ value.getBoolValue((i * blockSize))));
            }
        }


        byte[] result = new byte[(int) Math.ceil(value.length() / 8.0)];
        value.CopyTo(result, 0);
        return result;
    }

    public static void main(String[] args) {
        byte[] test = {1, 15, 127, 6, 45};
        System.out.println("Init...");
        BitArray.WriteBits(test);

        byte[] encoded = encode(test, (byte) 5);
        System.out.println("Encode...");
        BitArray.WriteBits(encoded);

        System.out.println("Noising...");
        int errorCount = 5;
        BitArray withErrors = new BitArray(encoded);
        for (int i = 0; i < errorCount; ++i) {
            int errorOffset = (int) Math.floor(Math.random() * withErrors.length());
            System.out.print(errorOffset);
            System.out.print(" ");
            withErrors.swapBit(errorOffset);
        }
        System.out.println();

        withErrors.CopyTo(encoded);
        BitArray.WriteBits(encoded);

        byte[] decoded = decode(encoded, (byte) 5);
        System.out.println("Decode...");
        BitArray.WriteBits(decoded);

        System.out.println("Compare");
        if (test.length == decoded.length) {
            boolean fail = false;
            for (int i = 0; i < test.length; i++) {
                if (test[i] != decoded[i]) {
                    System.err.println("Element #" + i + " incorrect [" + decoded[i] + "]");
                    fail = true;
                } else {
                    System.out.println("Element #" + i + " correct [" + decoded[i] + "]");
                }
            }

            if (!fail) {
                System.out.println();
                System.out.println("Decoded correctly");
            }
        } else {
            System.out.println("Length incorrect");
        }
    }
}
