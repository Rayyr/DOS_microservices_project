package dos_project.micro_web_services;
import static spark.Spark.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Hello world!
 */
  
public class App
    {
      public static void main(String[] args)
      {
          get("/hell", (req, res) -> "Hello hi Spark Web Framework!");

         
        try
        (
          // create a database connection
          Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\hp\\Desktop\\online_book_store.db");
          Statement statement = connection.createStatement();
        )
        {
          statement.setQueryTimeout(30);  // set timeout to 30 sec.

           
          ResultSet rs = statement.executeQuery("select * from catalog");
          while(rs.next())
          {
            // read the result set
            System.out.println("book_id = " + rs.getInt("book_id"));
            System.out.println("title = " + rs.getString("title"));
            System.out.println("description = " + rs.getString("description"));
            System.out.println("topic = " + rs.getString("topic"));
            System.out.println("cost = " + rs.getInt("cost"));
            System.out.println("stock quantity = " + rs.getInt("quantity"));


          }
        }
        catch(SQLException e)
        {
          // if the error message is "out of memory",
          // it probably means no database file is found
          e.printStackTrace(System.err);
        }
      }
    }