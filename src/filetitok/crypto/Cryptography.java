/*
 Kriptografia osztaly
 Készítette: Fülöp Márk <fulop.mark@outlook.com>
 */
package filetitok.crypto;

import java.security.*;
import java.util.Arrays;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.BCrypt;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;

public class Cryptography {

    static final String PROVIDER = "BC";

    static final String CRYPTO_ALGO = "AES";

    static final String CRYPTO_PARAM = "AES/CBC/PKCS5Padding";

    static final String HASH_ALGO = "SHA-256";

    static final int CRYPTO_BLOCK_SIZE = 0x10;
    static final int BCRYPT_COST = 0x10;
    static final int BCRYPT_SALT_SIZE = 0x10;

    private byte[] salt = new byte[BCRYPT_SALT_SIZE];

    private byte[] bytesIV = new byte[CRYPTO_BLOCK_SIZE];

    Cipher c = null;

    MessageDigest md = null;

    SecureRandom rnd = null;
    
    HMac hmac;
    
    Digest macdigest;

    public Cryptography() throws CryptoException {
        try {
            c = Cipher.getInstance(CRYPTO_PARAM, PROVIDER);
            md = MessageDigest.getInstance(HASH_ALGO, PROVIDER);
            macdigest = new SHA256Digest();
            rnd = new SecureRandom();
            hmac = new HMac(macdigest);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    public void initIV() {
        rnd.nextBytes(bytesIV);
    }

    /*
        uj iv generalasa, es bajtok titkositasa
     */
    public byte[] encrypt(byte[] data, byte[] key) throws CryptoException {
        initIV();
        try {
            c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, CRYPTO_ALGO), new IvParameterSpec(bytesIV));
            return c.doFinal(data);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new CryptoException(ex.getMessage(), ex);
        } finally {
            Arrays.fill(data, (byte) 0);
            Arrays.fill(key, (byte) 0);
        }
    }

    /*
    iv beallitasa es bajtok visszafejtese
     */
    public byte[] decrypt(byte[] data, byte[] key, byte[] bytesIV) throws CryptoException {
        setIV(bytesIV);
        try {
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, CRYPTO_ALGO), new IvParameterSpec(bytesIV));
            return c.doFinal(data);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new CryptoException(ex.getMessage(), ex);
        }
    }

    /*
     HASH_ALGO tipusu hash generalasa a megadott byte tombbol
     */
       public byte[] getMd(byte[] data) {
        return md.digest(data);
    }
       
       
    public byte[] generateHmac(byte[] key, byte[] data) {
           hmac.init(new KeyParameter(key));
           hmac.update(data,0,data.length);
           byte[] buf = new byte[macdigest.getDigestSize()];
           hmac.doFinal(buf, 0);
           return buf;
       }

    public byte[] getBytesIV() {
        byte[] temp = new byte[CRYPTO_BLOCK_SIZE];
        System.arraycopy(bytesIV, 0, temp, 0, CRYPTO_BLOCK_SIZE);
        return temp;
    }

    public void setIV(byte[] bytesIV) throws CryptoException {
        if (bytesIV.length != CRYPTO_BLOCK_SIZE) {
            throw new CryptoException("IV hossza nem " + CRYPTO_BLOCK_SIZE, null);
        }
        this.bytesIV = bytesIV;
    }

    public int getBlockSize() {
        return CRYPTO_BLOCK_SIZE;
    }

    public byte[] randomBytes(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("randomBytes size<=0");
        }

        byte[] random = new byte[size];
        rnd.nextBytes(random);
        return random;
    }

    /*
        kulcs eloallitasa a jelszobol
     */
    public byte[] deriveKey(byte[] passphrase, byte[] inputsalt) throws CryptoException {
        
        md.reset();
        if (inputsalt == null) {
            byte[] hash = getMd(randomBytes(BCRYPT_SALT_SIZE));
            System.arraycopy(hash, 0, salt, 0, BCRYPT_SALT_SIZE);
            try {
                return BCrypt.generate(passphrase, salt, BCRYPT_COST);
            } catch (Exception ex) {
                throw new CryptoException("bcrypt argument is invalid", ex);

            } finally {
                Arrays.fill(passphrase, (byte) 0);
            }
        } else {
            try {
                return BCrypt.generate(passphrase, inputsalt, BCRYPT_COST);
            } finally {
                Arrays.fill(passphrase, (byte) 0);
            }
        }
    }
    

    public byte[] getSalt() {
        byte[] temp = new byte[BCRYPT_SALT_SIZE];
        System.arraycopy(salt, 0, temp, 0, BCRYPT_SALT_SIZE);
        return temp;
    }

    public int getSaltSize() {
        return BCRYPT_SALT_SIZE;
    }

}
