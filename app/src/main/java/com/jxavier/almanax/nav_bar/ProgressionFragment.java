package com.jxavier.almanax.nav_bar;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.jxavier.almanax.Preferences;
import com.jxavier.almanax.R;
import com.jxavier.almanax.Utils;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class ProgressionFragment extends Fragment {

    Calendar calendar = Calendar.getInstance();
    final String date = new SimpleDateFormat("MM-dd").format(calendar.getTime());
    final CaldroidFragment caldroidFragment = new CaldroidFragment();
    private View v;
    LinearLayout item;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_month, container, false);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        caldroidFragment.setArguments(args);
        ft.replace(R.id.month_layout, caldroidFragment);
        ft.commit();

        return v;
    }

    public void setAlmanaxDone(CaldroidFragment fragment){
        ArrayList<String> done_almanax = Preferences.getArrayPrefs("done",getContext());
        Drawable d = getResources().getDrawable(R.drawable.dofus);
        for(String date : done_almanax){
            int year = calendar.get(Calendar.YEAR);
            int month = Integer.valueOf(date.substring(0,2))-1;
            int day = Integer.valueOf(date.substring(3));
            calendar.set(year,month,day);
            ColorDrawable green = new ColorDrawable(Color.GREEN);
            fragment.setBackgroundDrawableForDate(green, calendar.getTime());
        }
    };


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!Preferences.getPrefsBoolean("lang",getContext())){
            getActivity().setTitle("Calendrier");
        }else{
            getActivity().setTitle("Calendar");
        }
        //References
        item =(LinearLayout) getView().findViewById(R.id.affichage);
        final ImageView objectIDView = item.findViewById(R.id.objectid);
        final ImageView bossView = item.findViewById(R.id.boss);
        final ImageView monthView = item.findViewById(R.id.month);
        final ImageView zodiacView = item.findViewById(R.id.zodiac);
        final TextView nameView = item.findViewById(R.id.name);
        final TextView offeringView = item.findViewById(R.id.offering);
        final TextView bonusTitleView = item.findViewById(R.id.bonustitle);
        final TextView bonusDescView = item.findViewById(R.id.bonusdesc);
        final ProgressBar progressBar1 = (ProgressBar) item.findViewById(R.id.progress1);
        final ProgressBar progressBar2 = (ProgressBar) item.findViewById(R.id.progress2);
        item.setVisibility(View.GONE);
        ArrayList<String> done_almanax = Preferences.getArrayPrefs("done",getContext());
        String date_text= done_almanax.size()+"/365";
        SpannableString ss1=  new SpannableString(date_text);
        ss1.setSpan(new RelativeSizeSpan(1.5f), 1,ss1.length(), 0); // set size
        ss1.setSpan(new ForegroundColorSpan(Color.parseColor("#efbf31")), 0, date_text.length(),0);// set color
        TextView tv= (TextView) getView().findViewById(R.id.objectif_text);
        tv.setText(ss1);
        setAlmanaxDone(caldroidFragment);
        CaldroidListener listener = createListener(progressBar1,progressBar2,bossView,objectIDView,nameView,offeringView,bonusTitleView,bonusDescView);
        caldroidFragment.setCaldroidListener(listener);
    }

    public CaldroidListener createListener(final ProgressBar progressBar1,
                                           final ProgressBar progressBar2,
                                           final ImageView bossView,
                                           final ImageView objectIDView,
                                           final TextView nameView,
                                           final TextView offeringView,
                                           final TextView bonusTitleView,
                                           final TextView bonusDescView){
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date_selected, View view) {
                caldroidFragment.setTextColorForDate(R.color.caldroid_light_red, date_selected);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                SimpleDateFormat sdf_month = new SimpleDateFormat("MM");
                SimpleDateFormat sdf_day= new SimpleDateFormat("dd");
                final String day = sdf_day.format(date_selected.getTime());
                final String month = sdf_month.format(date_selected.getTime());
                final String date_almanax = new SimpleDateFormat("MM-dd").format(date_selected.getTime());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date_selected);
                final int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);

                String url;
                if(Preferences.getPrefs("Lang mode",getContext()).equals("FR")){
                    url = Utils.URL_FR;
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONObject dofus = response.getJSONObject("dofus");
                                        final JSONObject objectif = dofus.getJSONObject(""+month+"-"+day);
                                        Glide.with(getContext())
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
                                        Glide.with(getContext())
                                                .load("https://static.ankama.com/dofus/www/game/items/200/"+objectif.getString("objectID")+".png")
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
                                        nameView.setText("Quête : Offrande à "+objectif.getString("name"));
                                        offeringView.setText("Récupérer "+objectif.getString("offering")+" et rapporter l'offrande à Théodoran Ax");
                                        bonusTitleView.setText(objectif.getString("bonusTitle"));
                                        bonusDescView.setText(objectif.getString("bonusDescription"));
                                        item.setVisibility(View.VISIBLE);
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
                }else {
                    url = Utils.URL_EN;
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONObject dofus = response.getJSONObject("dofus");
                                        final JSONObject objectif = dofus.getJSONObject("" + month + "-" + day);
                                        Glide.with(getContext())
                                                .load("https://staticns.ankama.com/krosmoz/img/uploads/event/" + (160 + dayOfYear) + "/boss_all_96_128.png")
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
                                        Glide.with(getContext())
                                                .load("https://static.ankama.com/dofus/www/game/items/200/" + objectif.getString("objectID") + ".png")
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
                                        nameView.setText("Quest: Offering for "+ objectif.getString("name"));
                                        offeringView.setText("Find "+ objectif.getString("offering") + " and take the offering to Antyklime Ax");
                                        bonusTitleView.setText(objectif.getString("bonusTitle"));
                                        bonusDescView.setText(objectif.getString("bonusDescription"));
                                        item.setVisibility(View.VISIBLE);
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
        };
        return listener;
    }
}
