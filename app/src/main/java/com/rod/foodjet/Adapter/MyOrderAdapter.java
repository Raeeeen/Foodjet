package com.rod.foodjet.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rod.foodjet.Domain.FoodDomain;
import com.rod.foodjet.Helper.ManagementCart;
import com.rod.foodjet.Interface.ChangeNumberItemsListener;
import com.rod.foodjet.R;

import java.util.ArrayList;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {

    private ArrayList<FoodDomain> foodDomains;
    private ManagementCart managementCart;
    private ChangeNumberItemsListener changeNumberItemsListener;

    public MyOrderAdapter(ArrayList<FoodDomain> foodDomains, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.foodDomains = foodDomains;
        this.managementCart = new ManagementCart(context);
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @Override
    public MyOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_oders, parent, false);

        return new ViewHolder(inflate);
    }

    public void onBindViewHolder(@NonNull MyOrderAdapter.ViewHolder holder, int position) {
        holder.title.setText(foodDomains.get(holder.getAdapterPosition()).getTitle());
        holder.feeEachItem.setText(String.valueOf(foodDomains.get(holder.getAdapterPosition()).getFee()));
        holder.totalEachItem.setText(String.valueOf(Math.round((foodDomains.get(holder.getAdapterPosition()).getNumberInCart() * foodDomains.get(holder.getAdapterPosition()).getFee()) * 100) / 100));
        holder.num.setText(String.valueOf(foodDomains.get(holder.getAdapterPosition()).getNumberInCart()));

        int drawableResourceId = holder.itemView.getContext().getResources().getIdentifier(foodDomains.get(holder.getAdapterPosition()).getPic()
                , "drawable", holder.itemView.getContext().getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(drawableResourceId)
                .into(holder.pic);

    }

    @Override
    public int getItemCount() {
        return foodDomains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, feeEachItem;
        ImageView pic, plusItem, minusItem, removeItem;
        TextView totalEachItem, num;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            pic = itemView.findViewById(R.id.picCart);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            num = itemView.findViewById(R.id.numberItemTxt);
        }
    }
}