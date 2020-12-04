package lib_password_management;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordManager {
    private static final String ALGORYTHM = "SHA-256";
    private static final int SALT_DIM = 16;
    
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
    
    public static String getRandomSalt(String users_separator, String user_data_separator) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_DIM];
        random.nextBytes(salt);
        
        String res = new String(salt);
        res.replaceAll(users_separator, "");
        res.replaceAll(user_data_separator, "");
        
        return res;
    }
    
    public static class UserData implements Comparable {
        private String ID;
        private String salt;
        private String hashed_password;
        private String user_data_separator;
        private String users_separator;
        
        public UserData(String ID, String password, boolean password_already_hashed, String user_data_separator, String users_separator) {
            this.ID = ID;
            this.user_data_separator = user_data_separator;
            this.users_separator = users_separator;
            
            if(password_already_hashed) {
                hashed_password = password;
            } else {
                setPassword(password, true);
            }
        }
    
        public UserData(String ID, String salt, String password, boolean password_already_hashed, String user_data_separator, String users_separator) {
            this.ID = ID;
            this.salt = salt;
            this.user_data_separator = user_data_separator;
            this.users_separator = users_separator;
            
            if(password_already_hashed) {
                hashed_password = password;
            } else {
                setPassword(password, false);
            }
        }
    
        /**
         * hashes the given password and saves the hashed_password in the object attributes,
         * if generateRandomSalt is true, the salt gets generated randomly, otherwise the
         * actual value of salt is used
         * @param password
         * @param generateRandomSalt
         */
        public void setPassword(String password, boolean generateRandomSalt) {
            if(generateRandomSalt) {
                salt = getRandomSalt(users_separator, user_data_separator);
            }
            
            hashed_password = hash(password, salt, users_separator, user_data_separator);
        }
        
        public String getID() { return ID; }
        public String getSalt() { return salt; }
        public String getHashedPassword() { return hashed_password; }
    
        @Override
        public boolean equals(Object obj) {
            boolean equals = false;
            
            if(obj != null) {
                if(obj.getClass().getName().equals("lib_password_management.PasswordManager$UserData")) {
                    System.out.println("confronto: " + ID + " con " + ((PasswordManager.UserData) obj).getID() + " --> " + ID.equals(((PasswordManager.UserData) obj).getID()));
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
