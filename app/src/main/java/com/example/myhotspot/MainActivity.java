package com.example.myhotspot;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    EditText code;
    Button sub;
    WifiManager wifiManager;
    WifiConfiguration wifiConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        code=findViewById(R.id.code);
        sub=findViewById(R.id.submit);
        boolean settingsCanWrite = Settings.System.canWrite(MainActivity.this);

        if(!settingsCanWrite) {
            // If do not have write settings permission then open the Can modify system settings panel.
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Access Permission");
            builder.setMessage("You should give system permission to access this")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getApplicationContext(), "You can't proceed further", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
                    });
            AlertDialog alertdialog = builder.create();
            alertdialog.show();

        }

    }
    public static boolean setHotspotName(String newName, Context context) {
        try {

            WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
            assert wifiManager != null;

            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");

            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);

            assert wifiConfig != null;
            wifiConfig.SSID = newName;

            Method setConfigMethod = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            setConfigMethod.invoke(wifiManager, wifiConfig);

            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }
    public void turnOn()  {
    WifiManager    wifi_manager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        WifiConfiguration wifi_configuration = null;
        assert wifi_manager != null;
        wifi_manager.setWifiEnabled(false);

        try
        {
            //USE REFLECTION TO GET METHOD "SetWifiAPEnabled"
            Method method=wifi_manager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifi_manager, wifi_configuration, true);
        }
       catch (NoSuchMethodException e) {
           Toast.makeText(this,"No Method Exception",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        catch (InvocationTargetException e){
            Toast.makeText(this,"Invocation Exception",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            // TODO Auto-generated catch block

          Toast.makeText(this,"Exception",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    public void Submit(View view) throws InvocationTargetException, IllegalAccessException {
        String text=code.getText().toString();
        if(text.isEmpty()){
            Toast.makeText(this,"Please enter your code",Toast.LENGTH_LONG).show();
        }
        else{
                if(  setHotspotName(text,getApplicationContext())){
                   //
                    if(isApOn(MainActivity.this)) {
                        Log.d("DOne","Done"); AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Success Window");
                        builder.setMessage("SSID has been changed successfully");
                        AlertDialog alertdialog = builder.create();
                        alertdialog.show();

                    }
                    else{
                      turnOn();
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Success Window");
                        builder.setMessage("SSID has been changed successfully");
                        AlertDialog alertdialog = builder.create();
                        alertdialog.show();

                    }

                }
                else{
                    Toast.makeText(this,"Invalid SSID",Toast.LENGTH_LONG).show();
                }

        }
    }
}
