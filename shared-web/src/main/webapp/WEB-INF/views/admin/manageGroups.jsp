<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
    <title>Manage User Groups</title>
    <q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
    
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="Manage User Groups" />
		<c:if test="${not empty message }"><span class="errorMessage">${message}</span><br/></c:if>
		<h3>Manage user groups for user ${groupAdminUsername}</h3>
		
		<hr>
		<c:if test="${not empty groupList }" >
		    <c:forEach items="${groupList}" var="group" >
		         Name:${group.name}<br/>
		         Org: ${group.organisation }<br/>
		         Peer Review: ${group.peerReviewActive}<br/>    
		         <b>Members</b><br/>
		         <table>
		             <tr><th>Username</th><th>Email</th></tr>
		             <c:forEach items="${group.memberList}" var="user" >
		                 <tr><td>${user.username }</td><td>${user.emailAddress}</td></tr>
		             </c:forEach>
		         </table><br/>
		         <a href="${pageContext.request.contextPath}/admin/deleteGroup?groupId=${group.id}&adminUsername=${groupAdminUsername}">Delete Group</a>
		         <a href="${pageContext.request.contextPath}/admin/editGroupMembers?groupId=${group.id}&adminUsername=${groupAdminUsername}">Edit Members</a><br/>
		         <c:choose>
		           <c:when test="${empty group.peerReviewActive or not group.peerReviewActive }">
		             <a href="${pageContext.request.contextPath}/admin/togglePeerReview?groupId=${group.id}&adminUsername=${groupAdminUsername}&peerReview=true">Enable Peer Review</a>
		           </c:when>
		           <c:otherwise>
		             <a href="${pageContext.request.contextPath}/admin/togglePeerReview?groupId=${group.id}&adminUsername=${groupAdminUsername}&peerReview=false">Disable Peer Review</a>
		           </c:otherwise>
		         </c:choose>
		         <a href="${pageContext.request.contextPath}/admin/changeGroupSuperuser?groupId=${group.id}&adminUsername=${groupAdminUsername}">Change Group Superuser</a>
		        <hr>
		    </c:forEach>
		</c:if>
		<form:form commandName="newGroup" action="${pageContext.request.contextPath}/admin/addGroup/${groupAdminUsername}" method="POST" >
		    Group Name:<form:input path="name" /><br/>
		    Organisation:<form:input path="organisation" /><br/>
		
		
		    <button type="submit" id="addGroupButton">Add Group</button>
		</form:form>
		<br/>
	</div>
</body>
</html>		    