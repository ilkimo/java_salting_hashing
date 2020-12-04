import lib_password_management.PasswordManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final String REGISTER_ACCOUNT = "1";
    private static final String LOGIN = "2";
    private static final String USERS_SEPARATOR = ";";
    private static final String USER_DATA_SEPARATOR = " ";
    private static final String USER_STOP_EVENT_MESSAGE = "user wants to exit";
    private static final String ACCOUNT_FILE = "passwords.txt";
    private static final String GOODBYE_MESSAGE = "bye bye motherfucker!";
    private static Scanner t = new Scanner(System.in);
    
    /**
     * just a toy for trying to create accounts and make log in-s and verify that the
     * data gets inserted in the file represented by String ACCOUNT_FILE = "passwords.txt"
     * @param args
     */
    public static void main(String[] args) {
        PasswordManager passManager = new PasswordManager(ACCOUNT_FILE, USER_DATA_SEPARATOR, USERS_SEPARATOR);
        String choice = "";
        
        try {
            choice = askChoice();
    
            while(choice.equals(REGISTER_ACCOUNT) || choice.equals(LOGIN)) {
                switch(choice) {
                    case REGISTER_ACCOUNT -> {
                        if(register(passManager)) {
                            System.out.println("Registration complete! Inserted in file " + ACCOUNT_FILE);
                        } else {
                            System.out.println("SORRY, ID ALREADY IN USE");
                        }
                    }
                    case LOGIN -> {
                        if(login(passManager)) {
                            System.out.println("YOU LOGGED IN SUCCESFULLY!");
                        } else {
                            System.out.println("Sorry, the password is wrong, ACCESS DENIED!");
                        }
                    }
                }
            
                choice = askChoice();
            }
            
            System.out.println(GOODBYE_MESSAGE);
        } catch(Exception e) {
            if(e.getMessage().contains(USER_STOP_EVENT_MESSAGE)) {
                System.out.println(GOODBYE_MESSAGE);
            } else {
                e.printStackTrace();
                throw new Error(e.getMessage());
            }
        }
    }
    
    private static boolean register(PasswordManager passwordManager) throws Exception {
        boolean success = false;
        String ID, password;
        
        if(passwordManager != null) {
            System.out.print("Insert new account ID: ");
            ID = t.nextLine();
            System.out.print("Insert new account password: ");
            password = t.nextLine();
            
            success = passwordManager.registerUser(ID, password);
        } else {
            throw new Error("Error: passwordManager is null pointer!");
        }
        
        return success;
    }
    
    private static boolean login(PasswordManager passwordManager) {
        boolean valid_login = false;
        String ID, password;
        
        if(passwordManager != null) {
            System.out.print("Insert ID: ");
            ID = t.nextLine();
            System.out.print("Insert password: ");
            password = t.nextLine();
            
            valid_login = passwordManager.loginUser(ID, password);
        } else {
            throw new Error("Error: passwordManager is null pointer!");
        }
        
        return valid_login;
    }
    
    private static String askChoice() {
        String choice= "";
        
        try {
            System.out.println("What do you want to do?");
            System.out.println("\t" + REGISTER_ACCOUNT + " --> register account");
            System.out.println("\t" + LOGIN + " --> log-in existing account");
            System.out.println("\tanything else --> exit");
    
            System.out.print("choose: ");
            
            choice = t.nextLine();
        } catch(Exception e) {
            throw e;
        }
    
        return choice;
    }
}
