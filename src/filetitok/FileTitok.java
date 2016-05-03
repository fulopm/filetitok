/*
 Készítette: Fülöp Márk 10.D <fulop.mark@outlook.com>
 Projektmunka programozás gyakorlat órára
 */
package filetitok;

import filetitok.gui.Window;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;

public class FileTitok {

    public static void main(String[] args) {

        /* szoveg elsimitas bekapcsolasa */
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        /* ----- */

        setLaF();

        SwingUtilities.invokeLater(new Window()::createAndShowGUI);
    }

    // look and feel (kinézet) beállítása
    private static void setLaF() {
        boolean set = false;
        try {
            for (LookAndFeelInfo i : UIManager.getInstalledLookAndFeels()) {
                if (i.getName().equals("Nimbus")) {
                    UIManager.setLookAndFeel(i.getClassName());
                    set = true;
                    break;
                }
            }

            if (!set) {
                throw new RuntimeException("nimbus LaF nem talalhato");
            }
        } catch (Exception ex) {
            // silence is golden
        }

    }
}
