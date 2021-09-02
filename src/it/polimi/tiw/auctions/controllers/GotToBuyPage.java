package it.polimi.tiw.auctions.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
import it.polimi.tiw.auctions.dao.OfferDAO;

@WebServlet("/buy")
public class GotToBuyPage extends HttpServlet{
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
			String searchQuery = request.getParameter("searchQuery");
			String path = "/buy.jsp";
			int userId = ((User) session.getAttribute("currentUser")).getId();
			Instant now = Instant.now();  
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm").withLocale(Locale.ITALY).withZone(ZoneId.of("UTC"));
		    String todaydate = formatter.format(now);
			AuctionDAO dao1 = new AuctionDAO(connection);
			OfferDAO dao2 = new OfferDAO(connection);
			request.setAttribute("todaydate", todaydate);
			if(searchQuery != null && searchQuery.length() > 0){
				List<Auction> openAuctions;
				try {
					openAuctions = dao1.searchNonUserAuctions(userId, true, searchQuery);
					request.setAttribute("openAuctions", openAuctions);
					request.setAttribute("lastSearchQuery", searchQuery);
				} catch (SQLException e) {
				}
			}else {
				request.setAttribute("openAuctions", null);
				request.setAttribute("lastSearchQuery", null);
			}
			try {
				List<Offer> wonOffers = dao2.listUserOffers(userId, true);
				List<Auction> wonAuctions = new ArrayList<Auction>();
				for (Offer offer : wonOffers) {
					wonAuctions.add(dao1.getAuction(offer.getAuctionid()));
				}
				request.setAttribute("wonAuctions", wonAuctions);

			}catch (Exception e) {
				response.sendError(500, "Database access failed");
			}
			
			RequestDispatcher dispatcher = request.getRequestDispatcher(path);
			dispatcher.forward(request, response);
		    			
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
