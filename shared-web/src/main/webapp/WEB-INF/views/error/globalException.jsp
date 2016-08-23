<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="true"%>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Oops - an error has occured</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
	<style type="text/css">
		#menuTable {
			margin-top: 1em;
			margin-bottom: 1em;
		}
		
		html {
			height: 100%;
		}
		
		.main-content {
			border-radius: 10px;
		}
		
		#wrapper {
			display:table;
			width: 100%;
			height: 66%;
		}
		
		#cell {
			display: table-cell;
			vertical-align: middle;
		}
	</style>
</head>
<body class="sharedBodyBg">
	
	<div id="wrapper">
		<div id="cell">
			<div class="main-content">
				<h2>Oh no! An error has occurred!</h2>
				<p>An error has occured while trying to access: ${url}</p>
			    <c:if test="${ not empty exception}">
			        <p>
			        ${exception.message} 
			        </p>
			    </c:if><br/>
				<p>
				SHaRED system operations staff have been notified.<br/>
				If you can, click the browser back button and save your work.<br/>
				Otherwise, click <a href="${pageContext.request.contextPath}">here</a> to go to the main menu.
				</p>
				<br/>
				<p class="centeredContent lessImportantText">
					Please contact support at <a href="mailto:enquiry@aekos.org.au">enquiry@aekos.org.au</a>.
				</p>
				
				<q:brandingBlurb />
			</div>
		</div>
	</div>
</body>
</html>
