/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface DaoInterface<T> extends Remote{
    public void client_access(String ip_client) throws RemoteException;
    public int insert_comp(ArrayList<Books> books, T t)  throws RemoteException;
    public int insert(T t)  throws RemoteException;
    public int update_comp(ArrayList<Books> books, T t)  throws RemoteException;
    public int update(T t)  throws RemoteException;
    public int delete_comp(ArrayList<Books> books, T t)  throws RemoteException;
    public int delete(T t)  throws RemoteException;
    public ArrayList<T> selectAll()  throws RemoteException;
    public ArrayList<T> searchByName(String nameInput)  throws RemoteException;
    public T selectById(T t)  throws RemoteException;
    public ArrayList<T> selectListById(int id_suport)  throws RemoteException;
    public int insert_list(ArrayList<T> t, int id_suport)  throws RemoteException;
    public int delete_list(ArrayList<T> t, int id_suport)  throws RemoteException;
    public int update_list(ArrayList<T> t, int id_suport)  throws RemoteException;

}
