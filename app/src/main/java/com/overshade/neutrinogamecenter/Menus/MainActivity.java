package com.overshade.neutrinogamecenter.Menus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.overshade.neutrinogamecenter.DatabaseUtils.DatabaseManager;
import com.overshade.neutrinogamecenter.DatabaseUtils.UsersTable;
import com.overshade.neutrinogamecenter.LoginCredentials;
import com.overshade.neutrinogamecenter.R;

public class MainActivity extends AppCompatActivity {

    DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseManager = new DatabaseManager(this);
        //LoginCredentials.deleteCredentials(this);////////////////////////// Remove after test
        checkSavedCredentials();
    }

    private void checkSavedCredentials() {
        String username = LoginCredentials.getUsernameSavedCredentials(getApplicationContext());
        System.out.println(username);
        String password = LoginCredentials.getPasswordSavedCredentials(getApplicationContext());
        if (LoginCredentials.getUsernameSavedCredentials(getApplicationContext()) == null) {
            goToLoginPage();
        } else {
            if (UsersTable.checkCredentials(username, password, databaseManager)) {
                goToMenuPage();
            } else {
                goToLoginPage();
            }
        }
    }

    private void goToMenuPage() {
        startActivity(
                new Intent(
                        getApplicationContext(), MenuActivity.class
                ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        );
    }

    private void goToLoginPage() {
        startActivity(
                new Intent(
                        getApplicationContext(), LoginActivity.class
                ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        );
    }

    @Override
    public void onBackPressed() {
        //ignore
    }

}