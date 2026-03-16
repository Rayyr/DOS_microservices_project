package dos_project.micro_web_services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static spark.Spark.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.io.*;

public class frontendTier {

	 

	public static void main(String[] args) {

		port(4566);

		// GET books (calls catalog(book) service)
		get("/books/search/:topic", (req, res) -> {

			String topic = req.params(":topic");// extract topic from the URL

			
			//encode the illegal chars as spaces since they are not allowed 
			 String encodedTopic = URLEncoder.encode(topic, StandardCharsets.UTF_8) .replace("+", "%20");
			 
			//redirect the request from the front end to book tier
			//send the request to lower layer which is : booktier
            URL url = new URL("http://localhost:4560/search/"+encodedTopic);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            if(con.getContentType().equals("application/json"))//get the sender response type format 
            res.type("application/json");
            else //plain text
            res.type("text/plain");
            return response.toString();
		});

	
		
		

		// GET books (calls catalog(book) service)
		get("/books/info/:id", (req, res) -> {

			String id = req.params(":id");// extract topic from the URL

			//redirect the request from the front end to book tier
			//send the request to lower layer which is : booktier
            URL url = new URL("http://localhost:4560/info/"+id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            if(con.getContentType().equals("application/json"))//get the sender response type format 
            res.type("application/json");
            else //plain text
            res.type("text/plain");
            return response.toString();
		});
		
		
		
		// UPDATE book cost (calls catalog(book) service)
		patch("/books/updateCost/:id", (req, res) -> {

			 
			String id = req.params(":id");// extract topic from the URL

			
			//redirect the request from the front end to book tier
			//send the request to lower layer which is : booktier
            URL url = new URL("http://localhost:4560/updateCost/"+id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST"); // Use POST + Override if PATCH fails sice the standard http library does not support direct patch action
            con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            
            // Write body to write the new cost
            try (OutputStream os = con.getOutputStream()) {
                os.write(req.bodyAsBytes());
            }
             
            
            
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            if(con.getContentType().equals("application/json"))//get the sender response type format 
            res.type("application/json");
            else //plain text
            res.type("text/plain");
            return response.toString();
		});
		
		
		
		
		// UPDATE book quantity (calls catalog(book) service)
		patch("/books/updateQuantity/:id", (req, res) -> {

			 
			String id = req.params(":id");// extract topic from the URL

			
			//redirect the request from the front end to book tier
			//send the request to lower layer which is : booktier
            URL url = new URL("http://localhost:4560/increaseQuantity/"+id);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST"); // Use POST + Override if PATCH fails sice the standard http library does not support direct patch action
            con.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);

            
            // Write body to write the new quantity
            try (OutputStream os = con.getOutputStream()) {
                os.write(req.bodyAsBytes());
            }
             
            
            
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            if(con.getContentType().equals("application/json"))//get the sender response type format 
            res.type("application/json");
            else //plain text
            res.type("text/plain");
            return response.toString();
		});
	}

 

}