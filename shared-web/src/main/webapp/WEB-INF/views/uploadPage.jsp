<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
    <head>
        <title>Upload a file please</title>
    </head>
    <body>
        <h1>Please upload a file</h1>
        <form method="post" action="upload" enctype="multipart/form-data">
            <input type="text" name="name"/>
            <input type="file" name="file"/>
            <input type="submit"/>
        </form>
        <br/>
        <c:if test="${ not empty fileName}" >
          ${fileName }<br/>
          ${fileSize} <br/>
          ${storageDescription }
        </c:if>
    </body>
</html>