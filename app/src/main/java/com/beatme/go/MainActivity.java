package com.beatme.go;

import java.net.MalformedURLException;
import java.util.*;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;

import org.app.dao.LeaderBoard;
import org.app.support.ConnectivityReceiver;
import org.app.support.InternetApp;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardScoreBuffer;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ConnectivityReceiver.ConnectivityReceiverListener,
        RoomUpdateListener,
        RealTimeMessageReceivedListener,
        RoomStatusUpdateListener,
        HomeFragment.GameListener,
        GameFragment.GameListener,
        LeaderBoardFragment.GameListener,
        GamePlayFragment.GameListener,
        MapSearchFragment.GameListener,
        AcceptDuelFragment.GameListener{

    // All Fragments
    HomeFragment mHomeFragment;
    GameFragment mGameFragment;
    LeaderBoardFragment mLeaderBoradFragment;
    //UserProfileFragment mUserProfileFragment;
    //AppSettingsFragment mAppSettingsFragment;
    GamePlayFragment mGamePlayFragment;
    MapSearchFragment mMapSearchFragment;
    AcceptDuelFragment mAcceptDuelFragment;

    // Default string declaration
    public static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;
    final static int LOCATION_REQUEST = 1340;
    private boolean isSFXON = true;

    // Google SingIn variable declaration
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingConnectionFailure = false;
    private boolean mSignInClicked = false;
    private boolean mAutoStartSignInFlow = true;
    //private boolean mFirstLogin = false;
    Player mPlayer;
    // Google User Datas
    private String userGoogleName;
    private long userTrophy;
    public ArrayList<String> allPlayers;
    public ArrayList<Participant> allParticipants;
    public Room mRoom;
    public String mRoomID;
    public String mMyID;
    public String opponentID;
    public String playerTurn;


    //Shared Preference variables
    public static final String APPDateOnDevice = "BeatMeGoDeviceData";
    //public static final String isFirstGoogleLogin = "IsFirstGoogleLogin";
    public static final String isSFXONcheck = "IsSFXONcheck";
    SharedPreferences sharedPreferences;

    // UI widgets declaration

    // Other Local Variables
    Handler handler;
    Runnable runnable;
    int isGameBackPressActive = 0;
    int mapSearchRequestID;
    public boolean isPlayingGameBackPressed = false;
    public boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initialize GoogleAplClient to get access
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        // create Fragments
        mHomeFragment = new HomeFragment();
        mGameFragment = new GameFragment();
        mLeaderBoradFragment = new LeaderBoardFragment();
        //mUserProfileFragment = new UserProfileFragment();
        //mAppSettingsFragment = new AppSettingsFragment();
        mGamePlayFragment = new GamePlayFragment();
        mMapSearchFragment = new MapSearchFragment();
        mAcceptDuelFragment = new AcceptDuelFragment();

        // listen from fragments
        mHomeFragment.setListener(this);
        mGameFragment.setListener(this, isSFXON);
        mLeaderBoradFragment.setListener(this, isSFXON);
        //mUserProfileFragment.setListener(this, isSFXON);
        //mAppSettingsFragment.setListener(this, isSFXON);
        mGamePlayFragment.setListener(this, isSFXON);
        mMapSearchFragment.setListener(this, isSFXON);
        mAcceptDuelFragment.setListener(this, isSFXON);

        // call home fragment
        getSupportFragmentManager().beginTransaction().add(R.id.game_container,
                mHomeFragment).commit();

        // SharedPreferences to get data stored on device
        sharedPreferences = getSharedPreferences(APPDateOnDevice, Context.MODE_PRIVATE);
        //checkSharedData(0); //check to reload app, doesn't required now
        checkSharedData(3);
    }

    @Override
    public void onDestroy(){
        handler.removeCallbacks(runnable);
        super.onDestroy();
    }

    // on back press, quit app asking user
    @Override
    public void onBackPressed() {
        if(isGameBackPressActive == 1){
            final Dialog quitGame = new Dialog(this);
            quitGame.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            quitGame.setContentView(R.layout.game_quit);
            quitGame.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ImageButton clsDialog = (ImageButton) quitGame.findViewById(R.id.close_button);
            Button btnCancel, btnQuit;
            btnCancel = (Button) quitGame.findViewById(R.id.btn_cancel);
            btnQuit = (Button) quitGame.findViewById(R.id.btn_quit);

            clsDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quitGame.dismiss();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quitGame.dismiss();
                }
            });
            btnQuit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quitGame.dismiss();
                    isPlaying = false;
                    isPlayingGameBackPressed = true;
                    leaveGameRoom(true);
                }
            });

            quitGame.show();

        } else if(isGameBackPressActive == 0){
            final Dialog quitDialog = new Dialog(this);
            quitDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            quitDialog.setContentView(R.layout.activity_quitapp);
            quitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ImageButton clsDialog = (ImageButton) quitDialog.findViewById(R.id.close_button);
            Button btnCancel, btnQuit;
            btnCancel = (Button) quitDialog.findViewById(R.id.btn_cancel);
            btnQuit = (Button) quitDialog.findViewById(R.id.btn_quit);

            clsDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quitDialog.dismiss();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quitDialog.dismiss();
                }
            });
            btnQuit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            quitDialog.show();
        } else if(isGameBackPressActive == 2){

        }
    }

    // switch fragment layouts
    void switchToFragment(Fragment newFrag) {
        String fragmentName = newFrag.getClass().getName();
        FragmentManager manager = getSupportFragmentManager();
//        boolean fragPoped = manager.popBackStackImmediate(fragmentName, 0);
//        if (!fragPoped) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(fragmentName);
        if(f == null) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
            ft.replace(R.id.game_container, newFrag);
            ft.addToBackStack(fragmentName);
            ft.commit();
        }
//        } else{
//            Log.e(TAG, "Poped: "+fragmentName);
//        }
    }

    void backToLastFragment(){
        Log.e(TAG, "Stack: "+getSupportFragmentManager().getBackStackEntryCount());
        List<Fragment> list = getSupportFragmentManager().getFragments();
        String r = null;
        for(Fragment f: list){
            r += f.getClass().getName();
        }
        Log.e(TAG,"Fragments: "+r);
        if(getSupportFragmentManager().getBackStackEntryCount() > 1){
            getSupportFragmentManager().popBackStack();
        }
    }

    // open dialog fragment layouts
    void openDialogFragment(DialogFragment newDialogFrag, String tag){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        newDialogFrag.show(ft, tag);
    }

    // Check shared data stored in device
    private void checkSharedData(int CHECK_CODE) {
        switch (CHECK_CODE){
//            case 0:
//                boolean firstLogin = sharedPreferences.getBoolean(isFirstGoogleLogin, true);
//                if (firstLogin) { mFirstLogin = true; checkSharedData(1); }
//                break;
//            case 1: // setting FirstLogin to false
//                sharedPreferences.edit().putBoolean(isFirstGoogleLogin, false).apply();
//                break;
//            case 2: // setting FirstLogin to true
//                sharedPreferences.edit().putBoolean(isFirstGoogleLogin, true).apply();
//                break;
            case 3:
                boolean issfxon = sharedPreferences.getBoolean(isSFXONcheck, true);
                if(!issfxon){ isSFXON = false; }
                break;
            case 4:
                sharedPreferences.edit().putBoolean(isSFXONcheck, true).apply();
                break;
            case 5:
                sharedPreferences.edit().putBoolean(isSFXONcheck, false).apply();
                break;
        }
    }

    public void onButtonClick(){
//        MediaPlayer mp = new MediaPlayer();
//        if(mp.isPlaying()){
//            mp.stop();
//        }
//        try {
//            mp.reset();
//            AssetFileDescriptor afd;
//            afd = getAssets().openFd("sfx/btnclick.wav");
//            mp.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
//            mp.prepare();
//            mp.start();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
        MediaPlayer mp = MediaPlayer.create(this, R.raw.btnclick);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }

    // custom toast
    public void showToast(String text){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.z_toast, (ViewGroup) findViewById(R.id.customtoast));
        TextView errorText = (TextView) layout.findViewById(R.id.toasttext);
        errorText.setText(text);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    // check permissions
    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED == checkSelfPermission(perm));
    }

    /*
     GoogleApiClient Connection - START
    */
    // Runs on applicaiton start and connect with Google Play
    @Override
    protected void onStart() {
        super.onStart();
        //Log.d(TAG, "onStart(): Connecting to Google APIs");
        mGoogleApiClient.connect();
    }

    // Disconnect from Google Play
    @Override
    protected void onStop() {
        super.onStop();
        if(mRoomID != null || mMyID != null){
            leaveGameRoom(true);
        }
        if (mGoogleApiClient.isConnected() && isPlaying == false) {
            Log.e(TAG, "Disconnect Google API");
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        InternetApp.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("DO NOT CRASH", "OK");
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    // check user is connected
    private boolean isSignedIn() {
        return (mGoogleApiClient != null) && (mGoogleApiClient.isConnected());
    }

    // After getting connection from Google Play
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Log.d(TAG, "onConnected(): Connection successful");
//        if(mFirstLogin){
//            mFirstLogin = false;
//            checkSharedData(1);
//            reloadApp();
//        }
        // get player details
        onConnectGetUserData();
        // changing loading page UI & calling home page after 3 seconds
        mHomeFragment.setSignedIn(true);
        // call GameFragment after 2 seconds
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                switchToFragment(mGameFragment);
            }
        };
        handler.postDelayed(runnable,2000);
    }

    public void onConnectGetUserData(){
        mPlayer = Games.Players.getCurrentPlayer(mGoogleApiClient);
        if(mPlayer == null){
            Log.e(TAG, "mPlayer is null");
        } else{
            // usernmae
            userGoogleName = mPlayer.getDisplayName();

            // user trophies
            Games.Leaderboards.loadCurrentPlayerLeaderboardScore(
                    mGoogleApiClient,
                    getString(R.string.leaderboard_id),
                    LeaderboardVariant.TIME_SPAN_ALL_TIME,
                    LeaderboardVariant.COLLECTION_PUBLIC
                ).setResultCallback(
                    new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
                        @Override
                        public void onResult(@NonNull Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult) {
                            if(loadPlayerScoreResult != null
                                    && GamesStatusCodes.STATUS_OK == loadPlayerScoreResult.getStatus().getStatusCode()
                                    && loadPlayerScoreResult.getScore() != null){
                                setUserTrophy(loadPlayerScoreResult.getScore().getRawScore());
                            }
                        }
                    }
            );
        }
    }

    public void setUserTrophy(long score){
        this.userTrophy = score;
        mGameFragment.updateUI();
    }

    // reload MainActivity
    private void reloadApp() {
       Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(i);
    }

    // When connection suspend, tries to connect again
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended():  Trying to reconnect.");
        mGoogleApiClient.connect();
    }

    // When connection failed
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed(): attempting to resolve");
        if (mResolvingConnectionFailure) {
            // Already resolving
            Log.d(TAG, "onConnectionFailed(): ignoring connection failure, already resolving.");
            return;
        }

        // Launch the sign-in flow if the button was clicked or if auto sign-in is enabled
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;

            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult, RC_SIGN_IN,
                    getString(R.string.signin_other_error));
        }
    }

    // to handle the result of Connection Resolution
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode){
            case RC_SIGN_IN:
                    mSignInClicked = false;
                    mResolvingConnectionFailure = false;
                    if (resultCode == RESULT_OK) {
                        mGoogleApiClient.connect();
                    } else {
                        BaseGameUtils.showActivityResultError(this,
                                requestCode, resultCode, R.string.signin_other_error);
                    }
                break;

            case RC_SELECT_PLAYERS:
                    handleSelectPlayerResult(resultCode, intent);
                break;

            case RC_WAITING_ROOM:
                    if(resultCode == Activity.RESULT_OK){
                        // start game function
                    } else if(requestCode == Activity.RESULT_CANCELED){
                        //leaveGameRoom();
                    } else if(requestCode == GamesActivityResultCodes.RESULT_LEFT_ROOM){
                        //leaveGameRoom();
                    }
                break;

            case RC_INVITATION_INBOX:
                handleInvitationInboxResult(resultCode, intent);
                break;
        }
    }

    // handle permission request result
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case LOCATION_REQUEST:
                    openDialogFragment(mMapSearchFragment, "MapSearch");
                break;
        }
    }

    /*
     GoogleApiClient Connection - END
    */

    /*
     Internet Connection Alert - START
      */
    private void checkConnection(){
        boolean isConnected = ConnectivityReceiver.isConnected();
        showInternetPopUp(isConnected);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showInternetPopUp(isConnected);
    }

    // show dialog for internet connection
    private void showInternetPopUp(boolean isConnected) {
        if(!isConnected) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Connection Error")
                    .setMessage("Unable to connect to internet. Please check your connection and ...")
                    .setCancelable(false)
                    .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent homePage = new Intent(Intent.ACTION_MAIN);
                            homePage.addCategory(Intent.CATEGORY_HOME);
                            homePage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(homePage);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    /*
     Internet connection alert - END
      */

    /*
    HomeFragment's methods
     */
    @Override
    public void onHomeGoogleSignInClicked() {
        if(!isSignedIn()){
            mGoogleApiClient.connect();
        }
    }

    /*
    GameFragment's methods
     */
    // get GameUser name
    @Override
    public String getGameUserName(){
        if(mPlayer == null && userGoogleName == null){
            return null;
        }
        return userGoogleName;
    }

    // get GameUser score
    @Override
    public long getGameUserScore(){
        if(mPlayer == null){
            return Long.parseLong(null);
        }
        return userTrophy;
    }

    @Override
    public void onShowUserProfileRequest() {
        //openDialogFragment(mUserProfileFragment, "UserProfile");
        final Dialog profileDialog = new Dialog(this);
        profileDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        profileDialog.setContentView(R.layout.fragment_user_profile);
        profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageButton closeBtn = (ImageButton) profileDialog.findViewById(R.id.close_button);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
                profileDialog.dismiss();
            }
        });

        TextView userName, userTrophy, playerWins, highestTrophy, playerCompeted, localPlayers;
        userName = (TextView) profileDialog.findViewById(R.id.usergooglename);
        userTrophy = (TextView) profileDialog.findViewById(R.id.utrophy);
        playerWins = (TextView) profileDialog.findViewById(R.id.playerwins);
        highestTrophy = (TextView) profileDialog.findViewById(R.id.playerhighesttrophies);
        playerCompeted = (TextView) profileDialog.findViewById(R.id.playercompeted);
        localPlayers = (TextView) profileDialog.findViewById(R.id.playerlocal);

        userName.setText(this.userGoogleName);
        userTrophy.setText(String.valueOf(this.userTrophy));

        profileDialog.show();
    }

    @Override
    public void onShowLeaderBoardRequest() {
        openDialogFragment(mLeaderBoradFragment, "Leaderboard");
        // call GameFragment after 2 seconds
        Games.Leaderboards.loadTopScores(
                mGoogleApiClient,
                getString(R.string.leaderboard_id),
                LeaderboardVariant.TIME_SPAN_ALL_TIME,
                LeaderboardVariant.COLLECTION_PUBLIC,25)
                .setResultCallback(
                        new ResultCallback<Leaderboards.LoadScoresResult>() {
                            @Override
                            public void onResult(@NonNull Leaderboards.LoadScoresResult loadScoresResult) {
                                if(loadScoresResult.getStatus().getStatusCode() == GamesStatusCodes.STATUS_OK
                                        && loadScoresResult != null){
                                    LeaderboardScoreBuffer lbBuffer = loadScoresResult.getScores();
                                    callLeaderBoard(lbBuffer);
                                }
                            }
                        }
                );
//        Handler lbhandler = new Handler();
//        Runnable lbrunnable = new Runnable() {
//            @Override
//            public void run() {
//                callLeaderBoard();
//            }
//        };
//        lbhandler.postDelayed(lbrunnable,500);
    }

    public void callLeaderBoard(LeaderboardScoreBuffer lbBuffer){
        int lbSize = lbBuffer.getCount();
        ArrayList<LeaderBoard> lbList = new ArrayList<LeaderBoard>();
        for(int i=0;i<lbSize;i++){
            LeaderboardScore lbs = lbBuffer.get(i);
            lbList.add(new LeaderBoard(i+1, lbs.getScoreHolderDisplayName(), lbs.getRawScore()));
        }
//        lbList.add(new LeaderBoard(1, "ssdsds", 4526L));
//        lbList.add(new LeaderBoard(2, "rtrtr", 4343L));
//        lbList.add(new LeaderBoard(3, "dfdfdf", 4526L));
//        lbList.add(new LeaderBoard(4, "bfgfgf", 7687L));
//        lbList.add(new LeaderBoard(5, "d fd fd", 4526L));
        mLeaderBoradFragment.updateLeaderBoard(lbList);
    }

    @Override
    public void onSettingButtonClicked() {
        //openDialogFragment(mAppSettingsFragment, "Settings");
        final Dialog settingDialog = new Dialog(this);
        settingDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        settingDialog.setContentView(R.layout.fragment_app_settings);
        settingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageButton closeBtn = (ImageButton) settingDialog.findViewById(R.id.close_button);
        final Button googleSignIn, sfxBtn, creditBtn, helpBtn;
        googleSignIn = (Button) settingDialog.findViewById(R.id.googlesigninbtn);
        sfxBtn = (Button) settingDialog.findViewById(R.id.sfxbutton);
        creditBtn = (Button) settingDialog.findViewById(R.id.creditbutton);
        helpBtn = (Button) settingDialog.findViewById(R.id.helpbutton);

        // when close button clicked
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
                settingDialog.dismiss();
            }
        });
        // when google sing in button clicked
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
                // problem going after diconnecting and connecting. later check it
                if(isSignedIn()){
                    Games.signOut(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    //mFirstLogin = true;
                    googleSignIn.setText("Disconnected");
                    googleSignIn.setBackgroundResource(R.drawable.btnstngd);
                } else{
                    mGoogleApiClient.connect();
                    googleSignIn.setText("Connected");
                    googleSignIn.setBackgroundResource(R.drawable.btnstngc);
                }
            }
        });
        // when sfx button clicked
        sfxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
                if(isSFXON == true){
                    isSFXON = false;
                    sfxBtn.setText("Off");
                    sfxBtn.setBackgroundResource(R.drawable.btnstngd);
                } else{
                    isSFXON = true;
                    sfxBtn.setText("On");
                    sfxBtn.setBackgroundResource(R.drawable.btnstngc);
                }
            }
        });
        // when credit button clicked
        creditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
            }
        });
        // when help button clicked
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
            }
        });

        settingDialog.show();
    }

    @Override
    public void onWebSearchButtonClicked() {
        mRoom = null;

        isGameBackPressActive = 2;
        mGameFragment.setOnSearchDisable();
        mGameFragment.searchScreen.setVisibility(View.VISIBLE);

        Bundle bm = RoomConfig.createAutoMatchCriteria(1, 1, 0);
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setAutoMatchCriteria(bm);
        RoomConfig roomConfig = roomConfigBuilder.build();

        Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        loadingDialog = new Dialog(this);
//        loadingDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        loadingDialog.setContentView(R.layout.z_loading);
//        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        loadingDialog.show();
    }

    private RoomConfig.Builder makeBasicRoomConfigBuilder() {
        return RoomConfig.builder(this)
                .setMessageReceivedListener(this)
                .setRoomStatusUpdateListener(this);
    }
    // handle room
    @Override
    public void onRoomCreated(int i, Room room) {
        if (i != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "Error creating room in onRoom");
            return;
        }
        mRoom = room;
        mRoomID = room.getRoomId();
        Log.e(TAG, "room created with id "+mRoomID);
//        Intent in = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, Integer.MAX_VALUE);
//        startActivityForResult(in, RC_WAITING_ROOM);
    }

    @Override
    public void onJoinedRoom(int i, Room room) {
        if (i != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "Error creating room in onJoin");
            return;
        }
        Log.e(TAG, "room joined");
//        Intent in = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, Integer.MAX_VALUE);
//        startActivityForResult(in, RC_WAITING_ROOM);
    }

    @Override
    public void onLeftRoom(int i, String s) {
        if (i != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "Error creating room in onLEft");
            return;
        }
        Log.e(TAG, "room left successfully");
    }

    @Override
    public void onRoomConnected(int i, Room room) {
        if (i != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "Error creating room in onRConnected");
            return;
        }
        Log.e(TAG, "room connected");
        Handler gHandler = new Handler();
        Runnable gRunnable = new Runnable() {
            @Override
            public void run() {
                isPlaying = true;
                switchToFragment(mGamePlayFragment);
            }
        };
        gHandler.postDelayed(gRunnable,1000);
    }

    @Override
    public void onConnectedToRoom(Room room){
        allPlayers = room.getParticipantIds();
        allParticipants = room.getParticipants();
        mMyID = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));
        if(mRoomID == null){
            mRoomID = room.getRoomId();
        }
        if(allParticipants != null){
            for(Participant p : allParticipants){
                if(p.getParticipantId() != mMyID){
                    this.opponentID = p.getParticipantId();
                }
            }
        }
        Participant p = allParticipants.get(0);
        playerTurn = p.getParticipantId();
        mGamePlayFragment.setPlayerIDs(mMyID, opponentID);
        mGamePlayFragment.setPlayerTurn(playerTurn);
        Log.e(TAG, "Connected to room, Room ID: "+mRoomID+", myID: "+mMyID);
    }

    @Override
    public void onDisconnectedFromRoom(Room room) {
        //mRoomID = null;
        //showGameRoomError();
    }

    public void leaveGameRoom(boolean fromGameBoard){
        if(fromGameBoard){
            Log.e(TAG, "Leaving room");
            if(mRoomID != null) {
                byte[] msgBuf = {(byte) 'Q', (byte) 1};
                sendGoogleGameMessage(msgBuf);
                Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomID);
                //mRoomID = null;
            }
            isGameBackPressActive = 0;
            if (isPlayingGameBackPressed){
                backToLastFragment();
                isPlayingGameBackPressed = false;
            }
        }
    }

    // recieve message from google
    @Override
    public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
        byte[] msgBuf = realTimeMessage.getMessageData();
        String sender = realTimeMessage.getSenderParticipantId();
        switch (msgBuf[0]){
            case 'Q':
                if(1 == (msgBuf[1] & 0xFF)){
                    showToast("Opponent has left the match. You are winner of this match.");
                }
                break;
            case 'M':
                int colNum = msgBuf[1] & 0xFF;
                if(sender != mMyID){
                    mGamePlayFragment.playerMoved(sender, colNum);
                }
                break;
            default:
                String scr = new String(msgBuf);
                Log.e(TAG, scr);
                mGamePlayFragment.setOpponentScore(scr);
                break;
        }
    }

    // short function to send message in google room, variable is byte array
    public void sendGoogleGameMessage(byte[] msgBuf){
        if(allParticipants != null){
            for(Participant p : allParticipants){
                if(p.getParticipantId() != mMyID){
                    Games.RealTimeMultiplayer
                            .sendReliableMessage(
                                mGoogleApiClient,
                                null,
                                msgBuf,
                                mRoomID,
                                p.getParticipantId());
                }
            }
        }
    }

    @Override
    public void onRoomConnecting(Room room) { }

    @Override
    public void onRoomAutoMatching(Room room) { }

    @Override
    public void onPeersConnected(Room room, List<String> list) { }

    @Override
    public void onPeersDisconnected(Room room, List<String> list) { }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> list) { }

    @Override
    public void onPeerDeclined(Room room, List<String> list) { }

    @Override
    public void onPeerJoined(Room room, List<String> list) { }

    @Override
    public void onPeerLeft(Room room, List<String> list) { }

    @Override
    public void onP2PConnected(String s) { }

    @Override
    public void onP2PDisconnected(String s) { }
    // ends

    @Override
    public void onMapSearchButtonClicked(){
        openDialogFragment(mMapSearchFragment, "MapSearch");
        //Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3);
        //startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    public boolean hasAccessOfLocation(){
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    @Override
    public void onAcceptDuelButtonClicked() {
        openDialogFragment(mAcceptDuelFragment, "MapSearch");
//        Intent intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
//        startActivityForResult(intent, RC_INVITATION_INBOX);
    }

    // Other Real-Time Multiplayer functions for onActivityResult
    public void handleSelectPlayerResult(int resultCode, Intent data){
//        Bundle extras = data.getExtras();
//        final ArrayList<String> invitees =
//                data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
//        Log.e(TAG, "hh: "+sdss(invitees));
//        // get auto-match criteria
//        Bundle autoMatchCriteria = null;
//        int minAutoMatchPlayers =
//                data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
//        int maxAutoMatchPlayers =
//                data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
//
//        if (minAutoMatchPlayers > 0) {
//            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
//                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
//        } else {
//            autoMatchCriteria = null;
//        }
//
//        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
//        roomConfigBuilder.addPlayersToInvite(invitees);
//        if (autoMatchCriteria != null) {
//            roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
//        }
//        RoomConfig roomConfig = roomConfigBuilder.build();
//        Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);
//
//        // prevent screen from sleeping during handshake
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public String sdss(ArrayList<String> d){
        String r = null;
        for (String s: d){
            s += s+"\n";
        }
        return r;
    }

    public void handleInvitationInboxResult(int resultCode, Intent data){
//        Bundle extras = data.getExtras();
//        Invitation invitation =
//                extras.getParcelable(Multiplayer.EXTRA_INVITATION);
//
//        // accept it!
//
//        // prevent screen from sleeping during handshake
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /*
    Game Play functions
     */
    @Override
    public void onStartCloseLoading(){
        //loadingDialog.dismiss();
        mGameFragment.searchScreen.setVisibility(View.GONE);
        mGameFragment.setOnSearchEnable();
        isGameBackPressActive = 1;
    }

    @Override
    public Map<Integer, ArrayList<String>> getAllPlayers(){
        Map<Integer, ArrayList<String>> players = new HashMap<Integer, ArrayList<String>>();
        int j = 0;
        for(Participant p : allParticipants){
            ArrayList<String> pl = new ArrayList<String>();
            pl.add(p.getDisplayName());
            if(p.getParticipantId() == mMyID){
                pl.add(String.valueOf(this.userTrophy));
            } else{
                pl.add(null);
            }
            players.put(j, pl);
            j++;
        }
        return players;
    }

    @Override
    public void sendCoulmnNumberToMain(int colNum, String playerTurn){
        byte[] msgBuf = new byte[2];
        msgBuf[0] = 'M';
        msgBuf[1] = (byte)colNum;
        sendGoogleGameMessage(msgBuf);
        this.playerTurn = playerTurn;
    }

    @Override
    public void notMyMove(){
        showToast("Not your turn!!!");
    }

    // update score after match
    @Override
    public void onGameFinishChangeScore(int playerWin){
        isGameBackPressActive = 2;
        if(playerWin != 2){
            Participant p = this.allParticipants.get(playerWin);
            long score = this.userTrophy;
            if(p.getParticipantId() == mMyID){
                score = score + 10;
            } else{
                score = score - 10;
            }
            if(score < 0){
                score = 0;
            }
            Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.leaderboard_id), score);
            this.userTrophy = score;
            mGameFragment.updateScore(score);
        }
    }

    @Override
    public void onGameFinishClick(){
        if(mRoomID != null) {
            Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomID);
            mRoomID = null;
            mMyID = null;
        }
        isPlaying = false;
        isGameBackPressActive = 0;
        backToLastFragment();
    }

    @Override
    public void sendMyScoreToOpponent(){
        byte[] scoreByte = String.valueOf(this.userTrophy).getBytes();
//        byte[] mgBuf = new byte[scoreByte.length + 1];
//        mgBuf[0] = 'S';
//        for (int i=1;i<scoreByte.length;i++){
//            mgBuf[i] = scoreByte[i-1];
//        }
//        Log.e(TAG, mgBuf.toString());
        sendGoogleGameMessage(scoreByte);
    }

    /*
    App Setting functions


    @Override
    public void onSFXButtonClick() { }

    @Override
    public void onGoogleSignInClick() { }

    @Override
    public void onCreditButtonClick() { }

    @Override
    public void onHelpSupportButtonClick() { }
    */

    /*
    *
    * Facing problem after match over, one player can go out, other one cannot click OK
    * */

    /*
     Map Search Functions
     */

    @Override
    public String[] getPlayerDetail(){
        String[] data = new String[2];
        if (mPlayer != null){
            data[0] = mPlayer.getPlayerId();
        } else {
            data[0] = null;
        }
        data[1] = this.userGoogleName;
        return data;
    }

    @Override
    public void onResultSendRequest(int rawID){
        this.mapSearchRequestID = rawID;
    }

    /*
    Accept duel functions
     */
    @Override
    public void onAcceptDuelButtonClick(String playerID){
		
		mRoom = null;

        isGameBackPressActive = 2;
		

        Bundle bm = RoomConfig.createAutoMatchCriteria(1, 1, 0);
        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
        roomConfigBuilder.setAutoMatchCriteria(bm);
        RoomConfig roomConfig = roomConfigBuilder.build();

        Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
//        ArrayList<String> playersL = new ArrayList<String>();
//        playersL.add(playerID);
//        playersL.add(mPlayer.getPlayerId());
//
//        RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();
//        roomConfigBuilder.addPlayersToInvite(playersL);
//
//        RoomConfig roomConfig = roomConfigBuilder.build();
//        Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);
//
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}