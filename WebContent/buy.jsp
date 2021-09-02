<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="it.polimi.tiw.auctions.beans.Auction" %>
<%@page import="it.polimi.tiw.auctions.beans.Product" %>
<%@page import="it.polimi.tiw.auctions.beans.User" %>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" type="text/css" media="all" href="css/style.css" />
<title>Acquisto</title>
<c:set var="context" value="${pageContext.request.contextPath}" />
</head>
<body>
	<jsp:include page="header.jsp" />
	<div class="content top">
		<p class="title">Acquisto</p>	
	</div>
	<div class="content buy-main-div">
	<p class="subtitle centered">Cerca un asta o prodotto:</p>
		<c:url value="/buy" var="searchUrl" />			
		<form method="get" action="${searchUrl}" enctype="multipart/form-data">
			<div class="form search">
			<input type="text" name="searchQuery" required/><br>	
			<button class="submit" type="submit">Cerca</button>
			</div>
		</form>	
		<c:choose>
		<c:when test="${openAuctions != null && openAuctions.size()>0}">
			<p class="subtitle">Risultati per: <c:out value="${lastSearchQuery}"/></p>
			<hr>
			<p></p>
			<c:forEach var="auction" items="${openAuctions}">
				<a class="linkdiv" href="${context}/auction?auctionId=<c:url value="${auction.id}"/>">			
					<div class="small-auction-div">
						<p class="text"><b>Asta: </b><c:out value="${auction.title}"/></p>
						<img class="product-image" src="data:image/jpeg;base64,${auction.product.image}"/>
						<p class="text"><b>Prodotto: </b><c:out value="${auction.product.name}"/> (Codice: <c:out value="${auction.product.code}"/>)</p>				
						<p class="text"><b>Offerta massima corrente: </b><c:out value="${auction.offers[0].price}"/>€</p>
			 			<fmt:parseDate value="${auction.startTime}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="startTime" type="date"/>
			 			<p class="text">Asta creata in data <fmt:formatDate pattern="dd/MMM/yyyy" value="${startTime}"/> alle ore <fmt:formatDate pattern="HH:mm" value="${startTime}"/></p> 
			 			<fmt:parseDate value="${auction.endTime}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="endTime" type="date"/>
			 			<p class="text">Asta termina in data <fmt:formatDate pattern="dd/MMM/yyyy" value="${endTime}"/> alle ore <fmt:formatDate pattern="HH:mm" value="${endTime}"/></p>	
		 			</div>	
	 			</a>	
			</c:forEach>
		</c:when>
		<c:otherwise>
		<c:choose>
		<c:when test="${openAuctions != null && openAuctions.size()==0}">
			<div class="centered-div">
				<p class="text">Nessun risultato.</p>
			</div>
		</c:when>
		<c:otherwise>
			<p></p>
		</c:otherwise>
		</c:choose>
		</c:otherwise>
		</c:choose>
	</div>
	<div class="content buy-main-div">
	<p class="subtitle">Le tue aste aggiudicate</p>
	<hr>
	<p></p>
	<c:choose>
	<c:when test="${wonAuctions != null && wonAuctions.size()>0}">
		<c:forEach var="auction" items="${wonAuctions}">
		<a class="linkdiv" href="${context}/auction?auctionId=<c:url value="${auction.id}"/>">
			<div class="small-auction-div">
				<p class="text"><b>Asta: </b><c:out value="${auction.title}"/></p>
				<img class="product-image" src="data:image/jpeg;base64,${auction.product.image}"/>
				<p class="text"><b>Prodotto: </b><c:out value="${auction.product.name}"/> (Codice: <c:out value="${auction.product.code}"/>)</p>		
				<p class="text"><b>Prezzo pagato: </b><c:out value="${auction.offers[0].price}"/>€</p>
	 			<fmt:parseDate value="${auction.endTime}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="endTime" type="date"/>
	 			<p class="text">Asta vinta in data <fmt:formatDate pattern="dd/MMM/yyyy" value="${endTime}"/> alle ore <fmt:formatDate pattern="HH:mm" value="${endTime}"/></p> 
 			</div>	
		</a>	
		</c:forEach>
	</c:when>
	<c:otherwise>
		<div class="centered-div">
			<p class="text">Non hai mai vinto un'asta.</p>
		</div>
	</c:otherwise>
	</c:choose>
	</div>
</body>
</html>