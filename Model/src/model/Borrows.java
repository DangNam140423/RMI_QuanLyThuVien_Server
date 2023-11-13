package model;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public class Borrows implements Serializable {
    private int id;
    private int id_reader;
    private List<Books> list_books;
    private Date borrow_date;
    private Date due_date;
    private boolean status_return;

    public Borrows() {
        super();
    }

    public Borrows(int id, int id_reader, List<Books> list_books, Date borrow_date, Date due_date, boolean status_return) {
        super();
        this.id = id;
        this.id_reader = id_reader;
        this.list_books = list_books;
        this.borrow_date = borrow_date;
        this.due_date = due_date;
        this.status_return = status_return;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_reader() {
        return id_reader;
    }

    public void setId_reader(int id_reader) {
        this.id_reader = id_reader;
    }

    public List<Books> getList_books() {
        return list_books;
    }

    public void setList_books(List<Books> list_books) {
        this.list_books = list_books;
    }

    public Date getBorrow_date() {
        return borrow_date;
    }

    public void setBorrow_date(Date borrow_date) {
        this.borrow_date = borrow_date;
    }

    public Date getDue_date() {
        return due_date;
    }

    public void setDue_date(Date due_date) {
        this.due_date = due_date;
    }

    public boolean getStatus_return() {
        return status_return;
    }

    public void setStatus_return(boolean status_return) {
        this.status_return = status_return;
    }

    @Override
    public String toString() {
        return "Borrows{" +
                "id=" + id +
                ", id_reader=" + id_reader +
                ", list_books=" + list_books +
                ", borrow_date=" + borrow_date +
                ", due_date=" + due_date +
                ", status_return=" + status_return +
                '}';
    }
}
