package it.polimi.tiw.auctions.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductDAO {
	private Connection con;

	public ProductDAO(Connection connection) {
		this.con = connection;
	}
	
	public int createProduct(String name, String desc, InputStream imageStream) throws SQLException {
		String query = "INSERT into products (code, name, description, image) VALUES (SUBSTRING(MD5(RAND()) FROM 1 FOR 10), ?, ?, ?)";
		PreparedStatement pstatement = null;
		int newProductId = 0;		
		try {
			pstatement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			pstatement.setString(1, name);
			pstatement.setString(2, desc);
			pstatement.setBlob(3, imageStream);
			pstatement.executeUpdate();
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);
		} finally {
			try {
				ResultSet keys = pstatement.getGeneratedKeys();
			    if(keys.next()) {
				    	newProductId = keys.getInt((1));
				}
				pstatement.close();
			} catch (Exception e1) {}
		}
		return newProductId;
	}
}
