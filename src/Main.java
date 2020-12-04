import lib_password_management.PasswordManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
        String[] strings_accounts = null;
        ArrayList<PasswordManager.UserData> accounts = new ArrayList<>();
        String choice = "";
        
        try {
            strings_accounts = readFile(ACCOUNT_FILE).split(USERS_SEPARATOR);
            
            for(String str : strings_accounts) {
                String[] values = str.split(USER_DATA_SEPARATOR);
                
                if(values != null && values.length == 3) {
                    accounts.add(new PasswordManager.UserData(values[0], values[1], values[2], true, USER_DATA_SEPARATOR, USERS_SEPARATOR));
                } else {
                    // ignore wrong line
                }
            }
            
            choice = askChoice();
    
            while(choice.equals(REGISTER_ACCOUNT) || choice.equals(LOGIN)) {
                switch(choice) {
                    case REGISTER_ACCOUNT -> {
                        if(register(accounts)) {
                            System.out.println("Registration complete! Inserted in file " + ACCOUNT_FILE);
                        }
                    }
                    case LOGIN -> {
                        if(login(accounts)) {
                            System.out.println("YOU LOGGED IN SUCCESFULLY!");
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
    
    private static boolean register(ArrayList<PasswordManager.UserData> accounts) throws Exception {
        boolean success = false;
        
        if(accounts != null) {
            PasswordManager.UserData user = getUserData(accounts);
            accounts.add(user);
            
            saveOnFile(user, ACCOUNT_FILE);
            success = true;
        } else {
            throw new Error("Error: accounts is null pointer!");
        }
        
        return success;
    }
    
    private static boolean login(ArrayList<PasswordManager.UserData> accounts) {
        if(accounts != null) {
            //TODO
        } else {
            throw new Error("Error: accounts is null pointer!");
        }
        
        return false; //TODO
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
    
    private static String readFile(String filename) {
        String file_content = "";
        Scanner inputStream = null;
        
        try {
            inputStream = new Scanner(new File(filename));
            
            while(inputStream.hasNextLine()) {
                file_content += inputStream.nextLine();
            }
        } catch(Exception e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        } finally {
            if(inputStream != null) {
                inputStream.close();
            }
        }
        
        return file_content;
    }
    
    private static PasswordManager.UserData getUserData(ArrayList<PasswordManager.UserData> accounts) throws Exception {
        String ID, password;
        
        ID = getID(accounts);
        password = getPassword();
        return new PasswordManager.UserData(ID, password, false, USER_DATA_SEPARATOR, USERS_SEPARATOR);
    }
    
    private static String getID(ArrayList<PasswordManager.UserData> accounts) throws Exception {
        String ID = "";
        
        try {
            System.out.print("Insert ID (write 'EXIT' to exit): ");
            ID = t.nextLine();
            
            while(!((ID.equalsIgnoreCase("EXIT")) || (validID(ID, accounts)))) {
                System.out.print("this ID already exists, try another (write 'EXIT' to exit): ");
                ID = t.nextLine();
            }
    
            if(ID.equalsIgnoreCase("EXIT")) {
                throw new Exception("Exception: " + USER_STOP_EVENT_MESSAGE);
            }
        } catch(Exception e) {
            throw e;
        }
        
        return ID;
    }
    
    private static String getPassword() {
        String password = "";
    
        try {
            System.out.print("Insert password: ");
            password = t.nextLine();
        } catch(Exception e) {
        
        }
        
        return password;
    }
    
    private static boolean validID(String ID, ArrayList<PasswordManager.UserData> accounts) {
        if(accounts.size() == 0) {
            return true;
        } else {
            System.out.println("risultato del confronto: " + (accounts.indexOf(new PasswordManager.UserData(ID, "password", false, "", "")) != -1));
            return accounts.indexOf(new PasswordManager.UserData(ID, "password", false, "", "")) == -1;
        }
    }
    
    private static void saveOnFile(PasswordManager.UserData user, String filename) {
        PrintWriter outputStream = null;
        
        try {
            outputStream = new PrintWriter(filename);
            
            outputStream.println(user.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new Error(e.getMessage());
        } finally {
            if(outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        }
    }
}
