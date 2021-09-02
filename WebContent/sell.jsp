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
<title>Vendo</title>
<c:set var="context" value="${pageContext.request.contextPath}" />
</head>
<body>
	<jsp:include page="header.jsp" />
	<div class="top">
		<p class="title">Vendo</p>	
	</div>
	<div class="content sell-main-div open">
	<p class="subtitle">Le tue aste in corso:</p>
	<hr>
	<p></p>
	<c:choose>
	<c:when test="${openAuctions.size()>0}">
		<c:forEach var="auction" items="${openAuctions}">
		<a class="linkdiv" href="${context}/auction?auctionId=<c:url value="${auction.id}"/>">
			<div class="small-auction-div">
				<p class="text"><b>Asta: </b><c:out value="${auction.title}"/></p>
 				<img class="product-image" src="data:image/jpeg;base64,${auction.product.image}"/>						
				<p class="text"><b>Prodotto: </b><c:out value="${auction.product.name}"/> (<b>Codice: </b><c:out value="${auction.product.code}"/>)</p>			
				<p class="text"><b>Offerta massima corrente: </b><c:out value="${auction.offers[0].price}"/>€</p>
				<fmt:parseDate value="${auction.startTime}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="startTime" type="date"/>
 				<p class="text">Asta creata in data <fmt:formatDate pattern="dd/MMM/yyyy" value="${startTime}"/> alle ore <fmt:formatDate pattern="HH:mm" value="${startTime}"/></p> 
				<p class="text">Tempo rimanente: <c:out value="${auction.daysUntilEnd}"/> giorni, <c:out value="${auction.hoursUntilEnd}"/> ore, <c:out value="${auction.minutesUntilEnd}"/> minuti.</p>
			</div>	
 		</a>		
		</c:forEach>
	</c:when>
	<c:otherwise>
		<p class="text">Non hai aste in corso.</p>
	</c:otherwise>
	</c:choose>
	</div>
	<div class="content sell-main-div closed">
	<p class="subtitle">Le tue aste concluse:</p>
	<hr>
	<p></p>	
	<c:choose>
	<c:when test="${closedAuctions.size()>0}">
		<c:forEach var="auction" items="${closedAuctions}">
		<a class="linkdiv" href="${context}/auction?auctionId=<c:url value="${auction.id}"/>">
			<div class="small-auction-div">
				<p class="text"><b>Asta: </b><c:out value="${auction.title}"/></p>
				<img class="product-image" src="data:image/jpeg;base64,${auction.product.image}"/>
				<p class="text"><b>Prodotto: </b><c:out value="${auction.product.name}"/> (Codice: <c:out value="${auction.product.code}"/>)</p>		
				<p class="text"><b>Offerta massima: </b><c:out value="${auction.offers[0].price}"/>€</p>
	 			<fmt:parseDate value="${auction.startTime}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="startTime" type="date"/>
	 			<p class="text">Asta creata in data <fmt:formatDate pattern="dd/MMM/yyyy" value="${startTime}"/> alle ore <fmt:formatDate pattern="HH:mm" value="${startTime}"/></p> 
	 			<fmt:parseDate value="${auction.endTime}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="endTime" type="date"/>
	 			<p class="text">Asta conclusa in data <fmt:formatDate pattern="dd/MMM/yyyy" value="${endTime}"/> alle ore <fmt:formatDate pattern="HH:mm" value="${endTime}"/></p> 	
 			</div>	
 		</a>				
		</c:forEach>
	</c:when>
	<c:otherwise>
		<p class="text">Non hai mai concluso un'asta.</p>
	</c:otherwise>
	</c:choose>
	</div>
	<div class="content sell-main-div create">
	<p class="subtitle">Crea nuova asta</p>
	<c:url value="/createAuction" var="createUrl" />			
	<form method="post" action="${createUrl}" enctype="multipart/form-data" id="create-auction-id">
	<div class="form big">
		<label for="title"><b>Titolo asta</b></label>
		<input type="text" name="title" required/><br>
		<label for="name"><b>Nome del prodotto</b></label>
		<input type="text" name="name" required/><br>
		<label for="desc"><b>Descrizione del prodotto</b></label><br>
		<textarea form="create-auction-id" class="textarea" placeholder="Descrivi il tuo prodotto" name="desc" required></textarea><br>
		<label for="image"><b>Carica un'immagine del tuo prodotto</b></label>		
		<input type =file name="image"/><br>
		<label for="price"><b>Prezzo di partenza (€)</b></label>
		<input type="number" name="price"  step="0.01" min="0" required/><br>
		<label for="step"><b>Rialzo minimo</b></label>
		<input type="number" name="step" step="1" required/><br>
		<label for="enddate"><b>Termine asta</b></label>
		<input type="datetime-local" name="enddate" value="${todaydate}" min="${todaydate}" max="${maxdate}" required/><br>
		<button class="submit" type="submit">Inserisci inserzione</button>		
	</div>
	</form>	
	</div>
</body>
</html>