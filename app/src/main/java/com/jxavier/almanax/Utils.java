package com.jxavier.almanax;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class Utils {
    public static final String CHANNEL_ID = "my_channel_id";
    public static final String PREFS_NAME = "preferences";
    public static HashMap<String, String> monthConversion = new HashMap<String,String>(){{
        put("12", "Descendre");
        put("11", "Novamaire");
        put("10", "Octolliard");
        put("09", "Septange");
        put("08", "Fraouctor");
        put("07", "Joullier");
        put("06", "Juinssidor");
        put("05", "Maisial");
        put("04", "Aperirel");
        put("03", "Martalo");
        put("02", "Flovor");
        put("01", "Javian");
    }};
    public static String URL_EN = "https://api.jsonbin.io/b/5e3079f33d75894195e09cff";
    public static String URL_FR = "https://api.jsonbin.io/b/5e31f8cb50a7fe418c5630bb";

    //--------------------------------------------------------------
    // REQUESTS METHODS
    //--------------------------------------------------------------

    public static void setTodayAlmanax(final ProgressBar progressBar1,
                                       final ProgressBar progressBar2,
                                       final ImageView bossView,
                                       final ImageView objectIDView,
                                       final TextView nameView,
                                       final TextView offeringView,
                                       final TextView bonusTitleView,
                                       final TextView bonusDescView,
                                       final String date,
                                       final Context context,
                                       boolean lang){
        Calendar calendar = Calendar.getInstance();
        final int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        String url;
        if(lang){
            url = Utils.URL_FR;
            RequestQueue queue = Volley.newRequestQueue(context);
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
        }else{
            url = Utils.URL_EN;
            RequestQueue queue = Volley.newRequestQueue(context);
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
                                nameView.setText("Quest: Offering for "+day.getString("name"));
                                offeringView.setText("Find "+day.getString("offering")+" and take the offering to Antyklime Ax");
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
        // Instantiate the RequestQueue.

    }

    public static void setDayAlmanax(final LinearLayout ll,
                               final ProgressBar progressBar2,
                               final ImageView objectIDView,
                               final TextView nameView,
                               final TextView offeringView,
                               final String date,
                               final Context context){
        String url;
        if(Preferences.getPrefs("Lang mode",context).equals("FR")){
            url = Utils.URL_FR;
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject dofus = response.getJSONObject("dofus");
                                final JSONObject day = dofus.getJSONObject(date);
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
                                objectIDView.setVisibility(View.VISIBLE);
                                nameView.setText("Quête : Offrande à "+day.getString("name"));
                                offeringView.setText("Récupérer "+day.getString("offering")+" et rapporter l'offrande à Théodoran Ax");
                                ll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try{
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setTitle(""+Integer.valueOf(date.substring(3))+"\n"+ Utils.monthConversion.get(date.substring(0,2)));
                                            builder.setMessage("Bonus : "+day.getString("bonusTitle")+
                                                    "\n"+day.getString("bonusDescription")).show();
                                        }catch (JSONException e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
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
        }else{
            url = Utils.URL_EN;
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject dofus = response.getJSONObject("dofus");
                                final JSONObject day = dofus.getJSONObject(date);
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
                                objectIDView.setVisibility(View.VISIBLE);
                                nameView.setText("Quest: Offering for "+day.getString("name"));
                                offeringView.setText("Find "+day.getString("offering")+" and take the offering to Antyklime Ax");
                                ll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try{
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setTitle(""+Integer.valueOf(date.substring(3))+"\n"+ Utils.monthConversion.get(date.substring(0,2)));
                                            builder.setMessage("Bonus : "+day.getString("bonusTitle")+
                                                    "\n"+day.getString("bonusDescription")).show();
                                        }catch (JSONException e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
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
    }

    public static void setInfo(final LinearLayout ll,
                         final ProgressBar progressBar2,
                         final ImageView objectIDView,
                         final TextView nameView,
                         final TextView offeringView,
                         final String date,
                         final Context context){
        // Instantiate the RequestQueue.
        String url;
        if(Preferences.getPrefs("Lang mode",context).equals("FR")){
            url = Utils.URL_FR;
            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject dofus = response.getJSONObject("dofus");
                                final JSONObject day = dofus.getJSONObject(date);
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
                                objectIDView.setVisibility(View.VISIBLE);
                                nameView.setText("Quête : Offrande à "+day.getString("name"));
                                offeringView.setText("Récupérer "+day.getString("offering")+" et rapporter l'offrande à Théodoran Ax");
                                ll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try{
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setTitle(""+Integer.valueOf(date.substring(3))+"\n"+ Utils.monthConversion.get(date.substring(0,2)));
                                            builder.setMessage("Bonus : "+day.getString("bonusTitle")+
                                                    "\n "+day.getString("bonusDescription")).show();
                                        }catch (JSONException e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
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
        }else{
            url = Utils.URL_EN;
            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject dofus = response.getJSONObject("dofus");
                                final JSONObject day = dofus.getJSONObject(date);
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
                                objectIDView.setVisibility(View.VISIBLE);
                                nameView.setText("Quest: Offering for "+day.getString("name"));
                                offeringView.setText("Find "+day.getString("offering")+" and take the offering to Antyklime Ax");
                                ll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try{
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setTitle(""+Integer.valueOf(date.substring(3))+"\n"+ Utils.monthConversion.get(date.substring(0,2)));
                                            builder.setMessage("Bonus : "+day.getString("bonusTitle")+
                                                    "\n "+day.getString("bonusDescription")).show();
                                        }catch (JSONException e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
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

    }
}