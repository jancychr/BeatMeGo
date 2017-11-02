package com.beatme.go;

import java.util.*;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.graphics.*;
import android.media.MediaPlayer;
import android.os.*;
import android.support.annotation.NonNull;
import android.util.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

import com.game.libs.gameboard.Players;
import com.game.utils.*;
import com.game.listener.*;
import com.game.libs.gameboard.*;
import com.game.views.Connect4App;
import com.game.views.GameLinesView;
import com.google.android.gms.games.*;

public class GameView
        extends FrameLayout
        implements
        View.OnTouchListener,
        IOnNewGameListener,
        GamePlayListener{

    private final String TAG = "GameView";
    private Resources res;
    private RelativeLayout piecesFrame;
    private GameLinesView winLinesFrame;
    private LayoutInflater lInf;
    private ImageView boardImage;
    private IOnDebugListener dList;
    private IOnExitListener exList;
    private IOnTurnChangeListener turnList;
    private IOnBottomListener bottomList;
    private GameBoard gameBoard = new GameBoard();
    private int columnPlayed;
    private View newPiece;
    private Stack<Point> moveStack = new Stack<Point>();
    private int whoWon;
    private IBoard board = new Board();
    private SharedPreferences settings = this.getContext().getApplicationContext().getSharedPreferences(MainActivity.APPDateOnDevice, 0);
    protected int numPlayers;
    private CompTask processTask;
    private boolean boardEnabled;
    private PowerBall powerBall = new PowerBall();
    private boolean powerPressed;
    public String mMyID = null;
    public String opponentID = null;
    public String playerTurn = null;
    public boolean isSFXon = true;

    public interface GameViewListener{
        public void sendCoulmnNumber(int colNum, String playerTurn);
        public void notMyMove();
        public void openWinScreen(int playerWin);
    }
    public GameViewListener mListener = null;
    public void setListener(GameViewListener listener, boolean isSFXon){
        this.mListener = listener;
        this.isSFXon = isSFXon;
    }

    public GameView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public void setDepth(int d){
        board.setDepth(d);
    }
    public void setNumPlayers(int p){
        numPlayers = p;
    }
    public void setDifficulty(int d){
        board.setDifficulty(d);
    }
    public void setOnTurnChangeListener(IOnTurnChangeListener list){
        turnList = list;
    }
    public void setOnBottomListener(IOnBottomListener list){
        bottomList = list;
    }
    public void setOnExitListener(IOnExitListener list){
        exList = list;
    }

    private int getPieceId(){
        if(board.getPlayersGo()==Players.PLAYER1){
            return R.layout.z_redpiece;
        }
        else{
            return R.layout.z_greenpiece;
        }
    }




    private int getPowerId(){
        return R.layout.z_powerpiece;
    }
    private void addPiece(int i){
        boolean p = powerBall.playNow();
        if(powerPressed){
            p = true;
        }
        MarginLayoutParams mp = new MarginLayoutParams(gameBoard.getPieceDiam(),gameBoard.getPieceDiam());
        if(p){
            newPiece = lInf.inflate(getPowerId(), null);
        }
        else{
            newPiece = lInf.inflate(getPieceId(), null);
        }
        mp.leftMargin  =  gameBoard.getX(i);
        mp.topMargin =    gameBoard.getY(0);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mp);
        piecesFrame.addView(newPiece, params);
        if(p){
            powerBall.setHasBeenPlayed(true);
            powerBall.setJustPlayed( true );
        }
    }
    public void init(AttributeSet attrs, int i){
        this.setWillNotDraw(false);
        res = this.getContext().getApplicationContext().getResources();
        lInf = (LayoutInflater)(getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        lInf.inflate(R.layout.game_play_gameboard, this, true);
    }

    public void inflated(){
        newGame();
    }

    private void checkInitiated(){
        if(boardImage==null || gameBoard.getX(0)==0){
            initBoard();
        }
        this.invalidate();
    }


    //
    public  void newGame(){
        checkInitiated();
        whoWon= Players.NONE;
        board.reset();
        int p=1;
        int t = 0;
        powerBall.setInUse( (setting.getInt(Connect4App.PREFS_PLAY, Players.DIFF_EASY) == Players.POWER_OFF));
        this.setNumPlayers(p);
        this.powerPressed= false;
        moveStack.clear();
        this.enabledBoard(true);
        if(pieceFrame!=null){
            piecesFrame.removeAllViews();
        }
        if(winLinesFrame!=null){
            winLinesFrame.reset();
        }
        if(p==Players.ONE_PLAYER && t==Players.GO_SECOND) {
            computerPlaysFirst();
        }

    }


    //


    public void newGameshow(){
        checkInitiated();
        whoWon = Players.NONE;
        board.reset();
        int p = 1;
        int t = 0;
        powerBall.setInUse(  (settings.getInt(Connect4App.PREFS_POWER, Players.POWER_OFF) == Players.POWER_ON)  );
        this.setNumPlayers(p);
        this.debug("");
        changeTop();
        powerBall.reset();
        this.powerPressed = false;
        moveStack.clear();
        this.enableBoard(true);
        //enableBottomButtons(true);
        if(piecesFrame!=null){
            piecesFrame.removeAllViews();
        }
        if(winLinesFrame!=null){
            winLinesFrame.reset();
        }
        if(p==Players.ONE_PLAYER && t==Players.GO_SECOND ){
            computerPlaysFirst();
        }
    }
    
    private void computerPlaysFirst(){
        this.enableBoard(false);
        enableBottomButtons(false);
        board.alternateTurn();
        changeTop();
        processTask = new CompTask();
        processTask.execute();
    }
    private void initBoard(){
        FrameLayout f = (FrameLayout)(this.getChildAt(0));
        boardImage = (ImageView)f.findViewById(R.id.board);
        piecesFrame = (RelativeLayout)f.findViewById(R.id.piecesframe);
        winLinesFrame = (GameLinesView) f.findViewById(R.id.winlinesframe);
        gameBoard.setBoardDimensions(this.getWidth(), this.getHeight() );
        winLinesFrame.setPieceDiam(gameBoard.getPieceDiam());
        winLinesFrame.setWinListener(this);
    }

    // set players id
    public void setPlayerIDs(String mMyID, String opponentID){
        this.mMyID = mMyID;
        this.opponentID = opponentID;
    }

    // get player turn
    public void setPlayerTurn(String playerTurn){
        this.playerTurn = playerTurn;
    }

    @Override

    public boolean onTouch(View v, MotionEvent e) {
        //get x,y and play piece
        checkInitiated();
        if(this.mMyID == this.playerTurn) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                float xPos = e.getX();
                int colNum = gameBoard.getColForTouch(xPos);
                if (!board.colFull(colNum)) {
                    enableBoard(false);
//                if(numPlayers==Players.ONE_PLAYER){
//                    enableBottomButtons(false);
//                }
                    addPiece(colNum);
                    columnPlayed = colNum;
                    move();
                    this.playerTurn = opponentID;
                    mListener.sendCoulmnNumber(colNum, playerTurn);
                }
            }
        } else {
            mListener.notMyMove();
        }
        return false;
    }




    // other player move function
    public void opponentMove(String opponent, int colNum){
        addPiece(colNum);
        columnPlayed = colNum;
        move();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                playerTurn = mMyID;
                enableBoard(true);
            }
        };
//        this.playerTurn = mMyID;
//        enableBoard(true);
        Handler handler = new android.os.Handler();
        handler.postDelayed(runnable, 500);
    }

    private void debug(String s){
        if(dList!=null){
            dList.debug(s);
        }
    }
    public void setOnDebugListener(IOnDebugListener l){
        dList = l;
    }
    public boolean getBoardEnabled(){
        return boardEnabled;
    }
    public void enableBoard(boolean tf){
        boardEnabled = tf;
        if(tf){
            this.setOnTouchListener(this);
        }
        else{
            this.setOnTouchListener(null);
        }
    }
    private void computerGo(){
        debug("comp task "+board.getNumSpaces());
        processTask = new CompTask();
        processTask.execute();
    }
    public boolean cleanUp(){
        try{
            processTask.cancel(true);
            return true;
        }
        catch(Exception e){

        }
        return false;
    }
    public void restart(){
        computerGo();
    }
    private void dropped(){
        if(numPlayers==Players.ONE_PLAYER && board.getPlayersGo()==Players.PLAYER2){
            enableBottomButtons(true);
        }
        int numSteps = board.getStepsDown(columnPlayed);
        board.pushCol(columnPlayed, powerBall.getJustPlayed());
        moveStack.add(new Point(columnPlayed, numSteps));
        InRowPoints[] wonOther = board.checkWin();
        board.alternateTurn();
        InRowPoints[] wonPlayed = board.checkWin();
        if(wonPlayed!=null && wonOther!=null){
            // both win (using powerball)
            whoWon = Players.POWER_PLAYER;
            drawTwoWinLines(wonPlayed, wonOther);
            board.alternateTurn();
            return;
        }
        else if(wonPlayed!=null){
            whoWon = board.getPlayersGo();
            drawOneWinLine(wonPlayed);
            board.alternateTurn();
            return;
        }
        else if(wonOther!=null){
            board.alternateTurn();
            whoWon = board.getPlayersGo();
            board.alternateTurn();
            drawOneWinLine(wonOther);
            board.alternateTurn();
            return;
        }
        powerBall.setJustPlayed( false );
        board.alternateTurn();
        if(board.getNumSpaces()==0){
            enableBoard(false);
            openWinScreen(2);
            return;
        }
        if(numPlayers==Players.TWO_PLAYERS){
            enableBoard(true);
        }
        else{
            if(board.getPlayersGo()==Players.PLAYER2){
                computerGo();
            }
            else if(board.getPlayersGo()==Players.PLAYER1){
                enableBoard(true);
            }
        }
        changeTop();
    }
    private void changeTop(){
        if(turnList!=null){
            turnList.onChange(this, board.getPlayersGo());
        }
    }
    private void enableBottomButtons(boolean tf){
        if(bottomList!=null){
            bottomList.onChangeBottom(this, tf);
        }
    }
    private InRowPoints[][] convertToXY(InRowPoints[][] w){
        InRowPoints[][] out = new InRowPoints[w.length][w[0].length];
        for(int i=0;i<=w.length-1;i++){
            InRowPoints[] line = w[i];
            for(int j=0;j<=line.length-1;j++){
                out[i][j] = new InRowPoints(gameBoard.getX(line[j].x), gameBoard.getY(line[j].y) );
            }
        }
        return out;
    }
    private void drawTwoWinLines(InRowPoints[] won0, InRowPoints[] won1){
        if(whoWon==Players.PLAYER1){
            playSuccessSound();
        }
        else{
            playFailSound();
        }
        this.enableBottomButtons(false);
        InRowPoints[][] line = {{won0[0], won1[0]}, {won0[1], won1[1]}, {won0[2], won1[2]}, {won0[3], won1[3]}};
        winLinesFrame.draw(convertToXY(line));
    }
    private void drawOneWinLine(InRowPoints[] won){
        if(whoWon==Players.PLAYER1){
            playSuccessSound();
        }
        else{
            playFailSound();
        }
        this.enableBottomButtons(false);
        InRowPoints[][] line = {{won[0]}, {won[1]}, {won[2]}, {won[3]}};
        winLinesFrame.draw(convertToXY(line));
    }
    private void move(){
        int dx = 0;
        final View thisView = this;
        int numSteps = board.getStepsDown(columnPlayed);
        int dy = (int)(numSteps*gameBoard.getRealGapY());
        TranslateAnimation slide = new TranslateAnimation(0,dx,0,dy);
        slide.setInterpolator(new BounceInterpolator());
        slide.setDuration(600);
        slide.setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationStart(Animation anim){

            }
            public void onAnimationRepeat(Animation anim){

            }
            public void onAnimationEnd(Animation anim){
                thisView.postInvalidate();
                dropped();
            }
        });
        slide.setFillAfter(true);
        newPiece.startAnimation(slide);
        if(powerBall.getJustPlayed()){
            playDropPowerSound();
        }
        else{
            playDropSound();
        }
    }
    private void playSuccessSound(){
        playSound(R.raw.chime1);
    }
    private void playFailSound(){
        playSound(R.raw.chime2);
    }
    private void playDropSound(){
        playSound(R.raw.drop);
    }
    private void playDropPowerSound(){
        playSound(R.raw.droppower);
    }
    private void playSound(int i){
        if (isSFXon) {
            MediaPlayer mp = MediaPlayer.create(this.getContext(), i);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        }
    }
    private void openDialog(String s){
        final Dialog dialog = new Dialog(this.getContext(),R.style.MyDialog);
        dialog.setContentView(R.layout.z_gameover);
        TextView tV = (TextView)dialog.findViewById(R.id.gameover_msg);
        tV.setText(s);
        dialog.setCancelable(true);
        final GameView gV = this;
        Button undoButton = (Button) dialog.findViewById(R.id.over_undo);
        ((Button) dialog.findViewById(R.id.over_restart)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame();
                enableBottomButtons(true);
                dialog.dismiss();
            }
        });
        undoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                undo();
                enableBottomButtons(true);
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.over_close)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                enableBottomButtons(true);
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.over_newgame)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                enableBottomButtons(true);
                gV.exitGame();
            }
        });

        dialog.show();

    }
    private void exitGame(){
        exList.exit();
    }
    private void undoTwice(){
        if(moveStack.size()<=1){
            return;
        }
        Point lastMove = moveStack.pop();
        Point prevMove = moveStack.pop();
        board.popCol(lastMove.x);
        board.popCol(prevMove.x);
        piecesFrame.removeViewAt(piecesFrame.getChildCount()-1);
        piecesFrame.removeViewAt(piecesFrame.getChildCount()-1);
    }
    private void undoOnce(){
        if(moveStack.size()<1){
            return;
        }
        Point lastMove = moveStack.pop();
        board.popCol(lastMove.x);
        piecesFrame.removeViewAt(piecesFrame.getChildCount()-1);
    }
    private void undo(){
        this.powerPressed = false;
        debug("undo "+numPlayers+" "+whoWon+" "+board.getPlayersGo());
        if(numPlayers==Players.ONE_PLAYER){
            if(whoWon==Players.POWER_PLAYER){
                // both won - two winning lines using powerball
                if(board.getPlayersGo()==Players.PLAYER2){
                    // comp go (you just played powerball)
                    undoOnce();
                }
                else{
                    // your go (comp just played powerball)
                    undoTwice();
                }
            }
            else if(whoWon == Players.PLAYER1 && board.getPlayersGo() == Players.PLAYER1){
                // you won and it's now your go  - comp played powerball for you
                undoTwice();
            }
            else if(whoWon == Players.PLAYER1 && board.getPlayersGo() == Players.PLAYER2){
                // you won and it's now computers go - you won normally
                undoOnce();
            }
            else if(whoWon == Players.PLAYER2 && board.getPlayersGo() == Players.PLAYER1){
                // computer won and it's now your go - comp won normally
                undoTwice();
            }
            else if(whoWon == Players.PLAYER2 && board.getPlayersGo() == Players.PLAYER2){
                // computer won and it's now the computers go - you played the powerball for him
                undoOnce();
            }
            else{
                // noone won, just undo
                undoTwice();
            }
        }
        else{
            if(board.getPlayersGo()==whoWon){
                undoOnce();
            }
            else{
                undoTwice();
            }
        }
        this.changeTop();
        this.enableBoard(true);
        whoWon = Players.NONE;
    }
    @Override
    public boolean onNewGame(View v) {
        final boolean wasEnabled = this.getBoardEnabled();
        this.enableBoard(false);
        final Dialog dialog = new Dialog(this.getContext(),R.style.MyDialog);
        dialog.setContentView(R.layout.z_restart);
        TextView tV = (TextView)dialog.findViewById(R.id.restart_msg);
        tV.setText(R.string.surerestart);
        dialog.setCancelable(true);


        ((Button) dialog.findViewById(R.id.restart_ok)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                newGame();
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.restart_cancel)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                enableBoard(wasEnabled);
                dialog.dismiss();
            }
        });
        dialog.show();

        return false;
    }

    private void playComputer(InRowPoints p){
        processTask = null;
        debug("played at "+p.x+" type "+PlayTypes.getString(p.y)+" "+board.getNumSpaces());
        columnPlayed = p.x;
        addPiece(columnPlayed);
        move();
    }

    @Override
    public void onGameWinFinished() {
        int s;
        if(numPlayers==Players.TWO_PLAYERS){
            if(whoWon==Players.PLAYER1){
                s = 0;
            }
            else if(whoWon==Players.PLAYER2){
                s = 1;
            }
            else{
                s = 2;
            }
        } else{
            s = 5;
        }
        enableBoard(false);
        openWinScreen(s);
    }

    public void openWinScreen(int playerWin){
        mListener.openWinScreen(playerWin);
    }

    private class CompTask extends AsyncTask<Void, Void, InRowPoints> {
        @Override
        protected InRowPoints doInBackground(Void... v) {
            InRowPoints c = board.getBestPlay();
            return c;
        }
        @Override
        protected void onPostExecute(InRowPoints result) {
            try{
                playComputer(result);
            }
            catch(Exception e){

            }
        }

    }
    private void drawGrid(Canvas c){
        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setColor(Color.WHITE);
        for(int i=0;i<=Board.NUMX-1;i++){
            for(int j=0;j<=Board.NUMY-1;j++){
                int tlx = gameBoard.getX(i);
                int tly = gameBoard.getY(j);
                int r = (int)(gameBoard.getPieceDiam()/2);
                c.drawCircle(tlx+r, tly+r, r-4, paint);
            }
        }
    }
    @Override
    protected void onDraw(Canvas c){
        super.onDraw(c);
        if(Connect4App.DEBUG){
            drawGrid(c);
        }

    }
    public void powerPressed() {
        powerPressed = true;
    }
}
