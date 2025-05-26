package com.rod.foodjet.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rod.foodjet.Domain.CategoryDomain;
import com.rod.foodjet.R;

import java.util.ArrayList;

public class ShopListsAdapter extends RecyclerView.Adapter<ShopListsAdapter.ViewHolder> {

    ArrayList<CategoryDomain> categoryDomains;

    public ShopListsAdapter(ArrayList<CategoryDomain> categoryDomains) {
        this.categoryDomains = categoryDomains;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_customershopslist,parent,false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.shopname.setText(categoryDomains.get(position).getTitle());
        String picUrl = "";
        switch (position) {
            case 0: {
                picUrl = "shopsample";
                break;
            }

        }

        int drawbaleResourceId = holder.itemView.getContext().getResources().getIdentifier(picUrl, "drawable", holder.itemView.getContext().getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(drawbaleResourceId)
                .into(holder.shoppic);
    }

    @Override
    public int getItemCount() {
        return categoryDomains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView shopname;
        ImageView shoppic;
        ConstraintLayout mainlayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            shopname = itemView.findViewById(R.id.shopname);
            shoppic = itemView.findViewById(R.id.shoppic);
            mainlayout = itemView.findViewById(R.id.mainLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                    }
                }
            });
        }





    }
    }

