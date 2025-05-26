package com.rod.foodjet.Customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rod.foodjet.Adapter.MyOrderAdapter;
import com.rod.foodjet.Domain.FoodDomain;
import com.rod.foodjet.Helper.ManagementCart;
import com.rod.foodjet.Interface.ChangeNumberItemsListener;
import com.rod.foodjet.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomerMyOrderActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerViewList;
    private ManagementCart managementCart;
    private ArrayList<FoodDomain> foodDomains;
    private TextView totalFeeTxt, taxTxt, deliveryTxt, totalTxt, emptyTxt, mycart, checkout, noorders, myorder;
    private double tax;
    private ScrollView scrollView;
    private LinearLayout itemtotallayout, deliverylayout, totallayout;

    private DatabaseReference databaseReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_order);

        initView();
        initlist();
        bottomNavigation();
        CalculateCart();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewList.setLayoutManager(linearLayoutManager);
        adapter = new MyOrderAdapter(managementCart.getListCart(), this, new ChangeNumberItemsListener() {
            @Override
            public void changed() {
                CalculateCart();
            }
        }) {
        };

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            String currentDate = DateFormat.getDateInstance().format(new Date());

            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("orders").child(uid).child(currentDate);
            ordersRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                        for (DataSnapshot orderTimeSnapshot : snapshot.getChildren()) {
                            DatabaseReference ordersTimeRef = orderTimeSnapshot.child("orders").getRef();
                            ordersTimeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                                        ArrayList<FoodDomain> orderList = new ArrayList<>();
                                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                                            FoodDomain order = orderSnapshot.getValue(FoodDomain.class);
                                            orderList.add(order);
                                        }
                                        adapter = new MyOrderAdapter(orderList, CustomerMyOrderActivity.this, new ChangeNumberItemsListener() {
                                            @Override
                                            public void changed() {
                                                CalculateCart();
                                            }
                                        });
                                        recyclerViewList.setAdapter(adapter);
                                        recyclerViewList.setVisibility(View.VISIBLE); // Show the list view

                                        myorder.setVisibility(View.VISIBLE);
                                        totalFeeTxt.setVisibility(View.VISIBLE);
                                        deliveryTxt.setVisibility(View.VISIBLE);
                                        totalTxt.setVisibility(View.VISIBLE);
                                        itemtotallayout.setVisibility(View.VISIBLE);
                                        deliverylayout.setVisibility(View.VISIBLE);
                                        totallayout.setVisibility(View.VISIBLE);
                                        noorders.setVisibility(View.GONE);
                                    } else {
                                        recyclerViewList.setVisibility(View.GONE);
                                        myorder.setVisibility(View.GONE);
                                        itemtotallayout.setVisibility(View.GONE);
                                        deliverylayout.setVisibility(View.GONE);
                                        totallayout.setVisibility(View.GONE);
                                        noorders.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(CustomerMyOrderActivity.this, "Failed to retrieve orders", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CustomerMyOrderActivity.this, "Failed to retrieve orders", Toast.LENGTH_SHORT).show();
                }
            });
        }



    }



    private void initView() {
        recyclerViewList = findViewById(R.id.cartView2);
        totalFeeTxt = findViewById(R.id.totalFeeTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalTxt = findViewById(R.id.totalTxt);
        emptyTxt = findViewById(R.id.emptyTxt);
        scrollView = findViewById(R.id.scrollView2);
        mycart = findViewById(R.id.mycart);
        noorders = findViewById(R.id.noorder);
        myorder = findViewById(R.id.myorder);

        itemtotallayout = findViewById(R.id.itemtotallayout);
        deliverylayout = findViewById(R.id.deliverylayout);
        totallayout = findViewById(R.id.totallayout);
    }

    private void initlist() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewList.setLayoutManager(linearLayoutManager);
        managementCart = new ManagementCart(this);
        adapter = new MyOrderAdapter(managementCart.getListCart(), this, new ChangeNumberItemsListener() {
            @Override
            public void changed() {
                CalculateCart();
            }
        }) {
        };
        recyclerViewList.setAdapter(adapter);
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
                startActivity(new Intent(CustomerMyOrderActivity.this, CustomerCartListActivity.class));
            }
        });

        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerMyOrderActivity.this, CustomerMainActivity.class));
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerMyOrderActivity.this, CustomerProfileActivity.class));
            }
        });

        supportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerMyOrderActivity.this, CustomerSupportActivity.class));
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerMyOrderActivity.this, CustomerSettingsActivity.class));
            }
        });

    }

    private double totalFee;

    private void CalculateCart() {
        totalFee = 0.0;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            String currentDate = DateFormat.getDateInstance().format(new Date());

            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("orders").child(uid).child(currentDate);

            ordersRef.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                        for (DataSnapshot orderTimeSnapshot : snapshot.getChildren()) {
                            DatabaseReference ordersTimeRef = orderTimeSnapshot.child("orders").getRef();

                            ordersTimeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    double orderTotalFee = 0.0;
                                    boolean hasOrders = false;

                                    if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                                        ArrayList<FoodDomain> orderList = new ArrayList<>();

                                        for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                                            FoodDomain order = orderSnapshot.getValue(FoodDomain.class);

                                            if (order != null && order.getFee() != null) {
                                                orderTotalFee += order.getFee() * order.getNumberInCart();
                                                orderList.add(order);
                                                hasOrders = true;
                                            }
                                        }

                                        if (hasOrders) {
                                            adapter = new MyOrderAdapter(orderList, CustomerMyOrderActivity.this, new ChangeNumberItemsListener() {
                                                @Override
                                                public void changed() {
                                                    CalculateCart();
                                                }
                                            });

                                            recyclerViewList.setAdapter(adapter);
                                            recyclerViewList.setVisibility(View.VISIBLE);
                                            myorder.setVisibility(View.VISIBLE);
                                            totalFeeTxt.setVisibility(View.VISIBLE);
                                            deliveryTxt.setVisibility(View.VISIBLE);
                                            totalTxt.setVisibility(View.VISIBLE);
                                            itemtotallayout.setVisibility(View.VISIBLE);
                                            deliverylayout.setVisibility(View.VISIBLE);
                                            totallayout.setVisibility(View.VISIBLE);
                                            noorders.setVisibility(View.GONE);

                                            double deliveryFee = 10.0;
                                            totalFee += orderTotalFee + deliveryFee;

                                            totalFeeTxt.setText("₱" + String.format("%.2f", orderTotalFee));
                                            deliveryTxt.setText("₱" + String.format("%.2f", deliveryFee));
                                            totalTxt.setText("₱" + String.format("%.2f", totalFee));

                                        } else {
                                            recyclerViewList.setVisibility(View.GONE);
                                            myorder.setVisibility(View.GONE);
                                            itemtotallayout.setVisibility(View.GONE);
                                            deliverylayout.setVisibility(View.GONE);
                                            totallayout.setVisibility(View.GONE);
                                            noorders.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        recyclerViewList.setVisibility(View.GONE);
                                        myorder.setVisibility(View.GONE);
                                        itemtotallayout.setVisibility(View.GONE);
                                        deliverylayout.setVisibility(View.GONE);
                                        totallayout.setVisibility(View.GONE);
                                        noorders.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(CustomerMyOrderActivity.this, "Failed to retrieve orders", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        recyclerViewList.setVisibility(View.GONE);
                        myorder.setVisibility(View.GONE);
                        itemtotallayout.setVisibility(View.GONE);
                        deliverylayout.setVisibility(View.GONE);
                        totallayout.setVisibility(View.GONE);
                        noorders.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CustomerMyOrderActivity.this, "Failed to retrieve orders", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
