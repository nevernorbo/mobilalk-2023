package com.example.foodoo;

public class FoodItem {
    private String name;
    private String calories;
    private String price;
    private float ratedInfo;

    public FoodItem(String name, String calories, String price, float ratedInfo) {
        this.name = name;
        this.calories = calories;
        this.price = price;
        this.ratedInfo = ratedInfo;
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
}
