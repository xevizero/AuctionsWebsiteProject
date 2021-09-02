package it.polimi.tiw.auctions.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import it.polimi.tiw.auctions.beans.User;
import it.polimi.tiw.auctions.dao.AuctionDAO;
import it.polimi.tiw.auctions.dao.ProductDAO;


@WebServlet("/createAuction")
@MultipartConfig
public class CreateAuction extends HttpServlet{
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
		
		String title = request.getParameter("title");
		String name = request.getParameter("name");
		String desc = request.getParameter("desc");
		float price = Float.parseFloat(request.getParameter("price"));
		int pricestep = Integer.parseInt(request.getParameter("step"));
		String date = request.getParameter("enddate");
		Part imagePart = request.getPart("image");
		
		InputStream imageStream = null;
		String mimeType = null;
		if (imagePart != null) {
			imageStream = imagePart.getInputStream();
			String filename = imagePart.getSubmittedFileName();
			mimeType = getServletContext().getMimeType(filename);			
		}
		
		if (title == null || title.equals("") ||
			name == null || name.equals("") ||
			desc == null || desc.equals("") ||
			price <= 0 ||
			pricestep < 1  ||
			date == null || date.equals("") ||
			imageStream == null || (imageStream.available()==0) || 
			!mimeType.startsWith("image/"))
		{
			response.sendError(505, "Parameters incomplete");
			return;
		}
		
		ProductDAO dao1 = new ProductDAO(connection);
		
		String error = null;
		int productId = 0;
		try {
			productId = dao1.createProduct(name, desc, imageStream);
		} catch (SQLException e3) {
			error = "Bad database insertion input";
		}
		if (error != null) {
			response.sendError(505, error);
		} else {
			AuctionDAO dao2 = new AuctionDAO(connection);
			int userId = ((User) session.getAttribute("currentUser")).getId();
			try {
				productId = dao2.createAuction(userId, productId, title, price, pricestep, date);
			} catch (SQLException e3) {
				error = "Bad database insertion input";
			} catch (ParseException e4) {
				// TODO Auto-generated catch block
				e4.printStackTrace();
			}
			if (error != null) {
				response.sendError(505, error);
			} else {
				String path = getServletContext().getContextPath() + "/sell";
				response.sendRedirect(path);
			}
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
