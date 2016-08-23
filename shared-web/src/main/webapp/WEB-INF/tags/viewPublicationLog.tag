<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="log" type="au.edu.aekos.shared.data.entity.PublicationLog" required="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<div class="publicationLogEntry">
    <table class="centeredBlock">
		<tr>
			<th colspan="2">Publication Log for Submission id:${log.submission.id}</th>
		</tr>
		<tr>
			<th>Log Time</th>
			<td><fmt:formatDate value="${log.logTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
		</tr>
		<c:if test="${not empty log.doiSuccess}" >
			<tr>
				<th>DOI minting time</th>
				<td><fmt:formatDate value="${log.doiProcessTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			</tr>
			<c:choose>
	            <c:when test="${log.doiSuccess}">
					<tr>
						<th>DOI</th>
						<td>${log.doi}</td>
					</tr>
				</c:when>
	            <c:otherwise>
					<tr>
						<th>DOI Failed</th>
						<td>${log.doiErrorMessage}</td>
					</tr>
				</c:otherwise>
			</c:choose>
        </c:if>
        <c:if test="${not empty log.indexSuccess}" >
			<tr>
				<th>Aekos Index Time</th>
				<td><fmt:formatDate value="${log.aekosIndexTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			</tr>
			<c:choose>
                <c:when test="${log.indexSuccess}">
					<tr>
						<th>Index written</th>
						<td>success</td>
					</tr>
				</c:when>
                <c:otherwise>
					<tr>
						<th>Index write failed</th>
						<td>${log.indexErrorMessage}</td>
					</tr>
				</c:otherwise>
            </c:choose>
        </c:if>
        <c:if test="${not empty log.fileGenerated}" >
			<tr>
				<td>Rifcs file time</td>
				<td><fmt:formatDate value="${log.rifcsFileTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			</tr>
			<c:choose>
                <c:when test="${log.fileGenerated}">
					<tr>
						<th>Rifcs file</th>
						<td>${log.filepath}</td>
					</tr>
				</c:when>
                <c:otherwise>
					<tr>
						<th>Rifcs file failed</th>
						<td>${log.fileGenerationErrorMessage}</td>
					</tr>
				</c:otherwise>
            </c:choose>
        </c:if>
        <c:if test="${not empty log.scpSuccess}" >
			<tr>
				<th>Rifcs file transfer attempt</th>
				<td><fmt:formatDate value="${log.rifcsScpTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			</tr>
			<c:choose>
                <c:when test="${log.scpSuccess}">
					<tr>
						<th>Rifcs file transferred</th>
						<td>success</td>
					</tr>
				</c:when>
                <c:otherwise>
					<tr>
						<th>Rifcs file transfer failed</th>
						<td>${log.scpErrorMessage}</td>
					</tr>
				</c:otherwise>
            </c:choose>
        </c:if>
    </table>
    <c:if test="${not empty log.information}">
		<div class="stacktrace">
       		${log.htmlFormattedInformation}
		</div>
    </c:if>
</div>
