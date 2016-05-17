/*
 Main osztaly
 Készítette: Fülöp Márk <fulop.mark@outlook.com>
 */
package filetitok;

import filetitok.gui.Window;
import java.security.Security;
import javax.swing.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class FileTitok {

    /*
        Az USA-beli exportalasi torvenyek miatt a Java alapveto titkositasi csomagjanak
        (JCE) kulcsmerete korlatozva van, ezert a main metodusban
        egy kulso konyvtarat (Bouncy Castle) regisztralunk a Java biztonsagi nyilvantartojaban.
    
        A masodik sorban gyakorlatilag egy sima peldanyositas tortenik (Window
        osztaly, majd a GUI-t felepito es megjelenito metodus meghivasa).
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        Security.addProvider(new BouncyCastleProvider());
        SwingUtilities.invokeLater(
                new Window()::createAndShowGUI
        );

    }

}
