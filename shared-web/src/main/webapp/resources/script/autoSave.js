//Autosave removed for the time being
function initAutosave(formSelector, saveButtonSelector, currentPageNumber) {
	var FIVE_MINUTES = 5 * 60 * 1000;
	var ALL_ANSWERS_SELECTOR = ".sharedAnswer";
	var activityTimeout;

	function saveData() {
		$(saveButtonSelector).html('Saving...');
		shared.disableButton(saveButtonSelector);
		var formData = $(formSelector).serialize();
		$.post(pageContextPath + '/questionnaire/autosave/' + currentPageNumber, formData, function(data, textStatus, jqXHR) {
        	var resp = JSON.parse(data);
			if (resp.success) {
        		console.log('Autosave completed successfully');
        		$(saveButtonSelector).html('Saved');
        	} else {
        		console.error('autosave failed: ' + resp.failureCause);
        		$(saveButtonSelector).html('Autosave failed');
        	}
        });
	}

	function resetAutoSaveTimer() {
		$(saveButtonSelector).html('Save');
		shared.enableButton(saveButtonSelector);
		if (activityTimeout) {
			clearTimeout(activityTimeout);
		}
		activityTimeout = setTimeout(saveData, FIVE_MINUTES);
	}

	$(ALL_ANSWERS_SELECTOR)
	.keypress(function() {
		resetAutoSaveTimer();
	})
	.change(function() {
		resetAutoSaveTimer();
	});
}