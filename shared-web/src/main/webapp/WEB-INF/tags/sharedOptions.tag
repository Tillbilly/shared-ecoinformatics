<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="items" type="java.util.Collection" required="true" %>
<%@ attribute name="itemLabel" type="java.lang.String" required="true" %>
<%@ attribute name="itemValue" type="java.lang.String" required="true" %>
<%@ attribute name="itemTitle" type="java.lang.String" required="true" %>
<%@ attribute name="selectedValue" type="java.lang.String" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:forEach var="entry" items="${items}">
	<c:set var="selectedAttrString" value="${entry[itemValue] == selectedValue ? 'selected=\"selected\"' : ''}" />
	<option value="${entry[itemValue]}" title="${entry[itemTitle]}" ${selectedAttrString}>${entry[itemLabel]}</option>
</c:forEach>