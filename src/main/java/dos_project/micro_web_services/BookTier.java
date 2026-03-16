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
		 
		// GET action for specific books based to specific topic by passing it via path
				// params of the request
		get("/search/:topic",(req,res)->{
			 
			error=null;
			String topic = req.params(":topic");// extract topic from the URL
			books = getBooksBasedToTopic(topic);

			res.type("text/plain");
			if (error != null)
				return error ;

			if (books.size() == 0)
				return "There is no matching books with the specified topic : " + topic;

			res.type("application/json"); // response type
			return new Gson().toJson(books); // convert to JSON
		});

		
		
		// GET action for specific book information based to specific id by passing it
		// via path params of the request
		get("/info/:id", (req, res) -> {
			error=null;
			int id = Integer.parseInt(req.params(":id"));// extract id from the URL
			books = getBookInfoBasedToID(id);

			res.type("text/plain");
			if (error != null)
				return error ;

			if (books.size() == 0)
				return "There is no book associated with the specified ID : " + id;

			res.type("application/json"); // response type
			return new Gson().toJson(books); // convert to JSON
		});
		
		
		
		
		// Partial UPDATE action to update the cost of the specified book based to the
		// passed id via path params of the request
		patch("/updateCost/:id", (req, res) -> {
			
			error=null;
			// header : json format(default)
			int id = Integer.parseInt(req.params(":id"));// extract id from the URL
			// the newCost will be inside req body
			String body = req.body();// Read req JSON body as String
			 
			Gson gson = new Gson();
			Map<String, Double> sentUpdates = gson.fromJson(body, Map.class);// {"newCost":double_value};

			int status = updateBookCostBasedToID(id, sentUpdates.get("newCost"));

			res.type("text/plain");
			if (status == 1) {
				Map<String, Object> modifiedBook = getModifiedBook(id);

				if (error != null)
					return error ;// error in extracting the book

				res.type("application/json");
				return new Gson().toJson(modifiedBook);
			}

			return error ;
		});
		
		
		
		// Partial UPDATE action to update the quantity of the specified book based to
		// the passed id via path params of the request
		patch("/increaseQuantity/:id", (req, res) -> {
			// header : json format

			error=null;
			int id = Integer.parseInt(req.params(":id"));// extract id from the URL
			String body = req.body();// Read JSON body as String

			Gson gson = new Gson();
			Map<String, Object> sentUpdates = gson.fromJson(body, Map.class);

			int status = updateBookQuantityBasedToID(id, ((Double) sentUpdates.get("newQuantity")).intValue());

			res.type("text/plain");

			if (status == 1) {// no update error
				Map<String, Object> modifiedBook = getModifiedBook(id);

				if (error != null)
					return error ;// error in extracting the book

				res.type("application/json");
				return new Gson().toJson(modifiedBook);
			}

			// update error
			return error ;
		});
		
		
		
		
		}
		
		else System.err.print(error);
	return ;
	}
	
	
	
	private static int updateBookQuantityBasedToID(int id, int newQuantity) {

		try (Statement statement = dbCon.createStatement();) {

			String sql = "UPDATE Book SET quantity=? WHERE book_id=?";
			try (PreparedStatement ps = dbCon.prepareStatement(sql)) {
				ps.setInt(1, newQuantity);
				ps.setInt(2, id);
				ps.executeUpdate();
				return 1;// succes
			}

		} catch (SQLException e) {
			error = "Sorry , there is an error with data base : " + e.getMessage();
			return -1;
		}

	}
	
	
	private static Map<String, Object> getModifiedBook(int id) {

		Map<String, Object> book = new LinkedHashMap<String, Object>();

		try (Statement statement = dbCon.createStatement();
				ResultSet rs = statement.executeQuery("SELECT * FROM Book where book_id=" + id)) {

			ResultSetMetaData meta = rs.getMetaData();

			book.put(meta.getColumnName(2), rs.getString("title"));
			book.put(meta.getColumnName(3), rs.getString("description"));
			book.put(meta.getColumnName(4), rs.getDouble("cost"));
			book.put(meta.getColumnName(5), rs.getInt("quantity"));
			book.put(meta.getColumnName(6), rs.getString("topic"));

		}

		catch (SQLException e) {
			error = "Sorry , there is an error with data base : " + e.getMessage();
		}

		return book;
	}
	
	
	

	private static int updateBookCostBasedToID(int id, double newCost) {

		try (Statement statement =dbCon.createStatement();) {

			String sql = "UPDATE Book SET cost=? WHERE book_id=?";
			try (PreparedStatement ps = dbCon.prepareStatement(sql)) {
				ps.setDouble(1, newCost);
				ps.setInt(2, id);
				ps.executeUpdate();
				return 1;
			}

		} catch (SQLException e) {
			error = "Sorry , there is an error with data base : " + e.getMessage();
			return -1;
		}

	}
	
	
	
	
	private static List<Map<String, Object>> getBookInfoBasedToID(int id) {

		List<Map<String, Object>> allBooks = new ArrayList<>();
		try (Statement statement = dbCon.createStatement();
				ResultSet rs = statement.executeQuery("SELECT * from Book where book_id=" + id)) {

			ResultSetMetaData meta = rs.getMetaData();
			while (rs.next()) {
				Map<String, Object> book = new LinkedHashMap<>(); // we use linkedhashmap tp preserve the insertion
																	// order
				book.put(meta.getColumnName(2), rs.getString("title"));
				book.put(meta.getColumnName(3), rs.getString("description"));
				book.put(meta.getColumnName(4), rs.getDouble("cost"));
				book.put(meta.getColumnName(5), rs.getInt("quantity"));
				book.put(meta.getColumnName(6), rs.getString("topic"));
				allBooks.add(book);
			}

		} catch (SQLException e) {
			error = "Sorry , there is an error with data base : " + e.getMessage();
		}
		return allBooks;
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
