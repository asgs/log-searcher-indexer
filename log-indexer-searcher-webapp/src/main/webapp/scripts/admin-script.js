/**
 * 
 */

var base_url = "http://localhost:8080/log-indexer-searcher-webapp/";

$(document)
		.ready(
				function() {
					$("#adminButton")
							.click(
									function() {
										var source = encodeURIComponent($(
												"#source").val());
										var logPatternlayout = encodeURIComponent($(
												"#logPatternlayout").val());
										var sourceType = encodeURIComponent($(
												"#sourceType").val());
										var sourceIndex = encodeURIComponent($(
												"#sourceIndex").val());

										if (!source || !logPatternlayout
												|| !sourceType || !sourceIndex) {
											alert("Please fill out all fields to proceed further.");
											return;
										}

										var configUrl = base_url
												+ "admin/configure"
												+ "?source=" + source
												+ "&logPatternlayout="
												+ logPatternlayout
												+ "&sourceType=" + sourceType
												+ "&sourceIndex=" + sourceIndex;

										alert(configUrl);

										$.ajax({
											url : configUrl
										}).done(
												function(data) {
													console.log(data);
													$("#configResults").html(
															data.message);
												});
									});
				});
