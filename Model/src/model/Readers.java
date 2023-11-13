package model;

import java.io.*;
import java.sql.Date;

public class Readers implements Serializable {

    private int id;
    private String name;
    private String address;
    private int gender;
    private Date birthday;
    private int phonenumber;

    public Readers() {
        super();
    }

    public Readers(int id, String name, String address,int gender, Date birthday, int phonenumber) {
        super();
        this.id = id;
        this.name = name;
        this.address = address;
        this.gender = gender;
        this.birthday = birthday;
        this.phonenumber = phonenumber;
    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(int phonenumber) {
        this.phonenumber = phonenumber;
    }

    @Override
    public String toString() {
        return "Readers{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", gender=" + gender +
                ", birthday=" + birthday +
                ", phonenumber=" + phonenumber +
                '}';
    }
}
