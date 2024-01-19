package com.example.smart_home_controller_pine64;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.RadioGroup;

import com.example.smart_home_controller_pine64.Utill.DisconnectedException;
import com.example.smart_home_controller_pine64.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    final String ledStatusUrl = "http://192.168.169.1/led_state.json";
    final String ledChangeStateUrl = "http://192.168.169.1/set_led";
    final String ledAccessPointUrl = "http://192.168.169.1/led.html";
    final String led404Url = "http://192.168.169.1/404.html";

    ActivityMainBinding binding;
    private boolean isConnectedToBL602 = false;

    //target dialog number picker
    AlertDialog dialogNumberPicker;

    //target parameter
    int targetTempe = 20;
    int targetHumidity = 70;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inflating our xml layout in our activity main binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //for setting app to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();


        initViewsOnClickListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            //checkConnectionWithBl602();

        } catch (Exception e){
            snackBarMessage(e.getMessage());
        }

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;

        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            System.out.println("------------------------------------"+wifiInfo.getSSID());
            checkWifiStatus(
                    wifiInfo.getSSID().equals("\"BL602_AP\""));
        } else snackBarMessage("Not Connected to WIFI");

        //check led status
        //checkLedStatus();
    }



    void checkWifiStatus(boolean flag) {
        if (flag){
            binding.includeToolbox.imgConnectionStatus.setColorFilter(Color.BLACK);
            binding.includeToolbox.imgConnectionStatus.setImageDrawable(
                            AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_connected) );
            snackBarMessage("Connected");
            checkLedStatus();
            return;
        }
        binding.includeToolbox.imgConnectionStatus.setColorFilter(Color.RED);
        binding.includeToolbox.imgConnectionStatus.setImageDrawable(
                AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_disconnected) );
        snackBarMessage("Not Connected to PineCone");


    }

    void checkConnectionWithBl602() throws MalformedURLException, JSONException, ExecutionException, InterruptedException, DisconnectedException {
        isConnectedToBL602 =
                Devices.checkLedStatus(ledStatusUrl) != null;

        binding.includeToolbox.imgConnectionStatus.setImageDrawable(
                isConnectedToBL602?
                        AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_connected) :
                        AppCompatResources.getDrawable(getApplicationContext(),R.drawable.ic_disconnected)
        );

    }

    @SuppressLint("SetTextI18n")
    void initViewsOnClickListener(){
        //app drawer setup
        binding.includeToolbox.menuToolbox.setOnClickListener(View->{
            openDrawer(binding.parentDrawerLayout);
        });

        binding.includeToolbox.imgReload.setOnClickListener(View->{
            recreate();
        });

/*        binding.imgReload.setOnClickListener(View->{
            checkLedStatus();
        });*/

        //temperature switch
        binding.swTemperature.setOnClickListener(View->{
            CommunicateWIthBl602.toggleBl602Led(
                    ledChangeStateUrl,
                    "red",
                    binding.swTemperature.isChecked(),
                    getApplicationContext(),
                    this::checkLedStatus //lambda and interface
            );
        });

        //humidity switch
        binding.swHumidity.setOnClickListener(View->{
            CommunicateWIthBl602.toggleBl602Led(
                    ledChangeStateUrl,
                    "green",
                    binding.swHumidity.isChecked(),
                    getApplicationContext(),
                    this::checkLedStatus //lambda and interface
            );
        });

        //light switch
        binding.swLight.setOnClickListener(View->{
            CommunicateWIthBl602.toggleBl602Led(
                    ledChangeStateUrl,
                    "blue",
                    binding.swLight.isChecked(),
                    getApplicationContext(),
                    this::checkLedStatus //lambda and interface
            );
        });

        //target temperature set value
        binding.imgTargetTempe.setOnClickListener(View->{
            setDialogTargetTempe();
        });
        binding.swAutomate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.automationLinearLayout.setVisibility(
                        isChecked? View.VISIBLE : View.GONE
                );
            }
        });

        //target temperature set value
        binding.imgTargetHumidity.setOnClickListener(View->{
            setDialogTargetHumidity();
        });

    }

    void checkLedStatus(){
        try {
            Devices.setViewsAccordingToStatus(
                    Devices.checkLedStatus(ledStatusUrl),
                    binding);
            binding.includeToolbox.imgConnectionStatus.setImageDrawable(getDrawable(R.drawable.ic_connected));

        } catch (MalformedURLException | JSONException | NullPointerException | ExecutionException | InterruptedException | DisconnectedException e) {
            //exception for
            e.printStackTrace();
            if (e instanceof DisconnectedException){
                binding.includeToolbox.imgConnectionStatus.setImageDrawable(getDrawable(R.drawable.ic_disconnected));
                snackBarMessage(e.getMessage());
            }
            snackBarMessage(e.getMessage());
        }
    }

    void  snackBarMessage(String msg){
        Snackbar snackbar = Snackbar
                .make(binding.layoutError, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    void setDialogTargetTempe(){
        //new android.support.v4.app.AAlertDialog.Builder(getApplicationContext())
        dialogNumberPicker = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Temperature Picker")
                .create();
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.numberpicker, null);

        // Set the view to the AlertDialog
        dialogNumberPicker.setView(view);

        NumberPicker picker = view.findViewById(R.id.dialog_numberPicker);
        picker.setMaxValue(50);
        picker.setMinValue(0);
        picker.setValue(Integer.parseInt(binding.tvTargetTempe.getText().toString()));

        view.findViewById(R.id.dialog_ok).setOnClickListener(
            View->{
                binding.tvTargetTempe.setText(
                        String.valueOf(picker.getValue())
                );
                dialogNumberPicker.cancel();
        });

        dialogNumberPicker.show();
    }

    void setDialogTargetHumidity(){
        //new android.support.v4.app.AAlertDialog.Builder(getApplicationContext())
        dialogNumberPicker = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Humidity Picker")
                .create();
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.numberpicker, null);

        // Set the view to the AlertDialog
        dialogNumberPicker.setView(view);

        NumberPicker picker = view.findViewById(R.id.dialog_numberPicker);
        picker.setMaxValue(100);
        picker.setMinValue(0);
        picker.setValue(Integer.parseInt(binding.tvTargetHumidity.getText().toString()));

        view.findViewById(R.id.dialog_ok).setOnClickListener(
            View->{
                binding.tvTargetHumidity.setText(
                        String.valueOf(picker.getValue())
                );
                dialogNumberPicker.cancel();
        });

        dialogNumberPicker.show();
    }

    private void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void closeDrawer(DrawerLayout drawerLayout){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

}