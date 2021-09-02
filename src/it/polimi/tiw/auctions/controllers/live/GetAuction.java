package it.polimi.tiw.auctions.controllers.live;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.auctions.beans.Auction;
import it.polimi.tiw.auctions.beans.Offer;
import it.polimi.tiw.auctions.beans.User;
import it.polimi.tiw.auctions.dao.AuctionDAO;
import it.polimi.tiw.auctions.dao.UserDAO;

@WebServlet("/getAuction")
public class GetAuction extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
		    e.printStackTrace();			
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
		    e.printStackTrace();			
			throw new UnavailableException("Couldn't get db connection");
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("currentUser") == null) {
			String path = getServletContext().getContextPath();
			response.sendRedirect(path);
		}
		else {
		    Instant now = Instant.now();  
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm").withLocale(Locale.ITALY).withZone(ZoneId.of("UTC"));
		    String todaydate = formatter.format(now);
			int userId = ((User) session.getAttribute("currentUser")).getId();
			String auctionIdsString = request.getParameter("auctionId");
			List<Integer> auctionIds = new ArrayList<Integer>();
			boolean multiple = false;
			if(auctionIdsString.contains("A")) {
				multiple = true;
				for(String idString : auctionIdsString.split("A")) {
					if(idString.length()>0) {
						auctionIds.add(Integer.parseInt(idString));
					}
				}
			}else {
				if(auctionIdsString.length()>0) {
					auctionIds.add(Integer.parseInt(auctionIdsString));
				}
			}
			AuctionDAO dao1 = new AuctionDAO(connection);
			UserDAO dao2 = new UserDAO(connection);
			Auction auction;
			HashMap<String, String> resultsHashMap = new HashMap<String, String>();
			String jsonResponse = "";
			Gson gson = Converters.registerInstant(new GsonBuilder()).create();
			if(multiple) {
				try {
					List<Auction> vAuctions = new ArrayList<Auction>();
					for (int id : auctionIds) {
						vAuctions.add(dao1.getAuction(id));
					}
				    jsonResponse = gson.toJson(vAuctions);
				}catch (Exception e) {
					e.printStackTrace();
					response.sendError(500, "Database access failed");
				}
			}else if(auctionIds.size() == 1 && !multiple){
				try {
					auction = dao1.getAuction(auctionIds.get(0));
					auction.setDaysUntilEnd(ChronoUnit.DAYS.between(now, auction.getEndTime()));
					auction.setHoursUntilEnd(ChronoUnit.HOURS.between(now, auction.getEndTime()) % 24);
					auction.setMinutesUntilEnd(ChronoUnit.MINUTES.between(now, auction.getEndTime()) % 60);
					User userInfo = dao2.getUserInfo(userId);
					resultsHashMap.put("auction", gson.toJson(auction));
					resultsHashMap.put("userInfo", gson.toJson(userInfo));
					if(auction.isActive() == false){
						if(auction.getOffers().size() > 0){
							int winnerId = 0;
							Offer winningOffer = null;
							for (int i = 0; i < auction.getOffers().size(); i++) {
								Offer offer = auction.getOffers().get(i);
								if (offer.isWinner()) {
									winningOffer = offer;
									winnerId = offer.getBidderid();
									break;
								}
							}
							if(winningOffer == null) {
								resultsHashMap.put("winnerInfo", "");
								resultsHashMap.put("winnerOffer", "");
							}else {
								User winnerInfo = dao2.getUserInfo(winnerId);
								resultsHashMap.put("winnerInfo", gson.toJson(winnerInfo));
								resultsHashMap.put("winnerOffer", gson.toJson(winningOffer));
							}
						}else {
							resultsHashMap.put("winnerInfo", "");
							resultsHashMap.put("winnerOffer", "");
						}
					}else {
						if(auction.getOffers().size() > 0){
							int currentWinnerId = 0;
							Offer currentWinningOffer = null;
							for (int i = 0; i < auction.getOffers().size(); i++) {
								Offer offer = auction.getOffers().get(i);
								if (currentWinningOffer == null || offer.getPrice().compareTo(currentWinningOffer.getPrice()) > 0) {
									currentWinningOffer = offer;
									currentWinnerId = offer.getBidderid();
								}
							}
							if(currentWinningOffer == null) {
								resultsHashMap.put("winnerInfo", "");
								resultsHashMap.put("winnerOffer", "");
							}else{
								User currentWinnerInfo = dao2.getUserInfo(currentWinnerId);
								resultsHashMap.put("winnerInfo", gson.toJson(currentWinnerInfo));
								resultsHashMap.put("winnerOffer", gson.toJson(currentWinningOffer));
							}
						}else {
							resultsHashMap.put("winnerInfo", "");
							resultsHashMap.put("winnerOffer", "");
						}
					}
					resultsHashMap.put("todaydate", todaydate);
					jsonResponse = gson.toJson(resultsHashMap);
				} catch (SQLException e) {
					response.sendError(500, "Database access failed");
				}		
			}else {
				List<Auction> vAuctions = new ArrayList<Auction>();
				jsonResponse = gson.toJson(vAuctions);
			}
	
			 response.setContentType("application/json");
			 response.setCharacterEncoding("UTF-8");
			 response.getWriter().write(jsonResponse);	
		}
	}

	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {}
	}
}
