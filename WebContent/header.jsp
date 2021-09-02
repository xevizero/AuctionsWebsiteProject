<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<div class="topnav">
	<p class="topnav-container"><a class="home-button home" href="<c:url value="home"/>"><b>Home</b></a></p>
	<p class="topnav-container"><a class="home-button" href="<c:url value="buy"/>">Acquisto</a></p>
	<p class="topnav-container"><a class="home-button" href="<c:url value="sell"/>">Vendo</a></p>
	<p class="topnav-container username">Utente: <c:out value="${currentUser.username}"></c:out></p>
	<p class="topnav-container"><a class="logout" href="<c:url value="logout"/>">Log out</a></p>
</div>


