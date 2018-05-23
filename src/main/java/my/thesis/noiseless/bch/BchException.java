package my.thesis.noiseless.bch;

public class BchException extends Exception {
    public BchException(BchErrorCode code, String param) {
        super(String.format(code.getErrorMesage(), param));
    }

    public BchException(BchErrorCode code, String param, String param2) {
        super(String.format(code.getErrorMesage(), param, param2));
    }

    public BchException(BchErrorCode code) {
        super(code.getErrorMesage());
    }

    public BchException(String message) {
        super(message);
    }
}
