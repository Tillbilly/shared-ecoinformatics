<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ page session="true" %>
<q:sharedDoctype />
<html>
<head>
	<q:sharedHeadTitle>New Submission</q:sharedHeadTitle>
	<q:sharedCommonIncludes />
	<q:jQueryUiIncludes />
	<script type="text/javascript">
		$(function(){
			var LONG_ENOUGH_THAT_POPUP_BLOCKERS_ARENT_HAPPY_MS = 1100;
			$('#popupBlockerCheckButton').click(function(){
				$('#popupCheckMsg').html('<img src="${pageContext.request.contextPath}/images/loading.gif" />');
				setTimeout(function(){
					var url = '${pageContext.request.contextPath}/popupCheck';
					var popupWindow = window.open(url, '_blank', "location=no,toolbar=no,menubar=no,status=no,scrollbars=no,resizable=no,width=900,height=330", true);
					if (popupWindow == null || typeof(popupWindow)=='undefined') { 	
						$('#popupCheckMsg').html('Oh no, your pop up blocker doesn\'t like SHaRED.<br />Please disable your pop-up blocker.')
						.addClass('errorBox').removeClass('successBox');
					}
					else {
						$('#popupCheckMsg').html('Yep, everything is configured correctly.')
						.addClass('successBox').removeClass('errorBox');
					}
				}, LONG_ENOUGH_THAT_POPUP_BLOCKERS_ARENT_HAPPY_MS);
			});
			$("#userInfoAccordion").accordion({
				collapsible: true,
				active: false,
				heightStyle: "content"
			});
			$("#technicalInfoAccordion").accordion({
				collapsible: true,
				active: 0,
				heightStyle: "content"
			});
		});
	</script>
	<style type="text/css">
		.noBottomMargin {
			margin-bottom: 0;
		}
		
		.pageFocus {
			padding: 0.5em;
			width: 77%;
			margin: 1em auto;
		}
		
		#pageContentSummary {
			border-collapse: collapse;
		}
		
		#pageContentSummary tbody td {
			border: 1px solid black;
			width: 22em;
		}
		
		/* \/ Accordion overrides \/ */
		.ui-accordion h3 {
			background: none;
			font-size: 0.9em;
		}
		
		.ui-accordion-content {
			border: none;
		}
		/* /\ Accordion overrides /\ */
</style>
</head>
<body class="sharedBodyBg">
    <q:questionnaireLogout />
	<div class="main-content">
		<q:pageHeader titleText="New Submission" includeHomeButton="true" />
		<div id="userInfoAccordion">
			<h3>What to expect in SHaRED</h3>
			<div class="lessImportantText">
				<p>Data submission through SHaRED is a self-service activity that involves creating a detailed metadata description for your ecological data
					and uploading this along with your data files (Excel, Word, Access DB files etc.). <i>The value of your data set and the ability for others 
					to discover and retrieve it through the ÆKOS Data Portal is dependent on how well you complete the metadata</i>. SHaRED is
					structured as an online questionnaire, with sections of questions that cover specific topics or subjects about your data, with file upload
					functionality associated with certain questions.</p>
				<p>The questions together with your answers create the detailed metadata record that is submitted alongside your data to TERN Ecoinformatics
					for publication, discovery and re-use through &AElig;KOS.</p>
				<p>There are 14 sections in all with a number of questions in each section, the sections in the questionnaire cover the following topics:</p>
				<table id="pageContentSummary" class="centeredBlock centeredContent">
					<tbody>
						<tr>
							<td>Submission Information</td>
							<td>Environmental Features</td>
						</tr>
						<tr>
							<td>Project Description</td>
							<td>Associated/Supplementary Materials</td>
						</tr>
						<tr>
							<td>Dataset Description</td>
							<td>Field Methods</td>
						</tr>
						<tr>
							<td>Dataset Content</td>
							<td>Dataset Contact</td>
						</tr>
						<tr>
							<td>Spatial Coverage</td>
							<td>Dataset Author(s)</td>
						</tr>
						<tr>
							<td>Dates</td>
							<td>Dataset Conditions of Use</td>
						</tr>
						<tr>
							<td>Dataset Species</td>
							<td>Dataset Management</td>
						</tr>
					</tbody>
				</table>
			</div>
			<h3>Before You Start</h3>
			<div class="lessImportantText">
				<p>To complete a data submission through SHaRED in as efficient and comprehensive way as possible, it may help to prepare information about the
					data and any associated artefacts prior to starting the online questionnaire.</p>
				<p>In particular it would be useful to have information at hand about;</p>
				<ul>
					<li>Organisational and Administrative details around the dataset, e.g. Custodians, Authors and Contacts</li>
					<li>Ecological research/management themes, techniques and methods used in the data collection</li>
					<li>Spatial &amp; Temporal Data - where and when was the data collected, e.g. a GPS output file.</li>
					<li>Species Data - which plants and animals were recorded in the data</li>
					<li>Data Citation Requirements - which organisations, authors, contributors require acknowledgement</li>
					<li>Any artefacts produced that you would like to upload, i.e. documents, field manuals, images, spreadsheets etc</li>
					<li>Pre-existing DOI's or any other existing references to the data, e.g. a <a href="http://trove.nla.gov.au/" target="_blank">TROVE</a> ID.</li>
					<li>What type of license (<a href="http://creativecommons.org/licenses/by/3.0/au/" target="_blank">CC-BY</a> or 
						<a href="http://tern.org.au/datalicence/TERN-BY/1.0/" target="_blank">TERN-BY</a>) should the data be published under?
					</li>
				</ul>
			</div>
			<h3>Data Entry</h3>
			<div class="lessImportantText">
				<p>The questions in SHaRED are answered through either free text fields or by selecting from lists that have been pre-populated from the
					SHaRED/&AElig;KOS controlled vocabularies. Some of these questions are mandatory and require answers before the user can proceed, the majority are
					optional however.</p>
				<p>The tool provides the user with functionality to upload pre-defined Species and Site Location files as well as the actual data files.
					Uploading files provides an easy way to supply lists of animals and plants recorded and to provide the location of multiple sites or the boundary
					of a particular survey area.</p>
				<p>The tool also provides a mapping interface that allows users to "draw" the positions of sites and survey areas on a map if so required.</p>
			</div>
			<h3>Disclaimer</h3>
			<div class="lessImportantText">
				<p>By using this Data Submission Tool, users acknowledge and agree that the Data Submission Tool and the data submitted through this tool may
					be incomplete, contain errors or omissions and the Data Submission Tool may be unreliable in its operation or availability. Furthermore by using
					this Data Submission Tool users acknowledge and agree that they will not hold the University of Adelaide responsible or liable for the
					consequences of any use of this Data Submission Tool or data made available through the Data Portal. To the maximum permitted by law, the
					University of Adelaide excludes all liability to any person arising directly or indirectly from using this Data Submission Tool and any data
					available from it.</p>
			</div>
		</div>
		<div class="pageFocus">
			<h4 class="centeredContent">Create New Submission</h4>
			<p class="lessImportantText">
				You must accept the following statement before you can create a new submission.
				If this is your first time using SHaRED, you may be interested in the information above.
				It's worth checking your pop up blocker below too, it'll make the process more fun.
			</p>
			<q:createSubmissionAgreementFragment buttonText="Start New Submission" />
		</div>
		<div id="technicalInfoAccordion">
			<h3>Is your pop up blocker configured correctly?</h3>
			<div>
				<div class="warningBox msgBoxSpacing centeredContent lessImportantText">
					<div class="warningIcon centeredBlock"></div>
					<p>
						SHaRED uses pop up windows during the questionnaire and in order for them to work
						correctly, your browser's pop up blocker must be configured to allow them through. You can test
						your browser now by clicking this button.
					</p>
					<button id="popupBlockerCheckButton">Check my popup blocker now</button>
					<div id="popupCheckMsg"></div>
				</div>
			</div>
			<h3>Interested in how the questionnaire is configured?</h3>
			<div>
				<p class="centeredContent">
					The questionnaire is defined by an XML configuration file. Click <a href="${pageContext.request.contextPath}/files/questionnaire3.xml">here</a> for
					an example.
				</p>
			</div>
		</div>
		<q:brandingBlurb />
	</div>
</body>
</html>