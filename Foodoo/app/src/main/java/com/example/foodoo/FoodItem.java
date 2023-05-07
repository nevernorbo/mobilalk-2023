package com.example.foodoo;

public class FoodItem {
    private String id;
    private String name;
    private String calories;
    private String price;
    private float ratedInfo;
    private int storedCount;

    public FoodItem() {
    }

    public FoodItem(String name, String calories, String price, float ratedInfo, int storedCount) {
        this.name = name;
        this.calories = calories;
        this.price = price;
        this.ratedInfo = ratedInfo;
        this.storedCount = storedCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public float getRatedInfo() {
        return ratedInfo;
    }

    public void setRatedInfo(float ratedInfo) {
        this.ratedInfo = ratedInfo;
    }

    public int getStoredCount() {
        return storedCount;
    }

    public void setStoredCount(int storedCount) {
        this.storedCount = storedCount;
    }

    public String _getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
