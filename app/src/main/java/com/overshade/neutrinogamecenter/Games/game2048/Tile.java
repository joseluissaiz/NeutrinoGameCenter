package com.overshade.neutrinogamecenter.Games.game2048;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.widget.TextView;

public class Tile {
    private TextView view;
    private int value;
    private boolean canMove = true;

    public Tile(TextView view) {
        this.view = view;
        this.view.setZ(100f);
        changeValue(0);
    }

    public void changeValue(int value) {
        if (value == 0) {
            view.setAlpha(0);
        }
        if (value == 2) {
            view.getBackground().setColorFilter(Color.parseColor("#8799a4"), PorterDuff.Mode.SRC_ATOP);
            view.setTextColor(Color.parseColor("#efefef"));
            view.setAlpha(1);
        }
        if (value == 4) {
            view.getBackground().setColorFilter(Color.parseColor("#9787a4"), PorterDuff.Mode.SRC_ATOP);
            view.setTextColor(Color.parseColor("#efefef"));
            view.setAlpha(1);
        }
        if (value == 8) {
            view.getBackground().setColorFilter(Color.parseColor("#87a496"), PorterDuff.Mode.SRC_ATOP);
            view.setTextColor(Color.parseColor("#efefef"));
            view.setAlpha(1);
        }
        if (value == 16) {
            view.getBackground().setColorFilter(Color.parseColor("#a9866a"), PorterDuff.Mode.SRC_ATOP);
            view.setTextColor(Color.parseColor("#efefef"));
            view.setAlpha(1);
        }
        if (value == 32) {
            view.getBackground().setColorFilter(Color.parseColor("#a96a6a"), PorterDuff.Mode.SRC_ATOP);
            view.setTextColor(Color.parseColor("#efefef"));
            view.setAlpha(1);
        }

        if (value == 64) {
            view.getBackground().setColorFilter(Color.parseColor("#e96060"), PorterDuff.Mode.SRC_ATOP);
            view.setTextColor(Color.parseColor("#000000"));
            view.setAlpha(1);
        }

        if (value >= 128) {
            view.getBackground().setColorFilter(Color.parseColor("#3ccda8"), PorterDuff.Mode.SRC_ATOP);
            view.setTextColor(Color.parseColor("#000000"));
            view.setAlpha(1);
        }

        this.value = value;
        view.setText(Integer.toString(value));
    }

    //DTO

    public TextView getView() {
        return view;
    }

    public boolean canMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public int getValue() {
        return value;
    }
}
