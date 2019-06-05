package com.meditime.meditime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.internal.bind.ArrayTypeAdapter;

import java.util.ArrayList;

public class MedicineListAdapter extends ArrayAdapter<Medicine> {
    private  final Context context;
    private int myresource;

    public MedicineListAdapter(Context context, int resource, ArrayList<Medicine> objects){
        super(context,resource,objects);
        this.context = context;
        this.myresource = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = getItem(position).getName();
        //String image = getItem(position).getPhotoUrl();
        String time = getItem(position).getDayFreq();
        String dayfreq = convertDayFreq(time);


        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(myresource,parent,false);

        TextView tvname = (TextView) convertView.findViewById(R.id.textView2);
        TextView tvtime = (TextView) convertView.findViewById(R.id.textView3);
        ImageView ivpill = (ImageView) convertView.findViewById(R.id.imageView1);

        //set data into view
        tvname.setText(name);
        tvtime.setText(dayfreq);
        ivpill.setImageResource(R.drawable.medicine);

        return convertView;
    }
    public String convertDayFreq(String dayFreq){
        String[] sarrayDayFreq = new String[3];
        String s_dayfreq = new String();

        char[] arrayDayFreq = dayFreq.toCharArray();
        for(char c : arrayDayFreq){
            System.out.println(c);
        }

        if( arrayDayFreq[0] == '1'){
            sarrayDayFreq[0] = "Morning";
        }
        if( arrayDayFreq[1] == '1'){
            sarrayDayFreq[1] = "Afternoon";
        }
        if( arrayDayFreq[2] == '1'){
            sarrayDayFreq[2] = "Evening";
        }

        for(int i =0 ; i<sarrayDayFreq.length;i++){
            if(sarrayDayFreq[i] != null){
                s_dayfreq = s_dayfreq + " " + sarrayDayFreq[i];
            }
            else{
                s_dayfreq = s_dayfreq+"";
            }
        }
        return s_dayfreq;
    }
}
