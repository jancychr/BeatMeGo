package com.beatme.go;

import android.content.Context;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.MalformedURLException;

public class GameFragment extends Fragment implements OnClickListener {

    private final String TAG = "GameFragment";
    static int[] CLICKABLES = new int[] {
            R.id.uprofbtn,
            R.id.ldrbrdbtn,
            R.id.stingbtn,
            R.id.websrcbtn,
            R.id.mapsrctn,
            R.id.acptduelbtn
    };

    public interface GameListener{
        public String getGameUserName();
        public long getGameUserScore();
        public void onShowUserProfileRequest();
        public void onShowLeaderBoardRequest();
        public void onSettingButtonClicked();
        public void onWebSearchButtonClicked();
        public void onMapSearchButtonClicked() throws MalformedURLException;
        public void onAcceptDuelButtonClicked();
    }

    GameListener mListener = null;
    private View v;
    private Activity mActivity;
    private boolean isUserNameSet = false;
    private boolean isUserScoreSet = false;
    private boolean isSFXON = true;
    public LinearLayout searchScreen = null;
    public TextView gameUserName, userTrophy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_game, container, false);
        for (int i : CLICKABLES) {
            v.findViewById(i).setOnClickListener(this);
        }
        searchScreen = (LinearLayout) v.findViewById(R.id.search_screen);
        gameUserName = (TextView) v.findViewById(R.id.gameUserName);
        userTrophy = (TextView) v.findViewById(R.id.utrophy);

        return v;
    }

    public void setListener(GameListener mListener, boolean isSFXON) {
        this.mListener = mListener;
        this.isSFXON = isSFXON;
    }

    @Override
    public void onStart(){
        super.onStart();
        updateUI();
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("DO NOT CRASH", "OK");
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void updateUI(){
        if(getActivity() == null){ Log.e(TAG, "activity is null"); return;}
        gameUserName.setText(mListener.getGameUserName());
        String score = String.valueOf(mListener.getGameUserScore());
        userTrophy.setText(score);
//        if(!isUserNameSet){
//            TextView gameUserName = (TextView) mActivity.findViewById(R.id.gameUserName);
//            gameUserName.setText(mListener.getGameUserName());
//            isUserNameSet = true;
//        }
//
//        if(!isUserScoreSet){
//            TextView userTrophy = (TextView) mActivity.findViewById(R.id.utrophy);
//            String score = String.valueOf(mListener.getGameUserScore());
//            userTrophy.setText(score);
//            isUserScoreSet = true;
//        }
    }

    public void updateScore(long score){
        userTrophy.setText(String.valueOf(score));
    }

    @Override
    public void onClick(View v) {
        onButtonClick();
        switch (v.getId()){
            case R.id.uprofbtn:
                mListener.onShowUserProfileRequest();
                break;
            case R.id.ldrbrdbtn:
                mListener.onShowLeaderBoardRequest();
                break;
            case R.id.stingbtn:
                mListener.onSettingButtonClicked();
                break;
            case R.id.websrcbtn:
                mListener.onWebSearchButtonClicked();
                break;
            case R.id.mapsrctn:
                try {
                    mListener.onMapSearchButtonClicked();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                break;
            case R.id.acptduelbtn:
                try {
                    mListener.onAcceptDuelButtonClicked();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                break;
        }
    }

    public void onButtonClick(){
//        MediaPlayer mp = MediaPlayer.create(mActivity, R.raw.btnclick);
//        mp.start();
//        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                mp.release();
//            }
//        });
        if (isSFXON){
            getView().playSoundEffect(android.view.SoundEffectConstants.CLICK);
        }
    }

    public void setOnSearchDisable(){
        for (int i: CLICKABLES){
            v.findViewById(i).setClickable(false);
        }
    }

    public void setOnSearchEnable(){
        for (int i: CLICKABLES){
            v.findViewById(i).setClickable(true);
        }
    }
}
