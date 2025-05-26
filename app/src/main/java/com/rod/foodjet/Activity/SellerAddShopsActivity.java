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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rod.foodjet.R;
import com.rod.foodjet.Seller.SellerMainActivity;
import com.rod.foodjet.Seller.SellerProfileActivity;
import com.rod.foodjet.Seller.SellerSettingsActivity;

import java.util.HashMap;
import java.util.Map;

public class SellerAddShopsActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapter, adapter2;
    private RecyclerView recyclerViewCategoryList, recyclerViewBFC;
    private static final int SELECT_IMAGE_REQUEST_CODE = 100;
    private ImageButton addshopimg;
    private EditText shopname;
    private EditText shoplocation;
    private Uri selectedImageUri;
    private Button addbtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selleraddshop);

        bottomNavigation();
        functions();


    }


    private void functions() {
        addshopimg = findViewById(R.id.shopimg);
        shopname = findViewById(R.id.shopname);
        shoplocation = findViewById(R.id.shoplocation);
        addbtn = findViewById(R.id.addbtn);

        addshopimg.setOnClickListener(new View.OnClickListener() {
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
                    .into(addshopimg);
        }


        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageToStorage();
            }
        });

    }

    private void uploadImageToStorage() {
        if (selectedImageUri != null) {

            String shopnameValue = shopname.getText().toString().trim();
            String shoplocationValue = shoplocation.getText().toString().trim();

            if(addshopimg.getDrawable() == null) {
                Toast.makeText(SellerAddShopsActivity.this, "Please add a shop image", Toast.LENGTH_SHORT).show();
                return;
            }

            if(shopnameValue.isEmpty()) {
                Toast.makeText(SellerAddShopsActivity.this, "Please enter the shop name", Toast.LENGTH_SHORT).show();
                return;
            }

            if(shoplocationValue.isEmpty()) {
                Toast.makeText(SellerAddShopsActivity.this, "Please enter the shop location", Toast.LENGTH_SHORT).show();
                return;
            }


            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            String userId = firebaseAuth.getCurrentUser().getUid();

            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("users").child("Sellers").child(userId).child("shops").child(shopname.getText().toString().trim()).child("shopPic").child(selectedImageUri.getLastPathSegment());

            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUri) {
                                    saveShopDetails(downloadUri.toString(), userId);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SellerAddShopsActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveShopDetails(String shopImageUri, String userId) {
        String shopNameValue = shopname.getText().toString().trim();
        String shopLocationValue = shoplocation.getText().toString().trim();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("shops").child(userId);

        databaseRef.child(shopNameValue).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(SellerAddShopsActivity.this, "Shop entry already exists", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> shopMap = new HashMap<>();
                    shopMap.put("shopname", shopNameValue);
                    shopMap.put("shoplocation", shopLocationValue);
                    shopMap.put("shopspic", shopImageUri);

                    databaseRef.child(shopNameValue).setValue(shopMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Handle successful data saving
                                    Toast.makeText(SellerAddShopsActivity.this, "Shop details saved successfully", Toast.LENGTH_SHORT).show();

                                    shopname.setText("");
                                    shoplocation.setText("");
                                    addshopimg.setImageDrawable(null);

                                    startActivity(new Intent(SellerAddShopsActivity.this, SellerMainActivity.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SellerAddShopsActivity.this, "Failed to save shop details", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                startActivity(new Intent(SellerAddShopsActivity.this, SellerMainActivity.class));
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerAddShopsActivity.this, SellerProfileActivity.class));
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
                startActivity(new Intent(SellerAddShopsActivity.this, SellerSettingsActivity.class));
            }
        });
    }



}
