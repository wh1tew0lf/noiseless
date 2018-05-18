package my.thesis.noiseless.adamar;

public class BitArray {

    final int BYTE_LOG = 3;

    private boolean[] _boolArray;

    public BitArray(int size) {
        if (size > 0) {
            setBoolArray(new boolean[size]);
        } else {
            throw new IllegalArgumentException("Size should be more than 0");
        }
    }

    public BitArray(byte[] bytes) {
        this(bytes.length * Byte.SIZE);
        for(int i = 0; i < getBoolArray().length; ++i) {
            setBoolValue(i, 0 != (bytes[i >> BYTE_LOG] >> (Byte.SIZE - (1 + i % Byte.SIZE))) % 2);
        }
    }

    public BitArray(int size, boolean value) {
        setBoolArray(new boolean[size]);
        for (int i = 0; i < getBoolArray().length; i++) {
            setBoolValue(i, value);
        }
    }

    public void CopyTo(byte[] bytes) {
        CopyTo(bytes, 0);
    }

    public void CopyTo(byte[] bytes, int index) {
        for (int i = 0; i < getBoolArray().length; i++) {
            if (0 == i % Byte.SIZE) {
                bytes[index + i >> BYTE_LOG] = (byte) (getBoolValue(i) ? 1 : 0);
            } else {
                bytes[index + i >> BYTE_LOG] = (byte) ((bytes[index + i >> BYTE_LOG] << 1) + (getBoolValue(i) ? 1 : 0));
            }
        }
    }

    public void WriteBits() {
        for (boolean b : getBoolArray())
            System.out.print(b ? 1 : 0);
        System.out.println();
    }

    public static void WriteBits(byte[] bits) {
        (new BitArray(bits)).WriteBits();
    }

    public void setBoolValue(int index, boolean value) {
        boolean[] boolArray = getBoolArray();
        if (null != boolArray && boolArray.length > index) {
            boolArray[index] = value;
        } else {
            throw new IndexOutOfBoundsException("Too big index: " + String.valueOf(index));
        }
    }

    public void swapBit(int index) {
        setBoolValue(index, !getBoolValue(index));
    }

    public boolean getBoolValue(int index) {
        boolean[] boolArray = getBoolArray();
        if (null != boolArray && boolArray.length > index) {
            return boolArray[index];
        } else {
            throw new IndexOutOfBoundsException("Too big index: " + String.valueOf(index));
        }
    }

    public void setBoolArray(boolean[] boolArray) {
        this._boolArray = boolArray;
    }

    public boolean[] getBoolArray() {
        return _boolArray;
    }

    public int length() {
        return _boolArray.length;
    }
}
