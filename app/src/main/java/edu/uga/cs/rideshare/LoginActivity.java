/**
 * This activity is used to sign the user into the application. The user
 * can also choose to navigate to the registration screen using the provided
 * register button. Users must provide both a valid email and a password. The
 * user cannot sign into an account that has not been registered first.
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "LoginActivity";

    private EditText emailEditText; //user's email
    private EditText passwordEditText; //user's password
    private Button loginButton; //button to login to application
    private Button registerButton; //button to register an account

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEditText = (EditText) findViewById(R.id.editTextEmailAddress2);
        passwordEditText = (EditText) findViewById(R.id.editTextPassword2);

        registerButton = (Button) findViewById(R.id.button5);
        registerButton.setOnClickListener(new LoginButtonClickListener());

        loginButton = (Button) findViewById(R.id.button6);
        loginButton.setOnClickListener(new RegisterButtonClickListener());
    }

    /**
     * When the user presses the login button, they are signed into the application
     * using Firebase Authorization. Users must have an account that is already registered.
     */
    private class LoginButtonClickListener implements View.OnClickListener {
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

            //if email and password are in the correct format, then sign in
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    Toast.makeText(getApplicationContext(),
                                            "Login successful!",
                                            Toast.LENGTH_SHORT).show();


                                    //Sign in successful
                                    Log.d(DEBUG_TAG, "createUserWithEmail: success");

                                    //upon successful registration, go to home page
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    view.getContext().startActivity(intent);
                                }

                                else {
                                    try {
                                        throw task.getException();
                                    }

                                    //invalid credentials exception (invalid password)
                                    catch(FirebaseAuthInvalidCredentialsException e) {
                                        String errorCode = e.getErrorCode();
                                        //Log.w(DEBUG_TAG, errorCode);

                                        if(errorCode.equals("ERROR_WRONG_PASSWORD")) {
                                            passwordEditText.setError("Invalid password. Please try again.");
                                            return;
                                        }
                                    }

                                    //incorrect email or email is not registered
                                    catch(FirebaseAuthInvalidUserException e) {
                                        String errorCode = e.getErrorCode();
                                        //Log.w(DEBUG_TAG, errorCode);

                                        if(errorCode.equals("ERROR_USER_NOT_FOUND")) {
                                            emailEditText.setError("Email is incorrect or user does not exist. Try again or create an account.");
                                        }
                                    }

                                    //other exceptions
                                    catch(Exception e) {
                                        Log.w(DEBUG_TAG, "loginWithEmail: failure", task.getException());
                                        Toast.makeText(LoginActivity.this, "Login failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                );
        }
    }

    /**
     * If users click the registration button on the login screen, they will sent
     * to the registration page.
     */
    private class RegisterButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //go to registration page
            Intent intent = new Intent(view.getContext(), RegistrationActivity.class);
            view.getContext().startActivity(intent);
        }
    }
}
