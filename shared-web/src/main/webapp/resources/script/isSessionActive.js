var shared = shared || {};
shared.popupBlockedMessage = "Could not open pop up window. Is your pop up blocker disabled for SHaRED?";

shared.executeBasedOnActiveSession = function(paramObj) {
	var activeSessionFunction = paramObj.active || function() {};
	var inactiveSessionFunction = paramObj.inactive || function() {};
	var url = pageContextPath + "/isSessionActive";
	$.ajax({
	  url: url,
	  success: function( data ) {
		  var resp = JSON.parse(data);
		  if (resp.sessionActive) {
			  activeSessionFunction();
			  return;
		  }
		  inactiveSessionFunction();
	  },
	  error: inactiveSessionFunction
	});
}

shared.reloadPage = function() {
	alert("Your session has timed out. Any unsaved work has been lost. The page will now refresh automatically.");
	location.reload();
}