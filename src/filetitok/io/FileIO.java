/*
 Fajlmuveletek es titkositas vezerleseert felelos osztaly
 Készítette: Fülöp Márk <fulop.mark@outlook.com>
 */
package filetitok.io;

import filetitok.crypto.Cryptography;
import filetitok.Constants;
import filetitok.crypto.CryptoException;
import filetitok.crypto.KeyDerivation;
import filetitok.crypto.MessageAuthentication;
import filetitok.misc.Util;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileIO {

    /**
     * ez egy olyan, dinamikusan novekvo, valtoztathato tarolo, amelyben a
     * titkosito vagy eppen visszafejto metodusok a kesz bajtokat irjak, majd a
     * finalizalo metodusok innen irjak ki a fajlba az elkeszult adatsort
     *
     * egyebkent akar arraylist is lehetne
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
    private int CRYPTO_BLOCK_SIZE;
    private int KDF_SALT_SIZE;
    private int MAC_SIZE;

    public FileIO() throws CryptoException {
        CRYPTO_BLOCK_SIZE = 0;
        KDF_SALT_SIZE = 0;
        try {
            CRYPTO_BLOCK_SIZE = Cryptography.getCipherBlockSize();
            KDF_SALT_SIZE = KeyDerivation.getKdfSaltSize();
            MAC_SIZE = MessageAuthentication.getMacSize();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        FILE_HEADER_SIZE = CRYPTO_BLOCK_SIZE + KDF_SALT_SIZE * 2 + MAC_SIZE;
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
        byte[] fileBytes,
                keyBytes,
                keySalt,
                encryptedBytes,
                mac,
                macKeySalt,
                iv;
        fileBytes = readFileData(Constants.E_SRC_FILE, 0);

        keyBytes = KeyDerivation.deriveKey(pw, null);
        keySalt = KeyDerivation.getSalt();

        encryptedBytes = Cryptography.encrypt(fileBytes, keyBytes);
        iv = Cryptography.getIV();

        byte[] macKey = KeyDerivation.deriveKey(pw, null);
        mac = MessageAuthentication.calcMac(encryptedBytes, macKey);
        macKeySalt = KeyDerivation.getSalt();
        Arrays.fill(pw, (byte) 0);
        Arrays.fill(fileBytes, (byte) 0);
        BYTE_BUFFER.write(keySalt);
        BYTE_BUFFER.write(iv);
        BYTE_BUFFER.write(macKeySalt);
        BYTE_BUFFER.write(mac);
        BYTE_BUFFER.write(encryptedBytes);

    }

    /*
        beolvassuk a cacheben levo D_SRC_FILE kulcsu fajlt egy bajt tombbe, visszafejtjuk,
        bufferbe irjuk
     */
    public void readAndDecryptCached(byte[] pw) throws CryptoException, IOException {
        // file elso n bajtjanak beolvasasa (jelen esetben 32 - salt + iv)
        byte[] headerBytes = readFileHeader(Constants.D_SRC_FILE);
        // az elobbi header byte tomb elso 16 bajtjat kiemeljuk a salt tombbe
        byte[] keySalt = Arrays.copyOfRange(headerBytes, 0, 16);
        byte[] iv = Arrays.copyOfRange(headerBytes, 16, 32);
        byte[] macKeySalt = Arrays.copyOfRange(headerBytes, 32, 48);
        byte[] mac = Arrays.copyOfRange(headerBytes, 48, 80);
        byte[] fileBytes, keyBytes, decryptedBytes;

        // beolvassuk a fajl tobbi reszet, az elso n bajt atugrasaval (most 32), mivel ezeket mar felhasznaltuk
        fileBytes = readFileData(Constants.D_SRC_FILE, FILE_HEADER_SIZE);
        byte[] macKey = KeyDerivation.deriveKey(pw, macKeySalt);
        if (!MessageAuthentication.calcAndValidateMac(
                mac,
                fileBytes,
                macKey)) {
            // TODO log
            Arrays.fill(pw, (byte) 0);

            throw new CryptoException("message authentication failed", null);

        } else {
            // kulcs eloallitasa a megadott jelszobol, es a beolvasott saltbol
            keyBytes = KeyDerivation.deriveKey(pw, keySalt);
            Arrays.fill(pw, (byte) 0);
            decryptedBytes = Cryptography.decrypt(fileBytes, keyBytes, iv);
            BYTE_BUFFER.write(decryptedBytes);
        }
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
        if (isFileOk(file, false)) {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                bis.skip(skip);
                fileBytes = new byte[bis.available()];
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
                Paths.get(decSaveDir.toPath().toString(),
                        Util.makeFilename(decSrcFile.getName())),
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
