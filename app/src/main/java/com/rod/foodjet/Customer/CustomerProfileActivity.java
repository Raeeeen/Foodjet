package com.rod.foodjet.Customer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rod.foodjet.R;

public class CustomerProfileActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://foodjet-edcf4-default-rtdb.firebaseio.com/");

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bottomNavigation();
        buttonFunctions();



    }

    public void buttonFunctions() {

        TextView personname = findViewById(R.id.personname);
        TextView personPhoneNumber = findViewById(R.id.personPhoneNumber);
        TextView personemail = findViewById(R.id.personemail);
        TextView personstreet = findViewById(R.id.personstreet);
        TextView persondistrict = findViewById(R.id.persondistrict);
        TextView personbarangay = findViewById(R.id.personbarangay);
        ImageView personpic = findViewById(R.id.personpic);
        EditText persondeliveryinstruction = findViewById(R.id.persondeliveryinstruction);

        ImageView orders = findViewById(R.id.orders);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child("Customer").child(user.getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            // Function to create a circular bitmap from the given bitmap using BitmapShader
            private Bitmap getRoundedBitmap(Bitmap bitmap) {
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int radius = Math.min(width, height) / 2;

                Bitmap circularBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(circularBitmap);
                Paint paint = new Paint();
                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                paint.setShader(shader);
                paint.setAntiAlias(true);

                canvas.drawCircle(width / 2, height / 2, radius, paint);

                return circularBitmap;
            }
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String personnameTxt = snapshot.child("fullName").getValue(String.class);
                String personPhoneNumberTxt = snapshot.child("phoneNumber").getValue(String.class);
                String personEmailTxt = snapshot.child("email").getValue(String.class);
                String personStreetTxt = snapshot.child("street").getValue(String.class);
                String personDistrictTxt = snapshot.child("district").getValue(String.class);
                String personBarangayTxt = snapshot.child("barangay").getValue(String.class);
                String personDeliveryInstruction = snapshot.child("deliveryInstruction").getValue(String.class);

                personname.setText(personnameTxt);
                personPhoneNumber.setText(personPhoneNumberTxt);
                personemail.setText(personEmailTxt);
                personstreet.setText(personStreetTxt);
                persondistrict.setText(personDistrictTxt);
                personbarangay.setText(personBarangayTxt);
                persondeliveryinstruction.setText(personDeliveryInstruction);

                // Retrieve profile image from Firebase Storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("users").child("Customers").child(user.getUid()).child("profilePicture").child(user.getUid() + ".jpg");
                final long ONE_MEGABYTE = 1024 * 1024;
                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Convert bytes to bitmap
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        // Create a circular bitmap using a BitmapShader
                        Bitmap circularBitmap = getRoundedBitmap(bitmap);

                        // Set the circular bitmap to the ImageView
                        personpic.setImageBitmap(circularBitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to retrieve the profile image
                        Toast.makeText(CustomerProfileActivity.this, "Failed to retrieve profile image", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }


        private void bottomNavigation() {
        FloatingActionButton floatingActionButton = findViewById(R.id.cartBtn);
        LinearLayout homebtn = findViewById(R.id.homeBtn);
        LinearLayout profileBtn = findViewById(R.id.profileBtn);
        LinearLayout supportBtn = findViewById(R.id.supportBtn);
        LinearLayout settingsBtn = findViewById(R.id.settingsBtn);



        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerProfileActivity.this, CustomerCartListActivity.class));
            }
        });

        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerProfileActivity.this, CustomerMainActivity.class));
            }
        });

        supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerProfileActivity.this, CustomerSupportActivity.class));
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerProfileActivity.this, CustomerSettingsActivity.class));
            }
        });

    }


}
