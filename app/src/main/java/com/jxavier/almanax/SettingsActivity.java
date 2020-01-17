package com.jxavier.almanax;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import com.jxavier.almanax.notification.NotificationHelper;

import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {



    Button setdate,valider;
    EditText dest;
    Switch switch_settings;
    private int mHour, mMinute;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        context = getApplicationContext();

        setdate =  findViewById(R.id.button_setdate);
        dest = findViewById(R.id.dest);
        switch_settings = findViewById(R.id.switch_settings);
        valider = findViewById(R.id.valider);

        switch_settings.setChecked(Boolean.valueOf(Preferences.getPrefs("Notification mode",context)));
        setdate.setEnabled(Boolean.valueOf(Preferences.getPrefs("Notification mode",context)));
        dest.setEnabled(Boolean.valueOf(Preferences.getPrefs("Notification mode",context)));
        valider.setEnabled(Boolean.valueOf(Preferences.getPrefs("Notification mode",context)));

        switch_settings.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //enable notif
                if (isChecked) {
                    setdate.setEnabled(true);
                    dest.setEnabled(true);
                    valider.setEnabled(true);
                    Preferences.setPrefs("Notification mode", "true",context);
                } else {
                    setdate.setEnabled(false);
                    dest.setEnabled(false);
                    valider.setEnabled(false);
                    Preferences.setPrefs("Notification mode", "false",context);
                    NotificationHelper.cancelAlarmRTC();
                }

            }
        });
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotificationChannel();
                NotificationHelper.scheduleRepeatingRTCNotification(context,""+mHour,""+mMinute);
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
                        }
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        }
    }
}