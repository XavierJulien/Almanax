package com.jxavier.almanax;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.navigation.NavigationView;
import com.jxavier.almanax.notification.NotificationHelper;

import java.util.Calendar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {



    Button setdate,valider;
    TextView dest,tv_lang;
    Switch switch_settings,switch_lang;
    private int mHour, mMinute;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        context = getApplicationContext();

        setdate =  findViewById(R.id.button_setdate);
        dest = findViewById(R.id.date_result_picker);
        switch_settings = findViewById(R.id.switch_settings);
        switch_lang = findViewById(R.id.switch_lang);
        valider = findViewById(R.id.valider);
        tv_lang = findViewById(R.id.lang_text);

        if(Preferences.getPrefs("Lang mode",context).equals("FR")){
            switch_lang.setChecked(false);
            tv_lang.setText("Langue : Français");
        }else{
            switch_lang.setChecked(true);
            tv_lang.setText("Langue : Anglais");
        }
        switch_settings.setChecked(Boolean.valueOf(Preferences.getPrefs("Notification mode",context)));
        if (switch_settings.isChecked()) {
            setdate.setVisibility(View.VISIBLE);
            dest.setVisibility(View.VISIBLE);
            if(!Preferences.getPrefs("time_set_notif",context).equals("notfound")){
                dest.setText(Preferences.getPrefs("time_set_notif",context));
            }
            valider.setVisibility(View.VISIBLE);
        } else {
            setdate.setVisibility(View.GONE);
            dest.setVisibility(View.GONE);
            valider.setVisibility(View.GONE);
        }

        switch_lang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //enable notif
                if (!isChecked) {
                    Preferences.setPrefs("Lang mode", "FR",context);
                    tv_lang.setText("Langue : Français");
                } else {
                    Preferences.setPrefs("Lang mode", "EN",context);
                    tv_lang.setText("Langue : Anglais");
                }

            }
        });

        switch_settings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //enable notif
                if (isChecked) {
                    setdate.setVisibility(View.VISIBLE);
                    dest.setVisibility(View.VISIBLE);
                    valider.setVisibility(View.VISIBLE);
                    Preferences.setPrefs("Notification mode", "true",context);
                } else {
                    setdate.setVisibility(View.GONE);
                    dest.setVisibility(View.GONE);
                    valider.setVisibility(View.GONE);
                    Preferences.setPrefs("Notification mode", "false",context);
                }

            }
        });
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotificationChannel();
                if(switch_settings.isChecked()){
                    Preferences.setPrefs("time_set_notif",mHour+":"+mMinute,context);
                    NotificationHelper.scheduleRepeatingRTCNotification(context,mHour,mMinute);
                }else{
                    NotificationHelper.cancelAlarmRTC();
                }
                finish();
            }
        });
        setdate.setOnClickListener(this);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Utils.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == setdate) {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            dest.setText(hourOfDay + ":" + minute);
                            mMinute = minute;
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        }
    }

}