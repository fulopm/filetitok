/*
 Készítette: Fülöp Márk <fulop.mark@outlook.com>
 */
package filetitok;

import filetitok.gui.Window;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

public class FileTitok {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        init();
        SwingUtilities.invokeLater(new Window()::createAndShowGUI);

    }

    private static void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        for (final LookAndFeelInfo i : UIManager.getInstalledLookAndFeels()) {
            if (i.getName().equals("Nimbus")) {
                UIManager.setLookAndFeel(i.getClassName());

            }
        }

    }
}
