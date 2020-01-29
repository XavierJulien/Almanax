package com.jxavier.almanax.nav_bar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jxavier.almanax.Preferences;
import com.jxavier.almanax.R;
import com.jxavier.almanax.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class SemaineFragment extends Fragment {

    Calendar calendar = Calendar.getInstance();
    String  date = new SimpleDateFormat("MM-dd").format(calendar.getTime());

    private View v;
    private ViewGroup container;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_semaine, container, false);
        this.container = container;
        Calendar calendar_resume= Calendar.getInstance();
        calendar_resume.add(Calendar.DATE,-1);
        LinearLayout ll = (LinearLayout)v.findViewById(R.id.listView);
        ll.removeAllViews();
        for(int i = 0;i<7;i++){
            LinearLayout temp = (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.item_small, container, false);
            calendar.add(Calendar.DATE,1);
            String date_temp = new SimpleDateFormat("MM-dd").format(calendar.getTime());
            fillInfo(temp,date_temp);
            ll.addView(temp);
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        if(!Preferences.getPrefsBoolean("lang",getContext())){
            getActivity().setTitle("7 prochains jours");
        }else{
            getActivity().setTitle("Next 7 days");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Calendar calendar_resume= Calendar.getInstance();
        calendar_resume.set(Calendar.HOUR_OF_DAY, calendar_resume.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar_resume.set(Calendar.MINUTE, calendar_resume.getActualMinimum(Calendar.MINUTE));
        calendar_resume.set(Calendar.SECOND, calendar_resume.getActualMinimum(Calendar.SECOND));
        calendar_resume.set(Calendar.MILLISECOND, calendar_resume.getActualMinimum(Calendar.MILLISECOND));
        calendar_resume.add(Calendar.DATE,-1);
        LinearLayout ll = (LinearLayout)v.findViewById(R.id.listView);
        ll.removeAllViews();
        for(int i = 0;i<7;i++){
            LinearLayout temp = (LinearLayout)LayoutInflater.from(getContext()).inflate(R.layout.item_small, container, false);
            ViewGroup.LayoutParams params = temp.getLayoutParams();
            params.height = 250;
            temp.setLayoutParams(params);
            calendar_resume.add(Calendar.DATE,1);
            String date_temp = new SimpleDateFormat("MM-dd").format(calendar_resume.getTime());
            fillInfo(temp,date_temp);
            ll.addView(temp);
        }
    }

    public void fillInfo(LinearLayout ll,String date){
        ImageView objectIDView = ll.findViewById(R.id.objectid);
        TextView nameView = ll.findViewById(R.id.name);
        TextView offeringView = ll.findViewById(R.id.offering);
        ProgressBar progressBar2 = (ProgressBar) ll.findViewById(R.id.progress2);
        Utils.setInfo(ll,progressBar2,objectIDView,nameView,offeringView,date,getContext());
    }
}
