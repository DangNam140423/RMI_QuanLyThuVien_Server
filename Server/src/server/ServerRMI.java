/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import Controller.*;
import Database.JDBCUtil;
import model.*;


import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

public class ServerRMI {
    private int serverPort = 1099;
    private Registry registry;
    private Connection connection;
    private String rmiService_Books = "rmi_service_Book";
    private String rmiService_Categories = "rmi_service_Category";
    private String rmiService_Readers = "rmi_service_Reader";
    private String rmiService_Borrows = "rmi_service_Borrow";
    private String rmiService_Returns = "rmi_service_Return";
    private String rmiService_Users = "rmi_service_User";

    public ServerRMI(){
        connection = JDBCUtil.getConnection_1();

        if (connection != null) {
            System.out.println("Connection success!");
        }
            try {
//            String res = "rmi://localhost/rmi_service_Book";
//            Naming.rebind(res, new BookController(connection));
                // Đăng ký rmiregistry
                // chạy dịch vụ đăng ký rmiregistry trên cổng 1099
                registry = LocateRegistry.createRegistry(serverPort);
                // 1 rmiregistry có thể quản lý nhiều đối tượng
                registry.bind(rmiService_Books, new BookController(connection));
                registry.bind(rmiService_Readers, new ReaderController(connection));
                registry.bind(rmiService_Categories, new CategoryController(connection));
                registry.bind(rmiService_Borrows, new BorrowController(connection));
                registry.bind(rmiService_Returns, new ReturnController(connection));
                registry.bind(rmiService_Users, new UserController(connection));
                System.out.println("Server Started!");



//            JDBCUtil.closeConnection(connection);
            } catch (Exception e) {
                System.err.println("Server initialization error, error details: " + e);
            }

        }
}
