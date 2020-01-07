package com.jxavier.almanax;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Context context = MainActivity.this;
    //-----------------------------------------------
    // Date
    //-----------------------------------------------
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
    final String  date = sdf.format(calendar.getTime());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Progression mise à jour !", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                fab.setImageResource(R.drawable.ic_star_fill);
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
        final TextView nameView = findViewById(R.id.name);
        final TextView offeringView = findViewById(R.id.offering);
        final TextView bonusTitleView = findViewById(R.id.bonustitle);
        final TextView bonusDescView = findViewById(R.id.bonusdesc);
        final ImageView monthView = findViewById(R.id.month);
        final ImageView zodiacView = findViewById(R.id.zodiac);
        Glide.with(context)
                .load("https://staticns.ankama.com/krosmoz/img/uploads/month/7/all_128_128.png")
                .into(monthView);
        Glide.with(context)
                .load("https://staticns.ankama.com/krosmoz/img/uploads/zodiac/14/all_128_128.png")
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(14)))
                .into(zodiacView);

        String s= "18 Descendre";
        SpannableString ss1=  new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(2f), 0,2, 0); // set size
        ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#efbf31")), 0, s.length(),0);// set color
        TextView tv= (TextView) findViewById(R.id.date_text);
        tv.setText(ss1);

        //-----------------------------------------------
        // Update image
        //-----------------------------------------------
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
           connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            setTodayAlmanax(bossView,objectIDView,nameView,offeringView,bonusTitleView,bonusDescView);
        }else{
            Glide.with(context)
                    .load(R.drawable.picto_asset_dofus)
                    .into(bossView);
            Glide.with(context)
                    .load(R.drawable.picto_asset_dofus)
                    .into(objectIDView);
            nameView.setText("Not Connected. Turn on Wifi");
        }
        setDateBackground();

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
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

    private void setTodayAlmanax(final ImageView bossView,
                                 final ImageView objectIDView,
                                 final TextView nameView,
                                 final TextView offeringView,
                                 final TextView bonusTitleView,
                                 final TextView bonusDescView){
            String url = "https://api.jsonbin.io/b/5def81331c19843d88e9a4ca/1";
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
                                        .load("https://static.ankama.com/dofus/www/game/items/200/"+day.getString("objectID")+".png")
                                        .into(objectIDView);
                                Glide.with(context)
                                        .load("https://staticns.ankama.com/krosmoz/img/uploads/event/136/boss_all_96_128.png")
                                        .error(R.drawable.picto_asset_dofus)
                                        .into(bossView);
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
                            Log.d("HEERRE", "error");
                            error.printStackTrace();
                        }
                    });
            queue.add(request);
    }

    private void setDateBackground(){
        //final ConstraintLayout background = findViewById(R.id.background);
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        final String  month = sdf.format(calendar.getTime());
        Log.d("Date", month);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView almanax_season = hView.findViewById(R.id.almanax_season);
        ImageView almanax_icon = hView.findViewById(R.id.almanax_icon);
        switch(month){
            case "01": {
                almanax_season.setText("Winter");
                almanax_icon.setImageResource(R.drawable.winter_season);
                break;
            }
            case "02": {
                almanax_season.setText("Winter");
                almanax_icon.setImageResource(R.drawable.winter_season);
                break;
            }
            case "03": {
                almanax_season.setText("Spring");
                almanax_icon.setImageResource(R.drawable.spring_season);

                break;
            }
            case "04": {
                almanax_season.setText("Spring");
                almanax_icon.setImageResource(R.drawable.spring_season);

                break;
            }
            case "05": {
                almanax_season.setText("Spring");
                almanax_icon.setImageResource(R.drawable.spring_season);

                break;
            }
            case "06": {
                almanax_season.setText("Summer");
                almanax_icon.setImageResource(R.drawable.summer_season);

                break;
            }
            case "07": {
                almanax_season.setText("Summer");
                almanax_icon.setImageResource(R.drawable.summer_season);

                break;
            }
            case "08": {
                almanax_season.setText("Summer");
                almanax_icon.setImageResource(R.drawable.summer_season);

                break;
            }
            case "09": {
                almanax_season.setText("Autumn");
                almanax_icon.setImageResource(R.drawable.autumn_season);

                break;
            }
            case "10": {
                almanax_season.setText("Autumn");
                almanax_icon.setImageResource(R.drawable.autumn_season);

                break;
            }
            case "11": {
                almanax_season.setText("Autumn");
                almanax_icon.setImageResource(R.drawable.autumn_season);

                break;
            }
            case "12": {
                almanax_season.setText("Winter");
                almanax_icon.setImageResource(R.drawable.winter_season);
                break;
            }
            default: throw new Error("Bug check date.");
        }
    }
}
