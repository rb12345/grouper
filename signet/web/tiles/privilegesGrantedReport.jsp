<!-- $Header: /home/hagleyj/i2mi/signet/web/tiles/privilegesGrantedReport.jsp,v 1.44 2007-08-13 23:17:20 ddonn Exp $ -->
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>

<%@ page import="java.util.Set" %>
<%@ page import="java.util.TreeSet" %>
<%@ page import="java.util.SortedSet" %>
<%@ page import="java.util.Iterator" %>

<%@ page import="edu.internet2.middleware.signet.Signet" %>
<%@ page import="edu.internet2.middleware.signet.Assignment" %>
<%@ page import="edu.internet2.middleware.signet.Grantable" %>
<%@ page import="edu.internet2.middleware.signet.Subsystem" %>
<%@ page import="edu.internet2.middleware.signet.Function" %>
<%@ page import="edu.internet2.middleware.signet.Category" %>
<%@ page import="edu.internet2.middleware.signet.subjsrc.SignetSubject" %>
<%@ page import="edu.internet2.middleware.signet.Status" %>
<%@ page import="edu.internet2.middleware.signet.Proxy" %>
<%@ page import="edu.internet2.middleware.signet.resource.ResLoaderUI" %>
<%@ page import="edu.internet2.middleware.signet.ui.Common" %>
<%@ page import="edu.internet2.middleware.signet.ui.Constants" %>
<%@ page import="edu.internet2.middleware.signet.ui.PrivDisplayType" %>
<%@ page import="edu.internet2.middleware.signet.ui.UnusableStyle" %>

<tiles:useAttribute name="signet"                    classname="Signet" />
<tiles:useAttribute name="pSubject"                  classname="SignetSubject" />
<tiles:useAttribute name="loggedInPrivilegedSubject" classname="SignetSubject" />
<tiles:useAttribute name="privDisplayType"           classname="PrivDisplayType" />
<tiles:useAttribute name="currentSubsystem"          classname="Subsystem" />

<DIV id="Content"> 
  <DIV id="ViewHead">
    <SPAN class="dropback">
      <%=ResLoaderUI.getString("privilegesGrantedReport.page.hdr") %>
    </SPAN> 
      <H1>
        <%=pSubject.getName()%>
      </H1>
      <SPAN class="ident">
        <%=pSubject.getDescription()%>
      </SPAN>
  </DIV> 
  <!-- ViewHead -->

  <DIV class="tableheader">
  <!-- stubbing out export option for release 1.0
    <A
      href="javascript:;"
      onClick="alert('This will download the data shown in the table in an Excel-readable format.')">
      <IMG
        src="images/export.gif"
        alt="" />
      Export to Excel
    </A>
	-->
    <A href="PersonViewPrint.do">
      <IMG
        src="images/print.gif"
        alt="" />
      <%=ResLoaderUI.getString("privilegesGrantedReport.print.txt") %>
    </A>
  <A href="PrivilegesXML.do" target="_blank">
                <IMG
                	src="images/xml.gif"
                    alt="" />
                <%=ResLoaderUI.getString("privilegesGrantedReport.viewXML.txt") %>
               </A>    
  <H2 class="inlinecontrol">
      <%=privDisplayType.getDescription()%>
	</H2>
	<FORM class="inlinecontrol"
      id="personViewForm"
      name="personViewForm"
      method="post"
      action="PersonView.do">
        <SELECT
          name="privDisplayType"
          id="privDisplayType"
		  onChange="setShowButtonStatus()">
		  <OPTION value="" selected><%=ResLoaderUI.getString("privilegesGrantedReport.privDisplayType.txt") %></OPTION>
          <%=Common.displayOption(PrivDisplayType.CURRENT_RECEIVED, privDisplayType)%>
          <%=Common.displayOption(PrivDisplayType.FORMER_RECEIVED, privDisplayType)%>
          <%=Common.displayOption(PrivDisplayType.CURRENT_GRANTED, privDisplayType)%>
          <%=Common.displayOption(PrivDisplayType.FORMER_GRANTED, privDisplayType)%>
        </SELECT>
        <INPUT
          name="showButton"
          id="showButton"	  
          value="<%=ResLoaderUI.getString("privilegesGrantedReport.show.bt") %>"
          type="submit"
          disabled="disabled"	  
          class="button1" />
    </FORM>
  </DIV> <!-- tableheader -->
  
  <DIV class="tablecontrols">
    <%=ResLoaderUI.getString("privilegesGrantedReport.privilegeTypes.lbl") %>
    <%=Common.subsystemLinks(pSubject, privDisplayType, currentSubsystem)%>
  </DIV> <!-- tablecontrols -->


<!--   		title="<%=ResLoaderUI.getString("privilegesGrantedReport.warning.title") %>"  -->
   <FORM
   		name="checkform"
   		action="Revoke.do"
   		method="post"
   		id="checkform"
	    onSubmit="return confirm
	           (<%=new String("'" + ResLoaderUI.getString("privilegesGrantedReport.warning.txt") + "'") %>);">
	
    <DIV class="tablecontent">    
      <TABLE>
        <TR class="columnhead">
          <%
  if (privDisplayType.equals(PrivDisplayType.CURRENT_GRANTED)
      || privDisplayType.equals(PrivDisplayType.FORMER_GRANTED))
  {
%>
          <TH><%=ResLoaderUI.getString("privilegesGrantedReport.subject.hdr") %></TH>
          <%
  }
%>
          <TH width="40%"><%=ResLoaderUI.getString("privilegesGrantedReport.privilege.hdr")%></TH>
          <TH><%=ResLoaderUI.getString("privilegesGrantedReport.scope.hdr")%></TH>
          <TH><%=ResLoaderUI.getString("privilegesGrantedReport.limits.hdr")%></TH>
          <TH><%=ResLoaderUI.getString("privilegesGrantedReport.status.hdr")%></TH>
          <TH width="60"><%=ResLoaderUI.getString("privilegesGrantedReport.all.hdr")%>
              <INPUT
               name="checkAll"
               type="checkbox"
               id="checkAll"
               onClick="selectAll(this.checked);"
               value="<%=ResLoaderUI.getString("privilegesGrantedReport.checkall.bt")%>" 
               title="<%=ResLoaderUI.getString("privilegesGrantedReport.checkall.title")%>"/>
          </TH>
        </TR>
<%
  Set assignmentSet;
  Set proxySet;
  Subsystem subsystemFilter = null;
  
  if ( !currentSubsystem.equals(Constants.WILDCARD_SUBSYSTEM))
  {
    subsystemFilter = currentSubsystem;
  }
  
  SortedSet assignmentsAndProxies;
  assignmentsAndProxies = Common.getGrantablesForReport(pSubject, subsystemFilter, privDisplayType);
             
  Iterator assignmentsAndProxiesIterator = assignmentsAndProxies.iterator();
  while (assignmentsAndProxiesIterator.hasNext())
  {
    Grantable grantable = (Grantable)(assignmentsAndProxiesIterator.next());

%>
        <TR>
<%
    if (privDisplayType.equals(PrivDisplayType.CURRENT_GRANTED)
        || privDisplayType.equals(PrivDisplayType.FORMER_GRANTED))
    {
		SignetSubject grantee = grantable.getGrantee();
%>
			<!-- subject -->
			<TD>
				<A
					href="PersonView.do?<%=Constants.SIGNET_SOURCE_ID_HTTPPARAMNAME + "=" + grantee.getSourceId() +
						"&" + Constants.SIGNET_SUBJECT_ID_HTTPPARAMNAME + "=" + grantee.getId() + 
						(subsystemFilter == null ? "" : ("&" + Constants.SUBSYSTEM_HTTPPARAMNAME + "=" + subsystemFilter.getId()))%>"
					title="<%=ResLoaderUI.getString("privilegesGrantedReport.person.title") + grantee.getName()%>" >
				<%=grantee.getName()%>
				</A>
			</TD>
<%
    }
%>
			<!-- privilege -->
			<TD>
				<%=Common.popupIcon(grantable)%> <%=Common.privilegeStr(signet, grantable)%>
			</TD>
	
			<!-- scope -->
			<TD>
				<%=Common.scopeStr(grantable)%>
			</TD>
	
			<!-- limits -->
			<TD>
				<%=Common.editLink(loggedInPrivilegedSubject, grantable)%> <%=Common.displayLimitValues(grantable)%>
			</TD>
	
			<!-- status -->
			<TD>
				<%=Common.displayStatus(grantable) %>
			</TD>

			<%=Common.revokeBox(loggedInPrivilegedSubject, grantable, UnusableStyle.DIM) %>
		</TR>
<% 
  }
%>
        <TR >
          <%
    if (privDisplayType.equals(PrivDisplayType.CURRENT_GRANTED)
      || privDisplayType.equals(PrivDisplayType.FORMER_GRANTED))
  {
%>
          <TD>&nbsp;</TD>
          <%
  }
%>
          <TD>&nbsp;</TD>
          <TD>&nbsp;</TD>
          <TD>&nbsp;</TD>
          <TD>&nbsp;</TD>
          <TD width="60" align="center" ><INPUT
              name="revokeButton"
              type="submit"
              disabled="disabled"
              class="button1"
              value="<%=ResLoaderUI.getString("privilegesGrantedReport.revoke.bt") %>"
              title="<%=ResLoaderUI.getString("privilegesGrantedReport.revoke.title.bt") %>" />
          </TD>
        </TR>
      </TABLE>
    </DIV> 
    <!-- tablecontent -->
  </FORM> <!-- checkform -->
</DIV> <!-- Content -->