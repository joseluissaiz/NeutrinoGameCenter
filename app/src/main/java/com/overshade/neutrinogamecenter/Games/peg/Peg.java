package com.overshade.neutrinogamecenter.Games.peg;

import android.widget.ImageView;

import com.overshade.neutrinogamecenter.R;

public class Peg {
    private boolean isAccesible;
    private ImageView view;
    private PegState currentState;
    private PegSolitaireGame mainGame;
    private int x, y;

    public enum PegState {

        UNSELECTED, HIDED, SELECTED

    }

    public Peg(PegSolitaireGame mainGame, int x, int y, ImageView view, boolean isAccesible) {
        this.mainGame = mainGame;
        this.view = view;
        view.setZ(100f);
        this.isAccesible = isAccesible;
        this.x = x;
        this.y = y;
        if (!isAccesible) {
            changeState(PegState.HIDED);
        } else {
            changeState(PegState.UNSELECTED);
            setOnClickListener();
        }
    }

    private void setOnClickListener() {
        view.setOnClickListener(v -> {
            // if selection is null, this is the selection
            if (mainGame.getSelection() == null) {
                if (getState() != PegState.HIDED) {
                    mainGame.setSelection(this);
                    changeState(PegState.SELECTED);
                }
            } else {
                // if some peg has been selected before, if we can move to it, we do it
                if (mainGame.canMoveToPeg(mainGame.getSelection(),this)) {
                    mainGame.moveToPeg(this);
                } else {
                    // if we can't, if we are accesible we are now the selected peg
                    if (this.isAccesible() && this.getState() != PegState.HIDED) {
                        mainGame.getSelection().changeState(PegState.UNSELECTED);
                        mainGame.setSelection(this);
                        this.changeState(PegState.SELECTED);
                    }
                }
            }
        });
    }

    public void changeState(PegState state) {
        switch (state) {
            case HIDED:
                view.setImageResource(R.drawable.peg_unselected);
                view.setAlpha(0f);
                break;
            case UNSELECTED:
                view.setImageResource(R.drawable.peg_unselected);
                view.setAlpha(1f);
                break;
            case SELECTED:
                view.setImageResource(R.drawable.peg_selected);
                view.setAlpha(1f);
                break;
        }
        this.currentState = state;
    }

    public PegState getState() {
        return this.currentState;
    }

    public boolean isAccesible() { return isAccesible; }

    public ImageView getView() { return view; }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
