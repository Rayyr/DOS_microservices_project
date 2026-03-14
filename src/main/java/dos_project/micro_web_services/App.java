package dos_project.micro_web_services;

import static spark.Spark.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
	public static void main(String[] args) {

		Book.routing();
	}
}