<%@page import="au.edu.aekos.shared.service.security.SecurityServiceImpl"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>View Group Submissions</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
	<style type="text/css">
		table.displayTable td {
			vertical-align: middle;
		}
	</style>
	<script type="text/javascript">
	$(function(){
		$(".manageControlReportsButton").each(function(){
	    	$(this).click(function(){
	    		var submissionId = $(this).closest("tr").find("td.submissionIdCell").first().html();
	    		window.location="${pageContext.request.contextPath}/reports/publicationCertificate?submissionId=" + submissionId;
	    	});
	    });
	});
	
	</script>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader titleText="View Group Submissions" includeHomeButton="true" />
		<div class="centeredContent">
			<h2 class="noMargin">Group Administrator: <span class="focusText">${groupAdminUsername}</span></h2>
		</div>
		<!-- Iterate over the groupSubmissionMap -->
		<c:forEach items="${ groupSubmissionMap}" var="entry" varStatus="indx">
		    <h2>Group Name: ${entry.key.name }</h2>
		    <c:if test="${not empty entry.key.organisation }">
		        <h3>Organisation: ${entry.key.organisation }</h3>
		    </c:if>
		    <c:set var="submissionList" value="${entry.value}" scope="request"  />
			<div>
			    <display:table name="submissionList" id="sub" class="displayTable" requestURI="viewGroupSubmissions" htmlId="table${indx.index}">
					<display:setProperty name="basic.empty.showtable" value="true" />
			        <display:column title="ID" sortable="true" >
			        	${sub.id}
		        	</display:column>
		        	<display:column title="Submitter" sortable="true" >
			        	${sub.submitter.username}
		        	</display:column>
			        <display:column title="Submission Title" sortable="true" >
			            <c:choose>
				            <c:when test="${sub.status.deleted}" >
				                ${sub.title}
				            </c:when>
				            <c:otherwise>
				                <a href="${pageContext.request.contextPath}/viewSubmissionGroupAdmin?submissionId=${sub.id}">${sub.title}</a>
				            </c:otherwise>
			            </c:choose>
			        </display:column>
			        <display:column title="Created Date" sortProperty="submissionDate" sortable="true" >
			        	<fmt:formatDate pattern="MMM d, yyyy HH:mm" value="${sub.submissionDate}" />
		        	</display:column>
			        <display:column title="Status" property="status" sortable="true"/>
			        <display:column title="Last review" sortProperty="lastReviewDate" sortable="true">
			            <fmt:formatDate pattern="MMM d, yyyy HH:mm" value="${sub.lastReviewDate}" />
			        </display:column>
			        <display:column title="Controls">
			        	
		                <c:set var="reportButtonVisibility" value="${sub.status.publicationCertificateAvailable ? '' : 'invisible'}" />
		                <button class="manageControlReportsButton ${reportButtonVisibility}">
			                <img title="View Publication Certificate" src="${pageContext.request.contextPath}/images/icon_report.png"/>
			                <br />Certificate
		                </button>
		            </display:column>
			        <display:column headerClass="hiddenColumn" class="submissionIdCell hiddenColumn">${sub.id}</display:column>
			    </display:table>
			</div>
		
		</c:forEach>
		<hr>
	</div>
</body>
</html>