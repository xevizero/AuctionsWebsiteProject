package it.polimi.tiw.auctions.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.auctions.beans.Offer;

public class OfferDAO {
	private Connection con;

	public OfferDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<Offer> listUserOffers(int userId, boolean onlyWinning) throws SQLException {
		List<Offer> offers = new ArrayList<Offer>();
		String query = "SELECT O.*, U.username FROM offers as O JOIN users as U on O.bidderid = U.id WHERE U.id = ?" + ((onlyWinning)?" AND O.winner = ?":"") + " ORDER BY posttime DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, userId);
			if(onlyWinning)
				pstatement.setBoolean(2, onlyWinning);
			result = pstatement.executeQuery();
			while (result.next()) {
				Offer offer = new Offer(userId, result.getInt("O.auctionid"), result.getBoolean("O.winner"), result.getTimestamp("O.posttime").toInstant(), result.getBigDecimal("O.price"), result.getString("U.username"));		
				offers.add(offer);
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
		return offers;
	}
	
	public List<Offer> listAuctionOffers(int auctionId) throws SQLException {
		List<Offer> offers = new ArrayList<Offer>();
		String query = "SELECT *, username FROM offers JOIN users on bidderid = id WHERE auctionid = ? ORDER BY price DESC, posttime DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, auctionId);
			result = pstatement.executeQuery();
			while (result.next()) {
				Offer offer = new Offer(result.getInt("bidderid"), auctionId, result.getBoolean("winner"), result.getTimestamp("posttime").toInstant(), result.getBigDecimal("price"), result.getString("username"));		
				offers.add(offer);
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
		return offers;
	}
	
	public int setWinningOffer(int bidderId, int auctionId, Instant time) {
		String query = "UPDATE `offers` SET `winner` = '1' WHERE (`bidderid` = ?) AND (`auctionid` = ?) AND (`posttime` = ?)";
		PreparedStatement pstatement = null;
		int code = 0;		
		try {			
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, bidderId);
			pstatement.setInt(2, auctionId);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.from(ZoneOffset.UTC));
			String dbTimestamp = formatter.format(time);
			pstatement.setString(3, dbTimestamp);
			System.out.println(pstatement.toString());
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
	
	public int insertOffer(int bidderId, int auctionId, BigDecimal price) {
		String query = "INSERT INTO offers (bidderid, auctionid, winner, posttime, price) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement pstatement = null;
		int code = 0;		
		try {			
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, bidderId);
			pstatement.setInt(2, auctionId);
			pstatement.setBoolean(3, false);
			Instant time = Instant.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.from(ZoneOffset.UTC));
			String dbTimestamp = formatter.format(time);
			pstatement.setString(4, dbTimestamp);
			pstatement.setBigDecimal(5, price);
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
