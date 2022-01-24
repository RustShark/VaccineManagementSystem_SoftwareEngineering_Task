package com.vaccinemanagementsystem.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Database {
	//데이터베이스 객체
	
	private String driver = "org.mariadb.jdbc.Driver";
	private Connection connection;
	private PreparedStatement pstmt;
	private ResultSet result;
	
	public Database() {
		
	}
	
	public int connect() {
		int check = 1;
		
	    try {
	    	Class.forName(driver);
	        connection = DriverManager.getConnection(
	        		"jdbc:mariadb://3.36.70.196:3306/Covid19Vaccine", 
	        		"sogong", 
	        		"1110");
	           
	        if( connection != null ) {
	        	check = 1;
	        } else {
	        	check = 0;
	        }
	           
	    } catch (ClassNotFoundException e) { 
	        System.out.println("Driver Load Failed");
	    } catch (SQLException e) {
	        System.out.println("Failed Access Database.");
	        e.printStackTrace();
	    }
	    return check;
	}
	
	public void queryToDB(String query) {
		try {
			pstmt = connection.prepareStatement(query);
			result = pstmt.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQLException Error : " + e);
		} finally {
			try {
				if(result != null) {
					result.close();
				}
				if(pstmt != null) {
					pstmt.close();
				}
				
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Vector<String[]> getData(String query, int size) {
		Vector<String[]> resultData = new Vector<String[]>();
		
		try {
			pstmt = connection.prepareStatement(query);
			result = pstmt.executeQuery();
			
			while(result.next()) {
				String data[] = new String[size];
				for(int idx = 0; idx < size; idx++) {
					int temp = idx + 1;
					
					data[idx] = result.getString(temp);
				}
				resultData.add(data);
			}
		} catch (SQLException e) {
			System.out.println("SQLException Error : " + e);
		} finally {
			try {
				if(result != null) {
					result.close();
				}
				if(pstmt != null) {
					pstmt.close();
				}
				
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return resultData;
	}
}
