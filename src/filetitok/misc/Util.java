/*
 Készítette: Fülöp Márk <fulop.mark@outlook.com>
 */
package filetitok.misc;

import java.nio.charset.StandardCharsets;

public class Util {

    private Util() {}

    public static final byte[] convertCharsToBytes(char[] c) {

        return new String(c).getBytes(StandardCharsets.UTF_8);

    }

    public static String trim(String s) {
        return (s.length() > 7 ? s.substring(0, 7) + "..." : s);
    }

    public static String makeFilename(String name) {
        return (name.indexOf(".crypt") == -1) ? name.concat(".crypt") : name.replace(".crypt", "");
    }

}
