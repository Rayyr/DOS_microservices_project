package front.frontTier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import static spark.Spark.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;

public class frontendTier {

	//gson reads the numeric values as double 
	public static int status;
	public static String error;
	
	public static void main(String[] args) {

		port(4566);

		// GET books based on specific topic (calls catalog(book) service)
		get("/books/search/:topic", (req, res) -> {

			String topic = req.params(":topic");// extract topic from the URL

			// encode the illegal chars as spaces since they are not allowed
			String encodedTopic = URLEncoder.encode(topic, StandardCharsets.UTF_8).replace("+", "%20");

			// redirect the request from the front end to book tier
			// send the request to lower layer which is : booktier
			URL url = new URL("http://booktier:4560/search/" + encodedTopic);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("GET");

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();

			if (con.getContentType().equals("application/json"))// get the sender response type format
				res.type("application/json");
			else // plain text
				res.type("text/plain");
			return response.toString();
		});

		// GET book info based to id (calls catalog(book) service)
		get("/books/info/:id", (req, res) -> {

			String id = req.params(":id");// extract topic from the URL

			//id verification 
			//verify it is integral value as 2,3.0...
			if((int)Double.parseDouble(id)!=Double.parseDouble(id)) {
				res.type("text/plain");
				return "Sorry the id must be integral value";
			}
			
			//verify id>0
			if(Integer.parseInt(id)<0) {
				res.type("text/plain");
				return "Sorry the id must be positive or zero (>=0)";
			}
			
			// redirect the request from the front end to book tier
			// send the request to lower layer which is : booktier
			URL url = new URL("http://booktier:4560/info/" + id);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("GET");

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();

			if (con.getContentType().equals("application/json"))// get the sender response type format
				res.type("application/json");
			else // plain text
				res.type("text/plain");
			return response.toString();
		});

		
		
		// UPDATE book cost (calls catalog(book) service)
		patch("/books/updateCost/:id", (req, res) -> {

			String id = req.params(":id");// extract topic from the URL

			//id verification 
			//verify it is integral value as 2,3.0...
			if((int)Double.parseDouble(id)!=Double.parseDouble(id)) {
				res.type("text/plain");
				return "Sorry the id must be integral value";
			}
			
			//verify id>0
			if(Integer.parseInt(id)<0) {
				res.type("text/plain");
				return "Sorry the id must be positive or zero (>=0)";
			}
			
	

			Gson gson = new Gson();
			Map<String, Object> bodyMap = gson.fromJson(req.body(), Map.class);
			double cost = ((Double) bodyMap.get("newCost"));// from req body

			//Cost verification
			//verify it is + value
			if(cost<=0) {
				res.type("text/plain");
				return "Sorry the cost must be positive (>0)";
			}
			
			//evrything is ok
			// redirect the request from the front end to book tier
			// send the request to lower layer which is : booktier
			URL url = new URL("http://booktier:4560/updateCost/" + id);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("POST"); // Use POST + Override if PATCH fails sice the standard http library does not
											// support direct patch action
			con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);
			
			
			// Write body to write the new cost
			try (OutputStream os = con.getOutputStream()) {
				os.write(req.bodyAsBytes());
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();

			if (con.getContentType().equals("application/json"))// get the sender response type format
				res.type("application/json");
			else // plain text
				res.type("text/plain");
			return response.toString();
		});

		
		
		// UPDATE book quantity (calls catalog(book) service)
		patch("/books/increaseQuantity/:id", (req, res) -> {

			status=1;
			error=null;
		/*	// the quantity must be increased when it is updated since we cant decrease it
			// in case there are an prebooked orders ..
			// but when the order is submitted there is implicit calling for route to
			// decrese the quantity by 1 but if the route is calling directlly from front
			// end tier so only we must increase it*/
			String id = req.params(":id");// extract topic from the URL

			//id verification 
			//verify it is integral value as 2,3.0...
			if((int)Double.parseDouble(id)!=Double.parseDouble(id)) {
				res.type("text/plain");
				return "Sorry the id must be integral value";
			}
			
			//verify id>0
			if(Integer.parseInt(id)<0) {
				res.type("text/plain");
				return "Sorry the id must be positive or zero (>=0)";
			}
			
	

			// verify that the entered quantity is > (strictlly greater )than old one since
			// it is an increament
			// must be
			Gson gson = new Gson();
			Map<String, Object> bodyMap = gson.fromJson(req.body(), Map.class);
			int newQuantity = ((Double) bodyMap.get("newQuantity")).intValue();// from req body

			//Quantity verification 
			//verify it is integral value as 2,3.0...
			if(newQuantity!=(Double)bodyMap.get("newQuantity")) {
				res.type("text/plain");
				return "Sorry the new quantity must be integral value";
			}
			 
			int oldQuantity = getBookQuantity(Integer.parseInt(id));  
			
			//DB issue
			if(status==-1) {
				res.type("text/plain");
				return error;
			}
	 
			//verify it is + value
			/*in case it is = 0 then it can be detected in the last verification step since when you create a book it is invalid to make its quantity=0(logically)*/
			if(newQuantity <= 0) {
				res.type("text/plain");
				return "Sorry the new quantity must be positive (>0)";
			}

			//verify it is > oldQuantity
			if (newQuantity <= oldQuantity) {
				res.type("text/plain");
				return "Sorry the new quantity must be strictlly greater than the old quantity";
			}
			
			
			// redirect the request from the front end to book tier
			// send the request to lower layer which is : booktier
			URL url = new URL("http://booktier:4560/increaseQuantity/" + id);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("POST"); // Use POST + Override if PATCH fails sice the standard http library does not
											// support direct patch action
			con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);
			
			
			// Write body to write the new quantity
			try (OutputStream os = con.getOutputStream()) {
				os.write(req.bodyAsBytes());
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();

			if (con.getContentType().equals("application/json"))// get the sender response type format
				res.type("application/json");
			else // plain text
				res.type("text/plain");
			return response.toString();
		});

		
		
		// CREATE new book (calls catalog(book) service)
		post("/books/addBook", (req, res) -> {

			Gson gson = new Gson();
			Map<String, Object> bodyMap = gson.fromJson(req.body(), Map.class);
			int quantity = ((Double) bodyMap.get("quantity")).intValue();// from req body
			int id = ((Double) bodyMap.get("book_id")).intValue();// from req body
			double cost = ((Double) bodyMap.get("cost"));// from req body

			 //Quantity verification			
			//verify it is integral value as 2,3.0...
			if(quantity!=(Double)bodyMap.get("quantity")) {//5!=5.3
				res.type("text/plain");
				return "Sorry the quantity must be integral value";
			}
			
			//verify it is + value
			if(quantity <= 0) {
				res.type("text/plain");
				return "Sorry the quantity must be positive (>0)";
			}
			
			
			//Cost verification
			//verify it is + value
			if(cost <= 0) {
				res.type("text/plain");
				return "Sorry the cost must be positive (>0)";
			}
			
			//id verification
			//verify it is + value
			if(id < 0) {
				res.type("text/plain");
				return "Sorry the id must be positive or zero (>=0)";
			}
			
			//verify it is integral value as 2,3.0...
			if(id!=(Double)bodyMap.get("book_id")) {//5!=5.3
				res.type("text/plain");
				return "Sorry the id must be integral value";
			}
			
			// redirect the request from the front end to book tier
						// send the request to lower layer which is : booktier
						URL url = new URL("http://booktier:4560/addBook");
						HttpURLConnection con = (HttpURLConnection) url.openConnection();

						con.setRequestMethod("POST");
						con.setRequestProperty("Content-Type", "application/json");
						con.setDoOutput(true);
			
			// Write body to write the new book to be added
			try (OutputStream os = con.getOutputStream()) {
				os.write(req.bodyAsBytes());
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();

			if (con.getContentType().equals("application/json"))// get the sender response type format
				res.type("application/json");
			else // plain text
				res.type("text/plain");
			return response.toString();
		});
		
		
		//delete book based to id
		delete("/books/deleteBook/:id", (req, res) -> {

		    String id = req.params(":id");

		    
			//id verification
			//verify it is integral value
		    if((int)Double.parseDouble(id)!=Double.parseDouble(id)) {//5!=5.3
				res.type("text/plain");
				return "Sorry the id must be integral value";
			}
			
			//verify it is + value
			if(Integer.parseInt(id) < 0) {
				res.type("text/plain");
				return "Sorry the id must be positive or zero (>=0)";
			}
			
		    URL url = new URL("http://booktier:4560/deleteBook/" + id);
		    HttpURLConnection con = (HttpURLConnection) url.openConnection();

		    con.setRequestMethod("DELETE");

		    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		    String inputLine;
		    StringBuilder response = new StringBuilder();

		    while ((inputLine = in.readLine()) != null) {
		        response.append(inputLine);
		    }

		    in.close();

			if (con.getContentType().equals("application/json"))// get the sender response type format
				res.type("application/json");
			else  res.type("text/plain");
		   
		    return response.toString();
		});

		
		
		// make order (calls order service)
		post("/orders/makeOrder/:id/:req_q", (req, res) -> {

			String id = req.params(":id");// extract id from the URL
			String req_q = req.params(":req_q");// extract req_q from the URL
			
			 //Quantity verification
			//verify it is integral value
			if((int)Double.parseDouble(req_q)!=Double.parseDouble(req_q)) {
				res.type("text/plain");
				return "Sorry the quantity must be integral value";
			}
			
			//verify it is + value
			if(Integer.parseInt(req_q) <= 0) {
				res.type("text/plain");
				return "Sorry the quantity must be positive (>0)";
			}
			
			 //id verification
			//verify it is integral value
			if((int)Double.parseDouble(id)!=Double.parseDouble(id)) {
				res.type("text/plain");
				return "Sorry the id must be integral value";
			}
			
			//verify it is + value
			if(Integer.parseInt(id) < 0) {
				res.type("text/plain");
				return "Sorry the id must be positive or zero(>=0)";
			}
			
			// redirect the request from the front end to order tier
			// send the request to lower layer which is : ordertier
			URL url = new URL("http://ordertier:4561/makeOrder/" + id + "/" + req_q);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("POST");
   
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String inputLine;
			StringBuilder response = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();

			if (con.getContentType().equals("application/json"))// get the sender response type format
				res.type("application/json");
			else // plain text
				res.type("text/plain");
			
			return response.toString();
			 
		});
		
		
	
		//get all made orders
		get("/orders/getOrders", (req, res) -> {

		    URL url = new URL("http://ordertier:4561/getOrders");
		    HttpURLConnection con = (HttpURLConnection) url.openConnection();

		    con.setRequestMethod("GET");

		    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

		    String inputLine;
		    StringBuilder response = new StringBuilder();

		    while ((inputLine = in.readLine()) != null) {
		        response.append(inputLine);
		    }

		    in.close();

		    if (con.getContentType().equals("application/json"))
		        res.type("application/json");
		    else
		        res.type("text/plain");

		    return response.toString();
		});
	}

	
	




	private static int getBookQuantity(int id) {

		try (Connection con = DriverManager.getConnection("jdbc:sqlite:/app/data/online_book_store.db");
				Statement statement = con.createStatement();) {
			ResultSet rs = statement.executeQuery("SELECT quantity FROM Book where book_id=" + id);

			 status=1;
			return rs.getInt("quantity");

		} catch (SQLException e) {
			error= "Sorry , there is an error with data base : " + e.getMessage();
			status=-1;
			return -1;//just to return such as a value
		}

	}
}