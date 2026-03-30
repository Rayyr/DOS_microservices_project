package dos_project.micro_web_services;
 

import static spark.Spark.*;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;

public class OrderTier {

	public static String error;

	public static void main(String[] args) {

		port(4561);

		post("/makeOrder/:id/:req_q", (req, res) -> {

			error = null;
			int id = Integer.parseInt(req.params(":id"));
			int req_q = Integer.parseInt(req.params(":req_q"));

			try (Connection dbCon = connectWithDB();
					Statement st = dbCon.createStatement();

					ResultSet rs = st.executeQuery("SELECT quantity FROM Book WHERE book_id=" + id);) {

				if (rs.next() == false) {
					return "Sorry the required book is not in the store "; // if we have the required book or no
				}
				// ther is a book of course its quantity >0 (quantity constarint)
				int quantity = rs.getInt("quantity");

				if (quantity >= req_q) {// valid purchase

					if(quantity==req_q) removeBook(dbCon,id);
					
					else updateBookQuantity(dbCon,req_q, id);
					
					
					if(error==null) {
						addNewOrder(dbCon,id);
						if(error==null) {
							
							Map<String, Object> updatedBook;
							// return the updated book
							if(quantity!=req_q)
								  updatedBook = getUpdatedBook(dbCon,id);
							else {
								res.type("text/plain");
								return "Order successful. The quantity matched the stock, so the book has been removed from the store.";
							}
						
							if(error==null) {
								res.type("application/json");
								return new Gson().toJson(updatedBook);
							}
							//ther is error in book extraction
							res.type("text/plain");
							return error; 
						}
						//there is an error in order addition
						res.type("text/plain");
						return error; 
					}
					
					//there is an error in quantity update/removal
					res.type("text/plain");
					return error;
					
				 
				}

				// the q is lager than the found stock for this book
				res.type("text/plain");
				return "Sorry the required book quantity is not avaliable";

			} catch (SQLException e) {
				error = e.getMessage();
			}

			res.type("text/plain");// the issue neither quantity nor the book searching but in DB connection or SqL
									// stmt exuction
			return error;
		});
		delete("/deleteBook/:id", (req, res) -> {

		    int id = Integer.parseInt(req.params(":id"));

		    try (Connection dbCon = connectWithDB();
		         PreparedStatement ps = dbCon.prepareStatement("DELETE FROM Book WHERE book_id = ?")) {

		        ps.setInt(1, id);
		        int rows = ps.executeUpdate();

		        res.type("text/plain");

		        if (rows == 0) {
		            return "Book not found";
		        }

		        return "Book deleted successfully";

		    } catch (SQLException e) {
		        res.type("text/plain");
		        return "Database error: " + e.getMessage();
		    }
		});
		
		get("/getOrders", (req, res) -> {

		    try (Connection dbCon = connectWithDB();
		         Statement st = dbCon.createStatement();
		         ResultSet rs = st.executeQuery("SELECT * FROM Order_t")) {

		        java.util.List<Map<String, Object>> orders = new java.util.ArrayList<>();

		        while (rs.next()) {
		            Map<String, Object> order = new LinkedHashMap<>();
		            order.put("order_id", rs.getInt("order_id"));
		            order.put("book_id", rs.getInt("book_id"));

		            orders.add(order);
		        }

		        res.type("application/json");
		        return new Gson().toJson(orders);

		    } catch (SQLException e) {
		        res.type("text/plain");
		        return "Database error: " + e.getMessage();
		    }
		});
	}
	

	private static void removeBook(Connection dbCon, int id) {

		try  {

			PreparedStatement ps = dbCon.prepareStatement("DELETE from Book WHERE book_id = ?");
			 
			ps.setInt(1, id);
			ps.executeUpdate();
		
		}

		catch (SQLException e) {
			error = "Sorry , there is an error with data base : " + e.getMessage();
		}

	}
	
	
	private static void updateBookQuantity(Connection dbCon,int req_q, int id) {

		try  {

			PreparedStatement ps = dbCon.prepareStatement("UPDATE Book SET quantity = quantity - ? WHERE book_id = ?");

			ps.setInt(1, req_q); // how much to decrease
			ps.setInt(2, id);
			ps.executeUpdate();
		
		}

		catch (SQLException e) {
			error = "Sorry , there is an error with data base : " + e.getMessage();
		}

	}

	private static void addNewOrder(Connection dbCon,int id) {

		try   {

			PreparedStatement ps = dbCon.prepareStatement("INSERT INTO Order_t (book_id) VALUES (?)");
			ps.setInt(1, id);  
			ps.executeUpdate();

		}

		catch (SQLException e) {
			error = "Sorry , there is an error with data base : " + e.getMessage();
		}
	}

	private static Map<String, Object> getUpdatedBook(Connection dbCon,int id) {

		Map<String, Object> book = new LinkedHashMap<String, Object>();

		try (
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

	private static Connection connectWithDB() {
		try {
			return DriverManager.getConnection("jdbc:sqlite:C:\\Users\\PC\\eclipse-workspace\\DOS_microservices_project\\DBs\\online_book_store.db");

		} catch (SQLException e) {
			error = "Sorry , there is an error with data base connection" + e.getMessage();
			return null;
		}
	}
}