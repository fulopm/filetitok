/*
 Készítette: Fülöp Márk <fulop.mark@outlook.com>
 */
package filetitok;

public class Constants {


    public static final String APP_NAME = "FileTitok";
    public static final String AUTHOR = "Fülöp Márk, 10.D";
    public static final String BREAK = System.getProperty("line.separator");
    public static final String E_SRC_FILE = "e_src_file";
    public static final String E_DIR = "e_dir";
    public static final String D_SRC_FILE = "d_src_file";
    public static final String D_DIR = "d_dir";
    public static final String UI_FONT_NAME = "Segoe UI";


    public static final String UI_TO_ENC = "Titkosítandó fájl:";
    public static final String UI_TO_DEC = "Visszafejtendő fájl:";
    public static final String UI_SAVE_DIR = "Mentés helye:";
    public static final String UI_ENCRYPTION = "Titkosítás";
    public static final String UI_DECRYPTION = "Visszafejtés";
    public static final String UI_PW = "Jelszó:";
    public static final String UI_SELECT = "Kiválasztás...";


    public static final String UI_MSG_WARNING = "Figyelem!";
    public static final String UI_MSG = "Üzenet";
    public static final String UI_MSG_E_SUCCESS = "Sikeres titkosítás!" + BREAK + "Új fájl: ";
    public static final String UI_MSG_D_SUCCESS = "Sikeres visszafejtés!" + BREAK + "Új fájl: ";
    public static final String UI_MSG_DIR_NOT_AVAIL = "Úgy tűnik, hogy a kiválasztott könyvtár valami miatt nem elérhető." + BREAK + "Ellenőrizze, hogy a programnak van-e jogosultsága olvasni azt," + BREAK + "majd próbálja újra.";
    public static final String UI_MSG_THREE_REACHED = "Három próbálkozás elérve, tovább nem próbálkozhat!" + BREAK + "A program kilép.";
    public static final String UI_MSG_GENERAL_PARAMETER_ERROR = "Nincs megadva a titkosítandó/visszafejtendő fájl, a kulcs, vagy a mentés helye.";
    public static final String UI_MSG_BAD_PW = "Rossz jelszót adott meg! Maradék próbálkozások száma: ";
    public static final String UI_MSG_PW_LENGTH = "A jelszó túl gyenge. Ajánlott minimum 8 karakteres jelszót megadni." + BREAK + "Folytatja?";
    //public static final String UI_MSG_PW = "Nagyon fontos, hogy a megadott jelszó hiányában" + BREAK + "a későbbiekben a titkosított fájl nem lesz visszafejthető." + BREAK + "A titkosító kulcsot jól jegyezze meg!";
    public static final String UI_MSG_DELETE = "Töröljem az eredeti fájlt a titkosítás végeztével?";

}
