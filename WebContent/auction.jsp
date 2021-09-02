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
<link rel="stylesheet" type="text/css" media="all" href="css/style.css" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<c:choose>
	<c:when test="${userInfo.id == auction.owner.id}">
		<title>Dettaglio Asta</title>
	</c:when>
	<c:otherwise>
		<title>Offerta</title>
	</c:otherwise>
</c:choose>

<c:set var="context" value="${pageContext.request.contextPath}" />
</head>
<body>
	<jsp:include page="header.jsp" />
	<div class="content top">
	<c:choose>
		<c:when test="${userInfo.id == auction.owner.id}">
			<p class="title">Dettaglio Asta</p>
		</c:when>
		<c:otherwise>
			<p class="title">Offerta</p>
		</c:otherwise>
	</c:choose>
	</div>
	<div class="content big-auction-div image">
			<img class="product-image" src="data:image/jpeg;base64,${auction.product.image}"/>
	</div>
	<div class="content big-auction-div data">
		<p class="subtitle"><b>Asta: </b><c:out value="${auction.title}"/></p>
		<p class="subtitle"><b>Prezzo di partenza: </b><c:out value="${auction.minPrice}"/>€ (<b>Rialzo minimo: </b><c:out value="${auction.priceStep}"/>€)</p>
		<p class="text"><b>Prodotto: </b><c:out value="${auction.product.name}"/> (Codice: <c:out value="${auction.product.code}"/>)</p>
		<p class="text"><b>Descrizione:</b></p>
		<p class="text"><c:out value="${auction.product.desc}"/></p>
		<fmt:parseDate value="${auction.startTime}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="startTime" type="date"/>
		<p class="text">Asta creata in data <fmt:formatDate pattern="dd/MMM/yyyy" value="${startTime}"/> alle ore <fmt:formatDate pattern="HH:mm" value="${startTime}"/></p> 
		<fmt:parseDate value="${auction.endTime}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="endTime" type="date"/>
		<c:choose>
		<c:when test="${auction.active == true}">
			<p class="text">Termine ultimo in data <fmt:formatDate pattern="dd/MMM/yyyy" value="${endTime}"/> alle ore <fmt:formatDate pattern="HH:mm" value="${endTime}"/></p> 
			<p class="text">Tempo rimanente: <c:out value="${auction.daysUntilEnd}"/> giorni, <c:out value="${auction.hoursUntilEnd}"/> ore, <c:out value="${auction.minutesUntilEnd}"/> minuti.</p>
		</c:when>
		<c:otherwise>
	 		<p class="text">Asta conclusa in data <fmt:formatDate pattern="dd/MMM/yyyy" value="${endTime}"/> alle ore <fmt:formatDate pattern="HH:mm" value="${endTime}"/></p> 
		</c:otherwise>
		</c:choose>
	</div>
	<div class="content big-auction-div right">
		<div class="offers-options">	
			<c:choose>
			<c:when test="${auction.active == true}">
			<c:choose>
				<c:when test="${userInfo.id == auction.owner.id}">
					<c:url value="/closeAuction" var="closeUrl" />			
					<form method="post" action="${closeUrl}" enctype="multipart/form-data">
				  	<input type="hidden" id="auctionId" name="auctionId" value="${auction.id}">
					<button class="submit" type="submit">Chiudi inserzione</button>
					</form>	
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${userInfo.id != auction.owner.id}">
							<c:set var="newMinPrice" value="${((winnerOffer != null)?(winnerOffer.price + auction.priceStep):(auction.minPrice))}" />
							<p class="subtitle">Fai un'offerta</p>
							<c:url value="/makeOffer" var="makeOfferUrl" />			
							<form method="post" action="${makeOfferUrl}" enctype="multipart/form-data">
							<div class="form small">
							  	<input type="hidden" id="auctionId" name="auctionId" value="${auction.id}">
							  	<label for="price"><b>Prezzo:</b> (Offerta minima: <c:out value="${newMinPrice}"/>€)</label>
							  	<input type="number" id="price" name="price" min="${newMinPrice}" step="0.01" required/>
								<button class="submit" type="submit">Invia offerta</button>
							</div>
							</form>	
						</c:when>
						<c:otherwise>
							<p></p>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
			</c:when>
			<c:otherwise>
				<p class="subtitle">Asta conclusa.</p>
				<c:choose>
					<c:when test="${winnerInfo == null || winnerOffer == null}">
						<p class="text">Asta chiusa senza offerte.</p>
					</c:when>
					<c:otherwise>
						<p class="text">Aggiudicatario: <c:out value="${winnerInfo.username}"/></p>
						<p class="text">Prezzo: <c:out value="${winnerOffer.price}"/></p>
						<p class="text">Indirizzo: Via Celio Vibenna, 00184 Roma (RM)</p>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
			</c:choose>
		</div>
		<p></p>
		<hr>
		<div class="offers-list">
		 	<p class="subtitle">Offerte:</p>
	 		<div class="offers-list">
			<c:choose>
			<c:when test="${auction.offers.size()>0}">
				<c:forEach var="offer" items="${auction.offers}">
					<p class="text"><b>Offerente: </b><c:out value="${offer.bidderUsername}"/></p>
					<p class="text"><b>Offerta: </b><c:out value="${offer.price}"/></p>
					<fmt:parseDate value="${offer.time}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="time" type="date"/>
					<p class="text"><b>Data: </b><fmt:formatDate pattern="dd/MMM/yyyy HH:mm" value="${time}"/></p> 
		 			<hr>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<div class="centered-div">
					<p class="text">Nessuna offerta.</p>
				</div>
			</c:otherwise>
			</c:choose>
			</div>
		</div>
	</div>
</body>
</html>