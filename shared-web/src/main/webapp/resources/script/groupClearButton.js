//Not yet implemented - will do so when required
//MULTISELECT_IMAGE(""),
//MULTISELECT_FILE(""),
//FILE(""),
//SITE_FILE(""),
//SPECIES_LIST(""),
//LICENSE_CONDITIONS
//
//Operates when the group clear button is clicked.  All responses in the group are set to null
$(function(){
	function clearSimpleQuestionResponseInput(questionDiv){
		$(questionDiv).find(".questionResponseInput").first().val(null);		
	};
    function clearSelectListAutoResponse(questionDiv){
		$(questionDiv).find(".responseInputSpan input").each(function(){
			$(this).val(null);
		});
	};
	function clearSelectListResponse(questionDiv){
		if( $(questionDiv).find(".ui-autocomplete-input").length > 0){
			clearSelectListAutoResponse(questionDiv);
			return;
		}
		var $sel = $(questionDiv).find("select").first();
		$sel.find("option:selected").first().attr("selected", null);
		
	};
	function clearControlledVocabSuggest(questionDiv){
		clearSelectListResponse(questionDiv);
		var $suggestSpan = $(questionDiv).find("span.responseSuggestInputSpan").first();
		$suggestSpan.find("input").first().val(null);
		$suggestSpan.hide();
	};
	function clearGeoFeatureSetResponse(questionDiv){
		$(questionDiv).find("div.geoFeatureSet .geometryInput").first().attr("value" , null);
		$(questionDiv).find("div.geoFeatureSet div.featureList").first().empty();
	};
    function clearImageResponse(questionDiv){
		$(questionDiv).find("span.responseInputSpan input[type='text']").first().val(null);
		$(questionDiv).find("span.responseInputSpan input.uploadImageClearButton").first().hide();
		$(questionDiv).find("span.imageThumbSpan").first().hide();
	};
	function clearMultiselectTextResponse(questionDiv){
		var rowArray = $(questionDiv).find("tr.multirow");
		for(var x = 0; x < rowArray.length; x++){
			if(x > 0){
				$(rowArray[x]).remove();
			}else{
				$(rowArray[x]).find(".responseInputSpan input").first().val(null);
				$(rowArray[x]).find("span.multiAddRowText").first().show();
			}
		}
	};
	function clearMultiselectTextBoxResponse(questionDiv){
		var rowArray = $(questionDiv).find("tr.multirow");
		for(var x = 0; x < rowArray.length; x++){
			if(x > 0){
				$(rowArray[x]).remove();
			}else{
				$(rowArray[x]).find(".responseInputSpan textarea").first().val(null);
				$(rowArray[x]).find("span.multiAddRowTextArea").first().show();
			}
		}
	};
	function clearMultiselectCVAutoResponse(questionDiv){
		var rowArray = $(questionDiv).find("tr.multiAutoAnswerRow");
		for(var x = 0; x < rowArray.length; x++){
			if(x > 0){
				$(rowArray[x]).remove();
			}else{
				$(rowArray[x]).find("input").first().val(null);
			}
		}
		$(questionDiv).find(".ui-autocomplete-input").first().val(null);
	};
	function clearMultiselectCVResponse(questionDiv){
		if( $(questionDiv).find(".ui-autocomplete-input").length > 0){
			clearMultiselectCVAutoResponse(questionDiv);
			return;
		}
		var rowArray = $(questionDiv).find("tr.multirow");
		for(var x = 0; x< rowArray.length; x++ ){
			if(x == 0){
				$(rowArray[x]).find("option:selected").first().attr("selected",null);
				$(rowArray[x]).find("span.multiAddRowCV").first().show();
			}else{
				$(rowArray[x]).remove();
			}
		}
	};
	
	$(".groupClearButton").click(function(){
		$(this).closest("div.questionGroup").find("div.questionDiv").each(function(){
			var responseType = $(this).find("input.responseType").first().val();
			if(responseType != null && responseType != ""){
                if(responseType === "TEXT" || 
                   responseType === "TEXT_BOX" ||
                   responseType === "DATE" ){
                	clearSimpleQuestionResponseInput(this);
				}else if(responseType === "CONTROLLED_VOCAB" || responseType === "YES_NO" ){
					clearSelectListResponse(this);
				}else if(responseType === "CONTROLLED_VOCAB_SUGGEST"){
					clearControlledVocabSuggest(this);
				}else if(responseType === "GEO_FEATURE_SET"){
					clearGeoFeatureSetResponse(this);
				}else if(responseType === "IMAGE"){
					clearImageResponse(this);
				}else if(responseType === "MULTISELECT_TEXT"){
					clearMultiselectTextResponse(this);
				}else if(responseType === "MULTISELECT_TEXT_BOX"){
					clearMultiselectTextBoxResponse(this);
				}else if(responseType === "MULTISELECT_CONTROLLED_VOCAB"){
					clearMultiselectCVResponse(this);
				}
			}
		});
		return false;
	});
});

