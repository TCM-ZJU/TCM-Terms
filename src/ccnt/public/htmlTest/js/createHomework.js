var xmlHttp;
function createXMLHttpRequest() {
	if (window.ActiveXObject) {
		xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    else if (window.XMLHttpRequest) {
        xmlHttp = new XMLHttpRequest();
    }
}

function createHomework() {
	var name = document.getElementById("name").value;
	window.alert(name);
	var url = 'http://10.214.33.222:8080/mt/getThesaurus?word='+name;
 	window.alert(url);
 	createXMLHttpRequest();
 	xmlHttp.onreadystatechange = showResponse;
 	xmlHttp.open("GET", url, true);
 	xmlHttp.send(null);
}

function showResponse(){
	if(xmlHttp.readyState == 4) {
		if(xmlHttp.status == 200) {
			var response = xmlHttp.responseText;
			window.alert(response);			
		}
	}
}