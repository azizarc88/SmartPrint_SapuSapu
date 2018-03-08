package id.backbonedev.smartprint.App;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aziz Nur Ariffianto on 26 Agustus 2016.
 */
public class TanggalHandler
{
    public static String KonversiTanggal(String data, boolean full)
    {
        String expectedPattern, format;
        if (full)
        {
            expectedPattern = "yyyy-MM-dd HH:mm:ss";
            format = "dd MMMM yyyy hh:mm a";
        }
        else
        {
            expectedPattern = "yyyy-MM-dd";
            format = "dd MMMM yyyy";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);

        Date tanggal = null;
        try
        {
            tanggal = formatter.parse(data);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        sdf.setTimeZone(cal.getTimeZone());

        return sdf.format(tanggal);
    }

    public static String KonversiWaktu(String data)
    {
        String expectedPattern, format;
        expectedPattern = "yyyy-MM-dd HH:mm:ss";
        format = "hh:mm a";
        SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);

        Date tanggal = null;
        try
        {
            tanggal = formatter.parse(data);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar cal = Calendar.getInstance();
        sdf.setTimeZone(cal.getTimeZone());

        return sdf.format(tanggal);
    }

    public static String AmbilWaktu()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df3 = new SimpleDateFormat("dd MMMM yyyy hh:mm a");

        return df3.format(c.getTime());
    }

    public static String AmbilWaktu(String format)
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df3 = new SimpleDateFormat(format);

        return df3.format(c.getTime());
    }

    public static long AmbilWaktuMilis(String srcDate)
    {
        SimpleDateFormat desiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long dateInMillis = 0;
        try
        {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    public static Calendar AmbilWaktuCalendar(String srcDate, boolean full)
    {
        SimpleDateFormat desiredFormat;

        if (full)
        {
            desiredFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        else
        {
            desiredFormat = new SimpleDateFormat("yyyy-MM-dd");
        }

        long dateInMillis = 0;
        try
        {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static String AmbilJarakWaktu(String waktu)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String hasil = "";
        try
        {
            Date past = format.parse(waktu);

            Date now = new Date();
            long detik= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long menit=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long jam=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long hari=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
            long bulan=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if(detik<60)
            {
                if (detik < 5)
                    hasil = "Baru saja";
                else
                    hasil = detik + " detik yang lalu";
            }
            else if(menit<60)
            {
                if (menit < 2)
                    hasil = "Satu menit yang lalu";
                else
                    hasil = menit + " menit yang lalu";
            }
            else if(jam<24)
            {
                if (jam < 2)
                    hasil = "Satu jam yang lalu";
                else
                    hasil = jam + " jam yang lalu";
            }
            else
            {
                if (hari == 1)
                    hasil = "Kemarin, " + KonversiWaktu(waktu);
                else if (hari < 7)
                    hasil = hari + " hari yang lalu, " + KonversiWaktu(waktu);
                else
                {
                    hasil = KonversiTanggal(waktu, true);
                }
            }
        }
        catch (ParseException e)
        {

        }

        return hasil;
    }

    public static String AmbilRandomTime()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        return df.format(cal.getTime());
    }

    public static String AmbilRandomTimeFullInt()
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return df.format(cal.getTime());
    }
}