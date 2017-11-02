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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.util.ArrayList;

public class MapSearchFragment extends DialogFragment implements View.OnClickListener, WebServices.WebListener{

    private final String TAG = "MapSearchFragment";

    public interface GameListener{
        public String[] getPlayerDetail();
        public void onResultSendRequest(int rawID);
    }

    GameListener mListener = null;
    private Activity mActivity;
    private boolean isSFXON = true;
    private Double latitude, longitude;
    public TextView resultText;
    public String[] playerDetail;
    public WebServices webServices;
    private int rawID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map_search, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        v.findViewById(R.id.close_button).setOnClickListener(this);
        resultText = (TextView) v.findViewById(R.id.result_code);
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
        playerDetail = mListener.getPlayerDetail();
        try {
            initMapData();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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
    }

    public void initMapData() throws MalformedURLException {
        webServices = new WebServices(this);
        LocationFetcher locationFetcher = new LocationFetcher(mActivity);
        latitude = locationFetcher.getLatitude();
        longitude = locationFetcher.getLongitude();
        sendRequest();
    }

    public void sendRequest() throws MalformedURLException {
        if (latitude != null && longitude != null){
            webServices.sendRequest(playerDetail[0],playerDetail[1],latitude,longitude);
        }
    }

    public void deleteRequest(int rawID) throws MalformedURLException{
        webServices.deleteRequest(rawID);
    }

    public void closeDialog(){
        getDialog().dismiss();
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

    /*
    Web Services function
     */
    @Override
    public void onResultSendRequest(int resultID){
        this.rawID = resultID;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultText.setText("Session had been created! Waiting for opponent to accept...");
            }
        });
        mListener.onResultSendRequest(resultID);
    }

    @Override
    public void onResultFindRequest(ArrayList<String[]> rawData){ }

    @Override
    public void onResultDeleteRequest(int resultCode){

    }
}
