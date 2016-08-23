<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
    <title>Edit Group Members</title>
    <q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
    <script type="text/javascript">
	    $(function(){
	    	$("#inGroupDiv").on("click",".controlButton",function(){
	    		var $grpMemberDiv = $(this).closest("div.editGroupUserDiv").detach();
	    		//need to change containing control button text from 'Remove' to 'Add'
	    		$grpMemberDiv.find(".controlButton").first().html("Add");
	    		//Need to change the input bind path (name) from inGroupMap  to notInGroupMap
	    		var $usernameInput = $grpMemberDiv.find("input.usernameInput").first();
	    		var nameStr = $usernameInput.attr("name");
	    		$usernameInput.attr("name",nameStr.replace("inGroupMap","notInGroupMap") );
	    		$grpMemberDiv.appendTo("#outGroupDiv");
	    	});
	    	
            $("#outGroupDiv").on("click",".controlButton",function(){
            	var $grpMemberDiv = $(this).closest("div.editGroupUserDiv").detach();
	    		//need to change containing control button text from 'Add' to 'Remove'
	    		$grpMemberDiv.find(".controlButton").first().html("Remove");
	    		//Need to change the input bind path (name) from notInGroupMap  to inGroupMap
	    		var $usernameInput = $grpMemberDiv.find("input.usernameInput").first();
	    		var nameStr = $usernameInput.attr("name");
	    		$usernameInput.attr("name",nameStr.replace("notInGroupMap","inGroupMap") );
	    		$grpMemberDiv.appendTo("#inGroupDiv");
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
		<q:pageHeader includeHomeButton="true" titleText="Edit Group Members" />
		<h1>${group.name}</h1>
		<c:if test="${not empty group.organisation}" >
		    <h2>${group.organisation}</h2>
		</c:if>
		<form:form commandName="editGroupModel" action="${pageContext.request.contextPath}/admin/updateGroupMembers" method="POST" >
		    <form:hidden path="groupId"/>
		    <form:hidden path="groupAdminUser"/>
		    <h2>Group Members</h2>
		    <div id="inGroupDiv" class="editGroupMemberListDiv">
		        <c:forEach items="${editGroupModel.inGroupMap}" var="entry" >
			        <div class="editGroupUserDiv">
			            <span><form:input readonly="true" path="inGroupMap[${entry.key}].username" class="usernameInput"/></span>
			            <span>${editGroupModel.inGroupMap[ entry.key ].email}</span>
			            <span class="controlSpan">
			                <c:choose>
			                  <c:when test="${editGroupModel.groupAdminUser eq entry.key }">
			                      GROUP ADMIN
			                  </c:when>
			                  <c:otherwise>
			                    <button class="controlButton" type="button">Remove</button>
			                  </c:otherwise>
			                </c:choose>  
			            </span>
			        </div>
		        </c:forEach>
		    </div>
		    <br/>
		    <h2>Users to Add</h2>
		    <div id="outGroupDiv" class="editGroupMemberListDiv">
		        <c:forEach items="${editGroupModel.notInGroupMap}" var="entry" >
			        <div class="editGroupUserDiv">
			            <span><form:input readonly="true" path="notInGroupMap[${entry.key}].username" class="usernameInput"/></span><span>${editGroupModel.notInGroupMap[ entry.key ].email}</span><span class="controlSpan"><button class="controlButton" type="button">Add</button></span>
			        </div>
		        </c:forEach>
		    </div>
		    <button id="cancelButton" type="button">Cancel</button><button type="submit">Submit</button>
		</form:form>
	</div>	
</body>
</html>	