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

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<FoodItem> mFoodItemArrayList = new ArrayList<>();
    private ArrayList<FoodItem> mFoodItemArrayListAll = new ArrayList<>();
    private Context mContext;
    private int lastPosition = -1;

    FoodItemAdapter(Context context, ArrayList<FoodItem> foodItems) {
        this.mFoodItemArrayList = foodItems;
        this.mFoodItemArrayListAll = foodItems;
        this.mContext = context;
    }

    @Override
    public FoodItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(FoodItemAdapter.ViewHolder holder, int position) {
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

    @Override
    public Filter getFilter() {
        return foodItemFilter;
    }

    private Filter foodItemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<FoodItem> filteredList = new ArrayList<>();
            FilterResults filterResults = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                filterResults.count = mFoodItemArrayListAll.size();
                filterResults.values = mFoodItemArrayListAll;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (FoodItem item :
                        mFoodItemArrayListAll) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                filterResults.count = filteredList.size();
                filterResults.values = filteredList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            mFoodItemArrayList = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };


    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mItemName;
        private TextView mItemCalories;
        private TextView mItemPrice;
        private RatingBar mRatingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemName = itemView.findViewById(R.id.foodItemName);
            mItemCalories = itemView.findViewById(R.id.calorieValue);
            mItemPrice = itemView.findViewById(R.id.foodItemPrice);
            mRatingBar = itemView.findViewById(R.id.ratingBar);
        }

        public void bindTo(FoodItem currentItem) {
            mItemName.setText(currentItem.getName());
            mItemCalories.setText(currentItem.getCalories() + " kcal");
            mItemPrice.setText(currentItem.getPrice() + " Ft");
            mRatingBar.setRating(currentItem.getRatedInfo());

            itemView.findViewById(R.id.add_to_daily_intake).setOnClickListener(view -> {
                ((FoodsActivity) mContext).updateAlertIcon(currentItem);

                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.add_to_daily_intake_animation);
                itemView.startAnimation(animation);
            });
            itemView.findViewById(R.id.delete_food).setOnClickListener(view -> ((FoodsActivity) mContext).deleteFood(currentItem));
        }
    }
}