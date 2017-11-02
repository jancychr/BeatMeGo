package com.beatme.go;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.app.dao.PlayerDao;

import java.net.MalformedURLException;
import java.util.ArrayList;


public class AcceptDuelFragment extends DialogFragment implements View.OnClickListener, WebServices.WebListener, AcceptDuelAdapter.AcceptDuelClickner{

    private final String TAG = "AcceptDuelFragment";

    public interface GameListener{
        public String[] getPlayerDetail();
        public void onAcceptDuelButtonClick(String playerID);
    }

    GameListener mListener = null;
    private Activity mActivity;
    private boolean isSFXON = true;
    private Double latitude, longitude;
    public TextView resultText;
    public String[] playerDetail;
    public WebServices webServices;
    public String resultTxt;
    public ArrayList<PlayerDao> dataList;
    public LinearLayout accptdlloading,accptdllistbox;
    public AcceptDuelAdapter mAdapter;
    public ListView accptdl_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_accept_duel, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        v.findViewById(R.id.close_button).setOnClickListener(this);
        resultText = (TextView) v.findViewById(R.id.resulttext);
        accptdlloading = (LinearLayout) v.findViewById(R.id.accptdlloading);
        accptdllistbox = (LinearLayout) v.findViewById(R.id.accptdllistbox);
        accptdl_list = (ListView) v.findViewById(R.id.accptdl_list);
        v.findViewById(R.id.refreshacptlist).setOnClickListener(this);
        v.findViewById(R.id.close_button).setOnClickListener(this);
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
        dataList = new ArrayList<PlayerDao>();
        mAdapter = new AcceptDuelAdapter(mActivity, 0, dataList);
        mAdapter.setListener(this);
        LocationFetcher locationFetcher = new LocationFetcher(mActivity);
        latitude = locationFetcher.getLatitude();
        longitude = locationFetcher.getLongitude();
        Log.e(TAG, latitude+","+longitude);
        findRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_button:
                 getDialog().dismiss();
                break;
            case R.id.refreshacptlist:
                    dataList.clear();
                    accptdl_list.setAdapter(null);
                    accptdllistbox.setVisibility(View.GONE);
                    accptdlloading.setVisibility(View.VISIBLE);
                    if (latitude != null && longitude != null){
                        try {
                            webServices.findRequest(playerDetail[0], latitude, longitude);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                break;
        }
    }

    public void findRequest() throws MalformedURLException {
        if (latitude != null && longitude != null){
            webServices.findRequest(playerDetail[0], latitude, longitude);
        }
    }

    @Override
    public void onResultSendRequest(int resultID) { }

    @Override
    public void onResultFindRequest(ArrayList<String[]> rawData) {
        if(!rawData.isEmpty()){
            if(Integer.parseInt(rawData.get(0)[0]) != 1001){
                resultText = null;
                for (int i=1;i<rawData.size();i++){
                    String[] data = rawData.get(i);
                    dataList.add(new PlayerDao(data[0], data[1]));
                }
            } else{
                resultTxt = "error code 1001";
            }
        } else {
            resultTxt = "Empty list!";
        }

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (resultText == null && !dataList.isEmpty()){
                    accptdlloading.setVisibility(View.GONE);
                    accptdllistbox.setVisibility(View.VISIBLE);
                    mAdapter.setAdapter(dataList);
                    accptdl_list.setAdapter(mAdapter);
                } else{
                    resultText.setText("No Player found near you.");
                }
            }
        });
    }

    public void closeDialog(){
        getDialog().dismiss();
    }

    @Override
    public void onResultDeleteRequest(int resultCode) {}

    @Override
    public void onAcceptDuelButtonClick(String playerID) {
        mListener.onAcceptDuelButtonClick(playerID);
    }
}
