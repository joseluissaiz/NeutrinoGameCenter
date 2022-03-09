package com.overshade.neutrinogamecenter.Menus;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

public class GameLink {

    private String name;
    private int imageRes;
    private Class<? extends Activity> classLink;

    public GameLink(String name, int imageRes, Class<? extends Activity> classLink) {
        this.name = name;
        this.imageRes = imageRes;
        this.classLink = classLink;
     }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public Class<? extends Activity> getClassLink() {
        return classLink;
    }

    public void setClassLink(Class<? extends Activity> classLink) {
        this.classLink = classLink;
    }
}
