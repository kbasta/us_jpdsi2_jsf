function checkThemes(){
	if (document.cookie != null){
		document.body.style.setProperty("--font-color", getCookie('fontColor'));
		document.body.style.setProperty("--background-first-color", getCookie('bgColor1'));
		document.body.style.setProperty("--background-second-color", getCookie('bgColor2'));
	}
}
	
function setFontColor(color){
	document.body.style.setProperty("--font-color", color);
	setCookie("fontColor", color, 7);
}

function setBgColor(color1, color2){
	document.body.style.setProperty("--background-first-color", color1);
	document.body.style.setProperty("--background-second-color", color2);
	setCookie("bgColor1", color1, 7);
	setCookie("bgColor2", color2, 7);
}

function inputJsValidation(){
	var text = document.getElementsByTagName("input")[23].value;
	if (text.length < 4)
		alert("Wprowadzony ciąg znaków ma mniej niz 4 znaki.");
	else if (text.length > 4)
		alert("Wprowadzony ciąg znaków ma więcej niz 4 znaki.");
	else{
		const reg = /^[A-Z]{1}[a-z]{2}[0-9]$/;
		if (reg.test(text))
			alert("Wprowadzony ciąg znaków zgadza się z regexem.")
		else
			alert("Wprowadzony ciąg znaków nie zgadza się z regexem.");
	}
}

window.onload = function () {
	var dataPoints = [];
	var chart;
	$.getJSON("https://canvasjs.com/services/data/datapoints.php?xstart=1&ystart=10&length=2&type=json", function(data) {  
		$.each(data, function(key, value){
			dataPoints.push({x: value[0], y: parseInt(value[1])});
		});
		chart = new CanvasJS.Chart("chart",{
			title:{
				text:"Autoodświezający się wykres korzystający z JSON z canvasjs.com"
			},
			data: [{
				type: "splineArea",
				dataPoints : dataPoints,
			}]
		});
		chart.render();
		updateChart();
	});
	function updateChart() {
	$.getJSON("https://canvasjs.com/services/data/datapoints.php?xstart=" + (dataPoints.length + 1) + "&ystart=10&length=10&type=json", function(data) {
		$.each(data, function(key, value) {
			dataPoints.push({
			x: parseInt(value[0]),
			y: parseInt(value[1])
			});
		});
		chart.render();
		setTimeout(function(){updateChart()}, 1000);
	});
	}
}

function setCookie(name, value, days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days*24*60*60*1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "")  + expires + "; path=/";
}

function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}