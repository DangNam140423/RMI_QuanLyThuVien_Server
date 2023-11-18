package Controller;

import model.Books;
import model.Borrows;
import model.DaoInterface;
import model.Returns;
import synchronization.insertLog;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BorrowController extends UnicastRemoteObject implements DaoInterface<Borrows> {
    private Connection connection;
    private final Lock lock = new ReentrantLock();
    public BorrowController(Connection connection) throws RemoteException {
        this.connection = connection;
    }


    @Override
    public void client_access(String ip_client) throws RemoteException {

    }

    @Override
    public int insert_comp(ArrayList<Books> books, Borrows borrows) throws RemoteException {
        lock.lock();
        int check1 = 0;
        int check2 = 0;
        int id_borrow_vuathem = 0;
        BookController bookController = new BookController(connection);
        try{
            Thread.sleep(5000);
            // insert borrow
            id_borrow_vuathem = insert(borrows);
            if(id_borrow_vuathem != 0){
                // insert list book after borrow
                check1 = bookController.insert_list(books, id_borrow_vuathem);
                if(check1 == books.size()){
                    for(Books book : books){
                        book = bookController.selectById(book);
                        book.setQuantity_remaining(book.getQuantity_remaining() - 1);
                        // update quantity remaining book after borrow
                        bookController.update(book);
                        check2++;
                    }
                }
            }

            // Xử lý kết quả
            if(check2 == books.size()){
                System.out.println("Insert borrow Success");
            }else{
                System.out.println("Insert borrow Failed");
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return check2;
    }

    @Override
    public int insert(Borrows borrows) throws RemoteException {
        lock.lock();
        int id_borrow_vuathem = 0;
        try {
            // Tạo ra đối tượng PreparedStatement
            String sql = "INSERT INTO borrows(id_reader, borrow_date, due_date, status_return)"
                    + " VALUE(?, ?, ?, ?)";
            PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pst.setInt(1, borrows.getId_reader());
            pst.setDate(2, borrows.getBorrow_date());
            pst.setDate(3, borrows.getDue_date());
            pst.setBoolean(4, false);

            // Thực thi 1 câu lệnh SQL
            int check = pst.executeUpdate();
            if(check > 0){
                String data = borrows.getId() + "_" + borrows.getId_reader() + "_" + borrows.getBorrow_date()
                        + "_" + borrows.getDue_date() + "_" + borrows.getStatus_return();
                new insertLog(connection, "INSERT", "borrows", data, true);

                try (ResultSet resultSet = pst.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        id_borrow_vuathem = resultSet.getInt(1);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return id_borrow_vuathem;
    }

    @Override
    public int update_comp(ArrayList<Books> books, Borrows borrows) throws RemoteException {
        return 0;
    }

    @Override
    public int update(Borrows borrows) throws RemoteException {
        int check = 0;
        try {
            String sql = "UPDATE borrows" +
                    " SET " +
                    " status_return= ?" +
                    " WHERE id= ? ";

            PreparedStatement pst = connection.prepareStatement(sql);

            pst.setBoolean(1, borrows.getStatus_return());
            pst.setInt(2, borrows.getId());

            check = pst.executeUpdate();
            if(check > 0){
                System.out.println("Update borrow Success");
                String data = borrows.getId() + "_" + borrows.getId_reader() + "_" + borrows.getBorrow_date()
                        + "_" + borrows.getDue_date() + "_" + borrows.getStatus_return();
                new insertLog(connection, "UPDATE", "borrows", data, true);
            }else{
                System.out.println("Update borrow Failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public int delete_comp(ArrayList<Books> books, Borrows borrows) throws RemoteException {
        lock.lock();
        int check_1 = 0;
        int check_2 = 0;
        int check_3 = 0;
        int check_4 = 0;
        BookController bookController = new BookController(connection);
        ReturnController returnController = new ReturnController(connection);
        try{
            // delete list books
            check_1 = bookController.delete_list(books, borrows.getId());
            // nếu mã mượn đó chưa trả sách ( status_return == flase, có nghĩa là thủ thư thêm nhầm )
            // thì xóa phiếu mượn đã thêm nhầm và cập nhật lại số lượng sách trong phiếu mượn đó
            // nếu mã mượn đó đã được trả sách ( status_return == true, có nghĩa là phiếu mượn này đã được trả )
            // thì xóa phiếu mượn đó đồng thời xóa lun phiếu trả có id_borrow = mã phiếu mượn này
            if(borrows.getStatus_return() == false){
                for(Books book : books){
                    book = bookController.selectById(book);
                    book.setQuantity_remaining(book.getQuantity_remaining() + 1);
                    // update quantity remaining book
                    bookController.update(book);
                    check_2++;
                }
            }else if(borrows.getStatus_return() == true){
                ArrayList<Returns> list_returns = returnController.searchByName(String.valueOf(borrows.getId()));
                if(list_returns != null){
                    for(Returns returns : list_returns){
                        // delete return have id_borrow = id ( Borrows )
                        check_3 = returnController.delete(returns);
                    }
                }
            }

            if( (check_1 == books.size() && check_2 == books.size()) ||
                (check_1 == books.size() && check_3 == 1) )
            {
                // delete list borrow
                check_4 = delete(borrows);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }

        return check_4;
    }

    @Override
    public int delete(Borrows borrows) throws RemoteException {
        int check = 0;
        try {
            String sql = "DELETE FROM borrows" +
                    " WHERE id= ?";

            PreparedStatement pst = connection.prepareStatement(sql);

            pst.setInt(1, borrows.getId());

            // check = số lượng dòng thay đổi trong database
            check = pst.executeUpdate();
            if(check > 0){
                System.out.println("Delete borrow Success");
                String data = borrows.getId() + "_" + borrows.getId_reader() + "_" + borrows.getBorrow_date()
                        + "_" + borrows.getDue_date() + "_" + borrows.getStatus_return();
                new insertLog(connection, "DELETE", "borrows", data, true);
                // Xóa phiếu trả trong bảng returns nếu mã borrow này tồn tại trong bảng returns
//                ArrayList<Returns> list_returns = new ReturnController(connection).searchByName(String.valueOf(borrows.getId()));
//                if(list_returns != null){
//                    for(Returns returns : list_returns){
//                        new ReturnController(connection).delete(returns);
//                    }
//                }

            }else{
                System.out.println("Delete borrow Failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public ArrayList<Borrows> selectAll() throws RemoteException {
        ArrayList<Borrows> check = new ArrayList<Borrows>();
        try {
//            Connection connection = JDBCUtil.getConnection();

            String sql = "SELECT * FROM borrows " +
                         "ORDER BY id DESC ";

            PreparedStatement pst = connection.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                int id_reader = rs.getInt("id_reader");
                Date borrow_date = rs.getDate("borrow_date");
                Date due_date = rs.getDate("due_date");
                boolean status_return = rs.getBoolean("status_return");
                List<Books> listbook = null;

                Borrows borrow = new Borrows(id, id_reader, listbook, borrow_date, due_date, status_return);
                check.add(borrow);
            }
//            JDBCUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public ArrayList<Borrows> searchByName(String id_reader_search) throws RemoteException {
        ArrayList<Borrows> check = new ArrayList<Borrows>();
        try {
            String sql = "SELECT * FROM borrows" +
                    " WHERE id_reader = ? " +
                    " ORDER BY id DESC ";

            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(id_reader_search));

            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                int id_reader = rs.getInt("id_reader");
                Date borrow_date = rs.getDate("borrow_date");
                Date due_date = rs.getDate("due_date");
                boolean status_return = rs.getBoolean("status_return");
                List<Books> listbook = null;

                Borrows borrow = new Borrows(id, id_reader, listbook, borrow_date, due_date, status_return);
                check.add(borrow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public ArrayList<Borrows> selectListById(int id_suport) throws RemoteException {
        return null;
    }

    @Override
    public Borrows selectById(Borrows borrows) throws RemoteException {
        Borrows check = null;
        try {

            String sql = "SELECT * FROM borrows" +
                    " WHERE id=   ?  ";

            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, borrows.getId());

            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                int id_reader = rs.getInt("id_reader");
                Date borrow_date = rs.getDate("borrow_date");
                Date due_date = rs.getDate("due_date");
                boolean status_return = rs.getBoolean("status_return");
                List<Books> listbook = null;

                check = new Borrows(id, id_reader, listbook, borrow_date, due_date, status_return);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public int insert_list(ArrayList<Borrows> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public int delete_list(ArrayList<Borrows> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public int update_list(ArrayList<Borrows> t, int id_suport) throws RemoteException {
        return 0;
    }

}
