/*
 Készítette: Fülöp Márk 10.D <fulop.mark@outlook.com>
 Projektmunka programozás gyakorlat órára
 */
package filetitok.gui;

import filetitok.FileTitok;
import filetitok.io.FileIO;
import filetitok.misc.Util;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class Window implements ActionListener {

    // main frame, es a content panelje
    private JFrame frame;
    private JPanel contentPane;

    // ket kulon panel, amelyek kulon a titkositashoz es a visszafejteshez
    // tartalmazzak az kezeloszerveket -> ezeket adjuk a contentPane-hez
    private JPanel encryptionPanel;
    private JPanel decryptionPanel;

    // kezeloszervek amik a titkositas panelhez hasznalandoak -> ezeket adjuk
    // hozza az encryptionPanelhez, es igy epul fel a GUI
    private JLabel eSrcFileLbl = null;
    private JLabel eDestDirLbl = null;
    private JLabel eKeyLbl = null;
    private JButton eSrcFileBtn = null;
    private JButton eDestDirBtn = null;
    private JPasswordField eKeyInp = null;
    private JButton eOkBtn = null;

    // kezeloszervek melyek a visszafejtes panelhez hasznalandoak
    private JLabel dSrcFileLbl = null;
    private JButton dSrcFileBtn = null;
    private JLabel dKeyLbl = null;
    private JPasswordField dKeyInp = null;
    private JLabel dSaveDirLbl = null;
    private JButton dSaveDirBtn = null;
    private JButton dOkBtn = null;

    // placeholder objektumok (sima JLabel uresen)
    private final JLabel placeholder = new JLabel("");
    private final JLabel placeholder2 = new JLabel("");

    // betutipus konstansok
    private final Font standardFont = new Font("Tahoma", Font.PLAIN, 11);
    private final Font footerFont = new Font("Tahoma", Font.PLAIN, 9);

    private final String BREAK = System.lineSeparator();
    private final int ERROR = JOptionPane.ERROR_MESSAGE;
    private final String CONST_SELECT = "Kiválasztás...";
    private int tries = 0;

    // osszes objektum inicializalasa, GUI felepitese, es lathatova tetele
    public void createAndShowGUI() {

        /* main frame content panejenek inicializasa, es layout beallitasa */
        contentPane = new JPanel();
        contentPane.setOpaque(true);

        contentPane.setLayout(new GridBagLayout());

        contentPane.setBorder(BorderFactory.createTitledBorder(new TitledBorder(""), "Fülöp Márk, 10.D", TitledBorder.TRAILING, TitledBorder.BOTTOM, footerFont, Color.black));

        /* ----- */

 /* GridBagConstraints objektum a megfelelo igazitashoz */
        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 50;
        c.weightx = 50;
        c.weighty = 50;
        /* ----- */

 /* titkositas panel elemeinek inicializalasa es beallitasa */
        eSrcFileLbl = new JLabel("Titkosítandó fájl: ");
        eSrcFileLbl.setFont(standardFont);

        eSrcFileBtn = new JButton(CONST_SELECT);
        eSrcFileBtn.setBorder(new LineBorder(null, 0, false));
        eSrcFileBtn.addActionListener(this);

        eDestDirLbl = new JLabel("Mentés helye: ");
        eDestDirLbl.setFont(standardFont);

        eDestDirBtn = new JButton(CONST_SELECT);
        eDestDirBtn.setBorder(new LineBorder(null, 0, false));
        eDestDirBtn.addActionListener(this);
        eOkBtn = new JButton("Titkosítás");
        eOkBtn.addActionListener(this);
        eDestDirBtn.setBorder(new LineBorder(null, 0, false));

        eKeyLbl = new JLabel("Kulcs: ");
        eKeyLbl.setFont(standardFont);

        eKeyInp = new JPasswordField();

        /* ----- */

 /* titkositas panel inicializalasa, beallitasa */
        encryptionPanel = new JPanel();
        encryptionPanel.setOpaque(true);
        encryptionPanel.setBorder(BorderFactory.createTitledBorder("Titkosítás"));
        encryptionPanel.setLayout(new GridLayout(4, 2, 2, 2));
        /* ----- */

 /* titkositas panel eleminek hozzaadasa a panelhez */
        encryptionPanel.add(eSrcFileLbl);
        encryptionPanel.add(eSrcFileBtn);
        encryptionPanel.add(eDestDirLbl);
        encryptionPanel.add(eDestDirBtn);
        encryptionPanel.add(eKeyLbl);
        encryptionPanel.add(eKeyInp);
        encryptionPanel.add(placeholder2);
        encryptionPanel.add(eOkBtn);
        /* ----- */

        // main frame content panejehez hozzaadjuk a titositas panelt
        contentPane.add(encryptionPanel, c);

        /* visszafejtes panel elemeinek inicializalasa */
        dSrcFileLbl = new JLabel("Visszafejtendő fájl: ");
        dSrcFileLbl.setFont(standardFont);

        dSrcFileBtn = new JButton(CONST_SELECT);
        dSrcFileBtn.setBorder(new LineBorder(null, 0, false));
        dSrcFileBtn.addActionListener(this);

        dSaveDirLbl = new JLabel("Mentés helye: ");
        dSaveDirLbl.setFont(standardFont);

        dSaveDirBtn = new JButton(CONST_SELECT);
        dSaveDirBtn.setBorder(new LineBorder(null, 0, false));
        dSaveDirBtn.addActionListener(this);

        dKeyLbl = new JLabel("Kulcs: ");
        dKeyLbl.setFont(standardFont);
        dKeyInp = new JPasswordField();

        dOkBtn = new JButton("Visszafejtés");
        dOkBtn.addActionListener(this);
        dOkBtn.setBorder(new LineBorder(null, 0, false));
        /* ----- */

 /* visszafejtes panel elemeinek hozzadasa a panelhez */
        decryptionPanel = new JPanel();
        decryptionPanel.setOpaque(true);
        decryptionPanel.setBorder(BorderFactory.createTitledBorder("Visszafejtés"));
        decryptionPanel.setLayout(new GridLayout(4, 2, 2, 2));

        decryptionPanel.add(dSrcFileLbl);
        decryptionPanel.add(dSrcFileBtn);
        decryptionPanel.add(dSaveDirLbl);
        decryptionPanel.add(dSaveDirBtn);
        decryptionPanel.add(dKeyLbl);
        decryptionPanel.add(dKeyInp);
        decryptionPanel.add(placeholder);
        decryptionPanel.add(dOkBtn);
        /* ----- */

        // main frame content panejehez hozzaadjuk a visszafejtes panelt
        contentPane.add(decryptionPanel, c);

        /* main frame inicializalasa es megjelenitese */
        frame = new JFrame();
        frame.setAutoRequestFocus(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(FileTitok.getProgramInfo());
        frame.setContentPane(contentPane);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setIconImages(loadIcons());
        frame.setVisible(true);
        /* ----- */
    }

    // action listener a gombokhoz
    @Override
    public void actionPerformed(ActionEvent e) {

        // eventet kivalto objektum kiemelese
        final Object source = e.getSource();

        /* TITKOSITASI MUVELETEK KEZELESE */
        // titkositando fajl gomb
        if (source == eSrcFileBtn) {
            eSrcFileBtn.setText(registerFile("e_src_file", false));

            // titikositando fajl mentesenek helye gomb
        } else if (source == eDestDirBtn) {
            eDestDirBtn.setText(registerFile("e_dir", true));
            // titkositas OK gomb
        } else if (source == eOkBtn) {
            message(null, "Nagyon fontos, hogy a megadott kulcs hiányában a későbbiekben" + BREAK
                    + "a fájlhoz nem fog tudni hozzáférni!", JOptionPane.WARNING_MESSAGE);
            // titkositas elvegzese
            actionEncrypt(eKeyInp.getPassword());
            /* ----- */

 /* VISSZAFEJTESI MUVELETEK KEZELESE */
        } else if (source == dSrcFileBtn) {
            dSrcFileBtn.setText(registerFile("d_src_file", false));
        } else if (source == dSaveDirBtn) {
            dSaveDirBtn.setText(registerFile("d_dir", true));
        } else if (source == dOkBtn) {
            // visszafejtes elvegzese
            actionDecrypt(dKeyInp.getPassword());
        }

        /* ----- */
    }

    // grafikus hibauzentet megjelenitese
    public void message(Exception e, String message, int type) {
        if (e == null) {
            JOptionPane.showMessageDialog(this.frame, message, "Üzenet", type);
        } else {
            JOptionPane.showMessageDialog(this.frame,
                    message + " " + e.getMessage(),
                    "Üzenet", type);
        }

    }

    public int confirm(String message, String title) {
        return JOptionPane.showConfirmDialog(this.frame, message, title, JOptionPane.YES_NO_OPTION);
    }

    // fajl titkositas
    public void actionEncrypt(char[] key) {

        // kulcshosszusag es egyeb parameterek ellenorzese
        if (key.length != 16
                || !FileIO.FILE_BUFFER.containsKey("e_src_file")
                || !FileIO.FILE_BUFFER.containsKey("e_dir")) {
            message(null, "Nincs megadva a titkosítandó fájl, a kulcs, vagy a mentés helye,"
                    + System.lineSeparator()
                    + "vagy a kulcs hosszúsága nem 16!", ERROR);
            return;
        }
        try {
            // fajl es konyvtar mutatoinak kiemelese
            final File encSrcFile = FileIO.FILE_BUFFER.get("e_src_file");
            final File encSaveDir = FileIO.FILE_BUFFER.get("e_dir");
            FileIO io = new FileIO();
            // annak ellenorzese, hogy a titkositando fajl felulrija-e majd az
            // eredeti fájlt
            if (io.willOveride(encSaveDir, encSrcFile)) {
                if (confirm("Felülírni készül az eredeti fájlt, mert a mentés"
                        + "helye és a forrásfájl helye megegyezik. Folytatja?", "Figyelem")
                        == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            io.encryptBufferedFile(key, this);
            io.encDoFinal(this);

            message(null,
                    "Sikeres titkosítás!" + BREAK
                    + "Új fájl: " + encSaveDir.toPath()
                    + System.getProperty("file.separator")
                    + encSrcFile.getName(),
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            message(e, "Hiba:" + BREAK, ERROR);
        } finally {
            reset();
        }
    }

    public void actionDecrypt(char[] key) {
        if (tries == 3) {
            message(null, "Három próbálkozás elérve, tovább nem próbálkozhat!" + BREAK + "A program kilép.", ERROR);
            throw new RuntimeException("3 probalkozas elerve");
        }
        if (key.length != 16
                || !FileIO.FILE_BUFFER.containsKey("d_src_file")
                || !FileIO.FILE_BUFFER.containsKey("d_dir")) {
            message(null, "Nincs megadva a visszafejtendő fájl, a kulcs, vagy a mentés helye,"
                    + System.lineSeparator()
                    + "vagy a kulcs hosszúsága nem 16!", ERROR);
            return;
        }
        try {
            final File decSrcFile = FileIO.FILE_BUFFER.get("d_src_file");
            final File decSaveDir = FileIO.FILE_BUFFER.get("d_dir");
            FileIO io = new FileIO();

            io.decryptBufferedFile(key, this);
            io.decDoFinal(this);
            message(null,
                    "Sikeres visszafejtés!" + BREAK
                    + "Új fájl: " + decSaveDir.toPath()
                    + System.getProperty("file.separator")
                    + decSrcFile.getName(),
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (BadPaddingException e) {
            message(null, "Hibás kulcsot adott meg!", ERROR);
            tries++;
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | InvalidKeyException ex) {
            message(ex, "Hiba: " + BREAK, ERROR);
        } finally {
            reset();
        }
    }

    public void reset() {
        FileIO.FILE_BUFFER.clear();
        eKeyInp.setText("");
        eSrcFileBtn.setText(CONST_SELECT);
        eDestDirBtn.setText(CONST_SELECT);
        dKeyInp.setText("");
        dSrcFileBtn.setText(CONST_SELECT);
        dSaveDirBtn.setText(CONST_SELECT);
    }

    public String registerFile(String keyToRegister, boolean onlyDir) {
        JFileChooser chooser = new JFileChooser();
        if (onlyDir) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        if (chooser.showOpenDialog(this.frame) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            FileIO.FILE_BUFFER.put(keyToRegister, selected);
            System.out.println(selected.getName());
            return Util.trim(selected.getName());
        } else {
            message(null, "Nem választott fájlt!", ERROR);
            reset();
            return CONST_SELECT;
        }

    }

    // ez a metodus azert szukseges, mert minden operacios rendszeren mas-mas
    // meretu ikon kell, hogy legyen hasznalva esztetikai okok miatt, es igy a 
    // java ki tudja valasztani hogy O melyik meretut szeretne hasznalni
    public ArrayList<Image> loadIcons() {
        try {
            ArrayList<Image> temp = new ArrayList<>();
            File resfolder = new File("res");
            File[] icons = resfolder.listFiles();
            for (File f : icons) {
                temp.add(ImageIO.read(f));
            }
            return temp;
        } catch (Exception e) {
            return null;
        }
    }

}
