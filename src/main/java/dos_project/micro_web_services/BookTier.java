package dos_project.micro_web_services;

import static spark.Spark.*;

import com.google.gson.Gson;
import java.sql.*;
import java.util.*;

public class BookTier {

	public static void main(String[] args) {
	port(4567);
		
		get("/bb",(req,res)->{
			 
			return "hello from catalog";
		});

	}

}
