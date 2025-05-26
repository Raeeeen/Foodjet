package com.rod.foodjet.Seller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rod.foodjet.Activity.SellerAddFoodsActivity;
import com.rod.foodjet.Domain.FoodDomain;
import com.rod.foodjet.R;

import java.util.ArrayList;

public class SellerFoodsListActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapter, adapter2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sellerfoodslist);

        bottomNavigation();
        recyclerViewBFC();


    }

    private void bottomNavigation() {
        LinearLayout homebtn = findViewById(R.id.homeBtn);
        LinearLayout profileBtn = findViewById(R.id.profileBtn);
        LinearLayout supportBtn = findViewById(R.id.supportBtn);
        LinearLayout settingsBtn = findViewById(R.id.settingsBtn);


        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerFoodsListActivity.this, SellerMainActivity.class));
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerFoodsListActivity.this, SellerProfileActivity.class));
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
                startActivity(new Intent(SellerFoodsListActivity.this, SellerSettingsActivity.class));
            }
        });
    }

    private void recyclerViewBFC() {
        final TextView shopsName = findViewById(R.id.shopsname);
        final RecyclerView foodslist = findViewById(R.id.foodslist);
        final Button addfood = findViewById(R.id.addfood);

        addfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shopName = shopsName.getText().toString();

                Intent intent = new Intent(SellerFoodsListActivity.this, SellerAddFoodsActivity.class);
                intent.putExtra("shopName", shopName);
                startActivity(intent);
            }
        });


        String shopName = getIntent().getStringExtra("shopName");
        shopsName.setText(shopName);

        int spanCount = 3;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount, GridLayoutManager.HORIZONTAL, false);

        ArrayList<FoodDomain> foodDomains = new ArrayList<>();

        foodslist.setLayoutManager(gridLayoutManager);


    }




}
