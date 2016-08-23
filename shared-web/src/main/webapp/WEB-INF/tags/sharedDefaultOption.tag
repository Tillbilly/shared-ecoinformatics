<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="isMandatory" type="java.lang.Boolean" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:choose>
	<c:when test="${isMandatory}">
		<option value="">--Please Select--</option>
	</c:when>
	<c:otherwise>
		<option value="">N/A</option>
	</c:otherwise>
</c:choose>