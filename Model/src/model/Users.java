package model;

import java.io.Serializable;

public class Users implements Serializable {
    private int id;
    private String username;
    private String password;
    private String address;
    private String phonenumber;

    public Users() {
        super();
    }

    public Users(int id, String username, String password, String address, String phonenumber) {
        super();
        this.id = id;
        this.username = username;
        this.password = password;
        this.address = address;
        this.phonenumber = phonenumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
