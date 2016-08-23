/* Original version from http://keithscode.com/tutorials/javascript/3-a-simple-javascript-password-validator.html 
 *	@param	field1Selector	the jQuery selector for the first field
 *	@param	field2Selector	the jQuery selector for the second (or confirmation) field
 *	@param	messageSelector	the jQuery selector for the element to insert messages (success/fail) into
 *	@param	nameOfThing		a string used to build the message in the format "<nameOfThing> Match!"
 */
var shared = shared || {};

shared.validateConfirmationField = function(field1Selector, field2Selector, messageSelector, nameOfThing) {
	var pass1 = $(field1Selector);
    var pass2 = $(field2Selector);
    var message = $(messageSelector);
    if(pass1.val() == pass2.val()){
        pass2.addClass("goodColour");
        message.addClass("goodColour");
        pass2.removeClass("badColour");
        message.removeClass("badColour");
        message.html(nameOfThing + " Match!");
        return true;
    }
    pass2.addClass("badColour");
    message.addClass("badColour");
    pass2.removeClass("goodColour");
    message.removeClass("goodColour");
    message.html(nameOfThing + " Do Not Match!");
    return false;
}