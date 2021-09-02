package it.polimi.tiw.auctions.controllers.live;

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

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.auctions.beans.Auction;
import it.polimi.tiw.auctions.beans.User;
import it.polimi.tiw.auctions.dao.AuctionDAO;

@MultipartConfig
@WebServlet("/searchAuctions")
public class SearchAuctions extends HttpServlet{
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
			int userId = ((User) session.getAttribute("currentUser")).getId();
			String searchQuery = request.getParameter("searchQuery");
			AuctionDAO dao1 = new AuctionDAO(connection);
			String jsonResponse = "";
			Gson gson = Converters.registerInstant(new GsonBuilder()).create();
			if(searchQuery != null && searchQuery.length() > 0){
				List<Auction> openAuctions;
				try {
					openAuctions = dao1.searchNonUserAuctions(userId, true, searchQuery);
					jsonResponse = gson.toJson(openAuctions);
				} catch (SQLException e) {
					e.printStackTrace();
					response.sendError(500, "Database access failed");
				}
			}else {
				jsonResponse = gson.toJson("");
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
