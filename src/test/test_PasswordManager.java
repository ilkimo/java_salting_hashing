package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class test_PasswordManager {
    private static final String TEST_FILE_ACCOUNTS = "test_accounts.txt";
    
    @Before
    public void create_file_or_just_clean_it() throws IOException {
        File myObj = new File(TEST_FILE_ACCOUNTS);
        
        if(!myObj.createNewFile()) { //file already exists
            Path path = Paths.get(TEST_FILE_ACCOUNTS);
            Files.newBufferedWriter(path , StandardOpenOption.TRUNCATE_EXISTING);
        }
    }
    
    @Test
    public void test_register_and_login() {
        
    }
}
