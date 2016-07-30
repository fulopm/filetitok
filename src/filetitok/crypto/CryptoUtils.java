package filetitok.crypto;

import java.security.SecureRandom;

/**
 *
 * @author fulopm
 */
public class CryptoUtils {

    private static final SecureRandom RND;

    static {

        RND = new SecureRandom();

    }

    /**
     * Generates a byte array with cryptographycally secure pseudorandom numbers
     * (based on {java.security.SecureRandom})
     *
     * @param size size of the generated random array
     * @return random bytes
     */
    public static byte[] randomBytes(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("randomBytes size<=0");
        }

        byte[] random = new byte[size];
        RND.nextBytes(random);
        return random;
    }

}
