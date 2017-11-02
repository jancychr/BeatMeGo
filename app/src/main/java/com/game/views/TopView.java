package com.game.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beatme.go.R;
import com.game.listener.IOnTurnChangeListener;
import com.game.libs.gameboard.*;

import java.util.ArrayList;
import java.util.Map;

public class TopView extends LinearLayout implements IOnTurnChangeListener{

    private Resources res;
    public TextView pn1, pt1, pn2, pt2;
    public LinearLayout p1box, p2box;
    public int turn = Players.PLAYER1;
    public int myScoreAt;

    public TopView(Context context) {
        super(context);
        init(null, 0);
    }

    public TopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    // set top view layout to this view
    private void inflate(){
        LayoutInflater linf = (LayoutInflater)(getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        linf.inflate(R.layout.game_play_topview, this);
    }

    private void init(AttributeSet attrs, int defStyle) {
        inflate();
        res = getResources();
        this.setWillNotDraw(false);
        pn1 = (TextView) this.findViewById(R.id.player1name);
        pt1 = (TextView) this.findViewById(R.id.player1trophy);
        pn2 = (TextView) this.findViewById(R.id.player2name);
        pt2 = (TextView) this.findViewById(R.id.player2trophy);

        p1box = (LinearLayout) this.findViewById(R.id.player1box);
        p2box = (LinearLayout) this.findViewById(R.id.player2box);
    }

    private int dpToPix(float f){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, f, res.getDisplayMetrics());
    }

    public void setTurn(int i){
        turn = i;
        this.postInvalidate();
    }

    public void setNumPlayers(Map<Integer, ArrayList<String>> players){
        if(players.size() == 2){
            ArrayList<String> player1 = players.get(0);
            ArrayList<String> player2 = players.get(1);
            pn1.setText(player1.get(0));
            pn2.setText(player2.get(0));
            if(player1.get(1) != null){
                pt1.setText(player1.get(1));
                myScoreAt = 0;
            } else if (player2.get(1) != null){
                pt2.setText(player2.get(1));
                myScoreAt = 1;
            }
        }
    }

    public void setOpponentScore(String score){
        if(myScoreAt == 0){
            pt2.setText(score);
        } else{
            pt1.setText(score);
        }
    }

    @Override
    protected void onDraw(Canvas c) {

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(3, Color.BLACK);
        drawable.setCornerRadius(8);
        drawable.setColor(res.getColor(R.color.wood_dark));
        try{
            if(turn==Players.PLAYER1){
                p2box.setBackgroundResource(0);
                p1box.setBackground(drawable);
            }
            else{
                p1box.setBackgroundResource(0);
                p2box.setBackground(drawable);
            }
        }
        catch(Exception e){
            // not there yet
        }
        super.onDraw(c);
    }

    @Override
    public boolean onChange(View v, int t) {
        turn = t;
        this.postInvalidate();
        return false;
    }
}
