package com.beatme.go;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.*;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.game.listener.IOnDebugListener;
import com.game.listener.IOnExitListener;
import com.game.listener.IOptionsListener;
import com.game.views.*;

import com.game.libs.algo.*;


public class GamePlayFragment
        extends Fragment
        implements
        View.OnClickListener,
        IOnDebugListener,
        IOptionsListener,
        IOnExitListener,
        GameView.GameViewListener{

    private final String TAG = "GamePlayFragment";

    public interface GameListener{
        public void onStartCloseLoading();
        public Map<Integer, ArrayList<String>> getAllPlayers();
        public void sendCoulmnNumberToMain(int colNum, String playerTurn);
        public void notMyMove();
        public void onGameFinishChangeScore(int playerWin);
        public void onGameFinishClick();
        public void sendMyScoreToOpponent();
    }

    GameListener mListener = null;
    private Activity mActivity;
    private boolean isSFXON = true;
    public String mMyID = null;
    public String opponentID = null;
    public String playerTurn = null;

    // calling views
    private TopView tV;
    private GameView gV;
    //private BottomView bV;
    private TextView dV;
    private Button powerButton;
    private boolean compInterrupted = false;
    private LinearLayout win_screen, plyr1box, plyr2box;
    private TextView plyr1name, plyr2name, plyr1trphy, plyr2trphy;
    private Map<Integer, ArrayList<String>> players;
    public int myScoreAt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_play, container, false);
        //tV = (TopView) v.findViewById(R.id.gameTopView);
        win_screen = (LinearLayout) v.findViewById(R.id.win_screen);
        plyr1box = (LinearLayout) v.findViewById(R.id.plyr1box);
        plyr2box = (LinearLayout) v.findViewById(R.id.plyr2box);

        v.findViewById(R.id.winokbtn).setOnClickListener(this);

        plyr1name = (TextView) v.findViewById(R.id.plyr1name);
        plyr2name = (TextView) v.findViewById(R.id.plyr2name);
        plyr1trphy = (TextView) v.findViewById(R.id.plyr1trphy);
        plyr2trphy = (TextView) v.findViewById(R.id.plyr2trphy);

        return v;
    }

    public void setListener(GameListener mListener, boolean isSFXON) {
        this.mListener = mListener;
        this.isSFXON = isSFXON;
    }

    @Override
    public void onStart(){
        super.onStart();
        initGamePlay();
    }

    public void initGamePlay(){
        tV = (TopView) mActivity.findViewById(R.id.gameTopView);
        gV = (GameView) mActivity.findViewById(R.id.gameBoardView);
        //bV = (BottomView) mActivity.findViewById(R.id.gameBottomView);
        dV = (TextView) mActivity.findViewById(R.id.debugText);
        mListener.onStartCloseLoading();
        addListeners();
        startGame();
        gV.setListener(this, isSFXON);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        reloadGame();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("DO NOT CRASH", "OK");
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        Log.e(TAG, "in pause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "in stop");
        super.onStop();
        cleanUp();
    }

    public void reloadGame(){
        if(compInterrupted){
            gV.restart();
        }
    }

    private void addListeners(){
        //bV.setOnNewGameListener(gV);
        //bV.setOnOptionsListener(this);
        gV.setOnTurnChangeListener(tV);
        //gV.setOnBottomListener(bV);
        gV.setOnDebugListener(this);

        ViewTreeObserver vto = gV.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gameInflated();
                ViewTreeObserver obs = gV.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void gameInflated(){
        this.debug("inflated");
        gV.inflated();
    }

    private void startGame(){
        int d = 2;
        int p = 1;
        mListener.sendMyScoreToOpponent();
        this.players = mListener.getAllPlayers();
        setPlayerName(this.players);
        gV.setDepth(AlgoConstants.getDefaultDepth());
        gV.setDifficulty(d);
        gV.setOnExitListener(this);
        gV.setPlayerIDs(this.mMyID, this.opponentID);
        gV.setPlayerTurn(this.playerTurn);
        tV.setNumPlayers(this.players);
    }

    public void setPlayerName(Map<Integer, ArrayList<String>> players){
        if(players.size() == 2){
            ArrayList<String> player1 = players.get(0);
            ArrayList<String> player2 = players.get(1);
            plyr1name.setText(player1.get(0));
            plyr2name.setText(player2.get(0));
            if(player1.get(1) != null){
                plyr1trphy.setText(player1.get(1));
                myScoreAt = 0;
            } else if (player2.get(1) != null){
                plyr2trphy.setText(player2.get(1));
                myScoreAt = 1;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.winokbtn:
                    mListener.onGameFinishClick();
                break;
        }
    }

    @Override
    public void exit() {
    }

    @Override
    public void debug(String s) {
    }

    @Override
    public boolean onOptions(View v) {
        return false;
    }

    private void cleanUp(){
        this.debug("cleanUp");
        if(gV!=null){
            compInterrupted = gV.cleanUp();
        }
    }

    /*
    Game Play Functions
     */
    public void playerMoved(String opponent, int colNum){
        gV.opponentMove(opponent, colNum);
    }

    public void setPlayerIDs(String mMyID, String opponentID){
        this.mMyID = mMyID;
        this.opponentID = opponentID;
    }

    public void setPlayerTurn(String playerTurn){
        this.playerTurn = playerTurn;
    }

    public void setOpponentScore(String score){
        tV.setOpponentScore(score);
    }

    /*
    GameView Functions
     */
    @Override
    public void sendCoulmnNumber(int colNum, String playerTurn){
        mListener.sendCoulmnNumberToMain(colNum, playerTurn);
        this.playerTurn = playerTurn;
    }

    @Override
    public void notMyMove(){
        mListener.notMyMove();
    }

    @Override
    public void openWinScreen(int playerWin){
        if(playerWin != 5){
            mListener.onGameFinishChangeScore(playerWin);

            TextView winResult = new TextView(mActivity);
            winResult.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            winResult.setTextColor(Color.WHITE);
            winResult.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            winResult.setLayoutParams(params);

            switch (playerWin){
                case 0:
                    winResult.setText("Winner!");
                    plyr2box.setAlpha(0.5f);
                    plyr1box.addView(winResult, 0);
                    plyr1trphy.setText("+10 trophies");
                    plyr2trphy.setText("-10 trophies");
                    break;
                case 1:
                    winResult.setText("Winner!");
                    plyr1box.setAlpha(0.5f);
                    plyr2box.addView(winResult, 0);
                    plyr1trphy.setText("-10 trophies");
                    plyr2trphy.setText("+10 trophies");
                    break;
                case 2:
                    winResult.setText("DRAW!");
                    win_screen.addView(winResult, 0);
                    plyr1trphy.setText("0");
                    plyr2trphy.setText("0");
                    break;
            }
            win_screen.setVisibility(View.VISIBLE);
        }
    }
}