<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
    <title>Select User Roles</title>
    <q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/jquery-picklist.css"></link>
    <script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery-picklist.js"></script>
    <script type="text/javascript">
    $(function(){
 		$("#roleSelect").pickList();
 		
 		$("#cancel").click(function(){
 			window.location.href='${pageContext.request.contextPath}/admin/userManagement';
 		});
 		
        $("#finished").click(function(){
 			var $selectedRoleLiArray = $("ul.pickList_targetList li.pickList_listItem");
 			if($selectedRoleLiArray != null && $selectedRoleLiArray.length > 0){
 				var roleString = "";
 				for(var x = 0; x < $selectedRoleLiArray.length ; x++){
 					var $li = $selectedRoleLiArray[x];
 					var rolename = $($li).html();
 					roleString = roleString + rolename ;
 					if( x < $selectedRoleLiArray.length - 1){
 						roleString = roleString + ',';
 					}
 				}
 				window.location.href='${pageContext.request.contextPath}/admin/processEditRoles?username=${user.username}&roles=' + roleString ;
 			}else{
 				window.location.href='${pageContext.request.contextPath}/admin/processEditRoles?username=${user.username}&removeAllRoles=true';
 			}
 		});
    });
    </script>
    <style type="text/css">
    	ul.pickList_list {
    		width: 230px;
    	}
    </style>
</head>
<body class="sharedBodyBg">
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="Edit User Roles" />
		<div class="centeredContent">
			<h2>User: <span class="focusText">${user.username}</span></h2>
		</div>
		<div class="shrinkWrapCenter">
		    <select id="roleSelect" multiple="multiple">
		        <c:forEach items="${userRoleList}" var="role">
		            <option selected="selected">${role}</option>
		        </c:forEach>
		        <c:forEach items="${availableRoleList}" var="role">
		            <option >${role}</option>
		        </c:forEach>
		    </select>
		</div>
		<div class="centeredContent">
			<button id="cancel">Cancel</button>
			<button id="finished">Finished</button>
		</div>
	</div>
</body>    
</html>