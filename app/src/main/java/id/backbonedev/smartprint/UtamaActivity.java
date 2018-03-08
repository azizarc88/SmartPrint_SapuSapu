package id.backbonedev.smartprint;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeInfoDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.AwesomeProgressDialog;
import com.awesomedialog.blennersilva.awesomedialoglibrary.interfaces.Closure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import id.backbonedev.smartprint.App.BluetoothPrinter;
import id.backbonedev.smartprint.App.CoreApp;
import id.backbonedev.smartprint.App.KoneksiHandle;
import id.backbonedev.smartprint.App.TanggalHandler;
import id.backbonedev.smartprint.App.TentangItemAdapter;
import id.backbonedev.smartprint.Data.ItemTentang;

public class UtamaActivity extends AppCompatActivity
{
    private AHBottomNavigation navigation;
    private Button BCetak, BFeed, BCari, BUbahUsername;
    private TextView LVersi;
    private ListView LVTentang;
    private EditText TBKode, TBData, TBUsername, TBPassword, TBAdmin, TBLoket;
    private LinearLayout LLPengaturan, LLTentang, LLData, LLSekarang;
    private Spinner SPilihanPrinter, SPilihanTipe, SPilihanAdmin;

    private List<ItemTentang> itemTentangs = new ArrayList<>();
    private TentangItemAdapter tentangItemAdapter;
    private List<BluetoothDevice> pairedDevices = new ArrayList<>();
    private BluetoothDevice printer;
    private BluetoothPrinter mPrinter;
    private JSONObject terpilih;
    private String dataasli;
    private boolean done = false;

    private String TAG = "####UtamaActivity####";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utama);

        navigation = findViewById(R.id.navigation);
        BCetak = findViewById(R.id.BCetak);
        LLPengaturan = findViewById(R.id.LLPengaturan);
        LLTentang = findViewById(R.id.LLTentang);
        LLData = findViewById(R.id.LLData);
        SPilihanPrinter = findViewById(R.id.SPilihanPrinter);
        BFeed = findViewById(R.id.BFeed);
        SPilihanTipe = findViewById(R.id.SPilihanTipe);
        TBKode = findViewById(R.id.TBKode);
        BCari = findViewById(R.id.BCari);
        TBData = findViewById(R.id.TBData);
        SPilihanAdmin = findViewById(R.id.SAdmin);
        TBUsername = findViewById(R.id.TBUsername);
        TBPassword = findViewById(R.id.TBPassword);
        BUbahUsername = findViewById(R.id.BUbahUsername);
        TBAdmin = findViewById(R.id.TBAdmin);
        LVTentang = findViewById(R.id.LVTentang);
        LVersi = findViewById(R.id.LVersi);
        TBLoket = findViewById(R.id.TBLoket);

        LVersi.setText("Versi Bentukan: " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")");

        TBLoket.setText(CoreApp.getNamaloket());

        List<String> pilihantipe = new ArrayList<>();
        pilihantipe.add("PPLN");
        pilihantipe.add("TOKEN");
        pilihantipe.add("BPJS");
        pilihantipe.add("TELKOM");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, pilihantipe);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SPilihanTipe.setAdapter(adapter);

        itemTentangs.add(new ItemTentang(R.mipmap.icon_backbone, "Dibuat Oleh: ", "Backbone Technosmith", "http://www.backbonedev.id"));
        itemTentangs.add(new ItemTentang(R.mipmap.icon_bagikan, "Bagikan", "", "https://m.facebook.com/sharer.php?u=https%3A%2F%2Fplay.google.com%2Fstore%2Fapps%2Fdetails%3Fid%3D" + BuildConfig.APPLICATION_ID + "&sid=0&referrer=social_plugin&_rdr"));
        itemTentangs.add(new ItemTentang(R.mipmap.icon_like, "Sukai Kami", "", "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
        tentangItemAdapter = new TentangItemAdapter(UtamaActivity.this, itemTentangs);
        LVTentang.setAdapter(tentangItemAdapter);
        tentangItemAdapter.notifyDataSetChanged();

        LVTentang.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Uri uriurl = Uri.parse(((ItemTentang) tentangItemAdapter.getItem(i)).getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, uriurl);
                startActivity(intent);
            }
        });

        List<String> pilihanadmin = new ArrayList<>();
        pilihanadmin.add("1800");
        pilihanadmin.add("2000");
        pilihanadmin.add("2500");
        pilihanadmin.add("3000");
        pilihanadmin.add("Tentukan Sendiri");
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, pilihanadmin);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SPilihanAdmin.setAdapter(adapter2);

        MuatAlatBluetooth();

        LLPengaturan.setVisibility(View.GONE);
        LLTentang.setVisibility(View.GONE);
        LLData.setVisibility(View.GONE);

        int[] warna = {Color.parseColor("#71BF65"), Color.parseColor("#00B4B4"), Color.parseColor("#6498C7")};

        final AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.menu_utama);
        navigationAdapter.setupWithBottomNavigation(navigation, warna);

        navigation.setAccentColor(Color.parseColor("#FFFFFF"));
        navigation.setInactiveColor(Color.parseColor("#B7DEB0"));
        navigation.setDefaultBackgroundColor(Color.parseColor("#71BF65"));
        navigation.setBehaviorTranslationEnabled(true);
        navigation.setTranslucentNavigationEnabled(true);
        navigation.setUseElevation(true);
        navigation.setColored(true);
        navigation.setTitleTextSizeInSp(14f, 10f);
        navigation.setNotificationAnimationDuration(500);

        navigation.setCurrentItem(1, true);
        LLData.setVisibility(View.VISIBLE);
        LLSekarang = LLData;

        SPilihanAdmin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (i == 4)
                    TBAdmin.setVisibility(View.VISIBLE);
                else
                    TBAdmin.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            }
        });

        TBLoket.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                CoreApp.setNamaloket(charSequence.toString());
                CoreApp.SimpanPengaturan();
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        BUbahUsername.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (TBUsername.getText().toString().equals(CoreApp.getUsername()) && TBPassword.getText().toString().equals(CoreApp.getPassword()))
                {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(UtamaActivity.this);
                    alertDialog.setTitle("Ubah Username / Password");
                    alertDialog.setMessage("Silakan masukkan username dan password baru Anda");

                    final EditText TBUsernamem = new EditText(UtamaActivity.this);
                    final EditText TBPasswordm = new EditText(UtamaActivity.this);
                    final LinearLayout LL = new LinearLayout(UtamaActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams lpl = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    LL.setOrientation(LinearLayout.VERTICAL);
                    TBUsernamem.setLayoutParams(lp);
                    TBPasswordm.setLayoutParams(lp2);
                    LL.setLayoutParams(lpl);
                    TBUsernamem.setHint("Username Baru");
                    TBPasswordm.setHint("Password Baru");

                    LL.addView(TBUsernamem);
                    LL.addView(TBPasswordm);
                    alertDialog.setView(LL);

                    alertDialog.setPositiveButton("Ubah", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            String username, password;
                            username = TBUsernamem.getText().toString();
                            password = TBPasswordm.getText().toString();

                            if (username.length() < 8)
                            {
                                Toast.makeText(getApplicationContext(), "Username minimal memiliki 8 karakter", Toast.LENGTH_LONG).show();
                                dialogInterface.cancel();
                            }
                            else if (password.length() < 8)
                                Toast.makeText(getApplicationContext(), "Password minimal memiliki 8 karakter", Toast.LENGTH_LONG).show();
                            else
                            {
                                CoreApp.setUsername(username);
                                CoreApp.setPassword(password);
                                CoreApp.SimpanPengaturan();

                                TBUsername.setText(username);
                                TBPassword.setText(password);
                                Toast.makeText(getApplicationContext(), "Perubahan username dan password berhasil", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    alertDialog.setNegativeButton("Batal", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.dismiss();
                        }
                    });

                    alertDialog.show();
                }
                else
                {
                    CoreApp.Error(UtamaActivity.this, "Smart Print", "Username atau password yang Anda masukkan salah.", null);
                }
            }
        });

        navigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener()
        {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected)
            {
                if (position == 0)
                {
                    if (LLSekarang != LLPengaturan)
                    {
                        LLPengaturan.setVisibility(View.VISIBLE);
                        LLSekarang.setVisibility(View.GONE);
                        LLSekarang = LLPengaturan;

                        if (pairedDevices.size() == 0)
                            MuatAlatBluetooth();
                    }
                }
                else if (position == 1)
                {
                    if (LLSekarang != LLData)
                    {
                        LLData.setVisibility(View.VISIBLE);
                        LLSekarang.setVisibility(View.GONE);
                        LLSekarang = LLData;
                    }
                }
                else if (position == 2)
                {
                    if (LLSekarang != LLTentang)
                    {
                        LLTentang.setVisibility(View.VISIBLE);
                        LLSekarang.setVisibility(View.GONE);
                        LLSekarang = LLTentang;
                    }
                }
                return true;
            }
        });

        TBData.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                if (done)
                {
                    done = false;
                    TBData.setText(dataasli);
                    done = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        BFeed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!mPrinter.isConnected())
                {
                    KoneksiPrinter(new BluetoothPrinter.PrinterConnectListener()
                    {
                        @Override
                        public void onConnected()
                        {
                            mPrinter.feedPaper();
                        }

                        @Override
                        public void onFailed()
                        {
                            Toast.makeText(getApplicationContext(), "Pastikan perangkat printer sudah menyala", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else
                {
                    mPrinter.feedPaper();
                }
            }
        });

        BCetak.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mPrinter == null)
                {
                    CoreApp.Error(UtamaActivity.this, "Smart Print", "Sepertinya Anda belum mengatur printer yang akan digunakan Aplikasi", new Closure()
                    {
                        @Override
                        public void exec()
                        {
                            navigation.setCurrentItem(0, true);
                        }
                    });
                }
                else
                {
                    if (!mPrinter.isConnected())
                    {
                        KoneksiPrinter(new BluetoothPrinter.PrinterConnectListener()
                        {
                            @Override
                            public void onConnected()
                            {
                                CetakData();
                            }

                            @Override
                            public void onFailed()
                            {
                                Toast.makeText(getApplicationContext(), "Pastikan perangkat printer sudah menyala", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else
                    {
                        CetakData();
                    }
                }
            }
        });

        SPilihanPrinter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                printer = pairedDevices.get(i);
                CoreApp.setAlamatprinterbt(printer.getAddress());
                CoreApp.SimpanPengaturan();

                mPrinter = new BluetoothPrinter(printer);
                KoneksiPrinter(new BluetoothPrinter.PrinterConnectListener()
                {
                    @Override
                    public void onConnected()
                    {
                    }

                    @Override
                    public void onFailed()
                    {
                        Toast.makeText(getApplicationContext(), "Pastikan perangkat printer sudah menyala", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
                Toast.makeText(getApplicationContext(), "Anda harus memilih perangkat untuk dapat mencetak struk", Toast.LENGTH_LONG).show();
            }
        });

        TBKode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent wevent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(TBKode.getWindowToken(), 0);
                    BCari.performClick();
                    return true;
                }
                return false;
            }
        });

        BCari.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(TBKode.getWindowToken(), 0);

                if (TextUtils.isEmpty(TBKode.getText()))
                {
                    CoreApp.Error(UtamaActivity.this, "Smart Print", "Anda harus memasukkan kode pelanggan", null);
                }
                else if (SPilihanAdmin.getSelectedItemPosition() == 4 && TextUtils.isEmpty(TBAdmin.getText()))
                {
                    CoreApp.Error(UtamaActivity.this, "Smart Print", "Anda harus memasukkan jumlah harga admin", null);
                }
                else if (SPilihanAdmin.getSelectedItemPosition() == 4 && SPilihanTipe.getSelectedItemPosition() != 2 && (Integer.valueOf(TBAdmin.getText().toString()) > 5000 || Integer.valueOf(TBAdmin.getText().toString()) < 1800))
                {
                    CoreApp.Error(UtamaActivity.this, "Smart Print", "Nilai harga Admin tidak dapat melebihi Rp. 5.000 atau dibawah Rp. 1.800", null);
                }
                else if (SPilihanAdmin.getSelectedItemPosition() == 4 && (SPilihanTipe.getSelectedItemPosition() == 2 || SPilihanTipe.getSelectedItemPosition() == 3) && (Integer.valueOf(TBAdmin.getText().toString()) > 5000 || Integer.valueOf(TBAdmin.getText().toString()) < 2500))
                {
                    CoreApp.Error(UtamaActivity.this, "Smart Print", "Nilai harga Admin tidak dapat melebihi Rp. 5.000 atau dibawah Rp. 2.500", null);
                }
                else
                {
                    AmbilData();
                }
            }
        });
    }

    private void CetakData()
    {
        if (TextUtils.isEmpty(CoreApp.getNamaloket()))
        {
            CoreApp.Peringatan(UtamaActivity.this, "Smart Print", "Anda sepertinya belum mengatur Nama Loket", new Closure()
            {
                @Override
                public void exec()
                {
                    navigation.setCurrentItem(0, true);
                }
            });
        }
        else
        {
            try
            {
                String pesan = terpilih.getString("pesan");

                mPrinter.setAlign(BluetoothPrinter.ALIGN_CENTER);
                mPrinter.printText(CoreApp.getNamaperusahaan().toUpperCase());
                mPrinter.addNewLine();
                mPrinter.printText("LOKET:");
                mPrinter.addNewLine();
                mPrinter.printText(CoreApp.getNamaloket().toUpperCase());
                mPrinter.addNewLine();
                mPrinter.setAlign(BluetoothPrinter.ALIGN_LEFT);
                mPrinter.addNewLine();
                mPrinter.printText("Tgl. Bayar : " + terpilih.getString("ins_date"));
                mPrinter.addNewLine();
                mPrinter.printText("Id. Pel.   : " + terpilih.getString("id_pelanggan"));
                mPrinter.addNewLine();
                mPrinter.printText("Nama       : " + terpilih.getString("nama"));
                mPrinter.addNewLine();
                mPrinter.printText("================================");
                mPrinter.addNewLine();
                if (!TextUtils.isEmpty(terpilih.getString("tarif")))
                {
                    mPrinter.printText("Tarif/Daya : " + terpilih.getString("tarif"));
                    mPrinter.addNewLine();
                }

                if (SPilihanTipe.getSelectedItem().toString().equalsIgnoreCase("PPLN"))
                {
                    mPrinter.printText("Bulan/Tahun: " + terpilih.getString("bulan"));
                    mPrinter.addNewLine();
                    mPrinter.printText("Stand Meter: " + pesan.substring(pesan.lastIndexOf(" METER ") + 7, pesan.lastIndexOf(" METER ") + 26));
                    mPrinter.setAlign(BluetoothPrinter.ALIGN_LEFT);
                    mPrinter.addNewLine();
                    mPrinter.printText("Ref. : ");
                    mPrinter.addNewLine();
                    mPrinter.setAlign(BluetoothPrinter.ALIGN_CENTER);
                    mPrinter.printText(pesan.substring(pesan.lastIndexOf(" REF ") + 5, pesan.lastIndexOf(" REF ") + 34));
                    mPrinter.addNewLine();
                    mPrinter.setAlign(BluetoothPrinter.ALIGN_LEFT);
                }
                else if (SPilihanTipe.getSelectedItem().toString().equalsIgnoreCase("TOKEN"))
                {
                    mPrinter.printText("Jumlah Kwh : " + terpilih.getString("jmlkwh"));
                    mPrinter.addNewLine();
                }

                int harga = Integer.valueOf(pesan.substring(pesan.lastIndexOf(" HARGA ") + 7, pesan.lastIndexOf("_ Saldo")));

                mPrinter.printText("================================");
                mPrinter.addNewLine();
                mPrinter.printText("Tagihan    : Rp. " + String.valueOf(harga));
                mPrinter.addNewLine();

                if (SPilihanAdmin.getSelectedItemPosition() == 4)
                {
                    mPrinter.printText("Admin      : Rp. " + TBAdmin.getText().toString());
                    mPrinter.addNewLine();
                    mPrinter.printText("Total Bayar: Rp. " + (harga + Integer.valueOf(TBAdmin.getText().toString())));
                    mPrinter.addNewLine();
                }
                else
                {

                    mPrinter.printText("Admin      : Rp. " + SPilihanAdmin.getSelectedItem().toString());
                    mPrinter.addNewLine();
                    mPrinter.printText("Total Bayar: Rp. " + (harga + Integer.valueOf(SPilihanAdmin.getSelectedItem().toString())));
                    mPrinter.addNewLine();
                }

                mPrinter.printText("================================");
                mPrinter.addNewLine();
                mPrinter.addNewLine();

                mPrinter.setAlign(BluetoothPrinter.ALIGN_CENTER);

                if (SPilihanTipe.getSelectedItem().toString().equalsIgnoreCase("TOKEN"))
                {
                    mPrinter.printText("TOKEN : ");
                    mPrinter.addNewLine();
                    mPrinter.printText(terpilih.getString("sn_token"));
                    mPrinter.addNewLine();
                    mPrinter.addNewLine();
                }

                mPrinter.printText("TERIMA KASIH");
                mPrinter.addNewLine();
                mPrinter.printText("Struk ini merupakan bukti");
                mPrinter.addNewLine();
                mPrinter.printText("pembayaran yang sah");
                mPrinter.addNewLine();
                mPrinter.printText("Informasi PLN Hubungi 123");
                mPrinter.addNewLine();
                mPrinter.printText("www.pln.co.id");
                mPrinter.addNewLine();
                mPrinter.addNewLine();
                mPrinter.addNewLine();
                mPrinter.addNewLine();
            }
            catch (JSONException E)
            {
            }
        }
    }

    private void KoneksiPrinter(BluetoothPrinter.PrinterConnectListener listener)
    {
        mPrinter.finish();
        mPrinter.connectPrinter(listener);
    }

    private void MuatAlatBluetooth()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            CoreApp.Error(UtamaActivity.this, "Smart Print", "Kami tidak menemukan Bluetooth di perangkat Anda", new Closure()
            {
                @Override
                public void exec()
                {
                    finish();
                }
            });
        }
        else
        {
            if (mBluetoothAdapter.isEnabled())
            {
                try
                {
                    Set<BluetoothDevice> dev = mBluetoothAdapter.getBondedDevices();

                    List<String> s = new ArrayList<String>();
                    for(BluetoothDevice bt : dev)
                    {
                        pairedDevices.add(bt);
                        s.add(bt.getName() + " - " + TipeBluetooth(bt.getBluetoothClass().getMajorDeviceClass()));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                            android.R.layout.simple_spinner_item, s);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    SPilihanPrinter.setAdapter(adapter);

                    int a = 0;
                    for (BluetoothDevice b : pairedDevices)
                    {
                        if (b.getAddress().equalsIgnoreCase(CoreApp.getAlamatprinterbt()))
                        {
                            printer = b;
                            mPrinter = new BluetoothPrinter(printer);
                            SPilihanPrinter.setSelection(a);
                            KoneksiPrinter(new BluetoothPrinter.PrinterConnectListener()
                            {
                                @Override
                                public void onConnected()
                                {

                                }

                                @Override
                                public void onFailed()
                                {

                                }
                            });
                        }
                        a++;
                    }
                }
                catch (Exception e)
                {
                    CoreApp.Error(UtamaActivity.this, "Smart Print", "Anda harus menyalakan Bluetooth terlebih dahulu.", new Closure()
                    {
                        @Override
                        public void exec()
                        {
                            ComponentName cn = new ComponentName("com.android.settings",
                                    "com.android.settings.bluetooth.BluetoothSettings");
                        }
                    });
                }
            }
            else
            {
                CoreApp.Error(UtamaActivity.this, "Smart Print", "Anda harus menyalakan Bluetooth terlebih dahulu.", new Closure()
                {
                    @Override
                    public void exec()
                    {
                        ComponentName cn = new ComponentName("com.android.settings",
                                "com.android.settings.bluetooth.BluetoothSettings");
                    }
                });
            }
        }
    }

    private String TipeBluetooth(int major)
    {
        switch (major) {
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                return "Audio / Video";
            case BluetoothClass.Device.Major.COMPUTER:
                return "Komputer";
            case BluetoothClass.Device.Major.HEALTH:
                return "Kesehatan";
            case BluetoothClass.Device.Major.IMAGING:
                return "Imaging / Printer";
            case BluetoothClass.Device.Major.MISC:
                return "Lainlain";
            case BluetoothClass.Device.Major.NETWORKING:
                return "Jaringan";
            case BluetoothClass.Device.Major.PERIPHERAL:
                return "Peralatan";
            case BluetoothClass.Device.Major.PHONE:
                return "Telephone";
            case BluetoothClass.Device.Major.TOY:
                return "Mainan";
            case BluetoothClass.Device.Major.UNCATEGORIZED:
                return "Tak diketahui";
            case BluetoothClass.Device.Major.WEARABLE:
                return "Wearable";
            default:
                return "Tak diketahui";
        }
    }

    private void AmbilData()
    {
        final AwesomeProgressDialog info = new AwesomeProgressDialog(this)
                .setTitle(R.string.app_name)
                .setMessage("Mengambil Data")
                .setColoredCircle(R.color.dialogInfoBackgroundColor)
                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
                .setCancelable(true);

        info.show();

        List<String[]> data = new ArrayList<String[]>();
        data.add(new String[] {"kodepelanggan", TBKode.getText().toString()});
        data.add(new String[] {"tipe", SPilihanTipe.getSelectedItem().toString()});

        new KoneksiHandle().AmbilObjekCryptArray(UtamaActivity.this, false, data, CoreApp.link_ambil_data, new KoneksiHandle.OnResponArraySukses()
        {
            @Override
            public void onPanggil(final JSONArray array, JSONObject respon)
            {
                try
                {
                    if (array.length() > 1)
                    {
                        info.hide();
                        AlertDialog.Builder b = new AlertDialog.Builder(UtamaActivity.this);
                        b.setTitle("Ditemukan lebih dari satu, Silakan pilih data");
                        String[] types = new String[array.length()];
                        for (int i = 0; i < array.length(); i++)
                        {
                            JSONObject data = (JSONObject) array.get(i);
                            types[i] = TanggalHandler.KonversiTanggal(data.getString("ins_date"), true) + " - Rp. " + data.getString("tagihan");
                        }
                        b.setItems(types, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                try
                                {
                                    terpilih = array.getJSONObject(i);
                                    TampilkanData();
                                    info.hide();
                                }
                                catch (JSONException e)
                                {
                                }
                            }
                        });
                        b.show();
                    }
                    else if (array.length() == 1)
                    {
                        terpilih = array.getJSONObject(0);
                        TampilkanData();
                        info.hide();
                    }
                    else
                    {
                        info.hide();
                        CoreApp.Peringatan(UtamaActivity.this, "Smart Print", "Tidak ditemukan data dengan kode dan tipe pembayaran tersebut", null);
                    }
                } catch (JSONException e)
                {
                }
            }
        }, new KoneksiHandle.OnResponArrayGagal()
        {
            @Override
            public void onPanggil(JSONArray array, JSONObject respon)
            {
                info.hide();
                /*CoreApp.Peringatan(UtamaActivity.this, "Smart Print", "Gagal mengambil data", null);*/
            }
        });
    }

    private void TampilkanData()
    {
        done = false;
        String data = "EROR ";
        TBData.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        try
        {
            String pesan = terpilih.getString("pesan");

            data = "Tgl. Bayar \t: " + terpilih.getString("ins_date") + "\n";
            data += "Id. Pel.   \t: " + terpilih.getString("id_pelanggan") + "\n";
            data += "Nama       \t: " + terpilih.getString("nama") + "\n";
            if (!TextUtils.isEmpty(terpilih.getString("tarif")))
            {
                data += "Tarif/Daya \t: " + terpilih.getString("tarif") + "\n";
            }

            if (SPilihanTipe.getSelectedItem().toString().equalsIgnoreCase("PPLN"))
            {
                data += "Bulan/Tahun\t: " + terpilih.getString("bulan") + "\n";
                data += "Stand Meter\t: " + pesan.substring(pesan.lastIndexOf(" METER ") + 7, pesan.lastIndexOf(" METER ") + 26) + "\n";
            }
            else if (SPilihanTipe.getSelectedItem().toString().equalsIgnoreCase("TOKEN"))
            {
                data += "Jumlah Kwh \t: " + terpilih.getString("jmlkwh") + "\n";
            }

            int harga = Integer.valueOf(pesan.substring(pesan.lastIndexOf(" HARGA ") + 7, pesan.lastIndexOf("_ Saldo")));

            data += "Tagihan    \t: Rp. " + String.valueOf(harga) + "\n";
            if (SPilihanAdmin.getSelectedItemPosition() == 4)
            {
                data += "Admin      \t: Rp. " + TBAdmin.getText().toString() + "\n";
                data += "Total Bayar\t: Rp. " + (harga + Integer.valueOf(TBAdmin.getText().toString())) + "\n\n";
            }
            else
            {
                data += "Admin      \t: Rp. " + SPilihanAdmin.getSelectedItem().toString() + "\n";
                data += "Total Bayar\t: Rp. " + (harga + Integer.valueOf(SPilihanAdmin.getSelectedItem().toString())) + "\n\n";
            }

            if (SPilihanTipe.getSelectedItem().toString().equalsIgnoreCase("TOKEN"))
            {
                data += "TOKEN : " + terpilih.getString("sn_token");
            }

            data += "Ref : " + pesan.substring(pesan.lastIndexOf(" REF ") + 5, pesan.lastIndexOf(" REF ") + 34) + "\n";

            BCetak.setEnabled(true);
        }
        catch (JSONException E)
        {
            try
            {
                TBData.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                data = "Bukan data yang valid\n\nPesan: " + terpilih.getString("pesan");
                data += "\n" + E.getMessage().toString();
                BCetak.setEnabled(false);
            }
            catch (JSONException e)
            {
            }
        }

        dataasli = data;
        TBData.setText(data);
        done = true;
    }
}
