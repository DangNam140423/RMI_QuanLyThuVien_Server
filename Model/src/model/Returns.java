package model;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public class Returns implements Serializable {
    private int id;
    private int id_borrow;
    private List<Books> list_books;
    private Date return_date;

    public Returns() {
        super();
    }
    public Returns(int id, int id_borrow, List<Books> list_books, Date return_date) {
        super();
        this.id = id;
        this.id_borrow = id_borrow;
        this.list_books = list_books;
        this.return_date = return_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_borrow() {
        return id_borrow;
    }

    public void setId_borrow(int id_borrow) {
        this.id_borrow = id_borrow;
    }

    public List<Books> getList_books() {
        return list_books;
    }

    public void setList_books(List<Books> list_books) {
        this.list_books = list_books;
    }

    public Date getReturn_date() {
        return return_date;
    }

    public void setReturn_date(Date return_date) {
        this.return_date = return_date;
    }

    @Override
    public String toString() {
        return "Returns{" +
                "id=" + id +
                ", id_borrow=" + id_borrow +
                ", list_books=" + list_books +
                ", return_date=" + return_date +
                '}';
    }
}
