package dos_project.micro_web_services;

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

		// GET books (calls catalog(book) service)
		get("/books/search/:topic", (req, res) -> {

			String topic = req.params(":topic");// extract topic from the URL

			// encode the illegal chars as spaces since they are not allowed
			String encodedTopic = URLEncoder.encode(topic, StandardCharsets.UTF_8).replace("+", "%20");

			// redirect the request from the front end to book tier
			// send the request to lower layer which is : booktier
			URL url = new URL("http://localhost:4560/search/" + encodedTopic);
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

		// GET books (calls catalog(book) service)
		get("/books/info/:id", (req, res) -> {

			String id = req.params(":id");// extract topic from the URL

			// redirect the request from the front end to book tier
			// send the request to lower layer which is : booktier
			URL url = new URL("http://localhost:4560/info/" + id);
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

			// redirect the request from the front end to book tier
			// send the request to lower layer which is : booktier
			URL url = new URL("http://localhost:4560/updateCost/" + id);
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

			// redirect the request from the front end to book tier
			// send the request to lower layer which is : booktier
			URL url = new URL("http://localhost:4560/increaseQuantity/" + id);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("POST"); // Use POST + Override if PATCH fails sice the standard http library does not
											// support direct patch action
			con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);

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
			if(newQuantity <= 0) {
				res.type("text/plain");
				return "Sorry the new quantity must be positive";
			}

			//verify it is > oldQuantity
			if (newQuantity <= oldQuantity) {
				res.type("text/plain");
				return "Sorry the new quantity must be strictlly greater than the old quantity";
			}
			
			
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

		// CREATE book (calls catalog(book) service)
		post("/books/addBook", (req, res) -> {

			// redirect the request from the front end to book tier
			// send the request to lower layer which is : booktier
			URL url = new URL("http://localhost:4560/addBook");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);

			Gson gson = new Gson();
			Map<String, Object> bodyMap = gson.fromJson(req.body(), Map.class);
			int quantity = ((Double) bodyMap.get("quantity")).intValue();// from req body

			 //Quantity verification			
			//verify it is integral value as 2,3.0...
			if(quantity!=(Double)bodyMap.get("quantity")) {//5!=5.3
				res.type("text/plain");
				return "Sorry the new quantity must be integral value";
			}
			
			//verify it is + value
			if(quantity <= 0) {
				res.type("text/plain");
				return "Sorry the new quantity must be positive";
			}
			
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
	}

	private static int getBookQuantity(int id) {

		try (Connection con = DriverManager.getConnection(
				"jdbc:sqlite:C:\\Users\\hp\\eclipse-workspace\\micro_web_services\\DBs\\online_book_store.db");
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