package it.polimi.tiw.auctions.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.auctions.beans.Auction;
import it.polimi.tiw.auctions.beans.Offer;
import it.polimi.tiw.auctions.beans.User;
import it.polimi.tiw.auctions.dao.AuctionDAO;
import it.polimi.tiw.auctions.dao.UserDAO;

@WebServlet("/auction")
public class GoToAuctionPage extends HttpServlet {
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
		int auctionId = Integer.parseInt(request.getParameter("auctionId"));
		if (session == null || session.getAttribute("currentUser") == null) {
			String path = getServletContext().getContextPath();
			response.sendRedirect(path);
		}
		else {
		    Instant now = Instant.now();  
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm").withLocale(Locale.ITALY).withZone(ZoneId.of("UTC"));
		    String todaydate = formatter.format(now);
			AuctionDAO dao1 = new AuctionDAO(connection);
			UserDAO dao2 = new UserDAO(connection);
			Auction auction;
			int userId = ((User) session.getAttribute("currentUser")).getId();
			try {
				auction = dao1.getAuction(auctionId);
				auction.setDaysUntilEnd(ChronoUnit.DAYS.between(now, auction.getEndTime()));
				auction.setHoursUntilEnd(ChronoUnit.HOURS.between(now, auction.getEndTime()) % 24);
				auction.setMinutesUntilEnd(ChronoUnit.MINUTES.between(now, auction.getEndTime()) % 60);
				User userInfo = dao2.getUserInfo(userId);
				String path = "/auction.jsp";
				request.setAttribute("auction", auction);
				request.setAttribute("userInfo", userInfo);
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
							request.setAttribute("winnerInfo", null);
							request.setAttribute("winnerOffer", null);
						}else {
							User winnerInfo = dao2.getUserInfo(winnerId);
							request.setAttribute("winnerInfo", winnerInfo);
							request.setAttribute("winnerOffer", winningOffer);
						}
					}else {
						request.setAttribute("winnerInfo", null);
						request.setAttribute("winnerOffer", null);
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
							request.setAttribute("winnerInfo", null);
							request.setAttribute("winnerOffer", null);
						}else{
							User currentWinnerInfo = dao2.getUserInfo(currentWinnerId);
							request.setAttribute("winnerInfo", currentWinnerInfo);
							request.setAttribute("winnerOffer", currentWinningOffer);
						}
					}else {
						request.setAttribute("winnerInfo", null);
						request.setAttribute("winnerOffer", null);
					}
				}
				request.setAttribute("todaydate", todaydate);
				RequestDispatcher dispatcher = request.getRequestDispatcher(path);
				dispatcher.forward(request, response);
			} catch (SQLException e) {
				response.sendError(500, "Database access failed");
			}			
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
