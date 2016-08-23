<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="q"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ attribute name="titleText" type="java.lang.String" required="true" %>
<%@ attribute name="backUrl" type="java.lang.String" required="false" %>
<%@ attribute name="includeHomeButton" type="java.lang.Boolean" required="true" %>
<%@ attribute name="containerId" type="java.lang.String" required="false" %>
<%@ attribute name="col3Content" fragment="true" required="false" %>
<style type="text/css">
	/* The perfect 3 column liquid layout from http://matthewjamestaylor.com/blog/perfect-3-column.htm */
	h1.pageTitle {
		margin: 0.3em 0;
	}
	
	/* column container */
	.colmask {
		position: relative; /* This fixes the IE7 overflow hidden bug */
		clear: both;
		float: left;
		width: 100%; /* width of whole page */
		overflow: hidden; /* This chops off any overhanging divs */
	}
	/* common column settings */
	.colmid, .colleft {
		float: left;
		width: 100%; /* width of page */
		position: relative;
	}
	
	.col1, .col2, .col3 {
		float: left;
		position: relative;
		padding: 0 0 0.3em 0;
		/* no left and right padding on columns, we just make them narrower instead 
						only padding top and bottom is included here, make it whatever value you need */
		overflow: hidden;
	}
	/* 3 Column settings */
	.threecol { }
	
	.threecol .colmid {
		right: 25%; /* width of the right column */
	}
	
	.threecol .colleft {
		right: 50%; /* width of the middle column */
	}
	
	.threecol .col1 {
		width: 58%;
		/* width of center column content (column width minus padding on either side) */
		left: 96%; /* 100% plus left padding of center column */
		text-align: center;
	}
	
	.threecol .col2, .threecol .col3 {
		width: 21%;
	}
	
	.threecol .col2 {
		left: 17%;
		/* width of (right column) plus (center column left and right padding) plus (left column left padding) */
	}
	
	.threecol .col3 {
		left: 75%; /* Please make note of the brackets here:
						(100% - left column width) plus (center column left and right padding) plus (left column left and right padding) plus (right column left padding) */
	}
	
	div.pageHeaderClearHack {
		clear: both;
		border-bottom: 2px solid #000;
		padding-bottom: 0.2em;
	}
</style>

<div id="${containerId}">
	<div id="pageHeader" class="colmask threecol">
		<div class="colmid">
			<div class="colleft">
				<div class="col1">
					<h1 class="pageTitle">${titleText}</h1>
					<c:set var="displayButtons" value="${includeHomeButton or not empty backUrl}" />
					<c:if test="${displayButtons}">
						<div class="centeredContent">
							<c:if test="${includeHomeButton}">
								<a href="${pageContext.request.contextPath}/" class="noUnderline">
									<button>Home</button>
								</a>
							</c:if>
							<c:if test="${not empty backUrl}">
								<a href="${backUrl}" class="noUnderline">
									<button>Back</button>
								</a>
							</c:if>
						</div>
					</c:if>
				</div>
				<div class="col2">
					<q:sharedSmallLogo />
				</div>
				<div class="col3">
					<jsp:invoke fragment="col3Content" />
				</div>
			</div>
		</div>
	</div>
	<!-- This next DIV needs to be here because it "catches" the content from "falling" into the next element. -->
	<div class="pageHeaderClearHack">
		<jsp:doBody />
	</div>
</div>