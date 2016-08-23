<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="buttonText" type="java.lang.String" required="true" %>
<script type="text/javascript">
	$(function() {
		$("#licenseCheckbox").change(function(){
			var startQuestButton = $('#startQuestButton');
			if ( $(this).attr("checked") == "checked" ){
				shared.enableButton(startQuestButton);
				return
			}
			shared.disableButton(startQuestButton);
		});
		$("#startQuestButton").click(function(){
			window.location="${pageContext.request.contextPath}/questionnaire/1?new=true";
		});
	});
</script>
<style type="text/css">
	#startQuestButton {
		font-size: 1.2em;
		margin-top: 1em;
	}
	
	.twoColLeft {
		width: 5%;
	}
	
	label.twoColRight {
		display: block;
	}
</style>
<div>
	<div class="twoColLeft">
		<input id="licenseCheckbox" type="checkbox" name="licenseCheckbox" />
	</div>
	<label class="twoColRight lessImportantText" for="licenseCheckbox">
		I confirm that I am the rights holder or have the permission of the rights holder to submit this data under an open access licence.
	</label>
</div>
<div class="centeredContent">
	<button id="startQuestButton" disabled="disabled">${buttonText}</button>
</div>