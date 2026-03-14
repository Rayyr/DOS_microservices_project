package dos_project.micro_web_services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Api {
	
	public static Connection dbCon;
	public static void main(String[] args) {

		dbCon=connectWithDB();
		if(dbCon!=null) {
		Book.routing();
		}
	}
	
	private static Connection connectWithDB()   {
	    try {
			return DriverManager.getConnection("jdbc:sqlite:C:\\Users\\hp\\eclipse-workspace\\micro_web_services\\DBs\\online_book_store.db");
		} catch (SQLException e) {
			System.err.print("Sorry , there is an error with data base connection");
			return null;
		}
	}

}