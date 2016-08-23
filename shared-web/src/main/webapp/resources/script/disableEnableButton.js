var shared = shared || {};
shared.enableButton = function(buttonSelector) {
	try {
		$(buttonSelector).button("enable");
	} catch (err) {
		$(buttonSelector).prop('disabled', false);
	}
};
shared.disableButton = function(buttonSelector) {
	try {
		$(buttonSelector).button("disable");
	} catch (err) {
		$(buttonSelector).prop('disabled', true);
	}
};