package com.game.views;

/**
 * Created by patel on 7/18/2017.
 */

public class GameViewOldCode {
    //    public void setDepth(int d){
//        board.setDepth(d);
//    }
//    public void setNumPlayers(int p){
//        numPlayers = p;
//    }
//    public void setDifficulty(int d){
//        board.setDifficulty(d);
//    }
//    public void setOnTurnChangeListener(IOnTurnChangeListener list){
//        turnList = list;
//    }
//    public void setOnBottomListener(IOnBottomListener list){
//        bottomList = list;
//    }
//    public void setOnExitListener(IOnExitListener list){
//        exList = list;
//    }
//
//    // set top view layout to this view
//    private void init(AttributeSet attrs, int defStyle) {
//        this.setWillNotDraw(false);
//        res = getResources();
//        lInf = (LayoutInflater)(getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
//        lInf.inflate(R.layout.game_play_gameboard, this);
//    }
//
//    private void checkInitiated(){
//        if(boardImage==null || gameBoard.getX(0)==0){
//            initBoard();
//        }
//        this.invalidate();
//    }
//    private void initBoard(){
//        FrameLayout f = (FrameLayout)(this.getChildAt(0));
//        boardImage = (ImageView)f.findViewById(R.id.board);
//        piecesFrame = (RelativeLayout)f.findViewById(R.id.piecesframe);
//        winLinesFrame = (GameLinesView) f.findViewById(R.id.winlinesframe);
//        gameBoard.setBoardDimensions(this.getWidth(), this.getHeight() );
//        winLinesFrame.setPieceDiam(gameBoard.getPieceDiam());
//        winLinesFrame.setWinListener(this);
//    }
//
//    public void newGame(){
//        checkInitiated();
//        whoWon = Players.NONE;
//        board.reset();
//        int p = settings.getInt("player", Players.ONE_PLAYER);
//        int t = settings.getInt("turn", Players.GO_FIRST);
//        powerBall.setInUse(  (settings.getInt("power", Players.POWER_OFF) == Players.POWER_ON)  );
//        this.setNumPlayers(p);
//        this.debug("");
//        changeTop();
//        powerBall.reset();
//        this.powerPressed = false;
//        moveStack.clear();
//        this.enableBoard(true);
//        enableBottomButtons(true);
//        if(piecesFrame!=null){
//            piecesFrame.removeAllViews();
//        }
//        if(winLinesFrame!=null){
//            winLinesFrame.reset();
//        }
//    }
//    private void debug(String s){
//        if(dList!=null){
//            dList.debug(s);
//        }
//    }
//    private void changeTop(){
//        if(turnList!=null){
//            turnList.onChange(this, board.getPlayersGo());
//        }
//    }
//    public void enableBoard(boolean tf){
//        boardEnabled = tf;
//        if(tf){
//            this.setOnTouchListener(this);
//        }
//        else{
//            this.setOnTouchListener(null);
//        }
//    }
//    private void enableBottomButtons(boolean tf){
//        if(bottomList!=null){
//            bottomList.onChangeBottom(this, tf);
//        }
//    }
//
//    private int dpToPix(float f){
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, f, res.getDisplayMetrics());
//    }
//
//    // get coins
//    private int getPieceId(){
//        if(board.getPlayersGo()==Players.PLAYER1){
//            return R.layout.z_redpiece;
//        }
//        else{
//            return R.layout.z_greenpiece;
//        }
//    }
//    private int getPowerId(){
//        return R.layout.z_powerpiece;
//    }
//
//    // add coins on slot
//    private void addPiece(int i){
//        boolean p = powerBall.playNow();
//        if(powerPressed){
//            p = true;
//        }
//        MarginLayoutParams mp = new MarginLayoutParams(gameBoard.getPieceDiam(),gameBoard.getPieceDiam());
//        if(p){
//            newPiece = lInf.inflate(getPowerId(), null);
//        }
//        else{
//            newPiece = lInf.inflate(getPieceId(), null);
//        }
//        mp.leftMargin  =  gameBoard.getX(i);
//        mp.topMargin =    gameBoard.getY(0);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mp);
//        piecesFrame.addView(newPiece, params);
//        if(p){
//            powerBall.setHasBeenPlayed(true);
//            powerBall.setJustPlayed( true );
//        }
//    }
//
//    @Override
//    public boolean onTouch(View v, MotionEvent e) {
//        checkInitiated();
//        if(e.getAction()==MotionEvent.ACTION_DOWN){
//            float xPos = e.getX();
//            int colNum = gameBoard.getColForTouch(xPos);
//            if(!board.colFull(colNum)){
//                enableBoard(false);
//                if(numPlayers==Players.ONE_PLAYER){
//                    enableBottomButtons(false);
//                }
//                addPiece(colNum);
//                columnPlayed = colNum;
//                move();
//            }
//        }
//        return false;
//    }
//    private void move(){
//        int dx = 0;
//        final View thisView = this;
//        int numSteps = board.getStepsDown(columnPlayed);
//        int dy = (int)(numSteps*gameBoard.getRealGapY());
//        TranslateAnimation slide = new TranslateAnimation(0,dx,0,dy);
//        slide.setInterpolator(new BounceInterpolator());
//        slide.setDuration(600);
//        slide.setAnimationListener(new Animation.AnimationListener(){
//            public void onAnimationStart(Animation anim){
//
//            }
//            public void onAnimationRepeat(Animation anim){
//
//            }
//            public void onAnimationEnd(Animation anim){
//                thisView.postInvalidate();
//                dropped();
//            }
//        });
//        slide.setFillAfter(true);
//        newPiece.startAnimation(slide);
//        if(powerBall.getJustPlayed()){
//            playDropPowerSound();
//        }
//        else{
//            playDropSound();
//        }
//    }
//    private void dropped(){
//        if(numPlayers==Players.ONE_PLAYER && board.getPlayersGo()==Players.PLAYER2){
//            enableBottomButtons(true);
//        }
//        int numSteps = board.getStepsDown(columnPlayed);
//        board.pushCol(columnPlayed, powerBall.getJustPlayed());
//        moveStack.add(new Point(columnPlayed, numSteps));
//        InRowPoints[] wonOther = board.checkWin();
//        board.alternateTurn();
//        InRowPoints[] wonPlayed = board.checkWin();
//        if(wonPlayed!=null && wonOther!=null){
//            // both win (using powerball)
//            whoWon = Players.POWER_PLAYER;
//            drawTwoWinLines(wonPlayed, wonOther);
//            board.alternateTurn();
//            return;
//        }
//        else if(wonPlayed!=null){
//            whoWon = board.getPlayersGo();
//            drawOneWinLine(wonPlayed);
//            board.alternateTurn();
//            return;
//        }
//        else if(wonOther!=null){
//            board.alternateTurn();
//            whoWon = board.getPlayersGo();
//            board.alternateTurn();
//            drawOneWinLine(wonOther);
//            board.alternateTurn();
//            return;
//        }
//        powerBall.setJustPlayed( false );
//        board.alternateTurn();
//        if(board.getNumSpaces()==0){
//            openDialog(res.getString(R.string.msg_draw));
//            return;
//        }
//        if(numPlayers==Players.TWO_PLAYERS){
//            enableBoard(true);
//        }
//        else{
//            if(board.getPlayersGo()==Players.PLAYER2){
//                computerGo();
//            }
//            else if(board.getPlayersGo()==Players.PLAYER1){
//                enableBoard(true);
//            }
//        }
//        changeTop();
//    }
//    private void drawTwoWinLines(InRowPoints[] won0, InRowPoints[] won1){
//        if(whoWon==Players.PLAYER1){
//            playSuccessSound();
//        }
//        else{
//            playFailSound();
//        }
//        this.enableBottomButtons(false);
//        InRowPoints[][] line = {{won0[0], won1[0]}, {won0[1], won1[1]}, {won0[2], won1[2]}, {won0[3], won1[3]}};
//        winLinesFrame.draw(convertToXY(line));
//    }
//    private void drawOneWinLine(InRowPoints[] won){
//        if(whoWon==Players.PLAYER1){
//            playSuccessSound();
//        }
//        else{
//            playFailSound();
//        }
//        this.enableBottomButtons(false);
//        InRowPoints[][] line = {{won[0]}, {won[1]}, {won[2]}, {won[3]}};
//        winLinesFrame.draw(convertToXY(line));
//    }
//    private InRowPoints[][] convertToXY(InRowPoints[][] w){
//        InRowPoints[][] out = new InRowPoints[w.length][w[0].length];
//        for(int i=0;i<=w.length-1;i++){
//            InRowPoints[] line = w[i];
//            for(int j=0;j<=line.length-1;j++){
//                out[i][j] = new InRowPoints(gameBoard.getX(line[j].x), gameBoard.getY(line[j].y) );
//            }
//        }
//        return out;
//    }
//
//    @Override
//    public boolean onNewGame(View v) {
//        return false;
//    }
//    private void computerGo(){
//        debug("comp task "+board.getNumSpaces());
//        processTask = new CompTask();
//        processTask.execute();
//    }
//
//    @Override
//    public void onGameWinFinished() {
//
//    }
//
//    // open result dialog
//    private void openDialog(String s){
//        final Dialog dialog = new Dialog(this.getContext(),R.style.MyDialog);
//        dialog.setContentView(R.layout.z_gameover);
//        TextView tV = (TextView)dialog.findViewById(R.id.gameover_msg);
//        tV.setText(s);
//        dialog.setCancelable(true);
//        final GameView gV = this;
//        ((Button) dialog.findViewById(R.id.over_restart)).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                newGame();
//                enableBottomButtons(true);
//                dialog.dismiss();
//            }
//        });
//        ((Button) dialog.findViewById(R.id.over_close)).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                enableBottomButtons(true);
//                dialog.dismiss();
//            }
//        });
//        ((Button) dialog.findViewById(R.id.over_newgame)).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                enableBottomButtons(true);
//                gV.exitGame();
//            }
//        });
//        dialog.show();
//    }
//    private void exitGame(){
//        exList.exit();
//    }
//
//    private class CompTask extends AsyncTask<Void, Void, InRowPoints> {
//        @Override
//        protected InRowPoints doInBackground(Void... v) {
//            InRowPoints c = board.getBestPlay();
//            return c;
//        }
//        @Override
//        protected void onPostExecute(InRowPoints result) {
//            try{
//                playComputer(result);
//            }
//            catch(Exception e){
//
//            }
//        }
//    }
//    private void playComputer(InRowPoints p){
//        processTask = null;
//        debug("played at "+p.x+" type "+PlayTypes.getString(p.y)+" "+board.getNumSpaces());
//        columnPlayed = p.x;
//        addPiece(columnPlayed);
//        move();
//    }
//
//    // play sfx
//    private void playSuccessSound(){
//        playSound(R.raw.chime1);
//    }
//    private void playFailSound(){
//        playSound(R.raw.chime2);
//    }
//    private void playDropSound(){
//        playSound(R.raw.drop);
//    }
//    private void playDropPowerSound(){
//        playSound(R.raw.droppower);
//    }
//    private void playSound(int i){
//        MediaPlayer mp = MediaPlayer.create(this.getContext(), i);
//        if(mp.isPlaying()){
//            mp.stop();
//        }
//        mp.start();
//        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                mp.release();
//            }
//        });
//    }
}
