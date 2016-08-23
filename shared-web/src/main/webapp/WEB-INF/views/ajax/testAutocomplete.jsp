<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="false" %>
<html>
<head>
	<title>SHaRED Home</title>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
	<style>
		.ui-autocomplete-loading {
			background: white url('${pageContext.request.contextPath}/images/ui-anim_basic_16x16.gif') right center no-repeat;
		}
		.ui-autocomplete {
			max-height: 500px;
			overflow-y: auto;
			/* prevent horizontal scrollbar */
			overflow-x: hidden;
	    }
	</style>
	<script type="text/javascript">
	$(function(){
		$( "#taxonNamesInput" ).autocomplete({
			source: "${pageContext.request.contextPath}/questionnaire/vocab/autocomplete/taxon_names",
			minLength: 2,
			select: function( event, ui ) {
				log( ui.item ?
					"Selected: " + ui.item.value :
					"Nothing selected, input was " + this.value );
			}
		}).data('uiAutocomplete')._renderItem = function( ul, item ) {  //overwrites original _renderItem to not escape html markup in label strings
		        return $( "<li></li>" )
		            .data( "item.autocomplete", item )
		            .append($( "<a></a>" ).html(item.label) )
		            .appendTo( ul );
        };
				
		function log( message ) {
			$( "<div>" ).text( message ).prependTo( "#log" );
			$( "#log" ).scrollTop( 0 );
		}
		
	});
	</script>
</head>
<body>
<a style="text-decoration:none;"><em>t<strong>es</strong>t</em></a>

<div class="ui-widget">
	<input id="taxonNamesInput" />
</div>
<div class="ui-widget" style="margin-top: 2em; font-family: Arial;">
	Result:
	<div id="log" style="height: 200px; width: 300px; overflow: auto;" class="ui-widget-content"></div>
</div>


</body>