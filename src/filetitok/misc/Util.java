/*
 Készítette: Fülöp Márk 10.D <fulop.mark@outlook.com>
 Projektmunka programozás gyakorlat órára
 */
package filetitok.misc;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author fulopm
 */
public class Util {

    private Util() {
        
    }
    // egy karakter tombot bajt tombbe alakit at
    public static final byte[] convertCharsToBytes(char[] c) {

        return new String(c).getBytes(StandardCharsets.UTF_8);

    }

    public static String trim(String s) {
        return (s.length() > 7 ? s.substring(0, 7) + "..." : s);
    }

}
