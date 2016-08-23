$(function(){
	$(".multiAddRowText").click(function(){
		//locate the containing div.multirow to clone and modify.
		var $sourceMultiRow = $(this).closest("tr.multirow");
		//find the index value
		var multiIndex = $sourceMultiRow.find("div.multiRowIndex").first().html();
		var newIndexInt = parseInt(multiIndex) + 1;
		var newIndex = String(newIndexInt);
		var $newMultiRow = $sourceMultiRow.clone(true, true);
		//update the form input name  and  id
		var $origInput = $sourceMultiRow.find("span.responseInputSpan input").first();
		var searchNameStr = "multiselectAnswerList["+multiIndex+"]";
		var replaceNameStr = "multiselectAnswerList["+newIndex+"]";
		var searchIdStr = "multiselectAnswerList"+multiIndex;
		var replaceIdStr = "multiselectAnswerList"+newIndex;
		var origName = $origInput.attr("name");
		var newName = origName.replace(searchNameStr, replaceNameStr);
		var origId = $origInput.attr("id");
		var newId = origId.replace(searchIdStr, replaceIdStr);
		var $newInput = $newMultiRow.find("span.responseInputSpan input").first()
		$newInput.attr("name", newName);
		$newInput.attr("id", newId);
		$newInput.val("");
		//update the hidden index value
		$newMultiRow.find("div.multiRowIndex").first().html(newIndex);
		if(newIndexInt >= 10){
			$newMultiRow.find(".multiAddRowText").first().hide();
		}
		$newMultiRow.insertAfter($sourceMultiRow);
		$(this).hide();
	});
	
	$(".multiAddRowTextArea").click(function(){
		//locate the containing div.multirow to clone and modify.
		var $sourceMultiRow = $(this).closest("tr.multirow");
		//find the index value
		var multiIndex = $sourceMultiRow.find("div.multiRowIndex").first().html();
		var newIndexInt = parseInt(multiIndex) + 1;
		var newIndex = String(newIndexInt);
		var $newMultiRow = $sourceMultiRow.clone(true, true);
		//update the form input name  and  id
		var $origInput = $sourceMultiRow.find("span.responseInputSpan textarea").first();
		var searchNameStr = "multiselectAnswerList["+multiIndex+"]";
		var replaceNameStr = "multiselectAnswerList["+newIndex+"]";
		var searchIdStr = "multiselectAnswerList"+multiIndex;
		var replaceIdStr = "multiselectAnswerList"+newIndex;
		var origName = $origInput.attr("name");
		var newName = origName.replace(searchNameStr, replaceNameStr);
		var origId = $origInput.attr("id");
		var newId = origId.replace(searchIdStr, replaceIdStr);
		var $newInput = $newMultiRow.find("span.responseInputSpan textarea").first()
		$newInput.attr("name", newName);
		$newInput.attr("id", newId);
		$newInput.val("");
		//update the hidden index value
		$newMultiRow.find("div.multiRowIndex").first().html(newIndex);
		if(newIndexInt >= 10){
			$newMultiRow.find(".multiAddRowText").first().hide();
		}
		$newMultiRow.insertAfter($sourceMultiRow);
		$(this).hide();
	});
	
	
	$(".multiAddRowCV").click(function(){
		
		//locate the containing div.multirow to clone and modify.
		var $sourceMultiRow = $(this).closest("tr.multirow");
		//find the index value
		var multiIndex = $sourceMultiRow.find("div.multiRowIndex").first().html();
		var newIndexInt = parseInt(multiIndex) + 1;
		var newIndex = String(newIndexInt);
		var $newMultiRow = $sourceMultiRow.clone(true, true);
		//update the form input name  and  id
		var $origInput = $sourceMultiRow.find("span.responseInputSpan select").first();
		var searchNameStr = "multiselectAnswerList["+multiIndex+"]";
		var replaceNameStr = "multiselectAnswerList["+newIndex+"]";
		var searchIdStr = "multiselectAnswerList"+multiIndex;
		var replaceIdStr = "multiselectAnswerList"+newIndex;
		var origName = $origInput.attr("name");
		var newName = origName.replace(searchNameStr, replaceNameStr);
		var origId = $origInput.attr("id");
		var newId = origId.replace(searchIdStr, replaceIdStr);
		var $newSelect = $newMultiRow.find("span.responseInputSpan select").first()
		$newSelect.attr("name", newName);
		$newSelect.attr("id", newId);
		//Set the option to value ''
		$newSelect.val("");
		//update the hidden index value
		$newMultiRow.find("div.multiRowIndex").first().html(newIndex);
		if(newIndexInt >= 10){
			$newMultiRow.find(".multiAddRowText").first().hide();
		}
		$newMultiRow.insertAfter($sourceMultiRow);
		$(this).hide();
		
	});
	
	$(".multiAddRowImage").click(function(){
		//locate the containing div.multirow to clone and modify.
		var $sourceMultiRow = $(this).closest("tr.multirow");
		//find the index value
		var multiIndex = $sourceMultiRow.find("div.multiRowIndex").first().html();
		var newIndexInt = parseInt(multiIndex) + 1;
		var newIndex = String(newIndexInt);
		var $newMultiRow = $sourceMultiRow.clone(true, true);
		//update the form input name  and  id
		var $origInput = $sourceMultiRow.find("span.responseInputSpan input").first();
		var searchNameStr = "multiselectAnswerList["+multiIndex+"]";
		var replaceNameStr = "multiselectAnswerList["+newIndex+"]";
		var searchIdStr = "multiselectAnswerList"+multiIndex;
		var replaceIdStr = "multiselectAnswerList"+newIndex;
		var origName = $origInput.attr("name");
		var newName = origName.replace(searchNameStr, replaceNameStr);
		var origId = $origInput.attr("id");
		var newId = origId.replace(searchIdStr, replaceIdStr);
		var $newInput = $newMultiRow.find("span.responseInputSpan input").first()
		$newInput.attr("name", newName);
		$newInput.attr("id", newId);
		$newInput.val("");
		//update the hidden index value
		$newMultiRow.find("div.multiRowIndex").first().html(newIndex);
		if(newIndexInt >= 20){
			$newMultiRow.find(".multiAddRowText").first().hide();
		}
		//clear out the image settings
		var html = "<a target='_blank'><img class='thumbnail' /></a>";
		var $imageThumbSpan = $newMultiRow.find("span.imageThumbSpan").first();
		$imageThumbSpan.html(html);
		$imageThumbSpan.hide();
		$newMultiRow.insertAfter($sourceMultiRow);
		$(this).hide();
		
	});
	
	$(".clearNearestSharedAnswerButton").click(function() {
		$(this).parent().find(".sharedAnswer").val("");
	});
});