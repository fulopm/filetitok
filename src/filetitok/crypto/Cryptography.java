/*
 Készítette: Fülöp Márk <fulop.mark@outlook.com>
 */
package filetitok.crypto;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author fulopm
 */
public class Cryptography {

    private Cipher c = null;
    private MessageDigest md5 = null;
    private SecureRandom rnd = null;
    private final String CRYPTO_ALGO = "AES";
    private final String CRYPTO_PARAM = "AES/CBC/PKCS5Padding";
    private final String MD_ALGORITHM = "MD5";
    private final int BLOCK_SIZE = 0x10;
    private byte[] bytesIV = new byte[BLOCK_SIZE];

    public Cryptography() throws CryptoException {
        try {
            c = Cipher.getInstance(CRYPTO_PARAM);
            md5 = MessageDigest.getInstance(MD_ALGORITHM);
            rnd = new SecureRandom();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    public void initIV() {
        rnd.nextBytes(bytesIV);
    }

    public byte[] encrypt(byte[] data, byte[] key) throws CryptoException {
        initIV();
        try {
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, CRYPTO_ALGO), new IvParameterSpec(bytesIV));
            return c.doFinal(data);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new CryptoException(ex.getMessage(), ex);
        }
    }

    public byte[] decrypt(byte[] data, byte[] key, byte[] bytesIV) throws CryptoException {
        setIV(bytesIV);
        try {
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, CRYPTO_ALGO), new IvParameterSpec(bytesIV));
            return c.doFinal(data);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new CryptoException(ex.getMessage(), ex);
        }
    }

    public byte[] getMd(byte[] key) {
        return md5.digest(key);
    }

    public byte[] getBytesIV() {
        byte[] temp = new byte[BLOCK_SIZE];
        System.arraycopy(bytesIV, 0, temp, 0, BLOCK_SIZE);
        return temp;
    }

    public void setIV(byte[] bytesIV) throws CryptoException {
        if (bytesIV.length != this.BLOCK_SIZE) {
            throw new CryptoException("IV hossza nem " + this.BLOCK_SIZE, null);
        }
        this.bytesIV = bytesIV;
    }

    public int getBlockSize() {
        return BLOCK_SIZE;
    }
}
