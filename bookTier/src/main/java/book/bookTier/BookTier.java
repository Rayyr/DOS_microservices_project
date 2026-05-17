package book.bookTier;
import static spark.Spark.*;

import com.google.gson.Gson;
import java.sql.*;
import java.util.*;

public class BookTier {

	 
	public static String error;
	static List<Map<String, Object>> books;

	
	public static void main(String[] args) {
	
	 
			
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
				return error;

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
				Map<String, Object> modifiedBook = getBook(id);

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
				Map<String, Object> modifiedBook = getBook(id);

				if (error != null)
					return error ;// error in extracting the book

				res.type("application/json");
				return new Gson().toJson(modifiedBook);
			}

			// update error
			return error ;
		});
		
		
		
		// POST op
		// create action for new book
		post("/addBook", (req, res) -> {

			error=null;//to earse the previous query error if it is have error
			// the new book to be added will be in the req body
			String body = req.body();

			Gson gson = new Gson();
			Map<String, Object> sentBook = gson.fromJson(body, Map.class);
			addBook(sentBook);

			res.type("text/plain");

			if (error != null)
				return error ;// error in adding the book

			res.type("application/json");
			return new Gson().toJson(sentBook);

		});
		
		
		//delete book
		delete("/deleteBook/:id", (req, res) -> {

			error=null;
		    int id = Integer.parseInt(req.params(":id"));

		    
			Map<String, Object> deletedBook = getBook(id);//get the book before deletion
			res.type("text/plain");
			
			if (error != null)
				return error ;// error in extracting the book
			

			 deleteBook(id);
			 
			 if (error != null)
					return error ;// error in deleting the book / with DB
			 
		      //successful deletion
		    	res.type("application/json");
				return new Gson().toJson(deletedBook);
		});
		 
	return ;
	}
	
	
	
	private static void deleteBook(int id) {
		
		  try (Connection dbCon = connectWithDB();
		        PreparedStatement ps = dbCon.prepareStatement("DELETE FROM Book WHERE book_id = ?");) {

		    	ps.setInt(1, id);
		        int rows = ps.executeUpdate();
		        

		        if (rows == 0) {
		            error= "Sorry , there is no such a book having this id in the store : "+id;
		        } 

		    } catch (SQLException e) {
		    	error = "Sorry , there is an error with data base : " + e.getMessage();
		    }
	}
	
	
	
	
	private static void addBook(Map<String, Object> sentBook) {

		try (Connection dbCon=connectWithDB();
				Statement statement = dbCon.createStatement();) {

			String sql = "insert into Book (book_id,title,description,cost,quantity,topic) values(?,?,?,?,?,?)";
			try (PreparedStatement ps = dbCon.prepareStatement(sql)) {

				ps.setInt(1, ((Double) sentBook.get("book_id")).intValue());
				ps.setString(2, (String) sentBook.get("title"));
				ps.setString(3, (String) sentBook.get("description"));
				ps.setDouble(4, (Double) sentBook.get("cost"));
				ps.setInt(5, ((Double) sentBook.get("quantity")).intValue());
				ps.setString(6, (String) sentBook.get("topic"));

				ps.executeUpdate();

			}

		} catch (SQLException e) {
			error = "Sorry , there is an error with data base : " + e.getMessage();

		}
	}
	
	
	
	
	private static int updateBookQuantityBasedToID(int id, int newQuantity) {

		try (Connection dbCon=connectWithDB();
				Statement statement = dbCon.createStatement();) {

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
	
	
	private static Map<String, Object> getBook(int id) {

		Map<String, Object> book = new LinkedHashMap<String, Object>();

		try (Connection dbCon=connectWithDB();
				Statement statement = dbCon.createStatement();
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

		try (Connection dbCon=connectWithDB();
				Statement statement =dbCon.createStatement();) {

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
		try (Connection dbCon=connectWithDB();
				Statement statement = dbCon.createStatement();
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
//i used try with resources syntax so after the db connection is made when the try scope is ended then by default the db connection will be closed to avoid db conflicts
		List<Map<String, Object>> allBooks = new ArrayList<>();
		try (Connection dbCon=connectWithDB();
				Statement statement = dbCon.createStatement();
				ResultSet rs = statement.executeQuery("SELECT book_id,title FROM Book where topic=\'" + topic + "\'")) {

			ResultSetMetaData meta = rs.getMetaData();
			while (rs.next()) {
				Map<String, Object> book = new LinkedHashMap<>(); // we use linkedhashmap tp preserve the insertion
																	// order
				book.put(meta.getColumnName(1), rs.getInt("book_id"));
				book.put(meta.getColumnName(2), rs.getString("title"));
				allBooks.add(book);
			}

		} catch (SQLException e) {
			error = "Sorry , there is an error with data base : " + e.getMessage();
		}
		return allBooks;
	}
	
	
	
	
	
	
	
	
	private static Connection connectWithDB()   {
	    try {
			return DriverManager.getConnection("jdbc:sqlite:/app2/data/online_book_store.db");
		} catch (SQLException e) {
			error="Sorry , there is an error with data base connection"+e.getMessage();
			return null;
		}
	}

}
