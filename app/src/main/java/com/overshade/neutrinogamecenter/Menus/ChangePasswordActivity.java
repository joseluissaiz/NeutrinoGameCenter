package com.overshade.neutrinogamecenter.Menus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.overshade.neutrinogamecenter.CryptUtil;
import com.overshade.neutrinogamecenter.DatabaseUtils.DatabaseManager;
import com.overshade.neutrinogamecenter.DatabaseUtils.UsersTable;
import com.overshade.neutrinogamecenter.LoginCredentials;
import com.overshade.neutrinogamecenter.R;

public class ChangePasswordActivity extends AppCompatActivity {

    DatabaseManager dbManager;

    ConstraintLayout mainLayout;

    ImageButton backButton;

    TextView passwordErrorText;
    EditText passwordEditText;
    TextView rePasswordErrorText;
    EditText rePasswordEditText;

    Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        dbManager = new DatabaseManager(getApplicationContext());

        mainLayout = findViewById(R.id.main_layout);
        backButton = findViewById(R.id.backButton);
        passwordErrorText = findViewById(R.id.password_error_text);
        passwordEditText = findViewById(R.id.password_edittext);
        rePasswordErrorText = findViewById(R.id.repassword_error_text);
        rePasswordEditText = findViewById(R.id.repassword_edittext);
        changePasswordButton = findViewById(R.id.applyButton);

        mainLayout.animate().alpha(1f).setDuration(250).start();

        backButton.setOnClickListener(v -> goToMenu());
        changePasswordButton.setOnClickListener(v -> applyPassword());
    }

    private void applyPassword() {
        passwordErrorText.setVisibility(View.INVISIBLE);
        rePasswordErrorText.setVisibility(View.INVISIBLE);
        if (checkCredentials()) {
            try {
                // change password inside the db
                UsersTable.setPassword(
                        LoginCredentials.getUsernameSavedCredentials(this),
                        CryptUtil.encrypt(
                                passwordEditText.getText().toString(),
                                passwordEditText.getText().toString()
                        ),
                        dbManager
                );
                // change password inside SP
                LoginCredentials.saveUserCredentials(
                        LoginCredentials.getUsernameSavedCredentials(this),
                        CryptUtil.encrypt(
                                passwordEditText.getText().toString(),
                                passwordEditText.getText().toString()
                        ),
                        this
                );
                Toast.makeText(
                        this, "Password changed succesfully", Toast.LENGTH_SHORT
                ).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(
                        this, "Some error happened...", Toast.LENGTH_SHORT
                ).show();
            }
            goToMenu();
        }
    }

    private void goToMenu() {
        backButton.animate().alpha(0f).setDuration(500).start();
        mainLayout.animate().alpha(0f).setDuration(500).start();
        new Handler().postDelayed(() -> startActivity(
                new Intent(
                        getApplicationContext(), MenuActivity.class
                ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        ), 500);
    }

    private boolean checkCredentials() {
        boolean validCredentials = true;
        if (!checkPassword()) {
            validCredentials = false;
            passwordErrorText.setVisibility(View.VISIBLE);
        }
        if (!checkRepeatPassword()) {
            validCredentials = false;
            rePasswordErrorText.setVisibility(View.VISIBLE);
        }
        return validCredentials;
    }

    @SuppressLint("SetTextI18n")
    private boolean checkPassword() {
        boolean isValid = true;
        String password = passwordEditText.getText().toString();
        if (password.length() > 45) {
            isValid = false;
            passwordErrorText.setText("Your password cannot be bigger than 45 characters.");
        }
        if (password.length() < 5) {
            isValid = false;
            passwordErrorText.setText("Your password cannot have less than 5 characters.");
        }
        if (password.equals(LoginCredentials.getUsernameSavedCredentials(this)) && !password.equals("")) {
            isValid = false;
            passwordErrorText.setText("Your password cannot be the same as your username");
        }
        return isValid;
    }

    @SuppressLint("SetTextI18n")
    private boolean checkRepeatPassword() {
        if (passwordEditText.getText().toString().equals(rePasswordEditText.getText().toString())) {
            return true;
        } else {
            rePasswordErrorText.setText("Passwords don't match.");
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        goToMenu();
    }
}