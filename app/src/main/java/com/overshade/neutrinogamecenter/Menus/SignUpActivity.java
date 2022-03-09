package com.overshade.neutrinogamecenter.Menus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.overshade.neutrinogamecenter.CryptUtil;
import com.overshade.neutrinogamecenter.DatabaseUtils.DatabaseManager;
import com.overshade.neutrinogamecenter.DatabaseUtils.UsersTable;
import com.overshade.neutrinogamecenter.LoginCredentials;
import com.overshade.neutrinogamecenter.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    DatabaseManager databaseManager;
    boolean allFieldsFilled = false;

    ImageButton backButton;
    ConstraintLayout mainLayout;
    TextView usernameErrorText;
    EditText usernameEditText;
    TextView nameErrorText;
    EditText nameEditText;
    TextView surnameErrorText;
    EditText surnameEditText;
    TextView passwordErrorText;
    EditText passwordEditText;
    TextView rePasswordErrorText;
    EditText rePasswordEditText;
    Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        databaseManager = new DatabaseManager(this);

        backButton          = findViewById(R.id.backButton);
        mainLayout          = findViewById(R.id.main_layout);
        usernameErrorText   = findViewById(R.id.username_error_text);
        usernameEditText    = findViewById(R.id.username_edittext);
        nameErrorText       = findViewById(R.id.name_error_text);
        nameEditText        = findViewById(R.id.name_edittext);
        surnameErrorText    = findViewById(R.id.surname_error_text);
        surnameEditText     = findViewById(R.id.surname_edittext);
        passwordErrorText   = findViewById(R.id.password_error_text);
        passwordEditText    = findViewById(R.id.password_edittext);
        rePasswordErrorText = findViewById(R.id.repassword_error_text);
        rePasswordEditText  = findViewById(R.id.repassword_edittext);
        signUpButton        = findViewById(R.id.signupButton);

        backButton.animate().alpha(1f).setDuration(500).start();
        mainLayout.animate().alpha(1f).setDuration(500).start();

        backButton.setOnClickListener(v -> goToLogin());
        signUpButton.setOnClickListener(v -> signUp());

        setFieldListeners();
    }

    private void signUp() {
        if (!allFieldsFilled) {
            return;
        }
        if (checkCredentials()) {
            try {
                UsersTable.insertUser(
                        usernameEditText.getText().toString(),
                        CryptUtil.encrypt(
                                passwordEditText.getText().toString(),
                                passwordEditText.getText().toString()
                        ),
                        nameEditText.getText().toString(),
                        surnameEditText.getText().toString(),
                        R.drawable.robot_icon,
                        0,
                        databaseManager
                );
                System.out.println(
                        "SQL-DATABASE: USER-CREATED : "+usernameEditText.getText().toString()
                );
                System.out.println(CryptUtil.encrypt(passwordEditText.getText().toString(), passwordEditText.getText().toString()
                ));
                LoginCredentials.saveUserCredentials(
                    usernameEditText.getText().toString(),
                    CryptUtil.encrypt(
                            passwordEditText.getText().toString(),
                            passwordEditText.getText().toString()
                    ),
                    this
                );
                backButton.animate().alpha(0f).setDuration(500).start();
                mainLayout.animate().alpha(0f).setDuration(500).start();
                new Handler().postDelayed(() -> startActivity(
                    new Intent(
                            getApplicationContext(), MenuActivity.class
                    ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                ), 500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(
                    this, "Some values are incorrect...", Toast.LENGTH_SHORT
            ).show();
        }
    }

    private boolean checkCredentials() {
        usernameErrorText.setVisibility(View.INVISIBLE);
        nameErrorText.setVisibility(View.INVISIBLE);
        surnameErrorText.setVisibility(View.INVISIBLE);
        passwordErrorText.setVisibility(View.INVISIBLE);
        rePasswordErrorText.setVisibility(View.INVISIBLE);
        boolean validCredentials = true;
        if (!checkUsername()) {
            validCredentials = false;
            usernameErrorText.setVisibility(View.VISIBLE);
        }
        if (!checkName(nameEditText.getText().toString())) {
            validCredentials = false;
            nameErrorText.setVisibility(View.VISIBLE);
        }
        if (!checkName(surnameEditText.getText().toString())) {
            validCredentials = false;
            surnameErrorText.setVisibility(View.VISIBLE);
        }
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
    private boolean checkUsername() {
        boolean isValid = true;
        String username = usernameEditText.getText().toString();
        if (UsersTable.userExists(username, databaseManager)) {
            isValid = false;
            usernameErrorText.setText("This username is already taken.");
        }
        String regex = "^[A-Za-z]\\w{5,29}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);
        if (!matcher.matches()) {
            isValid = false;
            usernameErrorText.setText("Invalid username, please try with another one.");
        }
        return isValid;
    }

    private boolean checkName(String name) {
        String regex = "^[a-zA-z ]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        return (matcher.matches() && !name.equals(""));
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
        if (password.equals(usernameEditText.getText().toString()) && !password.equals("")) {
            isValid = false;
            passwordErrorText.setText("Your password cannot be the same as your username");
        }
        if (password.equals(nameEditText.getText().toString()) && !password.equals("")) {
            isValid = false;
            passwordErrorText.setText("Your password cannot be your name");
        }
        if (password.equals(surnameEditText.getText().toString()) && !password.equals("")) {
            isValid = false;
            passwordErrorText.setText("Your password cannot be your surname");
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

    private void setFieldListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (usernameEditText.getText().toString().equals("")  ||
                    nameEditText.getText().toString().equals("")      ||
                    surnameEditText.getText().toString().equals("")   ||
                    passwordEditText.getText().toString().equals("")  ||
                    rePasswordEditText.getText().toString().equals("")
                )
                {
                    allFieldsFilled = false;
                    signUpButton.setBackgroundResource(R.drawable.button_shape_disabled);
                } else {
                    allFieldsFilled = true;
                    signUpButton.setBackgroundResource(R.drawable.button_shape_enabled);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        usernameEditText.addTextChangedListener(textWatcher);
        nameEditText.addTextChangedListener(textWatcher);
        surnameEditText.addTextChangedListener(textWatcher);
        passwordEditText.addTextChangedListener(textWatcher);
        rePasswordEditText.addTextChangedListener(textWatcher);
    }

    private void goToLogin() {
        backButton.animate().alpha(0f).setDuration(1000).start();
        mainLayout.animate().alpha(0f).setDuration(1000).start();
        new Handler().postDelayed(() ->
            startActivity(
                new Intent(
                        getApplicationContext(), LoginActivity.class
                ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            ), 1000
        );
    }

    @Override
    public void onBackPressed() {
        goToLogin();
    }
}