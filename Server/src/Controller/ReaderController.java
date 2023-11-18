package Controller;

import model.Books;
import model.Categories;
import model.Readers;
import model.DaoInterface;
import synchronization.insertLog;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ReaderController extends UnicastRemoteObject implements DaoInterface<Readers> {
    private Connection connection;
    public ReaderController(Connection connection) throws RemoteException {
        this.connection = connection;
    }

    @Override
    public void client_access(String ip_client) throws RemoteException {

    }

    @Override
    public int insert(Readers readers) throws RemoteException {
        int check = 0;
        try {
            // Tạo ra đối tượng PreparedStatement
            String sql = "INSERT INTO readers(name, address, gender, birthday, phonenumber)"
                    + " VALUE(?, ?, ?, ?, ?)";
            PreparedStatement pst = connection.prepareStatement(sql);

            pst.setString(1, readers.getName());
            pst.setString(2, readers.getAddress());
            pst.setInt(3, readers.getGender());
            pst.setDate(4, readers.getBirthday());
            pst.setInt(5, readers.getPhonenumber());

            // Thực thi 1 câu lệnh SQL
            check = pst.executeUpdate();

            // Xử lý kết quả
            if(check > 0){
                System.out.println("Insert reader Success");
                String data = readers.getId() + "_" + readers.getName() + "_" + readers.getAddress() + "_" + readers.getGender()
                        + "_" + readers.getBirthday() + "_" + readers.getPhonenumber();
                new insertLog(connection, "INSERT", "readers", data, true);
            }else{
                System.out.println("Insert reader Failed");
            }

            // Ngắt kết nối để nhường slot cho các object ( đối tượng ) khác
            // JDBCUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public int update(Readers readers) throws RemoteException {
        int check = 0;
        try {
//            Connection connection = JDBCUtil.getConnection();

            String sql = "UPDATE readers" +
                    " SET " +
                    " name= ?," +
                    " address= ?," +
                    " gender= ?," +
                    " birthday= ?," +
                    " phonenumber= ?" +
                    " WHERE id= ? ";

            PreparedStatement pst = connection.prepareStatement(sql);

            pst.setString(1, readers.getName());
            pst.setString(2, readers.getAddress());
            pst.setInt(3, readers.getGender());
            pst.setDate(4, readers.getBirthday());
            pst.setInt(5, readers.getPhonenumber());
            pst.setInt(6, readers.getId());

            check = pst.executeUpdate();
            if(check > 0){
                System.out.println("Update reader Success");
                String data = readers.getId() + "_" + readers.getName() + "_" + readers.getAddress() + "_" + readers.getGender()
                        + "_" + readers.getBirthday() + "_" + readers.getPhonenumber();
                new insertLog(connection, "UPDATE", "readers", data, true);
            }else{
                System.out.println("Update reader Failed");
            }

//            JDBCUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public int delete(Readers readers) throws RemoteException {
        int check = 0;
        try {
//            Connection connection = JDBCUtil.getConnection();

            String sql = "DELETE FROM readers" +
                    " WHERE id= ?";

            PreparedStatement pst = connection.prepareStatement(sql);

            pst.setInt(1, readers.getId());

            // check = số lượng dòng thay đổi trong database
            check = pst.executeUpdate();
            if(check > 0){
                System.out.println("Delete reader Success");
                String data = readers.getId() + "_" + readers.getName() + "_" + readers.getAddress() + "_" + readers.getGender()
                        + "_" + readers.getBirthday() + "_" + readers.getPhonenumber();
                new insertLog(connection, "DELETE", "readers", data, true);
            }else{
                System.out.println("Delete reader Failed");
            }

//            JDBCUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public ArrayList<Readers> selectAll() throws RemoteException {
        ArrayList<Readers> check = new ArrayList<Readers>();
        try {
            String sql = "SELECT * FROM readers";

            PreparedStatement pst = connection.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                int gender = rs.getInt("gender");
                Date birthday = rs.getDate("birthday");
                int phonenumber = rs.getInt("phonenumber");

                Readers reader = new Readers(id, name, address,gender, birthday, phonenumber);
                check.add(reader);
            }
//            JDBCUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public ArrayList<Readers> searchByName(String nameInput) throws RemoteException {
        ArrayList<Readers> check = new ArrayList<Readers>();
        try {
            String sql = "SELECT * FROM readers" +
                    " WHERE name LIKE ? ";

            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, '%' + nameInput + '%');

            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                int gender = rs.getInt("gender");
                Date birthday = rs.getDate("birthday");
                int phonenumber = rs.getInt("phonenumber");

                Readers readers = new Readers(id, name, address, gender, birthday, phonenumber);
                check.add(readers);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public ArrayList<Readers> selectListById(int id_suport) throws RemoteException {
        return null;
    }

    @Override
    public Readers selectById(Readers readers) throws RemoteException {
        Readers check = null;
        try {
//            Connection connection = JDBCUtil.getConnection();

            String sql = "SELECT * FROM readers" +
                    " WHERE id=   ?  ";

            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setInt(1, readers.getId());

            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String address = rs.getString("address");
                int gender = rs.getInt("gender");
                Date birthday = rs.getDate("birthday");
                int phonenumber = rs.getInt("phonenumber");

                check = new Readers(id, name, address,gender, birthday, phonenumber);
            }
//            JDBCUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public int insert_list(ArrayList<Readers> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public int delete_list(ArrayList<Readers> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public int update_list(ArrayList<Readers> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public int insert_comp(ArrayList<Books> books, Readers readers) throws RemoteException {
        return 0;
    }

    @Override
    public int update_comp(ArrayList<Books> books, Readers readers) throws RemoteException {
        return 0;
    }

    @Override
    public int delete_comp(ArrayList<Books> books, Readers readers) throws RemoteException {
        return 0;
    }

}
