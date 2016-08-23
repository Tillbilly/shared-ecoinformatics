//Help icon utilises the jQuery qTip2 library http://craigsworks.com/projects/qtip2/

$(function() {
	// Original version from http://stackoverflow.com/a/822486
	function containsVisibleContent(html) {
	   var tmpDiv = document.createElement("DIV");
	   tmpDiv.innerHTML = html;
	   var innerText = tmpDiv.textContent || tmpDiv.innerText || "";
	   return innerText.trim().length > 0;
	}
	
	$('.helpIcon').qtip({
		content : {
			text : function(api) {
				var htmlContent = $(this).next(".helpText").html();
				if (containsVisibleContent(htmlContent)) {
					return htmlContent;
				}
				return "(No help available)";
			},
			title : {
				text : function(api) {
					return $(this).attr("alt");
				},
				button : true
			}
		},
		show : 'click',
		hide : 'unfocus',
		position : {
			my : 'left bottom',
			at : 'top right',
			target : false,
			adjust: {
	            method: 'flipinvert'
	        },
	        viewport: $(window)
		},
		style : {
			widget : false,
			classes: 'qtip-green',
			tip: {
				corner: true
			}
		}
	});
});