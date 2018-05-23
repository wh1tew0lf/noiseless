package my.thesis;

import my.thesis.noiseless.bch.BCHCoder;
import my.thesis.noiseless.bch.BchException;

public class Main {

    public static void main(String[] args) {
        try {
            BCHCoder.main(args);
        } catch (BchException e) {
            System.out.println(e.getMessage());
        }

    }
}
