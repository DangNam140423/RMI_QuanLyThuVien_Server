package synchronization;

import Controller.*;
import Database.JDBCUtil;
import model.*;

import java.sql.*;
import java.util.ArrayList;

public class DataSynchronization {
    private static Connection connection1 ;
    private static Connection connection2 ;

    public DataSynchronization(){
        // Kết nối đến cả hai cơ sở dữ liệu.
        this.connection1 = JDBCUtil.getConnection_1();
        this.connection2 = JDBCUtil.getConnection_2();

        synchronizeData();
    }

    public static void synchronizeData() {
        try {
            // Truy vấn và lấy dữ liệu từ bảng data_log trong cả hai cơ sở dữ liệu.
            ResultSet resultSet1 = getDataFromLogTable(connection1);
            ResultSet resultSet2 = getDataFromLogTable(connection2);

            // So sánh và đồng bộ hóa dữ liệu.
            synchronizeDataTables(resultSet1, resultSet2);

            // Đóng kết nối.
//            connection1.close();
//            connection2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static ResultSet getDataFromLogTable(Connection connection) throws SQLException {
        String query = "SELECT * FROM data_log";
        PreparedStatement statement = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        return statement.executeQuery();
    }

    private static void synchronizeDataTables(ResultSet resultSet1, ResultSet resultSet2){
        try {
            while (resultSet1.next()) {
                // Lấy thông tin resultSet1 từ bảng data_log của database1.
                int id1 = resultSet1.getInt("id");
                String operation1 = resultSet1.getString("operation");
                String table1 = resultSet1.getString("table");
                String data1 = resultSet1.getString("data");

                boolean dataExists = false;
                while(resultSet2.next()){
                    int id2 = resultSet2.getInt("id");

                    // nếu thông tin resultSet1 đã tồn tại trong database2 thì thoát vòng lặp
                    if(id2 == id1){
                        dataExists = true;
                        break;
                    }
                }

                // dataExists == false khi duyệt hết trong resultSet2 thông qua while
                // nhưng thông tin của resultSet1 ko tồn tại trong data_log của database2
                if(dataExists == false){
                    // cập nhật lại database 2 sau khi phân tích thông tin từ resultSet1 ...
                    // cập nhật ở đây có thể là INSERT, UPDATE, DELETE tùy theo operation1 trong resultSet1
                    try{
                        switch (table1) {
                            case "books":
                                BookController bookController = new BookController(connection2);
                                String[] tokens = data1.split("_");
                                int id_book = Integer.parseInt(tokens[0]);
                                String name_book = tokens[1];
                                String author = tokens[2];
                                String category = tokens[3];
                                String pub_year = tokens[4];
                                String pub_company = tokens[5];
                                int quantity = Integer.parseInt(tokens[6]);
                                int quantity_remaining = Integer.parseInt(tokens[7]);
                                Books book = new Books(id_book, name_book, author,category, pub_company, pub_year, quantity, quantity_remaining);
                                databaseSynchronization(bookController, operation1, book);
                                break;
                            case "borrows":
                                BorrowController borrowController = new BorrowController(connection2);
                                String[] tokens_2 = data1.split("_");
                                int id_borrow = Integer.parseInt(tokens_2[0]);
                                int id_reader2 = Integer.parseInt(tokens_2[1]);
                                Date borrow_date = Date.valueOf(tokens_2[2]);
                                Date due_date = Date.valueOf(tokens_2[3]);
                                boolean status_return = Boolean.parseBoolean(tokens_2[4]);
                                Borrows borrow = new Borrows(id_borrow, id_reader2, null, borrow_date, due_date, status_return);
                                databaseSynchronization(borrowController, operation1, borrow);
                                break;
                            case "category":
                                break;
                            case "list_book_borrow":
                                BookController bookController2 = new BookController(connection2);
                                String[] tokens_4 = data1.split("_");
                                int id_listbook = Integer.parseInt(tokens_4[0]);
                                int id_borrow2 = Integer.parseInt(tokens_4[1]);
                                int id_book2 = Integer.parseInt(tokens_4[2]);
                                Books books = new Books();
                                books.setId(id_book2);
                                ArrayList<Books> list_book = new ArrayList<Books>();
                                list_book.add(books);
                                databaseSynchronization2(bookController2, operation1, list_book, id_borrow2);
                                break;
                            case "readers":
                                ReaderController readerController = new ReaderController(connection2);
                                String[] tokens_5 = data1.split("_");
                                int id_reader = Integer.parseInt(tokens_5[0]);
                                String name_reader = tokens_5[1];
                                String address = tokens_5[2];
                                int gender = Integer.parseInt(tokens_5[3]);
                                Date birthday = Date.valueOf(tokens_5[4]);
                                int phone = Integer.parseInt(tokens_5[5]);
                                Readers reader = new Readers(id_reader, name_reader, address, gender, birthday, phone);
                                databaseSynchronization(readerController, operation1, reader);
                                break;
                            case "returns":
                                System.out.println("hello1");
                                ReturnController returnController = new ReturnController(connection2);
                                String[] tokens_6 = data1.split("_");
                                int id_return = Integer.parseInt(tokens_6[0]);
                                int id_borrow3 = Integer.parseInt(tokens_6[1]);
                                Date return_date = Date.valueOf(tokens_6[2]);
                                Returns returns = new Returns(id_return, id_borrow3, null, return_date);
                                databaseSynchronization(returnController, operation1, returns);
                                break;
                            case "users":
                                UserController userController = new UserController(connection2);
                                String[] tokens_7 = data1.split("_");
                                int id_user = Integer.parseInt(tokens_7[0]);
                                String user_name = tokens_7[1];
                                String password = tokens_7[2];
                                String address_user = tokens_7[3];
                                String phone_number = tokens_7[4];
                                Users user = new Users(id_user, user_name, password, address_user, phone_number);
                                databaseSynchronization(userController, operation1, user);
                                break;
                            default:
                                break;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    // insert thông tin resultSet1 đó vào data_log trong database2 ..
                    // logSynchronization(operation1, table1, data1);
                }

                // Reset resultSet2 để so sánh với dòng tiếp theo trong resultSet1.
                resultSet2.beforeFirst();
            }

            // Reset resultSet1..
            resultSet1.beforeFirst();
            // NGƯỢC LẠI TA CŨNG CẦN SO SÁNH THÔNG TIN TỪ resultSet2 VỚI database1
            while (resultSet2.next()) {
                // Lấy thông tin resultSet2 từ bảng data_log của database2.
                int id2 = resultSet2.getInt("id");
                String operation2 = resultSet2.getString("operation");
                String table2 = resultSet2.getString("table");
                String data2 = resultSet2.getString("data");

                boolean dataExists2 = false;
                while(resultSet1.next()){
                    int id1 = resultSet1.getInt("id");

                    // nếu thông tin resultSet2 đã tồn tại trong database1 thì thoát vòng lặp
                    if(id1 == id2){
                        dataExists2 = true;
                        break;
                    }
                }

                // dataExists == false khi duyệt hết trong resultSet1 thông qua while
                // nhưng thông tin của resultSet2 ko tồn tại trong data_log của database1
                if(dataExists2 == false){
                    // cập nhật lại database1 sau khi phân tích thông tin từ resultSet2 ...
                    // cập nhật ở đây có thể là INSERT, UPDATE, DELETE tùy theo operation2 trong resultSet2
                    try{
                        switch (table2) {
                            case "books":
                                BookController bookController = new BookController(connection1);
                                String[] tokens = data2.split("_");
                                int id = Integer.parseInt(tokens[0]);
                                String name = tokens[1];
                                String author = tokens[2];
                                String category = tokens[3];
                                String pub_year = tokens[4];
                                String pub_company = tokens[5];
                                int quantity = Integer.parseInt(tokens[6]);
                                int quantity_remaining = Integer.parseInt(tokens[7]);

                                Books book = new Books(id, name, author,category, pub_company, pub_year, quantity, quantity_remaining);
                                databaseSynchronization(bookController, operation2, book);
                                break;
                            case "borrows":
                                BorrowController borrowController = new BorrowController(connection1);
                                String[] tokens_2 = data2.split("_");
                                int id_borrow = Integer.parseInt(tokens_2[0]);
                                int id_reader2 = Integer.parseInt(tokens_2[1]);
                                Date borrow_date = Date.valueOf(tokens_2[2]);
                                Date due_date = Date.valueOf(tokens_2[3]);
                                boolean status_return = Boolean.parseBoolean(tokens_2[4]);
                                Borrows borrow = new Borrows(id_borrow, id_reader2, null, borrow_date, due_date, status_return);
                                databaseSynchronization(borrowController, operation2, borrow);
                                break;
                            case "category":
                                break;
                            case "list_book_borrow":
                                BookController bookController2 = new BookController(connection1);
                                String[] tokens_4 = data2.split("_");
                                int id_listbook = Integer.parseInt(tokens_4[0]);
                                int id_borrow2 = Integer.parseInt(tokens_4[1]);
                                int id_book2 = Integer.parseInt(tokens_4[2]);
                                Books books = new Books();
                                books.setId(id_book2);
                                ArrayList<Books> list_book = new ArrayList<Books>();
                                list_book.add(books);
                                databaseSynchronization2(bookController2, operation2, list_book, id_borrow2);
                                break;
                            case "readers":
                                ReaderController readerController = new ReaderController(connection1);
                                String[] tokens_5 = data2.split("_");
                                int id_reader = Integer.parseInt(tokens_5[0]);
                                String name_reader = tokens_5[1];
                                String address = tokens_5[2];
                                int gender = Integer.parseInt(tokens_5[3]);
                                Date birthday = Date.valueOf(tokens_5[4]);
                                int phone = Integer.parseInt(tokens_5[5]);
                                Readers reader = new Readers(id_reader, name_reader, address, gender, birthday, phone);
                                databaseSynchronization(readerController, operation2, reader);
                                break;
                            case "returns":
                                System.out.println("hello");
                                ReturnController returnController = new ReturnController(connection1);
                                String[] tokens_6 = data2.split("_");
                                int id_return = Integer.parseInt(tokens_6[0]);
                                int id_borrow3 = Integer.parseInt(tokens_6[1]);
                                Date return_date = Date.valueOf(tokens_6[2]);
                                Returns returns = new Returns(id_return, id_borrow3, null, return_date);
                                databaseSynchronization(returnController, operation2, returns);
                                break;
                            case "users":
                                UserController userController = new UserController(connection1);
                                String[] tokens_7 = data2.split("_");
                                int id_user = Integer.parseInt(tokens_7[0]);
                                String user_name = tokens_7[1];
                                String password = tokens_7[2];
                                String address_user = tokens_7[3];
                                String phone_number = tokens_7[4];
                                Users user = new Users(id_user, user_name, password, address_user, phone_number);
                                databaseSynchronization(userController, operation2, user);
                                break;
                            default:
                                break;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    // insert thông tin resultSet2 đó vào data_log trong database1 ..
                    // logSynchronization(operation2, table2, data2);
                }

                // Reset resultSet1 để so sánh với dòng tiếp theo trong resultSet2.
                resultSet1.beforeFirst();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void databaseSynchronization(DaoInterface daoInterface, String operation, Object object) throws SQLException {
        try{
            if ("INSERT".equals(operation)) {
                daoInterface.insert(object);
            } else if("UPDATE".equals(operation)){
                daoInterface.update(object);
            } else if("DELETE".equals(operation)){
                daoInterface.delete(object);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void databaseSynchronization2(DaoInterface daoInterface, String operation,ArrayList arrayList, int id_suport) throws SQLException {
        try{
            if ("INSERT_LIST".equals(operation)) {
                daoInterface.insert_list(arrayList, id_suport);
            } else if("UPDATE_LIST".equals(operation)){
                daoInterface.update_list(arrayList, id_suport);
            } else if("DELETE_LIST".equals(operation)){
                daoInterface.delete_list(arrayList, id_suport);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
