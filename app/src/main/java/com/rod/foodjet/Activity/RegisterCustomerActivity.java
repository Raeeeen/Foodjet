package com.rod.foodjet.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.io.FileNotFoundException;
import java.io.InputStream;

public class RegisterCustomerActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final int SELECT_IMAGE_REQUEST_CODE = 1;
    private ImageButton imgprev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registercustomer);

        buttonFunctions();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap circularBitmap = createCircularBitmap(bitmap);
                imgprev.setImageBitmap(circularBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap createCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int radius = Math.min(width, height) / 2;
        Bitmap circularBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(circularBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawCircle(width / 2, height / 2, radius, paint);

        return circularBitmap;
    }

    private void buttonFunctions() {
        TextView registerbtn = findViewById(R.id.registerBtn);
        TextView gotologin = findViewById(R.id.gotologin);

        final EditText fullname = findViewById(R.id.fullname);
        final EditText usernameTxt = findViewById(R.id.username);
        final EditText passwordTxt = findViewById(R.id.password);
        final EditText phonenumber = findViewById(R.id.phonenumber);
        final EditText street = findViewById(R.id.street);
        final EditText district = findViewById(R.id.district);
        final EditText barangay = findViewById(R.id.barangay);
        final EditText otherlocation = findViewById(R.id.otherlocation);
        final Button passbtn = findViewById(R.id.passbtn);
        imgprev = findViewById(R.id.imgprev);


        imgprev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to pick an image from the gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
            }
        });


        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterCustomerActivity.this, LoginActivity.class));
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
                final String otherlocationTxt = otherlocation.getText().toString();


                if (fullNameTxt.isEmpty() || email.isEmpty() || password.isEmpty() || phonenumberTxt.isEmpty() || streetTxt.isEmpty() || districtTxt.isEmpty() || barangayTxt.isEmpty()) {
                    Toast.makeText(RegisterCustomerActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                } else if(imgprev.getDrawable() == null) {
                    Toast.makeText(RegisterCustomerActivity.this, "Please select your profile image", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.matches(".+@gmail\\.com")) {
                    Toast.makeText(RegisterCustomerActivity.this, "Please enter the correct Gmail address", Toast.LENGTH_SHORT).show();
                } else if (!phonenumberTxt.matches("09\\d{9}")) {
                    Toast.makeText(RegisterCustomerActivity.this, "Please enter the correct Mobile number", Toast.LENGTH_SHORT).show();
                } else if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")) {
                    Toast.makeText(RegisterCustomerActivity.this, "Please enter a Strong Password", Toast.LENGTH_SHORT).show();
                } else if(!phonenumberTxt.matches("09\\d{9}") && !Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.matches(".+@.+\\..+")) {
                    Toast.makeText(RegisterCustomerActivity.this, "Please enter the correct Mobile Number and Email", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseAuth.getInstance().setFirebaseUIVersion(BuildConfig.VERSION_NAME);

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterCustomerActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if (user != null) {
                                            String uid = user.getUid();
                                            DatabaseReference userRef = databaseReference.child("users").child("Customer").child(uid);


                                            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("users").child("Customers").child(uid).child("profilePicture").child(uid + ".jpg");
                                            imgprev.setDrawingCacheEnabled(true);
                                            imgprev.buildDrawingCache();
                                            Drawable drawable = imgprev.getDrawable();

                                            Bitmap bitmap;
                                            if (drawable instanceof BitmapDrawable) {
                                                bitmap = ((BitmapDrawable) drawable).getBitmap();
                                            } else if (drawable instanceof VectorDrawable || drawable instanceof VectorDrawableCompat) {
                                                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                                                Canvas canvas = new Canvas(bitmap);
                                                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                                                drawable.draw(canvas);
                                            } else {
                                                Toast.makeText(RegisterCustomerActivity.this, "Unsupported image type", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            byte[] imageData = baos.toByteArray();

                                            UploadTask uploadTask = storageRef.putBytes(imageData);
                                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri downloadUri) {
                                                            // Image uploaded successfully
                                                            String imageUrl = downloadUri.toString();
                                                            // Save the image URL to the user's database record
                                                            userRef.child("profileImageUrl").setValue(imageUrl);

                                                            // Save other user information
                                                            userRef.child("email").setValue(email);
                                                            userRef.child("fullName").setValue(fullNameTxt);
                                                            userRef.child("phoneNumber").setValue(phonenumberTxt);
                                                            userRef.child("street").setValue(streetTxt);
                                                            userRef.child("district").setValue(districtTxt);
                                                            userRef.child("barangay").setValue(barangayTxt);

                                                            if (!TextUtils.isEmpty(otherlocationTxt)) {
                                                                userRef.child("deliveryInstruction").setValue(otherlocationTxt);
                                                            }

                                                            Toast.makeText(RegisterCustomerActivity.this, "Account has been registered", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(RegisterCustomerActivity.this, LoginActivity.class));
                                                            finish();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Handle image upload failure
                                                    Toast.makeText(RegisterCustomerActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } else {
                                        Toast.makeText(RegisterCustomerActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }

            }
        });






    }
}
