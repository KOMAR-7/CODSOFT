package com.example.quote_today;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class sign_up extends AppCompatActivity {

    private UserDao userDao;
    private EditText usernameEditText,passwordEditText,confirmPasswordEditText;
    private Button signUpBtn;
    private TextView loginTextBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize userDao by obtaining an instance of your AppDatabase
        AppDatabase db = AppDatabase.getDatabase(this);
        userDao = db.userDao();

        signUpBtn = findViewById(R.id.signUpBtn);
        loginTextBtn = findViewById(R.id.loginTextBtn);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        signUpBtn.setOnClickListener(v ->signUp());
        loginTextBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, login.class);
            startActivity(intent);
        });
    }


    private class SignUpAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private String username;
        private String password;
        private String confirmPassword;

        SignUpAsyncTask(String username, String password, String confirmPassword) {
            this.username = username;
            this.password = password;
            this.confirmPassword = confirmPassword;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Check if the username already exists
                User existingUser = userDao.getUserByUsername(username);
                if (existingUser != null) {
                    return false; // Username already exists
                }

                // Check if the password and confirm password match
                if (!password.equals(confirmPassword)) {
                    return false; // Passwords don't match
                }

                // Insert the user into the database
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password);

                // Insert the new user into the database
                long userId = userDao.insertUser(newUser);

                usernameEditText.setText("");
                passwordEditText.setText("");
                confirmPasswordEditText.setText("");
                Intent i = new Intent(sign_up.this, login.class);
                startActivity(i);

                return true; // Sign-up successful
            } catch (Exception e) {
                // Handle exceptions here, log or show an error message
                Toast.makeText(sign_up.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                return false; // Sign-up failed due to an error
            }
        }

        @Override
        protected void onPostExecute(Boolean signUpSuccessful) {
            if (signUpSuccessful) {
                Toast.makeText(sign_up.this, "Sign-up successful", Toast.LENGTH_SHORT).show();
                // Handle successful sign-up, navigate to the next screen or perform other actions
            } else {
                Toast.makeText(sign_up.this, "Sign-up failed. Username may already exist or passwords don't match.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signUp() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            // Handle empty fields
            return;
        }

        // Execute SignUpAsyncTask on a background thread
        new SignUpAsyncTask(username, password, confirmPassword).execute();
    }

    // ... rest of your class
}