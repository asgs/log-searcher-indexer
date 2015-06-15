/**
 * 
 */

var base_url = "http://localhost:8080/log-indexer-searcher-webapp/"; 

$(document)
		.ready(
				function() {
					$("#searchButton")
							.click(
									function() {
										var text = encodeURIComponent($(
												"#searchQuery").val());
										var timeFrame = $("#timeFrame").val();
										var searchUrl = base_url + "search/query?text="
												+ text;
										if (timeFrame) {
											var timeValue = timeFrame.split(" ")[0];
											var timeUnit = timeFrame.split(" ")[1];
											searchUrl += "&timeframe="
												+ timeValue
												+ "&timeunit="
												+ timeUnit; 
										}									
										//alert("URL is " + searchUrl);
										$("#searchResults").html("");
										$("#searchResults").add("<div id=\"loading\">Loading...</div>");
										$.ajax({
											url : searchUrl
										}).done(function(data) {
											$("#loading").remove();
											if (data.length > 0) {
												$("#searchResults").append("<p style=\"color:green\">" + data.length + " results found.</p>");
												for (var index in data) {
													$("#searchResults").append("<div id=\"" + index + "\">" + data[index] + "</div><br/ >");
												}													
											} else {
												$("#searchResults").append("<p style=\"color:red\">No results found.</p>");
											}
										}).fail(function(jqXHR, textStatus){
											alert("failed to get data because " + textStatus);
										});

									});
				});
