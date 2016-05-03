/*
 Készítette: Fülöp Márk 10.D <fulop.mark@outlook.com>
 Projektmunka programozás gyakorlat órára
 */
package filetitok.gui;

public class Constants {

    // altalanos segedkonstansok
    public static final String PROGRAM = "FileTitok ";
    public static final String AUTHOR = "Fülöp Márk, 10.D";
    public static final String BREAK = System.getProperty("line.separator");
    public static final String E_SRC_FILE = "e_src_file";
    public static final String E_DIR = "e_dir";
    public static final String D_SRC_FILE = "d_src_file";
    public static final String D_DIR = "d_dir";
    public static final String UI_FONT_NAME = "Segoe UI";

    // UI konstansok (labelek es gombok szovegei)
    public static final String UI_TO_ENC = "Titkosítandó fájl:";
    public static final String UI_TO_DEC = "Visszafejtendő fájl:";
    public static final String UI_SAVE_DIR = "Mentés helye:";
    public static final String UI_ENCRYPTION = "Titkosítás";
    public static final String UI_DECRYPTION = "Visszafejtés";
    public static final String UI_KEY = "Kulcs (min. 16 karakter):";
    public static final String UI_SELECT = "Kiválasztás...";

    // felugro ablakokhoz kapcsolodo hibauzenetek
    public static final String UI_MSG_WARNING = "Figyelem!";
    public static final String UI_MSG = "Üzenet";
    public static final String UI_MSG_E_SUCCESS = "Sikeres titkosítás!" + BREAK + "Új fájl: ";
    public static final String UI_MSG_D_SUCCESS = "Sikeres visszafejtés!" + BREAK + "Új fájl: ";
    public static final String UI_MSG_DIR_NOT_AVAIL = "Úgy tűnik, hogy a kiválasztott könyvtár valami miatt nem elérhető." + BREAK + "Ellenőrizze, hogy a programnak van-e jogosultsága olvasni azt," + BREAK + "majd próbálja újra.";
    public static final String UI_MSG_THREE_REACHED = "Három próbálkozás elérve, tovább nem próbálkozhat!" + BREAK + "A program kilép.";
    public static final String UI_MSG_GENERAL_PARAMETER_ERROR = "Nincs megadva a visszafejtendő fájl, a kulcs, vagy a mentés helye," + BREAK + "vagy a kulcs hossza kevesebb, mint 16 karakter!";
    public static final String UI_MSG_BAD_KEY = "Rossz kulcsot adott meg! Maradék próbálkozások száma: ";
    public static final String UI_MSG_KEY = "Nagyon fontos, hogy a megadott kulcs hiányában" + BREAK + "a későbbiekben a titkosított fájl nem lesz visszafejthető." + BREAK + "A titkosító kulcsot jól jegyezze meg!";
    public static final String UI_MSG_OVERIDE = "Felülírni készül az eredeti fájlt, mert a mentés" + BREAK + "helye és a forrásfájl helye megegyezik. Folytatja?";

}
