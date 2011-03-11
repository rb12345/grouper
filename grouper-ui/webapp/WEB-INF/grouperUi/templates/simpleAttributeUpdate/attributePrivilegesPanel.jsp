<%@ include file="../common/commonTaglib.jsp"%>
<!-- Start: simpleAttributeUpdate/attributePrivilegesPanel.jsp -->

<div class="section" style="min-width: 900px">

  <grouper:subtitle key="simpleAttributeUpdate.privilegesSectionHeader" />

  <div class="sectionBody">
    <form id="attributePrivilegesSubjectFormId" name="attributePrivilegesSubjectFormName" onsubmit="return false;" >
    
	    <%-- signify which attribute def we are talking about --%>
	    <input type="hidden" name="attributeDefToEditId" 
	              value="${attributeUpdateRequestContainer.attributeDefToEdit.id}" />
      
	    <table class="formTable formTableSpaced" cellspacing="2">
	      <tr class="formTableRow">
	        <td class="formTableLeft" style="vertical-align: middle">
	          <label for="folder">
	            <grouper:message key="simpleAttributeUpdate.privilegeSubject" />
	          </label>
	        </td>
	        <td class="formTableRight" style="white-space: nowrap;">
            <div class="combohint"><grouper:message key="simpleAttributeUpdate.privilegeSubjectCombohint"/></div>
            <table width="900" cellpadding="0" cellspacing="0">
				      <tr valign="top">
				        <td style="padding: 0px" width="710">
	 	             <grouper:combobox filterOperation="SimpleAttributeUpdateFilter.filterPrivilegeSubject" id="simpleAttributeUpdatePrivilegeSubject" 
		               width="700"/>
				        </td>
				        <td>
				          <input class="blueButton" type="submit" 
				          onclick="ajax('../app/SimpleAttributeUpdateFilter.addPrivilegeSubject', {formIds: 'attributePrivilegesSubjectFormId'}); return false;" 
				          value="${simpleAttributeUpdateContainer.text.filterAttributePrivilegeSubject}" style="margin-top: 2px" />
				        </td>
				      </tr>
				    </table>
	        </td>
	      </tr>
	    </table>
    </form>
    <form id="attributePrivilegesFormId" name="attributePrivilegesFormName" onsubmit="return false;" >
      <table cellspacing="2" class="formTable" width="1100">
        <c:set var="row" value="0" />
	      <c:forEach items="${attributeUpdateRequestContainer.privilegeSubjectContainers}" var="privilegeSubjectContainer">
          
          <c:set var="guiMember" value="${attributeUpdateRequestContainer.privilegeSubjectContainerGuiMembers[row]}" />
          <c:if test="${attributeUpdateRequestContainer.showPrivilegeHeader[row]}">
	          <tr>
			        <c:forTokens var="privilegeName" items="attrView attrRead attrUpdate attrAdmin attrOptin attrOptout" delims=" ">
			          <th class="privilegeHeader"><grouper:message key="priv.${privilegeName}" /></grou></th>
			        </c:forTokens>
			        <th class="privilegeHeader" style="text-align: left">
			          &nbsp; &nbsp; <grouper:message key="simpleAttributeUpdate.entityHeader" /> 
	  			    </th>
				    </tr>
          </c:if>
          <tr>
            <c:forTokens var="privilegeName" items="attrView attrRead attrUpdate attrAdmin attrOptin attrOptout" delims=" ">
              <td class="privilegeRow">
                <%-- keep the previous state so we know what the user changed --%>
                <input  name="previousState__${guiMember.member.uuid}__${privilegeName}"
                  type="hidden" value="${privilegeSubjectContainer.privilegeContainers[privilegeName].privilegeAssignType.immediate ? 'true' : 'false'}" />
                <%-- note, too much space between elements, move it over 3px --%>
                <input  style="margin-right: -3px" name="privilegeCheckbox__${guiMember.member.uuid}__${privilegeName}"
                  type="checkbox" ${privilegeSubjectContainer.privilegeContainers[privilegeName].privilegeAssignType.immediate ? 'checked="checked"' : '' } 
                /><c:choose>
			            <c:when test="${privilegeSubjectContainer.privilegeContainers[privilegeName].privilegeAssignType.allowed}"
			            ><img src="../../grouperExternal/public/assets/images/accept.png" height="14px" border="0" 
			              onmouseover="Tip('${grouper:escapeJavascript(navMap[privilegeSubjectContainer.privilegeContainers[privilegeName].privilegeAssignType.immediate ? 'simpleAttributeUpdate.immediateTooltip' : 'simpleAttributeUpdate.immediateAndEffectiveTooltip'])}')" 
                    onmouseout="UnTip()"
			            /></c:when>
			            <c:otherwise><img src="../../grouperExternal/public/assets/images/cancel.png" height="14px" border="0" 
                    onmouseover="Tip('${grouper:escapeJavascript(navMap['simpleAttributeUpdate.effectiveTooltip'])}')" 
                    onmouseout="UnTip()"
			            /></c:otherwise>
			          </c:choose>
              </td>
            </c:forTokens>
            <td width="1000">
	            <%-- show an icon for the subject --%>
	            <grouper:subjectIcon guiSubject="${guiMember.guiSubject}" /> 
	            ${fn:escapeXml(guiMember.guiSubject.screenLabel)}
            </td>
          </tr>
          <c:set var="row" value="${row + 1}" />
		    </c:forEach>          
      </table> 
    </form>
    <br />
  </div>
</div>

<!-- End: simpleAttributeUpdate/attributePrivilegesPanel.jsp -->