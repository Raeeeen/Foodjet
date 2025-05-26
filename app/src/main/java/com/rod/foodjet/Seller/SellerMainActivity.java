package com.rod.foodjet.Seller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rod.foodjet.Activity.SellerAddShopsActivity;
import com.rod.foodjet.Adapter.SellerShopsListAdapter;
import com.rod.foodjet.Domain.SellerShopsDomain;
import com.rod.foodjet.R;

import java.util.ArrayList;

public class SellerMainActivity extends AppCompatActivity {


    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellermain);


        bottomNavigation();
        mainFunctions();

    }

    private void mainFunctions() {
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<SellerShopsDomain> sellerShopsDomains = new ArrayList<>();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("shops").child(userId);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot shopSnapshot : snapshot.getChildren()) {
                        String shopName = shopSnapshot.child("shopname").getValue(String.class);
                        String shopLocation = shopSnapshot.child("shoplocation").getValue(String.class);
                        if (shopName != null && shopLocation != null) {
                            sellerShopsDomains.add(new SellerShopsDomain(shopName, shopLocation));
                        }
                    }
                    SellerShopsListAdapter adapter = new SellerShopsListAdapter(sellerShopsDomains);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error case if needed
            }
        });
    }




    private void bottomNavigation() {
        LinearLayout profileBtn = findViewById(R.id.profileBtn);
        LinearLayout supportBtn = findViewById(R.id.supportBtn);
        LinearLayout settingsBtn = findViewById(R.id.settingsBtn);
        LinearLayout addshopBtn = findViewById(R.id.addshopbtn);


        addshopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerMainActivity.this, SellerAddShopsActivity.class));
            }
        });


        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerMainActivity.this, SellerProfileActivity.class));
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
                startActivity(new Intent(SellerMainActivity.this, SellerSettingsActivity.class));
            }
        });

    }


}