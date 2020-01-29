package com.jxavier.almanax;

import android.app.Activity;
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
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.jxavier.almanax.nav_bar.ProgressionFragment;
import com.jxavier.almanax.nav_bar.SearchFragment;
import com.jxavier.almanax.nav_bar.SemaineFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import java.util.ArrayList;
import java.util.Calendar;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //-----------------------------------------------
    // Date
    //-----------------------------------------------
    Calendar calendar = Calendar.getInstance();
    final String  date = new SimpleDateFormat("MM-dd").format(calendar.getTime());

    private Context context = MainActivity.this;
    public boolean lang;
    FloatingActionButton fab;
    Fragment fragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(!Preferences.getPrefsBoolean("lang",context)){
            lang = true;
            setTitle("Almanax du jour");
        }else{
            lang = false;
            setTitle("Almanax of the day");
        }
        //------------------------------------------------------------------------------------------
        // PROGRESSION
        //------------------------------------------------------------------------------------------
        boolean progressDone = Preferences.getPrefsBoolean(date,context);
        fab = (FloatingActionButton) findViewById(R.id.fab);
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
                    ArrayList<String> done_almanax = Preferences.getArrayPrefs("done",getApplicationContext());
                    if(!done_almanax.contains(date)) done_almanax.add(date);
                    Preferences.setArrayPrefs("done",done_almanax,context);
                    Preferences.setPrefs(date,true,context);
                }else{
                    Preferences.setPrefs(date,false,context);
                    ArrayList<String> done_almanax = Preferences.getArrayPrefs("done",getApplicationContext());
                    done_almanax.remove(date);
                    fab.setImageResource(R.drawable.ic_star_border);
                    Preferences.setArrayPrefs("done",done_almanax,context);
                }
            }
        });

        //------------------------------------------------------------------------------------------
        // DRAWER INIT
        //------------------------------------------------------------------------------------------
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menuNav=navigationView.getMenu();
        MenuItem nav_today = menuNav.findItem(R.id.nav_today);
        nav_today.setChecked(true);
        MenuItem nav_week = menuNav.findItem(R.id.nav_week);
        MenuItem nav_calendar = menuNav.findItem(R.id.nav_calendar);
        MenuItem nav_search = menuNav.findItem(R.id.nav_search);
        if(!Preferences.getPrefsBoolean("lang",context)){
            nav_today.setTitle("Aujourd'hui");
            nav_week.setTitle("Semaine");
            nav_calendar.setTitle("Calendrier");
            nav_search.setTitle("Recherche");
        }else{
            nav_today.setTitle("Today");
            nav_week.setTitle("Week");
            nav_calendar.setTitle("Calendar");
            nav_search.setTitle("Search");
        }

        //------------------------------------------------------------------------------------------
        // Permissions
        //------------------------------------------------------------------------------------------
        requestStoragePermission();

        //------------------------------------------------------------------------------------------
        // References
        //------------------------------------------------------------------------------------------
        final ImageView objectIDView = findViewById(R.id.objectid);
        final ImageView bossView = findViewById(R.id.boss);
        final ImageView monthView = findViewById(R.id.month);
        final ImageView zodiacView = findViewById(R.id.zodiac);
        final TextView nameView = findViewById(R.id.name);
        final TextView offeringView = findViewById(R.id.offering);
        final TextView bonusTitleView = findViewById(R.id.bonustitle);
        final TextView bonusDescView = findViewById(R.id.bonusdesc);
        final ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progress1);
        final ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.progress2);


        //------------------------------------------------------------------------------------------
        // Check Lang
        //------------------------------------------------------------------------------------------

        //------------------------------------------------------------------------------------------
        // Update image
        //------------------------------------------------------------------------------------------
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
           connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            Utils.setTodayAlmanax(progressBar1,progressBar2,bossView,objectIDView,nameView,offeringView,bonusTitleView,bonusDescView,date,context,lang);
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
        //------------------------------------------------------------------------------------------
        // Update background
        //------------------------------------------------------------------------------------------
        setBackground();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu menuNav=navigationView.getMenu();
        MenuItem nav_today = menuNav.findItem(R.id.nav_today);
        MenuItem nav_week = menuNav.findItem(R.id.nav_week);
        MenuItem nav_calendar = menuNav.findItem(R.id.nav_calendar);
        MenuItem nav_search = menuNav.findItem(R.id.nav_search);
        if(!Preferences.getPrefsBoolean("lang",context)){
            lang = true;
            if(fragment instanceof Fragment){
                setTitle("Almanax du jour");
                nav_today.setChecked(true);
            }
            if(fragment instanceof ProgressionFragment){
                nav_calendar.setChecked(true);
                setTitle("Calendrier");
            }
            if(fragment instanceof SearchFragment){
                nav_search.setChecked(true);
                setTitle("Recherche");
            }
            if(fragment instanceof SemaineFragment){
                nav_week.setChecked(true);
                setTitle("7 prochains jours");
            }
        }else{
            lang = false;
            if(fragment instanceof Fragment){
                nav_today.setChecked(true);
                setTitle("Almanax of the day");
            }
            if(fragment instanceof ProgressionFragment){
                nav_calendar.setChecked(true);
                setTitle("Calendar");
            }
            if(fragment instanceof SearchFragment){
                nav_search.setChecked(true);
                setTitle("Search");
            }
            if(fragment instanceof SemaineFragment){
                nav_week.setChecked(true);
                setTitle("Next 7 days");
            }
        }
        if(!Preferences.getPrefsBoolean("lang",context)){
            nav_today.setTitle("Aujourd'hui");
            nav_week.setTitle("Semaine");
            nav_calendar.setTitle("Calendrier");
            nav_search.setTitle("Recherche");
        }else{
            nav_today.setTitle("Today");
            nav_week.setTitle("Week");
            nav_calendar.setTitle("Calendar");
            nav_search.setTitle("Search");
        }
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
        final ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.progress1);
        final ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.progress2);

        //-----------------------------------------------
        // Update image
        //-----------------------------------------------
        if(lang != Preferences.getPrefsBoolean("lang",context)){
            lang = Preferences.getPrefsBoolean("lang",context);
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                Utils.setTodayAlmanax(progressBar1,progressBar2,bossView,objectIDView,nameView,offeringView,bonusTitleView,bonusDescView,date,context,lang);
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
        setBackground();
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(item.isChecked()){
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        int id = item.getItemId();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (id){
            case R.id.nav_today : {
                if(Preferences.getPrefsBoolean("lang",context)){
                    lang = true;
                    setTitle("Almanax du jour");
                }else{
                    lang = false;
                    setTitle("Almanax of the day");
                }
                fragment = new Fragment();
                fab.setEnabled(true);
                fab.setClickable(true);
                fab.setAlpha(1f);
                break;
            }
            case R.id.nav_week : {
                fragment = new SemaineFragment();
                fab.setEnabled(false);
                fab.setClickable(false);
                fab.setAlpha(0f);
                break;
            }
            case R.id.nav_calendar : {
                fragment = new ProgressionFragment();
                fab.setEnabled(false);
                fab.setClickable(false);
                fab.setAlpha(0f);
                break;
            }
            case R.id.nav_search : {
                fragment = new SearchFragment();
                fab.setEnabled(false);
                fab.setClickable(false);
                fab.setAlpha(0f);
                break;
            }
        }

        //replacing the fragment
        if (fragment != null) {
            ft.replace(R.id.fragment, fragment);
            ft.commit();
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
                setZodiaque(2);
                break;
            }
            case "03": {
                setBackgroundWithSeason("spring");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/11/all_128_128.png")
                        .into(monthView);
                setZodiaque(3);
                break;
            }
            case "04": {
                setBackgroundWithSeason("spring");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/12/all_128_128.png")
                        .into(monthView);
                setZodiaque(4);
                break;
            }
            case "05": {
                setBackgroundWithSeason("spring");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/13/all_128_128.png")
                        .into(monthView);
                setZodiaque(5);
                break;
            }
            case "06": {
                setBackgroundWithSeason("summer");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/14/all_128_128.png")
                        .into(monthView);
                setZodiaque(6);
                break;
            }
            case "07": {
                setBackgroundWithSeason("summer");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/15/all_128_128.png")
                        .into(monthView);
                setZodiaque(7);
                break;
            }
            case "08": {
                setBackgroundWithSeason("summer");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/8/all_128_128.png")
                        .into(monthView);
                setZodiaque(8);
                break;
            }
            case "09": {
                setBackgroundWithSeason("autumn");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/4/all_128_128.png")
                        .into(monthView);
                setZodiaque(9);
                break;
            }
            case "10": {
                setBackgroundWithSeason("autumn");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/5/all_128_128.png")
                        .into(monthView);
                setZodiaque(10);
                break;
            }
            case "11": {
                setBackgroundWithSeason("autumn");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/6/all_128_128.png")
                        .into(monthView);
                setZodiaque(11);
                break;
            }
            case "12": {
                setBackgroundWithSeason("winter");
                Glide.with(context)
                        .load("https://staticns.ankama.com/krosmoz/img/uploads/month/7/all_128_128.png")
                        .into(monthView);
                setZodiaque(12);
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
            if(lang){
                almanax_season.setText("Hiver");
            }else{
                almanax_season.setText("Winter");
            }
            almanax_icon.setImageResource(R.drawable.winter_season);
        }
        if(season.equals("autumn")){
            seasonView.setBackgroundResource(R.drawable.autumn_season);
            clockView.setBackgroundResource(R.drawable.autumn_clock);
            backgroundView.setBackgroundResource(R.drawable.autumn_bg_almanax);
            if(lang){
                almanax_season.setText("Automne");
            }else{
                almanax_season.setText("Autumn");
            }
            almanax_icon.setImageResource(R.drawable.autumn_season);
        }
        if(season.equals("summer")){
            seasonView.setBackgroundResource(R.drawable.summer_season);
            clockView.setBackgroundResource(R.drawable.summer_clock);
            backgroundView.setBackgroundResource(R.drawable.summer_bg_almanax);
            if(lang){
                almanax_season.setText("Été");
            }else{
                almanax_season.setText("Summer");
            }
            almanax_icon.setImageResource(R.drawable.summer_season);
        }
        if(season.equals("spring")){
            seasonView.setBackgroundResource(R.drawable.spring_season);
            clockView.setBackgroundResource(R.drawable.spring_clock);
            backgroundView.setBackgroundResource(R.drawable.spring_bg_almanax);
            if(lang){
                almanax_season.setText("Printemps");
            }else{
                almanax_season.setText("Spring");
            }
            almanax_icon.setImageResource(R.drawable.spring_season);
        }
    }

    private void setZodiaque(int month){
        ImageView zodiacView = findViewById(R.id.zodiac);
        int day = Integer.valueOf(date.substring(3));
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
