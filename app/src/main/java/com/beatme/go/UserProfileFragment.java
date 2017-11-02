package com.beatme.go;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


public class UserProfileFragment extends DialogFragment implements View.OnClickListener {

    private final String TAG = "UserProfileFragment";

    public interface GameListener{ }

    GameListener mListener = null;
    private Activity mActivity;
    private boolean isSFXON = true;

//    public static UserProfileFragment newInstance(String param1, String param2) {
//        UserProfileFragment fragment = new UserProfileFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        v.findViewById(R.id.close_button).setOnClickListener(this);
        return v;
    }

    @Override
    public void onStart(){
        super.onStart();

//        Window dialogwindows = getDialog().getWindow();
//
//        Point size = new Point();
//        Display display = dialogwindows.getWindowManager().getDefaultDisplay();
//        display.getSize(size);
//        int height = size.y;
//
//        dialogwindows.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogwindows.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (height*0.8));
        //updateUI();
    }

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

    public void setListener(GameListener mListener, boolean isSFXON) {
        this.mListener = mListener;
        this.isSFXON = isSFXON;
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
