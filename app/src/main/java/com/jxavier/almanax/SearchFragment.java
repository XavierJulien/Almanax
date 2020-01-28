package com.jxavier.almanax;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class SearchFragment extends Fragment {

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
    String  date = sdf.format(calendar.getTime());

    private View v;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_search, container, false);
        calendar.add(Calendar.DATE,-1);
        LinearLayout ll = (LinearLayout)v.findViewById(R.id.listView);
        fillInfo((LinearLayout)v.findViewById(R.id.item1));
        fillInfo((LinearLayout)v.findViewById(R.id.item2));
        fillInfo((LinearLayout)v.findViewById(R.id.item3));
        fillInfo((LinearLayout)v.findViewById(R.id.item4));
        fillInfo((LinearLayout)v.findViewById(R.id.item5));
        fillInfo((LinearLayout)v.findViewById(R.id.item6));
        fillInfo((LinearLayout)v.findViewById(R.id.item7));
        return v;
    }

    public void fillInfo(LinearLayout ll){
        calendar.add(Calendar.DATE,1);
        String date = sdf.format(calendar.getTime());
        ImageView objectIDView = ll.findViewById(R.id.objectid);
        TextView nameView = ll.findViewById(R.id.name);
        TextView offeringView = ll.findViewById(R.id.offering);
        ProgressBar progressBar2 = (ProgressBar) ll.findViewById(R.id.progress2);
        setDayAlmanax(ll,progressBar2,objectIDView,nameView,offeringView,date);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Semaine");
    }

    private void setDayAlmanax(final LinearLayout ll,
                               final ProgressBar progressBar2,
                               final ImageView objectIDView,
                               final TextView nameView,
                               final TextView offeringView,
                               final String date){
        String url = Utils.URL;
        // Instantiate the RequestQueue.
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
    }
}
