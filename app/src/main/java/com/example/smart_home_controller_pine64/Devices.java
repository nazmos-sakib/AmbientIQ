package com.example.smart_home_controller_pine64;

import android.graphics.Color;

import com.example.smart_home_controller_pine64.Utill.DisconnectedException;
import com.example.smart_home_controller_pine64.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.concurrent.ExecutionException;

public class Devices {

    public static JSONObject checkLedStatus(String ledStatusUrl) throws MalformedURLException, JSONException, ExecutionException, InterruptedException, DisconnectedException {
        String ledStatus = CommunicateWIthBl602.getLedStatus( new URL(ledStatusUrl) );
        if (ledStatus!=null){
             return  new JSONObject(ledStatus);
        } else throw new DisconnectedException("Disconnected");
    }

    public static void setViewsAccordingToStatus(JSONObject jsonObject, ActivityMainBinding binding) throws JSONException {

        //temperature
        binding.swTemperature.setChecked(
                jsonObject.getInt("led_red")==1);
        binding.imgTemperature.setColorFilter(Color.rgb(
                0,
                jsonObject.getInt("led_red")==1? 255: 0,
                0
        ));
        binding.tvTemperature.setText(
                MessageFormat.format("{0} °C", jsonObject.getInt("tempe"))
        );

        //humidity
        binding.swHumidity.setChecked(
                jsonObject.getInt("led_green")==1);
        binding.imgHumidity.setColorFilter(Color.rgb(
                0,
                jsonObject.getInt("led_green")==1? 255: 0,
                0
        ));
        binding.tvHumidity.setText(
                MessageFormat.format("{0} %", jsonObject.getInt("rh"))
        );

        //light
        binding.swLight.setChecked(
                jsonObject.getInt("led_blue")==1);
        binding.imgLight.setColorFilter(Color.rgb(
                0,
                jsonObject.getInt("led_blue")==1? 255: 0,
                0
        ));




        //hu

    }


}
