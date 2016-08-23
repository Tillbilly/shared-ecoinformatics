function replaceListIndex(nameAttr, listIndex ){
	return nameAttr.replace(/multiselectAnswerList\[.+\].response/ , "multiselectAnswerList\["+listIndex.toString()+"].response");
}

function reindexMultiAnswerNameAttribute( $answerTbl ){
    var inputArray = $answerTbl.find("input");
	for(var i=0; i < inputArray.length;i++){
		var nameAttr = $(inputArray[i]).attr("name");
		$(inputArray[i]).attr("name", replaceListIndex(nameAttr, i));
	}
}

$(function(){
	function buildSharedAutocompleteUrl(suffix) {
		return pageContextPath + "/questionnaire/vocab/autocomplete/" + suffix;
	}
	
	$(".autoInputClearIcon").click(function(){
		$(this).prev("input").val("");
	});
   
   $(".autoInputClearIconMS").click(function(){
	   var $answerTbl = $(this).parents("table.multiAutoAnswerTable").first();
		$(this).parents("tr.multiAutoAnswerRow").first().remove();
		reindexMultiAnswerNameAttribute( $answerTbl );
	});
   
   
   $(".autoInputClearIconMSFirst").click(function(){
	   //if there is only one answer row, just clear the input,
	   //Otherwise, need to copy all of the answers up 1 position
	   //and remove the last row.
	   
	   //find the parent table element
	   $tbl = $(this).parents("table.multiAutoAnswerTable");
	   $inputArray  = $tbl.find("input");
	   //determine number of rows
	   if($inputArray.length == 1){
		   $($inputArray[0]).val(null);
	   }else{
		   for(var x = 1;x < $inputArray.length; x++){
			   $($inputArray[x - 1]).val( $($inputArray[x]).val() );
		   }
		   $tbl.find("tr.multiAutoAnswerRow").last().remove();
	   }	
	});
   
   
	//jqueryui autocomplete widget
	var el = $( ".autocomplete_taxon_names" ).autocomplete({
		source: buildSharedAutocompleteUrl("taxon_names"),
		minLength: 2,
		select: function( event, ui ) {
			var val = ui.item ? ui.item.value : null;
			$(event.target).next("input").val(val);
			return false;
		}
		});
	
	if(el && $(el).data("uiAutocomplete")){
		$(el).data("uiAutocomplete")._renderItem = function( ul, item ) {  
	        return $( "<li></li>" )
            .data( "item.autocomplete", item )
            .append($( "<a></a>" ).html(item.label) )
            .appendTo( ul );
        };
	}
		
    el = $( ".autocomplete_taxon_names_multi" ).autocomplete({
		source: buildSharedAutocompleteUrl("taxon_names"),
		minLength: 2,
		select: function( event, ui ) {
			var val = ui.item ? ui.item.value : null;
			if(val){
				var $ansTbl = $(event.target).parents("table").find("table.multiAutoAnswerTable").first();
				//determine next row index
				var rowsArray = $ansTbl.find("tr.multiAutoAnswerRow"); 
				var newIdx = rowsArray.length ;
				//if there is only one row, and the value is currently null, just assign the value
				if(newIdx == 1 && 
						( $(rowsArray[0]).find("input").first().val() == null || $(rowsArray[0]).find("input").first().val() == "" ) ){
					var $inp = $(rowsArray[0]).find("input").first();
					$inp.val(val);
					return false;
				}
				var $newRow;
				//if we are cloning the top row, need to change the click event and class of the img attribute
				if(newIdx == 1){
					$newRow = $(rowsArray[newIdx - 1]).clone();
					var $imgElement = $newRow.find("img").first();
					$imgElement.removeClass("autoInputClearIconMSFirst").addClass("autoInputClearIconMS");
					$imgElement.click(function(){
						//find parent table, and row, remove the row from the dom, reindex the 'name' 
						//attributes of inputs in the row.
						var $answerTbl = $(this).parents("table.multiAutoAnswerTable").first();
						$(this).parents("tr.multiAutoAnswerRow").first().remove();
						reindexMultiAnswerNameAttribute( $answerTbl );
					});
				}else{
					$newRow = $(rowsArray[newIdx - 1]).clone(true);
				}
				$answerInput = $newRow.find("input").first();
				$answerInput.val(val);
				$answerInput.attr("name", replaceListIndex($answerInput.attr("name"), newIdx ) );
				$ansTbl.append($newRow);
			}
			
			return false;
		}
		});
		
    if(el && $(el).data("uiAutocomplete")){
		$(el).data("uiAutocomplete")._renderItem = function( ul, item ) {  
	        return $( "<li></li>" )
            .data( "item.autocomplete", item )
            .append($( "<a></a>" ).html(item.label) )
            .appendTo( ul );
        };
	}
    
    
   el = $( ".autocomplete_fauna_taxon_names" ).autocomplete({
		source: buildSharedAutocompleteUrl("fauna_taxon_names"),
		minLength: 2,
		select: function( event, ui ) {
			var val = ui.item ? ui.item.value : null;
			$(event.target).next("input").val(val);
			return false;
		}
		});
	
	if(el && $(el).data("uiAutocomplete")){
		$(el).data("uiAutocomplete")._renderItem = function( ul, item ) {  
	        return $( "<li></li>" )
            .data( "item.autocomplete", item )
            .append($( "<a></a>" ).html(item.label) )
            .appendTo( ul );
        };
	}
		
    el = $( ".autocomplete_fauna_taxon_names_multi" ).autocomplete({
    	source: buildSharedAutocompleteUrl("fauna_taxon_names"),
		minLength: 2,
		select: function( event, ui ) {
			var val = ui.item ? ui.item.value : null;
			if(val){
				var $ansTbl = $(event.target).parents("table").find("table.multiAutoAnswerTable").first();
				//determine next row index
				var rowsArray = $ansTbl.find("tr.multiAutoAnswerRow"); 
				var newIdx = rowsArray.length ;
				//if there is only one row, and the value is currently null, just assign the value
				if(newIdx == 1 && 
						( $(rowsArray[0]).find("input").first().val() == null || $(rowsArray[0]).find("input").first().val() == "" ) ){
					var $inp = $(rowsArray[0]).find("input").first();
					$inp.val(val);
					return false;
				}
				var $newRow;
				//if we are cloning the top row, need to change the click event and class of the img attribute
				if(newIdx == 1){
					$newRow = $(rowsArray[newIdx - 1]).clone();
					var $imgElement = $newRow.find("img").first();
					$imgElement.removeClass("autoInputClearIconMSFirst").addClass("autoInputClearIconMS");
					$imgElement.click(function(){
						//find parent table, and row, remove the row from the dom, reindex the 'name' 
						//attributes of inputs in the row.
						var $answerTbl = $(this).parents("table.multiAutoAnswerTable").first();
						$(this).parents("tr.multiAutoAnswerRow").first().remove();
						reindexMultiAnswerNameAttribute( $answerTbl );
					});
				}else{
					$newRow = $(rowsArray[newIdx - 1]).clone(true);
				}
				$answerInput = $newRow.find("input").first();
				$answerInput.val(val);
				$answerInput.attr("name", replaceListIndex($answerInput.attr("name"), newIdx ) );
				$ansTbl.append($newRow);
			}
			
			return false;
		}
		});
		
    if(el && $(el).data("uiAutocomplete")){
		$(el).data("uiAutocomplete")._renderItem = function( ul, item ) {  
	        return $( "<li></li>" )
            .data( "item.autocomplete", item )
            .append($( "<a></a>" ).html(item.label) )
            .appendTo( ul );
        };
	}
    
    
    
    el = $( ".autocomplete_common_fauna_names" ).autocomplete({
		source: buildSharedAutocompleteUrl("common_fauna_names"),
		minLength: 2,
		select: function( event, ui ) {
			var val = ui.item ? ui.item.value : null;
			$(event.target).next("input").val(val);
			return false;
		}
		});
	
	if(el && $(el).data("uiAutocomplete")){
		$(el).data("uiAutocomplete")._renderItem = function( ul, item ) {  
	        return $( "<li></li>" )
            .data( "item.autocomplete", item )
            .append($( "<a></a>" ).html(item.label) )
            .appendTo( ul );
        };
	}
		
    el = $( ".autocomplete_common_fauna_names_multi" ).autocomplete({
		source: buildSharedAutocompleteUrl("common_fauna_names"),
		minLength: 2,
		select: function( event, ui ) {
			var val = ui.item ? ui.item.value : null;
			if(val){
				var $ansTbl = $(event.target).parents("table").find("table.multiAutoAnswerTable").first();
				//determine next row index
				var rowsArray = $ansTbl.find("tr.multiAutoAnswerRow"); 
				var newIdx = rowsArray.length ;
				//if there is only one row, and the value is currently null, just assign the value
				if(newIdx == 1 && 
						( $(rowsArray[0]).find("input").first().val() == null || $(rowsArray[0]).find("input").first().val() == "" ) ){
					var $inp = $(rowsArray[0]).find("input").first();
					$inp.val(val);
					return false;
				}
				var $newRow;
				//if we are cloning the top row, need to change the click event and class of the img attribute
				if(newIdx == 1){
					$newRow = $(rowsArray[newIdx - 1]).clone();
					var $imgElement = $newRow.find("img").first();
					$imgElement.removeClass("autoInputClearIconMSFirst").addClass("autoInputClearIconMS");
					$imgElement.click(function(){
						//find parent table, and row, remove the row from the dom, reindex the 'name' 
						//attributes of inputs in the row.
						var $answerTbl = $(this).parents("table.multiAutoAnswerTable").first();
						$(this).parents("tr.multiAutoAnswerRow").first().remove();
						reindexMultiAnswerNameAttribute( $answerTbl );
					});
				}else{
					$newRow = $(rowsArray[newIdx - 1]).clone(true);
				}
				$answerInput = $newRow.find("input").first();
				$answerInput.val(val);
				$answerInput.attr("name", replaceListIndex($answerInput.attr("name"), newIdx ) );
				$ansTbl.append($newRow);
			}
			
			return false;
		}
		});
		
    if(el && $(el).data("uiAutocomplete")){
		$(el).data("uiAutocomplete")._renderItem = function( ul, item ) {  
	        return $( "<li></li>" )
            .data( "item.autocomplete", item )
            .append($( "<a></a>" ).html(item.label) )
            .appendTo( ul );
        };
	}

    el = $( ".autocomplete_common_flora_names" ).autocomplete({
		source: buildSharedAutocompleteUrl("common_flora_names"),
		minLength: 2,
		select: function( event, ui ) {
			var val = ui.item ? ui.item.value : null;
			$(event.target).next("input").val(val);
			return false;
		}
		});
	
	if(el && $(el).data("uiAutocomplete")){
		$(el).data("uiAutocomplete")._renderItem = function( ul, item ) {  
	        return $( "<li></li>" )
            .data( "item.autocomplete", item )
            .append($( "<a></a>" ).html(item.label) )
            .appendTo( ul );
        };
	}
		
    el = $( ".autocomplete_common_flora_names_multi" ).autocomplete({
		source: buildSharedAutocompleteUrl("common_flora_names"),
		minLength: 2,
		select: function( event, ui ) {
			var val = ui.item ? ui.item.value : null;
			if(val){
				var $ansTbl = $(event.target).parents("table").find("table.multiAutoAnswerTable").first();
				//determine next row index
				var rowsArray = $ansTbl.find("tr.multiAutoAnswerRow"); 
				var newIdx = rowsArray.length ;
				//if there is only one row, and the value is currently null, just assign the value
				if(newIdx == 1 && 
						( $(rowsArray[0]).find("input").first().val() == null || $(rowsArray[0]).find("input").first().val() == "" ) ){
					var $inp = $(rowsArray[0]).find("input").first();
					$inp.val(val);
					return false;
				}
				var $newRow;
				//if we are cloning the top row, need to change the click event and class of the img attribute
				if(newIdx == 1){
					$newRow = $(rowsArray[newIdx - 1]).clone();
					var $imgElement = $newRow.find("img").first();
					$imgElement.removeClass("autoInputClearIconMSFirst").addClass("autoInputClearIconMS");
					$imgElement.click(function(){
						//find parent table, and row, remove the row from the dom, reindex the 'name' 
						//attributes of inputs in the row.
						var $answerTbl = $(this).parents("table.multiAutoAnswerTable").first();
						$(this).parents("tr.multiAutoAnswerRow").first().remove();
						reindexMultiAnswerNameAttribute( $answerTbl );
					});
				}else{
					$newRow = $(rowsArray[newIdx - 1]).clone(true);
				}
				$answerInput = $newRow.find("input").first();
				$answerInput.val(val);
				$answerInput.attr("name", replaceListIndex($answerInput.attr("name"), newIdx ) );
				$ansTbl.append($newRow);
			}
			
			return false;
		}
		});
		
    if(el && $(el).data("uiAutocomplete")){
		$(el).data("uiAutocomplete")._renderItem = function( ul, item ) {  
	        return $( "<li></li>" )
            .data( "item.autocomplete", item )
            .append($( "<a></a>" ).html(item.label) )
            .appendTo( ul );
        };
	}
    
});