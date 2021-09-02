package it.polimi.tiw.auctions.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.auctions.beans.User;
import it.polimi.tiw.auctions.dao.UserDAO;

@WebServlet("/userinfo")
public class GetUser extends HttpServlet{
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
			UserDAO dao = new UserDAO(connection);
			int userId = ((User) session.getAttribute("currentUser")).getId();
			try {
				User userInfo = dao.getUserInfo(userId);
				String path = "/sell.jsp";
				request.setAttribute("userInfo", userInfo);
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
