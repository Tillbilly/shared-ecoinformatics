<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
    <title>SHaRED User Password Updated</title>
    <q:sharedCommonIncludes />
	<script type="text/javascript">
	$(function(){
		setInterval(function(){
			window.location.href='${pageContext.request.contextPath}';
		}, 10000);
	});
	</script>
	
</head>
<body class="sharedBodyBg">
	<div class="main-content centeredContent">
		<q:sharedSmallLogo />
		<h1>Change Details</h1>
	    <div class="infoBox msgBoxSpacing">
	    	<p>Password updated!</p>
		    <a href="${pageContext.request.contextPath}"><button>Home</button></a>
    	</div>
	</div>
</body>
</html>