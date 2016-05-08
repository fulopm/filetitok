/*
 Készítette: Fülöp Márk 10.D <fulop.mark@outlook.com>
 Projektmunka programozás gyakorlat órára
 */
package filetitok.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author fulopm
 */
public class Cryptography {

    private final Cipher c;
    private final MessageDigest md5;
    private final String AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding";
    private final String MD5 = "MD5";
    private final int BLOCK_SIZE = 16;
    private final byte[] bytesIV = new byte[BLOCK_SIZE];

    public Cryptography() throws NoSuchAlgorithmException, NoSuchPaddingException {
        c = Cipher.getInstance(AES_CBC_PKCS5);
        md5 = MessageDigest.getInstance(MD5);
    }

    public void initIV() {
        new SecureRandom().nextBytes(bytesIV);
    }

    public void encryptBytes(InputStream in, OutputStream out, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
        out.write(bytesIV);
        out.flush();
        // cipher objektum inicializalasa mod es iv megadasaval
        c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(bytesIV));
        out = new CipherOutputStream(out, c);
        byte[] buf = new byte[1024];
        int numRead = 0;
        while ((numRead = in.read(buf)) >= 0) {
            out.write(buf, 0, numRead);
        }
        out.close();
    }

    public void decryptBytes(InputStream in, OutputStream out, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
        in.read(bytesIV);
        c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(bytesIV));
        in = new CipherInputStream(in, c);
        byte[] buf = new byte[1024];
        int numRead = 0;
        while ((numRead = in.read(buf)) >= 0) {
            out.write(buf, 0, numRead);
        }
        out.close();

    }

    // megadott bajt tombbol md5 hashet kepez
    public byte[] hash(byte[] key) {
        return md5.digest(key);
    }

    public byte[] getIV() {
        byte[] temp = new byte[BLOCK_SIZE];
        System.arraycopy(bytesIV, 0, temp, 0, BLOCK_SIZE);
        return temp;
    }
}
