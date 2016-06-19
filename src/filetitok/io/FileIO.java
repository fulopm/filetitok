/*
 Fajlmuveletek es titkositas vezerleseert felelos osztaly
 Készítette: Fülöp Márk <fulop.mark@outlook.com>
 */
package filetitok.io;

import filetitok.crypto.Cryptography;
import filetitok.Constants;
import filetitok.crypto.CryptoException;
import filetitok.misc.Util;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileIO {

    final Cryptography crypt;

    /*
    ez egy olyan, dinamikusan novekvo, valtoztathato tarolo, amelyben a titkosito
    vagy eppen visszafejto metodusok a kesz bajtokat irjak, majd a finalizalo metodusok
    innen irjak ki a fajlba az elkeszult adatsort
    
    egyebkent akar arraylist is lehetne
     */
    private static ByteArrayOutputStream BYTE_BUFFER = new ByteArrayOutputStream();

    /*
    kulcs-ertek tipusu adattarolo, ebben tarolunk minden fajlt amelyre szukseg
    van a program futasa kozben
    mindegyikre egy String kulccsal tudunk hivatkozni get() metodus segitsegevel
    lasd Java api dokumentacio
     */
    public static Map<String, File> FILE_CACHE = new HashMap<>();

    // crypto osztalytol kapott adatok, amelyek a kulonbozo tombok es bufferek allokalasahoz szuksegesek
    // lasd reszletesen a Cryptography osztalyt
    private final int FILE_HEADER_SIZE;
    private final int CRYPTO_BLOCK_SIZE;
    private final int CRYPTO_SALT_SIZE;

    public FileIO() throws CryptoException {
        crypt = new Cryptography();
        CRYPTO_BLOCK_SIZE = crypt.getBlockSize();
        CRYPTO_SALT_SIZE = crypt.getSaltSize();
        // salt, mac salt, iv, mac
        FILE_HEADER_SIZE = CRYPTO_BLOCK_SIZE + CRYPTO_SALT_SIZE + CRYPTO_SALT_SIZE + CRYPTO_SALT_SIZE*2;
    }

    /*
    ez a metodus ellenorzi, hogy a fajl letezik es olvashato-e, illetve ha a paramter
    true, akkor azt is hogy irhato-e
     */
    public boolean isFileOk(File file, boolean writeAccess) {
        return (writeAccess ? file.exists() && file.canRead() && file.canWrite() : file.exists() && file.canRead());
    }

    /*
        beolvassuk a cacheben levo E_SRC_FILE kulcsu fajlt egy bajt tombbe, titkositjuk,
        bufferbe irjuk a salttal, IV-vel egyutt
     */
    public void readAndEncryptCached(byte[] pw) throws CryptoException, IOException {
        byte[] fileBytes;
        byte[] keyBytes;
        byte[] encryptedBytes;
        byte[] mac;

        // beolvassuk az egesz fajlt
        fileBytes = readFileData(Constants.E_SRC_FILE, 0);
        // elkeszitjuk a kulcsot a jelszobol (JELSZO !!= KULCS)
        keyBytes = crypt.deriveKey(pw, null);
        // titkositjuk a fajl bajtjait
        encryptedBytes = crypt.encrypt(fileBytes, keyBytes);
        // takaritas
        Arrays.fill(pw, (byte) 0);
        Arrays.fill(fileBytes, (byte) 0);
        BYTE_BUFFER.write(crypt.getSalt()); // TITKOSITAS SALT (16)
        mac = crypt.generateHmac(crypt.deriveKey(pw, null), encryptedBytes);
        BYTE_BUFFER.write(crypt.getSalt()); // MAC SALT (16)
        BYTE_BUFFER.write(crypt.getBytesIV()); // IV (16)
        BYTE_BUFFER.write(mac); // MAC (32)
        BYTE_BUFFER.write(encryptedBytes); // data (n)

    }

    /*
        beolvassuk a cacheben levo D_SRC_FILE kulcsu fajlt egy bajt tombbe, visszafejtjuk,
        bufferbe irjuk
     */
    public void readAndDecryptCached(byte[] pw) throws CryptoException, IOException {
        // file elso n bajtjanak beolvasasa (jelen esetben 32 - salt + iv)
        byte[] headerBytes = readFileHeader(Constants.D_SRC_FILE);
        // az elobbi header byte tomb elso 16 bajtjat kiemeljuk a salt tombbe
        byte[] cipherSaltBytes = Arrays.copyOfRange(headerBytes, 0, CRYPTO_SALT_SIZE);
        byte[] macSaltBytes = Arrays.copyOfRange(headerBytes, CRYPTO_SALT_SIZE, CRYPTO_SALT_SIZE*2);
        byte[] IVBytes = Arrays.copyOfRange(headerBytes, CRYPTO_SALT_SIZE*2, CRYPTO_SALT_SIZE*3);
        byte[] macBytes = Arrays.copyOfRange(headerBytes, CRYPTO_SALT_SIZE*3, CRYPTO_SALT_SIZE*5);

        byte[] fileBytes;
        byte[] keyBytes;
        byte[] macKeyBytes;
        byte[] decryptedBytes;
        byte[] calculatedMac;

        // beolvassuk a fajl tobbi reszet, az elso n bajt atugrasaval (most 32), mivel ezeket mar felhasznaltuk
        fileBytes = readFileData(Constants.D_SRC_FILE, FILE_HEADER_SIZE);
        // kulcs eloallitasa a megadott jelszobol, es a beolvasott saltbol
        keyBytes = crypt.deriveKey(pw, cipherSaltBytes);
        macKeyBytes = crypt.deriveKey(pw, macSaltBytes);
        calculatedMac = crypt.generateHmac(macKeyBytes, fileBytes);
        if (!Arrays.equals(macBytes, calculatedMac)) {
            throw new CryptoException("authentication failed", null);
        }
        // visszafejtes es bufferbe iras
        decryptedBytes = crypt.decrypt(fileBytes, keyBytes, IVBytes);
        BYTE_BUFFER.write(decryptedBytes);

    }

    /*
    ez a metodus a kulcskent megadott string fajlbol beolvassa a bajtokat,
    az elso skip bajtot atugorva
    tehat pl ha skip := 0, akkor az egesz fajlt
    last Java api dokumentacio streamek
     */
    public byte[] readFileData(String fileKey, int skip) throws IOException {
        if (!FILE_CACHE.containsKey(fileKey)) {
            throw new FileNotFoundException("megadott kulccsal nem letezik fajl a cacheben");
        }

        File file = FILE_CACHE.get(fileKey);
        byte[] fileBytes = null;
        int len = 0;
        if (isFileOk(file, false)) {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                bis.skip(skip);
                len = bis.available();
                fileBytes = new byte[len];
                bis.read(fileBytes);
            }
        } else {
            throw new IOException("fajlt nem hozzaferheto");
        }

        return fileBytes;
    }

    /*
        titkositas utan a titkositott bajtokat fajlba irja, es ha a paramter true,
        torli az eredeti fajlt
    
     */
    public void encDoFinal(boolean deleteOriginal) throws IOException {

        final File encSrcFile = FILE_CACHE.get(Constants.E_SRC_FILE);
        final File encSaveDir = FILE_CACHE.get(Constants.E_DIR);

        if (!isFileOk(encSaveDir, true)) {
            throw new FileNotFoundException(encSaveDir.getName() + " nem olvasható");
        }

        // egyszeru megoldas bajtok fajlba irasara
        Files.write(
                // erre az eleresi utvonalra mentjuk a titkositott fajlt
                Paths.get(encSaveDir.toPath().toString(), Util.makeFilename(encSrcFile.getName())),
                BYTE_BUFFER.toByteArray());
        if (deleteOriginal) {
            encSrcFile.delete();
        }
        clearCaches();
    }

    /*
    visszafejtes utan bajtok fajlba irasa
     */
    public void decDoFinal() throws IOException {

        final File decSrcFile = FILE_CACHE.get(Constants.D_SRC_FILE);
        final File decSaveDir = FILE_CACHE.get(Constants.D_DIR);

        if (!isFileOk(decSaveDir, true)) {
            throw new FileNotFoundException(decSaveDir.getName() + " nem olvasható");
        }
        Files.write(
                Paths.get(decSaveDir.toPath().toString(), Util.makeFilename(decSrcFile.getName())),
                BYTE_BUFFER.toByteArray());
        clearCaches();
    }

    /*
        beolvassa a fajl elso n bajtjat (crypto osztalytol fugg a szama, mostani esetben pl. 32)
     */
    public byte[] readFileHeader(String fileKey) throws IOException {
        if (!FILE_CACHE.containsKey(fileKey)) {
            throw new FileNotFoundException("megadott kulccsal nem letezik fajl a cacheben");
        }
        File file = FILE_CACHE.get(fileKey);
        byte[] bytes = new byte[FILE_HEADER_SIZE];
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            bis.read(bytes);
        } catch (Exception e) {
            throw new FileNotFoundException("olvasas kozbeni hiba");
        }

        return bytes;
    }

    /*
    cache szimpla kiuritese, buffer teljes kinullazasa
     */
    private void clearCaches() {
        byte[] cleanBytes = new byte[BYTE_BUFFER.size()];
        Arrays.fill(cleanBytes, (byte) 0);
        BYTE_BUFFER.write(cleanBytes, 0, BYTE_BUFFER.size());
        BYTE_BUFFER.reset();
        FILE_CACHE.clear();
    }

}
