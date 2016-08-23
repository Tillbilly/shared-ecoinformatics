<%@ tag language="java" pageEncoding="ISO-8859-1" trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ attribute name="maxLength" type="java.lang.Integer" required="true" %>
<%@ attribute name="val" type="java.lang.String" required="true" %>
${fn:substring(val, 0, maxLength)}<c:if test="${fn:length(val) gt maxLength}">...</c:if>