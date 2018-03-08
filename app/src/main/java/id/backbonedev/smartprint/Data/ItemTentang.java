package id.backbonedev.smartprint.Data;

/**
 * Created by Aziz Nur Ariffianto on 0009, 09 Jan 2018.
 */

public class ItemTentang
{
    int icon;
    String teks, subteks, link;

    public ItemTentang()
    {
    }

    public ItemTentang(int icon, String teks, String subteks, String link)
    {
        this.icon = icon;
        this.teks = teks;
        this.subteks = subteks;
        this.link = link;
    }

    public int getIcon()
    {
        return icon;
    }

    public void setIcon(int icon)
    {
        this.icon = icon;
    }

    public String getTeks()
    {
        return teks;
    }

    public void setTeks(String teks)
    {
        this.teks = teks;
    }

    public String getSubteks()
    {
        return subteks;
    }

    public void setSubteks(String subteks)
    {
        this.subteks = subteks;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }
}
