package my.thesis.noiseless.bch;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class BCHCoder {
    private int m, n, length, k, t, d;
    private int ninf;
    private int[] p;
    private int[] alpha_to, index_of, g;
    private static final boolean debug = false;
    public static final int MAX_M = 12;

    public BCHCoder(int m, int len, int t) throws BchException {
        this.m = m;
        this.length = len;
        this.t = t;

        generate_polynominal(); // Read m
        generate_gf(); //Construct the Galois Field GF(2**m)
        gen_poly(); //Compute the generator polynomial of BCH code
    }

    private void generate_polynominal() throws BchException {
        if ((m <= 1) || (m > MAX_M)) {
            throw new BchException(BchErrorCode.WRONG_M_VALUE, String.valueOf(MAX_M));
        }
        p = new int[m + 1];

        for (int i = 1; i < m; i++) {
            p[i] = 0;
        }

        p[0] = p[m] = 1;
        if (m == 2) p[1] = 1;
        else if (m == 3) p[1] = 1;
        else if (m == 4) p[1] = 1;
        else if (m == 5) p[2] = 1;
        else if (m == 6) p[1] = 1;
        else if (m == 7) p[1] = 1;
        else if (m == 8) p[4] = p[5] = p[6] = 1;
        else if (m == 9) p[4] = 1;
        else if (m == 10) p[3] = 1;
        else if (m == 11) p[2] = 1;
        else if (m == 12) p[3] = p[4] = p[7] = 1;
        else if (m == 13) p[1] = p[3] = p[4] = 1;
        else if (m == 14) p[1] = p[11] = p[12] = 1;
        else if (m == 15) p[1] = 1;
        else if (m == 16) p[2] = p[3] = p[5] = 1;
        else if (m == 17) p[3] = 1;
        else if (m == 18) p[7] = 1;
        else if (m == 19) p[1] = p[5] = p[6] = 1;
        else if (m == 20) p[3] = 1;

        n = (int) Math.pow(2, m) - 1;
        ninf = (n + 1) / 2 - 1;

        if ((length > n) || (length < ninf)) {
            throw new BchException(BchErrorCode.WRONG_LENGTH, String.valueOf(ninf), String.valueOf(n));
        }
    }

    private void generate_gf() {
        int mask;

        alpha_to = new int[n + 1];
        index_of = new int[n + 1];
        g = new int[n + 1];

        mask = 1;
        alpha_to[m] = 0;
        for (int i = 0; i < m; i++) {
            alpha_to[i] = mask;
            index_of[alpha_to[i]] = i;
            if (p[i] != 0) {
                alpha_to[m] ^= mask;
            }
            mask <<= 1;
        }
        index_of[alpha_to[m]] = m;
        mask >>= 1;
        for (int i = m + 1; i < n; i++) {
            if (alpha_to[i - 1] >= mask) {
                alpha_to[i] = alpha_to[m] ^ ((alpha_to[i - 1] ^ mask) << 1);
            } else {
                alpha_to[i] = alpha_to[i - 1] << 1;
            }
            index_of[alpha_to[i]] = i;
        }
        index_of[0] = -1;
    }

    private void gen_poly() throws BchException {
        int ii, jj, ll, kaux;
        int test, aux, nocycles, root, noterms, rdncy;
        int[][] cycle;
        int[] size, min, zeros;

        cycle = new int[10250][21];
        size = new int[10250];
        min = new int[10250];
        zeros = new int[10250];
        cycle[0][0] = 0;
        size[0] = 1;
        cycle[1][0] = 1;
        size[1] = 1;
        jj = 1; // cycle set index
        if (debug && (m > 9)) {
            System.out.println("Computing cycle sets modulo" + n);
            System.out.println("This may take some time...");
        }
        do {
            // Generate the jj-th cycle set
            ii = 0;
            do {
                ii++;
                cycle[jj][ii] = (cycle[jj][ii - 1] * 2) % n;
                size[jj]++;
                aux = (cycle[jj][ii] * 2) % n;
            } while (aux != cycle[jj][0]);
            // Next cycle set representative
            ll = 0;
            do {
                ll++;
                test = 0;
                for (ii = 1; ((ii <= jj) && (0 == test)); ii++) {
                    // Examine previous cycle sets
                    for (kaux = 0; ((kaux < size[ii]) && (0 == test)); kaux++) {
                        if (ll == cycle[ii][kaux]) {
                            test = 1;
                        }
                    }
                }
            } while ((0 != test) && (ll < (n - 1)));
            if ((0 == test)) {
                jj++; // next cycle set index
                cycle[jj][0] = ll;
                size[jj] = 1;
            }
        } while (ll < (n - 1));
        nocycles = jj; // number of cycle sets modulo n

        d = 2 * t + 1;

        // Search for roots 1, 2, ..., d-1 in cycle sets
        kaux = 0;
        rdncy = 0;
        for (ii = 1; ii <= nocycles; ii++) {
            min[kaux] = 0;
            test = 0;
            for (jj = 0; ((jj < size[ii]) && (0 == test)); jj++)
                for (root = 1; ((root < d) && (0 == test)); root++)
                    if (root == cycle[ii][jj]) {
                        test = 1;
                        min[kaux] = ii;
                    }
            if (0 != min[kaux]) {
                rdncy += size[min[kaux]];
                kaux++;
            }
        }
        noterms = kaux;
        kaux = 1;
        for (ii = 0; ii < noterms; ii++)
            for (jj = 0; jj < size[min[ii]]; jj++) {
                zeros[kaux] = cycle[min[ii]][jj];
                kaux++;
            }

        k = length - rdncy;

        if (k < 0) {
            throw new BchException(BchErrorCode.WRONG_PARAMETRES);
        }
        if (debug) {
            System.out.println("This is a (" + length + " " + k + " " + d + ") binary BCH code");
        }

        // Compute the generator polynomial
        g[0] = alpha_to[zeros[1]];
        g[1] = 1; // g(x) = (X + zeros[1]) initially
        for (ii = 2; ii <= rdncy; ii++) {
            g[ii] = 1;
            for (jj = ii - 1; jj > 0; jj--) {
                if (g[jj] != 0) {
                    g[jj] = g[jj - 1] ^ alpha_to[(index_of[g[jj]] + zeros[ii]) % n];
                } else {
                    g[jj] = g[jj - 1];
                }
            }
            g[0] = alpha_to[(index_of[g[0]] + zeros[ii]) % n];
        }
    }

    /// <summary>
    /// Compute redundacy bb[], the coefficients of b(x). The redundancy
    /// polynomial b(x) is the remainder after dividing x^(length-k)*data(x)
    /// by the generator polynomial g(x).
    /// </summary>
    /// <param name="data">Data for encode, will be encoded only first k ints</param>
    public int[] encode_bch(int[] data) {
        int i, j;
        int feedback;
        int[] bb = new int[length + 1];

        for (i = 0; i < length - k; i++) {
            bb[i] = 0;
        }
        for (i = k - 1; i >= 0; i--) {
            feedback = data[i] ^ bb[length - k - 1];
            if (feedback != 0) {
                for (j = length - k - 1; j > 0; j--) {
                    if (g[j] != 0) {
                        bb[j] = bb[j - 1] ^ feedback;
                    } else {
                        bb[j] = bb[j - 1];
                    }
                }
                bb[0] = (0 != g[0]) && (0 != feedback) ? 1 : 0;
            } else {
                for (j = length - k - 1; j > 0; j--) {
                    bb[j] = bb[j - 1];
                }
                bb[0] = 0;
            }
        }

        for (i = 0; i < k; i++) {
            bb[i + length - k] = data[i];
        }

        return bb;
    }

    /// <summary>
    /// Simon Rockliff's implementation of Berlekamp's algorithm.
    /// Assume we have received bits in recd[i], i=0..(n-1).
    ///
    /// Compute the 2*t syndromes by substituting alpha^i into rec(X) and
    /// evaluating, storing the syndromes in s[i], i=1..2t (leave s[0] zero) .
    /// Then we use the Berlekamp algorithm to find the error location polynomial
    /// elp[i].
    ///
    /// If the degree of the elp is >t, then we cannot correct all the errors, and
    /// we have detected an uncorrectable error pattern. We output the information
    /// bits uncorrected.
    ///
    /// If the degree of elp is <=t, we substitute alpha^i , i=1..n into the elp
    /// to get the roots, hence the inverse roots, the error location numbers.
    /// This step is usually called "Chien's search".
    ///
    /// If the number of errors located is not equal the degree of the elp, then
    /// the decoder assumes that there are more than t errors and cannot correct
    /// them, only detect them. We output the information bits uncorrected.
    /// </summary>
    /// <param name="recd">Received data that should be decoded</param>

    /**
     * @param recd
     * @return
     */
    int[] decode_bch(int[] recd) {
        int i, j, u, q, t2, count = 0, syn_error = 0;
        int[][] elp;
        int[] d, l, u_lu, s;
        int[] root, loc, reg;

        elp = new int[10260][1024];

        d = new int[10260];
        l = new int[10260];
        u_lu = new int[10260];
        s = new int[10250];

        root = new int[200];
        loc = new int[200];
        reg = new int[201];

        t2 = 2 * t;

        // first form the syndromes
        for (i = 1; i <= t2; i++) {
            s[i] = 0;
            for (j = 0; j < length; j++) {
                if (recd[j] != 0) {
                    s[i] ^= alpha_to[(i * j) % n];
                }
            }
            if (s[i] != 0) {
                syn_error = 1; // set error flag if non-zero syndrome
            }
            /*
             * Note:    If the code is used only for ERROR DETECTION, then
             *          exit program here indicating the presence of errors.
             */
            // convert syndrome from polynomial form to index form
            s[i] = index_of[s[i]];
            if (debug) {
                System.out.print(s[i]);
            }
        }
        if (debug) {
            System.out.println();
        }

        if (0 != syn_error) {
            // if there are errors, try to correct them
            /*
             * Compute the error location polynomial via the Berlekamp
             * iterative algorithm. Following the terminology of Lin and
             * Costello's book :   d[u] is the 'mu'th discrepancy, where
             * u='mu'+1 and 'mu' (the Greek letter!) is the step number
             * ranging from -1 to 2*t (see L&C),  l[u] is the degree of
             * the elp at that step, and u_l[u] is the difference between
             * the step number and the degree of the elp.
             */
            // initialise table entries
            d[0] = 0; // index form
            d[1] = s[1]; // index form
            elp[0][0] = 0; // index form
            elp[1][0] = 1; // polynomial form
            for (i = 1; i < t2; i++) {
                elp[0][i] = -1; // index form
                elp[1][i] = 0; // polynomial form
            }
            l[0] = 0;
            l[1] = 0;
            u_lu[0] = -1;
            u_lu[1] = 0;
            u = 0;

            do {
                u++;
                if (d[u] == -1) {
                    l[u + 1] = l[u];
                    for (i = 0; i <= l[u]; i++) {
                        elp[u + 1][i] = elp[u][i];
                        elp[u][i] = index_of[elp[u][i]];
                    }
                } else
                    /*
                     * search for words with greatest u_lu[q] for
                     * which d[q]!=0
                     */ {
                    q = u - 1;
                    while ((d[q] == -1) && (q > 0)) {
                        q--;
                    }
                    // have found first non-zero d[q]
                    if (q > 0) {
                        j = q;
                        do {
                            j--;
                            if ((d[j] != -1) && (u_lu[q] < u_lu[j])) {
                                q = j;
                            }
                        } while (j > 0);
                    }

                    /*
                     * have now found q such that d[u]!=0 and
                     * u_lu[q] is maximum
                     */
                    // store degree of new elp polynomial
                    if (l[u] > l[q] + u - q) {
                        l[u + 1] = l[u];
                    } else {
                        l[u + 1] = l[q] + u - q;
                    }

                    // form new elp(x)
                    for (i = 0; i < t2; i++) {
                        elp[u + 1][i] = 0;
                    }
                    for (i = 0; i <= l[q]; i++) {
                        if (elp[q][i] != -1) {
                            elp[u + 1][i + u - q] = alpha_to[(d[u] + n - d[q] + elp[q][i]) % n];
                        }
                    }
                    for (i = 0; i <= l[u]; i++) {
                        elp[u + 1][i] ^= elp[u][i];
                        elp[u][i] = index_of[elp[u][i]];
                    }
                }
                u_lu[u + 1] = u - l[u + 1];

                // form (u+1)th discrepancy
                if (u < t2) {
                    // no discrepancy computed on last iteration
                    if (s[u + 1] != -1) {
                        d[u + 1] = alpha_to[s[u + 1]];
                    } else {
                        d[u + 1] = 0;
                    }
                    for (i = 1; i <= l[u + 1]; i++) {
                        if ((s[u + 1 - i] != -1) && (elp[u + 1][i] != 0)) {
                            d[u + 1] ^= alpha_to[(s[u + 1 - i] + index_of[elp[u + 1][i]]) % n];
                        }
                    }
                    // put d[u+1] into index form
                    d[u + 1] = index_of[d[u + 1]];
                }
            } while ((u < t2) && (l[u + 1] <= t));

            u++;
            if (l[u] <= t) {
                // Can correct errors
                // put elp into index form
                for (i = 0; i <= l[u]; i++) {
                    elp[u][i] = index_of[elp[u][i]];
                }

                if (debug) {
                    System.out.print("sigma(x) = ");
                    for (i = 0; i <= l[u]; i++) {
                        System.out.println(elp[u][i]);
                    }
                    System.out.println();
                }

                if (debug) {
                    System.out.print("Roots: ");
                }

                // Chien search: find roots of the error location polynomial
                for (i = 1; i <= l[u]; i++) {
                    reg[i] = elp[u][i];
                }
                count = 0;
                ///////////todo BUG there
                for (i = 1; i <= n; i++) {
                    q = 1;
                    for (j = 1; j <= l[u]; j++)
                        if (reg[j] != -1) {
                            reg[j] = (reg[j] + j) % n;
                            q ^= alpha_to[reg[j]];
                        }
                    if (0 == q) {
                        // store root and error location number indices
                        root[count] = i;
                        loc[count] = n - i;
                        count++;
                        if (debug) {
                            System.out.print(n - i);
                        }
                    }
                }
                if (debug) {
                    System.out.print("");
                }
                if (count == l[u]) {
                    // no. roots = degree of elp hence <= t errors
                    for (i = 0; i < l[u]; i++) {
                        ///////////////////////
                        if (loc[i] == 131) {
                            // loc[1]=126;
                            //  loc[1]=135;
                            System.out.println();//////////////Debug here

                        }
                        /////////////////////////
                        recd[loc[i]] ^= 1;
                    }
                } else {
                    // elp has degree >t hence cannot solve
                    if (debug) {
                        System.out.println("Incomplete decoding: errors detected, но это не точно");
                    }
                    if (l[u] != t) return null; //detecded errors count that can't be resolved
                }
            }
        }
        int[] result = new int[k];

        for (i = 0; i < k; ++i) {
            if (i == 115) {
                int temp = i + length - k; //131
                if (debug) System.out.println();   ///Debug here
            }
            result[i] = recd[i + length - k];
        }

        return result;
    }
    /// <summary>
    /// Encode the specified data
    /// </summary>
    /// <param name="data">Encoded data</param>
    /// <param name="t">Count of errors that we can repair</param>
    /// <param name="length">Length of blocks. Короче если задавать, то можно будет меньшими блоками кодировать. Измеряется в битах</param>

    public static byte[] encode(byte[] data, int t, int length) throws BchException {
        boolean autoLen = (0 == length);
        // length = autoLen ? (8*(data.length + sizeof(int))) : length;
        length = autoLen ? (8 * (data.length) + Integer.SIZE) : length;

        int m = (int) Math.round(Math.log(length + 1) / Math.log(2));
        if ((m > MAX_M) && !autoLen) {
            throw new BchException(BchErrorCode.BIG_LENGTH,
                    String.valueOf((int) Math.pow(2, MAX_M) - 1));
        }
        m = (m > MAX_M) ? MAX_M : m;
        if (autoLen) {
            length = (int) Math.pow(2, m) - 1;
        }

        // byte[] dataWithLen = new byte[data.length + sizeof(int)];
        byte[] dataWithLen = new byte[data.length + Integer.SIZE / Byte.SIZE];
        int size = data.length;
        //for (int i = 0; i < sizeof(int); ++i)
        for (int i = 0; i < Integer.SIZE / Byte.SIZE; ++i) {
            dataWithLen[i] = (byte) (size % 256);
            size /= 256;
        }
        for (int i = 0; i < data.length; ++i) {
            //  dataWithLen[i + sizeof(int)] = data[i];
            dataWithLen[i + Integer.SIZE / Byte.SIZE] = data[i];
        }

        BCHCoder bch;
        do {
            bch = new BCHCoder(m, length, t);
            ++m;
            length = (int) Math.pow(2, m) - 1;
        } while ((m <= MAX_M) && (bch.k < (8 * dataWithLen.length)) && autoLen);

        int blocksCount = (int) Math.round((8 * dataWithLen.length) / (double) bch.k);

        int[] output = new int[(bch.length + 1) * blocksCount];
        for (int ind = 0; ind < blocksCount; ++ind) {
            int[] input = new int[bch.k];
            for (int i = 0; i < input.length; ++i) {
                int i2 = ind * bch.k + i;
                if (dataWithLen.length * 8 > i2) {
                    input[i] = (dataWithLen[i2 / 8] & (byte) Math.pow(2, i2 % 8)) > 0 ? 1 : 0;
                } else {
                    input[i] = 0;
                }
            }

            int[] tmpOutput = bch.encode_bch(input);
            for (int i = 0; i < tmpOutput.length; ++i) {
                output[ind * bch.length + i] = tmpOutput[i];
            }
        }

        byte[] encoded = new byte[(int) Math.round(output.length / 8.0)];

        for (int i = 0; i < output.length; ++i) {
            if (i % 8 == 0) {
                encoded[i / 8] = 0;
            }
            encoded[i / 8] += output[i] > 0 ? (byte) Math.pow(2, i % 8) : (byte) 0;
        }
        return encoded;
    }

    /// <summary>
    /// Decode the specified data
    /// </summary>
    /// <param name="data">Encoded data</param>
    /// <param name="t">Count of errors that we can repair</param>
    /// <param name="length">Length of blocks. Короче если задавать, то можно будет меньшими блоками кодировать. Измеряется в битах</param>
    public static byte[] decode(byte[] data, int t, int length) throws BchException {
        boolean autoLen = (0 == length);
        length = autoLen ? (8 * data.length) : length;

        int m = (int) Math.round(Math.log(length) / Math.log(2));
        if ((m > MAX_M) && !autoLen) {
            throw new BchException(BchErrorCode.BIG_LENGTH, String.valueOf((int) Math.pow(2, MAX_M) - 1));
        }
        m = (m > MAX_M) ? MAX_M : m;
        if (autoLen) {
            length = (int) Math.pow(2, m) - 1;
        }

        BCHCoder bch;
        do {
            bch = new BCHCoder(m, length, t);
            ++m;
            length = (int) Math.pow(2, m) - 1;
        } while ((m <= MAX_M) && ((bch.length + 1) < (8 * data.length)) && autoLen);

        int blocksCount = (int) Math.round((8 * data.length) / (double) (bch.length + 1));

        // int[] output = new int[bch.k*blocksCount + sizeof(int)*8];
        int[] output = new int[bch.k * blocksCount + Integer.SIZE / Byte.SIZE * 8];
        for (int ind = 0; ind < blocksCount; ++ind) {
            int[] input = new int[bch.length + 1];
            for (int i = 0; i < input.length; ++i) {
                int i2 = ind * (bch.length) + i;
                if (data.length * 8 > i2) {
                    input[i] = (data[i2 / 8] & (byte) Math.pow(2, i2 % 8)) > 0 ? 1 : 0;
                } else {
                    input[i] = 0;
                }
            }

            int[] tmpOutput = bch.decode_bch(input);
            if (null == tmpOutput) {
                throw new BchException(BchErrorCode.DECODE_ERROR);
            }
            for (int i = 0; i < tmpOutput.length; ++i) {
                output[ind * bch.k + i] = tmpOutput[i];
            }
        }

        //parse size
        int size = 0;
        // for (int i = sizeof(int); i > 0; --i)
        for (int i = Integer.SIZE / Byte.SIZE; i > 0; --i) {
            for (int j = 8; j > 0; --j) {
                size = size * 2 + output[(i - 1) * 8 + (j - 1)];
            }
        }
        if ((size <= 0) || (size > (int) Math.ceil(output.length / 8.0))) {
            throw new BchException(BchErrorCode.WRONG_SIZE, String.valueOf(size));
        }

        byte[] decoded = new byte[size];

        for (int i = 0; i < size * 8; ++i) {
            if (i % 8 == 0) {
                decoded[i / 8] = 0;
            }
            // int i2 = i + sizeof(int)*8;
            int i2 = i + Integer.SIZE / Byte.SIZE * 8;
            decoded[i / 8] += output[i2] > 0 ? (byte) Math.pow(2, i2 % 8) : (byte) 0;
        }

        return decoded;
    }

    /// <summary>
    /// Encode the specified data
    /// </summary>
    /// <param name="data">Encoded data</param>
    /// <param name="t">Count of errors that we can repair</param>
    /// <param name="length">Length of blocks. Короче если задавать, то можно будет меньшими блоками кодировать. Измеряется в битах</param>
    public static byte[] encode_systematic(byte[] data, int t, int length) throws BchException {
        boolean autoLen = (0 == length);
        // length = autoLen ? (8*(data.length + sizeof(int))) : length;
        length = autoLen ? (8 * (data.length) + Integer.SIZE) : length;

        int m = (int) Math.round(Math.log(length + 1) / Math.log(2));
        if ((m > MAX_M) && !autoLen) {
            throw new BchException(BchErrorCode.BIG_LENGTH,
                    String.valueOf((int) Math.pow(2, MAX_M) - 1));
        }
        m = (m > MAX_M) ? MAX_M : m;
        if (autoLen) {
            length = (int) Math.pow(2, m) - 1;
        }

        // byte[] dataWithLen = new byte[data.length + sizeof(int)];
        byte[] dataWithLen = new byte[data.length + Integer.SIZE / Byte.SIZE];
        int size = data.length;
        //for (int i = 0; i < sizeof(int); ++i)
        for (int i = 0; i < Integer.SIZE / Byte.SIZE; ++i) {
            dataWithLen[i] = (byte) (size % 256);
            size /= 256;
        }
        for (int i = 0; i < data.length; ++i) {
            // dataWithLen[i + sizeof(int)] = data[i];
            dataWithLen[i + Integer.SIZE / Byte.SIZE] = data[i];
        }

        BCHCoder bch;
        do {
            bch = new BCHCoder(m, length, t);
            ++m;
            length = (int) Math.pow(2, m) - 1;
        } while ((m <= MAX_M) && (bch.k < (8 * dataWithLen.length)) && autoLen);

        int blocksCount = (int) Math.ceil((8 * dataWithLen.length) / (double) bch.k);

        int[] output = new int[(bch.length + 1) * blocksCount];
        for (int ind = 0; ind < blocksCount; ++ind) {
            int[] input = new int[bch.k];
            for (int i = 0; i < input.length; ++i) {
                int i2 = ind * bch.k + i;
                if (dataWithLen.length * 8 > i2) {
                    input[i] = (dataWithLen[i2 / 8] & (byte) Math.pow(2, i2 % 8)) > 0 ? 1 : 0;
                } else {
                    input[i] = 0;
                }
            }

            int[] tmpOutput = bch.encode_bch(input);
            for (int i = 0; i < tmpOutput.length; ++i) {
                int redredundancy = bch.length - bch.k;
                int offset = (i < redredundancy)
                        ? (blocksCount * bch.k + ind * redredundancy + i)
                        : (ind * bch.k + i - redredundancy);
                output[offset] = tmpOutput[i];
            }
        }

        byte[] encoded = new byte[(int) Math.round(output.length / 8.0)];

        for (int i = 0; i < output.length; ++i) {
            if (i % 8 == 0) {
                encoded[i / 8] = 0;
            }
            encoded[i / 8] += output[i] > 0 ? (byte) Math.pow(2, i % 8) : (byte) 0;
        }
        return encoded;
    }

    /// <summary>
    /// Decode the specified data
    /// </summary>
    /// <param name="data">Encoded data</param>
    /// <param name="t">Count of errors that we can repair</param>
    /// <param name="length">Length of blocks. Короче если задавать, то можно будет меньшими блоками кодировать. Измеряется в битах</param>
    public static byte[] decode_systematic(byte[] data, int t, int length) throws BchException {
        boolean autoLen = (0 == length);
        length = autoLen ? (8 * data.length) : length;

        int m = (int) Math.round(Math.log(length) / Math.log(2));
        if ((m > MAX_M) && !autoLen) {
            throw new BchException(BchErrorCode.BIG_LENGTH,
                    String.valueOf((int) Math.pow(2, MAX_M) - 1));
        }
        m = (m > MAX_M) ? MAX_M : m;
        if (autoLen) {
            length = (int) Math.pow(2, m) - 1;
        }

        BCHCoder bch;
        do {
            bch = new BCHCoder(m, length, t);
            ++m;
            length = (int) Math.pow(2, m) - 1;
        } while ((m <= MAX_M) && ((bch.length + 1) < (8 * data.length)) && autoLen);

        int blocksCount = (int) Math.round((8 * data.length) / (double) (bch.length + 1));
        // int blocksCount = (int) Math.ceil((8 * data.length) / (double) (bch.length + 1));

        //  int[] output = new int[bch.k*blocksCount + sizeof(int)*8];
        int[] output = new int[bch.k * blocksCount + Integer.SIZE / Byte.SIZE * 8];
        for (int ind = 0; ind < blocksCount; ++ind) {
            int[] input = new int[bch.length + 1];
            for (int i = 0; i < input.length; ++i) {
                int redredundancy = bch.length - bch.k;
                int i2 = (i < redredundancy)
                        ? (blocksCount * bch.k + ind * redredundancy + i)
                        : (ind * bch.k + i - redredundancy);

                if (data.length * 8 > i2) {
                    input[i] = (data[i2 / 8] & (byte) Math.pow(2, i2 % 8)) > 0 ? 1 : 0;
                } else {
                    input[i] = 0;
                }
            }


            int[] tmpOutput = bch.decode_bch(input);
            if (null == tmpOutput) {
                throw new BchException(BchErrorCode.DECODE_ERROR);
            }
            for (int i = 0; i < tmpOutput.length; ++i) {
                if ((ind * bch.k + i) == 115) {
                    if (debug) System.out.println();    ///Debug here
                }
                output[ind * bch.k + i] = tmpOutput[i];
            }
        }

        //parse size
        int size = 0;
        //   for (int i = sizeof(int); i > 0; --i)
        for (int i = Integer.SIZE / Byte.SIZE; i > 0; --i) {
            for (int j = 8; j > 0; --j) {
                size = size * 2 + output[(i - 1) * 8 + (j - 1)];
            }
        }
        if ((size <= 0) || (size > (int) Math.ceil(output.length / 8.0))) {
            throw new BchException(BchErrorCode.WRONG_SIZE, String.valueOf(size));
        }

        byte[] decoded = new byte[size];

        for (int i = 0; i < size * 8; ++i) {
            if (i % 8 == 0) {
                decoded[i / 8] = 0;
            }
            // int i2 = i + sizeof(int)*8;
            int i2 = i + Integer.SIZE / Byte.SIZE * 8;
            Byte temp = (byte) Math.pow(2, i2 % 8);
            if (output[i2] > 0) {
                if (temp > 0) decoded[i / 8] += Math.pow(2, i2 % 8);
                else decoded[i / 8] += (byte) 0;
            }

        }

        return decoded;
    }

    private static void WriteBits(BitArray bits) {
        for (boolean b : bits.getBool()) {
            System.out.print(b ? 1 : 0);
        }
        System.out.println();
    }

    private static void WriteBits(byte[] bits) {
        WriteBits(new BitArray(bits));
    }

    public static void main(String[] args) throws BchException {
        int dataLenMax = 4094;
        int blockSize = 255;
        Random rnd = new Random();
        for (int i = 0; i < 1000; ++i) {
            if (debug) System.out.println(i);
            //  System.out.println(1 + (rnd.nextInt(50) % dataLenMax));
            //byte[] data1 = new byte[1 + (rnd.nextInt(100) % dataLenMax)];
            byte[] data1 = new byte[99];
            if (debug)
                System.out.println("Длина массива данных" + data1.length + " Двоичная сс " + Integer.toBinaryString(data1.length));
            for (int j = 0; j < data1.length; ++j) {
                data1[j] = (byte) (rnd.nextInt(100));
                //   data1[j] = (byte) (84);
                //  data1[j] = (byte) (33);
            }
            if (debug) System.out.println("DATA1->\n" + Arrays.toString(data1));////////

            int t = (0 == blockSize)
                    ? (1 + rnd.nextInt(50) % ((int) Math.round(data1.length * 0.17)))
                    //: (1 + rnd.nextInt(50) % ((int) Math.round(blockSize * 0.17)));
                    // : (1 + rnd.nextInt(50));
                    : (2);
            if (debug) System.out.println("Количество ошибок: " + t);
            byte[] data2 = encode_systematic(data1, t, blockSize);
            if (debug) System.out.println("DATA2->\n" + Arrays.toString(data2));////////
            int errCnt = 0;

            for (int j = 0; j < data1.length; ++j) {
                if (data1[j] != data2[j + Integer.SIZE / Byte.SIZE]) {
                    ++errCnt;
                }
            }
            if (errCnt > 0) {
                if (debug) System.out.print("Errors cnt " + errCnt + " " + data1.length + " !");
            }
            byte[] data3;
            try {
                data3 = decode_systematic(data2, t, blockSize);
            } catch (BchException e) {
                if (debug) {
                    WriteBits(data1);
                    for (int k = 0; k < data1.length; ++k) {
                        System.out.print(data1[k]);
                    }
                    System.out.println("");
                    WriteBits(data2);
                    for (int k = 0; k < data2.length; ++k) {
                        System.out.print(data2[k]);
                    }
                    System.out.println("");
                }
                throw e;
            }
            if (data3.length != data1.length) {
                throw new BchException("Incorrect len!");
            }
            boolean error = false;
            for (int j = 0; j < data3.length; ++j) {
                if (data1[j] != data3[j]) {
                    if (debug) System.out.println(Arrays.toString(data1));
                    if (debug) System.out.println(Arrays.toString(data3));
                    //  System.out.println("Длина массива данных"+data3.length);
                    if (debug) System.out.println("Позиция " + j);
                    error = true;

                }
            }
            if (error) throw new BchException("Incorrect value!");
        }
        if (debug) System.out.println("");
        if (debug) System.out.println("Tests passed!");
        //Scanner in = new Scanner(System.in);
        // in.next();
    }

}
