var shared = shared || {};
shared.openPopUps = shared.openPopUps || [];

function updateUploadedFileName(questionId, fileName){
	var divId = "Q" + questionId;
	var qDivId = divId.replace(/\./g,'\\.');
	var $qDiv = $('div[id="'+qDivId+'"]').first();
	$qDiv.find("input").first().val(fileName);
	$qDiv.find(".uploadFileClearButton").first().show();
};

$(function(){
	function launchFileUploadPopup(){
		//need to get the question id from the containing div.
		var qID = $(this).closest("div.questionDiv").attr("id");
		var questionId = qID.substring(1);
		var responseType = $(this).closest("div.questionDiv").find(".responseType").val();
		var url = pageContextPath + "/uploadFilePopup?questionId=" + questionId + "&responseType=" + responseType;
		var popup = window.open(url, "_blank", "location=no,toolbar=no,menubar=no,status=no,scrollbars=no,resizable=no,width=700,height=500");
		if (typeof popup == "undefined") {
			alert(shared.popupBlockedMessage);
			return;
		}
		popup.screenX = window.screenX + 200;
		popup.screenY = window.screenY + 100;
		popup.focus();
		shared.openPopUps.push(popup);
	}
	
	$(".uploadFileButton").each(function(index, element) {
		$(element).click(function() {
			shared.executeBasedOnActiveSession({
				active: function() { launchFileUploadPopup.call(element) },
				inactive: shared.reloadPage
			})
		});
	});
	
	$(".uploadFileClearButton").click(function(){
		$(this).closest("div.questionDiv").find("input").first().val("");
		$(this).hide();
		return false;
	});
});