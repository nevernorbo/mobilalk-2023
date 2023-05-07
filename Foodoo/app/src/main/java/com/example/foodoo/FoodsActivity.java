package com.example.foodoo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class FoodsActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getName();

    // Firebase
    private FirebaseUser user;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    // Notification handler
    private NotificationHandler mNotificationHandler;
    private JobScheduler mJobScheduler;

    private RecyclerView mRecyclerView;
    private ArrayList<FoodItem> mItemList;
    private FoodItemAdapter mFoodItemAdapter;

    private int gridNumber = 1;
    private boolean viewRow = true;


    private FrameLayout redCircle;
    private TextView countTextView;
    private int dailyIntakeItems = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemList = new ArrayList<>();

        mFoodItemAdapter = new FoodItemAdapter(this, mItemList);
        mRecyclerView.setAdapter(mFoodItemAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        mNotificationHandler = new NotificationHandler(this);

        mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        setJobScheduler();
        queryData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // cleanup
        if (mJobScheduler != null) {
            mJobScheduler.cancelAll();
        }
    }

    private void queryData() {
        mItemList.clear();

        mItems.orderBy("storedCount", Query.Direction.DESCENDING).limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
           for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
               FoodItem item = documentSnapshot.toObject(FoodItem.class);
               item.setId(documentSnapshot.getId());
               mItemList.add(item);
           }
           if (mItemList.size() == 0) {
               initializeData();
               queryData();
           }

           mFoodItemAdapter.notifyDataSetChanged();
        });
    }

    private void initializeData() {
        String[] foodItemNames = getResources().getStringArray(R.array.food_item_names);
        String[] foodItemCalories = getResources().getStringArray(R.array.food_item_calories);
        String[] foodItemPrices = getResources().getStringArray(R.array.food_item_price);
        TypedArray foodItemRates = getResources().obtainTypedArray(R.array.food_item_rates);

        for (int i = 0; i < foodItemNames.length; i++) {
            mItems.add(new FoodItem(foodItemNames[i], foodItemCalories[i], foodItemPrices[i], foodItemRates.getFloat(i, 0), 0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "OnCreateOptionsMenu is called");
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.food_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mFoodItemAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Log.d(LOG_TAG, "Kurva anyad");
        if (itemId == R.id.logoutButton) {
            Log.d(LOG_TAG, "Log out clicked");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        } else if (itemId == R.id.setting_button) {
            Log.d(LOG_TAG, "Settings clicked");
            return true;
        } else if (itemId == R.id.daily_intake) {
            Log.d(LOG_TAG, "Daily intake clicked");
            return true;
        } else if (itemId == R.id.view_selector) {
            Log.d(LOG_TAG, "View selector clicked");
            if (viewRow) {
                changeSpanCount(item, R.drawable.baseline_view_stream_24, 1);
            } else {
                changeSpanCount(item, R.drawable.baseline_grid_view_24, 2);
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "Menu: " + menu);
        final MenuItem alertMenuItem = menu.findItem(R.id.daily_intake);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        Log.d(LOG_TAG, "redCircle: " + redCircle);
        Log.d(LOG_TAG, "countTextView: " + countTextView);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(FoodItem item) {
        dailyIntakeItems++;
        if (dailyIntakeItems > 0) {
            countTextView.setText(String.valueOf(dailyIntakeItems));
        } else {
            countTextView.setText("");
        }
        redCircle.setVisibility((dailyIntakeItems > 0) ? VISIBLE : GONE);

        mItems.document(item._getId()).update("storedCount", item.getStoredCount() + 1).addOnFailureListener(failure -> {
                Toast.makeText(this, item.getName() + " cannot be changed.", Toast.LENGTH_LONG).show();
        });

        StringBuilder messageBuilder = new StringBuilder();
        for (FoodItem foodItem: mItemList) {
            if (foodItem.getStoredCount() > 0) {
                messageBuilder.append(foodItem.getName()).append(" x ").append(foodItem.getStoredCount()).append(", ");
            }
        }

        if (messageBuilder.length() > 0) {
            messageBuilder.setLength(messageBuilder.length() - 2);
        }

        mNotificationHandler.send("Jelenlegi ételeid", messageBuilder.toString());
        queryData();
    }

    public void deleteFood(FoodItem item) {
        DocumentReference reference = mItems.document(item._getId());

        reference.delete().addOnSuccessListener(success -> {
            Toast.makeText(this, item.getName() + " deleted.", Toast.LENGTH_LONG).show();

            dailyIntakeItems -= item.getStoredCount();
            if (dailyIntakeItems > 0) {
                countTextView.setText(String.valueOf(dailyIntakeItems));
            } else {
                countTextView.setText("");
            }
            redCircle.setVisibility((dailyIntakeItems > 0) ? VISIBLE : GONE);
        }).addOnFailureListener(failure -> {
            Toast.makeText(this, item.getName() + " cannot be deleted!", Toast.LENGTH_LONG).show();
        });

        queryData();
        mNotificationHandler.cancel();
    }

    private void setJobScheduler() {
        int networkType = JobInfo.NETWORK_TYPE_UNMETERED;
        int hardDeadLine = 5000;

        ComponentName name = new ComponentName(getPackageName(), NotificationJobService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(0, name)
                .setRequiredNetworkType(networkType)
                .setOverrideDeadline(hardDeadLine);

        mJobScheduler.schedule(builder.build());
    }
}