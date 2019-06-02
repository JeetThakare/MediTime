package com.meditime.meditime;

public class User {
    private String email;
    private String name;
    private String role;
    private String gender;
    private String age;

    public User(String email, String name, String role, String gender, String age) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.gender = gender;
        this.age=age;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getRole() {
        return role;
    }

    public String getGender() {
        return gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
