/*
 Készítette: Fülöp Márk 10.D <fulop.mark@outlook.com>
 Projektmunka programozás gyakorlat órára
 */
package filetitok.gui;

import com.sun.glass.events.KeyEvent;
import filetitok.io.FileIO;
import filetitok.misc.Util;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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

    /* TITKOSITAS
    kezeloszervek amik a titkositas panelhez hasznalandoak -> ezeket adjuk
     hozza az encryptionPanelhez, es igy epul fel a GUI*/
    private JLabel eSrcFileLbl = null;
    private JButton eSrcFileBtn = null;

    private JLabel eDestDirLbl = null;
    private JButton eDestDirBtn = null;

    private JLabel eKeyLbl = null;
    private JPasswordField eKeyInp = null;

    private JButton eOkBtn = null;
    /* ----- */

 /* VISSZAFEJTES 
    kezeloszervek melyek a visszafejtes panelhez hasznalandoak */
    private JLabel dSrcFileLbl = null;
    private JButton dSrcFileBtn = null;

    private JLabel dDestDirLbl = null;
    private JButton dDestDirBtn = null;

    private JLabel dKeyLbl = null;
    private JPasswordField dKeyInp = null;

    private JButton dOkBtn = null;

    /* ----- */
    // placeholder objektumok (sima JLabel uresen)
    private final JLabel placeholder = new JLabel("");
    private final JLabel placeholder2 = new JLabel("");

    // betutipus konstansok
    private final Font standardFont = new Font(Constants.UI_FONT_NAME, Font.PLAIN, 11);
    private final Font footerFont = new Font(Constants.UI_FONT_NAME, Font.PLAIN, 9);

    // hogy ne kelljen mindig kiirni azt hogy JOptionPane.xy
    private final int ERROR = JOptionPane.ERROR_MESSAGE;

    // a rossz probalkozasok szamlaloja
    private int tries = 0;

    // osszes objektum inicializalasa, GUI felepitese, es lathatova tetele
    public void createAndShowGUI() {

        /* main frame content panejenek inicializasa, es layout beallitasa */
        contentPane = new JPanel();
        contentPane.setOpaque(true);

        contentPane.setLayout(new GridBagLayout());

        contentPane.setBorder(BorderFactory.createTitledBorder(new TitledBorder(""), Constants.AUTHOR, TitledBorder.TRAILING, TitledBorder.BOTTOM, footerFont, Color.black));

        /* ----- */

 /* inaktiv - GridBagConstraints objektum a megfelelo igazitashoz 
        final GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 50;
        c.gridwidth = 50;
        c.weightx = 50;
        c.weighty = 50;
         ----- */

 /* titkositas panel elemeinek inicializalasa es beallitasa */
        eSrcFileLbl = new JLabel(Constants.UI_TO_ENC);
        eSrcFileLbl.setFont(standardFont);

        eSrcFileBtn = new JButton(Constants.UI_SELECT);
        eSrcFileBtn.setBorder(new LineBorder(null, 0, false));
        eSrcFileBtn.addActionListener(this);

        eDestDirLbl = new JLabel(Constants.UI_SAVE_DIR);
        eDestDirLbl.setFont(standardFont);

        eDestDirBtn = new JButton(Constants.UI_SELECT);
        eDestDirBtn.setBorder(new LineBorder(null, 0, false));
        eDestDirBtn.addActionListener(this);

        eOkBtn = new JButton(Constants.UI_ENCRYPTION);
        eOkBtn.addActionListener(this);

        eDestDirBtn.setBorder(new LineBorder(null, 0, false));

        eKeyLbl = new JLabel(Constants.UI_KEY);
        eKeyLbl.setFont(standardFont);

        eKeyInp = new JPasswordField();

        /* ----- */

 /* titkositas panel inicializalasa, beallitasa */
        encryptionPanel = new JPanel();
        encryptionPanel.setOpaque(true);
        encryptionPanel.setBorder(BorderFactory.createTitledBorder(Constants.UI_ENCRYPTION));
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
        contentPane.add(encryptionPanel);

        /* visszafejtes panel elemeinek inicializalasa */
        dSrcFileLbl = new JLabel(Constants.UI_TO_DEC);
        dSrcFileLbl.setFont(standardFont);

        dSrcFileBtn = new JButton(Constants.UI_SELECT);
        dSrcFileBtn.setBorder(new LineBorder(null, 0, false));
        dSrcFileBtn.addActionListener(this);

        dDestDirLbl = new JLabel(Constants.UI_SAVE_DIR);
        dDestDirLbl.setFont(standardFont);

        dDestDirBtn = new JButton(Constants.UI_SELECT);
        dDestDirBtn.setBorder(new LineBorder(null, 0, false));
        dDestDirBtn.addActionListener(this);

        dKeyLbl = new JLabel(Constants.UI_KEY);
        dKeyLbl.setFont(standardFont);
        dKeyInp = new JPasswordField();

        dOkBtn = new JButton(Constants.UI_DECRYPTION);
        dOkBtn.addActionListener(this);
        dOkBtn.setBorder(new LineBorder(null, 0, false));
        /* ----- */

 /* visszafejtes panel elemeinek hozzadasa a panelhez */
        decryptionPanel = new JPanel();
        decryptionPanel.setOpaque(true);
        decryptionPanel.setBorder(BorderFactory.createTitledBorder(Constants.UI_DECRYPTION));
        decryptionPanel.setLayout(new GridLayout(4, 2, 2, 2));

        decryptionPanel.add(dSrcFileLbl);
        decryptionPanel.add(dSrcFileBtn);
        decryptionPanel.add(dDestDirLbl);
        decryptionPanel.add(dDestDirBtn);
        decryptionPanel.add(dKeyLbl);
        decryptionPanel.add(dKeyInp);
        decryptionPanel.add(placeholder);
        decryptionPanel.add(dOkBtn);
        /* ----- */

        // main frame content panejehez hozzaadjuk a visszafejtes panelt
        contentPane.add(decryptionPanel);

        /* main frame inicializalasa es megjelenitese */
        frame = new JFrame();
        frame.setAutoRequestFocus(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(Constants.PROGRAM);
        frame.setIconImages(loadIcons());
        frame.setContentPane(contentPane);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        /* ----- */
    }

    // action listener a gombokhoz
    @Override
    public void actionPerformed(ActionEvent e) {

        // eventet kivalto objektum kiemelese
        final Object source = e.getSource();

        /* TITKOSITASI MUVELETEK KEZELESE */
        if (source == eSrcFileBtn) {
            eSrcFileBtn.setText(registerFile(Constants.E_SRC_FILE, false));
        } else if (source == eDestDirBtn) {
            eDestDirBtn.setText(registerFile(Constants.E_DIR, true));
        } else if (source == eOkBtn) {
            message(null, Constants.UI_MSG_KEY, JOptionPane.WARNING_MESSAGE);

            // titkositas elvegzese
            actionEncrypt(eKeyInp.getPassword());
            /* ----- */

 /* VISSZAFEJTESI MUVELETEK KEZELESE */
        } else if (source == dSrcFileBtn) {
            dSrcFileBtn.setText(registerFile(Constants.D_SRC_FILE, false));
        } else if (source == dDestDirBtn) {
            dDestDirBtn.setText(registerFile(Constants.D_DIR, true));
        } else if (source == dOkBtn) {
            // visszafejtes elvegzese
            actionDecrypt(dKeyInp.getPassword());
        }

        /* ----- */
    }

    // grafikus hibauzentet megjelenitese -> rovidebb
    public void message(Exception e, String message, int type) {
        if (e == null) {
            JOptionPane.showMessageDialog(this.frame, message, Constants.UI_MSG, type);
        } else {
            JOptionPane.showMessageDialog(this.frame,
                    message + " " + e.getMessage(),
                    Constants.UI_MSG, type);
        }

    }

    // felhasznalonak igen-nem opcios kerdezo ablakot jelenit meg ->rovidebb
    public int confirm(String message, String title) {
        return JOptionPane.showConfirmDialog(this.frame, message, title, JOptionPane.YES_NO_OPTION);
    }

    // fajl titkositas
    public void actionEncrypt(char[] key) {
        // kulcshosszusag es egyeb parameterek ellenorzese
        if (key.length < 16
                || !FileIO.FILE_BUFFER.containsKey(Constants.E_SRC_FILE)
                || !FileIO.FILE_BUFFER.containsKey(Constants.E_DIR)) {
            message(null, Constants.UI_MSG_GENERAL_PARAMETER_ERROR, ERROR);
            return;
        }
        try {
            // fajl es konyvtar mutatoinak kiemelese
            final File encSrcFile = FileIO.FILE_BUFFER.get(Constants.E_SRC_FILE);
            final File encSaveDir = FileIO.FILE_BUFFER.get(Constants.E_DIR);
            FileIO io = new FileIO();
            // annak ellenorzese, hogy a titkositando fajl felulrija-e majd az
            // eredeti fájlt
            if (io.willOveride(encSaveDir, encSrcFile)) {
                if (confirm(Constants.UI_MSG_OVERIDE, Constants.UI_MSG_WARNING)
                        == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            // titkositas es kiiras elvegzese
            // csak az első 16 karaktert használjuk fel (okai vannak)
            io.encryptBufferedFile(Arrays.copyOfRange(key, 0, 16), this);
            io.encDoFinal(this);
            // tajekoztatas a folyamat sikeressegerol es az elkeszult fajl
            // eleresi utvonalarol
            message(null,
                    Constants.UI_MSG_E_SUCCESS + encSaveDir.toPath()
                    + System.getProperty("file.separator")
                    + encSrcFile.getName(),
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            message(e, "Hiba:" + Constants.BREAK, ERROR);
        } finally {
            reset();
        }
    }

    public void actionDecrypt(char[] key) {

        /* figyelmeztetes es program bezarasa, ha harom rossz probalkozas tortent*/
        if (tries == 3) {
            message(null, Constants.UI_MSG_THREE_REACHED, ERROR);
            this.frame.dispose();
        }
        /* ----- */
        if (key.length < 16
                || !FileIO.FILE_BUFFER.containsKey(Constants.D_SRC_FILE)
                || !FileIO.FILE_BUFFER.containsKey(Constants.D_DIR)) {
            message(null, Constants.UI_MSG_GENERAL_PARAMETER_ERROR, ERROR);
            return;
        }
        try {
            final File decSrcFile = FileIO.FILE_BUFFER.get(Constants.D_SRC_FILE);
            final File decSaveDir = FileIO.FILE_BUFFER.get(Constants.D_DIR);
            FileIO io = new FileIO();
            // csak az első 16 karaktert használjuk fel (okai vannak)
            io.decryptBufferedFile(Arrays.copyOfRange(key, 0, 16), this);
            io.decDoFinal(this);
            message(null,
                    Constants.UI_MSG_D_SUCCESS + decSaveDir.toPath()
                    + System.getProperty("file.separator")
                    + decSrcFile.getName(),
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (BadPaddingException e) {
            message(null, Constants.UI_MSG_BAD_KEY + (3 - tries), ERROR);
            // megnoveljuk a probalkozasok szamat vezeto valtozo erteket 1-gyel
            tries++;
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | InvalidKeyException ex) {
            message(ex, "Hiba: " + Constants.BREAK, ERROR);
        } finally {
            reset();
        }
    }

    public void reset() {
        eKeyInp.setText("");
        dKeyInp.setText("");
        eSrcFileBtn.setText(Constants.UI_SELECT);
        eDestDirBtn.setText(Constants.UI_SELECT);
        dSrcFileBtn.setText(Constants.UI_SELECT);
        dDestDirBtn.setText(Constants.UI_SELECT);
    }

    // beker egy fajlt a usertol egy
    // filechooser segitsegevel, majd regisztralja azt a file bufferben
    // (elso parameter: kulcs, amivel kesobb hivatkozunk a fajlra, a masodik
    // parameter true-ra allitasaval pedig szukithetjuk a valaszthato fajlok
    // koret csak konyvtarakra)
    public String registerFile(String keyToRegister, boolean onlyDir) {
        JFileChooser chooser = new JFileChooser();
        if (onlyDir) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        if (chooser.showOpenDialog(this.frame) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            FileIO.FILE_BUFFER.put(keyToRegister, selected);
            return Util.trim(selected.getName());
        } else {
            message(null, "Nem választott fájlt!", ERROR);
            reset();
            return Constants.UI_SELECT;
        }

    }

    // ez a metodus azert szukseges, mert minden operacios rendszeren mas-mas
    // meretu ikon kell, hogy legyen hasznalva esztetikai okok miatt, es igy a 
    // java ki tudja valasztani hogy O melyik meretut szeretne hasznalni
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
