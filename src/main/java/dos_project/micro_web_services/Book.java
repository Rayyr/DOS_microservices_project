package dos_project.micro_web_services;

import static spark.Spark.*;

import com.google.gson.Gson;
import java.sql.*;
import java.util.*;

public class Book {

    static List<Map<String, Object>> allBooks;
    

	public static void routing( ) {
		 
		//GET action 
		get("/getBooks/all", (req, res) -> {

			allBooks=new ArrayList<>();
			try ( 
				     Statement statement = Api.dbCon.createStatement();
				     ResultSet rs = statement.executeQuery("SELECT * FROM Book")) {

				ResultSetMetaData meta = rs.getMetaData();
				  while (rs.next()) {
	                  Map<String, Object> book = new LinkedHashMap<>(); //we use linkedhashmap tp preserve the insertion order 
	                  book.put(meta.getColumnName(1), rs.getInt("book_id"));
	                  book.put(meta.getColumnName(2), rs.getString("title"));
	                  book.put(meta.getColumnName(3), rs.getString("description"));
	                  book.put(meta.getColumnName(4), rs.getDouble("cost"));
	                  book.put(meta.getColumnName(5), rs.getInt("quantity"));
	                  book.put(meta.getColumnName(6), rs.getString("topic"));
	                  allBooks.add(book);
	              }

				} catch (SQLException e) {
					System.err.print("Sorry , there is an error data base connection : "+e.getMessage());
				}
			
			res.type("application/json");    // response type
			return new Gson().toJson(allBooks); // convert to JSON
		});
	 
	}

	
	 
}