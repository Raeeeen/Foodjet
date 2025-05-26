package com.rod.foodjet.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rod.foodjet.R;
import com.rod.foodjet.Seller.SellerFoodsListActivity;
import com.rod.foodjet.Seller.SellerMainActivity;
import com.rod.foodjet.Seller.SellerProfileActivity;
import com.rod.foodjet.Seller.SellerSettingsActivity;

import java.util.HashMap;
import java.util.Map;

public class SellerAddFoodsActivity extends AppCompatActivity {

    private ImageButton foodimg;
    private EditText foodname, foodprice;
    private Button addFoodBtn;
    private static final int SELECT_IMAGE_REQUEST_CODE = 100;
    private Uri selectedImageUri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selleraddfoods);

        bottomNavigation();
        functions();


    }


    private void functions() {

        foodimg = findViewById(R.id.foodimg);
        foodname = findViewById(R.id.foodname);
        foodprice = findViewById(R.id.foodprice);
        addFoodBtn = findViewById(R.id.addfoodBtn);

        foodimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            Glide.with(this)
                    .load(selectedImageUri)
                    .into(foodimg);
        }


        addFoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String shopName = getIntent().getStringExtra("shopName");

                uploadImageToStorage(shopName);
            }
        });


    }

    private void uploadImageToStorage(String shopName) {
        if (selectedImageUri != null) {

            String foodNameValue = foodname.getText().toString().trim();
            String foodPriceValue = foodprice.getText().toString().trim();

            if (foodimg.getDrawable() == null) {
                Toast.makeText(SellerAddFoodsActivity.this, "Please add a food image", Toast.LENGTH_SHORT).show();
                return;
            }

            if (foodNameValue.isEmpty()) {
                Toast.makeText(SellerAddFoodsActivity.this, "Please enter the food name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (foodPriceValue.isEmpty()) {
                Toast.makeText(SellerAddFoodsActivity.this, "Please enter the food price", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {
                String userId = currentUser.getUid();

                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                StorageReference imageRef = storageRef.child("users")
                        .child("Sellers")
                        .child(userId)
                        .child("shops")
                        .child(shopName)
                        .child("foodsPic")
                        .child(foodNameValue)
                        .child(selectedImageUri.getLastPathSegment());

                imageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUri) {
                                        saveFoodsDetails(downloadUri.toString(), userId, shopName);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SellerAddFoodsActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(SellerAddFoodsActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }



    private void saveFoodsDetails(String foodImageUri, String userId, String shopName) {
        String foodNameValue = foodname.getText().toString().trim();
        String foodPriceValue = foodprice.getText().toString().trim();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference()
                .child("shops")
                .child(userId)
                .child(shopName)
                .child("foodsList");

        databaseRef.child(foodNameValue).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(SellerAddFoodsActivity.this, "Food entry already exists", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> foodMap = new HashMap<>();
                    foodMap.put("foodname", foodNameValue);
                    foodMap.put("foodprice", foodPriceValue);
                    foodMap.put("foodspic", foodImageUri);

                    databaseRef.child(foodNameValue).setValue(foodMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Handle successful data saving
                                    Toast.makeText(SellerAddFoodsActivity.this, "Food details saved successfully", Toast.LENGTH_SHORT).show();

                                    foodname.setText("");
                                    foodprice.setText("");
                                    foodimg.setImageDrawable(null);

                                    startActivity(new Intent(SellerAddFoodsActivity.this, SellerMainActivity.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SellerAddFoodsActivity.this, "Failed to save food details", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the cancellation if needed
            }
        });
    }





    private void bottomNavigation() {
        LinearLayout homebtn = findViewById(R.id.homeBtn);
        LinearLayout profileBtn = findViewById(R.id.profileBtn);
        LinearLayout supportBtn = findViewById(R.id.supportBtn);
        LinearLayout settingsBtn = findViewById(R.id.settingsBtn);


        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerAddFoodsActivity.this, SellerMainActivity.class));
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerAddFoodsActivity.this, SellerProfileActivity.class));
            }
        });

        supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerAddFoodsActivity.this, SellerSettingsActivity.class));
            }
        });
    }



}
