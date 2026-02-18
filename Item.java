package com.ryanjuniarto.daftarbelanjaku;

public class Item {
    private int id;
    private String name;
    private String quantity;
    private String category;
    private String date;
    private boolean isChecked;
    private int priority; // 1=Low, 2=Medium, 3=High

    public Item() {}

    public Item(int id, String name, String quantity, String category, String date, boolean isChecked, int priority) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.category = category;
        this.date = date;
        this.isChecked = isChecked;
        this.priority = priority;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public boolean getIsChecked() { return isChecked; }
    public void setIsChecked(boolean isChecked) { this.isChecked = isChecked; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    // Get priority color
    public int getPriorityColor() {
        switch (priority) {
            case 3: return R.color.priority_high;    // Red
            case 2: return R.color.priority_medium;  // Orange
            default: return R.color.priority_low;    // Green
        }
    }

    // Get category icon
    public int getCategoryIcon() {
        switch (category) {
            case "Food": return R.drawable.ic_food;
            case "Drink": return R.drawable.ic_drink;
            case "Household": return R.drawable.ic_household;
            case "Personal": return R.drawable.ic_personal;
            default: return R.drawable.ic_other;
        }
    }
}