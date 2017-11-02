package com.beatme.go;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.app.support.JSONParser;
import org.app.support.WebServiceConfig;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class WebServices {

    private final String TAG = "WebServices";

    public interface WebListener{
        public void onResultSendRequest(int resultID);
        public void onResultFindRequest(ArrayList<String[]> rawData);
        public void onResultDeleteRequest(int resultCode);
    }

    public WebListener mListener;

    JSONParser jsonParser;

    public WebServices(WebListener listener){
        this.mListener = listener;
        jsonParser = new JSONParser();
    }

    public void sendRequest(String playerID, String playerName, double latitude, double longitude) throws MalformedURLException {
        new SendRequestAsyncTask(playerID,playerName,latitude,longitude).execute();
    }

    public void findRequest(String playerID, double latitude, double longitude) throws MalformedURLException {
        new FindRequestAsyncTask(playerID,latitude,longitude).execute();
    }

    public void deleteRequest(int rawid){
        new DeleteRequestAsyncTask(rawid).execute();
    }

    private class SendRequestAsyncTask extends AsyncTask<String, String, InputStream>{

        String dataURL = WebServiceConfig.REQUEST_URL;
        String playerID, playerName;
        Double latitude, longitude;
        int insertID = -1;

        public SendRequestAsyncTask(String playerID, String playerName, double latitude, double longitude) throws MalformedURLException {
            this.playerID = playerID;
            this.playerName = playerName;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected InputStream doInBackground(String... params) {
            try {
                List<NameValuePair> sendData = new ArrayList<NameValuePair>();
                sendData.add(new BasicNameValuePair("request", "sendrequest"));
                sendData.add(new BasicNameValuePair("playerID", playerID));
                sendData.add(new BasicNameValuePair("playerName", playerName));
                sendData.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
                sendData.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));

                JSONObject json = jsonParser.makeHttpRequest(dataURL, "POST", sendData);

                JSONObject responseData = json.getJSONObject("output");

                if(responseData.getInt("error") == 0){
                    insertID = responseData.getInt("id");
                } else {
                    insertID = responseData.getInt("error_code");
                }
            } catch (JSONException e){
                Log.e(TAG, "Send Request: "+e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            mListener.onResultSendRequest(insertID);
        }
    }

    private class FindRequestAsyncTask extends AsyncTask<String, String, InputStream>{

        String dataURL = WebServiceConfig.REQUEST_URL;
        String playerID;
        Double latitude, longitude;
        ArrayList<String[]> fetchedData = new ArrayList<String[]>();

        public FindRequestAsyncTask(String playerID, double latitude, double longitude) throws MalformedURLException {
            this.playerID = playerID;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected InputStream doInBackground(String... params) {
            try {
                List<NameValuePair> sendData = new ArrayList<NameValuePair>();
                sendData.add(new BasicNameValuePair("request", "findrequest"));
                sendData.add(new BasicNameValuePair("playerID", playerID));
                sendData.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
                sendData.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));

                JSONObject json = JSONParser.makeHttpRequest(dataURL, "POST", sendData);

                JSONObject responseData = json.getJSONObject("output");
                if(responseData.getInt("error") == 0){
                    JSONArray playerList = responseData.getJSONArray("data");
                    int length = playerList.length();
                    String[] d1 = new String[2];
                    d1[0] = String.valueOf(11);
                    d1[1] = "no error";
                    fetchedData.add(d1);
                    for (int i=0;i<length;i++){
                        JSONObject rawData = playerList.getJSONObject(i);
                        String[] d = new String[2];
                        d[0] = rawData.getString("playerID");
                        d[1] = rawData.getString("playerName");
                        fetchedData.add(d);
                    }
                } else {
                    String[] d = new String[2];
                    d[0] = String.valueOf(responseData.getInt("error_code"));
                    d[1] = responseData.getString("error_msg");
                    fetchedData.add(d);
                }
            } catch (Exception e){
                Log.e(TAG, "Send Request: "+e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            super.onPostExecute(inputStream);
            mListener.onResultFindRequest(fetchedData);
        }
    }

    private class DeleteRequestAsyncTask extends AsyncTask<Void, Void, Void>{

        String dataURL = WebServiceConfig.REQUEST_URL;
        int rawID;
        int resultCode = -1;

        public DeleteRequestAsyncTask(int rawid){
            this.rawID = rawid;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> sendData = new ArrayList<NameValuePair>();
                sendData.add(new BasicNameValuePair("request", "deleterequest"));
                sendData.add(new BasicNameValuePair("rawid", String.valueOf(rawID)));

                JSONObject json = JSONParser.makeHttpRequest(dataURL, "POST", sendData);

                JSONObject responseData = json.getJSONObject("output");
                if(responseData.getInt("error") == 0){
                    resultCode = responseData.getInt("result");
                } else {
                    resultCode = responseData.getInt("error_code");
                }

            } catch (Exception e){
                Log.e(TAG, "Send Request: "+e.toString());
            }
            mListener.onResultDeleteRequest(resultCode);
            return null;
        }
    }
}
