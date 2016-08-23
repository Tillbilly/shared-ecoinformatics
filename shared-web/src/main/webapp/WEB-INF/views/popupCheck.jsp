<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>Pop Up Blocker Check</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<script type="text/javascript">
		$(function() {
			$('#closeBtn').click(function() {
				window.close();
			});
		})
	</script>
	<style type="text/css">
		.main-content {
			width: 700px;
		}
		
		/* Fragile way of taking precendence over the <style> block that's included closer to the header */
		div.threecol div.col2 {
			width: auto;
		}
	</style>
</head>
<body class="sharedBodyBg">
	<div class="main-content">
		<q:pageHeader includeHomeButton="false" titleText="Pop Up Check"></q:pageHeader>
		<div class="msgBoxSpacing infoBox centeredContent">
			<h2>Success!</h2>
			<p>Your pop up blocker is configured correctly for SHaRED.</p>
			<button id="closeBtn">Close</button>
		</div>
	</div>
</body>
</html>