<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ attribute name="progressIndicatorInfo" type="au.edu.aekos.shared.web.model.ProgressIndicatorInfo" required="true" %>
<style type="text/css">
	#progressInfoContainer {
	    position: absolute;
	    left: 229px;
	    right: 229px;
	    top: 51px;
	}
	
	#progressBar {
	    position: absolute;
	    left: 10px;
	    right: 10px;
	    height: 13px;
	    top: 10px;
	    border: 2px solid #666;
	}
	
	#progressBarDone, #progressBarRemaining {
	    position: absolute;
	    height: 100%;
	}
	
	#progressBarDone {
	    background-color: #8FB163;
	}
	
	#progressBarRemaining {
		background-color: #D2E0C0;
	}
	
	#progressBarDone {
	    left: 0;
	    width: 0%;
	}
	
	.progressMsgPrefix, .progressMsgCounter {
		color: #000;
	}
	
	#progressBarRemaining {
	    right: 0;
	    width: 100%;
	    opacity: 0.4;
	}
	
	#progressBarMsgContainer {
		position: absolute;
		top: 11px;
		left: 0;
		right: 0;
		text-align: center;
		font-size: 0.6em;
	}
</style>

<script type="text/javascript">
	$(function() {
		var page = ${progressIndicatorInfo.currentPageNum};
		var total = ${progressIndicatorInfo.totalPageCount};
	    var donePercentage = page / total * 100;
	    $('#progressCurrPage').text(page);
	    $('#progressTotalPages').text(total);
	    $('#progressBarDone').css('width', donePercentage+'%');
	    $('#progressBarRemaining').css('width', (100-donePercentage)+'%');
	})
</script>

<div id="progressInfoContainer">
	<div id="progressBar">
	    <div id="progressBarDone"></div>
	    <div id="progressBarRemaining"></div>
	</div>
	<div id="progressBarMsgContainer">
		<span class="progressMsgPrefix">PROGRESS </span>
		<span class="progressMsgCounter">
		    [<span id="progressCurrPage"></span> OF <span id="progressTotalPages"></span>]
		</span>
	</div>
</div>