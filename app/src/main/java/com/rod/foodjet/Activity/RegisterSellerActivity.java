package com.rod.foodjet.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rod.foodjet.BuildConfig;
import com.rod.foodjet.R;

import java.io.ByteArrayOutputStream;

public class RegisterSellerActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerseller);

        buttonFunctions();

    }

    private void buttonFunctions() {
        TextView registerbtn = findViewById(R.id.registerBtn2);
        TextView gotologin = findViewById(R.id.gotologin2);

        final EditText fullname = findViewById(R.id.fullname2);
        final EditText usernameTxt = findViewById(R.id.username2);
        final EditText passwordTxt = findViewById(R.id.password2);
        final EditText phonenumber = findViewById(R.id.phonenumber2);
        final EditText street = findViewById(R.id.street2);
        final EditText district = findViewById(R.id.district2);
        final EditText barangay = findViewById(R.id.barangay2);
        final Button passbtn = findViewById(R.id.passbtn);

        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterSellerActivity.this, LoginActivity.class));
                finish();
            }
        });

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

        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fullNameTxt = fullname.getText().toString();
                final String email = usernameTxt.getText().toString();
                final String password = passwordTxt.getText().toString();
                final String phonenumberTxt = phonenumber.getText().toString();
                final String streetTxt = street.getText().toString();
                final String districtTxt = district.getText().toString();
                final String barangayTxt = barangay.getText().toString();

                if (fullNameTxt.isEmpty() || email.isEmpty() || password.isEmpty() || phonenumberTxt.isEmpty() || streetTxt.isEmpty() || districtTxt.isEmpty() || barangayTxt.isEmpty()) {
                    Toast.makeText(RegisterSellerActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterSellerActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {
                                        String uid = user.getUid();
                                        DatabaseReference userRef = databaseReference.child("users").child("Seller").child(uid);

                                        // Save seller information to the database
                                        userRef.child("Selleremail").setValue(email);
                                        userRef.child("SellerfullName").setValue(fullNameTxt);
                                        userRef.child("SellerphoneNumber").setValue(phonenumberTxt);
                                        userRef.child("Sellerstreet").setValue(streetTxt);
                                        userRef.child("Sellerdistrict").setValue(districtTxt);
                                        userRef.child("Sellerbarangay").setValue(barangayTxt);

                                        Toast.makeText(RegisterSellerActivity.this, "Account has been registered", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterSellerActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(RegisterSellerActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });









    }
}
