package filetitok.crypto;

import static filetitok.crypto.CryptoUtils.randomBytes;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.crypto.generators.BCrypt;

/**
 *
 * @author fulopm
 */
public class KeyDerivation {

    private static byte[] salt;

    private static int kdfCost;
    private static int kdfSaltSize;

    private static final Logger LOG = Logger.getLogger(KeyDerivation.class.getName());

    public static int getKdfSaltSize() {
        return kdfSaltSize;
    }

    static {
        loadProperties();
        salt = new byte[kdfSaltSize];
    }

    public static byte[] deriveKey(byte[] passphrase, byte[] inputsalt) throws CryptoException {
        if (inputsalt == null) {
            byte[] hash = Digest.getHash(randomBytes(kdfSaltSize));
            System.arraycopy(hash, 0, salt, 0, kdfSaltSize);
            try {
                return BCrypt.generate(passphrase, salt, kdfCost);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "error while the key derivation process - CryptoException thrown", ex);
                throw new CryptoException("bcrypt argument is invalid", ex);

            }
        } else {
            try {
                return BCrypt.generate(passphrase, inputsalt, kdfCost);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "error while the key derivation process - CryptoException thrown", ex);
                throw new CryptoException("bcrypt argument is invalid", ex);
            }
        }
    }

    public static byte[] getSalt() {
        byte[] temp = new byte[kdfSaltSize];
        System.arraycopy(salt, 0, temp, 0, kdfSaltSize);
        return temp;
    }

    private static void loadProperties() {
        Properties props = new Properties();

        try (InputStream is = new FileInputStream(new File("res/crypto.properties"))) {
            props.load(is);

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "cannot read properties", ex);
        }

        kdfCost = Integer.parseInt(props.getProperty("keyderiv-cost"));
        kdfSaltSize = Integer.parseInt(props.getProperty("keyderiv-salt")) / 8;
        props.clear();
    }
}
