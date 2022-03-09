package com.overshade.neutrinogamecenter.Games.game2048;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.overshade.neutrinogamecenter.DatabaseUtils.DatabaseManager;
import com.overshade.neutrinogamecenter.DatabaseUtils.StatsTable;
import com.overshade.neutrinogamecenter.DatabaseUtils.UserInfo;
import com.overshade.neutrinogamecenter.DatabaseUtils.UsersTable;
import com.overshade.neutrinogamecenter.LoginCredentials;
import com.overshade.neutrinogamecenter.Menus.MenuActivity;
import com.overshade.neutrinogamecenter.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class Game2048 extends AppCompatActivity {

    private MediaPlayer soundtrackPlayer;
    private final int[] soundtrackStartPoints = new int[] {
            0,      // 00:00 (Infraction - Almost Evil)
            245000, // 04:00 (Infraction - ID)
            380000, // 06:15 (Infraction - Devil In A Red Dress)
            575000, // 09:31 (Infraction - Into The Fire)
            737000, // 12:12 (Infraction - Cyber Attack)
            910000, // 15:05 (Infraction - Virtual Reality)
           1111000, // 18:26 (Infraction - ID#2)
           1163000, // 18:18 (Infraction - ID#3)
           1267000, // 21:02 (Infraction - CyberGuitar)
           1465000, // 24:20 (Infraction - Already Evil)
    };

    private DatabaseManager dbManager;

    private ConstraintLayout mainLayout;

    private LinearLayout gridContainer;

    private GifImageView background;

    private TextView scoreText;
    private TextView highScoreText;

    private ImageView muteButton;
    private boolean isMuted = false;

    private Tile[][] tiles;
    private boolean turnDone = false;

    private int score;
    private int highScore;

    private boolean canPlay = true;

    private ImageView resetButton;
    private int[][] lastMove;
    private boolean turnReset;
    private int lastPoints;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game2048);

        dbManager = new DatabaseManager(this);

        mainLayout = findViewById(R.id.main_layout);
        gridContainer = findViewById(R.id.grid);
        background = findViewById(R.id.background);
        scoreText = findViewById(R.id.score);
        highScoreText = findViewById(R.id.high_score);
        muteButton = findViewById(R.id.muteButton);
        resetButton = findViewById(R.id.revertButton);

        resetButton.setAlpha(0f);

        muteButton.setOnClickListener(v -> mute());
        resetButton.setOnClickListener(v -> resetTurn());

        mainLayout.animate().alpha(1f).setDuration(500).start();

        soundtrackPlayer = MediaPlayer.create(this, R.raw.a2048_game_soundtrack);
        soundtrackPlayer.setLooping(true);
        soundtrackPlayer.seekTo(getRandomSong());
        soundtrackPlayer.start();

        restart();
    }

    private int getRandomSong() {
        Random random = new Random();
        int songPosition = random.nextInt(soundtrackStartPoints.length);
        return soundtrackStartPoints[songPosition];
    }

    private void mute() {
        if (isMuted) {
            soundtrackPlayer.setVolume(1f, 1f);
            isMuted = false;
            muteButton.setImageResource(R.drawable.ic_baseline_volume_up_24);
        } else {
            soundtrackPlayer.setVolume(0f, 0f);
            isMuted = true;
            muteButton.setImageResource(R.drawable.ic_baseline_volume_off_24);
        }
    }

    //GAME LOGIC

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListener() {
        mainLayout.setOnTouchListener(new OnSwipeListener(this) {
            @Override
            public void onSwipeRight() {
                if (!canPlay) { return; }
                lastMove = getLastMove();
                lastPoints = score;
                turnReset = false;
                animateResetButtonAppear();
                System.out.println("Swiped right");
                animateGrid(20f, 0f);
                for (int x = tiles.length-1; x >= 0; x--) {
                    for (int y = tiles[0].length-1; y >= 0 ; y--) {
                        //if tile has a value and is not in the border
                        if (tiles[x][y].getValue() != 0 && x != tiles.length-1) {
                            System.out.println("TILE :{"+x+","+y+"}");
                            //while the next tile is empty, we move this tile
                            boolean nextTileIsEmpty = tiles[x+1][y].getValue() == 0;
                            while (nextTileIsEmpty && x < tiles.length-1) {
                                System.out.println("has a space on its right");
                                moveTile(tiles[x][y], tiles[x+1][y]);
                                x++;
                                if (x < tiles.length-1) {
                                    nextTileIsEmpty = tiles[x+1][y].getValue() == 0;
                                } else {
                                    nextTileIsEmpty = false;
                                }
                            }
                            //when finishing moving, we check if we can collide a tile
                            if (x < tiles.length-1) {
                                if (checkColision(tiles[x][y], tiles[x+1][y]) && tiles[x][y].canMove()) {
                                    moveTile(tiles[x][y], tiles[x+1][y]);
                                    tiles[x+1][y].setCanMove(false);
                                    tiles[x][y].setCanMove(false);
                                }
                            }
                        }
                    }
                }
                nextTurn();
            }

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onSwipeLeft() {
                if (!canPlay) { return; }
                lastMove = getLastMove();
                lastPoints = score;
                turnReset = false;
                animateResetButtonAppear();
                System.out.println("Swiped left");
                animateGrid(-20f, 0f);
                for (int x = 0; x <= tiles.length-1; x++) {
                    for (int y = 0; y <= tiles[0].length-1 ; y++) {
                        //if tile has a value and is not in the border
                        if (tiles[x][y].getValue() != 0 && x != 0) {
                            System.out.println("TILE :{"+x+","+y+"}");
                            //while the next tile is empty, we move this tile
                            boolean nextTileIsEmpty = tiles[x-1][y].getValue() == 0;
                            while (nextTileIsEmpty) {
                                System.out.println("has a space on its left");
                                moveTile(tiles[x][y], tiles[x-1][y]);
                                x--;
                                if (x > 0) {
                                    nextTileIsEmpty = tiles[x-1][y].getValue() == 0;
                                } else {
                                    nextTileIsEmpty = false;
                                }
                            }
                            //when finishing moving, we check if we can collide a tile
                            if (x > 0) {
                                if (checkColision(tiles[x][y], tiles[x-1][y]) && tiles[x][y].canMove()) {
                                    moveTile(tiles[x][y], tiles[x-1][y]);
                                    tiles[x-1][y].setCanMove(false);
                                    tiles[x][y].setCanMove(false);
                                }
                            }
                        }
                    }
                }
                nextTurn();
            }

            @Override
            public void onSwipeTop() {
                if (!canPlay) { return; }
                lastMove = getLastMove();
                lastPoints = score;
                turnReset = false;
                animateResetButtonAppear();
                System.out.println("Swiped top");
                animateGrid(0f, -20f);
                for (int x = 0; x <= tiles.length-1; x++) {
                    for (int y = 0; y <= tiles[0].length-1 ; y++) {
                        //if tile has a value and is not in the border
                        if (tiles[x][y].getValue() != 0 && y != 0) {
                            System.out.println("TILE :{"+x+","+y+"}");
                            //while the next tile is empty, we move this tile
                            boolean nextTileIsEmpty = tiles[x][y-1].getValue() == 0;
                            while (nextTileIsEmpty) {
                                System.out.println("has a space on top");
                                moveTile(tiles[x][y], tiles[x][y-1]);
                                y--;
                                if (y > 0) {
                                    nextTileIsEmpty = tiles[x][y-1].getValue() == 0;
                                } else {
                                    nextTileIsEmpty = false;
                                }
                            }
                            //when finishing moving, we check if we can collide a tile
                            if (y > 0) {
                                if (checkColision(tiles[x][y], tiles[x][y-1]) && tiles[x][y].canMove()) {
                                    moveTile(tiles[x][y], tiles[x][y-1]);
                                    tiles[x][y-1].setCanMove(false);
                                    tiles[x][y].setCanMove(false);
                                }
                            }
                        }
                    }
                }
                nextTurn();
            }

            @Override
            public void onSwipeBottom() {
                if (!canPlay) { return; }
                lastMove = getLastMove();
                lastPoints = score;
                turnReset = false;
                animateResetButtonAppear();
                System.out.println("Swiped bottom");
                animateGrid(0f, 20f);
                for (int x = tiles.length-1; x >= 0; x--) {
                    for (int y = tiles[0].length-1; y >= 0 ; y--) {
                        //if tile has a value and is not in the border
                        if (tiles[x][y].getValue() != 0 && y != tiles[0].length-1) {
                            System.out.println("TILE :{"+x+","+y+"}");
                            //while the next tile is empty, we move this tile
                            boolean nextTileIsEmpty = tiles[x][y+1].getValue() == 0;
                            while (nextTileIsEmpty && y < tiles[0].length-1) {
                                System.out.println("has a space on bottom");
                                moveTile(tiles[x][y], tiles[x][y+1]);
                                y++;
                                if (y < tiles[0].length-1) {
                                    nextTileIsEmpty = tiles[x][y+1].getValue() == 0;
                                } else {
                                    nextTileIsEmpty = false;
                                }
                            }
                            //when finishing moving, if we check if we can collide a tile
                            if (y < tiles[0].length-1) {
                                if (checkColision(tiles[x][y], tiles[x][y+1]) && tiles[x][y].canMove()) {
                                    moveTile(tiles[x][y], tiles[x][y+1]);
                                    tiles[x][y+1].setCanMove(false);
                                    tiles[x][y].setCanMove(false);
                                }
                            }
                        }
                    }
                }
                nextTurn();
            }
        });
    }

    private void animateSpawn(Tile tile) {
        tile.getView().animate().scaleX(0f).setDuration(0).start();
        tile.getView().animate().scaleY(0f).setDuration(0).start();
        tile.getView().animate().scaleX(1f).setDuration(100).start();
        tile.getView().animate().scaleY(1f).setDuration(100).start();
    }

    private void animateGrid(float axisX, float axisY) {
        gridContainer.animate().translationX(axisX).setDuration(200).start();
        gridContainer.animate().translationY(axisY).setDuration(200).start();
        background.animate().translationX(-axisX).setDuration(200).start();
        background.animate().translationY(-axisY).setDuration(200).start();
        new Handler().postDelayed(() -> {
            gridContainer.animate().translationX(0f).setDuration(200).start();
            gridContainer.animate().translationY(0f).setDuration(200).start();
            background.animate().translationX(0f).setDuration(200).start();
            background.animate().translationY(0f).setDuration(200).start();
        }, 200);
    }

    private void nextTurn() {
        if (turnDone) {
            addRandomTile();
            for (Tile[] tileArray: tiles) {
                for (Tile tile : tileArray) {
                    tile.setCanMove(true);
                }
            }
            if (checkLost()) {
                canPlay = false;
                saveResults();
                showLostDialog();
            } else {
                turnDone = false;
            }
            turnReset = false;
        }
    }

    private int[][] getLastMove() {
        int[][] tilesCopy = new int[tiles.length][tiles[0].length];
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                tilesCopy[x][y] = tiles[x][y].getValue();
            }
        }
        return tilesCopy;
    }

    private void resetTurn() {
        if (!turnReset) {
            for (int x = 0; x < tiles.length; x++) {
                for (int y = 0; y < tiles[0].length; y++) {
                    tiles[x][y].changeValue(lastMove[x][y]);
                }
            }
            score = lastPoints;
            scoreText.setText(String.valueOf(score));
            turnReset = true;
            animateResetButtonGone();
        }
    }

    private void animateResetButtonAppear() {
        resetButton.animate().rotation(90).setDuration(150).start();
        resetButton.animate().alpha(1f).setDuration(150).start();
        resetButton.animate().scaleX(1f).setDuration(150).start();
        resetButton.animate().scaleY(1f).setDuration(150).start();
    }

    private void animateResetButtonGone() {
        resetButton.animate().rotation(0).setDuration(150).start();
        resetButton.animate().alpha(0f).setDuration(150).start();
        resetButton.animate().scaleX(0f).setDuration(150).start();
        resetButton.animate().scaleY(0f).setDuration(150).start();
    }

    private void moveTile(Tile moving, Tile objective) {
        if (objective.getValue() == 0) {
            objective.changeValue(moving.getValue());
            if (!moving.canMove()) {
                objective.setCanMove(false);
            }
        } else {
            objective.changeValue(moving.getValue()*2);
            if (!moving.canMove()) {
                objective.setCanMove(false);
            }
            score+= moving.getValue()*2;
            scoreText.setText(String.valueOf(score));
            if (score > highScore) {
                highScore = score;
                highScoreText.setText(String.valueOf(score));
            }
        }
        moving.changeValue(0);
        turnDone = true;
    }

    private boolean checkColision(Tile tile, Tile tilecolider) {
        if (tile.getValue() == 0) {
            return false;
        }
        if (tilecolider.getValue() == 0) {
            return true;
        }
        return tile.getValue() == tilecolider.getValue();
    }

    private List<Tile> getEmptyPositions() {
        List<Tile> emptyTiles = new ArrayList<>();

        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                if (tiles[x][y].getValue() == 0) {
                    emptyTiles.add(tiles[x][y]);
                }
            }
        }

        return emptyTiles;
    }

    private void addRandomTile() {
        List<Tile> emptyPositions = getEmptyPositions();
        if (!emptyPositions.isEmpty()) {
            Random r = new Random();
            int value;
            if (r.nextInt(10) < 9) {
                value = 2;
            } else {
                value = 4;
            }
            int position = r.nextInt(emptyPositions.size());
            emptyPositions.get(position).changeValue(value);
            animateSpawn(emptyPositions.get(position));
        }
    }

    private boolean checkLost() {
        boolean isLost = true;
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                //Check up
                try {
                    if (tiles[x][y-1].getValue() == tiles[x][y].getValue() || tiles[x][y-1].getValue() == 0) {
                        isLost = false;
                        break;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {}

                //Check left
                try {
                    if (tiles[x-1][y].getValue() == tiles[x][y].getValue() || tiles[x-1][y].getValue() == 0) {
                        isLost = false;
                        break;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {}

                //Check right
                try {
                    if (tiles[x+1][y].getValue() == tiles[x][y].getValue() || tiles[x+1][y].getValue() == 0) {
                        isLost = false;
                        break;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {}

                //Check down
                try {
                    if (tiles[x][y+1].getValue() == tiles[x][y].getValue() || tiles[x][y+1].getValue() == 0) {
                        isLost = false;
                        break;
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {}
            }
        }
        return isLost;
    }

    private void saveResults() {
        //sum rank points to the final score
        int rankPoints = UserInfo.getRankPoints(
                LoginCredentials.getUsernameSavedCredentials(this),
                dbManager
        );
        int finalScore = score + (rankPoints*1000);
        StatsTable.insertResult(
                LoginCredentials.getUsernameSavedCredentials(this),
                "2048",
                score,
                dbManager
        );
        UsersTable.addXP(
                LoginCredentials.getUsernameSavedCredentials(this),
                finalScore,
                dbManager
        );
    }

    private void showLostDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.game2048_game_over, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        //XP points
        TextView score = dialogView.findViewById(R.id.score);
        score.setText(String.valueOf(this.score));

        //Retry button
        Button retryButton = dialogView.findViewById(R.id.button_retry);
        retryButton.setOnClickListener(v -> {
            alertDialog.hide();
            restart();
        });

        //Exit button
        Button exitButton = dialogView.findViewById(R.id.button_exit);
        exitButton.setOnClickListener(v -> {
            alertDialog.hide();
            goToMenu();
        });

        Drawable background = ResourcesCompat.getDrawable(getResources(), R.drawable.edittext_shape, null);
        if (background != null) {
            background.setTint(Color.parseColor("#dd111111"));
        }
        alertDialog.getWindow().setBackgroundDrawable(background);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void goToMenu() {
        soundtrackPlayer.stop();
        mainLayout.animate().alpha(0f).setDuration(500).start();
        new Handler().postDelayed(() -> startActivity(
                new Intent(
                        getApplicationContext(), MenuActivity.class
                ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        ), 500);
    }

    //GAME INITIALIZER

    public void restart() {
        score = 0;
        scoreText.setText(String.valueOf(score));
        highScore = StatsTable.getHighScore(
                LoginCredentials.getUsernameSavedCredentials(this),
                "2048",
                dbManager
        );
        highScoreText.setText(String.valueOf(highScore));
        assignViewToVariables();
        for (int i = 0; i < 2; i++) {
            addRandomTile();
        }
        setOnTouchListener();
        canPlay = true;
        turnReset = true;
        animateResetButtonGone();
    }

    private void assignViewToVariables() {
        tiles = new Tile[4][4];
        tiles[0][0] = new Tile(findViewById(R.id.f0c0));
        tiles[1][0] = new Tile(findViewById(R.id.f0c1));
        tiles[2][0] = new Tile(findViewById(R.id.f0c2));
        tiles[3][0] = new Tile(findViewById(R.id.f0c3));

        tiles[0][1] = new Tile(findViewById(R.id.f1c0));
        tiles[1][1] = new Tile(findViewById(R.id.f1c1));
        tiles[2][1] = new Tile(findViewById(R.id.f1c2));
        tiles[3][1] = new Tile(findViewById(R.id.f1c3));

        tiles[0][2] = new Tile(findViewById(R.id.f2c0));
        tiles[1][2] = new Tile(findViewById(R.id.f2c1));
        tiles[2][2] = new Tile(findViewById(R.id.f2c2));
        tiles[3][2] = new Tile(findViewById(R.id.f2c3));

        tiles[0][3] = new Tile(findViewById(R.id.f3c0));
        tiles[1][3] = new Tile(findViewById(R.id.f3c1));
        tiles[2][3] = new Tile(findViewById(R.id.f3c2));
        tiles[3][3] = new Tile(findViewById(R.id.f3c3));
    }

    @Override
    protected void onStart() {
        super.onStart();
        soundtrackPlayer.start();
    }

    @Override
    protected void onPause() {
        if (soundtrackPlayer.isPlaying()) {
            soundtrackPlayer.pause();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        goToMenu();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {

        int[][] tileArray = new int[tiles.length][tiles[0].length];
        for (int x = 0; x < tileArray.length; x++) {
            for (int y = 0; y < tileArray[0].length; y++) {
                tileArray[x][y] = tiles[x][y].getValue();
            }
        }

        savedInstanceState.putSerializable("TILE_ARRAY", tileArray);
        savedInstanceState.putInt("SCORE", score);
        savedInstanceState.putInt("SONG_MILLIS", soundtrackPlayer.getCurrentPosition());
        savedInstanceState.putBoolean("IS_MUTED", isMuted);
        savedInstanceState.putBoolean("CAN_PLAY", canPlay);
        savedInstanceState.putSerializable("LAST_MOVE", lastMove);
        savedInstanceState.putBoolean("TURN_RESET", turnReset);
        savedInstanceState.putInt("LAST_POINTS", lastPoints);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        //Recover tiles position
        int[][] savedTiles = (int[][]) savedInstanceState.getSerializable("TILE_ARRAY");
        for (int x = 0; x < savedTiles.length; x++) {
            for (int y = 0; y < savedTiles[0].length; y++) {
                tiles[x][y].changeValue(savedTiles[x][y]);
            }
        }

        //Recover current score
        score = savedInstanceState.getInt("SCORE");
        scoreText.setText(String.valueOf(score));
        if (score > highScore) {
            highScore = score;
            highScoreText.setText(String.valueOf(highScore));
        }

        //Recover song position
        soundtrackPlayer.seekTo(savedInstanceState.getInt("SONG_MILLIS"));

        //IsMuted
        isMuted = savedInstanceState.getBoolean("IS_MUTED");
        if (isMuted) {
            muteButton.setImageResource(R.drawable.ic_baseline_volume_off_24);
            soundtrackPlayer.setVolume(0f, 0f);
        } else {
            muteButton.setImageResource(R.drawable.ic_baseline_volume_up_24);
            soundtrackPlayer.setVolume(1f, 1f);
        }

        //check if the game was already lost
        canPlay = savedInstanceState.getBoolean("CAN_PLAY");
        if (!canPlay) {
            showLostDialog();
        }

        //recover reset configuration
        lastMove = (int[][]) savedInstanceState.getSerializable("LAST_MOVE");
        turnReset = savedInstanceState.getBoolean("TURN_RESET");
        lastPoints = savedInstanceState.getInt("LAST_POINTS");

        if (lastMove != null && !turnReset) {
            animateResetButtonAppear();
        }

        super.onRestoreInstanceState(savedInstanceState);
    }
}
