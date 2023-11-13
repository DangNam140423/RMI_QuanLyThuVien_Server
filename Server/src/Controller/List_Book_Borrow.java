package Controller;

import model.Books;
import model.DaoInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;

public class List_Book_Borrow extends UnicastRemoteObject implements DaoInterface<Books> {
    private Connection connection;
    public List_Book_Borrow(Connection connection) throws RemoteException {
        this.connection = connection;
    }


    @Override
    public int insert_list(ArrayList<Books> books, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public void client_access(String ip_client) throws RemoteException {

    }

    @Override
    public int insert(Books books) throws RemoteException {
        return 0;
    }

    @Override
    public int update(Books books) throws RemoteException {
        return 0;
    }

    @Override
    public int delete(Books books) throws RemoteException {
        return 0;
    }

    @Override
    public ArrayList<Books> selectAll() throws RemoteException {
        return null;
    }

    @Override
    public ArrayList<Books> searchByName(String nameInput) throws RemoteException {
        return null;
    }

    @Override
    public Books selectById(Books books) throws RemoteException {
        return null;
    }

    @Override
    public ArrayList<Books> selectByCondition(String condition) throws RemoteException {
        return null;
    }


    @Override
    public int delete_list(ArrayList<Books> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public int update_list(ArrayList<Books> t, int id_suport) throws RemoteException {
        return 0;
    }

    @Override
    public ArrayList<Books> selectListById(int id_suport) throws RemoteException {
        return null;
    }
}
