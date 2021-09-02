package it.polimi.tiw.auctions.beans;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class Auction {
	private int id;
	private User owner;
	private Product product;
	private String title;
	private boolean active;
	private BigDecimal minPrice;
	private int priceStep;
	private Instant endTime;
	private Instant startTime;
	private List<Offer> offers;
	private long daysUntilEnd, hoursUntilEnd, minutesUntilEnd;

	public Auction() {
	}
	public Auction(int id, User owner, Product product, String title, boolean active, BigDecimal minPrice, int priceStep,
			Instant endTime, Instant startTime, List<Offer> offers) {
		this.id = id;
		this.owner = owner;
		this.product = product;
		this.title = title;
		this.active = active;
		this.minPrice = minPrice;
		this.priceStep = priceStep;
		this.endTime = endTime;
		this.startTime = startTime;
		this.offers = offers;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public BigDecimal getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(BigDecimal minPrice) {
		this.minPrice = minPrice;
	}
	public int getPriceStep() {
		return priceStep;
	}
	public void setPriceStep(int priceStep) {
		this.priceStep = priceStep;
	}
	public Instant getEndTime() {
		return endTime;
	}
	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
	}
	public Instant getStartTime() {
		return startTime;
	}
	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Offer> getOffers() {
		return offers;
	}
	public void setOffers(List<Offer> offers) {
		this.offers = offers;
	}
	public long getDaysUntilEnd() {
		return daysUntilEnd;
	}
	public void setDaysUntilEnd(long daysUntilEnd) {
		this.daysUntilEnd = daysUntilEnd;
	}
	public long getHoursUntilEnd() {
		return hoursUntilEnd;
	}
	public void setHoursUntilEnd(long hoursUntilEnd) {
		this.hoursUntilEnd = hoursUntilEnd;
	}
	public long getMinutesUntilEnd() {
		return minutesUntilEnd;
	}
	public void setMinutesUntilEnd(long minutesUntilEnd) {
		this.minutesUntilEnd = minutesUntilEnd;
	}
	
}
