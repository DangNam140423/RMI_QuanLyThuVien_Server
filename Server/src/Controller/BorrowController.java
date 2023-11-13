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

public class BorrowController extends UnicastRemoteObject implements DaoInterface<Borrows> {
    private Connection connection;
    public BorrowController(Connection connection) throws RemoteException {
        this.connection = connection;
    }


    @Override
    public void client_access(String ip_client) throws RemoteException {

    }

    @Override
    public int insert(Borrows borrows) throws RemoteException {
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
        }
        return id_borrow_vuathem;
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
    public ArrayList<Borrows> selectByCondition(String condition) throws RemoteException {
        return null;
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

    @Override
    public ArrayList<Borrows> selectListById(int id_suport) throws RemoteException {
        return null;
    }
}
