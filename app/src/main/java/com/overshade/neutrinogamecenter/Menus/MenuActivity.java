package com.overshade.neutrinogamecenter.Menus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.overshade.neutrinogamecenter.DatabaseUtils.DatabaseManager;
import com.overshade.neutrinogamecenter.DatabaseUtils.UserInfo;
import com.overshade.neutrinogamecenter.DatabaseUtils.UsersTable;
import com.overshade.neutrinogamecenter.Games.game2048.Game2048;
import com.overshade.neutrinogamecenter.Games.peg.PegSolitaireGame;
import com.overshade.neutrinogamecenter.LoginCredentials;
import com.overshade.neutrinogamecenter.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuActivity extends AppCompatActivity {

    //Database
    DatabaseManager dbManager;

    //lists and panels
    List<GameLink> gameLinkList;
    GridView gameLinkGrid;
    Spinner rankingSpinner;
    RecyclerView rankingView;
    TextView rankingScoreHeader;

    //settings
    ImageView settingsButton;
    ImageView quitSettingsButton;
    LinearLayout changePhotoButton;
    LinearLayout changePasswordButton;
    LinearLayout logOutButton;

    //player info
    CardView playerPhotoCard;
    CardView nameBarCard;
    TextView nameBarCardName;
    ImageView nameBarCardPhoto;
    TextView usernameTextBig;
    ImageView usernamePhotoBig;
    TextView levelText;
    ConstraintLayout mainLayout;
    TextView rankPointsText;
    CardView levelCard;
    CardView rankCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        dbManager = new DatabaseManager(this);

        gameLinkGrid = findViewById(R.id.game_link_grid);
        rankingView = findViewById(R.id.ranking_recyclerview);
        rankingSpinner = findViewById(R.id.rankingSpinner);
        rankingScoreHeader = findViewById(R.id.ranking_score_header);

        settingsButton = findViewById(R.id.settings_button);

        playerPhotoCard = findViewById(R.id.player_photo_card);
        nameBarCard = findViewById(R.id.name_bar_card);
        nameBarCardName = findViewById(R.id.username_text_little);
        nameBarCardPhoto = findViewById(R.id.player_photo_little);
        usernameTextBig = findViewById(R.id.username_text_big);
        usernamePhotoBig = findViewById(R.id.player_photo_big);
        levelCard = findViewById(R.id.levelCard);
        levelText = findViewById(R.id.level_text);
        mainLayout = findViewById(R.id.main_layout);
        rankCard = findViewById(R.id.rankCard);
        rankPointsText = findViewById(R.id.rankPoints);

        quitSettingsButton = findViewById(R.id.quit_settings_button);
        changePhotoButton = findViewById(R.id.change_photo_button);
        changePasswordButton = findViewById(R.id.change_password_button);
        logOutButton = findViewById(R.id.logout_button);

        mainLayout.setAlpha(0f);
        levelCard.setTranslationX(-300f);
        rankCard.setTranslationX(-400f);

        settingsButton.setOnClickListener(v -> showSettingsBar());
        quitSettingsButton.setOnClickListener(v -> hideSettingsBar());
        changePhotoButton.setOnClickListener(v -> goToChangePhoto());
        changePasswordButton.setOnClickListener(v -> goToChangePassword());
        logOutButton.setOnClickListener(v -> logOut());

        loadPlayerInfo();
        addGameShortcuts();
        addRankingSpinnerItems();

        //Initialize entry animation when components fully loaded (minimize lag)
        new Handler().postDelayed(() -> {
            float nameBarCardTranslation = getResources().getDimensionPixelSize(
                    R.dimen.name_bar_card_translation_X
            );

            mainLayout.animate().alpha(1f).setDuration(500).start();
            playerPhotoCard.animate().translationX(0f).setDuration(500).start();
            nameBarCard.animate().translationX(nameBarCardTranslation).setDuration(500).start();
            usernameTextBig.animate().translationX(0f).setDuration(500).start();
            levelCard.animate().translationX(0f).setDuration(500).start();
            rankCard.animate().translationX(0f).setDuration(500).start();
        }, 500);

    }

    private void addRankingSpinnerItems() {
        String[] optionList = new String[]{
                "Level",
                "Scores",
                "Scores by user",
                "2048 scores",
                "Peg Solitaire scores"
        };

        rankingSpinner.setAdapter(new ArrayAdapter<>(
                this, R.layout.spinner_item, optionList)
        );
        rankingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0 : {loadRankingByLevel(); break;}
                    case 1 : {loadRankingByScore(); break;}
                    case 2 : {loadRankingByUserScore(); break;}
                    case 3 : {loadRankingBy2048Score(); break;}
                    case 4 : {loadRankingByPegScore(); break;}
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadRankingByPegScore() {
        rankingScoreHeader.setText("Score");
        List<UserInfo> userList = UserInfo.getScoreGameRanking("peg", dbManager);
        RankingAdapter adapter = new RankingAdapter(userList);
        rankingView.setAdapter(adapter);
        rankingView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void loadRankingBy2048Score() {
        rankingScoreHeader.setText("Score");
        List<UserInfo> userList = UserInfo.getScoreGameRanking("2048", dbManager);
        RankingAdapter adapter = new RankingAdapter(userList);
        rankingView.setAdapter(adapter);
        rankingView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void loadRankingByUserScore() {
        rankingScoreHeader.setText("Best score");
        List<UserInfo> userList = UserInfo.getScoreUserRanking(dbManager);
        RankingAdapter adapter = new RankingAdapter(userList);
        rankingView.setAdapter(adapter);
        rankingView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void loadRankingByScore() {
        rankingScoreHeader.setText("Score");
        List<UserInfo> userList = UserInfo.getScoreRanking(dbManager);
        RankingAdapter adapter = new RankingAdapter(userList);
        rankingView.setAdapter(adapter);
        rankingView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void loadRankingByLevel() {
        rankingScoreHeader.setText("Best score");
        List<UserInfo> userList = UserInfo.getLevelRanking(dbManager);
        RankingAdapter adapter = new RankingAdapter(userList);
        rankingView.setAdapter(adapter);
        rankingView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void logOut() {
        LoginCredentials.deleteCredentials(this);
        goToLogin();
    }

    private void goToLogin() {
        mainLayout.animate().alpha(0f).setDuration(600).start();
        playerPhotoCard.animate().translationX(-900f).setDuration(600).start();
        nameBarCard.animate().translationX(-900f).setDuration(600).start();
        usernameTextBig.animate().translationX(-900f).setDuration(600).start();
        levelText.animate().translationX(-900f).setDuration(600).start();
        new Handler().postDelayed(() -> startActivity(
                new Intent(
                        getApplicationContext(), LoginActivity.class
                ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        ), 600);
    }

    private void goToChangePhoto() {
        mainLayout.animate().alpha(0f).setDuration(600).start();
        playerPhotoCard.animate().translationX(-900f).setDuration(600).start();
        nameBarCard.animate().translationX(-900f).setDuration(600).start();
        usernameTextBig.animate().translationX(-900f).setDuration(600).start();
        levelText.animate().translationX(-900f).setDuration(600).start();
        new Handler().postDelayed(() -> startActivity(
            new Intent(
                getApplicationContext(), ChangePhotoActivity.class
            ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        ), 600);
    }

    private void goToChangePassword() {
        mainLayout.animate().alpha(0f).setDuration(600).start();
        playerPhotoCard.animate().translationX(-900f).setDuration(600).start();
        nameBarCard.animate().translationX(-900f).setDuration(600).start();
        usernameTextBig.animate().translationX(-900f).setDuration(600).start();
        levelText.animate().translationX(-900f).setDuration(600).start();
        new Handler().postDelayed(() -> startActivity(
                new Intent(
                        getApplicationContext(), ChangePasswordActivity.class
                ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        ), 600);
    }

    private void loadPlayerInfo() {
        String username = LoginCredentials.getUsernameSavedCredentials(this);
        usernameTextBig.setText(username);
        nameBarCardName.setText(username);
        int iconRes = UsersTable.getIcon(username, dbManager);
        nameBarCardPhoto.setImageResource(iconRes);
        usernamePhotoBig.setImageResource(iconRes);
        //get level...
        int userXP = UsersTable.getLevel(username, dbManager);
        int userLevel = userXP/5000;
        levelText.setText(String.valueOf(userLevel));
        //get rank points
        int rankPoints = UserInfo.getRankPoints(username, dbManager);
        rankPointsText.setText(String.valueOf(rankPoints));
    }

    private void showSettingsBar() {
        settingsButton.animate().translationX(100f).setDuration(250).start();
        new Handler().postDelayed(() -> {
            //hide player info
            playerPhotoCard.animate().alpha(0f).setDuration(200).start();
            usernameTextBig.animate().alpha(0f).setDuration(200).start();
            levelCard.animate().alpha(0f).setDuration(200).start();
            rankCard.animate().alpha(0f).setDuration(200).start();
            //show options
            quitSettingsButton.animate().translationX(0f).setDuration(150).start();
            float panelDistance = getResources().getDimensionPixelSize(
                    R.dimen.settings_buttons_translation_showed
            );
            changePhotoButton.animate().translationX(panelDistance).setDuration(200).start();
            changePasswordButton.animate().translationX(panelDistance).setDuration(250).start();
            logOutButton.animate().translationX(panelDistance).setDuration(300).start();
        }, 250);
    }

    private void hideSettingsBar() {
        quitSettingsButton.animate().translationX(100f).setDuration(250).start();
        new Handler().postDelayed(() -> {
            //hide player info
            playerPhotoCard.animate().alpha(1f).setDuration(300).start();
            usernameTextBig.animate().alpha(1f).setDuration(300).start();
            levelCard.animate().alpha(1f).setDuration(300).start();
            rankCard.animate().alpha(1f).setDuration(300).start();
            //hide options
            float hideDistance = getResources().getDimensionPixelSize(
                    R.dimen.settings_buttons_translation_hided
            );
            settingsButton.animate().translationX(0).setDuration(350).start();
            changePhotoButton.animate().translationX(hideDistance).setDuration(200).start();
            changePasswordButton.animate().translationX(hideDistance).setDuration(250).start();
            logOutButton.animate().translationX(hideDistance).setDuration(300).start();
        }, 250);
    }

    private void addGameShortcuts() {
        gameLinkList = new ArrayList<>();
        GameLink game2048 = new GameLink("2048", R.drawable.icon_2048, Game2048.class);
        GameLink gamePeg = new GameLink("Peg solitaire", R.drawable.icon_peg, PegSolitaireGame.class);
        gameLinkList.add(game2048);
        gameLinkList.add(gamePeg);
        gameLinkGrid.setAdapter(
                new GameLinkAdapter(
                    this, R.layout.game_link_layout, gameLinkList
                )
        );
    }

    private void startGame(Class<? extends Activity> gameClass) {
        mainLayout.animate().alpha(0f).setDuration(600).start();
        playerPhotoCard.animate().translationX(-900f).setDuration(600).start();
        nameBarCard.animate().translationX(-900f).setDuration(600).start();
        usernameTextBig.animate().translationX(-900f).setDuration(600).start();
        levelCard.animate().translationX(-900f).setDuration(600).start();
        rankCard.animate().translationX(-900f).setDuration(600).start();

        new Handler().postDelayed(() -> startActivity(
                new Intent(
                        getApplicationContext(), gameClass
                ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        ), 600);
    }

    @Override
    public void onBackPressed() {}

    /** Custom array adapter for game list */
    public class GameLinkAdapter extends ArrayAdapter {

        public GameLinkAdapter(Context context, int resource, List<GameLink> gameLinkList) {
            super(context, resource, gameLinkList);
        }

        @Override public int getCount() {
            return super.getCount();
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE
                );
                view = inflater.inflate(R.layout.game_link_layout, null);
            }

            ImageView imageView = view.findViewById(R.id.game_icon);
            TextView textView = view.findViewById(R.id.game_name);

            GameLink gameLink = gameLinkList.get(position);

            imageView.setImageResource(gameLink.getImageRes());
            textView.setText(gameLink.getName());

            view.setOnClickListener(v -> startGame(gameLink.getClassLink()));

            return view;
        }
    }

    /**Custom recyclerview adapter for ranking list*/
    public static class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

        private final List<UserInfo> userList;

        public RankingAdapter(List<UserInfo> userList) {
            this.userList = userList;
        }

        @NonNull
        @Override
        public RankingAdapter.RankingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ranking_list_item, parent, false);
            return new RankingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RankingAdapter.RankingViewHolder holder, int position) {
            holder.bindRanking(userList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        public static class RankingViewHolder extends RecyclerView.ViewHolder {
            ImageView iconView;
            TextView positionText;
            TextView usernameText;
            TextView levelText;
            TextView scoreText;
            LottieAnimationView winnerView;

            public RankingViewHolder(View itemView) {
                super(itemView);
                iconView = itemView.findViewById(R.id.icon_rank);
                positionText = itemView.findViewById(R.id.position_rank);
                usernameText = itemView.findViewById(R.id.username_rank);
                levelText = itemView.findViewById(R.id.level_rank);
                scoreText = itemView.findViewById(R.id.score_rank);
                winnerView = itemView.findViewById(R.id.winnerView);
            }

            public void bindRanking(UserInfo userInfo, int position) {
                iconView.setImageResource(userInfo.getIconRes());
                positionText.setText(String.valueOf(position+1));
                usernameText.setText(userInfo.getUsername());
                levelText.setText(String.valueOf(userInfo.getLevel()));
                scoreText.setText(String.valueOf(userInfo.getScore()));
                if (position == 0) {
                    winnerView.setVisibility(View.VISIBLE);
                    winnerView.setTranslationX(-8f);
                    usernameText.setTranslationX(-8f);
                } else {
                    winnerView.setVisibility(View.GONE);
                }
            }
        }
    }
}