var shared = shared || {};
shared.openPopUps = shared.openPopUps || [];

function updateUploadedImage(questionId, imageName ,uploadedThumbImgSrc, uploadedImageHref){
	//find the question div
	var divId = "Q" + questionId;
	var qDivId = divId.replace(/\./g,'\\.');
	var $qDiv = $('div[id="'+qDivId+'"]').first();
	$qDiv.find("input").first().val(imageName);
	$qDiv.find(".uploadImageClearButton").first().show();
	var $imgThumbSpan = $qDiv.find("span.imageThumbSpan").first();
	$imgThumbSpan.find("img").first().attr("src","../" + uploadedThumbImgSrc);
	$imgThumbSpan.find("a").first().attr("href", "../" + uploadedImageHref);
	$imgThumbSpan.show();
};

function updateUploadedImageMulti(questionId, index, imageName ,uploadedThumbImgSrc, uploadedImageHref){
	//TODO Make this work multiselect style.
	
	//find the question div
	var divId = "Q" + questionId;
	var qDivId = divId.replace(/\./g,'\\.');
	var $qDiv = $('div[id="'+qDivId+'"]').first();
	
	//Need to locate the index div in the row of the multiselect table
	var $indexSpan = $qDiv.find("div.multiRowIndex:contains('" + index + "')").first();
	var $responseInputSpan = $indexSpan.siblings("span.responseInputSpan" ).first();
	$responseInputSpan.find("input.msImageInput").first().val(imageName);
	$responseInputSpan.find(".uploadMultiImageClearButton").first().show();
	
	var $imgThumbSpan = $indexSpan.siblings("span.imageThumbSpan").first();
	$imgThumbSpan.find("img").first().attr("src","../" + uploadedThumbImgSrc);
	$imgThumbSpan.find("a").first().attr("href", "../" + uploadedImageHref);
	$imgThumbSpan.show();
	
};

$(function(){
	function launchImageUploadPopup(){
		//need to get the question id from the containing div.
		var qID = $(this).closest("div.questionDiv").attr("id");
		var questionId = qID.substring(1);
		var responseType = $(this).closest("div.questionDiv").find(".responseType").val();
		var url = pageContextPath + "/uploadImagePopup?questionId=" + questionId + "&responseType=" + responseType;
		var popup = window.open(url, "Image Upload", "location=0,toolbar=no,menubar=no,status=no,scrollbars=no,resizable=no,width=700,height=400");
		if (typeof popup == "undefined") {
			alert(shared.popupBlockedMessage);
			return;
		}
		popup.screenX = window.screenX + 200;
		popup.screenY = window.screenY + 100;
		popup.focus();
		shared.openPopUps.push(popup);
	}
	
	$(".uploadImageButton").each(function(index, element) {
		$(element).click(function() {
			shared.executeBasedOnActiveSession({
				active: function() { launchImageUploadPopup.call(element) },
				inactive: shared.reloadPage
			})
		});
	});
	
	$(".uploadImageClearButton").click(function(){
		$(this).closest("div.questionDiv").find("input").first().val("");
		$(this).closest("div.questionDiv").find("span.imageThumbSpan").hide();
		$(this).hide();
	});
	
	/* Multiselect images */
	function launchMultiImageUploadPopup(){
		//need to get the question id from the containing div.
		var qID = $(this).closest("div.questionDiv").attr("id");
		var questionId = qID.substring(1);
		var responseType = $(this).closest("div.questionDiv").find(".responseType").val();
		//Get the index of the multiselect row
		var multiIndex = $(this).closest("td.multiAnswerCell").find("div.multiRowIndex").html();
		var url = pageContextPath + "/uploadImagePopupMulti?questionId=" + questionId + "&responseType=" + responseType + "&index=" + multiIndex;
		var popup = window.open(url, "Image Upload", "location=0,toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=no,width=700,height=400");
		if (typeof popup == "undefined") {
			alert(shared.popupBlockedMessage);
			return;
		}
		popup.screenX = window.screenX + 200;
		popup.screenY = window.screenY + 100;
		popup.focus();
		shared.openPopUps.push(popup);
	}
	
	$(".uploadMultiImageButton").each(function(index, element) {
		$(element).click(function() {
			shared.executeBasedOnActiveSession({
				active: function() { launchMultiImageUploadPopup.call(element) },
				inactive: shared.reloadPage
			})
		});
	});
	
	$(".uploadMultiImageClearButton").click(function(){
		$(this).closest("span.responseInputSpan").find("input").first().val("");
		$(this).closest("span.responseInputSpan").siblings("span.imageThumbSpan").hide();
		$(this).hide();
	});
	
});