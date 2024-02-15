package com.example.quote_today;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import soup.neumorphism.NeumorphButton;

public class login extends AppCompatActivity {

    private UserDao userDao;
    private EditText usernameEditText, passwordEditText;
    private NeumorphButton loginButton;
    private TextView signUpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDao = AppDatabase.getDatabase(this).userDao();

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signUpText = findViewById(R.id.signUpText);



        loginButton.setOnClickListener(v -> loginUser());

        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, sign_up.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            // Handle empty fields
            return;
        }

        new LoginAsyncTask(username, password).execute();
    }

    private class LoginAsyncTask extends AsyncTask<Void, Void, User> {

        private String username;
        private String password;

        LoginAsyncTask(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected User doInBackground(Void... voids) {
            try {
                // Check if the username and password match in the database
                return userDao.getUserByUsernameAndPassword(username, password);
            } catch (Exception e) {
                // Handle exceptions here, log or show an error message
                Toast.makeText(login.this, "Error during login. Please try again.", Toast.LENGTH_SHORT).show();
                return null; // Login failed due to an error
            }
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                // If login is successful, start the MainActivity
                Toast.makeText(login.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(login.this, MainActivity.class);
                intent.putExtra("USER_ID_EXTRA", user.getUserId());
                intent.putExtra("USERNAME_EXTRA", user.getUsername());
//                Toast.makeText(login.this, String.valueOf(user.getUserId()), Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(login.this, "Login failed. Invalid username or password.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
