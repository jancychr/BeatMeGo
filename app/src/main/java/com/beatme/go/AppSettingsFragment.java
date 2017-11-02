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

public class AppSettingsFragment extends DialogFragment implements View.OnClickListener {

    private final String TAG = "UserProfileFragment";
    static int[] CLICKABLES = new int[]{
            R.id.googlesigninbtn,
            R.id.sfxbutton,
            R.id.creditbutton,
            R.id.helpbutton
    };

    public interface GameListener{
        public void onSFXButtonClick();
        public void onGoogleSignInClick();
        public void onCreditButtonClick();
        public void onHelpSupportButtonClick();
    }

    GameListener mListener = null;
    private Activity mActivity;
    private boolean isSFXON = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_app_settings, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        v.findViewById(R.id.close_button).setOnClickListener(this);
        for (int i : CLICKABLES) {
            v.findViewById(i).setOnClickListener(this);
        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
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
            case R.id.googlesigninbtn:
                mListener.onGoogleSignInClick();
                break;
            case R.id.sfxbutton:
                mListener.onSFXButtonClick();
                break;
            case R.id.creditbutton:
                mListener.onCreditButtonClick();
                break;
            case R.id.helpbutton:
                mListener.onHelpSupportButtonClick();
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
