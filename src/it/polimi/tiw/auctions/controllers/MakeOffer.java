package it.polimi.tiw.auctions.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.auctions.beans.Auction;
import it.polimi.tiw.auctions.beans.User;
import it.polimi.tiw.auctions.dao.AuctionDAO;
import it.polimi.tiw.auctions.dao.OfferDAO;


@WebServlet("/makeOffer")
@MultipartConfig
public class MakeOffer extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private Connection connection;

	
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			String path = getServletContext().getContextPath();
			response.sendRedirect(path);
		}
		
		int auctionId = Integer.parseInt(request.getParameter("auctionId"));
		BigDecimal price = new BigDecimal(request.getParameter("price"));
		int userId = ((User) session.getAttribute("currentUser")).getId();
		BigDecimal minOffer = null;
		
		AuctionDAO dao1 = new AuctionDAO(connection);
		try {
			Auction auction = dao1.getAuction(auctionId);
			minOffer = (auction.getOffers().size()>0)?(auction.getOffers().get(0).getPrice().add(new BigDecimal(auction.getPriceStep()))):auction.getMinPrice();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.sendError(505, "Errore nella richiesta.");
			return;
		}
		
		if (price.compareTo(minOffer) < 0)
		{
			response.sendError(505, "Offerta troppo bassa.");
			return;
		}
		
		OfferDAO dao2 = new OfferDAO(connection);
		dao2.insertOffer(userId, auctionId, price);
		String path = getServletContext().getContextPath() + "/auction?auctionId=" + auctionId;
		response.sendRedirect(path);
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}	
}
