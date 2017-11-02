package com.game.views;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.game.libs.gameboard.InRowPoints;
import com.game.listener.GamePlayListener;

import com.beatme.go.R;

public class GameLinesView extends RelativeLayout {

    private int pieceDiam;
    private GamePlayListener winListener;

    public GameLinesView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameLinesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameLinesView(Context context, AttributeSet attrs, int ds) {
        super(context, attrs, ds);
        init(attrs, 0);
    }

    private void init(AttributeSet attrs, int defStyle) {
    }

    public void setPieceDiam(int d){
        pieceDiam = d;
    }

    public void setWinListener(GamePlayListener g){
        this.winListener = g;
    }

    private void decorFinished(){
        reset();
        if(winListener!=null){
            winListener.onGameWinFinished();
        }
    }

    public void reset(){
        removeAllViews();
    }

    public void draw(InRowPoints[][] line){
        for(int i=0;i<=line.length-1;i++){
            InRowPoints[] level = line[i];
            for(int j=0;j<=level.length-1;j++){
                makeWin(level[j],i);
            }
        }
        decorWait();
    }

    private void decorWait(){
        Timer t = new Timer();
        final Handler handler = new Handler();
        TimerTask endDecorTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        decorFinished();
                    }
                });
            }};
        t.schedule(endDecorTask, 2000);
    }

    private void makeWin(InRowPoints p, int n){
        MarginLayoutParams mp = new MarginLayoutParams(pieceDiam,pieceDiam);
        mp.leftMargin  =  p.x;
        mp.topMargin =    p.y;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mp);
        ImageView im = new ImageView(this.getContext());
        im.setScaleType(ScaleType.FIT_CENTER);
        im.setBackgroundResource(getAnimationId(n));
        addView(im, params);
        ((AnimationDrawable)(im.getBackground())).start();
    }

    private int getAnimationId(int i){
        if(i==0){
            return R.drawable.animated_decor0;
        }
        else if(i==1){
            return R.drawable.animated_decor1;
        }
        else if(i==2){
            return R.drawable.animated_decor2;
        }
        else{
            return R.drawable.animated_decor3;
        }
    }
}
