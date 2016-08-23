<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
    <title>Change Group Superuser</title>
    <q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
    <script type="text/javascript">
	    $(function(){
	    	$("#submitButton").click(function(){
	    		var newSuperUser = $("input[name=newSuperuser]:checked").val();
	    		if(newSuperUser == null || newSuperUser == ''){
	    			alert("You need to select a new super user, or press cancel");
	    			return;
	    		}
	    		if(confirm("You are about to make '" + newSuperUser + "' the group superuser")){
	    			window.location.href='${pageContext.request.contextPath}/admin/processChangeSuperuser?username=' + newSuperUser + '&groupId=${group.id}' ;
	    		}
	    	});
	    	
	    	
            $("#cancelButton").click(function(){
            	window.location.href='${pageContext.request.contextPath}/admin/manageGroups?username=${adminUsername}';
            });
	    });
    </script>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="Change Group Superuser" />
		<h1>${group.name}</h1>
		<c:if test="${not empty group.organisation}" >
		    <h2>${group.organisation}</h2>
		</c:if>
		Current Group Superuser : ${adminUsername}<br/><br/>
		<table>
			<c:forEach items="${group.memberList}" var="groupMember" >
			    <c:if test="${groupMember.username ne adminUsername }">
			        <tr><td style="padding-right:20px;">${groupMember.username}</td><td style="padding-right:20px;">${groupMember.emailAddress}</td><td><input type="radio" name="newSuperuser" value="${groupMember.username}"></td></tr>
			    </c:if>
			</c:forEach>
		</table>
		
		<button id="cancelButton" type="button">Cancel</button><button type="button" id="submitButton">Submit</button>
		
	</div>	
</body>
</html>	