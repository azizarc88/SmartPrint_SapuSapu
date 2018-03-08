package id.backbonedev.smartprint.App;

/**
 * Created by Aziz Nur Ariffianto on 10 Agustus 2016.
 */
public class Crypt
{
    public static final String PASSWORD_CRYPT = "jksdhngi7ws457nveruhscnierugjh3498756";

    private static int Konversi(char karakter)
    {
        return (int) karakter;
    }

    private static char Deversi(int nilai)
    {
        return (char) nilai;
    }

    public static String Encrypt(String teks)
    {
        String kunci = PASSWORD_CRYPT;
        String h = "";
        char[] cc = kunci.toCharArray();
        int b = 0;
        int i;

        for (char c : teks.toCharArray())
        {
            i = Konversi(c) + Konversi(cc[b]);
            b++;
            if (b == kunci.length())
            {
                b = 0;
            }

            h = h + Deversi(i);
        }

        return h;
    }

    public static String Decrypt(String teks)
    {
        String kunci = PASSWORD_CRYPT;
        String h = "";
        char[] cc = kunci.toCharArray();
        int b = 0;
        int i;

        for (char c : teks.toCharArray())
        {
            i = Konversi(c) - Konversi(cc[b]);
            b++;
            if (b == kunci.length())
            {
                b = 0;
            }

            h = h + Deversi(i);
        }

        return h;
    }
}
