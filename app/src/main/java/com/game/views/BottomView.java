package com.game.views;

import com.beatme.go.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.game.listener.*;

public class BottomView extends LinearLayout implements View.OnClickListener, IOnBottomListener{

    private IOnNewGameListener newList;
    private IOptionsListener optionsList;
    private Button undoButton;
    private Button restartButton;
    private boolean buttonsEnabled = true;
    public BottomView(Context c, AttributeSet a) {
        super(c,a);
        init();
    }
    public void setOnNewGameListener(IOnNewGameListener li){
        newList = li;
    }
    public void setOnOptionsListener(IOptionsListener li){
        optionsList = li;
    }
    public BottomView(Context c){
        super(c);
        init();
    }
    public boolean onChangeBottom(View v, boolean tf){
        buttonsEnabled = tf;
        return true;
    }
    private void inflate(){
        LayoutInflater linf = (LayoutInflater)(getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        linf.inflate(R.layout.game_play_bottomview, this, true);

    }
    private void init(){
        inflate();
        undoButton = (Button)this.findViewById(R.id.undo_button);
        restartButton = (Button)this.findViewById(R.id.new_button);
        restartButton.setOnClickListener(this);
        undoButton.setOnClickListener(this);
        this.findViewById(R.id.options_button).setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.new_button && newList!=null && this.buttonsEnabled){
            newList.onNewGame(v);
        }
        else if(v.getId()==R.id.options_button && optionsList!=null){
            optionsList.onOptions(v);
        }

    }
}
