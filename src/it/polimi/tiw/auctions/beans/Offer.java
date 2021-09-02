package it.polimi.tiw.auctions.beans;

import java.math.BigDecimal;
import java.time.Instant;

public class Offer {
	private int bidderid;
	private String bidderUsername;
	private int auctionid;
	private boolean winner;
	private Instant time;
	private BigDecimal price;
	
	public Offer() {
		
	}
	
	public Offer(int bidderid, int auctionid, boolean winner, Instant time, BigDecimal price, String bidderUsername) {
		this.bidderid = bidderid;
		this.auctionid = auctionid;
		this.winner = winner;
		this.time = time;
		this.price = price;
		this.bidderUsername = bidderUsername;
	}
	public int getBidderid() {
		return bidderid;
	}
	public void setBidderid(int bidderid) {
		this.bidderid = bidderid;
	}
	public int getAuctionid() {
		return auctionid;
	}
	public void setAuctionid(int auctionid) {
		this.auctionid = auctionid;
	}
	public boolean isWinner() {
		return winner;
	}
	public void setWinner(boolean winner) {
		this.winner = winner;
	}
	public Instant getTime() {
		return time;
	}
	public void setTime(Instant time) {
		this.time = time;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getBidderUsername() {
		return bidderUsername;
	}

	public void setBidderUsername(String bidderUsername) {
		this.bidderUsername = bidderUsername;
	}
}
