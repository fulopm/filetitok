package filetitok.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 *
 * @author fulopm
 */
public class MessageAuthentication {

    private static String macAlgo;
    private static String macHashAlgo;

    private static org.bouncycastle.crypto.Digest defaultDigest;
    private static org.bouncycastle.crypto.Mac defaultMac;
    private static final Logger LOG = Logger.getLogger(MessageAuthentication.class.getName());

    static {
        loadProperties();
        defaultDigest = filetitok.crypto.Digest.getInstance();
        if (macAlgo.equalsIgnoreCase("hmac")) {
            defaultMac = new HMac(defaultDigest);

        } else {
            LOG.log(Level.INFO, "{0} MAC algorithm, specified in the properties file did not found, using HMAC...", macAlgo);
            defaultMac = new HMac(defaultDigest);
        }
    }

    public static byte[] calcMac(byte[] bytes, byte[] key) {
        defaultMac.init(new KeyParameter(key));
        byte[] out = new byte[getMacSize()];
        defaultMac.update(bytes, 0, 0);
        defaultMac.doFinal(out, 0);
        defaultMac.reset();
        return out;
    }

    public static boolean validateMac(byte[] mac1, byte[] mac2) {
        if (mac1.length != mac2.length) {
            LOG.log(Level.INFO, "given MAC's aren't equal");
            return false;
        }
        boolean isValid = true;
        for (int i = 0; i < mac1.length; i++) {
            if (mac1[i] != mac2[i]) {
                isValid = false;
            }
        }
        return isValid;
    }

    public static boolean calcAndValidateMac(byte[] mac, byte[] data, byte[] key) {
        return validateMac(
                calcMac(data, key),
                mac);
    }

    public static int getMacSize() {
        if (defaultMac == null) {
            LOG.log(Level.SEVERE, "defaultMac object is not initialized");
            return -1;
        } else {
            return defaultMac.getMacSize();
        }
    }

    private static void loadProperties() {
        Properties props = new Properties();

        try (InputStream is = new FileInputStream(new File("res/crypto.properties"))) {
            props.load(is);

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "cannot read properties", ex);
        }

        macAlgo = props.getProperty("mac");
        macHashAlgo = props.getProperty("hash");
        props.clear();
    }
}
