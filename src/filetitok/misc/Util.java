/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filetitok.misc;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 *
 * @author fulopm
 */
public class Util {

    // egy karakter tombot bajt tombbe alakit at
    public static final byte[] convertCharsToBytes(char[] c) {

        return new String(c).getBytes(StandardCharsets.UTF_8);

    }

    public static String trim(String s) {
        return (s.length() > 7 ? s.substring(0, 7) + "..." : s);
    }

}
