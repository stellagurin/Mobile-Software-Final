/**
 * This is the home page of the application after the user
 * signs in or registers. From here, the user can choose to
 * view as a rider, driver, or choose to log out of the application
 */
package edu.uga.cs.rideshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "HomeActivity";

    //determines whether user is viewing as a rider or driver
    boolean isDriver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button driverButton = findViewById(R.id.button7); //view as driver
        Button riderButton = findViewById(R.id.button8); //view as rider
        Button logoutButton = findViewById(R.id.button9); //logout

        driverButton.setOnClickListener(new HomeActivity.DriverClickListener());
        riderButton.setOnClickListener(new HomeActivity.RiderClickListener());
        logoutButton.setOnClickListener(new HomeActivity.LogoutClickListener());

    }

    /**
     * If user clicks the driver button, then isDriver = true is passed
     * to SelectionActivity so the activity knows to set up the page from the
     * driver's view
     */
    private class DriverClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //go to driver's page
            isDriver = true;
            Intent intent = new Intent(view.getContext(), SelectionActivity.class);
            intent.putExtra("Type", isDriver);
            view.getContext().startActivity(intent);
        }

    }

    /**
     * If user clicks the rider button, then isDriver = false is passed
     * to SelectionActivity so the activity knows to set up the page from the
     * rider's view
     */
    private class RiderClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //go to rider's page
            isDriver = false;
            Intent intent = new Intent(view.getContext(), SelectionActivity.class);
            intent.putExtra("Type", isDriver);
            view.getContext().startActivity(intent);
        }
    }

    /**
     * If the user selects the logout button, then the user is completely
     * signed out from the application and cannot use any of its functions
     * until they sign in again.
     */
    private class LogoutClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //logout of app and go back to splash screen
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
            Toast.makeText(getApplicationContext(),
                    "Successfully logged out",
                    Toast.LENGTH_SHORT).show();
            finish();
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            view.getContext().startActivity(intent);
        }
    }
}
