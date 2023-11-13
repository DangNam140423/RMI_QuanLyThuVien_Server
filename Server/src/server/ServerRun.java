/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import Controller.ReturnController;
import model.Users;

/**
 *
 * @author TrungNguyen
 */
public class ServerRun {
//    private int number_user = 0;

//    public int number_user_login(Users user){
//        if(user.getUsername() != null){
//            number_user ++;
//            System.out.println("Number user: "+number_user);
//        }
//        return number_user;
//    }

    public static void main(String[] args) {
        try {
            new ServerRMI();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
