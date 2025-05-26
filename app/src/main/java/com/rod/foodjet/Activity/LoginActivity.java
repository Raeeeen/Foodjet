package com.rod.foodjet.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rod.foodjet.Customer.CustomerMainActivity;
import com.rod.foodjet.R;
import com.rod.foodjet.Seller.SellerMainActivity;

public class LoginActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("");

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonFunctions();

    }

    private void buttonFunctions() {
        TextView usernameTxt = findViewById(R.id.username);
        TextView passwordTxt = findViewById(R.id.password);
        TextView loginBtn = findViewById(R.id.loginBtn);
        TextView gotoregister = findViewById(R.id.gotoregister);
        TextView forgotpass = findViewById(R.id.forgotpass);
        final Button passbtn = findViewById(R.id.passbtn);

        passbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordTxt.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    // Show password
                    passwordTxt.setTransformationMethod(null);
                    passbtn.setBackgroundResource(R.drawable.showpass);
                } else {
                    // Hide password
                    passwordTxt.setTransformationMethod(new PasswordTransformationMethod());
                    passbtn.setBackgroundResource(R.drawable.hiddenpass);
                }
            }
        });



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = usernameTxt.getText().toString();
                final String password = passwordTxt.getText().toString();


                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please input all fields", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user.isEmailVerified()) {
                                    DatabaseReference usersRef = databaseReference.child("users");

                                    usersRef.child("Customer").child(user.getUid()).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists() && snapshot.getValue(String.class).equals(email)) {
                                                // User is a Customer
                                                startActivity(new Intent(LoginActivity.this, CustomerMainActivity.class));
                                                finish();
                                            } else {

                                                usersRef.child("Seller").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            // User is a Seller
                                                            startActivity(new Intent(LoginActivity.this, SellerMainActivity.class));
                                                            finish();

                                                        } else {
                                                            // User not recognized
                                                            Toast.makeText(LoginActivity.this, "Invalid user", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        // Handle database read error
                                                        Toast.makeText(LoginActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Handle database read error
                                            Toast.makeText(LoginActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    user.sendEmailVerification();
                                    Toast.makeText(LoginActivity.this, "Check your Email for verification", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    Toast.makeText(LoginActivity.this, "Email has not been registered.", Toast.LENGTH_SHORT).show();
                                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(LoginActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }

            }
        });




        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = usernameTxt.getText().toString().trim();
                final String newPassword = passwordTxt.getText().toString().trim(); // Retrieve the new password from user input

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "Enter your email address", Toast.LENGTH_SHORT).show();
                } else {
                    sendPasswordResetEmail(email, newPassword); // Pass the new password to the sendPasswordResetEmail method
                }
            }

            private void sendPasswordResetEmail(final String email, final String newPassword) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        gotoregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoicesDialog();
            }

            private void showChoicesDialog() {
                final String[] choices = {"CUSTOMER", "SELLER"};

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setItems(choices, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String selectedChoice = choices[which];

                                String message;
                                if (selectedChoice.equals("CUSTOMER")) {
                                    startActivity(new Intent(LoginActivity.this, RegisterCustomerActivity.class));
                                    finish();
                                } else if (selectedChoice.equals("SELLER")) {
                                    startActivity(new Intent(LoginActivity.this, RegisterSellerActivity.class));
                                    finish();
                                } else {
                                    message = "Unknown choice";
                                }

                                Toast.makeText(LoginActivity.this, choices[which], Toast.LENGTH_SHORT).show();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });




    }
}
