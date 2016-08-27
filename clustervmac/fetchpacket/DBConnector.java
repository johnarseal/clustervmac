package clustervmac.fetchpacket;

import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DBConnector {
	private Connection connection;
	private String dbType;
	private String dbAddr;
	private String dbUsr;
	private String dbPwd;
	private Statement stmt;
	
	public DBConnector(String database){
		if(database == "PostgreSQL"){
	        try {
	            Class.forName("org.postgresql.Driver");
		    } catch (ClassNotFoundException e) {
		            System.out.println("PostgreSQL JDBC Driver is NOT Included!");
		            e.printStackTrace();
		            System.exit(1);
		    }
	        System.out.println("PostgreSQL JDBC Driver Registered!");
	        connection = null;
		}
		else{
			System.out.println(database+" is not supported");
			System.exit(0);
		}
		dbType = database;
	}
	public void connect(String addr, String usr, String pwd){
		if(dbType == "PostgreSQL"){
	        try {
                connection = DriverManager.getConnection(
                                "jdbc:postgresql://"+addr, usr,
                                pwd);
	        } catch (SQLException e) {
	                System.out.println("Connection Failed! Check output console");
	                e.printStackTrace();
	                System.exit(1);
	        }		
		}
        if (connection != null) {
            System.out.println("You made it, take control your database now!");
	    } else {
	        System.out.println("Failed to make connection!");
	        System.exit(1);
	    }
		dbAddr = addr;
		dbUsr = usr;
		dbPwd = pwd;
		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("create statement failed!");
			e.printStackTrace();
			System.exit(1);
		}
	}
	public ResultSet execute(String sql){
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}
}
