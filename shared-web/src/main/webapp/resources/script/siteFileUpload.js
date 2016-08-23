var shared = shared || {};
shared.openPopUps = shared.openPopUps || [];

function updateSiteFileName(filename){
	$("input.siteFileInput").val(filename);
	$("input.siteFileInput").trigger('change');
	$(".clearSiteFileButton").show();
};

function initSiteFileButtons(pageContextPath){
	function launchSiteFilePopup(){
		var url = pageContextPath + "/uploadSiteFile";
		var filename = $(this).siblings(".siteFileInput").first().val();
		if(filename != null && filename != ''){
			url = url + "?filename=" + filename;
		}
		var questionId = null;
		//Try and find the question id
		var $qdiv = $(this).closest("div.questionDiv");
		if($qdiv != null ){
			questionId = $qdiv.find("span.questionIdSpan").first().html();
			if(filename != null && filename != ''){
				url = url + "&questionId=" + questionId;
			}else{
				url = url + "?questionId=" + questionId;
			}
		}
		var popup = window.open(url, '_blank', "location=no,toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=no,width=900,height=650", true);
		if (typeof popup == "undefined") {
			alert(shared.popupBlockedMessage);
			return;
		}
		popup.screenX = window.screenX + 200;
		popup.screenY = window.screenY + 100;
		popup.focus();
		shared.openPopUps.push(popup);
	}
	
	$(".uploadSiteFileButton").each(function(index, element) {
		$(element).click(function() {
			shared.executeBasedOnActiveSession({
				active: function() { launchSiteFilePopup.call(element) },
				inactive: shared.reloadPage
			})
		});
	});
	
	$(".clearSiteFileButton").click(function(){
		var $theInput = $(this).siblings(".siteFileInput").first();
		$theInput.val(null);
		$theInput.trigger('change');
		return false;
	});
};
