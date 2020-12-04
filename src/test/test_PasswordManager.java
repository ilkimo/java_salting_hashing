package test;

import lib_password_management.PasswordManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class test_PasswordManager {
    private static final String TEST_FILE_ACCOUNTS = "test_accounts.txt";
    private static PasswordManager passwordManager;
    private static final String USERS_SEPARATOR = ";";
    private static final String USER_DATA_SEPARATOR = " ";
    
    @Before
    public void create_file_or_just_clean_it() throws IOException {
        File myObj = new File(TEST_FILE_ACCOUNTS);
        
        if(!myObj.createNewFile()) { //file already exists
            Path path = Paths.get(TEST_FILE_ACCOUNTS);
            Files.newBufferedWriter(path , StandardOpenOption.TRUNCATE_EXISTING);
        }
        
        passwordManager = new PasswordManager(TEST_FILE_ACCOUNTS, " ", ";");
    }
    
    @Test
    public void test_getRandomSalt() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method getSalt;
        String str;
    
        try {
            // creating a reflection of the private method, to be able to test it
            getSalt = PasswordManager.class.getDeclaredMethod("getRandomSalt", String.class, String.class);
            //setting the visibility to "public"
            getSalt.setAccessible(true);
        
            // invoke wraps the returned value in it's wrapper class (if it is a primitive type) int --> Integer
            // first param is ignored if the function to invoke is static.
            for(int i = 0; i < 20000; i++) {
                str = (String) getSalt.invoke(passwordManager, USERS_SEPARATOR, USER_DATA_SEPARATOR);
    
                //check that no USERS_SEPARATOR or IGNORE2 remains in the Strings
                assertEquals(-1, str.indexOf(USERS_SEPARATOR));
                assertEquals(-1, str.indexOf(USER_DATA_SEPARATOR));
            }
        
        } catch(Exception e) {
            throw e;
        }
    }
    
    @Test
    public void test_register_and_login() {
        String[] mails = new String[] {
                "mario.rossi@gmail.com", "luigi.verdi@gmail.com", "gianfranco.lu@gmail.com",
                "pino.ponzi@gmail.com", "jerp.lorp@gmail.com", "manuela.genchi@gmail.com",
                "sorin.abdari@gmail.com", "abdullah.west@gmail.com", "dom.tomato@gmail.com",
                "jason.paul@gmail.com", "daniel.gerp@gmail.com", "walterri.luomahoo@gmail.com",
                "ginco.rasi@gmail.com", "arianna.masi@gmail.com", "lucian.domine@gmail.com"
        };
        
        String[] passwords = new String[] {
                "password", "otherPassword", "super Diff1cult password",
                "acif83sl sa}[[", "vehsiudrhtwie7", "qwerty",
                "LARndsosOHo948{[;", "sdoijtwo488878HU f", "yeppAJSDI82 dw",
                "a", "pass", "pas"
        };
        
        int[] associated_passwords = new int[mails.length];
        
        Random r = new Random();
        
        for(int i = 0; i < mails.length; i++) {
            associated_passwords[i] = r.nextInt(passwords.length);
        }
        
        //REGISTER
        
        for(int i = 0; i < mails.length; i++) {
            assertEquals(true, passwordManager.registerUser(mails[i], passwords[associated_passwords[i]]));
        }
        
        for(int i = 0; i < mails.length; i++) {
            assertEquals(false, passwordManager.registerUser(mails[i], passwords[associated_passwords[i]]));
        }
        
        //LOGIN
        
        for(int i = 0; i < mails.length; i++) {
            int valid_logins = 0;
            
            for(int j = 0; j < passwords.length; j++) {
                if(passwordManager.loginUser(mails[i], passwords[j])) {
                    assertEquals(associated_passwords[i], j);
                    valid_logins++;
                }
            }
            
            assertEquals(1, valid_logins);
        }
    }
}
