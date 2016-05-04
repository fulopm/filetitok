/*
 Készítette: Fülöp Márk 10.D <fulop.mark@outlook.com>
 Projektmunka programozás gyakorlat órára
 */
package filetitok.crypto;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author fulopm
 */
public class Cryptography {

    /* inicializacios vektor forrastombje (ez igy nem biztonsagos) 
    
    bovebben: ha ket fajl elso blokkja, es a kulcs azonos, ugyan azt az eredményt
              fogjuk kapni a titkositott szovegben
    megoldas: SafeRandom objektummal mindig random ertekekkel kell feltolteni
              az IV forrastombjet
     */
    private static final byte[] bytesIV = {
        0x1F, 0x2A, 0x1B, 0x30,
        0x40, 0x3F, 0x2D, 0x44,
        0x14, 0x5C, 0x7E, 0x05,
        0x0A, 0x07, 0x10, 0x40
    };

    private final Cipher c;
    private final MessageDigest md5;
    private final String AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding";
     private final String MD5= "MD5";

    public Cryptography() throws NoSuchAlgorithmException, NoSuchPaddingException {
        c = Cipher.getInstance(AES_CBC_PKCS5);
        md5 = MessageDigest.getInstance(MD5);
    }

    public byte[] encryptBytes(byte[] data, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException {
        // lekerjuk a kulcs md5 hashjet (a kulonbozo meretu jelszavak miatt, 
        // mert ugye nekunk minden esetben 128 bites kulcsra van szuksegunk, es
        // az MD5 hash funkcio minden esetben 128 bites hash erteket terit
        // vissza), es a jelszobol megfelelo meretu kulcsot faragunk
        // jelszo != kulcs !!!
        key = hash(key);
        // iv betoltese a byte tombbol
        IvParameterSpec IV = new IvParameterSpec(bytesIV);
        // SecretKey objektum betoltese a megadott kulcs bajtokbol
        SecretKey k = new SecretKeySpec(key, "AES");
        // cipher objektum inicializalasa mod es iv megadasaval
        c.init(Cipher.ENCRYPT_MODE, k, IV);
        // titkositas elvegzese, es titkositott bajtok visszaadasa
        return c.doFinal(data);

    }

    public byte[] decryptBytes(byte[] data, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // lekerjuk a kulcs md5 hashjet (a kulonbozo meretu jelszavak miatt, 
        // mert ugye nekunk minden esetben 128 bites kulcsra van szuksegunk, es
        // az MD5 hash funkcio minden esetben 128 bites hash erteket terit
        // vissza), es a jelszobol megfelelo meretu kulcsot faragunk
        // jelszo != kulcs !!!
        key = hash(key);
        // iv betoltese a byte tombbol
        IvParameterSpec IV = new IvParameterSpec(bytesIV);
        // SecretKey objektum betoltese a megadott kulcs bajtokbol
        SecretKey k = new SecretKeySpec(key, "AES");
        // cipher objektum inicializalasa mod es iv megadasaval
        c.init(Cipher.DECRYPT_MODE, k, IV);
        // visszafejtes elvegzese, es bajtok visszaadasa
        return c.doFinal(data);

    }
    
    // megadott bajt tombbol md5 hashet kepez
    public byte[] hash(byte[] key) {
        return md5.digest(key);
    }
}
