package com.example.firebase;

public class User {

    String name;
    int mob;

    public User(String name, int mob) {
        this.name = name;
        this.mob = mob;
    }
    public User()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMob() {
        return mob;
    }

    public void setMob(int mob) {
        this.mob = mob;
    }
}

