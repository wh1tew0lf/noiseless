package my.thesis.noiseless.bch;

public class BitArray {
    private boolean[] bool;

    public BitArray(byte[] bits) {
        setBool(new boolean[bits.length]);
        for (int i = 0; i < bits.length; i++) {
            if (bits[i] == 1) {
                getBool()[i] = true;
            } else getBool()[i] = false;
        }
    }

    public boolean[] getBool() {
        return bool;
    }

    public void setBool(boolean[] bool) {
        this.bool = bool;
    }
}
