package dos_project.micro_web_services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static spark.Spark.*;
import java.net.*;
import java.io.*;

public class Api {

	public static Connection dbCon;

	public static void main(String[] args) {

		port(4569);

		// get books (calls catalog service)
		get("/aa", (req, res) -> {


            URL url = new URL("http://localhost:4567/bb");
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

            return response.toString();
		});

	
	}

	private static Connection connectWithDB() {
		try {
			return DriverManager.getConnection(
					"jdbc:sqlite:C:\\Users\\hp\\eclipse-workspace\\micro_web_services\\DBs\\online_book_store.db");
		} catch (SQLException e) {
			System.err.print("Sorry , there is an error with data base connection");
			return null;
		}
	}

}