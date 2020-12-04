package lib_password_management;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class PasswordManager {
    private static final String ALGORYTHM = "SHA-256";
    private static final int SALT_DIM = 100;
    private final String user_data_file;
    private final String user_data_separator;
    private final String users_separator;
    private ArrayList<UserData> userList;
    
    public PasswordManager(String user_data_file, String user_data_separator, String users_separator) {
        this.user_data_file = user_data_file;
        this.user_data_separator = user_data_separator;
        this.users_separator = users_separator;
        
        init();
    }
    
    private void init() {
        String[] strings_accounts;
        userList = new ArrayList<>();
    
        strings_accounts = readFile().split(users_separator);
    
        for(String str : strings_accounts) {
            String[] values = str.split(user_data_separator);
        
            if(values.length == 3) {
                userList.add(new UserData(values[0], values[1], values[2], true, user_data_separator, users_separator, user_data_file));
            } else {
                // ignore wrong line
            }
        }
    }
    
    /**
     * This function is synchronized on the PasswordManager object.
     * Checks if the ID is already taken, if not the given credentials are
     * used to make a user account and save it's data on the PasswordManager user account's file
     * @param ID primary key of the account (email, for example)
     * @param NOT_hashed_password clear password, not hashed or not salted yet
     * @return true id the account is created and saved in the file, false if the ID is already taken
     */
    public boolean registerUser(String ID, String NOT_hashed_password) {
        boolean created = false;
        UserData user;
        
        synchronized(this) { //TODO #01 implement LETTORI-SCRITTORI synchronizaton?
            if(!ID_taken(ID)) {
                user = new UserData(ID, NOT_hashed_password, false,
                        user_data_separator, users_separator, user_data_file);
        
                user.saveOnFile();
                userList.add(user);
                created = true;
            }
        }
        
        return created;
    }
    
    /**
     * Verifies if the given credentials match with an existing account saved in the PassordManager's associated file
     * @param ID
     * @param password
     * @return true if the user credentials were right, false if login failed
     */
    public boolean loginUser(String ID, String password) {
        boolean valid_login = false;
        int index;
        
        synchronized(this) { //TODO #01 implement LETTORI-SCRITTORI synchronizaton?
            if(userList != null) {
                index = userList.indexOf(new UserData(ID, "template_password",
                        true, "", "", ""));
    
                if(index != -1) {
                    String hashed_password = (new PasswordManager.UserData(ID, userList.get(index).getSalt(), password, false, "", "", "")).getHashedPassword();
                    valid_login = hashed_password.equals(userList.get(index).getHashedPassword());
                }
            } else {
                throw new Error("Error: accounts is null pointer!");
            }
        }
        
        return valid_login;
    }
    
    private static String hash(String password, String salt, String users_separator, String user_data_separator) {
        MessageDigest md;
        byte[] hashed_password;
        StringBuilder sb;
        
        try {
            // Select the message digest for the hash computation -> SHA-256
            md = MessageDigest.getInstance(ALGORYTHM);
            
            // Passing the salt to the digest for the computation
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            
            // Generate the salted hash
            hashed_password = md.digest(password.getBytes(StandardCharsets.UTF_8));
            
            sb = new StringBuilder();
            for(byte b : hashed_password) {
                sb.append(String.format("%02x", b));
            }
        } catch(NoSuchAlgorithmException e) {
            throw new Error("Error: no such algorythm --> " + ALGORYTHM);
        }
        
        String res = sb.toString();
        res.replaceAll(users_separator, "");
        res.replaceAll(user_data_separator, "");
        
        return res;
    }
    
    private static String getRandomSalt(String users_separator, String user_data_separator) {
        Random r = new Random();
        byte[] bytes = new byte[SALT_DIM];
        String res = "";
        
        for(int i = 0; i < SALT_DIM; i++) {
            bytes[i] = (byte) (r.nextInt((int) ('}' - '0')) + '0');
            
            if(((int) bytes[i]) == '\\') {
                bytes[i] = (byte) (((int) bytes[i] )+ 1);
            }
        }
    
        res = new String(bytes);
        res = res.replaceAll(users_separator, "");
        res = res.replaceAll(user_data_separator, "");
        return res;
    }
    
    private boolean ID_taken(String ID) {
        if(userList.size() == 0) {
            return false;
        } else {
            return userList.indexOf(new UserData(ID, "template_password",
                    true, "", "", "")) != -1;
        }
    }
    
    private String readFile() {
        String file_content = "";
        Scanner inputStream = null;
        
        try {
            inputStream = new Scanner(new File(user_data_file));
            
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
    
    private class UserData implements Comparable {
        private String ID;
        private String salt;
        private String hashed_password;
        private String user_data_separator;
        private String users_separator;
        private String user_data_file;
        
        public UserData(String ID, String password, boolean password_already_hashed,
                        String user_data_separator, String users_separator, String user_data_file) {
            this.ID = ID;
            this.user_data_separator = user_data_separator;
            this.users_separator = users_separator;
            this.user_data_file = user_data_file;
            
            if(password_already_hashed) {
                hashed_password = password;
            } else {
                setPassword(password, true);
            }
        }
    
        public UserData(String ID, String salt, String password, boolean password_already_hashed,
                        String user_data_separator, String users_separator, String user_data_file) {
            this.ID = ID;
            this.salt = salt;
            this.user_data_separator = user_data_separator;
            this.users_separator = users_separator;
            this.user_data_file = user_data_file;
            
            if(password_already_hashed) {
                hashed_password = password;
            } else {
                setPassword(password, false);
            }
        }
        
        public String getID() { return ID; }
        public String getSalt() { return salt; }
        public String getHashedPassword() { return hashed_password; }
    
        /**
         * hashes the given password and saves the hashed_password in the object attributes,
         * if generateRandomSalt is true, the salt gets generated randomly, otherwise the
         * actual value of salt is used
         * @param NOT_hashed_password
         * @param generateRandomSalt
         */
        public void setPassword(String NOT_hashed_password, boolean generateRandomSalt) {
            if(generateRandomSalt) {
                salt = getRandomSalt(users_separator, user_data_separator);
            }
        
            hashed_password = hash(NOT_hashed_password, salt, users_separator, user_data_separator);
        }
    
        /**
         * Saves this UserData object appending it at the associated file
         */
        private void saveOnFile() {
            Path path = Paths.get(user_data_file);
        
            try {
                Files.write(path, (toString() + "\n").getBytes(), StandardOpenOption.APPEND);  //Append mode
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error(e.getMessage());
            }
        }
        
        @Override
        public boolean equals(Object obj) {
            boolean equals = false;
            
            if(obj != null) {
                if(obj.getClass().getName().equals("lib_password_management.PasswordManager$UserData")) {
                    equals = ID.equals(((PasswordManager.UserData) obj).getID());
                }
            }
            
            return equals;
        }
    
        @Override
        public int compareTo(Object other) {
            int comparison = -1;
            
            if(other != null) {
                if(other.getClass().getName().equals("lib_password_management.PasswordManager$UserData")) {
                    comparison = ID.compareTo(((PasswordManager.UserData) other).getID());
                }
            }
            
            return comparison;
        }
    
        @Override
        public String toString() {
            return ID + user_data_separator +
                    salt + user_data_separator +
                    hashed_password + users_separator;
        }
    }
}
