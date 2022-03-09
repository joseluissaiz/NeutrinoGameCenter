package com.overshade.neutrinogamecenter.Menus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.overshade.neutrinogamecenter.CryptUtil;
import com.overshade.neutrinogamecenter.DatabaseUtils.DatabaseManager;
import com.overshade.neutrinogamecenter.DatabaseUtils.UsersTable;
import com.overshade.neutrinogamecenter.LoginCredentials;
import com.overshade.neutrinogamecenter.R;

public class LoginActivity extends AppCompatActivity {

        boolean exitApp = false;
        boolean allFieldsFilled = false;

        DatabaseManager dbManager;

        ConstraintLayout mainLayout;
        TextView loginErrorText;
        EditText usernameEditText;
        EditText passwordEditText;
        Button loginButton;
        TextView createAccountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbManager = new DatabaseManager(this);

        mainLayout = findViewById(R.id.main_layout);
        loginErrorText = findViewById(R.id.login_error_text);
        usernameEditText = findViewById(R.id.username_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        loginButton = findViewById(R.id.loginButton);
        createAccountText = findViewById(R.id.create_account_text);

        mainLayout.animate().alpha(1f).setDuration(500).start();

        setFieldListeners();
        createAccountText.setOnClickListener(v -> goToSignUp());
        loginButton.setOnClickListener(v -> login());
    }

    private void login() {
        loginErrorText.setVisibility(View.INVISIBLE);
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        try {
            if (UsersTable.checkCredentials(
                    username,
                    CryptUtil.encrypt(password, password), dbManager))
            {
                LoginCredentials.saveUserCredentials(
                        username, CryptUtil.encrypt(password, password), this
                );
                goToMenu();
            } else {
                loginErrorText.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToMenu() {
        mainLayout.animate().alpha(0f).setDuration(1000).start();
        new Handler().postDelayed(() ->
                startActivity(
                        new Intent(
                                getApplicationContext(), MenuActivity.class
                        ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                ), 1000
        );
    }

    private void goToSignUp() {
        mainLayout.animate().alpha(0f).setDuration(1000).start();
        new Handler().postDelayed(() ->
                startActivity(
                        new Intent(
                                getApplicationContext(), SignUpActivity.class
                        ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                ), 1000
        );
    }

    private void setFieldListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (usernameEditText.getText().toString().equals("") ||
                        passwordEditText.getText().toString().equals(""))
                {
                    allFieldsFilled = false;
                    loginButton.setBackgroundResource(R.drawable.button_shape_disabled);
                } else {
                    allFieldsFilled = true;
                    loginButton.setBackgroundResource(R.drawable.button_shape_enabled);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        usernameEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
    }

    @Override
    public void onBackPressed() {
        if (exitApp) {
            finishAffinity();
        } else {
            exitApp = true;
            new Handler().postDelayed(
                    (Runnable) () -> exitApp = false, 500
            );
        }
    }

}