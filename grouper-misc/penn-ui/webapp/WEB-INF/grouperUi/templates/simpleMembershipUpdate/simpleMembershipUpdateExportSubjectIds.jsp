<%@ include file="../common/commonTaglib.jsp"%>

<div class="simpleMembershipDownload">
  <grouperGui:message key="simpleMembershipUpdate.downloadSubjectIdsLabel" /> <br /><br />
  <a href="../app/SimpleMembershipUpdateImportExport.exportSubjectIdsCsv/${simpleMembershipUpdateContainer.guiGroup.exportSubjectIdsFileName}"
    >${fn:escapeXml(simpleMembershipUpdateContainer.guiGroup.group.displayName)}</a>
</div>
