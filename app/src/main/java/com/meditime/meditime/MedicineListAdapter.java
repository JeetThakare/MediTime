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
        String time = getItem(position).getStartDate();
        //MedicineData data = new MedicineData(name,image,time);

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(myresource,parent,false);

        TextView tvname = (TextView) convertView.findViewById(R.id.textView3);
        TextView tvtime = (TextView) convertView.findViewById(R.id.textView2);
        ImageView ivpill = (ImageView) convertView.findViewById(R.id.imageView1);

        //set data into view
        tvname.setText(name);
        tvtime.setText(time);
        ivpill.setImageResource(R.drawable.medicine);

        return convertView;
    }
}
