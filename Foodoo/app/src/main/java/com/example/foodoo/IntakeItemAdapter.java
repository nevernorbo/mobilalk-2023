package com.example.foodoo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IntakeItemAdapter extends RecyclerView.Adapter<IntakeItemAdapter.ViewHolder> {

    private ArrayList<FoodItem> mFoodItemArrayList = new ArrayList<>();
    private ArrayList<FoodItem> mFoodItemArrayListAll = new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;

    public IntakeItemAdapter(Context mContext, ArrayList<FoodItem> mFoodItemArrayList) {
        this.mFoodItemArrayList = mFoodItemArrayList;
        this.mFoodItemArrayListAll = mFoodItemArrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public IntakeItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new IntakeItemAdapter.ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.intake_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IntakeItemAdapter.ViewHolder holder, int position) {
        FoodItem currentItem = mFoodItemArrayList.get(position);

        holder.bindTo(currentItem);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }


    @Override
    public int getItemCount() {
        return mFoodItemArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mItemName;
        private TextView mItemCalories;
        private TextView mItemPrice;
        private TextView mStoredCount;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemName = itemView.findViewById(R.id.foodItemName);
            mItemCalories = itemView.findViewById(R.id.calorieValue);
            mItemPrice = itemView.findViewById(R.id.foodItemPrice);
            mStoredCount = itemView.findViewById(R.id.storedFoodCount);
        }

        public void bindTo(FoodItem currentItem) {
            mItemName.setText(currentItem.getName());
            mItemCalories.setText(currentItem.getCalories() + " kcal");
            mItemPrice.setText(currentItem.getPrice() + " Ft");
            mStoredCount.setText("x " + currentItem.getStoredCount());

            itemView.findViewById(R.id.delete_added_food).setOnClickListener(view -> ((IntakeActivity) mContext).deleteStoredFood(currentItem));
        }
    }
}
