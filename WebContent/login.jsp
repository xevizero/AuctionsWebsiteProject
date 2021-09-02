<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" type="text/css" media="all" href="css/style.css" />
<title>Auctions - Home</title>
</head>
<body>
	<c:choose>
		<c:when test="${empty currentUser}">
		<div class="content middle">
			<p class="title big">Benvenuto su AsteOnline</p>
			<p class="subtitle centered">Log in</p>
			<c:url value="/login" var="loginUrl"/>			
			<form method="post" action="${loginUrl}">
					<div class="form">
    				<label for="username"><b>Username</b></label>
    				<input type="text" placeholder="Enter Username" name="username" required/>
    				<label for="password"><b>Password</b></label>
    				<input type="password" placeholder="Enter Password" name="password" required/>
    				<button class="submit" name="website_choice" value="pureHTML" type="submit">Login al sito classico</button>
    				<button class="submit" name="website_choice" value="javascript" type="submit">Login al sito RIA</button>
    				</div>
  			</form>	
  		</div>		
		</c:when>
		<c:otherwise>
			<jsp:include page="header.jsp" />
			<div class="content middle">
				<div class="home-div">
					<a class="big-link" href="<c:url value="sell"/>">Vendo</a>		
					<a class="big-link" href="<c:url value="buy"/>">Acquisto</a>
				</div>
			</div>
		</c:otherwise>
	</c:choose>
</body>
</html>