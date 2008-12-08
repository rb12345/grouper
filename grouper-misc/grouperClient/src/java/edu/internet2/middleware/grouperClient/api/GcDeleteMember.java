/*
 * @author mchyzer
 * $Id: GcDeleteMember.java,v 1.3 2008-12-08 02:55:52 mchyzer Exp $
 */
package edu.internet2.middleware.grouperClient.api;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import edu.internet2.middleware.grouperClient.util.GrouperClientUtils;
import edu.internet2.middleware.grouperClient.ws.GcTransactionType;
import edu.internet2.middleware.grouperClient.ws.GrouperClientWs;
import edu.internet2.middleware.grouperClient.ws.beans.WsDeleteMemberResults;
import edu.internet2.middleware.grouperClient.ws.beans.WsGroupLookup;
import edu.internet2.middleware.grouperClient.ws.beans.WsParam;
import edu.internet2.middleware.grouperClient.ws.beans.WsRestDeleteMemberRequest;
import edu.internet2.middleware.grouperClient.ws.beans.WsSubjectLookup;


/**
 * class to run an delete member web service call
 */
public class GcDeleteMember {

  /** client version */
  private String clientVersion;

  /**
   * assign client version
   * @param theClientVersion
   * @return this for chaining
   */
  public GcDeleteMember assignClientVersion(String theClientVersion) {
    this.clientVersion = theClientVersion;
    return this;
  }
  

  /** group name to delete member from */
  private String groupName;
  
  /**
   * set the group name
   * @param theGroupName
   * @return this for chaining
   */
  public GcDeleteMember assignGroupName(String theGroupName) {
    this.groupName = theGroupName;
    return this;
  }
  
  /** subject lookups */
  private List<WsSubjectLookup> subjectLookups = new ArrayList<WsSubjectLookup>();

  /** params */
  private List<WsParam> params = new ArrayList<WsParam>();

  /**
   * add a param to the list
   * @param paramName
   * @param paramValue
   * @return this for chaining
   */
  public GcDeleteMember addParam(String paramName, String paramValue) {
    this.params.add(new WsParam(paramName, paramValue));
    return this;
  }
  
  /**
   * add a param to the list
   * @param wsParam
   * @return this for chaining
   */
  public GcDeleteMember addParam(WsParam wsParam) {
    this.params.add(wsParam);
    return this;
  }
  
  /** 
   * add a subject lookup
   * @param wsSubjectLookup
   * @return this for chaining
   */
  public GcDeleteMember addSubjectLookup(WsSubjectLookup wsSubjectLookup) {
    this.subjectLookups.add(wsSubjectLookup);
    return this;
  }
  
  /** 
   * add a subject lookup
   * @param subjectId
   * @return this for chaining
   */
  public GcDeleteMember addSubjectId(String subjectId) {
    this.subjectLookups.add(new WsSubjectLookup(subjectId, null, null));
    return this;
  }
  
  /** 
   * add a subject lookup
   * @param subjectIdentifier
   * @return this for chaining
   */
  public GcDeleteMember addSubjectIdentifier(String subjectIdentifier) {
    this.subjectLookups.add(new WsSubjectLookup(null, null, subjectIdentifier));
    return this;
  }
  
  /** act as subject if any */
  private WsSubjectLookup actAsSubject;

  /**
   * assign the act as subject if any
   * @param theActAsSubject
   * @return this for chaining
   */
  public GcDeleteMember assignActAsSubject(WsSubjectLookup theActAsSubject) {
    this.actAsSubject = theActAsSubject;
    return this;
  }
  
  /**
   * validate this call
   */
  private void validate() {
    if (GrouperClientUtils.isBlank(this.groupName)) {
      throw new RuntimeException("Group name is required: " + this);
    }
    if (GrouperClientUtils.length(this.subjectLookups) == 0) {
      throw new RuntimeException("Need at least one subject to add to group: " + this);
    }
  }
  
  /** field name to add member */
  private String fieldName;
  
  /**
   * assign the field name to the request
   * @param theFieldName
   * @return this for chaining
   */
  public GcDeleteMember assignFieldName(String theFieldName) {
    this.fieldName = theFieldName;
    return this;
  }
  
  /** tx type for request */
  private GcTransactionType txType;

  /**
   * assign the tx type
   * @param gcTransactionType
   * @return self for chaining
   */
  public GcDeleteMember assignTxType(GcTransactionType gcTransactionType) {
    this.txType = gcTransactionType;
    return this;
  }
  
  /** if the group detail should be sent back */
  private Boolean includeGroupDetail;
  
  /** if the subject detail should be sent back */
  private Boolean includeSubjectDetail;

  /** subject attribute names to return */
  private Set<String> subjectAttributeNames = new LinkedHashSet<String>();

  /**
   * 
   * @param subjectAttributeName
   * @return this for chaining
   */
  public GcDeleteMember addSubjectAttributeName(String subjectAttributeName) {
    this.subjectAttributeNames.add(subjectAttributeName);
    return this;
  }
  
  /**
   * assign if the group detail should be included
   * @param theIncludeGroupDetail
   * @return this for chaining
   */
  public GcDeleteMember assignIncludeGroupDetail(Boolean theIncludeGroupDetail) {
    this.includeGroupDetail = theIncludeGroupDetail;
    return this;
  }
  
  /**
   * if should include subject detail
   * @param theIncludeSubjectDetail
   * @return this for chaining
   */
  public GcDeleteMember assignIncludeSubjectDetail(Boolean theIncludeSubjectDetail) {
    this.includeSubjectDetail = theIncludeSubjectDetail;
    return this;
  }
  
  /**
   * execute the call and return the results.  If there is a problem calling the service, an
   * exception will be thrown
   * 
   * @return the results
   */
  public WsDeleteMemberResults execute() {
    this.validate();
    WsDeleteMemberResults wsDeleteMemberResults = null;
    try {
      //Make the body of the request, in this case with beans and marshaling, but you can make
      //your request document in whatever language or way you want
      WsRestDeleteMemberRequest deleteMember = new WsRestDeleteMemberRequest();

      deleteMember.setActAsSubjectLookup(this.actAsSubject);

      deleteMember.setFieldName(this.fieldName);
      
      deleteMember.setTxType(this.txType == null ? null : this.txType.name());
      
      if (this.includeGroupDetail != null) {
        deleteMember.setIncludeGroupDetail(this.includeGroupDetail ? "T" : "F");
      }

      if (this.includeSubjectDetail != null) {
        deleteMember.setIncludeSubjectDetail(this.includeSubjectDetail ? "T" : "F");
      }
      
      WsGroupLookup wsGroupLookup = new WsGroupLookup();
      wsGroupLookup.setGroupName(this.groupName);
      
      deleteMember.setWsGroupLookup(wsGroupLookup);
      
      if (this.subjectAttributeNames.size() > 0) {
        deleteMember.setSubjectAttributeNames(
            GrouperClientUtils.toArray(this.subjectAttributeNames, String.class));
      }
      
      WsSubjectLookup[] subjectLookupsResults = GrouperClientUtils.toArray(this.subjectLookups, 
          WsSubjectLookup.class);
      deleteMember.setSubjectLookups(subjectLookupsResults);

      //add params if there are any
      if (this.params.size() > 0) {
        deleteMember.setParams(GrouperClientUtils.toArray(this.params, WsParam.class));
      }
      
      GrouperClientWs grouperClientWs = new GrouperClientWs();
      
      //kick off the web service
      wsDeleteMemberResults = (WsDeleteMemberResults)
        grouperClientWs.executeService("groups/" + this.groupName + "/members", deleteMember, "deleteMember", this.clientVersion);
      
      String resultMessage = wsDeleteMemberResults.getResultMetadata().getResultMessage();
      grouperClientWs.handleFailure(wsDeleteMemberResults, wsDeleteMemberResults.getResults(), resultMessage);
      
    } catch (Exception e) {
      GrouperClientUtils.convertToRuntimeException(e);
    }
    return wsDeleteMemberResults;
    
  }
  
}
