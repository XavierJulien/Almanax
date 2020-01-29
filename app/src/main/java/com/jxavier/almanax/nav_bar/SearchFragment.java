package com.jxavier.almanax.nav_bar;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;


public class SearchFragment extends Fragment {

    private View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_search, container, false);
        addItemsOnSpinner(v);
        Button submit = v.findViewById(R.id.btnSubmit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout ll = v.findViewById(R.id.layout_items_result);
                ll.removeAllViews();
                update();
            }
        });
        return v;
    }

    private void addItemsOnSpinner(final View v) {
        final HashSet<String> bonus = new HashSet<>();
        final Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
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
                                Iterator<String> iterator = dofus.keys();
                                while(iterator.hasNext()){
                                    String next = iterator.next();
                                    JSONObject item = dofus.getJSONObject(next);
                                    bonus.add(item.getString("bonusTitle"));
                                }

                                List<String> list_bonus = new ArrayList<String>(bonus);
                                Collections.sort(list_bonus);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, list_bonus);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(adapter);
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
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject dofus = response.getJSONObject("dofus");
                                Iterator<String> iterator = dofus.keys();
                                while(iterator.hasNext()){
                                    String next = iterator.next();
                                    JSONObject item = dofus.getJSONObject(next);
                                    bonus.add(item.getString("bonusTitle"));
                                }

                                List<String> list_bonus = new ArrayList<String>(bonus);
                                Collections.sort(list_bonus);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, list_bonus);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(adapter);
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

    @Override
    public void onResume() {
        super.onResume();
        addItemsOnSpinner(v);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(Preferences.getPrefs("Lang mode",getContext()).equals("FR")){
            getActivity().setTitle("Recherche");
        }else{
            getActivity().setTitle("Search");
        }
    }

    public void fillInfo(LinearLayout ll,String date){
        ImageView objectIDView = ll.findViewById(R.id.objectid);
        TextView nameView = ll.findViewById(R.id.name);
        TextView offeringView = ll.findViewById(R.id.offering);
        ProgressBar progressBar2 = (ProgressBar) ll.findViewById(R.id.progress2);
        setInfo(ll,progressBar2,objectIDView,nameView,offeringView,date);
    }

    private void setInfo(final LinearLayout ll,
                         final ProgressBar progressBar2,
                         final ImageView objectIDView,
                         final TextView nameView,
                         final TextView offeringView,
                         final String date){
        // Instantiate the RequestQueue.
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
                                final JSONObject day = dofus.getJSONObject(date);
                                Glide.with(getContext())
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
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject dofus = response.getJSONObject("dofus");
                                final JSONObject day = dofus.getJSONObject(date);
                                Glide.with(getContext())
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
                                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    private void update(){
        final LinearLayout ll = v.findViewById(R.id.layout_items_result);
        final Spinner spinner = v.findViewById(R.id.spinner);

        //REQUEST
        String url;
        if(Preferences.getPrefs("Lang mode",getContext()).equals("FR")){
            url = Utils.URL_FR;
        }else{
            url = Utils.URL_EN;
        }
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject dofus = response.getJSONObject("dofus");
                            Iterator<String> iterator = dofus.keys();
                            while(iterator.hasNext()){
                                String next = iterator.next();
                                JSONObject item = dofus.getJSONObject(next);
                                if(item.getString("bonusTitle").equals(spinner.getSelectedItem().toString())){
                                    LinearLayout temp = (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.item_small, null, false);
                                    fillInfo(temp,next);
                                    ll.addView(temp);
                                }
                            }
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
