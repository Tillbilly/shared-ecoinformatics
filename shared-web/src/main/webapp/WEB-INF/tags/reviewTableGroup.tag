<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="model" type="au.edu.aekos.shared.web.model.GroupModel" required="true" %>
<%@ attribute name="reviewModel" type="au.edu.aekos.shared.web.model.SubmissionReviewModel" %>
<%@ attribute name="answerHistoryMap" type="java.util.Map" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
     <tr class="groupRow">
         <td class="reviewIdColumn">${model.groupId}</td>
         <td colspan="5" class="reviewGroupTitle">${model.title}</td> 
     </tr>
	   
	<c:forEach var="item" items="${model.items}">
	   <c:choose>
	       <c:when test="${item.itemType eq 'QUESTION' or item.itemType eq 'QUESTION_SET'}">
                <q:reviewTableQuestion model="${item}" reviewModel="${reviewModel}" answerHistoryMap="${answerHistoryMap}"/>
            </c:when>
            <c:when test="${item.itemType eq 'GROUP'}">
                <q:reviewTableGroup model="${item}" reviewModel="${reviewModel}" answerHistoryMap="${answerHistoryMap}"/>
            </c:when>
	   </c:choose>
	</c:forEach>
	