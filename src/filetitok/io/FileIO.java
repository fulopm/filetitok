/*
 Készítette: Fülöp Márk <fulop.mark@outlook.com>

 */
package filetitok.io;

import filetitok.crypto.Cryptography;
import filetitok.Constants;
import filetitok.gui.Window;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;

public class FileIO {

    final Cryptography crypt;

    final ByteArrayOutputStream BYTE_BUFFER = new ByteArrayOutputStream();

    public static final Map<String, File> FILE_CACHE = new HashMap<>();

    public FileIO() throws NoSuchAlgorithmException, NoSuchPaddingException {
        crypt = new Cryptography();
    }

    public boolean isFileOk(File file, boolean writeAccess) {
        return (writeAccess ? file.exists() && file.canRead() && file.canWrite() : file.exists() && file.canRead());
    }

    public void encryptBufferedFile(byte[] pw, Window w) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        final File encSrcFile = FileIO.FILE_CACHE.get(Constants.E_SRC_FILE);

        byte[] keyBytes;
        if (isFileOk(encSrcFile, false)) {
            keyBytes = crypt.hash(pw);
            crypt.initIV();
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(encSrcFile))) {
                crypt.encryptStream(bis, BYTE_BUFFER, keyBytes);
            }

        } else {
            w.message(null, Constants.UI_MSG_DIR_NOT_AVAIL, JOptionPane.WARNING_MESSAGE);
        }

    }

    public void decryptBufferedFile(byte[] pw, Window w) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        final File decSrcFile = FileIO.FILE_CACHE.get(Constants.D_SRC_FILE);

        byte[] keyBytes;

        if (isFileOk(decSrcFile, false)) {
            keyBytes = crypt.hash(pw);
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(decSrcFile))) {
                crypt.decryptStream(bis, BYTE_BUFFER, keyBytes);
            }

        } else {
            w.message(null, Constants.UI_MSG_DIR_NOT_AVAIL, JOptionPane.WARNING_MESSAGE);
        }

    }

    public void encDoFinal(Window w) throws IOException {

        final File encSrcFile = FileIO.FILE_CACHE.get(Constants.E_SRC_FILE);
        final File encSaveDir = FileIO.FILE_CACHE.get(Constants.E_DIR);

        if (!isFileOk(encSaveDir, true)) {
            w.message(null, Constants.UI_MSG_DIR_NOT_AVAIL, JOptionPane.WARNING_MESSAGE);
            return;
        }

        Files.write(Paths.get(encSaveDir.toPath().toString(), encSrcFile.getName()), BYTE_BUFFER.toByteArray());
        clearCaches();
    }

    public void decDoFinal(Window w) throws IOException {

        final File decSrcFile = FileIO.FILE_CACHE.get(Constants.D_SRC_FILE);
        final File decSaveDir = FileIO.FILE_CACHE.get(Constants.D_DIR);

        if (!isFileOk(decSaveDir, true)) {
            w.message(null, Constants.UI_MSG_DIR_NOT_AVAIL, JOptionPane.WARNING_MESSAGE);
            return;
        }

        Files.write(Paths.get(decSaveDir.toPath().toString(), decSrcFile.getName()), BYTE_BUFFER.toByteArray());
        clearCaches();
    }

    public boolean willOveride(File file1, File file2) {
        return file1.toPath().equals(file2.getParentFile().toPath());
    }

    private void clearCaches() {
        BYTE_BUFFER.reset();
        FILE_CACHE.clear();
    }

}
