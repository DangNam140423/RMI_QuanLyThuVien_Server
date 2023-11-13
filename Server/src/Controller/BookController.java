package Controller;

import model.Books;
import model.DaoInterface;
import synchronization.insertLog;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class BookController extends UnicastRemoteObject implements DaoInterface<Books> {
    private Connection connection;
    private final Lock lock = new ReentrantLock();
    public BookController(Connection connection) throws RemoteException {
        this.connection = connection;
    }


    @Override
    public void client_access(String ip_client) throws RemoteException {
//        while(true){
//            try {
//                String clientAddress = RemoteServer.getClientHost();
//                System.out.println("Có kết nối từ: "+clientAddress);
//            } catch (ServerNotActiveException e) {
//                e.printStackTrace();
//            }
//        }
//        new Thread(() -> {
//            int check = update(books);
//        }).start();
//        return 0;

    }

    @Override
    public int insert(Books books)  throws RemoteException{
        lock.lock();
        int check = 0;
        try {
            // Tạo ra đối tượng PreparedStatement
            String sql = "INSERT INTO books(name, author, category, pub_company, pub_year, quantity, quantity_remaining)"
                    + " VALUE(?, ?, ?, ?, ?, ?, ? )";
            PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pst.setString(1, books.getName());
            pst.setString(2, books.getAuthor());
            pst.setString(3, books.getCategory());
            pst.setString(4, books.getPub_company());
            pst.setString(5, books.getPub_year());
            pst.setInt(6, books.getQuantity());
            pst.setInt(7, books.getQuantity());

            // Thực thi 1 câu lệnh SQL
            check = pst.executeUpdate();
            // Xử lý kết quả
            if(check > 0){
                System.out.println("Insert book Success");
                String data = books.getId() + "_" + books.getName() + "_" + books.getAuthor() + "_" + books.getCategory()
                        + "_" + books.getPub_year() + "_" + books.getPub_company()
                        + "_" + books.getQuantity() + "_" + books.getQuantity();
                new insertLog(connection, "INSERT", "books", data, true);
            }else{
                System.out.println("Insert book Failed");
            }

            // Ngắt kết nối để nhường slot cho các object ( đối tượng ) khác
            // JDBCUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return check;
    }

    @Override
    public int update(Books books)  throws RemoteException{
        lock.lock();
        int check = 0;
        try {
//            Connection connection = JDBCUtil.getConnection();
            String sql = "UPDATE books" +
                    " SET " +
                    " name= ?," +
                    " author= ?," +
                    " category= ?," +
                    " pub_company= ?," +
                    " pub_year= ?," +
                    " quantity= ?," +
                    " quantity_remaining= ?" +
                    " WHERE id= ? ";

            PreparedStatement pst = connection.prepareStatement(sql);

            pst.setString(1, books.getName());
            pst.setString(2, books.getAuthor());
            pst.setString(3, books.getCategory());
            pst.setString(4, books.getPub_company());
            pst.setString(5, books.getPub_year());
            pst.setInt(6, books.getQuantity());
            Books book = selectById(books);
            int quantity_book_old = book.getQuantity();
            int quantity_book_new = books.getQuantity();
            int quantity_book_insert = quantity_book_new - quantity_book_old;
            pst.setInt(7, books.getQuantity_remaining() + quantity_book_insert);
            pst.setInt(8, books.getId());

//            Thread.sleep(6000);
//            System.out.println("Đã qua 6 giây");
            check = pst.executeUpdate();
            if(check > 0){
                System.out.println("Update book Success");
                String data = books.getId() + "_" + books.getName() + "_" + books.getAuthor() + "_" + books.getCategory()
                        + "_" + books.getPub_year() + "_" + books.getPub_company()
                        + "_" + books.getQuantity() + "_" + books.getQuantity_remaining();
                new insertLog(connection, "UPDATE", "books", data, true);
            }else{
                System.out.println("Update book Failed");
            }


//            JDBCUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
//            try {
//                System.out.println("End " + connection.getCatalog());
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
            lock.unlock();
        }
        return check;
    }

    @Override
    public int delete(Books books)  throws RemoteException{
        lock.lock();
        int check = 0;
        try {
//            Connection connection = JDBCUtil.getConnection();

            String sql = "DELETE FROM books" +
                    " WHERE id= ?";

            PreparedStatement pst = connection.prepareStatement(sql);

            pst.setInt(1, books.getId());

            // check = số lượng dòng thay đổi trong database
            Thread.sleep(5000);
            check = pst.executeUpdate();
            if(check > 0){
                System.out.println("Delete book Success");
                String data = books.getId() + "_" + books.getName() + "_" + books.getAuthor() + "_" + books.getCategory()
                        + "_" + books.getPub_year() + "_" + books.getPub_company()
                        + "_" + books.getQuantity() + "_" + books.getQuantity_remaining();
                new insertLog(connection, "DELETE", "books", data, true);
            }else{
                System.out.println("Delete book Failed");
            }

//            JDBCUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return check;
    }
//import java.time.Instant;
//import java.time.Duration;
//
//    Instant start = Instant.now();
//    // Code bạn muốn đo thời gian thực hiện
//    Instant end = Instant.now();
//
//    Duration duration = Duration.between(start, end);
//    long millis = duration.toMillis();
    @Override
    public ArrayList<Books> selectAll()  throws RemoteException{
        ArrayList<Books> check = new ArrayList<Books>();
        try {
//            Connection connection = JDBCUtil.getConnection();

            String sql = "SELECT * FROM books "+
                         "ORDER BY name ASC ";

            PreparedStatement pst = connection.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String author = rs.getString("author");
                String category = rs.getString("category");
                String pub_year = rs.getString("pub_year");
                String pub_company = rs.getString("pub_company");
                int quantity = rs.getInt("quantity");
                int quantity_remaining = rs.getInt("quantity_remaining");

                Books book = new Books(id, name, author, category, pub_company, pub_year, quantity, quantity_remaining);
                check.add(book);
            }
//            JDBCUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public ArrayList<Books> searchByName(String nameInput) throws RemoteException {
        ArrayList<Books> check = new ArrayList<Books>();
        try {
            String sql = "SELECT * FROM books" +
                    " WHERE name LIKE ? " +
                    "ORDER BY name ASC ";

            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, '%' + nameInput + '%');

            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String author = rs.getString("author");
                String category = rs.getString("category");
                String pub_year = rs.getString("pub_year");
                String pub_company = rs.getString("pub_company");
                int quantity = rs.getInt("quantity");
                int quantity_remaining = rs.getInt("quantity_remaining");

                Books book = new Books(id, name, author, category, pub_company, pub_year, quantity, quantity_remaining);
                check.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public Books selectById(Books books)  throws RemoteException{
        Books check = null;
        try {
//            Connection connection = JDBCUtil.getConnection();

            String sql = "SELECT * FROM books" +
                    " WHERE id=   ?  ";

            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, books.getId());

            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String author = rs.getString("author");
                String category = rs.getString("category");
                String pub_year = rs.getString("pub_year");
                String pub_company = rs.getString("pub_company");
                int quantity = rs.getInt("quantity");
                int quantity_remaining = rs.getInt("quantity_remaining");

                check = new Books(id, name, author, category, pub_company, pub_year, quantity, quantity_remaining);

            }
//            JDBCUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public ArrayList<Books> selectByCondition(String condition)  throws RemoteException{
        ArrayList<Books> check = new ArrayList<Books>();
        try {
//            Connection connection = JDBCUtil.getConnection();

            String sql = "SELECT * FROM books where " + condition + " ORDER BY name ASC ";

            PreparedStatement pst = connection.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String author = rs.getString("author");
                String category = rs.getString("category");
                String pub_year = rs.getString("pub_year");
                String pub_company = rs.getString("pub_company");
                int quantity = rs.getInt("quantity");
                int quantity_remaining = rs.getInt("quantity_remaining");

                Books book = new Books(id, name, author, category, pub_company, pub_year, quantity, quantity_remaining);
                check.add(book);
            }
//            JDBCUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }


    @Override
    public int insert_list(ArrayList<Books> books, int id_suport) throws RemoteException {
        lock.lock();
        int check = 0;
        try{
            for(Books book : books){
                String sql = "INSERT INTO list_book_borrow(id_borrow, id_book)"
                        + " VALUE(?, ?)";
                PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pst.setInt(1, id_suport);
                pst.setInt(2, book.getId());
                //Tiếp tục thực thi 1 câu lệnh SQL
                check += pst.executeUpdate();

                String data = "0" + "_" + id_suport + "_" + book.getId();
                new insertLog(connection, "INSERT_LIST", "list_book_borrow", data, true);
            }

            // Xử lý kết quả
            if(check == books.size()){
                System.out.println("Insert borrow Success");
            }else{
                System.out.println("Insert borrow Failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return check;
    }

    @Override
    public int delete_list(ArrayList<Books> books, int id_borrow) throws RemoteException {
        lock.lock();
        int check = 0;
        try{
            for(Books book : books){
                String sql = "DELETE FROM list_book_borrow" +
                        " WHERE id_borrow= ?";

                PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                pst.setInt(1, id_borrow);
                //Tiếp tục thực thi 1 câu lệnh SQL
                check += pst.executeUpdate();

                String data = "0" + "_" + id_borrow + "_" + book.getId();
                new insertLog(connection, "DELETE_LIST", "list_book_borrow", data, true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return check;
    }

    @Override
    public int update_list(ArrayList<Books> books, int id_borrow) throws RemoteException {
//        int check = 0;
//        try{
//            for(Books book : books){
//                // update quantity remaining book after return
//                Books book_old = selectById(book);
//                book_old.setQuantity_remaining(book_old.getQuantity_remaining()+1);
//                update(book_old);
//
//                check++;
//            }
//            new DataSynchronization();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return 0;
    }

    @Override
    public ArrayList<Books> selectListById(int id_suport) throws RemoteException {
        ArrayList<Books> check = new ArrayList<Books>();
        try {
                String sql = "SELECT * FROM list_book_borrow" +
                        " WHERE id_borrow=   ?  ";

                PreparedStatement pst = connection.prepareStatement(sql);

                pst.setInt(1, id_suport);

                ResultSet rs = pst.executeQuery();

                while(rs.next()){
                    int id_book = rs.getInt("id_book");
                    Books books = new Books();
                    books.setId(id_book);

                    Books new_book = selectById(books);
                    check.add(new_book);
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }
}