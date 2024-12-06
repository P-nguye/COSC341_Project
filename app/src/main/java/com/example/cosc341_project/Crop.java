package com.example.cosc341_project;

public class Crop {
    String name, id, type;
    int quantity;
    public Crop() {
        // Default constructor required for Firebase
    }
    public Crop(String i, String n, String t, int q){
        this.id=i;
        this.name=n;
        this.quantity =q;
        this.type=t;
    }

    public String getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setType(String type) {
        this.type = type;
    }
}
