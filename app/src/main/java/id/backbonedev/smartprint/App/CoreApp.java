package id.backbonedev.smartprint.App;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeErrorDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeNoticeDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeSuccessDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import id.backbonedev.smartprint.BuildConfig;
import id.backbonedev.smartprint.R;

/**
 * Created by Aziz Nur Ariffianto on 0002, 02 Jan 2018.
 */

public class CoreApp
{
    private static Context cc;
    private static SharedPreferences sharedPref;
    private static String alamatprinterbt, username, password, pengaturanserver, namaperusahaan, namaloket;
    public static boolean isdebug = true;

    public static String server;
    public static String link_ambil_data = "ambil_data.php";

    public static String getNamaperusahaan()
    {
        return namaperusahaan;
    }

    public static void setNamaperusahaan(String namaperusahaan)
    {
        CoreApp.namaperusahaan = namaperusahaan;
    }

    public static String getNamaloket()
    {
        return namaloket;
    }

    public static void setNamaloket(String namaloket)
    {
        CoreApp.namaloket = namaloket;
    }

    public static String getPengaturanserver()
    {
        return pengaturanserver;
    }

    public static void setPengaturanserver(String pengaturanserver)
    {
        server = pengaturanserver;
        CoreApp.pengaturanserver = pengaturanserver;
    }

    public static String getUsername()
    {
        return username;
    }

    public static void setUsername(String username)
    {
        CoreApp.username = username;
    }

    public static String getPassword()
    {
        return password;
    }

    public static void setPassword(String password)
    {
        CoreApp.password = password;
    }

    public static String getAlamatprinterbt()
    {
        return alamatprinterbt;
    }

    public static void setAlamatprinterbt(String alamatprinterbt)
    {
        CoreApp.alamatprinterbt = alamatprinterbt;
    }

    public CoreApp(Context cc)
    {
        this.cc = cc;
        sharedPref = cc.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        BacaPengaturan();
    }

    public static void BacaPengaturan()
    {
        alamatprinterbt = sharedPref.getString("alamatprinterbt", "Tidak Ada");
        pengaturanserver = sharedPref.getString("server", "Tidak Ada");
        namaperusahaan = sharedPref.getString("namaperusahaan", "");
        namaloket = sharedPref.getString("namaloket", "");
        server = Crypt.Decrypt(sharedPref.getString("3", Crypt.Encrypt("tidak ada")));
        username = Crypt.Decrypt(sharedPref.getString("1", Crypt.Encrypt("tidak ada")));
        password = Crypt.Decrypt(sharedPref.getString("2", Crypt.Encrypt("tidak ada")));
    }

    public static String BacaPengaturan(String namaval)
    {
        return sharedPref.getString(namaval, "Tidak Ada");
    }

    public static void SimpanPengaturan(String namaval, String value)
    {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(namaval, value);
        editor.commit();
    }

    public static void SimpanPengaturan(String namaval, int value)
    {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(namaval, value);
        editor.commit();
    }

    public static void SimpanPengaturan()
    {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("alamatprinterbt", alamatprinterbt);
        editor.putString("3", Crypt.Encrypt(pengaturanserver));
        editor.putString("namaperusahaan", namaperusahaan);
        editor.putString("namaloket", namaloket);
        editor.putString("1", Crypt.Encrypt(username));
        editor.putString("2", Crypt.Encrypt(password));
        editor.commit();
    }

    public static void Info(Activity context, String judul, String pesan, @Nullable Closure listener)
    {
        if (!context.isFinishing() && context != null)
        {
            new AwesomeNoticeDialog(context)
                    .setTitle(judul)
                    .setMessage(pesan)
                    .setColoredCircle(R.color.colorAccent)
                    .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                    .setCancelable(true)
                    .setButtonText("Oke")
                    .setButtonBackgroundColor(R.color.colorAccent)
                    .setNoticeButtonClick(listener)
                    .show();
        }
    }

    public static void Peringatan(Activity context, String judul, String pesan, @Nullable Closure listener)
    {
        if (!context.isFinishing() && context != null)
        {
            new AwesomeErrorDialog(context)
                    .setTitle(judul)
                    .setMessage(pesan)
                    .setColoredCircle(R.color.dialogWarningBackgroundColor)
                    .setDialogIconAndColor(R.drawable.ic_dialog_warning, R.color.white)
                    .setCancelable(true)
                    .setButtonText("OKE")
                    .setButtonBackgroundColor(R.color.dialogWarningBackgroundColor)
                    .setErrorButtonClick(listener)
                    .show();
        }
    }

    public static void Error(Context context, String judul, String pesan, @Nullable Closure listener)
    {
        if (context != null)
        {
            new AwesomeErrorDialog(context)
                    .setTitle(judul)
                    .setMessage(pesan)
                    .setColoredCircle(R.color.dialogErrorBackgroundColor)
                    .setDialogIconAndColor(R.drawable.ic_dialog_error, R.color.white)
                    .setCancelable(true)
                    .setButtonText("OKE")
                    .setButtonBackgroundColor(R.color.dialogErrorBackgroundColor)
                    .setErrorButtonClick(listener)
                    .show();
        }
    }

    public static void PesanYesNo(Activity context, String judul, String pesan, @Nullable Closure onya, @Nullable Closure ontidak)
    {
        if (!context.isFinishing() && context != null)
        {
            new AwesomeInfoDialog(context)
                    .setTitle(judul)
                    .setMessage(pesan)
                    .setColoredCircle(R.color.colorAccent)
                    .setDialogIconAndColor(R.drawable.icon_tanya, R.color.white)
                    .setCancelable(true)
                    .setPositiveButtonText("Ya")
                    .setPositiveButtonbackgroundColor(R.color.colorAccent)
                    .setPositiveButtonTextColor(R.color.white)
                    .setNegativeButtonText("Tidak")
                    .setNegativeButtonbackgroundColor(R.color.colorGray)
                    .setNegativeButtonTextColor(R.color.white)
                    .setPositiveButtonClick(onya)
                    .setNegativeButtonClick(ontidak)
                    .show();
        }
    }

    public static void PesanYesNo(Activity context, String judul, String pesan, String teksonya, @Nullable Closure onya, String teksontidak, @Nullable Closure ontidak)
    {
        if (!context.isFinishing() && context != null)
        {
            new AwesomeInfoDialog(context)
                    .setTitle(judul)
                    .setMessage(pesan)
                    .setColoredCircle(R.color.colorAccent)
                    .setDialogIconAndColor(R.drawable.icon_tanya, R.color.white)
                    .setCancelable(true)
                    .setPositiveButtonText(teksonya)
                    .setPositiveButtonbackgroundColor(R.color.colorAccent)
                    .setPositiveButtonTextColor(R.color.white)
                    .setNegativeButtonText(teksontidak)
                    .setNegativeButtonbackgroundColor(R.color.colorGray)
                    .setNegativeButtonTextColor(R.color.colorWhite)
                    .setPositiveButtonClick(onya)
                    .setNegativeButtonClick(ontidak)
                    .show();
        }
    }

    public static void PesanYesNoOpsi(Activity context, String judul, String pesan, String teksonya, @Nullable Closure onya, String teksontidak, @Nullable Closure ontidak)
    {
        if (!context.isFinishing() && context != null)
        {
            new AwesomeInfoDialog(context)
                    .setTitle(judul)
                    .setMessage(pesan)
                    .setColoredCircle(R.color.colorAccent)
                    .setDialogIconAndColor(R.drawable.icon_tanya, R.color.white)
                    .setCancelable(true)
                    .setPositiveButtonText(teksonya)
                    .setPositiveButtonbackgroundColor(R.color.colorAccent)
                    .setPositiveButtonTextColor(R.color.white)
                    .setNegativeButtonText(teksontidak)
                    .setNegativeButtonbackgroundColor(R.color.colorAccent)
                    .setNegativeButtonTextColor(R.color.white)
                    .setPositiveButtonClick(onya)
                    .setNegativeButtonClick(ontidak)
                    .show();
        }
    }

    public static void PesanSukses(Activity context, String judul, String pesan, @Nullable Closure onya)
    {
        if (!context.isFinishing() && context != null)
        {
            new AwesomeSuccessDialog(context)
                    .setTitle(judul)
                    .setMessage(pesan)
                    .setColoredCircle(R.color.dialogSuccessBackgroundColor)
                    .setDialogIconAndColor(R.drawable.ic_success, R.color.white)
                    .setCancelable(true)
                    .setPositiveButtonText("OKE")
                    .setPositiveButtonbackgroundColor(R.color.dialogSuccessBackgroundColor)
                    .setPositiveButtonTextColor(R.color.white)
                    .setPositiveButtonClick(onya)
                    .show();
        }
    }

    public static void PesanSuksesYesNo(Activity context, String judul, String pesan, String teksonya, @Nullable Closure onya, String teksontidak, @Nullable Closure ontidak)
    {
        if (!context.isFinishing() && context != null)
        {
            new AwesomeSuccessDialog(context)
                    .setTitle(judul)
                    .setMessage(pesan)
                    .setColoredCircle(R.color.dialogSuccessBackgroundColor)
                    .setDialogIconAndColor(R.drawable.ic_success, R.color.white)
                    .setCancelable(true)
                    .setPositiveButtonText(teksonya)
                    .setPositiveButtonbackgroundColor(R.color.dialogSuccessBackgroundColor)
                    .setPositiveButtonTextColor(R.color.white)
                    .setNegativeButtonText(teksontidak)
                    .setNegativeButtonbackgroundColor(R.color.colorGray)
                    .setNegativeButtonTextColor(R.color.colorWhite)
                    .setPositiveButtonClick(onya)
                    .setNegativeButtonClick(ontidak)
                    .show();
        }
    }

    public static void PesanYesNoNetral(Activity context, String judul, String pesan, String netral, Closure onya, Closure ontidak, Closure onnetral)
    {
        if (!context.isFinishing() && context != null)
        {
            new AwesomeInfoDialog(context)
                    .setTitle(judul)
                    .setMessage(pesan)
                    .setColoredCircle(R.color.colorAccent)
                    .setDialogIconAndColor(R.drawable.icon_tanya, R.color.white)
                    .setCancelable(false)
                    .setPositiveButtonText("Iya")
                    .setPositiveButtonbackgroundColor(R.color.colorAccent)
                    .setPositiveButtonTextColor(R.color.white)
                    .setNeutralButtonText("Tidak")
                    .setNeutralButtonbackgroundColor(R.color.colorAccent)
                    .setNeutralButtonTextColor(R.color.white)
                    .setNegativeButtonText(netral)
                    .setNegativeButtonbackgroundColor(R.color.colorGray)
                    .setNegativeButtonTextColor(R.color.colorWhite)
                    .setPositiveButtonClick(onya)
                    .setNegativeButtonClick(onnetral)
                    .setNeutralButtonClick(ontidak)
                    .show();
        }
    }

    public static void PesanYesNoNetralOpsi(Activity context, String judul, String pesan, String sonya, Closure onya, String sontidak, Closure ontidak, String sonnetral, Closure onnetral)
    {
        if (!context.isFinishing() && context != null)
        {
            new AwesomeInfoDialog(context)
                    .setTitle(judul)
                    .setMessage(pesan)
                    .setColoredCircle(R.color.colorAccent)
                    .setDialogIconAndColor(R.drawable.icon_tanya, R.color.white)
                    .setCancelable(false)
                    .setPositiveButtonText(sonya)
                    .setPositiveButtonbackgroundColor(R.color.colorAccent)
                    .setPositiveButtonTextColor(R.color.white)
                    .setNeutralButtonText(sontidak)
                    .setNeutralButtonbackgroundColor(R.color.colorAccent)
                    .setNeutralButtonTextColor(R.color.white)
                    .setNegativeButtonText(sonnetral)
                    .setNegativeButtonbackgroundColor(R.color.colorAccent)
                    .setNegativeButtonTextColor(R.color.white)
                    .setPositiveButtonClick(onya)
                    .setNegativeButtonClick(onnetral)
                    .setNeutralButtonClick(ontidak)
                    .show();
        }
    }

    public static void PesanYesNoNetral(Activity context, String judul, String pesan, String sonya, Closure onya, String sontidak, Closure ontidak, String sonnetral, Closure onnetral, boolean bolehhilang)
    {
        if (!context.isFinishing() && context != null)
        {
            new AwesomeInfoDialog(context)
                    .setTitle(judul)
                    .setMessage(pesan)
                    .setColoredCircle(R.color.colorAccent)
                    .setDialogIconAndColor(R.drawable.icon_tanya, R.color.white)
                    .setCancelable(bolehhilang)
                    .setPositiveButtonText(sonya)
                    .setPositiveButtonbackgroundColor(R.color.colorAccent)
                    .setPositiveButtonTextColor(R.color.white)
                    .setNeutralButtonText(sontidak)
                    .setNeutralButtonbackgroundColor(R.color.colorAccent)
                    .setNeutralButtonTextColor(R.color.white)
                    .setNegativeButtonText(sonnetral)
                    .setNegativeButtonbackgroundColor(R.color.colorAccent)
                    .setNegativeButtonTextColor(R.color.white)
                    .setPositiveButtonClick(onya)
                    .setNegativeButtonClick(onnetral)
                    .setNeutralButtonClick(ontidak)
                    .show();
        }
    }
}
