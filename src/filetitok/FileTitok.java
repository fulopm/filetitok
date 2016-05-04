/*
 Készítette: Fülöp Márk 10.D <fulop.mark@outlook.com>
 Projektmunka programozás gyakorlat órára
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

    // look and feel (kinézet) beállítása
    private static void init() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        /* szoveg elsimitas bekapcsolasa */
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        /* ----- */
        boolean set = false;
       
            for (LookAndFeelInfo i : UIManager.getInstalledLookAndFeels()) {
                if (i.getName().equals("Nimbus")) {
                    UIManager.setLookAndFeel(i.getClassName());
                    set = true;
                    break;
                }
            }

       

    }
}
