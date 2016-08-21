/*
 Grafikus feluletet kezelo osztaly
 Készítette: Fülöp Márk <fulop.mark@outlook.com>
 */
package filetitok.gui;

import filetitok.Constants;
import filetitok.crypto.CryptoException;
import filetitok.io.FileIO;
import filetitok.misc.Util;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.crypto.BadPaddingException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import static filetitok.io.FileIO.FILE_CACHE;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import org.bouncycastle.util.Arrays;

public class Window implements ActionListener {

    // alapveto feluleti elemek deklaralasa
    private JFrame frame;
    private JPanel contentPane;

    private JPanel encryptionPanel;
    private JPanel decryptionPanel;

    private JLabel eSrcFileLbl;
    private JButton eSrcFileBtn;

    private JLabel eDestDirLbl;
    private JButton eDestDirBtn;

    private JLabel ePassLbl;
    private JPasswordField ePassInp;

    private JButton eOkBtn;

    private JLabel dSrcFileLbl;
    private JButton dSrcFileBtn;

    private JLabel dDestDirLbl;
    private JButton dDestDirBtn;

    private JLabel dPassLbl;
    private JPasswordField dPassInp;

    private JButton dOkBtn;

    private final JLabel placeholder = new JLabel("");
    private final JLabel placeholder2 = new JLabel("");

    private final Font standardFont = new Font(Constants.UI_FONT_NAME, Font.PLAIN, 11);
    private final Font footerFont = new Font(Constants.UI_FONT_NAME, Font.PLAIN, 9);

    // visszafejtesnel rossz probalkozasok szama
    private int tries = 0;

    public void createAndShowGUI() {

        // main contentpanel inicializalsa
        contentPane = new JPanel();
        contentPane.setOpaque(!true);

        contentPane.setLayout(new FlowLayout(FlowLayout.LEADING));

        contentPane.setBorder(BorderFactory.createTitledBorder(new TitledBorder(""), Constants.AUTHOR, TitledBorder.TRAILING, TitledBorder.BOTTOM, footerFont, Color.black));

        eSrcFileLbl = new JLabel(Constants.UI_TO_ENC);
        eSrcFileLbl.setFont(standardFont);

        eSrcFileBtn = new JButton(Constants.UI_SELECT);
        eSrcFileBtn.addActionListener(this);

        eDestDirLbl = new JLabel(Constants.UI_SAVE_DIR);
        eDestDirLbl.setFont(standardFont);

        eDestDirBtn = new JButton(Constants.UI_SELECT);

        eDestDirBtn.addActionListener(this);

        eOkBtn = new JButton(Constants.UI_ENCRYPTION);
        eOkBtn.addActionListener(this);

        ePassLbl = new JLabel(Constants.UI_PW);
        ePassLbl.setFont(standardFont);

        ePassInp = new JPasswordField();

        // titkositas panel inicializalasa, es elemek bepakolasa
        encryptionPanel = new JPanel();
        encryptionPanel.setOpaque(true);
        encryptionPanel.setBorder(BorderFactory.createTitledBorder(Constants.UI_ENCRYPTION));
        encryptionPanel.setLayout(new GridLayout(4, 2, 2, 2));

        encryptionPanel.add(eSrcFileLbl);
        encryptionPanel.add(eSrcFileBtn);
        encryptionPanel.add(eDestDirLbl);
        encryptionPanel.add(eDestDirBtn);
        encryptionPanel.add(ePassLbl);
        encryptionPanel.add(ePassInp);
        encryptionPanel.add(placeholder2);
        encryptionPanel.add(eOkBtn);

        dSrcFileLbl = new JLabel(Constants.UI_TO_DEC);
        dSrcFileLbl.setFont(standardFont);

        dSrcFileBtn = new JButton(Constants.UI_SELECT);

        dSrcFileBtn.addActionListener(this);

        dDestDirLbl = new JLabel(Constants.UI_SAVE_DIR);
        dDestDirLbl.setFont(standardFont);

        dDestDirBtn = new JButton(Constants.UI_SELECT);

        dDestDirBtn.addActionListener(this);

        dPassLbl = new JLabel(Constants.UI_PW);
        dPassLbl.setFont(standardFont);
        dPassInp = new JPasswordField();

        dOkBtn = new JButton(Constants.UI_DECRYPTION);
        dOkBtn.addActionListener(this);

        // visszafejtes panel inicializalasa es elemek bepakolasa
        decryptionPanel = new JPanel();
        decryptionPanel.setOpaque(true);
        decryptionPanel.setBorder(BorderFactory.createTitledBorder(Constants.UI_DECRYPTION));
        decryptionPanel.setLayout(new GridLayout(4, 2, 2, 2));

        decryptionPanel.add(dSrcFileLbl);
        decryptionPanel.add(dSrcFileBtn);
        decryptionPanel.add(dDestDirLbl);
        decryptionPanel.add(dDestDirBtn);
        decryptionPanel.add(dPassLbl);
        decryptionPanel.add(dPassInp);
        decryptionPanel.add(placeholder);
        decryptionPanel.add(dOkBtn);

        // hozzadjuk mindket panelt a main content panehez
        contentPane.add(encryptionPanel);
        contentPane.add(decryptionPanel);

        // jframe dolgok
        frame = new JFrame();
        frame.setAutoRequestFocus(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(Constants.APP_NAME);
        frame.setIconImages(loadIcons());
        frame.setContentPane(contentPane);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // eventet kiválto objektum kiemelese
        final Object source = e.getSource();

        // titkositasi muveletek kezelese
        if (source == eSrcFileBtn) {
            eSrcFileBtn.setText(
                    registerFile(Constants.E_SRC_FILE, false));
        } else if (source == eDestDirBtn) {
            eDestDirBtn.setText(
                    registerFile(Constants.E_DIR, true));
        } else if (source == eOkBtn) {

            actionEncrypt(ePassInp.getPassword());

            // visszafejtesi muveletek kezelese
        } else if (source == dSrcFileBtn) {
            dSrcFileBtn.setText(
                    registerFile(Constants.D_SRC_FILE, false));
        } else if (source == dDestDirBtn) {
            dDestDirBtn.setText(
                    registerFile(Constants.D_DIR, true));
        } else if (source == dOkBtn) {

            actionDecrypt(dPassInp.getPassword());
        }

    }

    /*
        altalanos grafikus uzenet kiiro metodus, hogy leroviditsuk a beirando
        utasitas hosszat
     */
    public void message(Exception e, String message, int type) {
        if (e == null) {
            JOptionPane.showMessageDialog(this.frame, message, Constants.UI_MSG, type);
        } else {
            JOptionPane.showMessageDialog(this.frame,
                    message + " " + e.getMessage(),
                    Constants.UI_MSG, type);
        }

    }

    /*
        ugyan az a celja mint az elozonek, csak itt a felhasznalonak egy yes-no
        kerdes ablakot dob fel a program
     */
    public int confirm(String message, String title) {
        return JOptionPane.showConfirmDialog(this.frame, message, title, JOptionPane.YES_NO_OPTION);
    }

    /*
        titkositasi muveletet vezerlo metodus, parameterkent kapja a jelszot
     */
    public void actionEncrypt(char[] pass) {
        // ellenorizzuk hogy meg lettek-e adva a titkositashoz szukseges fajlok, ha nem, return
        if (!FILE_CACHE.containsKey(Constants.E_SRC_FILE)
                || !FILE_CACHE.containsKey(Constants.E_DIR)) {
            message(null, Constants.UI_MSG_GENERAL_PARAMETER_ERROR, ERROR_MESSAGE);
            return;
        }
        // figyelmeztetes hogy a megadott jelszo rovid
        if (pass.length < 8 && confirm(Constants.UI_MSG_PW_LENGTH, Constants.UI_MSG_WARNING) == JOptionPane.NO_OPTION) {
            return;
        }
        // annak bekerese, hogy toroljuk-e az eredeti plaintext fajlt a titkositas utan
        boolean deleteOriginal = (confirm(Constants.UI_MSG_DELETE, Constants.UI_MSG_WARNING) == JOptionPane.YES_OPTION);
        try {
            // kiemeljuk ket kulon objektumba a titkositando fajlt, es a mentes helyet jelolo fajlt objektumot
            final File encSrcFile = FILE_CACHE.get(Constants.E_SRC_FILE);
            final File encSaveDir = FILE_CACHE.get(Constants.E_DIR);
            FileIO io = new FileIO();
            // atalakitjuk a jelszot byte tombbe
            byte[] passBytes = Util.toBytes(pass);
            // fajl beolvasas, titkositas, bufferbe iras
            io.readAndEncryptCached(passBytes);
            // biztonsagi takaritas
            ePassInp.setText("");
            Arrays.fill(pass, '\u0000');
            Arrays.fill(ePassInp.getPassword(), '\u0000');

            // zaro muveletek elvegzese, ha a parameter true, torli az eredeti fajlt
            io.encDoFinal(deleteOriginal);

            // uzenet a muvelet sikeresseggerol
            message(null,
                    Constants.UI_MSG_E_SUCCESS + encSaveDir.toPath()
                    + System.getProperty("file.separator")
                    + encSrcFile.getName(),
                    JOptionPane.INFORMATION_MESSAGE);

            Arrays.fill(passBytes, (byte) 0);
        } catch (CryptoException | IOException e) {
            message(e, "Hiba:" + Constants.BREAK, ERROR_MESSAGE);
        } finally {
            // gui elemek szovegeinek visszallitasa
            reset();
        }

    }

    /*
        visszafejtes vezerleset vegzo metodus
   
     */
    public void actionDecrypt(char[] pass) {
        // ha elerte a harom rossz probalkozast, uzenet, takaritas, kilepes
        if (tries == 3) {
            message(null, Constants.UI_MSG_THREE_REACHED, ERROR_MESSAGE);
            Arrays.fill(pass, '\u0000');
            Arrays.fill(dPassInp.getPassword(), '\u0000');
            System.exit(0);
        }

        // ellenorizzuk hogy meg lettek-e adva a visszafejteshez szukseges fajlok, ha nem, return
        if (!FILE_CACHE.containsKey(Constants.D_SRC_FILE)
                || !FILE_CACHE.containsKey(Constants.D_DIR)) {
            message(null, Constants.UI_MSG_GENERAL_PARAMETER_ERROR, ERROR_MESSAGE);
            return;
        }

        try {
            // kiemeljuk ket kulon objektumba a visszafejtendo fajlt, es a mentes helyet jelolo fajlt objektumot
            final File decSrcFile = FILE_CACHE.get(Constants.D_SRC_FILE);
            final File decSaveDir = FILE_CACHE.get(Constants.D_DIR);
            FileIO io = new FileIO();
            // atalakitjuk a jelszot byte tombbe
            byte[] passBytes = Util.toBytes(pass);
            // fajl beolvasas, visszafejtes, bufferbe iras
            io.readAndDecryptCached(passBytes);
            // takaritas
            dPassInp.setText("");
            Arrays.fill(pass, '\u0000');
            Arrays.fill(dPassInp.getPassword(), '\u0000');
            io.decDoFinal();

            //tajekoztatas hogy a muvelet sikeres volt
            message(null,
                    Constants.UI_MSG_D_SUCCESS + decSaveDir.toPath()
                    + System.getProperty("file.separator")
                    + decSrcFile.getName(),
                    JOptionPane.INFORMATION_MESSAGE);
            Arrays.fill(passBytes, (byte) 0);
        } catch (CryptoException | IOException e) {
            // BadPaddingException dobodik, ha a megadott kulcs nem jo, ekkor megnoveljuk a probalkozasok szamat
            if (e.getCause() instanceof BadPaddingException) {
                message(null, Constants.UI_MSG_BAD_PW + (3 - tries), ERROR_MESSAGE);
                tries++;
            } else {
                message(e, "Hiba:" + e.getMessage() + Constants.BREAK, ERROR_MESSAGE);
            }
        } finally {
            reset();
        }
    }

    /*
        GUI elemek szovegenek eredeti allapotba visszaallitasat vegzo metodus
     */
    public void reset() {
        eSrcFileBtn.setText(Constants.UI_SELECT);
        eDestDirBtn.setText(Constants.UI_SELECT);
        dSrcFileBtn.setText(Constants.UI_SELECT);
        dDestDirBtn.setText(Constants.UI_SELECT);
    }

    /*
        FILE_CACHE-ben regisztralunk egy felhasznalotol bekert fajlt, a String
        parameterkent kapott kulcson
        ha a masodik parameter true, csak konyvtarakat fogad el a program
     */
    public String registerFile(String keyToRegister, boolean onlyDir) {
        final JFileChooser chooser = new JFileChooser();
        if (onlyDir) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        // ha a megadott elem megfelelo
        if (chooser.showOpenDialog(this.frame) == JFileChooser.APPROVE_OPTION) {
            // kiemeljuk, regisztraljuk a FILE_CACHE-ben
            File selected = chooser.getSelectedFile();
            FILE_CACHE.put(/*KULCS - STRING*/keyToRegister, /*ERTEK - FILE*/ selected);
            // visszaadjuk a megjelenitesre alkalmas nevet
            return Util.trim(selected.getName());
        } else {
            // ha nem adott meg elemet, visszaalitjuk a GUI elemeit, es a meghivo objektumnak is egy altalnos stringet adunk vissza
            reset();
            return Constants.UI_SELECT;
        }

    }

    /*
        betoltjuk a res konyvtarban levo osszes fajlt egy listaba, es visszaadjuk.
        azert szukseges, mert minden OS-en masmilyen meretu logo kell, es hagyni
        kell hogy a Java dontse el mekkorat szeretne hasznalni, kulonben pixeles lesz
        vagy megnyujtott stb...
    
        csak esztetikai okok
     */
    public List<Image> loadIcons() {
        List<Image> temp = new ArrayList<>();
        try {
            File resDir = new File("res");
            if (!resDir.exists()) {
                throw new RuntimeException("resource konyvtar nem talalhato!");
            }

            for (File f : resDir.listFiles()) {
                temp.add(ImageIO.read(f));
            }
            return temp;
        } catch (IOException e) {
            return null;
        }
    }

}
