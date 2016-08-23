<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
    <title>User Management</title>
    <q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
    <script type="text/javascript">
	    function getRowUsernameFromButton($button){
	    	/* The username is inside td.sharedUsername from the same row  */
	    	return $($button).closest("tr").find("td.sharedUsername").first().html();
	    }
	    
	    $(function(){
	    	$(".editRolesButton").click(function(){
	    		var username = getRowUsernameFromButton(this);
	    		window.location.href='${pageContext.request.contextPath}/admin/editRoles?username=' + username;
	    	});
	    	
	        $(".disableUserButton").click(function(){
	        	var username = getRowUsernameFromButton(this);
	        	window.location.href='${pageContext.request.contextPath}/admin/disableUser?username=' + username;
	    	});
	    	
			$(".activateUserButton").click(function(){
				var username = getRowUsernameFromButton(this);
				window.location.href='${pageContext.request.contextPath}/admin/activateUser?username=' + username;
			});
			$(".resetPasswordButton").click(function(){
				var username = getRowUsernameFromButton(this);
				window.location.href='${pageContext.request.contextPath}/admin/resetPassword?username=' + username;
			});
			$(".editGroupButton").click(function(){
				var username = getRowUsernameFromButton(this);
				window.location.href='${pageContext.request.contextPath}/admin/manageGroups?username=' + username;
			});
			$("#homeButton").click(function(){
				window.location.href='${pageContext.request.contextPath}';
			});
	    });
    </script>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="User Management" />
		<div>
			<c:if test="${not empty message}">
				<div class="infoBox msgBoxSpacing centeredContent">${message}</div>
			</c:if>
			<h2 class="centeredContent">Registered Users</h2>
			<table class="sharedTable">
				<thead>
					<tr>
						<th>Username</th>
						<th>Full name</th>
						<th>Email Address</th>
						<th>Ph:</th>
						<th>Roles</th>
						<th>Active</th>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<c:forEach items="${userList}" var="user">
					<tr>
						<td class="sharedUsername">${user.username}</td>
						<td>${user.fullName}</td>
						<td>${user.emailAddress}</td>
						<td>${user.phoneNumber}</td>
						<td>
							<c:if test="${not empty user.roles}">
								<ul class="roleList">
									<c:forEach items="${user.roles}" var="role">
										<li>${role.sharedRole}</li>
									</c:forEach>
								</ul>
							</c:if>
						</td>
						<td>${user.enabled}</td>
						<td><button class="editRolesButton">Edit Roles</button></td>
						<td>
							<c:choose>
								<c:when test="${user.username eq 'admin' }">
									<!-- Can't disable the admin user -->
								</c:when>
								<c:when test="${user.enabled }">
									<button class="disableUserButton">Disable</button>
								</c:when>
								<c:otherwise>
									<button class="activateUserButton">Activate</button>
								</c:otherwise>
							</c:choose>
						</td>
						<td>
						    <c:if test="${user.isGroupAdministrator }">
							    <button class="editGroupButton">Group</button>
							</c:if>
						</td>
						<td>
							<c:choose>
							    <c:when test="${empty user.aafUser or not user.aafUser }">
								    <button class="resetPasswordButton">Reset Password</button>
								</c:when>
								<c:otherwise>AAF</c:otherwise>
							</c:choose>	
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
</body>
</html>