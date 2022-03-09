package com.overshade.neutrinogamecenter.Menus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.overshade.neutrinogamecenter.R;

public class SplashActivity extends AppCompatActivity {

    LottieAnimationView lottieAnimation;
    ImageView logo;
    TextView appName;
    TextView version;
    TextView productFrom;
    LinearLayout screenCleaner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        lottieAnimation = findViewById(R.id.lottie_animation);
        logo = findViewById(R.id.logo);
        appName = findViewById(R.id.app_name);
        version = findViewById(R.id.version);
        productFrom = findViewById(R.id.productFrom);
        screenCleaner = findViewById(R.id.screenCleaner);

        //First intro
        logo.animate().rotation(0).setDuration(2500).start();
        logo.animate().scaleX(1.2f).setDuration(3000).start();
        logo.animate().scaleY(1.2f).setDuration(3000).start();
        lottieAnimation.animate().scaleX(100f).setDuration(2500).setStartDelay(1000).start();
        lottieAnimation.animate().scaleY(100f).setDuration(2500).setStartDelay(1000).start();
        logo.animate().scaleX(0.5f).setDuration(500).setStartDelay(2300).start();
        logo.animate().scaleY(0.5f).setDuration(500).setStartDelay(2300).start();

        //Show logo, app name and product from
        float logoDistance = getResources().getDimensionPixelSize(R.dimen.splash_distance_logo);
        float appNameDistance = getResources().getDimensionPixelSize(R.dimen.splash_distance_title);
        logo.animate().translationX(logoDistance).setDuration(500).setStartDelay(2300).start();
        appName.animate().translationX(appNameDistance).setDuration(1).setStartDelay(2300).start();
        appName.animate().alpha(1f).setDuration(1000).setStartDelay(2600).start();
        version.animate().alpha(1f).setDuration(1000).setStartDelay(2600).start();
        productFrom.animate().alpha(1f).setDuration(1000).setStartDelay(2600).start();

        //Exit animation
        screenCleaner.animate().alpha(1f).setDuration(1000).setStartDelay(4500).start();

        new Handler().postDelayed(() -> startActivity(new Intent(
                        getApplicationContext(), MainActivity.class
                ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        ), 6000);

    }

    @Override
    public void onBackPressed() {
        //ignore
    }

}