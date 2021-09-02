function makeCall(method, url, formElement, cback, reset) {
    var req = new XMLHttpRequest();
    req.onreadystatechange = function() {
      cback(req)
    }; 
    req.open(method, url);
	//req.setRequestHeader("Content-Type", "multipart/form-data");
    if (formElement == null) {
      req.send();
    } else {
	var content = new FormData(formElement);
	for (var [key, value] of content.entries()) { 
 		 console.log(key, value);
		}
      req.send(content);
    }
    if (formElement !== null && reset === true) {
      formElement.reset();
    }
  }

function setCookie(cname, cvalue, exdays) {
  const d = new Date();
  d.setTime(d.getTime() + (exdays*24*60*60*1000));
  let expires = "expires="+ d.toUTCString();
  document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
  let name = cname + "=";
  let decodedCookie = decodeURIComponent(document.cookie);
  let ca = decodedCookie.split(';');
  for(let i = 0; i <ca.length; i++) {
    let c = ca[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
}

function addToCookieList(cname, cvalue, exdays){
	let oldcookielist = getCookie(cname);
	let valuearr = oldcookielist.split("A");
	if(!valuearr.includes(cvalue+"")){
		let newcookievalue = "";
		for(let i = 0; i < valuearr.length; i++){
			newcookievalue = newcookievalue + valuearr[i] + "A";
		}
		newcookievalue = newcookievalue + cvalue;
		setCookie(cname, newcookievalue, exdays);
	}
}

function getCookieList(cname){
	return getCookie(cname).split("A");
}
