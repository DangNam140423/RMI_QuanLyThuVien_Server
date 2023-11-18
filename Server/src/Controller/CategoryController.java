package Controller;

import model.Books;
import model.Categories;
import model.DaoInterface;
import model.Readers;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class CategoryController extends UnicastRemoteObject implements DaoInterface<Categories> {
    private Connection connection;
    public CategoryController(Connection connection) throws RemoteException {
        this.connection = connection;
    }


    @Override
    public void client_access(String ip_client) throws RemoteException {

    }

    @Override
    public int insert_comp(ArrayList<Books> books, Categories categories) throws RemoteException {
        return 0;
    }

    @Override
    public int insert(Categories categories) throws RemoteException {
        return 0;
    }

    @Override
    public int update_comp(ArrayList<Books> books, Categories categories) throws RemoteException {
        return 0;
    }

    @Override
    public int update(Categories categories) throws RemoteException {
        return 0;
    }

    @Override
    public int delete_comp(ArrayList<Books> books, Categories categories) throws RemoteException {
        return 0;
    }

    @Override
    public int delete(Categories categories) throws RemoteException {
        return 0;
    }

    @Override
    public ArrayList<Categories> selectAll() throws RemoteException {
        ArrayList<Categories> check = new ArrayList<Categories>();
        try {
            String sql = "SELECT * FROM category";

            PreparedStatement pst = connection.prepareStatement(sql);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String code = rs.getString("code");

                Categories category = new Categories(id, name, code);
                check.add(category);
            }
//            JDBCUtil.closeConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }

    @Override
    public ArrayList<Categories> searchByName(String nameInput) throws RemoteException {
        return null;
    }

    @Override
    public Categories selectById(Categories categories) throws RemoteException {
        return null;
    }

    @Override
    public int insert_list(ArrayList<Categories> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public int delete_list(ArrayList<Categories> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public int update_list(ArrayList<Categories> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public ArrayList<Categories> selectListById(int id_suport) throws RemoteException {
        return null;
    }
}
