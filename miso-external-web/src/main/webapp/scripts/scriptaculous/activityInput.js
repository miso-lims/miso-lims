var baseUrl;
var availableInput;
var lockedInput;
var spinner;

function loading() {
	spinner.style.display = 'block'; 
}

function stopLoading() {
	spinner.style.display = 'none'; 
}

function setBaseUrl(url) {
	baseUrl = url;
}

function renderInputSelector(divId) {
	var div = document.getElementById(divId);
	div.innerHTML = '';
    
	availableInput = document.createElement("select"); 
    var lockButton = document.createElement("button"); 
    var unlockButton = document.createElement("button"); 
    lockedInput = document.createElement("select"); 
    spinner = document.createElement("p"); 

    availableInput.setAttribute('multiple','multiple');
    lockButton.setAttribute('type','button');
    lockButton.appendChild(document.createTextNode('Lock >>'));
    lockButton.onclick = ajaxLockInput;
    unlockButton.setAttribute('type','button');
    unlockButton.appendChild(document.createTextNode('Unlock <<'));
    unlockButton.onclick = ajaxReleaseInput;
    lockedInput.setAttribute('multiple','multiple');
    
    spinner.innerHTML='...(updating)...';
    
    div.appendChild(availableInput);
    div.appendChild(lockButton);
    div.appendChild(unlockButton);
    div.appendChild(lockedInput);
    div.appendChild(spinner);
    
    ajaxAvailableInput();
}

function updateAvailableInput(inputDataXml) {
	var opts = availableInput.options;
	var data = inputDataXml.getElementsByTagName("activityData");
	for (i = 0; i < data.length; i++) {
		var label = data[i].attributes.getNamedItem("displayName").value;
		var value = data[i].attributes.getNamedItem("uniqueId").value;
		opts[opts.length]=new Option(label, value, false, false);
	}
}

function removeAvailableInput(inputIds) {
	var opts = availableInput.options;
	for (i = 0; i < opts.length; i++) {
		optloop: for (j = 0; j < inputIds.length; j++) {
			if (opts[i].value==inputIds[j]) {
				opts[i--] = null;
				break optloop;
			}
		}
	}
}

function getSelectedAvailableInputs() {
	var opts = availableInput.options;
	var results = new Array();
	for (i = 0; i < opts.length; i++) {
		if (opts[i].selected) {
			results.push(opts[i].value);
		}
	}
	return results;
}

function updateLockedInput(inputDataXml) {
	var opts = lockedInput.options;
	var data = inputDataXml.getElementsByTagName("activityData");
	for (i = 0; i < data.length; i++) {
		var label = data[i].attributes.getNamedItem("displayName").value;
		var value = data[i].attributes.getNamedItem("uniqueId").value;
		opts[opts.length]=new Option(label, value, false, false);
	}
}

function removeLockedInput(inputIds) {
	var opts = lockedInput.options;
	for (i = 0; i < opts.length; i++) {
		optloop: for (j = 0; j < inputIds.length; j++) {
			if (opts[i].value==inputIds[j]) {
				opts[i--] = null;
				break optloop;
			}
		}
	}
}

function updateReleasedInput(inputDataXml) {
	var inputIds = new Array();
	var data = inputDataXml.getElementsByTagName("activityData");
	for (i = 0; i < data.length; i++) {
		inputIds.push(data[i].attributes.getNamedItem("uniqueId").value);
	}
	updateAvailableInput(inputDataXml);
	removeLockedInput(inputIds);
}

function getSelectedLockedInputs() {
	var opts = lockedInput.options;
	var results = new Array();
	for (i = 0; i < opts.length; i++) {
		if (opts[i].selected) {
			results.push(opts[i].value);
		}
	}
	return results;
}

function getHttpReq(strURL) {
    var xmlHttpReq = false;
    var self = this;
    // Mozilla/Safari
    if (window.XMLHttpRequest) {
        xmlHttpReq = new XMLHttpRequest();
    }
    // IE
    else if (window.ActiveXObject) {
        xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
    }
    xmlHttpReq.open('GET', strURL, true);
    xmlHttpReq.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    return xmlHttpReq;
}

function ajaxAvailableInput() {	
    loading();
    var strURL = baseUrl+"/activity/input/available";
    var xmlHttpReq = getHttpReq(strURL);
    xmlHttpReq.onreadystatechange = function() {
        if (xmlHttpReq.readyState == 4) {
            updateAvailableInput(xmlHttpReq.responseXML.documentElement);
        }
        stopLoading();
    }
    xmlHttpReq.send(strURL);
}

function ajaxLockInput() {
    loading();
    var inputIds = getSelectedAvailableInputs();
    if (inputIds.length<1) {
    	stopLoading();
    	return;
    }
    var strURL = baseUrl+"/activity/input/lock";
    for (i =0; i < inputIds.length; i++) {
    	strURL += (i>0)?'&':'?';
    	strURL+='inputId='+inputIds[i];
    }
	removeAvailableInput(inputIds);
    var xmlHttpReq = getHttpReq(strURL);
    xmlHttpReq.onreadystatechange = function() {
        if (xmlHttpReq.readyState == 4) {
            updateLockedInput(xmlHttpReq.responseXML.documentElement);
        }
        stopLoading();
    }
    xmlHttpReq.send(strURL);
}

function ajaxReleaseInput() {
    loading();
    var inputIds = getSelectedLockedInputs();
    if (inputIds.length<1) {
    	stopLoading();
    	return;
    }
    var strURL = baseUrl+"/activity/input/release";
    for (i =0; i < inputIds.length; i++) {
    	strURL += (i>0)?'&':'?';
    	strURL+='inputId='+inputIds[i];
    }
    var xmlHttpReq = getHttpReq(strURL);
    xmlHttpReq.onreadystatechange = function() {
        if (xmlHttpReq.readyState == 4) {
            updateReleasedInput(xmlHttpReq.responseXML.documentElement);
        }
        stopLoading();
    }
    xmlHttpReq.send(strURL);
}
