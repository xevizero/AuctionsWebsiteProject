package it.polimi.tiw.auctions.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import it.polimi.tiw.auctions.beans.User;


public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}
	
	public User checkUser(String username, String password) throws SQLException {
		User user = null;
		String query = "SELECT * FROM users WHERE username = ? and password = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, username);
			pstatement.setString(2, password);
			result = pstatement.executeQuery();
			while (result.next()) {
				user = new User();
				user.setId(result.getInt("id"));
				user.setName(result.getString("name"));
				user.setSurname(result.getString("surname"));
				user.setEmail(result.getString("email"));
				user.setUsername(result.getString("username"));
			}
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);

		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}		
		return user;
	}

	public User getUserInfo(int userId) throws SQLException {
		User user1 = new User(userId);
		String query = "SELECT username, name, surname, email FROM users WHERE id = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, userId);
			result = pstatement.executeQuery();
			while (result.next()) {
				user1.setUsername(result.getString("username"));
				user1.setName(result.getString("name"));
				user1.setSurname(result.getString("surname"));
				user1.setEmail(result.getString("email"));
			}
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);

		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}
		return user1;
	}

}