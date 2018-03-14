package my.thesis.noiseless.bch3;

public enum BchErrorCode {
    WRONG_M_VALUE("M should be between %s"),
    WRONG_LENGTH("Length should be between %s and %s"),
    WRONG_PARAMETRES("Parameters invalid!"),
    BIG_LENGTH("Length too big! Max length == %s !"),
    DECODE_ERROR("Can not be decoded!"),
   WRONG_SIZE("Incorrect size %s");


    private String errorMesage;

    BchErrorCode(String errorMesage) {
        this.errorMesage = errorMesage;
    }

    public String getErrorMesage() {
        return errorMesage;
    }
}
