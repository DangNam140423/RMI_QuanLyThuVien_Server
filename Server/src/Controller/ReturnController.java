package Controller;

import model.Books;
import model.Borrows;
import model.DaoInterface;
import model.Returns;
import synchronization.DataSynchronization;
import synchronization.insertLog;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReturnController extends UnicastRemoteObject implements DaoInterface<Returns> {
    private Connection connection;
    private final Lock lock = new ReentrantLock();

    public ReturnController(Connection connection) throws RemoteException {
        this.connection = connection;
    }

    @Override
    public void client_access(String ip_client) throws RemoteException {

    }

    @Override
    public int insert(Returns returns) throws RemoteException {
        int check = 0;
        try {
            Borrows borrows = new Borrows();
            borrows.setId(returns.getId_borrow());
            boolean status_return = new BorrowController(connection).selectById(borrows).getStatus_return();
            if(status_return == true){
                System.out.println("This borrowed code has been completed");
                check = 2;
            }else{
                String sql = "INSERT INTO returns(id_borrow, return_date)"
                        + " VALUE(?, ?)";
                PreparedStatement pst = connection.prepareStatement(sql);

                pst.setInt(1, returns.getId_borrow());
                pst.setDate(2, returns.getReturn_date());


                // Thực thi 1 câu lệnh SQL
                check = pst.executeUpdate();

                if(check == 1){
                    String data = returns.getId() + "_" + returns.getId_borrow() + "_" + returns.getReturn_date();
                    new insertLog(connection, "INSERT", "returns", data, true);
                    System.out.println("Insert return Success");

                    // cập nhật lại status_return trong bảng Borrows
//                    borrows = new BorrowController(connection).selectById(borrows);
//                    borrows.setStatus_return(true);
//                    new BorrowController(connection).update(borrows);

                    // Cập nhật lại số lượng sách trong bảng Books
//                    ArrayList<Books> list_book = new BookController(connection).selectListById(returns.getId_borrow());
//                    new BookController(connection).update_list(list_book, returns.getId_borrow()); // thực chất tham số thứ 2 là id_borrow không được sử dụng
                }else{
                    System.out.println("Insert return Failed");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public int update(Returns returns) throws RemoteException {
        return 0;
    }

    @Override
    public int delete(Returns returns) throws RemoteException {
        lock.lock();
        int check = 0;
        try {
            String sql = "DELETE FROM returns" +
                    " WHERE id= ?";

            PreparedStatement pst = connection.prepareStatement(sql);

            pst.setInt(1, returns.getId());

            // check = số lượng dòng thay đổi trong database
            check = pst.executeUpdate();
            if(check > 0){
                System.out.println("Delete return Success");
                String data = returns.getId() + "_" + returns.getId_borrow() + "_" + returns.getReturn_date();
                new insertLog(connection, "DELETE", "returns", data, true);
            }else{
                System.out.println("Delete return Failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
        return check;
    }

    @Override
    public ArrayList<Returns> selectAll() throws RemoteException {
        ArrayList<Returns> check = new ArrayList<Returns>();
        try {

            String sql = "SELECT * FROM returns " +
                         "ORDER BY id DESC ";

            PreparedStatement pst = connection.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                int id_borrow = rs.getInt("id_borrow");
                Date return_date = rs.getDate("return_date");
                List<Books> listbook = null;

                Returns returns = new Returns(id, id_borrow, listbook,return_date);
                check.add(returns);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public ArrayList<Returns> searchByName(String id_borrow_search) throws RemoteException {
        ArrayList<Returns> check = new ArrayList<Returns>();
        try {
            String sql = "SELECT * FROM returns " +
                    "WHERE id_borrow = ? " +
                    "ORDER BY id DESC ";

            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(id_borrow_search));

            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                int id_borrow = rs.getInt("id_borrow");
                Date return_date = rs.getDate("return_date");
                List<Books> listbook = null;

                Returns returns = new Returns(id, id_borrow, listbook,return_date);
                check.add(returns);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public Returns selectById(Returns returns) throws RemoteException {
        return null;
    }

    @Override
    public ArrayList<Returns> selectByCondition(String condition) throws RemoteException {
        return null;
    }

    @Override
    public int insert_list(ArrayList<Returns> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public int delete_list(ArrayList<Returns> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public int update_list(ArrayList<Returns> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public ArrayList<Returns> selectListById(int id_suport) throws RemoteException {
        return null;
    }
}
