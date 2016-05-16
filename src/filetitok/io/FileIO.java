/*
 Készítette: Fülöp Márk <fulop.mark@outlook.com>

 */
package filetitok.io;

import filetitok.crypto.Cryptography;
import filetitok.Constants;
import filetitok.crypto.CryptoException;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileIO {

    final Cryptography crypt;

    private static ByteArrayOutputStream BYTE_BUFFER = new ByteArrayOutputStream();

    public static Map<String, File> FILE_CACHE = new HashMap<>();

    
    private final int FILE_HEADER_SIZE;
    private final int CRYPTO_BLOCK_SIZE;
    private final int CRYPTO_SALT_SIZE;

    public FileIO() throws CryptoException {
        crypt = new Cryptography();
        CRYPTO_BLOCK_SIZE = crypt.getBlockSize();
        CRYPTO_SALT_SIZE = crypt.getSaltSize();
        FILE_HEADER_SIZE=CRYPTO_BLOCK_SIZE+CRYPTO_SALT_SIZE;
    }

    public boolean isFileOk(File file, boolean writeAccess) {
        return (writeAccess ? file.exists() && file.canRead() && file.canWrite() : file.exists() && file.canRead());
    }

    public void encryptBufferedFile(byte[] pw) throws CryptoException, IOException {
        byte[] fileBytes = readFileData(Constants.E_SRC_FILE, 0);
        byte[] keyBytes;
        byte[] encryptedBytes;

        keyBytes = crypt.deriveKey(pw, null);
        encryptedBytes = crypt.encrypt(fileBytes, keyBytes);
        BYTE_BUFFER.write(crypt.getSalt());
        BYTE_BUFFER.write(crypt.getBytesIV());
        BYTE_BUFFER.write(encryptedBytes);
        crypt.clear();

    }

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
            throw new FileNotFoundException();
        }

        return fileBytes;
    }

    public void decryptBufferedFile(byte[] pw) throws CryptoException, IOException {
        byte[] headerBytes = readFileHeader(Constants.D_SRC_FILE);

        byte[] saltBytes = Arrays.copyOfRange(headerBytes, 0, CRYPTO_SALT_SIZE);
        byte[] IVBytes = Arrays.copyOfRange(headerBytes, CRYPTO_SALT_SIZE, headerBytes.length);

        byte[] fileBytes = readFileData(Constants.D_SRC_FILE, CRYPTO_BLOCK_SIZE + CRYPTO_SALT_SIZE);
        byte[] keyBytes;
        byte[] decryptedBytes;

        keyBytes = crypt.deriveKey(pw, saltBytes);
        decryptedBytes = crypt.decrypt(fileBytes, keyBytes, IVBytes);
        BYTE_BUFFER.write(decryptedBytes);
        crypt.clear();

    }

    public void encDoFinal() throws IOException {

        final File encSrcFile = FILE_CACHE.get(Constants.E_SRC_FILE);
        final File encSaveDir = FILE_CACHE.get(Constants.E_DIR);

        if (!isFileOk(encSaveDir, true)) {
            throw new FileNotFoundException(encSaveDir.getName() + " nem olvasható");
        }

        Files.write(Paths.get(encSaveDir.toPath().toString(), encSrcFile.getName()), BYTE_BUFFER.toByteArray());
        clearCaches();
    }

    public void decDoFinal() throws IOException {

        final File decSrcFile = FILE_CACHE.get(Constants.D_SRC_FILE);
        final File decSaveDir = FILE_CACHE.get(Constants.D_DIR);

        if (!isFileOk(decSaveDir, true)) {
            throw new FileNotFoundException(decSaveDir.getName() + " nem olvasható");
        }

        Files.write(Paths.get(decSaveDir.toPath().toString(), decSrcFile.getName()), BYTE_BUFFER.toByteArray());
        clearCaches();
    }

    public boolean willOveride(File file1, File file2) {
        return file1.toPath().equals(file2.getParentFile().toPath());
    }

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

    private void clearCaches() {
        BYTE_BUFFER.reset();
        FILE_CACHE.clear();
    }

}
