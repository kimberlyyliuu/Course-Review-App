package edu.virginia.sde.reviews;

import java.util.Objects;

public class User {

    private String username;
    private String password;

    public User(String password, String username){
        this.password = password;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return super.equals(obj);
    }


}
