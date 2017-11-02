package com.beatme.go;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.app.dao.PlayerDao;

import java.util.ArrayList;
import java.util.List;

public class AcceptDuelAdapter extends ArrayAdapter<PlayerDao> {

    public interface AcceptDuelClickner{
        public void onAcceptDuelButtonClick(String playerID);
    }

    private ArrayList<PlayerDao> list;
    private int lastPosition = -1;
    private Context mContext;
    public AcceptDuelClickner mListener;
    public PlayerDao dataModel;

    public class PlaceHolder{
        TextView indxno;
        TextView playerName;
        ImageButton acceptBtn;
    }

    public AcceptDuelAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<PlayerDao> objects) {
        super(context, resource, objects);
        this.list = objects;
        this.mContext = context;
    }

    public void setAdapter(ArrayList<PlayerDao> list){
        this.list = list;
    }

    public void setListener(AcceptDuelClickner listener){
        this.mListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        dataModel = getItem(position);

        View v = convertView;

        PlaceHolder placeHolder;
        if (v == null){
            v = LayoutInflater.from(getContext()).inflate(R.layout.player_list, parent, false);
            placeHolder = new PlaceHolder();
            placeHolder.indxno = (TextView) v.findViewById(R.id.indxno);
            placeHolder.playerName = (TextView) v.findViewById(R.id.playeruid);
            placeHolder.acceptBtn = (ImageButton) v.findViewById(R.id.acceptbtn);

            v.setTag(placeHolder);
        } else {
            placeHolder = (PlaceHolder) v.getTag();
        }

        lastPosition = position;
        placeHolder.indxno.setText(String.valueOf(position+1));
        placeHolder.playerName.setText(String.valueOf(dataModel.getPlayerName()));
        placeHolder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAcceptDuelButtonClick(dataModel.getPlayerID());
            }
        });

        if(position % 2 == 0){
            v.setBackgroundResource(R.drawable.lblistview);
        } else{
            v.setBackgroundResource(R.drawable.lblistviewo);
        }

        return v;
    }
}
