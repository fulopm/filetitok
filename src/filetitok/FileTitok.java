/*
 Készítette: Fülöp Márk 10.D <fulop.mark@outlook.com>
 Projektmunka programozás gyakorlat órára
 */
package filetitok;

import filetitok.gui.Window;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class FileTitok {

    private static final String PROGRAM_INFO = "FileTitok";

    public static void main(String[] args) {
        setLnF();
        SwingUtilities.invokeLater(new Window()::createAndShowGUI);
    }

    public static void setLnF() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (Exception e) {
            System.err.println("Nimbus L&F nem találhato!");
            throw new RuntimeException("Look and Feel nem talalhato");
        }

    }

    public static String getProgramInfo() {
        return PROGRAM_INFO;
    }

}
