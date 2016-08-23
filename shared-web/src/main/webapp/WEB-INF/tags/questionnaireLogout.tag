<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="displayConfirmation" type="java.lang.Boolean" required="false" %>
<script type="text/javascript">
	var shared = shared || {};
	shared.logoutInterceptor = function(destination) {
		var displayConfirmation = ${displayConfirmation == null ? false : displayConfirmation};
		if (!displayConfirmation) {
			window.location = destination;
			return;
		}
		var popupResult = window.confirm("Are you sure you want to logout? All unsaved work will be lost.");
		if (popupResult) {
			window.location = destination;
		}
	}
</script>
<div class="logoutLinkContainer">
	<a class="logoutLinkUpper" href="#" onclick="shared.logoutInterceptor('${pageContext.request.contextPath}/j_spring_security_logout')"></a>
	<a class="logoutLinkLower" href="#" onclick="shared.logoutInterceptor('${pageContext.request.contextPath}/j_spring_security_logout')"></a>
</div>