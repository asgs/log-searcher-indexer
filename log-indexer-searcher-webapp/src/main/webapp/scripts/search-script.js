/**
 * 
 */

$(document)
		.ready(
				function() {
					$("#searchButton")
							.click(
									function() {
										// alert("jQuery clicked!");
										var text = encodeURIComponent($(
												"#searchQuery").val());
										var timeFrame = $("#timeFrame").val();
										var timeValue = timeFrame.split(" ")[0];
										var timeUnit = timeFrame.split(" ")[1];
										var searchUrl = "http://localhost:8080/log-indexer-searcher-webapp/search/query?text="
												+ text
												+ "&timeframe="
												+ timeValue
												+ "&timeunit="
												+ timeUnit;
										//alert("URL is " + searchUrl);
										$.ajax({
											url : searchUrl

										}).done(function(data) {
											// $("#searchReults").slideDown();
											//var myData = "[\"127.0.0.1 - - [14/Jun/2015:19:50:40 +0530] \"GET /log-indexer-searcher-webapp/log/retrieveAvailableLogs HTTP/1.1\" 200 255\",\"127.0.0.1 - - [14/Jun/2015:19:50:40 +0530] \"GET /log-indexer-searcher-webapp/log/retrieveAvailableLogs HTTP/1.1\" 200 255\"]";
											//var json_array = JSON.parse(data);
											/*var html_text="";
											for (var counter in json_array) {
												html_text +=  json_array[counter];
											}
											$("#searchReults").html(html_text);*/
											//console.log("data[0] is " + data[0]);
											$("#searchResults").html("");
											if (data.length > 0) {
												$("#searchResults").append("<p style=\"color:green\">" + data.length + " results found.</p>");
												for (var index in data) {
													$("#searchResults").append("<div id=\"" + index + "\">" + data[index] + "</div><br/ >");
													//result += data[index] + "\r\n";
												}													
											} else {
												$("#searchResults").append("<p style=\"color:red\">No results found.</p>");
											}
											
											//console.log(html_text);
										});

									});
				});
