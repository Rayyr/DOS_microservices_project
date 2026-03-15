package dos_project.micro_web_services;

import static spark.Spark.*;

import com.google.gson.Gson;
import java.sql.*;
import java.util.*;

public class BookTier {

	public static Connection dbCon;
	public static String error;
	static List<Map<String, Object>> books;

	
	public static void main(String[] args) {
	
		//database connection
		dbCon=connectWithDB();
		
		if(dbCon!=null) {
			
		//the port that the book_tier machine will listen from 
		port(4560);
		 
		
		get("/search/:topic",(req,res)->{
			 
			String topic = req.params(":topic");// extract topic from the URL
			books = getBooksBasedToTopic(topic);

			res.type("text/plain");
			if (error != null)
				return error + "\n" + res.status();

			if (books.size() == 0)
				return "There is no matching books with the specified topic : " + topic;

			res.type("application/json"); // response type
			return new Gson().toJson(books); // convert to JSON
		});

		}
		
		else System.err.print(error);
	return ;
	}
	
	
	
	
	private static List<Map<String, Object>> getBooksBasedToTopic(String topic) {

		List<Map<String, Object>> allBooks = new ArrayList<>();
		try (Statement statement = dbCon.createStatement();
				ResultSet rs = statement.executeQuery("SELECT book_id,title FROM Book where topic=\'" + topic + "\'")) {

			ResultSetMetaData meta = rs.getMetaData();
			while (rs.next()) {
				Map<String, Object> book = new LinkedHashMap<>(); // we use linkedhashmap tp preserve the insertion
																	// order
				book.put(meta.getColumnName(1), rs.getInt("book_id"));
				book.put(meta.getColumnName(2), rs.getString("title"));
				/*
				 * book.put(meta.getColumnName(3), rs.getString("description"));
				 * book.put(meta.getColumnName(4), rs.getDouble("cost"));
				 * book.put(meta.getColumnName(5), rs.getInt("quantity"));
				 * book.put(meta.getColumnName(6), rs.getString("topic"));
				 */
				allBooks.add(book);
			}

		} catch (SQLException e) {
			error = "Sorry , there is an error with data base : " + e.getMessage();
		}
		return allBooks;
	}
	
	
	
	
	
	
	
	
	private static Connection connectWithDB()   {
	    try {
			return DriverManager.getConnection("jdbc:sqlite:C:\\Users\\hp\\eclipse-workspace\\micro_web_services\\DBs\\online_book_store.db");
		} catch (SQLException e) {
			error="Sorry , there is an error with data base connection"+e.getMessage();
			return null;
		}
	}

}
