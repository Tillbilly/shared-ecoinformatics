<%@page import="au.edu.aekos.shared.service.security.SecurityServiceImpl"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="q" tagdir="/WEB-INF/tags"%>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Transfer Submission</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
    <script type="text/javascript">
    $(function(){
    	
    	$("#cancelButton").click(function(){
    		window.location.href="${pageContext.request.contextPath}/groupAdmin/manageGroupSubmissions";
    	});
    	
        $("#submitButton").click(function(){
        	//Validate the user has selected a username and written a message
        	//id="transferSelect"
        	var uname = $("#transferSelect").val();
        	if(uname == null || uname == ''){
        		alert("Please select a username to transfer to");
        		$("#transferSelect").focus();
        		return;
        	}
        	
        	var message=$("#messageBox").val();
        	if(message==null||message==''){
        		alert("Please enter a message for the chosen user");
        		$("#messageBox").focus();
        		return;
        	}
        	$("#transferForm").submit();
    	});
    	
    	
    	
    });
    </script>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="Transfer Group Submission ${userGroup.name}" backUrl="${pageContext.request.contextPath}/groupAdmin/manageGroupSubmissions" />
		<h2 class="centeredContent">Transfer submission</h2>
		<div class="submissionMetaDetails">
		    <div>
		        <span class="titleSpan">Submission Title:</span>
		        <span class="valueSpan">${submission.submissionTitle}</span>
		        <span class="titleSpan">Submission Id:</span>
		        <span class="valueSpan">${submission.submissionId}</span>
		    </div>
		    <div>
		        <span class="titleSpan">Current Owner:</span>
		        <span class="valueSpan">${submission.submittedByUsername}</span>
		        <span class="titleSpan">Config Id:</span>
		        <span class="valueSpan">${submission.questionnaireConfigId}</span>
		    </div>
		</div>
		<div class="transferFormDiv" >
		    <form id="transferForm" action="${pageContext.request.contextPath}/groupAdmin/executeTransfer" method="get">
		        <input type="hidden" name="submissionId" value="${submission.submissionId}" >
		        <input type="hidden" name="groupId" value="${userGroup.id}" >
		        <table>
		          <tr>
			        <th>Transfer To:</th>
			        <td>
			          <select id="transferSelect" name="transferTo">
			            <option value="" ></option>
			            <c:forEach items="${usernamesList }" var="uname" >
			                <option>${uname}</option>
			            </c:forEach>
			          </select>
			        </td>
			      </tr>
			      <tr>
			        <th>Message:</th>  
			        <td><textarea id="messageBox" name="message"></textarea></td>
			      </tr>
			      <tr><td></td><td><button id="cancelButton" type="button">Cancel</button><button id="submitButton" type="button">Submit</button></td></tr>
		        </table>
		    </form>
		</div>
	</div>
</body>
</html>
    
    
    