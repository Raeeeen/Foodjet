package com.rod.foodjet.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.rod.foodjet.Customer.CustomerShowDetailActivity;
import com.rod.foodjet.Domain.FoodDomain;
import com.rod.foodjet.R;

import java.util.ArrayList;

public class FoodsListAdapter extends RecyclerView.Adapter<FoodsListAdapter.ViewHolder> {

    ArrayList<FoodDomain> foodslist;

    public FoodsListAdapter(ArrayList<FoodDomain> popularFood) {
        this.foodslist = foodslist;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_foodslist, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(foodslist.get(holder.getAdapterPosition()).getTitle());
        holder.fee.setText(String.valueOf(foodslist.get(holder.getAdapterPosition()).getFee()));

        int drawbaleResourceId = holder.itemView.getContext().getResources().getIdentifier(foodslist.get(holder.getAdapterPosition()).getPic(), "drawable", holder.itemView.getContext().getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(drawbaleResourceId)
                .into(holder.pic);

        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), CustomerShowDetailActivity.class);
                intent.putExtra("object", foodslist.get(holder.getAdapterPosition()));
                holder.itemView.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return foodslist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, fee;
        ImageView pic;
        TextView addBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            fee = itemView.findViewById(R.id.fee);
            pic = itemView.findViewById(R.id.pic);
            addBtn = itemView.findViewById(R.id.addBtn);


        }
    }
}
