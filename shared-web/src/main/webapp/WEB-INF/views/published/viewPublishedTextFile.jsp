<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false"%>
<html>
	<head>
	    <title>Published Submission Metadata Text File ${fileViewModel.submissionId} ${fileViewModel.submissionDataId}</title>
	</head>
	<body>
	<c:choose>
	    <c:when test="${not empty fileViewModel.errorMessage }" >
	        <div>The request failed for the following reason:</div>
	        <div>${fileViewModel.errorMessage}</div>
	    </c:when>
	    <c:otherwise>
	        <div>${fileViewModel.filename} ${fileViewModel.dataFileType} ${fileViewModel.submissionTitle}</div>
	        <br/>
	        <c:forEach items="${fileViewModel.lines}" var="line">
	            <div class="viewFileLineDiv">${line}</div>
	        </c:forEach>
	    </c:otherwise>
	</c:choose>
	</body>
</html>