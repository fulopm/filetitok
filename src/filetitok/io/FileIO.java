package filetitok.io;

import filetitok.crypto.Cryptography;
import filetitok.gui.Window;
import filetitok.misc.Util;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;

public class FileIO {

    final Cryptography crypt;

    // az aszinkron mukodeshez szukseg van egy dinamikusan novekvo meretu adattarolora,
    // ebben az eppen aktualis bajtokat tarolja a program, tulajdonkeppen egy memoria
    final ByteArrayOutputStream BYTE_BUFFER = new ByteArrayOutputStream();

    // ez a kulcs-ertek tipusu objektum arra valo, hogy a Window class munkajat
    // segitse, amely fajl-, es konyvtarneveket tarol az objektumban, hogy ezek
    // bekereset meg lehessen valositani tobb lepesben, anelkul hogy az elozo
    // ertek elveszne. hasznalatanal figyelni kell arra, hogy minden titkositott
    // fajl utan ki kell uritnenunk ertelem szeruen.
    public static final HashMap<String, File> FILE_BUFFER = new HashMap<>();

    public FileIO() throws NoSuchAlgorithmException, NoSuchPaddingException {
        crypt = new Cryptography();
    }

    // ellenorzi a fajl elerhetoseget (letezik-e, tudjuk-e olvasni, es irhato-e,
    // ha writeAccess parameter igaz)
    public boolean isFileOk(File file, boolean writeAccess) {
        return (writeAccess ? file.exists() && file.canRead() && file.canWrite() : file.exists() && file.canRead());
    }

    public void encryptBufferedFile(char[] key, Window w) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // kiemeljuk a titkositando fajl mutatojat
        final File encSrcFile = FileIO.FILE_BUFFER.get("e_src_file");

        /* ellenorzesek, beolvasas es titkositas elvegzese */
        byte[] fileBytes;
        byte[] keyBytes;
        byte[] encryptedBytes;
        if (isFileOk(encSrcFile, false)) {
            fileBytes = Files.readAllBytes(encSrcFile.toPath());
            keyBytes = Util.convertCharsToBytes(key);
            encryptedBytes = crypt.encryptBytes(fileBytes, keyBytes);
            BYTE_BUFFER.write(encryptedBytes);
            /* ----- */
        } else {
            w.message(null, "Úgy tűnik, hogy  a kiválasztott fájl valami miatt nem elérhető."
                    + System.lineSeparator()
                    + "Ellenőrizze, hogy a programnak van-e jogosultsága azt olvasni, "
                    + System.lineSeparator()
                    + "majd válassza ki újra", JOptionPane.WARNING_MESSAGE);
        }

    }

    public void decryptBufferedFile(char[] key, Window w) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // kiemeljuk a visszafejtendo fajl mutatojat
        final File decSrcFile = FileIO.FILE_BUFFER.get("d_src_file");

        /* ellenorzesek, beolvasas es visszafejtes elvegzese */
        byte[] fileBytes;
        byte[] keyBytes;
        byte[] decryptedBytes;
        // fajl ellenorzese
        if (isFileOk(decSrcFile, false)) {
            // teljes fajl beolvasa
            fileBytes = Files.readAllBytes(decSrcFile.toPath());
            // kulcs karaktereinek bajtokka konvertalasa
            keyBytes = Util.convertCharsToBytes(key);
            // visszafejtes, es bufferhez adas
            decryptedBytes = crypt.decryptBytes(fileBytes, keyBytes);
            BYTE_BUFFER.write(decryptedBytes);
            /* ----- */
        } else {
            w.message(null, "Úgy tűnik, hogy  a kiválasztott fájl valami miatt nem elérhető."
                    + System.lineSeparator()
                    + "Ellenőrizze, hogy a programnak van-e jogosultsága azt olvasni, "
                    + System.lineSeparator()
                    + "majd válassza ki újra", JOptionPane.WARNING_MESSAGE);
        }

    }

    // ez a metodus vegzi el a titkositott bajtok kiirasat a byte bufferbol
    public void encDoFinal(Window w) throws IOException {
        // mutatok kiemelese
        final File encSrcFile = FileIO.FILE_BUFFER.get("e_src_file");
        final File encSaveDir = FileIO.FILE_BUFFER.get("e_dir");
        // fajlellenorzes
        if (!isFileOk(encSaveDir, true)) {
            w.message(null, "Úgy tűnik, hogy  a kiválasztott könyvtár valami miatt nem elérhető."
                    + System.lineSeparator()
                    + "Ellenőrizze, hogy a programnak van-e jogosultsága azt olvasni, "
                    + System.lineSeparator()
                    + "majd válassza ki újra", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // kiiras es takaritas
        Files.write(Paths.get(encSaveDir.toPath().toString(), encSrcFile.getName()), BYTE_BUFFER.toByteArray(), StandardOpenOption.CREATE);
        BYTE_BUFFER.reset();
        FILE_BUFFER.clear();
    }

    // visszafejtett bajtok kiirasa a fajlba
    public void decDoFinal(Window w) throws IOException {
        // mutatok kiemelese
        final File decSrcFile = FileIO.FILE_BUFFER.get("d_src_file");
        final File decSaveDir = FileIO.FILE_BUFFER.get("d_dir");
        // fajlellenorzes
        if (!isFileOk(decSaveDir, true)) {
            w.message(null, "Úgy tűnik, hogy  a kiválasztott könyvtár valami miatt nem elérhető."
                    + System.lineSeparator()
                    + "Ellenőrizze, hogy a programnak van-e jogosultsága azt olvasni, "
                    + System.lineSeparator()
                    + "majd válassza ki újra", JOptionPane.WARNING_MESSAGE);
            return;
        }
        //kiiras es takaritas
        Files.write(Paths.get(decSaveDir.toPath().toString(), decSrcFile.getName()), BYTE_BUFFER.toByteArray(), StandardOpenOption.CREATE);
        BYTE_BUFFER.reset();
        FILE_BUFFER.clear();
    }
    
    // ez a metodus azt ellenorzi, hogy ha file1-et elmentjuk, az nem fogja-e
    // felulirni file2-t
    public boolean willOveride(File file1, File file2) {
        return file1.toPath().equals(file2.getParentFile().toPath());
    }

}
