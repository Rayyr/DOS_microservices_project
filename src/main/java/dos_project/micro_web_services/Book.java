package dos_project.micro_web_services;

import static spark.Spark.*;

import com.google.gson.Gson;
import java.sql.*;
import java.util.*;

public class Book {
 
	static List<Map<String, Object>> books;
	public static void routing( ) {
		 
		//GET action for all books  
		get("/getBooks/all", (req, res) -> {
			books=getAllBooks();
			if(books.size()==0) return "There is no books in the stock!";
			res.type("application/json");    // response type
			return new Gson().toJson(books); // convert to JSON
		});
		
		
		
		
		
		
		//GET action for specific books based to specific topic by passing it via path params of the request
		get("/search/:topic",(req,res)->{
			String topic=req.params(":topic");//extract topic from the URL
			books=getBooksBasedToTopic(topic);
			if(books.size()==0) return "There is no matching books with the specified topic : "+topic;
			res.type("application/json");    // response type
			return new Gson().toJson(books); // convert to JSON
		});
	 
		
		
		
		
		
		//GET action for specific book information based to specific id by passing it via path params of the request
		get("/info/:id",(req,res)->{
			int id=Integer.parseInt(req.params(":id"));//extract id from the URL
			books=getBookInfoBasedToID(id);
			if(books.size()==0) return "There is no book associated with the specified ID : "+id;
			res.type("application/json");    // response type
			return new Gson().toJson(books); // convert to JSON
		});
		
		
	}

	
	
	
	
	
	private static List<Map<String, Object>> getBookInfoBasedToID(int id) {
		
		List<Map<String, Object>> allBooks=new ArrayList<>();
		try ( 
			     Statement statement = Api.dbCon.createStatement();
			     ResultSet rs = statement.executeQuery("SELECT * from Book where book_id="+id)) {

			ResultSetMetaData meta = rs.getMetaData();
			  while (rs.next()) {
                  Map<String, Object> book = new LinkedHashMap<>(); //we use linkedhashmap tp preserve the insertion order 
                  book.put(meta.getColumnName(2), rs.getString("title"));
                  book.put(meta.getColumnName(3), rs.getString("description"));
                  book.put(meta.getColumnName(4), rs.getDouble("cost"));
                  book.put(meta.getColumnName(5), rs.getInt("quantity"));
                  book.put(meta.getColumnName(6), rs.getString("topic"));
                  allBooks.add(book);
              }

			} catch (SQLException e) {
				System.err.print("Sorry , there is an error with data base : "+e.getMessage());
			}
		return allBooks;
	}
	
	
	
	
	
	private static List<Map<String, Object>> getBooksBasedToTopic(String topic) {
		
		List<Map<String, Object>> allBooks=new ArrayList<>();
		try ( 
			     Statement statement = Api.dbCon.createStatement();
			     ResultSet rs = statement.executeQuery("SELECT book_id,title FROM Book where topic=\'"+topic+"\'")) {

			ResultSetMetaData meta = rs.getMetaData();
			  while (rs.next()) {
                  Map<String, Object> book = new LinkedHashMap<>(); //we use linkedhashmap tp preserve the insertion order 
                  book.put(meta.getColumnName(1), rs.getInt("book_id"));
                  book.put(meta.getColumnName(2), rs.getString("title"));
                /*  book.put(meta.getColumnName(3), rs.getString("description"));
                  book.put(meta.getColumnName(4), rs.getDouble("cost"));
                  book.put(meta.getColumnName(5), rs.getInt("quantity"));
                  book.put(meta.getColumnName(6), rs.getString("topic"));*/
                  allBooks.add(book);
              }

			} catch (SQLException e) {
				System.err.print("Sorry , there is an error with data base : "+e.getMessage());
			}
		return allBooks;
	}
	
	
	
	
	private static List<Map<String, Object>> getAllBooks() {
	
		List<Map<String, Object>> allBooks=new ArrayList<>();
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
				System.err.print("Sorry , there is an error with data base : "+e.getMessage());
			}
		return allBooks;
	}
	 
}