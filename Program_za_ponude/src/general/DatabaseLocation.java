/*
 * Helper class to get main database location path from local file
 * 
 * @author Petar
 */
package general;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


public class DatabaseLocation {
    
    //  Gets and returns database location
    public String getDatabaseLocation(){
        
        String path = System.getProperty("user.dir") + "\\documents\\databaseLocation.ser";
        try(FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);){
            return (String) ois.readObject();
        } catch (ClassNotFoundException | IOException e){
            System.err.println(e.toString() );
        }
        return null;
    }
}
