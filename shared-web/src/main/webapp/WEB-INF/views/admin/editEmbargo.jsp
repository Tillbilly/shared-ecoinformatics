<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
    <title>Edit Embargo</title>
    <q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
    <script type="text/javascript" >
       $(function(){
    	   
    	   $(".dateResponseInput").datepicker(
  	    	     { dateFormat: "dd/mm/yy",
  	    	       changeMonth : true,
  	    	       changeYear  : true,
  	    	       yearRange: "2000:2030"
  	    	     }
  	    	);
    	   
    	   $("#cancelButton").click(function(){
        	   window.location.href='${pageContext.request.contextPath}/viewSubmission?submissionId=${embargoModel.submissionId}';
           });
    	   
           $("#clearButton").click(function(){
        	   $("#dateString").val(null);
           });
           
           $("#submitButton").click(function(){
        	   var origDateStr = '${embargoModel.dateString}';
        	   var newEmbargoDate = $("#dateString").val();
        	   if(origDateStr == null || origDateStr == 'null'){
        		   origDateStr = '';
        	   }
        	   if(newEmbargoDate == null){
        		   newEmbargoDate = '';
        	   }
        	   if(origDateStr == newEmbargoDate){
        		   window.location.href='${pageContext.request.contextPath}/viewSubmission?submissionId=${embargoModel.submissionId}';
        	   }
        	   if(confirm("Embargo date is being changed from '" + origDateStr +"' to '" + newEmbargoDate +"'" ) ){
        		   $("#embargoForm").submit();
        	   }
           });
       });
    </script>
</head>
<body class="sharedBodyBg">
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="Edit Embargo" />
		<div class="centeredContent">
			<h4>Submission Details</h4>
			<p>
				<em>Title:</em> ${embargoModel.submissionTitle}<br />
				<em>Owned by:</em> ${embargoModel.username}<br />
				<em>Submission ID:</em> ${embargoModel.submissionId}
			</p>
			<form:form id="embargoForm" commandName="embargoModel" action="${pageContext.request.contextPath}/admin/processEditEmbargo" method="POST">
			    <form:hidden path="submissionId" />
			    <label>Set embargo date to:</label>
			    <form:input path="dateString" cssClass="dateResponseInput" />
			</form:form>
			<p>
				<button id="cancelButton">Cancel</button>
				<button id="clearButton">Clear</button>
				<button id="submitButton">Submit</button>
			</p>
		</div>
	</div>
</body>
</html>    