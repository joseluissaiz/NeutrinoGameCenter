package com.overshade.neutrinogamecenter.Menus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.overshade.neutrinogamecenter.DatabaseUtils.DatabaseManager;
import com.overshade.neutrinogamecenter.DatabaseUtils.UsersTable;
import com.overshade.neutrinogamecenter.LoginCredentials;
import com.overshade.neutrinogamecenter.R;

import java.util.ArrayList;
import java.util.List;

public class ChangePhotoActivity extends AppCompatActivity {

    DatabaseManager dbManager;

    ConstraintLayout mainLayout;
    ImageButton backButton;
    CardView currentPhotoCard;
    ImageView currentPhoto;
    GridView photoGrid;
    Button applyButton;

    List<Integer> photoList;
    Integer currentPhotoRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_photo);

        dbManager = new DatabaseManager(this);

        mainLayout = findViewById(R.id.main_layout);
        backButton = findViewById(R.id.backButton);
        currentPhotoCard = findViewById(R.id.current_photo_card);
        currentPhoto = findViewById(R.id.current_photo);
        photoGrid = findViewById(R.id.photo_grid);
        applyButton = findViewById(R.id.applyButton);

        mainLayout.animate().alpha(1).setDuration(250).start();
        backButton.animate().alpha(1).setDuration(350).start();
        currentPhotoCard.animate().translationX(0).setDuration(250).start();

        backButton.setOnClickListener(v -> goToMenu());
        applyButton.setOnClickListener(v -> apply());

        loadCurrentPhoto();
        loadPhotos();
    }

    private void loadCurrentPhoto() {
        int icon = UsersTable.getIcon(
                LoginCredentials.getUsernameSavedCredentials(this), dbManager
        );
        currentPhotoRes = icon;
        currentPhoto.setImageResource(icon);
    }

    private void apply() {
        UsersTable.setIcon(
                LoginCredentials.getUsernameSavedCredentials(this),
                currentPhotoRes,
                dbManager
        );
        goToMenu();
    }

    private void goToMenu() {
        mainLayout.animate().alpha(0).setDuration(250).start();
        backButton.animate().alpha(0).setDuration(350).start();
        currentPhotoCard.animate().translationX(1000f).setDuration(250).start();
        new Handler().postDelayed(() -> startActivity(
                new Intent(
                        getApplicationContext(), MenuActivity.class
                ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        ), 350);
    }

    private void changePhoto(int position) {
        currentPhoto.setImageResource(photoList.get(position));
        currentPhotoRes = photoList.get(position);
    }

    private void loadPhotos() {
        photoList = new ArrayList<>();
        Integer robotIcon = R.drawable.robot_icon;
        Integer darkIcon = R.drawable.dark_icon;
        Integer happyIcon = R.drawable.happy_icon;
        Integer funnyIcon = R.drawable.funny_icon;
        Integer angryIcon = R.drawable.angry_icon;
        Integer relaxedIcon = R.drawable.relaxed_icon;
        Integer afraidIcon = R.drawable.afraid_icon;
        Integer draculaIcon = R.drawable.dracula_icon;

        photoList.add(robotIcon);
        photoList.add(darkIcon);
        photoList.add(happyIcon);
        photoList.add(funnyIcon);
        photoList.add(angryIcon);
        photoList.add(relaxedIcon);
        photoList.add(afraidIcon);
        photoList.add(draculaIcon);

        photoGrid.setAdapter(
                new PhotoListAdapter(this, R.layout.photo_list_layout, photoList)
        );
    }

    @Override
    public void onBackPressed() {
        goToMenu();
    }

    /** Custom array adapter for photo list */
    public class PhotoListAdapter extends ArrayAdapter {

        public PhotoListAdapter(Context context, int resource, List<Integer> photoResList) {
            super(context, resource, photoResList);
        }

        @Override public int getCount() {
            return super.getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE
                );
                view = inflater.inflate(R.layout.photo_list_layout, null);
            }

            ImageView imageView = view.findViewById(R.id.photo_icon);
            imageView.setOnClickListener(v -> changePhoto(position));

            Integer photoRes = photoList.get(position);

            imageView.setImageResource(photoRes);
            return view;
        }
    }
}