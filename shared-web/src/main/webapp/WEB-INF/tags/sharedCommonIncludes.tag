<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<meta http-equiv="X-UA-Compatible" content="IE=Edge">
<link rel="shortcut icon" href="${pageContext.request.contextPath}/images/favicon.ico">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/shared-web.css?version=${sharedVersion}">
<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery-1.7.2.min.js"></script>
<script type="text/javascript">
    //Global javascript page context path variable to be used in the included javascript files
    var pageContextPath ="${pageContext.request.contextPath}";
    var console = console || 
	{
		log : function() {},
		info : function() {},
		warn : function() {},
		error: function() {}
	};
</script>
<spring:eval expression="@sharedWebProperties.getProperty('shared.version')" var="sharedVersion" />