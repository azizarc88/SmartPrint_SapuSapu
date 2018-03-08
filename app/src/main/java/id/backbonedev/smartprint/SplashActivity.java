package id.backbonedev.smartprint;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import id.backbonedev.smartprint.App.CoreApp;

public class SplashActivity extends AppCompatActivity
{
    public CoreApp coreApp;
    private FirebaseRemoteConfig firebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        coreApp = new CoreApp(getApplicationContext());

        CoreApp.setNamaperusahaan("SAPU SAPU TRONIK");
        CoreApp.SimpanPengaturan();

        if (CoreApp.getUsername().equalsIgnoreCase("tidak ada"))
        {
            CoreApp.setUsername("smartprint");
            CoreApp.setPassword("jog991hum1");
            CoreApp.SimpanPengaturan();
        }

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build();
        firebaseRemoteConfig.setConfigSettings(configSettings);
        firebaseRemoteConfig.setDefaults(R.xml.pengaturan_default);
        firebaseRemoteConfig.fetch(0).addOnCompleteListener(SplashActivity.this, new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if ((task.isComplete()))
                {
                    firebaseRemoteConfig.activateFetched();
                    CoreApp.setPengaturanserver(firebaseRemoteConfig.getString("alamatserver"));
                    coreApp.SimpanPengaturan();
                    firebaseRemoteConfig.activateFetched();
                }
                else
                {
                }
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                Intent intent = new Intent(SplashActivity.this, UtamaActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
        super.onResume();
    }
}
