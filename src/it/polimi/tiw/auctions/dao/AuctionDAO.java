package it.polimi.tiw.auctions.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


import it.polimi.tiw.auctions.beans.Auction;
import it.polimi.tiw.auctions.beans.Offer;
import it.polimi.tiw.auctions.beans.Product;
import it.polimi.tiw.auctions.beans.User;


public class AuctionDAO {
	private Connection con;

	public AuctionDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<Auction> listAuctions(int userId, boolean open, boolean withId, String searchQuery) throws SQLException {
		List<Auction> auctions = new ArrayList<Auction>();
		String query = "SELECT A.*, U.username, U.name, U.surname, U.email, P.id, P.code, P.name, P.description, P.image FROM auctions as A JOIN users as U ON A.ownerid = U.id JOIN products as P ON P.id = A.productid WHERE ownerid "
				+ ((withId)?"=":"<>") +
				" ? AND active = ? " 
				+ ((searchQuery == null)?"":"AND concat(A.title, P.description, P.name) LIKE ?") +
				" ORDER BY endtime ASC, title DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, userId);
			pstatement.setBoolean(2, open);
			if(searchQuery != null)
				pstatement.setString(3, "%" + searchQuery + "%");
			result = pstatement.executeQuery();
			while (result.next()) {			
				auctions.add(buildAuctionObj(result, userId));
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
		return auctions;
	}

	public List<Auction> listUserAuctions(int userId, boolean open) throws SQLException {
		return listAuctions(userId, open, true, null);
	}
	
	public List<Auction> listNonUserAuctions(int userId, boolean open) throws SQLException {
		return listAuctions(userId, open, false, null);
	}
	
	public List<Auction> searchNonUserAuctions(int userId, boolean open, String searchQuery) throws SQLException {
		return listAuctions(userId, open, false, searchQuery);
	}
	
	public Auction getAuction(int auctionId) throws SQLException {
		Auction auction = null;
		String query = "SELECT A.*, U.id, U.username, U.name, U.surname, U.email, P.id, P.code, P.name, P.description, P.image FROM auctions as A JOIN users as U ON A.ownerid = U.id JOIN products as P ON P.id = A.productid WHERE A.id = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, auctionId);
			result = pstatement.executeQuery();
			if (result.next()) {
				auction = buildAuctionObj(result);			
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
		return auction;
	}
	
	private Auction buildAuctionObj(ResultSet result) throws NumberFormatException, SQLException {
		return buildAuctionObj(result, Integer.parseInt(result.getString("U.id")));
	}
	
	private Auction buildAuctionObj(ResultSet result, int userId) throws SQLException {
		Auction auction = new Auction();
		auction.setId(result.getInt("id"));
		auction.setOwner(new User(userId, result.getString("U.username"), result.getString("U.name"), result.getString("U.surname"), result.getString("U.email")));
		auction.setProduct(new Product(result.getInt("P.id"), result.getString("P.code"), result.getString("P.name"), result.getString("P.description"), Base64.getEncoder().encodeToString(result.getBytes("image"))));
		auction.setTitle(result.getString("title"));
		auction.setActive(result.getBoolean("active"));
		auction.setMinPrice(result.getBigDecimal("minprice"));
		auction.setPriceStep(result.getInt("minstep"));
		auction.setStartTime(result.getTimestamp("starttime").toInstant());
		auction.setEndTime(result.getTimestamp("endtime").toInstant());
		//Now get all bids
		OfferDAO dao1 = new OfferDAO(con);
		List<Offer> offers;
		offers = dao1.listAuctionOffers(result.getInt("id"));
		auction.setOffers(offers);			
		return auction;
	}
	
	public int createAuction(int userId, int productId, String title, float price, int pricestep, String date) throws SQLException, ParseException {
		String query = "INSERT into auctions (ownerid, productid, title, active, minprice, minstep, endtime, starttime) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstatement = null;
		int code = 0;		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, userId);
			pstatement.setInt(2, productId);
			pstatement.setString(3, title);
			pstatement.setBoolean(4, true);
			pstatement.setFloat(5, price);
			pstatement.setInt(6, pricestep);
			Instant dateInstant = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(date).toInstant();
			pstatement.setTimestamp(7, Timestamp.from(dateInstant));
			pstatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
			code = pstatement.executeUpdate();
		} catch (SQLException e) {
		    e.printStackTrace();
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e1) {}
		}
		return code;
	}
	
	public int closeAuction(int auctionId){
		String query = "UPDATE auctions SET active = 0, endtime = ? WHERE id = ?";
		PreparedStatement pstatement = null;
		int code = 0;		
		try {
			pstatement = con.prepareStatement(query);
			Instant time = Instant.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.from(ZoneOffset.UTC));
			String dbTimestamp = formatter.format(time);
			pstatement.setString(1, dbTimestamp);
			pstatement.setInt(2, auctionId);
			code = pstatement.executeUpdate();
		} catch (SQLException e) {
		    e.printStackTrace();
		} finally {
			try {
				pstatement.close();
			} catch (Exception e1) {}
		}
		return code;
	}

}