<%@ taglib tagdir="/WEB-INF/tags" prefix="q" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page session="false" %>
<q:sharedDoctype />
<html>
<head>
    <title>Vocabulary Management</title>
    <q:sharedCommonIncludes />
    <q:jQueryUiIncludes />
    <script type="text/javascript">
    $(function(){
    	$("#viewVocabularyButton").click(function(){
    		var selectedOption = $(".vocabSelect option:selected").first();
    		if(selectedOption != null){
    			var vocabVal = $(selectedOption).html();
    			if(vocabVal != null && vocabVal != ''){
    				window.location.href="${pageContext.request.contextPath}/vocabManagement/viewVocabulary?vocabularyName=" + vocabVal;
    			}
    			
    		}
    	});
    });
    </script>
</head>
<body class="sharedBodyBg">
	<q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader includeHomeButton="true" titleText="Vocabulary Management" />
		<div>
			<table>
			    <tr><td>View vocabulary <select class="vocabSelect">   
			                                <option></option>
			                                <c:forEach items="${customVocabNames }" var="vocabName" >
			                                    <option>${vocabName}</option>
			                                </c:forEach>
			                             </select>    </td>
			        <td><button id="viewVocabularyButton" >View</button></td>
			    </tr>
			    <tr><td>Add new trait vocabulary</td>
			        <td><form action="${pageContext.request.contextPath}/vocabManagement/addNewVocabularyFromFile" method="post" enctype="multipart/form-data">
			              <input id="traitVocabFileInput" type="file" name="vocabFile"><button id="addNewTraitButton" >Add</button>
			           </form>
			        </td>
			    </tr>
			      <tr><td>Replace custom vocabularies (!!WARNING!! Don't do this unless you are sure of what you are doing)</td>
			          <td><form action="${pageContext.request.contextPath}/vocabManagement/replaceVocabularies" method="post" enctype="multipart/form-data">
			                  <input id="vocabFileInput" type="file" name="vocabFile">
			                  <button id="replaceVocabsButton" >Replace Vocabs</button>
			              </form>
			          </td>
			      </tr>
			     <tr><td>Download custom vocab file</td><td><a href="${pageContext.request.contextPath}/vocabManagement/downloadCustomVocabularies">Download</a></td></tr>
			</table>
		</div>
	</div>
</body>
</html>