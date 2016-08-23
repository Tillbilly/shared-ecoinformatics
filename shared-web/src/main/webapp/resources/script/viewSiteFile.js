function initViewSiteFile(pageContextPath){
	$(".siteFileViewButton").click(function(){
		var url = pageContextPath + "/viewSiteFile";
		var filename = $(this).siblings(".siteFileName").first().html();
		if(filename != null && filename != ''){
			url = url + "?filename=" + filename;
		}
		var siteFileDataId = $(this).siblings(".siteFileDataId").first().html();
		if(siteFileDataId != null && siteFileDataId != ''){
			url = url + "&siteFileDataId=" + siteFileDataId;
		}
		var popup = window.open(url, "View Site File", "location=0,toolbar=no,menubar=no,status=no,scrollbars=1,resizable=no,width=700,height=600",true);	
		popup.screenX = window.screenX + 200;
		popup.screenY = window.screenY + 100;
		popup.focus();
		return false;
	});
};
