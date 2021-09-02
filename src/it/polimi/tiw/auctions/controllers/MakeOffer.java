package it.polimi.tiw.auctions.controllers;

import java.io.IOException;
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

import it.polimi.tiw.auctions.beans.User;
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
		float price = Float.parseFloat(request.getParameter("price"));
		int userId = ((User) session.getAttribute("currentUser")).getId();

		
		if (price <= 0 ||
			auctionId <= 0)
		{
			response.sendError(505, "Parameters incomplete");
			return;
		}
		
		OfferDAO dao1 = new OfferDAO(connection);
		dao1.insertOffer(userId, auctionId, price);
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
