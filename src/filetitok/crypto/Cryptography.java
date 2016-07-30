/*
 Kriptografia osztaly
 Készítette: Fülöp Márk <fulop.mark@outlook.com>
 */
package filetitok.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.util.Arrays;
import java.util.Properties;
import static filetitok.crypto.CryptoUtils.randomBytes;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Cryptography {

    private static byte[] iv;

    private static Cipher c;

    private static String cipherAlgo;
    private static String cipherMode;
    private static String cipherPadding;

    private static final Logger LOG = Logger.getLogger(Cryptography.class.getName());

    private static int cipherBlockSize;
    private static int cipherIVSize;
    private static int cipherKeyLength;

    static {
        loadProperties();
        try {

            c = Cipher.getInstance(
                    cipherAlgo + '/'
                    + cipherMode + '/'
                    + cipherPadding,
                    "BC");

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException e) {
            LOG.log(Level.SEVERE, "error initializing class - ExceptionInInitializerError thrown", e);
            throw new ExceptionInInitializerError(e.getMessage());
        }
    }

    public static void genIV() throws CryptoException {
        setIV(randomBytes(cipherIVSize));
    }

    /*
        uj iv generalasa, es bajtok titkositasa
     */
    public static final byte[] encrypt(byte[] data, byte[] key) throws CryptoException {
        genIV();
        try {
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, cipherAlgo), new IvParameterSpec(iv));
            return c.doFinal(data);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            LOG.log(Level.WARNING, "error during the encryption process - CryptoException thrown", ex);
            throw new CryptoException(ex.getMessage(), ex);
        } finally {
            Arrays.fill(data, (byte) 0);
            Arrays.fill(key, (byte) 0);
        }
    }

    /*
    iv beallitasa es bajtok visszafejtese
     */
    public static final byte[] decrypt(byte[] data, byte[] key, byte[] bytesIV) throws CryptoException {
        setIV(bytesIV);
        try {
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, cipherAlgo), new IvParameterSpec(iv));
            return c.doFinal(data);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            LOG.log(Level.WARNING, "error during the decryption process - CryptoException thrown", ex);
            throw new CryptoException(ex.getMessage(), ex);
        } finally {
            Arrays.fill(key, (byte) 0);
        }
    }

    public static byte[] getIV() {
        byte[] temp = new byte[cipherIVSize];
        System.arraycopy(iv, 0, temp, 0, cipherIVSize);
        return temp;
    }

    public static void setIV(byte[] bytesIV) throws CryptoException {
        if (bytesIV.length != cipherBlockSize) {
            throw new CryptoException("IV hossza nem " + cipherBlockSize, null);
        }
        iv = bytesIV;
    }

    public static int getCipherBlockSize() {
        return cipherBlockSize;
    }

    public static int getCipherIVSize() {
        return cipherIVSize;
    }

    public static int getCipherKeyLength() {
        return cipherKeyLength;
    }

    private static void loadProperties() {
        Properties props = new Properties();

        try (InputStream is = new FileInputStream(new File("res/crypto.properties"))) {
            props.load(is);

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "cannot read properties", ex);
        }

        cipherAlgo = props.getProperty("cipher-algo");
        cipherMode = props.getProperty("cipher-mode");
        cipherPadding = props.getProperty("cipher-padding");
        cipherBlockSize = Integer.parseInt(props.getProperty("cipher-blocksize")) / 8;
        cipherIVSize = Integer.parseInt(props.getProperty("cipher-ivsize")) / 8;
        cipherKeyLength = Integer.parseInt(props.getProperty("cipher-keysize")) / 8;
        props.clear();
    }

}
