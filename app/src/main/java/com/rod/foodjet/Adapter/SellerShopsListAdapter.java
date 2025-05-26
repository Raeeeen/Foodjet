package com.rod.foodjet.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

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
import com.rod.foodjet.Seller.SellerFoodsListActivity;
import com.rod.foodjet.Domain.SellerShopsDomain;
import com.rod.foodjet.R;

import java.util.ArrayList;

public class SellerShopsListAdapter extends RecyclerView.Adapter<SellerShopsListAdapter.ViewHolder> {

    ArrayList<SellerShopsDomain> sellerShopsDomains;

    public SellerShopsListAdapter(ArrayList<SellerShopsDomain> sellerShopsDomains) {
        this.sellerShopsDomains = sellerShopsDomains;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_sellershops,parent,false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String shopName = sellerShopsDomains.get(position).getTitle();
        String shoplocation = sellerShopsDomains.get(position).getShoplocation();
        holder.shopname.setText(shopName);
        holder.shoplocation.setText(shoplocation);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference()
                    .child("shops")
                    .child(userId);

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot shopSnapshot : snapshot.getChildren()) {
                            String shopSnapshotName = shopSnapshot.child("shopname").getValue(String.class);
                            if (shopName != null && shopName.equals(shopSnapshotName)) {
                                String shopImageUri = shopSnapshot.child("shopspic").getValue(String.class);

                                Glide.with(holder.itemView.getContext())
                                        .load(shopImageUri)
                                        .into(holder.shoppic);

                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the error case if needed
                }
            });

        }
    }



    @Override
    public int getItemCount() {
        return sellerShopsDomains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView shopname, shoplocation;
        ImageView shoppic;
        ConstraintLayout mainlayout;
        ImageButton editBtn, deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            shopname = itemView.findViewById(R.id.shopname);
            shoppic = itemView.findViewById(R.id.shoppic);
            shoplocation = itemView.findViewById(R.id.shoplocation);
            mainlayout = itemView.findViewById(R.id.mainLayout);
            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        SellerShopsDomain clickedItem = sellerShopsDomains.get(position);
                        String shopName = clickedItem.getTitle();


                        Intent intent = new Intent(itemView.getContext(), SellerFoodsListActivity.class);
                        intent.putExtra("shopName", shopName);
                        itemView.getContext().startActivity(intent);
                    }
                }
            });


            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                    }
                }
            });


        }

        private void deleteItem(int position) {
            SellerShopsDomain deletedItem = sellerShopsDomains.get(position);
            sellerShopsDomains.remove(position);
            notifyItemRemoved(position);

            // Delete the image from storage
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference()
                    .child("shops")
                    .child(userId)
                    .child(deletedItem.getTitle());

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String imageUrl = snapshot.child("shopPic").getValue(String.class);
                        if (imageUrl != null) {
                            // Get a reference to the Firebase Storage location
                            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

                            // Delete the image from storage
                            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Image deleted successfully from storage
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle any errors that occur during image deletion
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle the error case if needed
                }
            });

            // Delete the shop from the database
            databaseRef.removeValue();
        }


    }



}

