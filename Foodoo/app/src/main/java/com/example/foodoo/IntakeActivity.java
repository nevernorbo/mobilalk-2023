package com.example.foodoo;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class IntakeActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    // Firebase
    private FirebaseUser user;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    private RecyclerView mRecyclerView;
    private ArrayList<FoodItem> mItemList;
    private IntakeItemAdapter mIntakeItemAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intake);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerViewStoredFoods);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mItemList = new ArrayList<>();

        mIntakeItemAdapter = new IntakeItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mIntakeItemAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        queryData();
    }

    private void queryData() {
        mItemList.clear();

        mItems.orderBy("storedCount", Query.Direction.DESCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                FoodItem item = documentSnapshot.toObject(FoodItem.class);
                item.setId(documentSnapshot.getId());
                if (item.getStoredCount() > 0) {
                    mItemList.add(item);
                }
            }
            if (mItemList.size() == 0) {
                Toast.makeText(this, "Még nem adtál hozzá egy ételt sem az étrendedhez!", Toast.LENGTH_LONG);
                queryData();
            }
            updateInfo();
            mIntakeItemAdapter.notifyDataSetChanged();
        });
    }

    public void updateInfo() {
        int sumCalories = 0;
        int sumPrice = 0;
        float sumRating = 0;

        for (FoodItem item : mItemList) {
            sumCalories += Integer.parseInt(item.getCalories()) * item.getStoredCount();
            sumPrice += Integer.parseInt(item.getPrice()) * item.getStoredCount();
            sumRating += item.getRatedInfo();
        }

        float avgRating = sumRating / mItemList.size();

        TextView caloriesSumTextView = findViewById(R.id.calories_sum_textview);
        TextView pricesSumTextView = findViewById(R.id.prices_sum_textview);
        TextView ratingAvgTextView = findViewById(R.id.health_rating_avg_textview);

        caloriesSumTextView.setText("Összkalória: " + sumCalories + " kcal");
        pricesSumTextView.setText("Pénzösszeg: " + sumPrice + " Ft");
        ratingAvgTextView.setText("Átlagos tápérték: " + String.format("%.2f", avgRating));
    }


    public void deleteStoredFood(FoodItem item) {
        DocumentReference reference = mItems.document(item._getId());

        reference.delete().addOnSuccessListener(success -> {
            Toast.makeText(this, item.getName() + " törölve lett.", Toast.LENGTH_LONG).show();
        }).addOnFailureListener(failure -> {
            Toast.makeText(this, item.getName() + " nem törölhető!", Toast.LENGTH_LONG).show();
        });

        queryData();
    }
}