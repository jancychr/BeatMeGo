package com.beatme.go;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class HomeFragment extends Fragment implements OnClickListener{

    // UI widgets declaration
    //ProgressBar pb_f;
    //TextView pbS;

    public interface GameListener{
        public void onHomeGoogleSignInClicked();
    }

    GameListener mListener;
    private View v;
    private Activity mActivity;
    public boolean isSignedIn = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);
        //pb_f = getActivity().findViewById(R.id.pb_f);
        //pbS = getActivity().findViewById(R.id.pbS);
        return v;
    }

    @Override
    public void onStart(){
        super.onStart();
        //new GetUserDataFromGPGS().execute();
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

    @Override
    public void onResume(){
        super.onResume();
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

    public void setListener(GameListener listener){
        mListener = listener;
    }

    public void setSignedIn(boolean showSignIn){
        this.isSignedIn = showSignIn;
        if(this.isSignedIn){
            v.findViewById(R.id.singInContainer).setVisibility(View.GONE);
            v.findViewById(R.id.appLoading).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sign_in_button){
            mListener.onHomeGoogleSignInClicked();
        }
    }

    // show progressbar and after that calls HomeScreen
    /*public class GetUserDataFromGPGS extends AsyncTask<Void, Integer, Void> {

        int dataProgress;

        @Override
        protected void onPreExecute() {
            dataProgress = 0;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while(dataProgress<100){
                dataProgress++;
                publishProgress(dataProgress);
                SystemClock.sleep(20);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pb_f.setProgress(values[0]);
            pb_f.setSecondaryProgress(values[0] + 1);
            pbS.setText(String.valueOf(dataProgress)+"%");
        }

        @Override
        protected void onPostExecute(Void result) {
            Intent gameOverload = new Intent(MainActivity.this, GameActivity.class);
            //finish();
            startActivity(gameOverload);
        }
    }*/
}