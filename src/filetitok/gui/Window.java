/*
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

public class Window implements ActionListener {

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

    private final int ERROR = JOptionPane.ERROR_MESSAGE;

    private int tries = 0;

    public void createAndShowGUI() {

        contentPane = new JPanel();
        contentPane.setOpaque(true);

        contentPane.setLayout(new GridBagLayout());

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

        contentPane.add(encryptionPanel);

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

        contentPane.add(decryptionPanel);

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

        final Object source = e.getSource();

        if (source == eSrcFileBtn) {
            eSrcFileBtn.setText(registerFile(Constants.E_SRC_FILE, false));
        } else if (source == eDestDirBtn) {
            eDestDirBtn.setText(registerFile(Constants.E_DIR, true));
        } else if (source == eOkBtn) {

            actionEncrypt(ePassInp.getPassword());

        } else if (source == dSrcFileBtn) {
            dSrcFileBtn.setText(registerFile(Constants.D_SRC_FILE, false));
        } else if (source == dDestDirBtn) {
            dDestDirBtn.setText(registerFile(Constants.D_DIR, true));
        } else if (source == dOkBtn) {

            actionDecrypt(dPassInp.getPassword());
        }

    }

    public void message(Exception e, String message, int type) {
        if (e == null) {
            JOptionPane.showMessageDialog(this.frame, message, Constants.UI_MSG, type);
        } else {
            JOptionPane.showMessageDialog(this.frame,
                    message + " " + e.getMessage(),
                    Constants.UI_MSG, type);
        }

    }

    public int confirm(String message, String title) {
        return JOptionPane.showConfirmDialog(this.frame, message, title, JOptionPane.YES_NO_OPTION);
    }

    public void actionEncrypt(char[] key) {

        if (!FILE_CACHE.containsKey(Constants.E_SRC_FILE)
                || !FILE_CACHE.containsKey(Constants.E_DIR)) {
            message(null, Constants.UI_MSG_GENERAL_PARAMETER_ERROR, ERROR);
            return;
        }
        if (key.length < 8 && confirm(Constants.UI_MSG_PW_LENGTH, Constants.UI_MSG_WARNING) == JOptionPane.NO_OPTION) {
            return;
        }
        message(null, Constants.UI_MSG_PW, JOptionPane.WARNING_MESSAGE);

        try {

            final File encSrcFile = FILE_CACHE.get(Constants.E_SRC_FILE);
            final File encSaveDir = FILE_CACHE.get(Constants.E_DIR);
            FileIO io = new FileIO();

            if (io.willOveride(encSaveDir, encSrcFile) && confirm(Constants.UI_MSG_OVERIDE, Constants.UI_MSG_WARNING) == JOptionPane.NO_OPTION) {

                return;

            }

            io.encryptBufferedFile(Util.convertCharsToBytes(key));
            io.encDoFinal();
            message(null,
                    Constants.UI_MSG_E_SUCCESS + encSaveDir.toPath()
                    + System.getProperty("file.separator")
                    + encSrcFile.getName(),
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (CryptoException | IOException e) {
            message(e, "Hiba:" + Constants.BREAK, ERROR);
        } finally {
            reset();
        }
    }

    public void actionDecrypt(char[] key) {

        if (tries == 3) {
            message(null, Constants.UI_MSG_THREE_REACHED, ERROR);
            this.frame.dispose();
        }

        if (!FILE_CACHE.containsKey(Constants.D_SRC_FILE)
                || !FILE_CACHE.containsKey(Constants.D_DIR)) {
            message(null, Constants.UI_MSG_GENERAL_PARAMETER_ERROR, ERROR);
            return;
        }

        try {
            final File decSrcFile = FILE_CACHE.get(Constants.D_SRC_FILE);
            final File decSaveDir = FILE_CACHE.get(Constants.D_DIR);
            FileIO io = new FileIO();
            io.decryptBufferedFile(Util.convertCharsToBytes(key));
            io.decDoFinal();
            message(null,
                    Constants.UI_MSG_D_SUCCESS + decSaveDir.toPath()
                    + System.getProperty("file.separator")
                    + decSrcFile.getName(),
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (CryptoException | IOException e) {
            if (e.getCause() instanceof BadPaddingException) {
                message(null, Constants.UI_MSG_BAD_PW + (3 - tries), ERROR);
                tries++;
                e.printStackTrace();
            } else {
                message(e, "Hiba:" + Constants.BREAK, ERROR);
            }
        } finally {
            reset();
        }
    }

    public void reset() {
        ePassInp.setText("");
        dPassInp.setText("");
        eSrcFileBtn.setText(Constants.UI_SELECT);
        eDestDirBtn.setText(Constants.UI_SELECT);
        dSrcFileBtn.setText(Constants.UI_SELECT);
        dDestDirBtn.setText(Constants.UI_SELECT);
    }

    public String registerFile(String keyToRegister, boolean onlyDir) {
        final JFileChooser chooser = new JFileChooser();
        if (onlyDir) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        if (chooser.showOpenDialog(this.frame) == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            FILE_CACHE.put(keyToRegister, selected);
            return Util.trim(selected.getName());
        } else {
            reset();
            return Constants.UI_SELECT;
        }

    }

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
