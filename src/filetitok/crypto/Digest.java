package filetitok.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;

/**
 * This class is for general message digest functions.
 *
 * @author fulopm
 */
public class Digest {

    /**
     * The global {org.bouncycastle.crypto.Digest} object
     */
    private static final org.bouncycastle.crypto.Digest d;
    /**
     * Default hash algorithm
     */
    private static String defaultAlgorithm;

    
    /**
     * Class logger
     */
    private static final Logger LOG = Logger.getLogger(Digest.class.getName());

    /**
     * Static initializer
     *
     */
    static {
        loadProperties();

        if (defaultAlgorithm.equalsIgnoreCase("sha512")) {
            d = new SHA512Digest();
        } else if (defaultAlgorithm.equalsIgnoreCase("blake2")) {
            d = new Blake2bDigest();
        } else {
            d = new SHA256Digest();
        }
    }

    /**
     * Calculates the parameter's digest with the Digest object
     * @param bytes the byte array to be hashed
     * @return the calculated hash
     */
    public static byte[] getHash(byte[] bytes) {
        byte[] out = new byte[d.getDigestSize()];
        d.update(bytes, 0, 0);
        d.doFinal(out, 0);
        d.reset();
        return out;
    }

    /**
     * Returns the native hash object
     * @return hasher
     */
    static org.bouncycastle.crypto.Digest getInstance() {
        return d;
    }

    /**
     * loads the properties from the global configuration file
     */
    private static void loadProperties() {
        Properties props = new Properties();

        try (InputStream is = new FileInputStream(new File("res/crypto.properties"))) {
            props.load(is);

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "cannot read properties", ex);
        }

        defaultAlgorithm = props.getProperty("hash");
        props.clear();

    }
}
