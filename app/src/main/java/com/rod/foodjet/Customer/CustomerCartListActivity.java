package com.rod.foodjet.Customer;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rod.foodjet.Adapter.CartListAdapter;
import com.rod.foodjet.Domain.FoodDomain;
import com.rod.foodjet.Helper.ManagementCart;
import com.rod.foodjet.Interface.ChangeNumberItemsListener;
import com.rod.foodjet.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CustomerCartListActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerViewList;
    private ManagementCart managementCart;
    private ArrayList<FoodDomain> foodDomains;
    private TextView totalFeeTxt, taxTxt, deliveryTxt, totalTxt, emptyTxt, mycart, checkout;
    private double tax;
    private ScrollView scrollView;

    private CartListAdapter.ViewHolder holder;
    private ChangeNumberItemsListener changeNumberItemsListener;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);

        managementCart = new ManagementCart(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();


        initView();
        initlist();
        CalculateCart();
        bottomNavigation();
    }

    private void bottomNavigation() {
        FloatingActionButton floatingActionButton = findViewById(R.id.cartBtn);
        LinearLayout homebtn = findViewById(R.id.homeBtn);
        LinearLayout profileBtn = findViewById(R.id.profileBtn);
        LinearLayout settingsBtn = findViewById(R.id.settingsBtn);
        LinearLayout supportBtn = findViewById(R.id.supportBtn);


        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerCartListActivity.this, CustomerMainActivity.class));
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerCartListActivity.this, CustomerProfileActivity.class));
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerCartListActivity.this, CustomerSettingsActivity.class));
            }
        });

        supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerCartListActivity.this, CustomerSupportActivity.class));
            }
        });


    }

    private void initView() {
        recyclerViewList = findViewById(R.id.recyclerView);
        totalFeeTxt = findViewById(R.id.totalFeeTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalTxt = findViewById(R.id.totalTxt);
        emptyTxt = findViewById(R.id.emptyTxt);
        scrollView = findViewById(R.id.scrollView2);
        recyclerViewList = findViewById(R.id.cartView2);
        mycart = findViewById(R.id.mycart);
        checkout = findViewById(R.id.checkout);
    }


    private void initlist() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewList.setLayoutManager(linearLayoutManager);
        adapter = new CartListAdapter(managementCart.getListCart(), this, new ChangeNumberItemsListener() {
            @Override
            public void changed() {
                CalculateCart();
            }
        }) {
        };

        recyclerViewList.setAdapter(adapter);
        if(managementCart.getListCart().isEmpty()) {
            emptyTxt.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            mycart.setVisibility(View.GONE);

        }
        else {
            emptyTxt.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            mycart.setVisibility(View.VISIBLE);
        }

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String uid = currentUser.getUid();

                ArrayList<FoodDomain> cartItems = managementCart.getListCart();

                String currentDate = DateFormat.getDateInstance().format(new Date());

                Calendar calendar = Calendar.getInstance();
                TimeZone.setDefault(TimeZone.getTimeZone("Asia/Manila"));
                calendar.setTimeZone(TimeZone.getDefault());

                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
                String currentTime = timeFormat.format(calendar.getTime());

                DatabaseReference currentDateRef = FirebaseDatabase.getInstance().getReference().child("orders").child(uid).child(currentDate).child(currentTime);

                for (FoodDomain item : cartItems) {
                    DatabaseReference ordersRef = currentDateRef.child("orders");
                    Query query = ordersRef.orderByChild("title").equalTo(item.getTitle());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean orderExists = false;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                orderExists = true;
                                int currentNumberInCart = snapshot.child("numberInCart").getValue(Integer.class);
                                int newNumberInCart = currentNumberInCart + item.getNumberInCart();
                                snapshot.getRef().child("numberInCart").setValue(newNumberInCart);
                            }
                            if (!orderExists) {
                                DatabaseReference newItemRef = ordersRef.push();
                                newItemRef.child("title").setValue(item.getTitle());
                                newItemRef.child("fee").setValue(item.getFee());
                                newItemRef.child("numberInCart").setValue(item.getNumberInCart());
                                newItemRef.child("pic").setValue(item.getPic());
                                newItemRef.child("login_date").setValue(currentUser.getMetadata().getLastSignInTimestamp()); // add the user's login date to the order
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled", databaseError.toException());
                        }
                    });
                }

                managementCart.clearCart();

                Toast.makeText(CustomerCartListActivity.this, "Your order has been made", Toast.LENGTH_SHORT).show();


            }
        });




    }



    private void CalculateCart() {
        double delivery = 10;

        double total = Math.round((managementCart.getTotalFee() + tax + delivery) * 100) / 100;
        double itemTotal = Math.round(managementCart.getTotalFee() * 100) / 100;

        totalFeeTxt.setText("₱" + itemTotal);
        deliveryTxt.setText("₱" + delivery);
        totalTxt.setText("₱" + total);
    }
}