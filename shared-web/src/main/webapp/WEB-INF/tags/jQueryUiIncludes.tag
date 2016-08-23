<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="dontInitButtons" type="java.lang.Boolean" required="false" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery-ui-1.10.3.custom.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/script/disableEnableButton.js"></script>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/custom-theme/jquery-ui-1.10.3.custom.min.css"></link>
<c:if test="${empty dontInitButtons or dontInitButtons eq false}">
	<script type="text/javascript">
		$(function() {
			$("button").button();
		});
	</script>
</c:if>
