package com.overshade.neutrinogamecenter.Games.peg;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.TimerTask;

public class PegSolitaireGame extends AppCompatActivity {

    DatabaseManager dbManager;

    private Peg[][] pegs;
    private Peg selection = null;

    private ConstraintLayout mainLayout;

    private int xp;
    private TextView xpText;
    private int pegCount;
    private TextView pegCounterText;

    private int currentTime;
    private TextView currentTimeText;
    private long startTime = 0;
    Handler timerHandler = new Handler();
    private final Runnable timerRunnable = new Runnable() {

        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            PegSolitaireGame.this.runOnUiThread(() -> {
                long millis = System.currentTimeMillis() - startTime;
                int seconds = (int) (millis / 1000);
                int minutes = seconds / 60;
                seconds     = seconds % 60;

                currentTime = (int) millis;
                currentTimeText.setText(String.format("%d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 500);
            });
        }
    };
    private boolean isPaused;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peg_solitaire);

        dbManager = new DatabaseManager(this);

        mainLayout = findViewById(R.id.main_layout);
        pegCounterText = findViewById(R.id.pegCountText);
        currentTimeText = findViewById(R.id.currentTimeText);
        xpText = findViewById(R.id.xpText);

        restart();
    }


    // GAME LOGIC

    private boolean isGameLost() {
        for (int x = 0; x < pegs.length; x++) {
            for (int y = 0; y < pegs[0].length; y++) {
                if (pegs[x][y].isAccesible() && (pegs[x][y].getState() == Peg.PegState.UNSELECTED)) {

                    //Check up
                    try {
                        if (canMoveToPeg(pegs[x][y], pegs[x][y-2]) && pegs[x][y-2].isAccesible()) {
                            return false;
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {}

                    //Check left
                    try {
                        if (canMoveToPeg(pegs[x][y], pegs[x-2][y]) && pegs[x-2][y].isAccesible()) {
                            return false;
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {}

                    //Check down
                    try {
                        if (canMoveToPeg(pegs[x][y], pegs[x][y+2]) && pegs[x][y+2].isAccesible()) {
                            return false;
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {}


                    //Check right
                    try {
                        if (canMoveToPeg(pegs[x][y], pegs[x+2][y]) && pegs[x+2][y   ].isAccesible()) {
                            return false;
                        }
                    } catch (ArrayIndexOutOfBoundsException ignored) {}
                }
            }
        }
        return true;
    }

    public boolean canMoveToPeg(Peg selection, Peg objective) {
        if (objective == null) {
            System.out.println("No se puede mover");
            return false;
        }

        System.out.println("Selection: {"+selection.getX()+","+selection.getY()+"}");
        System.out.println("Objective: {"+objective.getX()+","+objective.getY()+"}");
        System.out.println("ObBoard  : {"+pegs[objective.getX()][objective.getY()].getX()+","+pegs[objective.getX()][objective.getY()].getY()+"}");

        //For Y axis
        if (objective.getY()+2 == selection.getY() && objective.getX() == selection.getX()) {
            if (objective.getState() == Peg.PegState.HIDED) {
                if (pegs[objective.getX()][objective.getY()+1].getState() == Peg.PegState.UNSELECTED) {
                    System.out.println("Se puede mover");
                    return true;
                }
            }
        }

        if (objective.getY()-2 == selection.getY() && objective.getX() == selection.getX()) {
            if (objective.getState() == Peg.PegState.HIDED) {
                if (pegs[objective.getX()][objective.getY()-1].getState() == Peg.PegState.UNSELECTED) {
                    System.out.println("Se puede mover");
                    return true;
                }
            }
        }

        //For X axis
        if (objective.getX()+2 == selection.getX() && objective.getY() == selection.getY()) {
            if (objective.getState() == Peg.PegState.HIDED) {
                if (pegs[objective.getX()+1][objective.getY()].getState() == Peg.PegState.UNSELECTED) {
                    System.out.println("Se puede mover");
                    return true;
                }
            }
        }

        if (objective.getX()-2 == selection.getX() && objective.getY() == selection.getY()) {
            if (objective.getState() == Peg.PegState.HIDED) {
                if (pegs[objective.getX()-1][objective.getY()].getState() == Peg.PegState.UNSELECTED) {
                    System.out.println("Se puede mover");
                    return true;
                }
            }
        }

        System.out.println("No se puede mover");
        return false;
    }

    public void moveToPeg(Peg pegObjective) {
        if (pegObjective.getX() - 2 == selection.getX()) {
            pegs[pegObjective.getX()-1][pegObjective.getY()].changeState(Peg.PegState.HIDED);
        }
        if (pegObjective.getX() + 2 == selection.getX()) {
            pegs[pegObjective.getX()+1][pegObjective.getY()].changeState(Peg.PegState.HIDED);
        }
        if (pegObjective.getY() - 2 == selection.getY()) {
            pegs[pegObjective.getX()][pegObjective.getY()-1].changeState(Peg.PegState.HIDED);
        }
        if (pegObjective.getY() + 2 == selection.getY()) {
            pegs[pegObjective.getX()][pegObjective.getY()+1].changeState(Peg.PegState.HIDED);
        }

        selection.changeState(Peg.PegState.HIDED);
        pegObjective.changeState(Peg.PegState.UNSELECTED);
        selection = null;
        pegCount--;
        xp += 5*((32-pegCount));
        xpText.setText(String.valueOf(xp));
        pegCounterText.setText(String.valueOf(pegCount));
        if (pegCount == 1) {
            //do win stuff
            int moreXP = 50000-(currentTime/2);
            if (xp+moreXP > xp) {
                xp += xp+moreXP;
            }
            saveResults();
            showWinDialog();
        } else {
            if (isGameLost()) {
                //do lose stuff
                saveResults();
                showLostDialog();
            }
        }
        System.out.println("--------------------------------------------------------------------");
    }

    private void animatePeg(Peg peg) {
        peg.getView().animate().scaleX(1.2f).setDuration(150).start();
        peg.getView().animate().scaleY(1.2f).setDuration(150).start();
        new Handler().postDelayed(() -> {
                peg.getView().animate().scaleX(1f).setDuration(150).start();
                peg.getView().animate().scaleY(1f).setDuration(150).start();
        }, 150);
    }

    private void saveResults() {
        //sum rank points to the final score
        int rankPoints = UserInfo.getRankPoints(
                LoginCredentials.getUsernameSavedCredentials(this),
                dbManager
        );
        int finalScore = xp + (rankPoints*1000);
        StatsTable.insertResult(
                LoginCredentials.getUsernameSavedCredentials(this),
                "peg",
                xp,
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
        View dialogView = LayoutInflater.from(this).inflate(R.layout.peg_solitaire_game_over, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        //XP points
        TextView score = dialogView.findViewById(R.id.score);
        score.setText(String.valueOf(this.xp));

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
            background.setTint(Color.parseColor("#dd6C5F55"));
        }
        alertDialog.getWindow().setBackgroundDrawable(background);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void showWinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.peg_solitaire_win, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        //XP points
        TextView score = dialogView.findViewById(R.id.score);
        score.setText(String.valueOf(this.xp));

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
            background.setTint(Color.parseColor("#dd6C5F55"));
        }
        alertDialog.getWindow().setBackgroundDrawable(background);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void goToMenu() {
        mainLayout.animate().alpha(0f).setDuration(500).start();
        new Handler().postDelayed(() -> startActivity(
                new Intent(
                        getApplicationContext(), MenuActivity.class
                ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        ), 500);
    }

    //GAME INITIALIZER

    public void restart() {
        xp = 0;
        xpText.setText(String.valueOf(xp));
        pegCount = 32;
        pegCounterText.setText(String.valueOf(pegCount));
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
        assignViewToVariables();
        createFirstHole();
    }

    private void createFirstHole() {
        pegs[3][3].changeState(Peg.PegState.HIDED);
    }

    private void assignViewToVariables() {
        pegs = new Peg[7][7];
        pegs[0][0] = new Peg(this, 0, 0, findViewById(R.id.f0c0), false);
        pegs[1][0] = new Peg(this, 1, 0, findViewById(R.id.f0c1), false);
        pegs[2][0] = new Peg(this, 2, 0, findViewById(R.id.f0c2), true);
        pegs[3][0] = new Peg(this, 3, 0, findViewById(R.id.f0c3), true);
        pegs[4][0] = new Peg(this, 4, 0, findViewById(R.id.f0c4), true);
        pegs[5][0] = new Peg(this, 5, 0, findViewById(R.id.f0c5), false);
        pegs[6][0] = new Peg(this, 6, 0, findViewById(R.id.f0c6), false);

        pegs[0][1] = new Peg(this, 0, 1, findViewById(R.id.f1c0), false);
        pegs[1][1] = new Peg(this, 1, 1, findViewById(R.id.f1c1), false);
        pegs[2][1] = new Peg(this, 2, 1, findViewById(R.id.f1c2), true);
        pegs[3][1] = new Peg(this, 3, 1, findViewById(R.id.f1c3), true);
        pegs[4][1] = new Peg(this, 4, 1, findViewById(R.id.f1c4), true);
        pegs[5][1] = new Peg(this, 5, 1, findViewById(R.id.f1c5), false);
        pegs[6][1] = new Peg(this, 6, 1, findViewById(R.id.f1c6), false);

        pegs[0][2] = new Peg(this, 0, 2, findViewById(R.id.f2c0), true);
        pegs[1][2] = new Peg(this, 1, 2, findViewById(R.id.f2c1), true);
        pegs[2][2] = new Peg(this, 2, 2, findViewById(R.id.f2c2), true);
        pegs[3][2] = new Peg(this, 3, 2, findViewById(R.id.f2c3), true);
        pegs[4][2] = new Peg(this, 4, 2, findViewById(R.id.f2c4), true);
        pegs[5][2] = new Peg(this, 5, 2, findViewById(R.id.f2c5), true);
        pegs[6][2] = new Peg(this, 6, 2, findViewById(R.id.f2c6), true);

        pegs[0][3] = new Peg(this, 0, 3, findViewById(R.id.f3c0), true);
        pegs[1][3] = new Peg(this, 1, 3, findViewById(R.id.f3c1), true);
        pegs[2][3] = new Peg(this, 2, 3, findViewById(R.id.f3c2), true);
        pegs[3][3] = new Peg(this, 3, 3, findViewById(R.id.f3c3), true);
        pegs[4][3] = new Peg(this, 4, 3, findViewById(R.id.f3c4), true);
        pegs[5][3] = new Peg(this, 5, 3, findViewById(R.id.f3c5), true);
        pegs[6][3] = new Peg(this, 6, 3, findViewById(R.id.f3c6), true);

        pegs[0][4] = new Peg(this, 0, 4, findViewById(R.id.f4c0), true);
        pegs[1][4] = new Peg(this, 1, 4, findViewById(R.id.f4c1), true);
        pegs[2][4] = new Peg(this, 2, 4, findViewById(R.id.f4c2), true);
        pegs[3][4] = new Peg(this, 3, 4, findViewById(R.id.f4c3), true);
        pegs[4][4] = new Peg(this, 4, 4, findViewById(R.id.f4c4), true);
        pegs[5][4] = new Peg(this, 5, 4, findViewById(R.id.f4c5), true);
        pegs[6][4] = new Peg(this, 6, 4, findViewById(R.id.f4c6), true);

        pegs[0][5] = new Peg(this, 0, 5, findViewById(R.id.f5c0), false);
        pegs[1][5] = new Peg(this, 1, 5, findViewById(R.id.f5c1), false);
        pegs[2][5] = new Peg(this, 2, 5, findViewById(R.id.f5c2), true);
        pegs[3][5] = new Peg(this, 3, 5, findViewById(R.id.f5c3), true);
        pegs[4][5] = new Peg(this, 4, 5, findViewById(R.id.f5c4), true);
        pegs[5][5] = new Peg(this, 5, 5, findViewById(R.id.f5c5), false);
        pegs[6][5] = new Peg(this, 6, 5, findViewById(R.id.f5c6), false);

        pegs[0][6] = new Peg(this, 0, 6, findViewById(R.id.f6c0), false);
        pegs[1][6] = new Peg(this, 1, 6, findViewById(R.id.f6c1), false);
        pegs[2][6] = new Peg(this, 2, 6, findViewById(R.id.f6c2), true);
        pegs[3][6] = new Peg(this, 3, 6, findViewById(R.id.f6c3), true);
        pegs[4][6] = new Peg(this, 4, 6, findViewById(R.id.f6c4), true);
        pegs[5][6] = new Peg(this, 5, 6, findViewById(R.id.f6c5), false);
        pegs[6][6] = new Peg(this, 6, 6, findViewById(R.id.f6c6), false);

    }

    // DTO

    public Peg getSelection() { return selection; }

    public void setSelection(Peg selection) {
        this.selection = selection;
        animatePeg(selection);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        //We assign a number depending of the peg current state {1=unselected, 2=selected, 3=hidden}
        int[][] pegArray = new int[pegs.length][pegs[0].length];
        for (int x = 0; x < pegs.length; x++) {
            for (int y = 0; y < pegs[0].length; y++) {
                //hidden
                if (pegs[x][y].getState() == Peg.PegState.HIDED) {
                    pegArray[x][y] = 3;
                }
                //selected
                if (pegs[x][y].getState() == Peg.PegState.SELECTED) {
                    pegArray[x][y] = 2;
                }
                //unselected
                if (pegs[x][y].getState() == Peg.PegState.UNSELECTED) {
                    pegArray[x][y] = 1;
                }
            }
        }
        savedInstanceState.putSerializable("PEG_ARRAY", pegArray);
        savedInstanceState.putLong("START_TIME", startTime);
        savedInstanceState.putInt("CURRENT_TIME", currentTime);
        savedInstanceState.putInt("XP", xp);
        savedInstanceState.putInt("PEG_COUNT", pegCount);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        //Get the current state of each peg
        int[][] pegArray = (int[][]) savedInstanceState.getSerializable("PEG_ARRAY");
        for (int x = 0; x < pegArray.length; x++) {
            for (int y = 0; y < pegArray[0].length; y++) {
                if (pegArray[x][y] == 3) {
                    pegs[x][y].changeState(Peg.PegState.HIDED);
                }
                if (pegArray[x][y] == 2) {
                    pegs[x][y].changeState(Peg.PegState.SELECTED);
                    selection = pegs[x][y];
                }
                if (pegArray[x][y] == 1) {
                    pegs[x][y].changeState(Peg.PegState.UNSELECTED);
                }
            }
        }
        startTime = savedInstanceState.getLong("START_TIME");
        currentTime = savedInstanceState.getInt("CURRENT_TIME");
        xp = savedInstanceState.getInt("XP");
        xpText.setText(String.valueOf(xp));
        pegCount = savedInstanceState.getInt("PEG_COUNT");
        pegCounterText.setText(String.valueOf(pegCount));

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        goToMenu();
    }
}
