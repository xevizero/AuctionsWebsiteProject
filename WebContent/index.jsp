<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title></title>
<script src="scripts/utils.js" defer></script>
<script src="scripts/homePageController.js" defer></script>
<link rel="stylesheet" type="text/css" media="all" href="css/style.css" />
</head>
<body>
<noscript>
	<div class="content">
        <p class="title big">Javascript is disabled, or not supported.</p>
		<p class="subtitle centered"><a class="home-button" href="http://localhost:8080/Auctions%20Project/home">Go to the pure HTML website.</a></p>
	</div>
</noscript>
<jsp:include page="jsheader.jsp" />
<div id="content-div">

</div>
</body>
</html>