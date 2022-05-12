/**
 * This is the main activity and splash screen of the RideShare application.
 * This activity contains a login and registration button for the user.
 */
package edu.uga.cs.rideshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button registrationButton = findViewById(R.id.button); //register button
        Button loginButton = findViewById(R.id.button2); //login button

        registrationButton.setOnClickListener(new RegistrationClickListener());
        loginButton.setOnClickListener(new LoginClickListener());

    }

    /**
     * This class allows the user to register for an account within the application.
     * The button will take the user to the registration screen.
     */
    private class RegistrationClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //start user registration activity
            Intent intent = new Intent(view.getContext(), RegistrationActivity.class);
            view.getContext().startActivity(intent);
        }

    }

    /**
     * This class allows the user to login with a registered account within the application.
     * The button will take the user to the login screen.
     */
    private class LoginClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //start user login activity
            Intent intent = new Intent(view.getContext(), LoginActivity.class);
            view.getContext().startActivity(intent);

        }
    }
}