<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Login Page</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/shared-web-lt960px-width.css"></link>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/qTip2/jquery.qtip.min.css"></link>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/hmac-sha512.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/enc-base64-min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/jquery.qtip.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/script/helpIcon.js?version=${sharedVersion}"></script>
	<script type="text/javascript">
		$(function() {
			$("#loginForm").submit(
					function() {
						var enteredPassword = $("#passwd").val();
						var username = $("#username").val();
						if (enteredPassword && enteredPassword.length > 0
								&& username && username.length > 0) {
							var hash = CryptoJS.HmacSHA512(enteredPassword,
									username);
							var hashStr = hash.toString(CryptoJS.enc.Base64);
							$("#hashPasswd").val(hashStr);
						}
					});
		});
	</script>
	<style type="text/css">
		.pageFocus {
			padding-top: 0;
		}
	</style>
</head>
<body onload='document.loginForm.j_username.focus();' class="sharedBodyBg">
	<div class="main-content">
		<div class="centeredContent">
			<div class="imageContainer centeredBlock">
				<img src="${pageContext.request.contextPath}/images/shared-logo.png" class="sharedLogo" />
			</div>
			<spring:eval expression="@sharedWebProperties.getProperty('shared.version')" var="sharedVersion" />
			<h1>Welcome to SHaRED <span class="dontWrap">( ${sharedVersion} )</span></h1>
			<h2>AEKOS Data Submission Tool</h2>
		</div>
		<c:if test="${param.error == 'true'}">
			<div class="errorBox msgBoxSpacing centeredContent errorText">
				<p>Your login attempt was not successful, try again.</p>
			</div>
		</c:if>
		<div id="loginMethodContainer" class="pageFocus">
			<h2 class="centeredContent loginTitle">Log in</h2>
			<spring:eval expression="@sharedWebProperties.getProperty('shared.aaf.endpoint')" var="sharedAAFEndpoint" />
			<div class="loginMethod"> 
				<h3 class="centeredContent">AAF User</h3>
				
		        <div class="centeredContent">
		            <img src="https://rapid.test.aaf.edu.au/aaf_service_223x54.png"/>
		            <br />
			        <a href="${sharedAAFEndpoint}">
			        	<button>Login using AAF</button>
			        </a>
		        </div>
		    </div>
			<div class="loginMethodSeparator"></div>
			<form id="loginForm" class="loginMethod" name='loginForm' action="<c:url value='j_spring_security_check' />" method='POST'>
				<h3 class="centeredContent">Other SHaRED User</h3>
				<div class="twoColLeft">User:</div>
				<div class="twoColRight"><input id="username" type='text' name='j_username' value=''></div>
				<div class="twoColLeft">Password:</div>
				<div class="twoColRight"><input id="passwd" type='password' /></div>
				<div class="centeredContent">
					<a href="reg/detailedRegistration">Register?</a>
					<input name="submit" type="submit" value="Login" />
				</div>
				<input type="hidden" name="j_password" id="hashPasswd" />
			</form>
		</div>
		
		<h4 class="centeredContent">SHaRED - a service for publishing ecological data</h4>
		<div class="lessImportantText">
			<p>SHaRED is a data submission service of TERN Eco-informatics enabling any type of ecological datasets to be archived and published via the
				Advanced Ecological Knowledge and Observation System (<a href="http://www.aekos.org.au/" target="_blank">&AElig;KOS</a>). We gratefully acknowledge you submitting your research data.</p>
			<ul class="sharedTermsList">
				<li>
					<span class="projectDescriptionTerms">Citations</span> - datasets are fully citable and cross-referenced in scientific publications.
					<div class="helpIcon" alt="More information"></div>
					<div class="helpText">
		        		<p>Reliable identification and access with a <a href="http://www.doi.org/" target="_blank">DOI</a> name for each data submission.</p>
		       		</div>
				</li>
				<li>
					<span class="projectDescriptionTerms">Managed dataset usage</span> - Data submission form guides comprehensive data description to a 'publication ready' standard.
					<div class="helpIcon" alt="More information"></div>
					<div class="helpText">
						<p>You have already invested enormous effort to collect high quality data for research. A little extra effort to fully describe your data
							will secure its legacy and help to minimize its potential misuse. As publishing scientists, we have little control on how others will use any
							aspect of our published works. SHaRED is a service for submitting well-described datasets (metadata) to enable you to manage usage of them. It
							is the responsibility of data users to consider "fair and reasonable" use of your data and give credit by citing it in publications. You may
							even be invited to collaborate on new research.</p>
					</div>
				</li>
				<li>
					<span class="projectDescriptionTerms">Discoverable, accessible</span> - all datasets can be found and downloaded in &AElig;KOS Data Portal and other technical services.
					<div class="helpIcon" alt="More information"></div>
					<div class="helpText">
		        		<p>Quality assurance on metadata, such as citation, references, species, geo-location, and standard parameter vocabularies.</p>
		       		</div>
				</li>
				<li><span class="projectDescriptionTerms">Pre-publication embargoes</span> - service to protect publication until a specified date.</li>
				<li><span class="projectDescriptionTerms">Editing service</span> - direct communication with authors about data description content.</li>
				<li><span class="projectDescriptionTerms">Certificate of publication</span> - electronic certificates sent to authors as proof of open data publication.</li>
				<li><span class="projectDescriptionTerms">Costs</span> - free submission for open access to all datasets using <a target="_blank"
					href="http://www.tern.org.au/rs/7/sites/998/user_uploads/File/Data Licensing Documents/TERN Data Licensing Policy v1_0.pdf">Creative Commons
						Attribution licence</a>.</li>
			</ul>
		</div>
		<p class="centeredContent lessImportantText">
			<a href="http://www.ecoinformatics.org.au/legals_and_licensing" target="_blank">Legal Information</a>
		</p>
		<p class="centeredContent lessImportantText">
			If you have any queries regarding SHaRED Submissions please contact us at <a href="mailto:enquiry@aekos.org.au">enquiry@aekos.org.au</a>.
		</p>
		<q:brandingBlurb />
	</div>
</body>
</html>