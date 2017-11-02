package com.beatme.go;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.app.dao.LeaderBoard;

import java.util.ArrayList;

public class LeaderBoardFragment extends DialogFragment implements OnClickListener{

    private final String TAG = "LeaderBoradFragment";

    public interface GameListener{ }

    GameListener mListener = null;
    private Activity mActivity;
    private boolean isSFXON = true;
    private ListView leaderboardList;
    public LinearLayout lbloading;
//    public LeaderBoardFragment(AppDialogInterface appDialogInterface){
//        this.appDialogInterface = appDialogInterface;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_leader_board, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        v.findViewById(R.id.close_button).setOnClickListener(this);
        lbloading = (LinearLayout) v.findViewById(R.id.leaderboard_loading);
        leaderboardList = (ListView) v.findViewById(R.id.leaderboard_list);
        return v;
    }

    public void setListener(GameListener mListener, boolean isSFXON) {
        this.mListener = mListener;
        this.isSFXON = isSFXON;
    }

    @Override
    public void onStart(){
        super.onStart();

        Window dialogwindows = getDialog().getWindow();

        Point size = new Point();
        Display display = dialogwindows.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int height = size.y;

        dialogwindows.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialogwindows.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height*0.8));
        //updateUI();
    }

//    public void onAttach(Activity activity){
//        super.onAttach(activity);
//        mActivity = activity;
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void updateLeaderBoard(ArrayList<LeaderBoard> lbList){
        if(mActivity != null){
            lbloading.setVisibility(View.GONE);
            leaderboardList.setVisibility(View.VISIBLE);
            LeaderBoardAdapter adapter = new LeaderBoardAdapter(mActivity,0,lbList);
            leaderboardList.setAdapter(adapter);
//            float height = getResources().getDimension(R.dimen.lbh);
//            RelativeLayout.LayoutParams mParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)height);
//            leaderboardList.setLayoutParams(mParam);
        } else{
            Log.e(TAG, "Context is null");
        }
    }

    @Override
    public void onClick(View v) {
        onButtonClick();
        switch (v.getId()) {
            case R.id.close_button:
                getDialog().dismiss();
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
}
