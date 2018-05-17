import java.util.BitSet;

public class BitArray {

    private boolean[] boolArray;

    public BitArray(int size) {
        setBoolArray(new boolean[size]);
    }


    public BitArray(byte[] bits) {
        setBoolArray(new boolean[bits.length * 8]);
        int index = 0;
        for (byte bit : bits) {
            String byteToBits = Integer.toBinaryString(bit);
            int ZeroBit = 8 - byteToBits.length();
            while (ZeroBit != 0) {
                setBoolValue(index, false);
                index++;
                ZeroBit--;
            }
            for (char c : byteToBits.toCharArray()) {
                if (c == '1')
                    setBoolValue(index, true);
                else
                    setBoolValue(index, false);
                index++;
            }
        }
    }

    public BitArray(int size, boolean value) {
        setBoolArray(new boolean[size]);
        for (boolean b : getBoolArray())
            b = value;
    }


    public void CopyTo(byte[] bytes, int index) {
        System.out.println(getBoolArray().length);
        StringBuilder sb = new StringBuilder();
        int k = 0;
        for (int i = 0; i < getBoolArray().length; i++) {
            if (sb.length() != 8) {
                if (getBoolArray()[i])
                    sb.append(1);
                else sb.append(0);
            } else {
                bytes[index + k] = (byte) Integer.parseInt(sb.toString(), 2);
                k++;
                sb.delete(0,9);
            }
        }
    }

    public void setBoolValue(int index, boolean value) {
        getBoolArray()[index] = value;
    }

    public boolean getBoolValue(int index) {
        return getBoolArray()[index];
    }


    public void setBoolArray(boolean[] boolArray) {
        this.boolArray = boolArray;
    }

    public boolean[] getBoolArray() {
        return boolArray;
    }

    public int length() {
        return getBoolArray().length;
    }
}
