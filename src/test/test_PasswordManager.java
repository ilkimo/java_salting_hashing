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
    public void test_register_and_login() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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
    public void test() {
        assertEquals(1, 1);
        System.out.println("jaa");
    }
}
