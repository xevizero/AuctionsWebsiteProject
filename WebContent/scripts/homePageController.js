(function() { 

  var loggedUsername = "", visAuctionsSave, content, mainDivs = [], mainController = new MainController(); 
	
  window.addEventListener("load", () => {
      mainController.start(); 
  }, false);

  function MainController() {
	var self = this;
	content = document.getElementById("content-div");
	var jsheadertext = document.getElementById("jsheader_user").innerText;
	if (jsheadertext.split(": ").length > 1){
		loggedUsername = jsheadertext.split(": ")[1];
	}
	var buybutton = document.getElementById("buybutton");
	var sellbutton = document.getElementById("sellbutton");
	
	buybutton.addEventListener("click", function(){
		self.enterBuyMode();
	});
	sellbutton.addEventListener("click", function(){
		self.enterSellMode();
	});
	this.start = function() {
		if(getCookie("firstVisit"+loggedUsername) == ""){
			setCookie("firstVisit"+loggedUsername, "true", 30);
			this.enterBuyMode();
			
		}else{
			if(getCookie("lastAction"+loggedUsername) == "createAuction"){
				this.enterSellMode();	
			}else{
				this.enterBuyMode();
			}
		}
	};
	
	this.enterBuyMode = function(){
		this.newPage();
		this.getVisitedAuctions();
	}
	
	this.enterAuctionMode = function(auctionId, nonUserAuction){
		this.newPage();
		this.getAuction(auctionId, nonUserAuction);
	}
	
	this.enterSellMode = function(){
		this.newPage();
		this.getUserOpenAuctions();
	}
	
	this.newPage = function() {
		this.refreshView();
		mainDivs = [];
		visAuctionsSave = null;
	}
	
	this.refreshView = function() {
		mainDivs.forEach(function(div){
			if(document.getElementById(div) != null)
				document.getElementById(div).remove();
		});
	}
	
	this.getVisitedAuctions = function() {
      makeCall("GET", "getAuction?auctionId=" + getCookie("visitedAuctions"+loggedUsername), null,
        function(req) {
          if (req.readyState == 4) {
            if (req.status == 200) {
				visAuctionsSave = JSON.parse(req.responseText);
				self.getWonAuctions();
            }else{
				window.alert("Errore: " + req.status);
			}
			
          } else {
			
          }
        })
	};
	
	this.getWonAuctions = function() {
      makeCall("GET", "getWonAuctions", null,
        function(req) {
          if (req.readyState == 4) {
            if (req.status == 200) {
              var wonAuctions = JSON.parse(req.responseText);
				self.buildBuyModeStructure(wonAuctions);
            }else{
				window.alert("Errore: " + req.status);
			}
			
          } else {
			
          }
        })
	};
	
	this.searchAuctions = function(query){
		setCookie("lastSearch"+loggedUsername, query, 30);
		makeCall("GET", "searchAuctions?searchQuery=" + query , null,
        function(req) {
          if (req.readyState == 4) {
            if (req.status == 200) {
              var results = JSON.parse(req.responseText);
				document.getElementById("searchdiv").remove();
				var oldWonDiv = document.getElementById("wonDiv");
				document.getElementById("wonDiv").remove();
				self.buildBuyModeSearchAuctions(results, null);
				content.appendChild(oldWonDiv);			
            }else{
				window.alert("Errore: " + req.status);
			}
          } else {
	
          }
        }, false)
	};
	
	this.getAuction = function(auctionId, nonUserAuction){
		if(nonUserAuction === true)
			addToCookieList("visitedAuctions"+loggedUsername, auctionId, 30);
		makeCall("GET", "getAuction?auctionId=" + auctionId , null,
        function(req) {
          if (req.readyState == 4) {
            if (req.status == 200) {
              var results = JSON.parse(req.responseText);
				self.buildAuctionModeStructure(
					(results.userInfo == "")?null:JSON.parse(results.userInfo), 
					(results.winnerOffer == "")?null:JSON.parse(results.winnerOffer), 
					(results.winnerInfo == "")?null:JSON.parse(results.winnerInfo), 
					results.todaydate, 
					(results.auction == "")?null:JSON.parse(results.auction));
            }else{
				window.alert("Errore: " + req.status);
			}
          } else {
	
          }
        }, false)
	};
	
	this.getUserOpenAuctions = function(refreshView, oldClosedDiv, oldCreateDiv) {
      makeCall("GET", "getActiveAuctions", null,
        function(req) {
          if (req.readyState == 4) {
            if (req.status == 200) {
			var openAuctions = JSON.parse(req.responseText);
			if(refreshView){
				content.appendChild(self.buildSellModeActiveAuctions(openAuctions));
				content.appendChild(oldClosedDiv);	
				content.appendChild(oldCreateDiv);	
			}else{
				self.getUserClosedAuctions(openAuctions);
			}
              
            }else{
				window.alert("Errore: " + req.status);
			}
			
          } else {
			
          }
        })
	};
	
	this.getUserClosedAuctions = function(openAuctions) {
      makeCall("GET", "getClosedAuctions", null,
        function(req) {
          if (req.readyState == 4) {
            if (req.status == 200) {
              var closedAuctions = JSON.parse(req.responseText);
				self.buildSellModeStructure(openAuctions, closedAuctions);
            }else{
				window.alert("Errore: " + req.status);
			}
			
          } else {
			
          }
        })
	};
	
		
	this.makeOffer = function(auctionId, form) {
      makeCall("POST", "makeOffer", document.getElementById(form),
        function(req) {
          if (req.readyState == 4) {
            if (req.status == 200) {
			setCookie("lastAction"+loggedUsername, "makeOffer", 30);
              self.enterAuctionMode(auctionId);
            }else{
				window.alert("Errore: " + req.status);
			}
			
          } else {
			
          }
        })
	};
	
		
	this.closeAuction = function(auctionId, form) {
      makeCall("POST", "closeAuction", document.getElementById(form),
        function(req) {
          if (req.readyState == 4) {
            if (req.status == 200) {
				setCookie("lastAction"+loggedUsername, "closeAuction", 30);
				self.enterAuctionMode(auctionId);
            }else{
				window.alert("Errore: " + req.status);
			}
			
          } else {
			
          }
        })
	};
	
	this.createAuction = function(form) {
      makeCall("POST", "createAuction", document.getElementById(form),
        function(req) {
          if (req.readyState == 4) {
            if (req.status == 200) {
				setCookie("lastAction"+loggedUsername, "createAuction", 30);
				document.getElementById("activeDiv").remove();
				var oldClosedDiv = document.getElementById("closedDiv");
				var oldCreateDiv = document.getElementById("createDiv");
				document.getElementById("closedDiv").remove();
				document.getElementById("createDiv").remove();
				self.getUserOpenAuctions(true, oldClosedDiv, oldCreateDiv);
            }else{
				window.alert("Errore: " + req.status);
			}
			
          } else {
			
          }
        })
	};
	
	this.buildBuyModeSearchAuctions = function(searchResults){
		

		//create the div for the search and previously visited
		var searchdiv = document.createElement("div");
		searchdiv.setAttribute("id", "searchdiv");
		searchdiv.className = "content buy-main-div";
		
		var searchtitle = document.createElement("p");
		searchtitle.className = "subtitle centered";
		var searchtitlenode = document.createTextNode("Cerca un asta o prodotto:");
		searchtitle.appendChild(searchtitlenode);
		searchdiv.appendChild(searchtitle);
		var searchform = document.createElement("form");
    	searchform.setAttribute("method", "get");
    	searchform.setAttribute("action", "");
    	searchform.setAttribute("enctype", "multipart/form-data");
		var searchformdiv = document.createElement("div");
		searchformdiv.className = "form search";
		var searchforminput1 = document.createElement("input");
    	searchforminput1.setAttribute("type", "text");
    	searchforminput1.setAttribute("name", "searchQuery");
		searchform.addEventListener("submit", function(evt){
			evt.preventDefault();
			self.searchAuctions(searchforminput1.value);
		}, true);
		var searchformbutton1 = document.createElement("button");
		searchformbutton1.className = "submit";
    	searchformbutton1.setAttribute("type", "submit");
		var searchformbutton1text = document.createTextNode("Cerca");
		searchformbutton1.appendChild(searchformbutton1text);
		searchformdiv.appendChild(searchforminput1);
		searchformdiv.appendChild(searchformbutton1);
		searchform.appendChild(searchformdiv);
		searchdiv.appendChild(searchform);
		
		
		if(searchResults == null){
			
		}else{
			var divider1 = document.createElement("hr");
			var divider2 = document.createElement("p");
			
			if(searchResults.length == 0){
				var resultsdiv = document.createElement("div");
				resultsdiv.className = "centered-div";
				var text = document.createElement("p");
				text.className  = "text";
				text.innerHTML = "Nessun risultato."
				resultsdiv.appendChild(text);
				searchdiv.appendChild(resultsdiv);
			}else{
				searchdiv.appendChild(divider1);
				searchdiv.appendChild(divider2);
				var searchsubtitle = document.createElement("p");
				searchsubtitle.className  = "subtitle";
				searchsubtitle.innerHTML = "Risultati per: " + getCookie("lastSearch"+loggedUsername);
				searchdiv.appendChild(searchsubtitle);
				searchResults.forEach(function(result){
					searchdiv.appendChild(self.buildAuctionSearchResult(result, true));
				});
			}
		}
		
		if(visAuctionsSave != null && visAuctionsSave.length > 0){
			var visitedDiv = document.createElement("div");
			visitedDiv.setAttribute("id", "visitedDiv"); 
			var visitedTitle = document.createElement("p");
			visitedTitle.className = "subtitle";
			var visitedTitlenode = document.createTextNode("Visitate in precedenza");
			visitedTitle.appendChild(visitedTitlenode);
			var divider3 = document.createElement("hr");
			var divider4 = document.createElement("p");
			var divider5 = document.createElement("p");
			visitedTitle.appendChild(visitedTitlenode);
			visitedDiv.appendChild(visitedTitle);
			var visitedOne = false;
			visAuctionsSave.forEach(function(result){
				if(result != null){
					visitedOne = true;
				}	
			});
			if(visitedOne) {
				searchdiv.appendChild(visitedDiv);
				searchdiv.appendChild(divider4);
				searchdiv.appendChild(divider3);	
				searchdiv.appendChild(divider5);
				visAuctionsSave.forEach(function(result){
					if(result != null){
						searchdiv.appendChild(self.buildAuctionSearchResult(result, true));
					}	
				});
			}
		}
		content.appendChild(searchdiv);

		mainDivs.indexOf("searchdiv") === -1 ? mainDivs.push("searchdiv") : console.log("This item already exists");
		return searchdiv;
	};
	
	this.buildAuctionSearchResult = function(result, notCurrentUser){
		var link = document.createElement("a");
		link.className = "linkdiv";
		link.addEventListener("click", function(){
			self.enterAuctionMode(result.id, notCurrentUser);
		});
		var auctContainer = document.createElement("div");
		auctContainer.className = "small-auction-div";
		var auctTitle = document.createElement("p");
		auctTitle.className  = "text";
		auctTitle.innerHTML = "<b>Asta: </b>" + result.title;
		var auctImg = document.createElement("img");
		auctImg.src = "data:image/jpeg;base64," + result.product.image;
		auctImg.className = "product-image";
		var auctProd = document.createElement("p");
		auctProd.className  = "text";
		auctProd.innerHTML = "<b>Prodotto: </b>" + result.product.name + " (Codice: " + result.product.code + ")";
		var maxOffer = document.createElement("p");
		maxOffer.className  = "text";
		maxOffer.innerHTML = "<b>Offerta massima corrente: </b>" +((Object.keys(result.offers).length > 0)?(result.offers[0].price + "€"):"nessuna.");
		var auctDate1 = document.createElement("p");
		auctDate1.className  = "text";
		var startTime = new Date(result.startTime);
		auctDate1.innerHTML = "Asta creata in data " + startTime.getDate() + "/" + (startTime.getMonth()+1) + "/" + startTime.getFullYear() + " alle ore " + startTime.getHours() + ":" + startTime.getMinutes();
		var auctDate2 = document.createElement("p");
		auctDate2.className  = "text";
		var endTime = new Date(result.endTime);
		auctDate2.innerHTML = "Asta termina in data " + endTime.getDate() + "/" + (endTime.getMonth()+1) + "/" + endTime.getFullYear() + " alle ore " + endTime.getHours() + ":" + endTime.getMinutes();
		
		auctContainer.appendChild(auctTitle);
		auctContainer.appendChild(auctImg);
		auctContainer.appendChild(auctProd);
		auctContainer.appendChild(maxOffer);
		auctContainer.appendChild(auctDate1);
		auctContainer.appendChild(auctDate2);
		link.appendChild(auctContainer);
		return link;
	}
	
	this.buildBuyModeMyAuctions = function(wonAuctions){
		//create the div for the won auctions
		var wonDiv = document.createElement("div");
		wonDiv.setAttribute("id", "wonDiv");
		wonDiv.className = "content buy-main-div";

		var wontitle = document.createElement("p");
		wontitle.className = "subtitle";
		var wontitlenode = document.createTextNode("Le tue aste aggiudicate");
		wontitle.appendChild(wontitlenode);
		wonDiv.appendChild(wontitle);	
		if(wonAuctions == null){
			
		}else{
			var divider1 = document.createElement("hr");
			var divider2 = document.createElement("p");
			
			if(wonAuctions.length == 0){
				var resultsdiv = document.createElement("div");
				resultsdiv.className = "centered-div";
				var text = document.createElement("p");
				text.className  = "text";
				text.innerHTML = "Non hai mai vinto un'asta."
				resultsdiv.appendChild(text);
				wonDiv.appendChild(resultsdiv);
			}else{
				wonAuctions.forEach(function(result){
					wonDiv.appendChild(self.buildBuyModeAuctionWonResult(result));
				});
			}
			content.appendChild(wonDiv);
		}
		mainDivs.indexOf("wonDiv") === -1 ? mainDivs.push("wonDiv") : console.log("This item already exists");
		return wonDiv;
	};
	
	this.buildBuyModeAuctionWonResult = function(result){
		var link = document.createElement("a");
		link.className = "linkdiv";
		link.addEventListener("click", function(){
			self.enterAuctionMode(result.id, false);
		});
		var auctContainer = document.createElement("div");
		auctContainer.className = "small-auction-div";
		var auctTitle = document.createElement("p");
		auctTitle.className  = "text";
		auctTitle.innerHTML = "<b>Asta: </b>" + result.title;
		var auctImg = document.createElement("img");
		auctImg.src = "data:image/jpeg;base64," + result.product.image;
		auctImg.className = "product-image";
		var auctProd = document.createElement("p");
		auctProd.className  = "text";
		auctProd.innerHTML = "<b>Prodotto: </b>" + result.product.name + " (Codice: " + result.product.code + ")";
		var maxOffer = document.createElement("p");
		maxOffer.className  = "text";
		maxOffer.innerHTML = "<b>Prezzo pagato: </b>" + result.offers[0].price + "€";
		var auctDate2 = document.createElement("p");
		auctDate2.className  = "text";
		var endTime = new Date(result.endTime);
		auctDate2.innerHTML = "Asta vinta in data " + endTime.getDate() + "/" + (endTime.getMonth()+1) + "/" + endTime.getFullYear() + " alle ore " + endTime.getHours() + ":" + endTime.getMinutes();
		
		auctContainer.appendChild(auctTitle);
		auctContainer.appendChild(auctImg);
		auctContainer.appendChild(auctProd);
		auctContainer.appendChild(maxOffer);
		auctContainer.appendChild(auctDate2);
		link.appendChild(auctContainer);
		return link;
	}
	
	
	this.buildSellModeAuctionClosedResult = function(result){
		var link = document.createElement("a");
		link.className = "linkdiv";
		link.addEventListener("click", function(){
			self.enterAuctionMode(result.id, false);
		});
		var auctContainer = document.createElement("div");
		auctContainer.className = "small-auction-div";
		var auctTitle = document.createElement("p");
		auctTitle.className  = "text";
		auctTitle.innerHTML = "<b>Asta: </b>" + result.title;
		var auctImg = document.createElement("img");
		auctImg.src = "data:image/jpeg;base64," + result.product.image;
		auctImg.className = "product-image";
		var auctProd = document.createElement("p");
		auctProd.className  = "text";
		auctProd.innerHTML = "<b>Prodotto: </b>" + result.product.name + " (Codice: " + result.product.code + ")";
		var maxOffer = document.createElement("p");
		maxOffer.className  = "text";
		maxOffer.innerHTML = "<b>Offerta massima: </b>" +((Object.keys(result.offers).length > 0)?(result.offers[0].price + "€"):"nessuna.");
		var auctDate2 = document.createElement("p");
		auctDate2.className  = "text";
		var endTime = new Date(result.endTime);
		auctDate2.innerHTML = "Asta conclusa in data " + endTime.getDate() + "/" + (endTime.getMonth()+1) + "/" + endTime.getFullYear() + " alle ore " + endTime.getHours() + ":" + endTime.getMinutes();
		
		auctContainer.appendChild(auctTitle);
		auctContainer.appendChild(auctImg);
		auctContainer.appendChild(auctProd);
		auctContainer.appendChild(maxOffer);
		auctContainer.appendChild(auctDate2);
		link.appendChild(auctContainer);
		return link;
	}
	
	
	this.buildSellModeActiveAuctions = function(activeAuctions){
		//create the div for the active auctions
		var activeDiv = document.createElement("div");
		activeDiv.setAttribute("id", "activeDiv");
		activeDiv.className = "content sell-main-div open";

		var activetitle = document.createElement("p");
		activetitle.className = "subtitle";
		var activetitlenode = document.createTextNode("Le tue aste in corso:");
		activetitle.appendChild(activetitlenode);
		activeDiv.appendChild(activetitle);	
		var divider1 = document.createElement("hr");
		var divider2 = document.createElement("p");
		activeDiv.appendChild(divider1);	
		activeDiv.appendChild(divider2);	
		if(activeAuctions == null){
			
		}else{
			if(activeAuctions.length == 0){
				var resultsdiv = document.createElement("div");
				resultsdiv.className = "centered-div";
				var text = document.createElement("p");
				text.className  = "text";
				text.innerHTML = "Non hai aste in corso."
				resultsdiv.appendChild(text);
				activeDiv.appendChild(resultsdiv);
			}else{
				activeAuctions.forEach(function(result){
					activeDiv.appendChild(self.buildAuctionSearchResult(result, false));
				});
			}
			content.appendChild(activeDiv);
		}
		mainDivs.indexOf("activeDiv") === -1 ? mainDivs.push("activeDiv") : console.log("This item already exists");
		return activeDiv;		
	}
	
	this.buildSellModeClosedAuctions = function(closedAuctions){
		//create the div for the closed auctions
		var closedDiv = document.createElement("div");
		closedDiv.setAttribute("id", "closedDiv");
		closedDiv.className = "content sell-main-div closed";

		var closedtitle = document.createElement("p");
		closedtitle.className = "subtitle";
		var closedtitlenode = document.createTextNode("Le tue aste concluse:");
		closedtitle.appendChild(closedtitlenode);
		closedDiv.appendChild(closedtitle);	
		var divider1 = document.createElement("hr");
		var divider2 = document.createElement("p");
		closedDiv.appendChild(divider1);	
		closedDiv.appendChild(divider2);	
		if(closedAuctions == null){
			
		}else{
			if(closedAuctions.length == 0){
				var resultsdiv = document.createElement("div");
				resultsdiv.className = "centered-div";
				var text = document.createElement("p");
				text.className  = "text";
				text.innerHTML = "Non hai mai concluso un\'asta."
				resultsdiv.appendChild(text);
				closedDiv.appendChild(resultsdiv);
			}else{
				closedAuctions.forEach(function(result){
					closedDiv.appendChild(self.buildSellModeAuctionClosedResult(result));
				});
			}
			content.appendChild(closedDiv);
		}
		mainDivs.indexOf("closedDiv") === -1 ? mainDivs.push("closedDiv") : console.log("This item already exists");
		return closedDiv;
	}
	
	this.buildSellModeCreateAuctions = function(){
		//create the div for the create auction form
		var createDiv = document.createElement("div");
		createDiv.setAttribute("id", "createDiv");
		createDiv.className = "content sell-main-div create";	

		var createtitle = document.createElement("p");
		createtitle.className = "subtitle";
		var createtitlenode = document.createTextNode("Crea nuova asta");
		createtitle.appendChild(createtitlenode);
		createDiv.appendChild(createtitle);	
		
		createForm = document.createElement("form");
		createForm.setAttribute("id", "create-auction-id")
		createForm.setAttribute("method", "POST")
		createForm.setAttribute("enctype", "multipart/form-data")
		var createDivInner = document.createElement("div");
		createDivInner.className = "form big";		
		var createFormlabel1 = document.createElement("label");
		createFormlabel1.setAttribute("for", "title");	
		createFormlabel1.innerHTML = "<b>Titolo asta</b>";	
		var createForminput1 = document.createElement("input");
    	createForminput1.setAttribute("type", "text");
    	createForminput1.setAttribute("name", "title");
    	createForminput1.required = true;
		var createFormlabel2 = document.createElement("label");
		createFormlabel2.setAttribute("for", "name");	
		createFormlabel2.innerHTML = "<b>Nome del prodotto</b>";		
		var createForminput2 = document.createElement("input");
    	createForminput2.setAttribute("type", "text");
    	createForminput2.setAttribute("name", "name");
    	createForminput2.required = true;		
		var createFormlabel3 = document.createElement("label");
		createFormlabel3.setAttribute("for", "desc");	
		createFormlabel3.innerHTML = "<b>Descrizione del prodotto</b>";		
		var createForminput3 = document.createElement("textarea");
    	createForminput3.setAttribute("form", "create-auction-id");
    	createForminput3.setAttribute("placeholder", "Descrivi il tuo prodotto");
    	createForminput3.setAttribute("name", "desc");
    	createForminput3.required = true;			
		var createFormlabel4 = document.createElement("label");
		createFormlabel4.setAttribute("for", "image");	
		createFormlabel4.innerHTML = "<b>Carica un'immagine del tuo prodotto</b>";		
		var createForminput4 = document.createElement("input");
    	createForminput4.setAttribute("type", "file");
    	createForminput4.setAttribute("name", "image");
    	createForminput4.required = true;			
		var createFormlabel5 = document.createElement("label");
		createFormlabel5.setAttribute("for", "price");	
		createFormlabel5.innerHTML = "<b>Prezzo di partenza (€)</b>";		
		var createForminput5 = document.createElement("input");
    	createForminput5.setAttribute("type", "number");
    	createForminput5.setAttribute("name", "price");
    	createForminput5.setAttribute("step", "0.01");
    	createForminput5.setAttribute("min", "0");
    	createForminput5.required = true;	
		var createFormlabel6 = document.createElement("label");
		createFormlabel6.setAttribute("for", "step");	
		createFormlabel6.innerHTML = "<b>Rialzo minimo</b>";		
		var createForminput6 = document.createElement("input");
    	createForminput6.setAttribute("type", "number");
    	createForminput6.setAttribute("name", "step");
    	createForminput6.setAttribute("step", "1");
    	createForminput6.required = true;			
		var createFormlabel7 = document.createElement("label");
		createFormlabel7.setAttribute("for", "enddate");	
		createFormlabel7.innerHTML = "<b>Termine asta</b>";	
		var now = new Date();
		now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
		var endDate = now.toISOString().slice(0,16);
		var createForminput7 = document.createElement("input");
    	createForminput7.setAttribute("type", "datetime-local");
    	createForminput7.setAttribute("name", "enddate");
    	createForminput7.setAttribute("value", endDate);
    	createForminput7.setAttribute("min", endDate);
    	createForminput7.required = true;			
		var createFormbutton1 = document.createElement("button");
		createFormbutton1.className = "submit";
    	createFormbutton1.setAttribute("type", "submit");		
    	createFormbutton1.innerHTML = "Inserisci inserzione";	

		createForm.addEventListener("submit", function(evt){
			evt.preventDefault();
			self.createAuction("create-auction-id");
		}, true);
		
		createDivInner.appendChild(createFormlabel1);
		createDivInner.appendChild(createForminput1);
		createDivInner.appendChild(createFormlabel2);
		createDivInner.appendChild(createForminput2);
		createDivInner.appendChild(createFormlabel3);
		createDivInner.appendChild(createForminput3);
		createDivInner.appendChild(createFormlabel4);
		createDivInner.appendChild(createForminput4);
		createDivInner.appendChild(createFormlabel5);
		createDivInner.appendChild(createForminput5);
		createDivInner.appendChild(createFormlabel6);
		createDivInner.appendChild(createForminput6);
		createDivInner.appendChild(createFormlabel7);
		createDivInner.appendChild(createForminput7);
		createDivInner.appendChild(createFormbutton1);
		createForm.appendChild(createDivInner);
		createDiv.appendChild(createForm);
		content.appendChild(createDiv);		
		mainDivs.indexOf("createDiv") === -1 ? mainDivs.push("createDiv") : console.log("This item already exists");
		return createDiv;	
	}
	
	this.buildBuyModeStructure = function(wonAuctions){
		document.title = "Acquisto";
		var top = document.createElement("div");
		top.className = "content top";
		top.setAttribute("id", "topDiv");
		var title = document.createElement("p");
		title.className = "title";
		var node = document.createTextNode("Acquisto");
		title.appendChild(node);
		top.appendChild(title);
		content.appendChild(top);
		mainDivs.indexOf("topDiv") === -1 ? mainDivs.push("topDiv") : console.log("This item already exists");
		content.appendChild(this.buildBuyModeSearchAuctions(null));
		content.appendChild(this.buildBuyModeMyAuctions(wonAuctions));
	};
	
	this.buildSellModeStructure = function(openAuctions, closedAuctions){
		document.title = "Vendo";
		var top = document.createElement("div");
		top.className = "content top";
		top.setAttribute("id", "topDiv");
		var title = document.createElement("p");
		title.className = "title";
		var node = document.createTextNode("Vendo");
		title.appendChild(node);
		top.appendChild(title);
		content.appendChild(top);
		mainDivs.indexOf("topDiv") === -1 ? mainDivs.push("topDiv") : console.log("This item already exists");
		content.appendChild(this.buildSellModeActiveAuctions(openAuctions));
		content.appendChild(this.buildSellModeClosedAuctions(closedAuctions));
		content.appendChild(this.buildSellModeCreateAuctions());
		
	};
	
	
	this.buildAuctionModeStructure = function(userInfo, winnerOffer, winnerInfo, todaydate, auction){
		var top = document.createElement("div");
		top.className = "content top";
		top.setAttribute("id", "topDiv");
		var title = document.createElement("p");
		title.className = "title";
		var titlestring = "";
		if(userInfo.id === auction.owner.id){
			var titlestring = "Dettaglio Asta";
		}else{
			var titlestring = "Offerta";
		}
		document.title = titlestring;
		var node = document.createTextNode(titlestring);
		title.appendChild(node);
		top.appendChild(title);
		content.appendChild(top);
		mainDivs.indexOf("topDiv") === -1 ? mainDivs.push("topDiv") : console.log("This item already exists");
		
		//create the div for the image
		var imagediv = document.createElement("div");
		imagediv.setAttribute("id", "imagediv");
		imagediv.className = "content big-auction-div image";
		var auctImg = document.createElement("img");
		auctImg.src = "data:image/jpeg;base64," + auction.product.image;
		auctImg.className = "product-image";
		imagediv.appendChild(auctImg);
		content.appendChild(imagediv);
		mainDivs.indexOf("imagediv") === -1 ? mainDivs.push("imagediv") : console.log("This item already exists");

		//create the div for the auction data
		var auctDataContainer = document.createElement("div");
		auctDataContainer.className = "content big-auction-div data";
		auctDataContainer.setAttribute("id", "auctDataContainer");
		var auctTitle = document.createElement("p");
		auctTitle.className  = "subtitle";
		auctTitle.innerHTML = "<b>Asta: </b>" + auction.title;
		var auctPrice = document.createElement("p");
		auctPrice.className  = "subtitle";
		auctPrice.innerHTML = "<b>Prezzo di partenza: </b>" + auction.minPrice + "€ (<b>Rialzo minimo: </b>" + auction.priceStep + "€)";
		var auctProd = document.createElement("p");
		auctProd.className  = "text";
		auctProd.innerHTML = "<b>Prodotto: </b>" + auction.product.name + " (Codice: " + auction.product.code + ")";
		var auctDesc1 = document.createElement("p");
		auctDesc1.className  = "text";
		auctDesc1.innerHTML = "<b>Descrizione:</b>";
		var auctDesc2 = document.createElement("p");
		auctDesc2.className  = "text";
		auctDesc2.innerHTML = auction.product.desc;
		var auctDate2 = document.createElement("p");
		auctDate2.className  = "text";
		var startTime = new Date(auction.startTime);
		auctDate2.innerHTML = "Asta creata in data " + startTime.getDate() + "/" + (startTime.getMonth()+1) + "/" + startTime.getFullYear() + " alle ore " + startTime.getHours() + ":" + startTime.getMinutes();

		auctDataContainer.appendChild(auctTitle);
		auctDataContainer.appendChild(auctPrice);
		auctDataContainer.appendChild(auctProd);
		auctDataContainer.appendChild(auctDesc1);
		auctDataContainer.appendChild(auctDesc2);
		auctDataContainer.appendChild(auctDate2);
		if(auction.active == true){
			var auctDate3 = document.createElement("p");
			auctDate3.className  = "text";
			var endTime = new Date(auction.endTime);
			auctDate3.innerHTML = "Termine ultimo in data " + endTime.getDate() + "/" + (endTime.getMonth()+1) + "/" + endTime.getFullYear() + " alle ore " + endTime.getHours() + ":" + endTime.getMinutes();
			var auctDate4 = document.createElement("p");
			auctDate4.className  = "text";
			auctDate4.innerHTML = "Tempo rimanente: " + auction.daysUntilEnd + " giorni, " + auction.hoursUntilEnd + " ore, " + auction.minutesUntilEnd + " minuti.";
			auctDataContainer.appendChild(auctDate3);
			auctDataContainer.appendChild(auctDate4);

		}else{
			var auctDate5 = document.createElement("p");
			auctDate5.className  = "text";
			var endTime = new Date(auction.endTime);
			auctDate2.innerHTML = "Asta conclusa in data " + endTime.getDate() + "/" + (endTime.getMonth()+1) + "/" + endTime.getFullYear() + " alle ore " + endTime.getHours() + ":" + endTime.getMinutes();
			auctDataContainer.appendChild(auctDate5);
		}
		mainDivs.indexOf("auctDataContainer") === -1 ? mainDivs.push("auctDataContainer") : console.log("This item already exists");
		content.appendChild(auctDataContainer);
		
		
		//create the div for the auction options and offers
		var auctOptionsContainer = document.createElement("div");
		auctOptionsContainer.className = "content big-auction-div right";
		auctOptionsContainer.setAttribute("id", "auctOptionsContainer");	
		var auctOptionsA = document.createElement("div");
		auctOptionsA.className = "offers-options";
		if(auction.active === true){
			if(userInfo.id === auction.owner.id){
				var closeForm = document.createElement("form");
		    	closeForm.setAttribute("id", "closeForm");
		    	closeForm.setAttribute("method", "post");
		    	closeForm.setAttribute("action", "");
				var closeForminput1 = document.createElement("input");
		    	closeForminput1.setAttribute("type", "hidden");
		    	closeForminput1.setAttribute("name", "auctionId");
		    	closeForminput1.setAttribute("id", "auctionId");
		    	closeForminput1.setAttribute("value", auction.id);
				closeForm.addEventListener("submit", function(evt){
					evt.preventDefault();
					self.closeAuction(closeForminput1.value, "closeForm");
				}, true);
				var closeFormbutton1 = document.createElement("button");
				closeFormbutton1.className = "submit";
		    	closeFormbutton1.setAttribute("type", "submit");
				var closeFormbutton1text = document.createTextNode("Chiudi inserzione");
				closeFormbutton1.appendChild(closeFormbutton1text);
				closeForm.appendChild(closeForminput1);
				closeForm.appendChild(closeFormbutton1);
				auctOptionsA.appendChild(closeForm);
			}else{
				var offerFormhintText = document.createElement("p");
				offerFormhintText.className  = "subtitle";
				offerFormhintText.innerHTML = "Fai un offerta";
				var offerForm = document.createElement("form");
		    	offerForm.setAttribute("id", "offerForm");
		    	offerForm.setAttribute("method", "post");
		    	offerForm.setAttribute("action", "");
				var offerFormDiv = document.createElement("div");
				offerFormDiv.className = "form small";
				var offerForminput1 = document.createElement("input");
		    	offerForminput1.setAttribute("type", "hidden");
		    	offerForminput1.setAttribute("name", "auctionId");
		    	offerForminput1.setAttribute("id", "auctionId");
		    	offerForminput1.setAttribute("value", auction.id);
				var offerFormlabel = document.createElement("label");
				offerForminput1.setAttribute("for", "price");
				var newMinPrice = (winnerOffer != null)?(winnerOffer.price + auction.priceStep):(auction.minPrice);
				offerFormlabel.innerHTML = "<b>Prezzo:</b> (Offerta minima: " + newMinPrice + "€)";
				var offerForminput2 = document.createElement("input");
		    	offerForminput2.setAttribute("type", "number");
		    	offerForminput2.setAttribute("name", "price");
		    	offerForminput2.setAttribute("step", "0.01");
				offerForminput2.setAttribute("min", "" + newMinPrice);
		    	offerForminput2.setAttribute("id", "price");
				offerForminput2.required = true;
				offerForm.addEventListener("submit", function(evt){
					evt.preventDefault();
					self.makeOffer(offerForminput1.value, "offerForm");
				}, true);
				var offerFormbutton1 = document.createElement("button");
				offerFormbutton1.className = "submit";
		    	offerFormbutton1.setAttribute("type", "submit");
				var offerFormbutton1text = document.createTextNode("Invia offerta");
				offerFormbutton1.appendChild(offerFormbutton1text);
				offerForm.appendChild(offerForminput1);
				offerForm.appendChild(offerFormlabel);
				offerForm.appendChild(offerForminput2);
				offerForm.appendChild(offerFormbutton1);
				offerFormDiv.appendChild(offerForm);
				auctOptionsA.appendChild(offerFormhintText);
				auctOptionsA.appendChild(offerFormDiv);
			}
		}else{
			var auctionMessage1 = document.createElement("p");
			auctionMessage1.className  = "subtitle";
			auctionMessage1.innerHTML = "Asta conclusa";
			auctOptionsA.appendChild(auctionMessage1);
			if(winnerInfo === null || winnerOffer === null){
				var auctionMessage2 = document.createElement("p");
				auctionMessage2.className  = "text";
				auctionMessage2.innerHTML = "Asta chiusa senza offerte.";
				auctOptionsA.appendChild(auctionMessage2);
			}else{
				var auctionMessage3 = document.createElement("p");
				auctionMessage3.className  = "text";
				auctionMessage3.innerHTML = "Aggiudicatario: " + winnerInfo.username;
				var auctionMessage4 = document.createElement("p");
				auctionMessage4.className  = "text";
				auctionMessage4.innerHTML = "Prezzo: " + winnerOffer.price + "€";
				var auctionMessage5 = document.createElement("p");
				auctionMessage5.className  = "text";
				auctionMessage5.innerHTML = "Indirizzo: Via Celio Vibenna, 00184 Roma (RM)";
				auctOptionsA.appendChild(auctionMessage3);
				auctOptionsA.appendChild(auctionMessage4);
				if(userInfo.id === auction.owner.id)
					auctOptionsA.appendChild(auctionMessage5);
			}
		}
		var divider1 = document.createElement("p");
		var divider2 = document.createElement("hr");
		var auctOptionsB = document.createElement("div");
		auctOptionsB.className = "offers-list";				
		var offersTitle = document.createElement("p");
		offersTitle.className  = "subtitle";
		offersTitle.innerHTML = "Offerte:";
		var auctOptionsBInner = document.createElement("div");
		auctOptionsBInner.className = "offers-list";
		if(auction.offers.length > 0){
			for(let i = 0; i < auction.offers.length; i++){
				var offer = auction.offers[i];
				var offerDetail1 = document.createElement("p");
				offerDetail1.className  = "text";
				offerDetail1.innerHTML = "<b>Offerente: </b>" + offer.bidderUsername;
				var offerDetail2 = document.createElement("p");
				offerDetail2.className  = "text";
				offerDetail2.innerHTML = "<b>Offerta: </b>" + offer.price + "€";
				var offerDetail3 = document.createElement("p");
				offerDetail3.className  = "text";
				var time = new Date(offer.time);
				offerDetail3.innerHTML = "<b>Data: </b>" + time.getDate() + "/" + (time.getMonth()+1) + "/" + time.getFullYear() + " " + time.getHours() + ":" + time.getMinutes();								
				var divider3 = document.createElement("hr");
				auctOptionsBInner.appendChild(offerDetail1);
				auctOptionsBInner.appendChild(offerDetail2);
				auctOptionsBInner.appendChild(offerDetail3);
				auctOptionsBInner.appendChild(divider3);
			}
		}else{
			var auctOptionsBInner2 = document.createElement("div");
			auctOptionsBInner2.className = "centered-div";	
			var offersTitle2 = document.createElement("p");
			offersTitle2.className  = "text";
			offersTitle2.innerHTML = "Nessuna offerta.";	
			auctOptionsBInner2.appendChild(offersTitle2);
			auctOptionsBInner.appendChild(auctOptionsBInner2);
		}

		auctOptionsB.appendChild(offersTitle);
		auctOptionsB.appendChild(auctOptionsBInner);

		auctOptionsContainer.appendChild(auctOptionsA);
		auctOptionsContainer.appendChild(divider1);
		auctOptionsContainer.appendChild(divider2);
		auctOptionsContainer.appendChild(auctOptionsB);
		mainDivs.indexOf("auctOptionsContainer") === -1 ? mainDivs.push("auctOptionsContainer") : console.log("This item already exists");
		content.appendChild(auctOptionsContainer);
	};
  }
})();
