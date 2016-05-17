/*
 Altalanos muveleteket vegzo osztaly
 Készítette: Fülöp Márk <fulop.mark@outlook.com>
 */
package filetitok.misc;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import org.bouncycastle.util.Arrays;

public class Util {

    
    // titkositott fajlok kiterjesztese
    static final String ENCRYPTED_EXTENSION = ".crypt";
    
    
    /*
        meggatoljuk a peldanyositast
    */
    private Util() {}

    /*
        a parameterkent kapott karakter tombot byte tombbe alakitjuk,
        es utana kinullazzuk a tombot, es a ByteBuffer-t (biztonsagi okok)
     */
    public static final byte[] toBytes(char[] c) {

        CharBuffer charbuff = CharBuffer.wrap(c);
        ByteBuffer bytebuff = StandardCharsets.UTF_8.encode(charbuff);
        byte[] bytes = Arrays.copyOfRange(bytebuff.array(), bytebuff.position(), bytebuff.limit());
        Arrays.fill(charbuff.array(), '\u0000');
        Arrays.fill(bytebuff.array(), (byte) 0);
        return bytes;
    }

    /*
        ha a kapott string hossza nagyobb mint 7, veszi az elso 7 karakteret,
        majd hozzailleszt harom pontot, kulonben visszaadja az eredeti stringet
     */
    public static String trim(String s) {
        return (s.length() > 7)
                ? s.substring(0, 7) + "..."
                : s;
    }

    /*
        a paramterkent kapott fajlnevbol eldonti hogy a fajlt egy titkositando,
        vagy egy titkositott fajl, es ez alapjan adja vissza a kimeneti fajl
        nevet
     */
    public static String makeFilename(String name) {
        return (name.indexOf(ENCRYPTED_EXTENSION) == -1)
                ? name.concat(ENCRYPTED_EXTENSION)
                : name.replace(ENCRYPTED_EXTENSION, "");
    }

}
