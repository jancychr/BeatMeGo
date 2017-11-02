package com.beatme.go;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.app.dao.LeaderBoard;
import java.util.ArrayList;


public class LeaderBoardAdapter extends ArrayAdapter<LeaderBoard> {

    private ArrayList<LeaderBoard> lbData;
    private int lastPosition = -1;
    Context mContext;

    public class PlaceHolder{
        TextView lbnumber;
        TextView lbuname;
        TextView lbuscore;
    }

    public LeaderBoardAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<LeaderBoard> lbList) {
        super(context, resource, lbList);
        this.lbData = lbList;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LeaderBoard dataModel = getItem(position);

        //View v = super.getView(position,convertView,parent);
        View v = convertView;

        PlaceHolder placeHolder;

        if(v == null){
            v = LayoutInflater.from(getContext()).inflate(R.layout.leaderboard_list, parent, false);
            placeHolder = new PlaceHolder();
            placeHolder.lbnumber = (TextView) v.findViewById(R.id.lbnumber);
            placeHolder.lbuname = (TextView) v.findViewById(R.id.lbuname);
            placeHolder.lbuscore = (TextView) v.findViewById(R.id.lbuscore);

            v.setTag(placeHolder);
        } else{
            placeHolder = (PlaceHolder) v.getTag();
        }

        lastPosition = position;
        placeHolder.lbnumber.setText(String.valueOf(dataModel.getUserrank()));
        placeHolder.lbuname.setText(String.valueOf(dataModel.getUsername()));
        placeHolder.lbuscore.setText(String.valueOf(dataModel.getUserscore()));

        if(position % 2 == 0){
            v.setBackgroundResource(R.drawable.lblistview);
        } else{
            v.setBackgroundResource(R.drawable.lblistviewo);
        }

        return v;
    }
}
