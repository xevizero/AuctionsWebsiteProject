package it.polimi.tiw.auctions.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.auctions.beans.Offer;
import it.polimi.tiw.auctions.dao.AuctionDAO;
import it.polimi.tiw.auctions.dao.OfferDAO;


@WebServlet("/closeAuction")
@MultipartConfig
public class CloseAuction extends HttpServlet{
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
		
		if (auctionId == 0)
		{
			response.sendError(505, "Parameters incomplete");
			return;
		}
		
		AuctionDAO dao1 = new AuctionDAO(connection);
		OfferDAO dao2 = new OfferDAO(connection);
		try {
			List<Offer> offers = dao2.listAuctionOffers(auctionId);
			if(offers.size()>0) {
				Offer winningOffer = offers.get(0);
				dao2.setWinningOffer(winningOffer.getBidderid(), auctionId, winningOffer.getTime());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			dao1.closeAuction(auctionId);
			String path = getServletContext().getContextPath() + "/auction?auctionId=" + auctionId;
			response.sendRedirect(path);
		}
		
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
