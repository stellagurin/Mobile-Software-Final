/**
 * This activity is used register a user's account for the application. The user
 * can also choose to navigate to the login screen using the provided
 * login button. Users must provide both a valid email and a password. The
 * user cannot use an email that already has an account associated with it.
 * Passwords must be at least 6 characters long.
 */
package edu.uga.cs.rideshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "RegistrationActivity";

    private EditText emailEditText; //user's email
    private EditText passwordEditText; //user's password
    private Button loginButton; //button to login to application
    private Button registerButton; //button to register an account

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailEditText = (EditText) findViewById(R.id.editTextEmailAddress);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword);

        registerButton = (Button) findViewById(R.id.button3);
        registerButton.setOnClickListener(new RegisterButtonClickListener());

        loginButton = (Button) findViewById(R.id.button4);
        loginButton.setOnClickListener(new LoginButtonClickListener());
    }

    /**
     * When the user presses the registration button, their account is registered
     * using Firebase Authorization. Users must use an email that is not already
     * registered with the application.
     */
    private class RegisterButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final String email = emailEditText.getText().toString();
            final String password = passwordEditText.getText().toString();

            //firebase authorization instance
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            //email field is empty
            if(email.isEmpty()) {
                emailEditText.setError("Email is required.");
                return;
            }

            //checking if email is in valid format
            else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Please enter a valid email.");
                return;
            }

            //password field is empty
            if(password.isEmpty()) {
                passwordEditText.setError("Password is required.");
                return;
            }

            //if email and password are in the correct format, then create account
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {

                                //database reference
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference userRef = database.getReference("users");

                                //creating user object and adding user to database
                                String userEmail = email;
                                String userId = userRef.push().getKey();
                                int initialPoints = 1000;
                                User newUser = new User(userId, userEmail, initialPoints, null, null, null, null);
                                userRef.child(userId).setValue(newUser);

                                Toast.makeText(getApplicationContext(),
                                        "Registered user: " + email,
                                        Toast.LENGTH_SHORT).show();

                                //Sign in successful
                                Log.d( DEBUG_TAG, "createUserWithEmail: success" );

                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                //upon successful registration, go to home page
                                Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                                view.getContext().startActivity(intent);
                            }

                            else {
                                try {
                                    throw task.getException();
                                }

                                //account already exists
                                catch(FirebaseAuthUserCollisionException e) {
                                    emailEditText.setError("The email address is already in use by " +
                                            "another account. Sign in to continue.");
                                    emailEditText.requestFocus();
                                    return;
                                }

                                //password is too short (should be at least 6 characters)
                                catch(FirebaseAuthWeakPasswordException e)
                                {
                                    passwordEditText.setError("Password should be at least 6 characters.");
                                    return;
                                }

                                //other exceptions
                                catch(Exception e) {
                                    Log.w(DEBUG_TAG, "createUserWithEmail: failure", task.getException());
                                    Toast.makeText(RegistrationActivity.this, "Registration failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

        }
    }

    /**
     * If users click the login button on the registration screen, they will sent
     * to the login page.
     */
    private class LoginButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //go to login page
            Intent intent = new Intent(view.getContext(), LoginActivity.class);
            view.getContext().startActivity(intent);
        }
    }
}
