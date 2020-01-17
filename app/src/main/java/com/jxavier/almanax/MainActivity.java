package com.jxavier.almanax;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Context context = MainActivity.this;
    //-----------------------------------------------
    // Date
    //-----------------------------------------------
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
    final String  date = sdf.format(calendar.getTime());

    public boolean onCreateDone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean progressDone = Preferences.getPrefsBoolean(date,context);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(progressDone){
            fab.setImageResource(R.drawable.ic_star_fill);
        }else{
            fab.setImageResource(R.drawable.ic_star_border);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Progression mise à jour !", Snackbar.LENGTH_LONG).show();
                boolean progress = Preferences.getPrefsBoolean(date,context);
                if(!progress) {
                    fab.setImageResource(R.drawable.ic_star_fill);
                    Preferences.setPrefs(date,true,context);
                }else{
                    fab.setImageResource(R.drawable.ic_star_border);
                    Preferences.setPrefs(date,false,context);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //-----------------------------------------------
        // Permissions
        //-----------------------------------------------
        requestStoragePermission();

        //-----------------------------------------------
        // References
        //-----------------------------------------------
        final ImageView objectIDView = findViewById(R.id.objectid);
        final ImageView bossView = findViewById(R.id.boss);
        final ImageView monthView = findViewById(R.id.month);
        final ImageView zodiacView = findViewById(R.id.zodiac);
        final TextView nameView = findViewById(R.id.name);
        final TextView offeringView = findViewById(R.id.offering);
        final TextView bonusTitleView = findViewById(R.id.bonustitle);
        final TextView bonusDescView = findViewById(R.id.bonusdesc);


        //-----------------------------------------------
        // Images MàJ
        //-----------------------------------------------
        final ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progress1);
        final ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.progress2);

        //-----------------------------------------------
        // Update image
        //-----------------------------------------------
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
           connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            setTodayAlmanax(progressBar1,progressBar2,bossView,objectIDView,nameView,offeringView,bonusTitleView,bonusDescView);
            onCreateDone = true;
        }else{
            Glide.with(context)
                    .load(R.drawable.picto_asset_dofus)
                    .into(bossView);
            Glide.with(context)
                    .load(R.drawable.picto_asset_dofus)
                    .into(objectIDView);
            bossView.setVisibility(View.VISIBLE);
            objectIDView.setVisibility(View.VISIBLE);
            bossView.setVisibility(View.VISIBLE);
            progressBar1.setVisibility(View.GONE);
            progressBar2.setVisibility(View.GONE);
            Toast.makeText(this,"Not Connected to Internet, please turn on wifi or data",Toast.LENGTH_LONG).show();

            //ALERT DIALOG
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Wifi not enabled");
            builder.setMessage("go to parameters ?");
            builder
                    .setNegativeButton("restart", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            triggerRebirth();
                        }})
                    .setPositiveButton("paramètres", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                        }}).show();
            onCreateDone = false;
        }

        //-----------------------------------------------
        // Update background
        //-----------------------------------------------
        setBackground();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        //-----------------------------------------------
        // References
        //-----------------------------------------------
        final ImageView objectIDView = findViewById(R.id.objectid);
        final ImageView bossView = findViewById(R.id.boss);
        final ImageView monthView = findViewById(R.id.month);
        final ImageView zodiacView = findViewById(R.id.zodiac);
        final TextView nameView = findViewById(R.id.name);
        final TextView offeringView = findViewById(R.id.offering);
        final TextView bonusTitleView = findViewById(R.id.bonustitle);
        final TextView bonusDescView = findViewById(R.id.bonusdesc);


        //-----------------------------------------------
        // Images MàJ
        //-----------------------------------------------
        final ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progress1);
        final ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.progress2);

        //-----------------------------------------------
        // Update image
        //-----------------------------------------------
        if(!onCreateDone){
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                setTodayAlmanax(progressBar1,progressBar2,bossView,objectIDView,nameView,offeringView,bonusTitleView,bonusDescView);
            }else{
                Glide.with(context)
                        .load(R.drawable.picto_asset_dofus)
                        .into(bossView);
                Glide.with(context)
                        .load(R.drawable.picto_asset_dofus)
                        .into(objectIDView);
                bossView.setVisibility(View.VISIBLE);
                objectIDView.setVisibility(View.VISIBLE);
                bossView.setVisibility(View.VISIBLE);
                progressBar1.setVisibility(View.GONE);
                progressBar2.setVisibility(View.GONE);
                Toast.makeText(this,"Not Connected to Internet, please turn on wifi or data",Toast.LENGTH_LONG).show();

                //ALERT DIALOG
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Wifi not enabled");
                builder.setMessage("go to parameters ?");
                builder
                        .setNegativeButton("restart", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                triggerRebirth();
                            }})
                        .setPositiveButton("paramètres", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                            }}).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void requestStoragePermission() {
        if(!(ActivityCompat.checkSelfPermission(this,READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ||
           !(ActivityCompat.checkSelfPermission(this,ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this,new String[]{INTERNET,ACCESS_NETWORK_STATE,READ_EXTERNAL_STORAGE}, 100);
        }
    }

    private void setTodayAlmanax(final ProgressBar progressBar1,
                                 final ProgressBar progressBar2,
                                 final ImageView bossView,
                                 final ImageView objectIDView,
                                 final TextView nameView,
                                 final TextView offeringView,
                                 final TextView bonusTitleView,
                                 final TextView bonusDescView){
        Calendar calendar = Calendar.getInstance();
        final int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        Log.d("DAYOFYEAR",""+dayOfYear);
        String url = "https://api.jsonbin.io/b/5e15bb148d761771cc8d3206";
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject dofus = response.getJSONObject("dofus");
                                JSONObject day = dofus.getJSONObject(date);
                                Glide.with(context)
                                        .load("https://staticns.ankama.com/krosmoz/img/uploads/event/"+(160+dayOfYear)+"/boss_all_96_128.png")
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                progressBar1.setVisibility(View.GONE);
                                                return false;
                                            }
                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                progressBar1.setVisibility(View.GONE);
                                                return false;
                                            }
                                        })
                                        .into(bossView);
                                Glide.with(context)
                                        .load("https://static.ankama.com/dofus/www/game/items/200/"+day.getString("objectID")+".png")
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                progressBar2.setVisibility(View.GONE);
                                                return false;
                                            }
                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                progressBar2.setVisibility(View.GONE);
                                                return false;
                                            }
                                        })
                                        .into(objectIDView);

                                bossView.setVisibility(View.VISIBLE);
                                objectIDView.setVisibility(View.VISIBLE);
                                nameView.setText("Quête : Offrande à "+day.getString("name"));
                                offeringView.setText("Récupérer "+day.getString("offering")+" et rapporter l'offrande à Théodoran Ax");
                                bonusTitleView.setText(day.getString("bonusTitle"));
                                bonusDescView.setText(day.getString("bonusDescription"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
            queue.add(request);
    }

    private void setBackground(){
        //-----------------------------------------------
        // Date MàJ
        //-----------------------------------------------
        String date_text= ""+Integer.valueOf(date.substring(3))+"\n"+ Utils.monthConversion.get(date.substring(0,2));
        SpannableString ss1=  new SpannableString(date_text);
        ss1.setSpan(new RelativeSizeSpan(2f), 0,2, 0); // set size
        ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#efbf31")), 0, date_text.length(),0);// set color
        TextView tv= (TextView) findViewById(R.id.date_text);
        tv.setText(ss1);

        //-----------------------------------------------
        // Background MàJ
        //-----------------------------------------------
        //Date
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        final String  month = sdf.format(calendar.getTime());

        //References
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView almanax_season = hView.findViewById(R.id.almanax_season);
        ImageView almanax_icon = hView.findViewById(R.id.almanax_icon);
        RelativeLayout backgroundView = findViewById(R.id.background);
        RelativeLayout clockView = findViewById(R.id.clock);
        ImageView zodiacView = findViewById(R.id.zodiac);
        ImageView monthView = findViewById(R.id.month);

        switch(month){
            case "01": {
                setBackgroundWithSeason("winter");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/9/all_128_128.png")
                        .into(monthView);
                setZodiaque(1);
                break;
            }
            case "02": {
                setBackgroundWithSeason("winter");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/10/all_128_128.png")
                        .into(monthView);
                setZodiaque(1);
                break;
            }
            case "03": {
                setBackgroundWithSeason("spring");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/11/all_128_128.png")
                        .into(monthView);
                setZodiaque(1);
                break;
            }
            case "04": {
                setBackgroundWithSeason("spring");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/12/all_128_128.png")
                        .into(monthView);
                setZodiaque(1);
                break;
            }
            case "05": {
                setBackgroundWithSeason("spring");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/13/all_128_128.png")
                        .into(monthView);
                setZodiaque(1);
                break;
            }
            case "06": {
                setBackgroundWithSeason("summer");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/14/all_128_128.png")
                        .into(monthView);
                setZodiaque(1);
                break;
            }
            case "07": {
                setBackgroundWithSeason("summer");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/15/all_128_128.png")
                        .into(monthView);
                setZodiaque(1);
                break;
            }
            case "08": {
                setBackgroundWithSeason("summer");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/8/all_128_128.png")
                        .into(monthView);
                setZodiaque(1);
                break;
            }
            case "09": {
                setBackgroundWithSeason("autumn");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/4/all_128_128.png")
                        .into(monthView);
                setZodiaque(1);
                break;
            }
            case "10": {
                setBackgroundWithSeason("autumn");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/5/all_128_128.png")
                        .into(monthView);
                setZodiaque(1);
                break;
            }
            case "11": {
                setBackgroundWithSeason("autumn");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/6/all_128_128.png")
                        .into(monthView);
                setZodiaque(1);
                break;
            }
            case "12": {
                setBackgroundWithSeason("winter");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/7/all_128_128.png")
                        .into(monthView);
                setZodiaque(1);
                break;
            }
            default: throw new Error("Bug check date.");
        }
    }

    private void setBackgroundWithSeason(String season){
        //References
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView almanax_season = hView.findViewById(R.id.almanax_season);
        ImageView almanax_icon = hView.findViewById(R.id.almanax_icon);
        RelativeLayout backgroundView = findViewById(R.id.background);
        RelativeLayout clockView = findViewById(R.id.clock);
        ImageView seasonView = findViewById(R.id.season);

        if(season.equals("winter")){
            seasonView.setBackgroundResource(R.drawable.winter_season);
            clockView.setBackgroundResource(R.drawable.winter_clock);
            backgroundView.setBackgroundResource(R.drawable.winter_bg_almanax);
            almanax_season.setText("Winter");
            almanax_icon.setImageResource(R.drawable.winter_season);
        }
        if(season.equals("autumn")){
            seasonView.setBackgroundResource(R.drawable.autumn_season);
            clockView.setBackgroundResource(R.drawable.autumn_clock);
            backgroundView.setBackgroundResource(R.drawable.autumn_bg_almanax);
            almanax_season.setText("Autumn");
            almanax_icon.setImageResource(R.drawable.autumn_season);
        }
        if(season.equals("summer")){
            seasonView.setBackgroundResource(R.drawable.summer_season);
            clockView.setBackgroundResource(R.drawable.summer_clock);
            backgroundView.setBackgroundResource(R.drawable.summer_bg_almanax);
            almanax_season.setText("Summer");
            almanax_icon.setImageResource(R.drawable.summer_season);
        }
        if(season.equals("spring")){
            seasonView.setBackgroundResource(R.drawable.spring_season);
            clockView.setBackgroundResource(R.drawable.spring_clock);
            backgroundView.setBackgroundResource(R.drawable.spring_bg_almanax);
            almanax_season.setText("Spring");
            almanax_icon.setImageResource(R.drawable.spring_season);
        }
    }

    private void setZodiaque(int month){
        ImageView zodiacView = findViewById(R.id.zodiac);
        int day = Integer.valueOf(date.substring(4));
        switch(month) {
            case 1: {
                if(day <=20){
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/15/all_128_128.png")
                            .into(zodiacView);
                    break;
                }else{
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/16/all_128_128.png")
                            .into(zodiacView);
                    break;
                }
            }
            case 2:{
                if(day <=18){
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/16/all_128_128.png")
                            .into(zodiacView);
                    break;
                }else{
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/17/all_128_128.png")
                            .into(zodiacView);
                    break;
                }
            }
            case 3:{
                if(day <=20){
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/17/all_128_128.png")
                            .into(zodiacView);
                    break;
                }else{
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/18/all_128_128.png")
                            .into(zodiacView);
                    break;
                }
            }
            case 4:{
                if(day <=20){
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/18/all_128_128.png")
                            .into(zodiacView);
                    break;
                }else{
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/19/all_128_128.png")
                            .into(zodiacView);
                    break;
                }
            }
            case 5:{
                if(day <=20){
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/19/all_128_128.png")
                            .into(zodiacView);
                    break;
                }else{
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/20/all_128_128.png")
                            .into(zodiacView);
                    break;
                }
            }
            case 6:{
                if(day <=21){
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/20/all_128_128.png")
                            .into(zodiacView);
                    break;
                }else{
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/21/all_128_128.png")
                            .into(zodiacView);
                    break;
                }
            }
            case 7:{
                if(day <=22){
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/21/all_128_128.png")
                            .into(zodiacView);
                    break;
                }else{
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/22/all_128_128.png")
                            .into(zodiacView);
                    break;
                }
            }
            case 8:{
                if(day <=22){
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/22/all_128_128.png")
                            .into(zodiacView);
                    break;
                }else{
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/10/all_128_128.png")
                            .into(zodiacView);
                    break;
                }
            }
            case 9:{
                if(day <=22){
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/10/all_128_128.png")
                            .into(zodiacView);
                    break;
                }else{
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/11/all_128_128.png")
                            .into(zodiacView);
                    break;
                }
            }
            case 10:{
                if(day <=22){
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/11/all_128_128.png")
                            .into(zodiacView);
                    break;
                }else{
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/12/all_128_128.png")
                            .into(zodiacView);
                    break;
                }
            }
            case 11:{
                if(day <=22){//sagittaire
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/12/all_128_128.png")
                            .into(zodiacView);
                    break;
                }else{
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/14/all_128_128.png")
                            .into(zodiacView);
                    break;
                }
            }
            case 12:{
                if(day <=21){
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/14/all_128_128.png")
                            .into(zodiacView);
                    break;
                }else{
                    Glide.with(context)
                            .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/15/all_128_128.png")
                            .into(zodiacView);
                    break;
                }
            }
            default: throw new Error("Bug check date.");
        }
    }

    private void triggerRebirth() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (this instanceof Activity) {
            ((Activity) this).finish();
        }
        Runtime.getRuntime().exit(0);
    }
}
