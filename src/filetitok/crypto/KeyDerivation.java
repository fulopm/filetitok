package filetitok.crypto;

import static filetitok.crypto.CryptoUtils.randomBytes;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private static int KDF_COST;
    private static int KDF_SALT_SIZE;

    private static final Logger LOG = Logger.getLogger(KeyDerivation.class.getName());

    static {
        loadProperties();
        salt = new byte[KDF_SALT_SIZE];
    }

    public static byte[] createKey(byte[] passphrase, byte[] inputsalt) throws CryptoException {

        return inputsalt == null
                ? deriveKey(passphrase)
                : createKey(passphrase, inputsalt);
    }

    private static byte[] deriveKey(byte[] passphrase) throws CryptoException {
        newSalt();
        try {
            return BCrypt.generate(passphrase, salt, KDF_COST);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "error while the key derivation process - CryptoException thrown", ex);
            throw new CryptoException("bcrypt argument is invalid", ex);

        }
    }

    private static byte[] deriveKey(byte[] passphrase, byte[] existingSalt) throws CryptoException {
        salt = existingSalt;
        try {
            return BCrypt.generate(passphrase, salt, KDF_COST);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "error while the key derivation process - CryptoException thrown", ex);
            throw new CryptoException("bcrypt argument is invalid", ex);
        }
    }

    public static int getKdfSaltSize() {
        return KDF_SALT_SIZE;
    }

    public static byte[] getSalt() {
        byte[] temp = new byte[KDF_SALT_SIZE];
        System.arraycopy(salt, 0, temp, 0, KDF_SALT_SIZE);
        return temp;
    }

    private static void newSalt() {

        salt = Digest.getHash(randomBytes(KDF_SALT_SIZE));
    }

    private static void loadProperties() {
        Properties props = new Properties();

        try (InputStream is = new FileInputStream(new File("res/crypto.properties"))) {
            props.load(is);

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "cannot read properties", ex);
        }

        KDF_COST = Integer.parseInt(props.getProperty("keyderiv-cost"));
        KDF_SALT_SIZE = Integer.parseInt(props.getProperty("keyderiv-salt")) / 8;
        props.clear();
    }
}
