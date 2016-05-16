/*
 Készítette: Fülöp Márk <fulop.mark@outlook.com>
 */
package filetitok;

import filetitok.gui.Window;
import java.security.Security;
import javax.swing.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class FileTitok {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        Security.addProvider(new BouncyCastleProvider());
        SwingUtilities.invokeLater(new Window()::createAndShowGUI);

    }

}
