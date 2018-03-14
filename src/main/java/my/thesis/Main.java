package my.thesis;

import my.thesis.noiseless.bch3.BCHCoder;
import my.thesis.noiseless.bch3.BchException;

public class Main {

    public static void main(String[] args) {
        try {
            BCHCoder.main(args);
        } catch (BchException e) {
          System.out.println(e.getMessage());
        }

    }
}
