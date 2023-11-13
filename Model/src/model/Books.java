package model;

import java.io.*;

public class Books implements Serializable {
    private int id;
    private String name;
    private String author;
    private String category;
    private String pub_company;
    private String pub_year;
    private int quantity;
    private int quantity_remaining;

    public Books(){
        super();
    }

    public Books(int id, String name, String author, String category, String pub_company, String pub_year, int quantity, int quantity_remaining) {
        super();
        this.id = id;
        this.name = name;
        this.author = author;
        this.category = category;
        this.pub_company = pub_company;
        this.pub_year = pub_year;
        this.quantity = quantity;
        this.quantity_remaining = quantity_remaining;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPub_company() {
        return pub_company;
    }

    public void setPub_company(String pub_company) {
        this.pub_company = pub_company;
    }

    public String getPub_year() {
        return pub_year;
    }

    public void setPub_year(String pub_year) {
        this.pub_year = pub_year;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity_remaining() {
        return quantity_remaining;
    }

    public void setQuantity_remaining(int quantity_remaining) {
        this.quantity_remaining = quantity_remaining;
    }

    @Override
    public String toString() {
//        return "Books{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", author='" + author + '\'' +
//                ", category='" + category + '\'' +
//                ", pub_company='" + pub_company + '\'' +
//                ", pub_year='" + pub_year + '\'' +
//                ", quantity=" + quantity +
//                ", quantity_remaining=" + quantity_remaining +
//                '}';
        return name + " - " + author;
    }
    public String toGetName_Author() {
        return name + " - " + author;
    }
}
