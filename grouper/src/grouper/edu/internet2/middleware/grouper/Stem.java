/*
  Copyright (C) 2004-2007 University Corporation for Advanced Internet Development, Inc.
  Copyright (C) 2004-2007 The University Of Chicago

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package edu.internet2.middleware.grouper;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;

import edu.internet2.middleware.grouper.annotations.GrouperIgnoreClone;
import edu.internet2.middleware.grouper.annotations.GrouperIgnoreDbVersion;
import edu.internet2.middleware.grouper.annotations.GrouperIgnoreFieldConstant;
import edu.internet2.middleware.grouper.cfg.GrouperConfig;
import edu.internet2.middleware.grouper.exception.GrantPrivilegeException;
import edu.internet2.middleware.grouper.exception.GroupAddAlreadyExistsException;
import edu.internet2.middleware.grouper.exception.GroupAddException;
import edu.internet2.middleware.grouper.exception.GrouperRuntimeException;
import edu.internet2.middleware.grouper.exception.GrouperSessionException;
import edu.internet2.middleware.grouper.exception.InsufficientPrivilegeException;
import edu.internet2.middleware.grouper.exception.MemberNotFoundException;
import edu.internet2.middleware.grouper.exception.RevokePrivilegeAlreadyRevokedException;
import edu.internet2.middleware.grouper.exception.RevokePrivilegeException;
import edu.internet2.middleware.grouper.exception.SchemaException;
import edu.internet2.middleware.grouper.exception.StemAddException;
import edu.internet2.middleware.grouper.exception.StemDeleteException;
import edu.internet2.middleware.grouper.exception.StemModifyException;
import edu.internet2.middleware.grouper.exception.StemNotFoundException;
import edu.internet2.middleware.grouper.exception.UnableToPerformAlreadyExistsException;
import edu.internet2.middleware.grouper.exception.UnableToPerformException;
import edu.internet2.middleware.grouper.hibernate.GrouperTransactionType;
import edu.internet2.middleware.grouper.hibernate.HibernateHandler;
import edu.internet2.middleware.grouper.hibernate.HibernateSession;
import edu.internet2.middleware.grouper.hooks.StemHooks;
import edu.internet2.middleware.grouper.hooks.beans.HooksStemBean;
import edu.internet2.middleware.grouper.hooks.logic.GrouperHookType;
import edu.internet2.middleware.grouper.hooks.logic.GrouperHooksUtils;
import edu.internet2.middleware.grouper.hooks.logic.VetoTypeGrouper;
import edu.internet2.middleware.grouper.internal.dao.GrouperDAOException;
import edu.internet2.middleware.grouper.internal.dao.QueryOptions;
import edu.internet2.middleware.grouper.internal.dao.hib3.Hib3GrouperVersioned;
import edu.internet2.middleware.grouper.internal.util.GrouperUuid;
import edu.internet2.middleware.grouper.internal.util.ParameterHelper;
import edu.internet2.middleware.grouper.internal.util.Quote;
import edu.internet2.middleware.grouper.internal.util.U;
import edu.internet2.middleware.grouper.log.EventLog;
import edu.internet2.middleware.grouper.misc.E;
import edu.internet2.middleware.grouper.misc.GrouperDAOFactory;
import edu.internet2.middleware.grouper.misc.GrouperSessionHandler;
import edu.internet2.middleware.grouper.misc.M;
import edu.internet2.middleware.grouper.misc.Owner;
import edu.internet2.middleware.grouper.misc.SaveMode;
import edu.internet2.middleware.grouper.privs.AccessPrivilege;
import edu.internet2.middleware.grouper.privs.NamingPrivilege;
import edu.internet2.middleware.grouper.privs.Privilege;
import edu.internet2.middleware.grouper.privs.PrivilegeHelper;
import edu.internet2.middleware.grouper.subj.GrouperSubject;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.grouper.validator.AddGroupValidator;
import edu.internet2.middleware.grouper.validator.AddStemValidator;
import edu.internet2.middleware.grouper.validator.DeleteStemValidator;
import edu.internet2.middleware.grouper.validator.GrouperValidator;
import edu.internet2.middleware.grouper.validator.NamingValidator;
import edu.internet2.middleware.grouper.validator.NotNullOrEmptyValidator;
import edu.internet2.middleware.subject.SourceUnavailableException;
import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectNotFoundException;

/** 
 * A namespace within the Groups Registry.
 * <p/>
 * @author  blair christensen.
 * @version $Id: Stem.java,v 1.172.2.3 2009-04-10 18:44:21 mchyzer Exp $
 */
@SuppressWarnings("serial")
public class Stem extends GrouperAPI implements Owner, Hib3GrouperVersioned, Comparable {

  /** table for stems table in the db */
  public static final String TABLE_GROUPER_STEMS = "grouper_stems";

  /** uuid col in db */
  public static final String COLUMN_UUID = "uuid";
  
  /** old id col for id conversion */
  public static final String COLUMN_OLD_ID = "old_id";
  
  /** old uuid id col for id conversion */
  public static final String COLUMN_OLD_UUID = "old_uuid";
 
  /** param helper */
  @GrouperIgnoreDbVersion 
  @GrouperIgnoreFieldConstant
  @GrouperIgnoreClone
  private ParameterHelper param = new ParameterHelper();


  //*****  START GENERATED WITH GenerateFieldConstants.java *****//

  /** constant for field name for: createTime */
  public static final String FIELD_CREATE_TIME = "createTime";

  /** constant for field name for: creatorUUID */
  public static final String FIELD_CREATOR_UUID = "creatorUUID";

  /** constant for field name for: dbVersion */
  public static final String FIELD_DB_VERSION = "dbVersion";

  /** constant for field name for: description */
  public static final String FIELD_DESCRIPTION = "description";

  /** constant for field name for: displayExtension */
  public static final String FIELD_DISPLAY_EXTENSION = "displayExtension";

  /** constant for field name for: displayName */
  public static final String FIELD_DISPLAY_NAME = "displayName";

  /** constant for field name for: extension */
  public static final String FIELD_EXTENSION = "extension";

  /** constant for field name for: modifierUUID */
  public static final String FIELD_MODIFIER_UUID = "modifierUUID";

  /** constant for field name for: modifyTime */
  public static final String FIELD_MODIFY_TIME = "modifyTime";

  /** constant for field name for: name */
  public static final String FIELD_NAME = "name";

  /** constant for field name for: parentUUID */
  public static final String FIELD_PARENT_UUID = "parentUUID";

  /** constant for field name for: uuid */
  public static final String FIELD_UUID = "uuid";

  /**
   * fields which are included in db version
   */
  private static final Set<String> DB_VERSION_FIELDS = GrouperUtil.toSet(
      FIELD_CREATE_TIME, FIELD_CREATOR_UUID, FIELD_DESCRIPTION, 
      FIELD_DISPLAY_EXTENSION, FIELD_DISPLAY_NAME, FIELD_EXTENSION, FIELD_MODIFIER_UUID, 
      FIELD_MODIFY_TIME, FIELD_NAME, FIELD_PARENT_UUID, 
      FIELD_UUID);

  /**
   * fields which are included in clone method
   */
  private static final Set<String> CLONE_FIELDS = GrouperUtil.toSet(
      FIELD_CREATE_TIME, FIELD_CREATOR_UUID, FIELD_DB_VERSION, 
      FIELD_DESCRIPTION, FIELD_DISPLAY_EXTENSION, FIELD_DISPLAY_NAME, FIELD_EXTENSION, 
      FIELD_HIBERNATE_VERSION_NUMBER, FIELD_MODIFIER_UUID, FIELD_MODIFY_TIME, 
      FIELD_NAME, FIELD_PARENT_UUID, FIELD_UUID);

  //*****  END GENERATED WITH GenerateFieldConstants.java *****//

  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(Object o) {
    if (o==null || (!(o instanceof Stem))) {
      return 1;
    }
    String thisName = StringUtils.defaultString(this.getName());
    Stem that = (Stem)o;
    String thatName = StringUtils.defaultString(that.getName());
    return thisName.compareTo(thatName);
  }


  /**
   * Search scope: one-level or subtree.
   * @since   1.2.1
   */
  public enum Scope { 
    /** one level (direct children) */
    ONE, 
    
    /** all decendents */
    SUB 
  }; // TODO 20070802 is this the right location?

  /**
   * Hierarchy delimiter.
   */
  public static final String DELIM      = ":";
  /**
   * Default name of root stem.
   */
  public static final String ROOT_NAME  = GrouperConfig.EMPTY_STRING;
  
  
  // PROTECTED CLASS CONSTANTS //
  // TODO 20070419 how can i get rid of this?
  /** root int */
  public static final String ROOT_INT = ":"; // Appease Oracle, et. al.


  // PRIVATE CLASS CONSTANTS //
  /** event log */
  private static final EventLog EL = new EventLog();

  // PRIVATE INSTANCE VARIABLES //
  /** creator of stem */
  @GrouperIgnoreDbVersion 
  @GrouperIgnoreFieldConstant
  @GrouperIgnoreClone
  private Subject creator;
  
  /** modifier of stem */
  @GrouperIgnoreDbVersion 
  @GrouperIgnoreFieldConstant
  @GrouperIgnoreClone
  private Subject modifier;

  private long    createTime;
  private String  creatorUUID;

  private String  description;
  private String  displayExtension;
  private String  displayName;
  private String  extension;
  private String  modifierUUID;
  private long    modifyTime;
  private String  name;
  private String  parentUUID;
  private String  uuid;


  // PUBLIC INSTANCE METHODS //

  /**
   * Add a new group to the registry.
   * <pre class="eg">
   * // Add a group with the extension "edu" beneath this stem.
   * try {
   *   Group edu = ns.addChildGroup("edu", "edu domain");
   * }
   * catch (GroupAddException eGA) {
   *   // Group not added
   * }
   * catch (InsufficientPrivilegeException eIP) {
   *   // Not privileged to add group
   * }
   * </pre>
   * @param   extension         Group's extension
   * @param   displayExtension  Groups' displayExtension
   * @return  The added {@link Group}
   * @throws  GroupAddException 
   * @throws  InsufficientPrivilegeException
   */
  public Group addChildGroup(String extension, String displayExtension) 
    throws  GroupAddException,
            InsufficientPrivilegeException
  {
    return this.internal_addChildGroup(extension, displayExtension, null);
  } // public Group addChildGroup(extension, displayExtension)

  /**
   * Add a new stem to the registry.
   * <pre class="eg">
   * // Add a stem with the extension "edu" beneath this stem.
   * try {
   *   Stem edu = ns.addChildStem("edu", "edu domain");
   * }
   * catch (StemAddException e) {
   *   // Stem not added
   * }
   * </pre>
   * @param   extension         Stem's extension
   * @param   displayExtension  Stem' displayExtension
   * @return  The added {@link Stem}
   * @throws  InsufficientPrivilegeException
   * @throws  StemAddException 
   */
  public Stem addChildStem(String extension, String displayExtension) 
    throws  InsufficientPrivilegeException,
            StemAddException 
  {
    return internal_addChildStem(extension, displayExtension, null);
  } // public Stem addChildStem(extension, displayExtension)
  
  /**
   * Delete this stem from the Groups Registry.
   * <pre class="eg">
   * try {
   *   ns.delete();
   * }
   * catch (InsufficientPrivilegeException eIP) {
   *   // not privileged to delete stem
   * }
   * catch (StemDeleteException eSD) {
   *   // unable to delete stem
   * }
   * </pre>
   * @throws  InsufficientPrivilegeException
   * @throws  StemDeleteException
   */
  public void delete() 
    throws  InsufficientPrivilegeException,
            StemDeleteException
  {
    StopWatch sw = new StopWatch();
    sw.start();
    GrouperSession.validate(GrouperSession.staticGrouperSession());
    if ( !PrivilegeHelper.canStem( this, GrouperSession.staticGrouperSession().getSubject() ) ) {
      throw new InsufficientPrivilegeException(E.CANNOT_STEM);
    }
    DeleteStemValidator v = DeleteStemValidator.validate(this);
    if (v.isInvalid()) {
      throw new StemDeleteException( v.getErrorMessage() );
    }
    try {
      String name = this.getName(); // Preserve name for logging
      this._revokeAllNamingPrivs();
      GrouperDAOFactory.getFactory().getStem().delete( this );
      sw.stop();
      EventLog.info(GrouperSession.staticGrouperSession(), M.STEM_DEL + Quote.single(name), sw);
    }
    catch (GrouperDAOException eDAO)      {
      throw new StemDeleteException( eDAO.getMessage(), eDAO );
    }
    catch (RevokePrivilegeException eRP)  {
      throw new StemDeleteException(eRP.getMessage(), eRP);
    }
    catch (SchemaException eS)            {
      throw new StemDeleteException(eS.getMessage(), eS);
    }
  } // public void delete()

  /**
   * Get groups that are immediate children of this stem.
   * @return  Set of {@link Group} objects.
   * @see     Stem#getChildGroups(Scope)
   */
  public Set getChildGroups() {
    return this.getChildGroups(Scope.ONE);
  }

  /**
   * Get groups that are children of this stem.
   * @param   scope of search: <code>Scope.ONE</code> or <code>Scope.SUB</code>
   * @return  Child groups.
   * @throws  IllegalArgumentException if null scope.
   * @since   1.2.1
   */
  public Set<Group> getChildGroups(Scope scope) 
    throws  IllegalArgumentException {
    return getChildGroups(scope, AccessPrivilege.VIEW_PRIVILEGES, null);
  }

  /**
   * Get groups that are children of this stem.
   * @param   scope of search: <code>Scope.ONE</code> or <code>Scope.SUB</code>
   * @param inPrivSet set of privileges that the grouper session needs one of for the row to be returned.
   * AccessPrivilege has some pre-baked constant sets for use here
   * @param queryOptions 
   * @return  Child groups.
   * @throws  IllegalArgumentException if null scope.
   * @since   1.2.1
   */
  public Set<Group> getChildGroups(Scope scope, Set<Privilege> inPrivSet, QueryOptions queryOptions) 
    throws  IllegalArgumentException {
    if (scope == null) { // TODO 20070815 ParameterHelper
      throw new IllegalArgumentException("null Scope");
    }
    this.param.notNullPrivilegeSet(inPrivSet);

    inPrivSet = AccessPrivilege.filter(inPrivSet);
    
    if (inPrivSet.size() == 0) {
      return new LinkedHashSet<Group>();
    }
    
    GrouperSession grouperSession = GrouperSession.staticGrouperSession();
    Subject     subj    = grouperSession.getSubject();
    Set<Group> findAllChildGroups = 
      GrouperDAOFactory.getFactory().getStem().findAllChildGroupsSecure( this, scope, 
          grouperSession, subj, inPrivSet, queryOptions );
    return findAllChildGroups;
  }

  /**
   * Get groups that are children of this stem and there is a list membership.
   * @param   scope of search: <code>Scope.ONE</code> or <code>Scope.SUB</code>
   * @param inPrivSet set of privileges that the grouper session needs one of for the row to be returned.
   * AccessPrivilege has some pre-baked constant sets for use here
   * @param queryOptions 
   * @return  Child groups.
   * @throws  IllegalArgumentException if null scope.
   * @since   1.2.1
   */
  public Set<Group> getChildMembershipGroups(Scope scope, Set<Privilege> inPrivSet, QueryOptions queryOptions) 
    throws  IllegalArgumentException {
    if (scope == null) { // TODO 20070815 ParameterHelper
      throw new IllegalArgumentException("null Scope");
    }
    this.param.notNullPrivilegeSet(inPrivSet);

    GrouperSession grouperSession = GrouperSession.staticGrouperSession();
    Subject     subj    = grouperSession.getSubject();
    Set<Group> findAllChildGroups = 
      GrouperDAOFactory.getFactory().getStem().findAllChildMembershipGroupsSecure( this, scope, 
          grouperSession, subj, inPrivSet, queryOptions );
    return findAllChildGroups;
  }

  /**
   * Get groups that are children of this stem.
   * @param   scope of search: <code>Scope.ONE</code> or <code>Scope.SUB</code>
   * @param inPrivSet set of privileges that the grouper session needs one of for the row to be returned.
   * AccessPrivilege has some pre-baked constant sets for use here
   * @param queryOptions 
   * @return  Child groups.
   * @throws  IllegalArgumentException if null scope.
   * @since   1.2.1
   */
  public Set<Stem> getChildStems(Scope scope, Set<Privilege> inPrivSet, QueryOptions queryOptions) 
    throws  IllegalArgumentException {
    if (scope == null) { // TODO 20070815 ParameterHelper
      throw new IllegalArgumentException("null Scope");
    }
    this.param.notNullPrivilegeSet(inPrivSet);

    GrouperSession grouperSession = GrouperSession.staticGrouperSession();
    Subject     subj    = grouperSession.getSubject();
    Set<Stem> findAllChildStems = 
      GrouperDAOFactory.getFactory().getStem().findAllChildStemsSecure( this, scope, 
          grouperSession, subj, inPrivSet, queryOptions );
    return findAllChildStems;
  }

  /**
   * get child groups
   * @param privileges privs 
   * @param scope all or direct
   * @return  Child groups where current subject has any of the specified <i>privileges</i>.
   * @throws  IllegalArgumentException if any parameter is null.
   * @since   1.2.1
   * @deprecated use the overload
   */
  @Deprecated
  public Set<Group> getChildGroups(Privilege[] privileges, Scope scope)
      throws  IllegalArgumentException {
    
    return getChildGroups(scope, GrouperUtil.toSet(privileges), null);
  }

  /**
   * Get stems that are immediate children of this stem.
   * @return  Set of {@link Stem} objects.
   * @see     Stem#getChildStems(Scope)
   */
  public Set<Stem> getChildStems() {
    return this.getChildStems(Scope.ONE);
  }

  /**
   * Get stems that are children of this stem.
   * @param   scope of search: <code>Scope.ONE</code> or <code>Scope.SUB</code>
   * @return  Child stems.
   * @throws  IllegalArgumentException if null scope.
   * @since   1.2.1
   */
  public Set<Stem> getChildStems(Scope scope) 
    throws  IllegalArgumentException
  {
    if (scope == null) { // TODO 20070815 ParameterHelper
      throw new IllegalArgumentException("null Scope");
    }
    Set<Stem> stems = new LinkedHashSet();
    for ( Stem child : GrouperDAOFactory.getFactory().getStem().findAllChildStems( this, scope ) ) {
      stems.add(child);
    }
    return stems;
  }

  /**
   * get child stems
   * @param privileges privs
   * @param scope all or direct
   * @return  Child (or deeper) stems where current subject has any of the specified <i>privileges</i>.  Parent stems of grandchild (or deeper) groups where the current subject has any of the specified <i>privileges</i>.
   * @throws  IllegalArgumentException if any parameter is null.
   * @since   1.2.1
   */
  public Set<Stem> getChildStems(Privilege[] privileges, Scope scope)
    throws  IllegalArgumentException 
  {
    this.param.notNullPrivilegeArray(privileges); 

    Set<Stem> stems = new LinkedHashSet();
    // TODO 20070824 this could be a lot prettier
    for ( Stem stem : this.getChildStems(scope) ) {

      for ( Privilege priv : PrivilegeHelper.getNamingPrivileges(privileges) ) {
        try {
          PrivilegeHelper.dispatch( GrouperSession.staticGrouperSession(), stem, 
              GrouperSession.staticGrouperSession().getSubject(), priv );
          stems.add(stem);
          break; // we only care that one privilege matches
        }
        catch (InsufficientPrivilegeException eIP) {
          // ignore
        }
        catch (SchemaException eSchema) {
          // ignore
        }
      }

      if ( !stems.contains(stem) ) { // no matching naming privileges so checking access privilegees
        // filtering out naming privileges will happen in "#getChildGroups(Privilege[], Scope)"
        for ( Group group : stem.getChildGroups(privileges, scope) ) {
          stems.add( group.getParentStem() );
        }
      }

    }
    return stems;
  }

  /**
   * Get subject that created this stem.
   * <pre class="eg">
   * // Get creator of this stem.
   * try {
   *   Subject creator = ns.getCreateSubject();
   * }
   * catch (SubjectNotFoundException e) {
   *   // Couldn't find subject
   * }
   * </pre>
   * @return  {@link Subject} that created this stem.
   * @throws  SubjectNotFoundException
   */
  public Subject getCreateSubject() 
    throws  SubjectNotFoundException
  {
    if (this.creator == null) {
      try {
        this.creator = MemberFinder.findByUuid( GrouperSession.staticGrouperSession(), 
            this.getCreatorUuid() ).getSubject();
      }
      catch (MemberNotFoundException eMNF) {
        throw new SubjectNotFoundException( eMNF.getMessage(), eMNF );
      }
    }
    return this.creator; 
  } // public Subject getCreateSubject()
  
  /**
   * Get creation time for this stem.
   * <pre class="eg">
   * // Get create time.
   * Date created = ns.getCreateTime();
   * </pre>
   * @return  {@link Date} that this stem was created.
   */
  public Date getCreateTime() {
    return new Date( this.getCreateTimeLong() );
  } // public Date getCreateTime()

  /**
   * Get subjects with CREATE privilege on this stem.
   * <pre class="eg">
   * Set creators = ns.getCreators();
   * </pre>
   * @return  Set of {@link Subject} objects
   * @throws  GrouperRuntimeException
   */
  public Set getCreators() 
    throws  GrouperRuntimeException
  {
    return GrouperSession.staticGrouperSession().getNamingResolver().getSubjectsWithPrivilege(this, NamingPrivilege.CREATE);
  }

  /**
   * Get stem description.
   * <pre class="eg">
   * // Get description
   * String description = ns.getDescription();
   * </pre>
   * @return  Stem description.
   */
  public String getDescription() {
    String desc = this.description;
    if (desc == null) {
      desc = GrouperConfig.EMPTY_STRING;
    }
    return desc;
  } 
 
  /**
   * Get stem displayExtension.
   * <pre class="eg">
   * // Get displayExtension
   * String displayExtn = ns.getDisplayExtension();
   * </pre>
   * @return  Stem displayExtension.
   */
  public String getDisplayExtension() {
    String val = this.getDisplayExtensionDb();
    if (val.equals(ROOT_INT)) {
      return ROOT_NAME;
    }
    return val;
  }
 
  /**
   * Get stem displayName.
   * <pre class="eg">
   * // Get displayName
   * String displayName = ns.getDisplayName();
   * </pre>
   * @return  Stem displayName.
   */
  public String getDisplayName() {
    String val = this.getDisplayNameDb();
    if (val.equals(ROOT_INT)) {
      return ROOT_NAME;
    }
    return val;
  }
 
  /**
   * Get stem extension.
   * <pre class="eg">
   * // Get extension
   * String extension = ns.getExtension();
   * </pre>
   * @return  Stem extension.
   */
  public String getExtension() {
    String val = this.getExtensionDb();
    if (val.equals(ROOT_INT)) {
      return ROOT_NAME;
    }
    return val;
  }
 
  /**
   * Get subject that last modified this stem.
   * <pre class="eg">
   * // Get last modifier of this stem.
   * try {
   *   Subject modifier = ns.getModifySubject();
   * }
   * catch (SubjectNotFoundException e) {
   *   // Couldn't find subject
   * }
   * </pre>
   * @return  {@link Subject} that last modified this stem.
   * @throws  SubjectNotFoundException
   */
  public Subject getModifySubject() 
    throws  SubjectNotFoundException
  {
    if (this.modifier == null) {
      if ( this.getModifierUuid() == null) {
        throw new SubjectNotFoundException("stem has not been modified");
      }
      try {
        this.modifier = MemberFinder.findByUuid( GrouperSession.staticGrouperSession(), 
            this.getModifierUuid() ).getSubject();
      }
      catch (MemberNotFoundException eMNF) {
        throw new SubjectNotFoundException( eMNF.getMessage(), eMNF );
      }
    }
    return this.modifier; 
  } // public Subject getModifySubject()
  
  /**
   * Get last modified time for this stem.
   * <pre class="eg">
   * // Get last modified time.
   * Date modified = ns.getModifyTime();
   * </pre>
   * @return  {@link Date} that this stem was last modified.
   */
  public Date getModifyTime() {
    return new Date( this.getModifyTimeLong() );
  } // public Date getModifyTime()

  /**
   * Get stem name.
   * <pre class="eg">
   * // Get name
   * String name = ns.getName();
   * </pre>
   * @return  Stem name.
   */ 
  public String getName() {
    String val = this.getNameDb();
    if (val.equals(ROOT_INT)) {
      return ROOT_NAME;
    }
    return val;
  }

  /**
   * Get parent stem.
   * <pre class="eg">
   * // Get parent
   * Stem parent = ns.getParentStem();
   * </pre>
   * @return  Parent {@link Stem}.
   * @throws StemNotFoundException if stem not found
   */
  public Stem getParentStem() 
    throws StemNotFoundException
  {
    String uuid = this.getParentUuid();
    if (uuid == null) {
      throw new StemNotFoundException();
    }
    Stem parent = GrouperDAOFactory.getFactory().getStem().findByUuid(uuid);
    return parent;
  } // public Stem getParentStem()

  /**
   * Get privileges that the specified subject has on this stem.
   * <pre class="eg">
   * Set privs = ns.getPrivs(subj);
   * </pre>
   * @param   subj  Get privileges for this subject.
   * @return  Set of {@link NamingPrivilege} objects.
   */
  public Set<NamingPrivilege> getPrivs(Subject subj) {
    return GrouperSession.staticGrouperSession().getNamingResolver().getPrivileges(this, subj);
  } 

  /**
   * Get subjects with STEM privilege on this stem.
   * <pre class="eg">
   * Set stemmers = ns.getStemmers();
   * </pre>
   * @return  Set of {@link Subject} objects
   * @throws  GrouperRuntimeException
   */
  public Set getStemmers() 
    throws  GrouperRuntimeException
  {
    return GrouperSession.staticGrouperSession().getNamingResolver().getSubjectsWithPrivilege(this, NamingPrivilege.STEM);
  } 

  /**
   */
  public String getUuid() {
    return this.uuid;
  } // public String getUuid()

  /**
   * Grant a privilege on this stem.
   * <pre class="eg">
   * try {
   *   ns.grantPriv(subj, NamingPrivilege.CREATE);
   * }
   * catch (GrantPrivilegeException e) {
   *   // Error granting privilege
   * }
   * </pre>
   * @param   subj  Grant privilege to this subject.
   * @param   priv  Grant this privilege.
   * @throws  GrantPrivilegeException
   * @throws  InsufficientPrivilegeException
   * @throws  SchemaException
   */
  public void grantPriv(Subject subj, Privilege priv)
    throws  GrantPrivilegeException,        // TODO 20070820 stop throwing
            InsufficientPrivilegeException, // TODO 20070820 stop throwing
            SchemaException                 // TODO 20070820 stop throwing
  {
    grantPriv(subj, priv, true);
    
  }
  /**
   * Grant a privilege on this stem.
   * <pre class="eg">
   * try {
   *   ns.grantPriv(subj, NamingPrivilege.CREATE);
   * }
   * catch (GrantPrivilegeException e) {
   *   // Error granting privilege
   * }
   * </pre>
   * @param   subj  Grant privilege to this subject.
   * @param   priv  Grant this privilege.
   * @param exceptionIfAlreadyMember if false, and subject is already a member,
   * then dont throw a MemberAddException if the member is already in the group
   * @throws  GrantPrivilegeException
   * @throws  InsufficientPrivilegeException
   * @throws  SchemaException
   * @return false if it already existed, true if it didnt already exist
   */
  public boolean grantPriv(Subject subj, Privilege priv, boolean exceptionIfAlreadyMember) 
    throws  GrantPrivilegeException,        // TODO 20070820 stop throwing
            InsufficientPrivilegeException, // TODO 20070820 stop throwing
            SchemaException                 // TODO 20070820 stop throwing
  {
    StopWatch sw = new StopWatch();
    sw.start();
    boolean didNotExist = true;
    try {
      GrouperSession.staticGrouperSession().getNamingResolver().grantPrivilege(this, subj, priv);
    }
    catch (UnableToPerformAlreadyExistsException eUTP) {
      if (exceptionIfAlreadyMember) {
        throw new GrantPrivilegeAlreadyExistsException( eUTP.getMessage(), eUTP );
      }
      didNotExist = false;
    }
    catch (UnableToPerformException eUTP) {
      throw new GrantPrivilegeException( eUTP.getMessage(), eUTP );
    }
    sw.stop();
    if (didNotExist) {
      EL.stemGrantPriv(GrouperSession.staticGrouperSession(), this.getName(), subj, priv, sw);
    }
    return didNotExist;
  } 

  /**
   * Check whether a subject has the CREATE privilege on this stem.
   * <pre class="eg">
   * if (ns.hasCreate(subj)) {
   *   // Has CREATE
   * }
   *   // Does not have CREATE
   * } 
   * </pre>
   * @param   subj  Check whether this subject has CREATE.
   * @return  Boolean true if the subject has CREATE.
   */
  public boolean hasCreate(Subject subj) {
    return GrouperSession.staticGrouperSession().getNamingResolver().hasPrivilege(this, subj, NamingPrivilege.CREATE);
  } 
 
  /**
   * Check whether a member has the STEM privilege on this stem.
   * <pre class="eg">
   * if (ns.hasStem(subj)) {
   *   // Has STEM
   * }
   *   // Does not have STEM
   * } 
   * </pre>
   * @param   subj  heck whether this subject has STEM.
   * @return  Boolean true if the subject has STEM.
   */
  public boolean hasStem(Subject subj) {
    return GrouperSession.staticGrouperSession().getNamingResolver().hasPrivilege(this, subj, NamingPrivilege.STEM);
  } 
 
  /**
   * TODO 20070813 make public?
   * @param group group
   * @return  True if <i>group</i> is child, at any depth, of this stem.
   * @throws  IllegalArgumentException if <i>group</i> is null.
   * @since   1.2.1
   */
  public boolean isChildGroup(Group group)
    throws  IllegalArgumentException
  {
    if (group == null) { // TODO 20070813 ParameterHelper
      throw new IllegalArgumentException("null Group");
    }

    if (this.isRootStem()) {
      return true;
    } 

    String stemName = this.getName();
    String groupName = group.getName();

    if (groupName.length() <= (stemName.length() + DELIM.length())) {
      return false;
    }
    
    if ((stemName + DELIM).equals(groupName.substring(0, stemName.length() + DELIM.length()))) {
      return true;
    }

    return false;
  }

  /**
   * TODO 20070813 make public?
   * @param stem stem
   * @return  True if <i>stem</i> is child, at any depth, of this stem.
   * @throws  IllegalArgumentException if <i>stem</i> is null.
   * @since   1.2.1
   */
  public boolean isChildStem(Stem stem) 
    throws  IllegalArgumentException
  {
    if (stem == null) { // TODO 20070813 ParameterHelper
      throw new IllegalArgumentException("null Stem");
    }

    String thisName = this.getName();
    String stemName = stem.getName();

    if (
         ( thisName.equals( stemName ) )  // can't be child of self
         ||
         stem.isRootStem()                            // root stem can't be child
       )
    {
      return false;
    }
    if ( this.isRootStem() ) {
      return true; // all stems are children
    }

    if (stemName.length() <= (thisName.length() + DELIM.length())) {
      return false;
    }
    
    if ((thisName + DELIM).equals(stemName.substring(0, thisName.length() + DELIM.length()))) {
      return true;
    }

    return false;
  }

  /**
   * @return  Boolean true if this is the root stem of the Groups Registry.
   * @since   1.2.0
   */
  public boolean isRootStem() {
    return ROOT_INT.equals( this.getNameDb() );
  } 

  /**
   * Revoke all privileges of the specified type on this stem.
   * <pre class="eg">
   * try {
   *   ns.revokePriv(NamingPrivilege.CREATE);
   * }
   * catch (InsufficientPrivilegeException eIP) {
   *   // Not privileged to revoke this privilege
   * }
   * catch (RevokePrivilegeException eRP) {
   *   // Error revoking privilege
   * }
   * </pre>
   * @param   priv  Revoke this privilege.
   * @throws  InsufficientPrivilegeException
   * @throws  RevokePrivilegeException
   * @throws  SchemaException
   */
  public void revokePriv(Privilege priv) 
    throws  InsufficientPrivilegeException, // TODO 20070820 stop throwing this
            RevokePrivilegeException,
            SchemaException                 // TODO 20070820 stop throwing this
  {
    StopWatch sw = new StopWatch();
    sw.start();
    if ( Privilege.isAccess(priv) ) {
      throw new SchemaException("attempt to use access privilege");
    }
    try {
      GrouperSession.staticGrouperSession().getNamingResolver().revokePrivilege(this, priv);
    }
    catch (UnableToPerformException e) {
      throw new RevokePrivilegeException( e.getMessage(), e );
    }
    sw.stop();
    EL.stemRevokePriv(GrouperSession.staticGrouperSession(), this.getName(), priv, sw);
  }
 
  /**
   * Revoke a privilege on this stem.
   * <pre class="eg">
   * try {
   *   ns.revokePriv(subj, NamingPrivilege.CREATE);
   * }
   * catch (InsufficientPrivilegeException eIP) {
   *   // Not privileged to revoke this privilege
   * }
   * catch (RevokePrivilegeException eRP) {
   *   // Error revoking privilege
   * }
   * </pre>
   * @param   subj  Revoke privilege from this subject.
   * @param   priv  Revoke this privilege.
   * @throws  InsufficientPrivilegeException
   * @throws  RevokePrivilegeException
   * @throws  SchemaException
   */
  public void revokePriv(Subject subj, Privilege priv)
    throws  InsufficientPrivilegeException, // TODO 20070820 stop throwing this
            RevokePrivilegeException,
            SchemaException                 // TODO 20070820 stop throwing this
  {
    revokePriv(subj, priv, true);
  }

  /**
   * Revoke a privilege on this stem.
   * <pre class="eg">
   * try {
   *   ns.revokePriv(subj, NamingPrivilege.CREATE);
   * }
   * catch (InsufficientPrivilegeException eIP) {
   *   // Not privileged to revoke this privilege
   * }
   * catch (RevokePrivilegeException eRP) {
   *   // Error revoking privilege
   * }
   * </pre>
   * @param   subj  Revoke privilege from this subject.
   * @param   priv  Revoke this privilege.
   * @param exceptionIfAlreadyRevoked if false, and subject is already a member,
   * then dont throw a MemberAddException if the member is already in the group
   * @return false if it was already revoked, true if it wasnt already deleted
   * @throws  InsufficientPrivilegeException
   * @throws  RevokePrivilegeException
   * @throws  SchemaException
   */
  public boolean revokePriv(Subject subj, Privilege priv, boolean exceptionIfAlreadyRevoked)
    throws  InsufficientPrivilegeException, // TODO 20070820 stop throwing this
            RevokePrivilegeException,
            SchemaException                 // TODO 20070820 stop throwing this
  {
    boolean wasntAlreadyRevoked = true;
    StopWatch sw = new StopWatch();
    sw.start();
    if ( Privilege.isAccess(priv) ) {
      throw new SchemaException("attempt to use access privilege");
    }
    try {
      GrouperSession.staticGrouperSession().getNamingResolver().revokePrivilege(this, subj, priv);
    } catch (UnableToPerformAlreadyExistsException eUTP) {
      if (exceptionIfAlreadyRevoked) {
        throw new RevokePrivilegeAlreadyRevokedException( eUTP.getMessage(), eUTP );
      }
      wasntAlreadyRevoked = false;
    } catch (UnableToPerformException e) {
      throw new RevokePrivilegeException( e.getMessage(), e );
    }
    sw.stop();
    if (wasntAlreadyRevoked) {
      EL.stemRevokePriv(GrouperSession.staticGrouperSession(), this.getName(), subj, priv, sw);
    }
    return wasntAlreadyRevoked;
  } 

  /**
   * Set stem description.
   * <pre class="eg">
   * // Set description
   * try {
   *  ns.setDescription(value);
   * }
   * }
   * catch (InsufficientPrivilegeException e0) {
   *   // Not privileged to set description
   * catch (StemModifyException e1) {
   *   // Error setting description
   * }
   * </pre>
   * @param   value   Set description to this value.
   * @throws  InsufficientPrivilegeException
   * @throws  StemModifyException
   */
  public void setDescription(String value) 
    throws  InsufficientPrivilegeException,
            StemModifyException
  {
    StopWatch sw = new StopWatch();
    sw.start();
    if ( !PrivilegeHelper.canStem( this, GrouperSession.staticGrouperSession().getSubject() ) ) {
      throw new InsufficientPrivilegeException(E.CANNOT_STEM);
    }
    try {
      this.setDescriptionDb(value);
      this.internal_setModified();
      if (!GrouperConfig.getPropertyBoolean(GrouperConfig.PROP_SETTERS_DONT_CAUSE_QUERIES, false)) {

        GrouperDAOFactory.getFactory().getStem().update( this );
      }
      sw.stop();
      EL.stemSetAttr(GrouperSession.staticGrouperSession(), this.getName(), GrouperConfig.ATTR_DESCRIPTION, value, sw);
    }
    catch (GrouperDAOException eDAO) {
      throw new StemModifyException( "unable to set description: " + eDAO.getMessage(), eDAO );
    }
  }

  /**
   * will be implemented soon
   * @throws StemModifyException if problem
   */
  public void store() {
    if (GrouperConfig.getPropertyBoolean(GrouperConfig.PROP_SETTERS_DONT_CAUSE_QUERIES, false)) {
      GrouperDAOFactory.getFactory().getStem().update( this );
    }
  }
  
  /**
   * Set <i>displayExtension</i>.
   * <p>This will also update the <i>displayName</i> of all child stems and groups.</p>
   * <pre class="eg">
   * try {
   *  ns.setDisplayExtension(value);
   * }
   * catch (InsufficientPrivilegeException eIP) {
   *   // Not privileged to set displayExtension
   * catch (StemModifyException eNSM) {
   *   // Error setting displayExtension
   * }
   * </pre>
   * @param   value   Set displayExtension to this value.
   * @throws  InsufficientPrivilegeException
   * @throws  StemModifyException
   */
  public void setDisplayExtension(String value) 
    throws  InsufficientPrivilegeException,
            StemModifyException
  {
    StopWatch sw = new StopWatch();
    sw.start();
    NamingValidator nv = NamingValidator.validate(value);
    if (nv.isInvalid()) {
      if ( this.isRootStem() && value.equals(ROOT_NAME) ) {
        // Appease Oracle
        value = ROOT_INT;   
      }
      else {
        throw new StemModifyException( nv.getErrorMessage() );
      }
    }
    if ( !PrivilegeHelper.canStem( this, GrouperSession.staticGrouperSession().getSubject() ) ) {
      throw new InsufficientPrivilegeException(E.CANNOT_STEM);
    }
    try {
      this.setDisplayExtensionDb(value);
      this.internal_setModified();
      if (this.isRootStem()) {
        this.setDisplayNameDb(value);
      }
      else {
        try {
          this.setDisplayNameDb( U.constructName( this.getParentStem().getDisplayName(), value ) );
        }
        catch (StemNotFoundException eShouldNeverHappen) {
          throw new IllegalStateException( 
            "this should never happen: non-root stem without parent: " + eShouldNeverHappen.getMessage(), eShouldNeverHappen 
          );
        }
      }
      if (!GrouperConfig.getPropertyBoolean(GrouperConfig.PROP_SETTERS_DONT_CAUSE_QUERIES, false)) {
        // Now iterate through all child groups and stems, renaming each.
        GrouperDAOFactory.getFactory().getStem().renameStemAndChildren( this, this._renameChildren(GrouperConfig.ATTR_DISPLAY_EXTENSION) );
      }
    }
    catch (GrouperDAOException eDAO) {
      throw new StemModifyException( "unable to set displayExtension: " + eDAO.getMessage(), eDAO );
    }
    sw.stop();
    // Reset for logging purposes
    if (value.equals(ROOT_INT)) {
      value = ROOT_NAME;
    }
    EL.stemSetAttr(GrouperSession.staticGrouperSession(), this.getName(), GrouperConfig.ATTR_DISPLAY_EXTENSION, value, sw);
  } // public void setDisplayExtension(value)

  /**
   * Set <i>extension</i>.
   * <p>This will also update the <i>name</i> of all child stems and groups.</p>
   * <pre class="eg">
   * try {
   *  ns.setExtension(value);
   * }
   * catch (InsufficientPrivilegeException eIP) {
   *   // Not privileged to set "extension"
   * catch (StemModifyException eNSM) {
   *   // Error setting "extension"
   * }
   * </pre>
   * @param   value   Set <i>extension</i> to this value.
   * @throws  InsufficientPrivilegeException
   * @throws  StemModifyException
   */
  public void setExtension(String value) 
    throws  InsufficientPrivilegeException,
            StemModifyException
  {
    // TODO 20070531 DRY w/ "setDisplayExtension"
    StopWatch sw = new StopWatch();
    sw.start();
    NamingValidator nv = NamingValidator.validate(value);
    if (nv.isInvalid()) {
      if ( this.isRootStem() && value.equals(ROOT_NAME) ) {
        // Appease Oracle
        value = ROOT_INT;   
      }
      else {
        throw new StemModifyException( nv.getErrorMessage() );
      }
    }
    if ( !PrivilegeHelper.canStem( this, GrouperSession.staticGrouperSession().getSubject() ) ) {
      throw new InsufficientPrivilegeException(E.CANNOT_STEM);
    }
    try {
      this.setExtensionDb(value);
      this.internal_setModified();
      if (this.isRootStem()) {
        this.setNameDb(value);
      }
      else {
        try {
          this.setNameDb( U.constructName( this.getParentStem().getName(), value ) );
        }
        catch (StemNotFoundException eShouldNeverHappen) {
          throw new IllegalStateException( 
            "this should never happen: non-root stem without parent: " + eShouldNeverHappen.getMessage(), eShouldNeverHappen 
          );
        }
      }
      if (!GrouperConfig.getPropertyBoolean(GrouperConfig.PROP_SETTERS_DONT_CAUSE_QUERIES, false)) {
  
        // Now iterate through all child groups and stems, renaming each.
        GrouperDAOFactory.getFactory().getStem().renameStemAndChildren( this, this._renameChildren(GrouperConfig.ATTR_EXTENSION) );
      }
    }
    catch (GrouperDAOException eDAO) {
      throw new StemModifyException( "unable to set extension: " + eDAO.getMessage(), eDAO );
    }
    sw.stop();
    // Reset for logging purposes
    if (value.equals(ROOT_INT)) {
      value = ROOT_NAME;
    }
    EL.stemSetAttr( GrouperSession.staticGrouperSession(), this.getName(), GrouperConfig.ATTR_EXTENSION, value, sw );
  } // public void setExtension(value)

  public String toString() {
    return new ToStringBuilder(this)
      .append( GrouperConfig.ATTR_DISPLAY_NAME, this.getDisplayName()  )
      .append( GrouperConfig.ATTR_NAME,  this.getName()         )
      .append( "uuid",                this.getUuid()         )
      .append( "creator",             this.getCreatorUuid()  )
      .append( "modifier",            this.getModifierUuid() )
      .toString();
  } // public String toString()


  // PROTECTED CLASS METHODS //

  /**
   * add root stem
   * @param s session
   * @param changed if you want to know if it was added, pass in array of size one, else null
   * @since   1.2.0
   * @return stem
   * @throws GrouperRuntimeException is problem
   */
  public static Stem internal_addRootStem(GrouperSession s, boolean[] changed) 
    throws  GrouperRuntimeException {
    Stem root = null;
    try {
      root = StemFinder.findByName(s, ROOT_INT);
    } catch (StemNotFoundException snfe) {
    
    }
    
    //dont add twice!
    if (root != null) {
      
      if (!StringUtils.equals(root.getDisplayExtensionDb(), ROOT_INT)) {
        throw new RuntimeException("Root display extension should be '" 
            + ROOT_INT + "' but is: '" + root.getDisplayExtensionDb() + "'" );
      }
      if (!StringUtils.equals(root.getDisplayNameDb(), ROOT_INT)) {
        throw new RuntimeException("Root display name should be '" 
            + ROOT_INT + "' but is: '" + root.getDisplayNameDb() + "'" );
      }
      if (!StringUtils.equals(root.getExtensionDb(), ROOT_INT)) {
        throw new RuntimeException("Root extension should be '" 
            + ROOT_INT + "' but is: '" + root.getExtensionDb() + "'" );
      }
      if (!StringUtils.equals(root.getNameDb(), ROOT_INT)) {
        throw new RuntimeException("Root name should be '" 
            + ROOT_INT + "' but is: '" + root.getNameDb() + "'" );
      }
      if (GrouperUtil.length(changed) > 0) {
        changed[0] = false;
      }
      return root;
    }
    if (GrouperUtil.length(changed) > 0) {
      changed[0] = false;
    }
    
    //note, no need for GrouperSession inverse of control
    try {
      root = new Stem();
      root.setCreatorUuid( s.getMember().getUuid() );
      root.setCreateTimeLong( new Date().getTime() );
      root.setDisplayExtensionDb(ROOT_INT);
      root.setDisplayNameDb(ROOT_INT);
      root.setExtensionDb(ROOT_INT);
      root.setNameDb(ROOT_INT);
      root.setUuid( GrouperUuid.getUuid() );
      GrouperDAOFactory.getFactory().getStem().createRootStem(root) ;
      return root;
    }
    catch (GrouperDAOException eDAO) {
      String msg = E.STEM_ROOTINSTALL + eDAO.getMessage();
      LOG.fatal(msg);
      throw new GrouperRuntimeException(msg, eDAO);
    }
  } // protected static Stem internal_addRootStem(GrouperSession s)

  /** logger */
  private static final Log LOG = GrouperUtil.getLog(Stem.class);

  /**
   * set modified
   * @since   1.2.0
   */
  public void internal_setModified() {
    this.setModifierUuid( GrouperSession.staticGrouperSession().getMember().getUuid() );
    this.setModifyTimeLong(  new Date().getTime()    );
  } // protected void internal_setModified()


  // PROTECTED INSTANCE METHODS //

  /**
   * add child group with uuid
   * @param extn extension
   * @param dExtn display extension
   * @param uuid uuid
   * @return group 
   * @throws GroupAddException if problem 
   * @throws InsufficientPrivilegeException if problem 
   * @since   1.2.0
   */
  public Group internal_addChildGroup(final String extn, final String dExtn, final String uuid) 
    throws  GroupAddException, InsufficientPrivilegeException {
    
    //this must be in one transaction
    try {
      return (Group)HibernateSession.callbackHibernateSession(GrouperTransactionType.READ_WRITE_OR_USE_EXISTING, new HibernateHandler() {
  
        public Object callback(HibernateSession hibernateSession)
            throws GrouperDAOException {
          StopWatch sw = new StopWatch();
          sw.start();
          if ( !PrivilegeHelper.canCreate( GrouperSession.staticGrouperSession(), 
              Stem.this, GrouperSession.staticGrouperSession().getSubject() ) ) {
            throw new GrouperDAOException(null, new InsufficientPrivilegeException(E.CANNOT_CREATE));
          } 
          GrouperValidator v = AddGroupValidator.validate(Stem.this, extn, dExtn);
          if (v.isInvalid()) {
            String errorMessage = StringUtils.defaultString(v.getErrorMessage());
            if (errorMessage.startsWith(AddGroupValidator.GROUP_ALREADY_EXISTS_WITH_NAME_PREFIX)) {
              throw new GrouperDAOException(null, new GroupAddAlreadyExistsException(errorMessage));
            }
            throw new GrouperDAOException(null, new GroupAddException( errorMessage ));
          }
          try {
            Map<String, String> attrs = new HashMap<String, String>();
            attrs.put( GrouperConfig.ATTR_DISPLAY_EXTENSION, dExtn );
            attrs.put( GrouperConfig.ATTR_DISPLAY_NAME, U.constructName( Stem.this.getDisplayName(), dExtn ) );
            attrs.put( GrouperConfig.ATTR_EXTENSION,  extn );
            attrs.put( GrouperConfig.ATTR_NAME,  U.constructName( Stem.this.getName(), extn ) );
            Set types = new LinkedHashSet();
            types.add( GroupTypeFinder.find("base") ); 
            Group group = new Group();
            group.setAttributes(attrs);
            group.setCreateTimeLong( new Date().getTime() );
            group.setCreatorUuid( GrouperSession.staticGrouperSession().getMember().getUuid() );
            group.setParentUuid( Stem.this.getUuid() );
            group.setTypes(types);
            v = NotNullOrEmptyValidator.validate(uuid);
            if (v.isInvalid()) {
              group.setUuid( GrouperUuid.getUuid() );
            }
            else {
              group.setUuid(uuid);
            }
  
            GrouperSubject  subj  = new GrouperSubject(group);
            Member _m = new Member();
            _m.setSubjectIdDb( subj.getId() );
            _m.setSubjectSourceIdDb( subj.getSource().getId() );
            _m.setSubjectTypeId( subj.getType().getName() );
            // TODO 20070328 this is incredibly ugly.  making it even worse is that i am also checking
            //               for existence in the dao as well.
            if (uuid == null) {
              _m.setUuid( GrouperUuid.getUuid() ); // assign a new uuid
            }
            else {
              try {
                // member already exists.  use existing uuid.
                _m.setUuid( GrouperDAOFactory.getFactory().getMember().findBySubject(subj).getUuid() );
              }
              catch (MemberNotFoundException eMNF) {
                // couldn't find member.  assign new uuid.
                _m.setUuid( GrouperUuid.getUuid() ); 
              }
            }
  
            //CH 20080220: this will start saving the stem
            GrouperDAOFactory.getFactory().getStem().createChildGroup( Stem.this, group, _m );
              
            sw.stop();
            EventLog.info(GrouperSession.staticGrouperSession(), M.GROUP_ADD + Quote.single(group.getName()), sw);
            _grantDefaultPrivsUponCreate(group);
            return group;
          } catch (GroupAddException gae)        {
            throw new GrouperDAOException(null, gae );
          }
          catch (SchemaException eS)              {
            throw new GrouperDAOException(null, new GroupAddException(E.CANNOT_CREATE_GROUP + eS.getMessage(), eS));
          }
          catch (SourceUnavailableException eSU)  {
            throw new GrouperDAOException(new GroupAddException(E.CANNOT_CREATE_GROUP + eSU.getMessage(), eSU));
          }
        }
        
      });
    } catch (GrouperDAOException gde) {
      Throwable cause = gde.getCause();
      if (cause instanceof GroupAddException) {
        throw (GroupAddException)cause;
      }
      if (cause instanceof InsufficientPrivilegeException) {
        throw (InsufficientPrivilegeException)cause;
      }
      throw new GroupAddException( E.CANNOT_CREATE_GROUP + gde.getMessage(), gde );
    }

  } 

  /**
   * add child stem with uuid
   * @since   1.2.0
   * @param extn extension
   * @param dExtn display extension
   * @param uuid uuid
   * @return the new stem
   * @throws StemAddException if problem
   * @throws InsufficientPrivilegeException if problem
   */
  public Stem internal_addChildStem(String extn, String dExtn, String uuid) 
    throws  StemAddException,
            InsufficientPrivilegeException
  {
    StopWatch sw = new StopWatch();
    sw.start();
    if ( !PrivilegeHelper.canStem( this, GrouperSession.staticGrouperSession().getSubject() ) ) {
      throw new InsufficientPrivilegeException(E.CANNOT_STEM);
    } 
    GrouperValidator v = AddStemValidator.validate(this, extn, dExtn);
    if (v.isInvalid()) {
      throw new StemAddException( "Problem with stem extension: '" + extn 
          + "', displayExtension: '" + dExtn + "', " + v.getErrorMessage() );
    }
    try {
      Stem _ns = new Stem();
      _ns.setCreatorUuid( GrouperSession.staticGrouperSession().getMember().getUuid() );
      _ns.setCreateTimeLong( new Date().getTime() );
      _ns.setDisplayExtensionDb(dExtn);
      _ns.setDisplayNameDb( U.constructName( this.getDisplayName(), dExtn ) );
      _ns.setExtensionDb(extn);
      _ns.setNameDb( U.constructName( this.getName(), extn ) );
      _ns.setParentUuid( this.getUuid() );
      
      v = NotNullOrEmptyValidator.validate(uuid);
      if (v.isInvalid()) {
        _ns.setUuid( GrouperUuid.getUuid() );
      }
      else {
        _ns.setUuid(uuid);
      }
      GrouperDAOFactory.getFactory().getStem().createChildStem( this, _ns ) ;

      sw.stop();
      EventLog.info(GrouperSession.staticGrouperSession(), M.STEM_ADD + Quote.single( _ns.getName() ), sw);
      _grantDefaultPrivsUponCreate(_ns);
      return _ns;
    }
    catch (GrouperDAOException eDAO) {
      throw new StemAddException( E.CANNOT_CREATE_STEM + eDAO.getMessage(), eDAO );
    }
  }

  /**
   * <pre>
   * Now grant ADMIN (as root) to the creator of the child group.
   *
   * Ideally this would be wrapped up in the broader transaction
   * of adding the child stem but as the interfaces may be
   * outside of our control, I don't think we can do that.  
   *
   * Possibly a bug. The modify* attrs get set when granting ADMIN at creation.
   * </pre>
   * @param g group
   * @throws GroupAddException if problem
   */
  private void _grantDefaultPrivsUponCreate(Group g)
    throws  GroupAddException
  {
    try {
      GrouperSession.staticGrouperSession().internal_getRootSession().getAccessResolver().grantPrivilege(
        g, GrouperSession.staticGrouperSession().getSubject(), AccessPrivilege.ADMIN       
      );

      // Now optionally grant other privs
      this._grantOptionalPrivUponCreate( g, AccessPrivilege.ADMIN, GrouperConfig.GCGAA );
      this._grantOptionalPrivUponCreate( g, AccessPrivilege.OPTIN, GrouperConfig.GCGAOI );
      this._grantOptionalPrivUponCreate( g, AccessPrivilege.OPTOUT, GrouperConfig.GCGAOO );
      this._grantOptionalPrivUponCreate( g, AccessPrivilege.READ, GrouperConfig.GCGAR );
      this._grantOptionalPrivUponCreate( g, AccessPrivilege.UPDATE, GrouperConfig.GCGAU );
      this._grantOptionalPrivUponCreate( g, AccessPrivilege.VIEW, GrouperConfig.GCGAV );
    }
    catch (GrantPrivilegeException eGP)         {
      throw new GroupAddException(eGP.getMessage(), eGP);
    }
    catch (InsufficientPrivilegeException eIP)  {
      throw new GroupAddException(eIP.getMessage(), eIP);
    }
    catch (SchemaException eS)                  {
      throw new GroupAddException(eS.getMessage(), eS);
    }
    catch (UnableToPerformException eUTP) {
      throw new GroupAddException( eUTP.getMessage(), eUTP );
    }
  } 
  /**
   * Now grant STEM (as root) to the creator of the child stem.
   *
   * Ideally this would be wrapped up in the broader transaction
   * of adding the child stem but as the interfaces may be
   * outside of our control, I don't think we can do that.  
   *
   * Possibly a bug. The modify* attrs get set when granting privs at creation.
   * 
   * @param ns stem
   * @throws StemAddException if problem
   */
  private void _grantDefaultPrivsUponCreate(Stem ns)
    throws  StemAddException
  {
    try {
      GrouperSession.staticGrouperSession().internal_getRootSession().getNamingResolver().grantPrivilege(
        ns, GrouperSession.staticGrouperSession().getSubject(), NamingPrivilege.STEM
      );

      // Now optionally grant other privs
      this._grantOptionalPrivUponCreate(
        ns, NamingPrivilege.CREATE, GrouperConfig.SCGAC
      );
      this._grantOptionalPrivUponCreate(
        ns, NamingPrivilege.STEM, GrouperConfig.SCGAS
      );
    }
    catch (GrantPrivilegeException eGP)         {
      throw new StemAddException(eGP.getMessage(), eGP);
    }
    catch (InsufficientPrivilegeException eIP)  {
      throw new StemAddException(eIP.getMessage(), eIP);
    }
    catch (SchemaException eS)                  {
      throw new StemAddException(eS.getMessage(), eS);
    }
    catch (UnableToPerformException eUTP) {
      throw new StemAddException( eUTP.getMessage(), eUTP );
    }
  } 

  /**
   * grant optional priv upon create
   * @param o object
   * @param p prov
   * @param opt opt
   * @throws GrantPrivilegeException if problem 
   * @throws  IllegalStateException if <i>o</i> is neither group nor stem.
   * @throws InsufficientPrivilegeException if not privs
   * @throws SchemaException if problem
   * @throws UnableToPerformException if problem
   * @since   1.2.1
   */
  private void _grantOptionalPrivUponCreate(Object o, Privilege p, String opt) 
    throws  GrantPrivilegeException,
            IllegalStateException,
            InsufficientPrivilegeException,
            SchemaException,
            UnableToPerformException
  {
    Subject       all = SubjectFinder.findAllSubject();
    if (GrouperConfig.getProperty(opt).equals(GrouperConfig.BT)) {
      StopWatch sw = new StopWatch();
      sw.start();
      if      (o instanceof Group) {
        Group g = (Group) o;
        GrouperSession.staticGrouperSession().getAccessResolver().grantPrivilege(g, all, p);
        sw.stop();
        EL.groupGrantPriv(GrouperSession.staticGrouperSession(), g.getName(), all, p, sw);
      }
      else if (o instanceof Stem) {
        Stem ns = (Stem) o;
        GrouperSession.staticGrouperSession().getNamingResolver().grantPrivilege(ns, all, p);
        sw.stop();
        EL.stemGrantPriv(GrouperSession.staticGrouperSession(), ns.getName(), all, p, sw);
      }
      else {
        throw new IllegalStateException("unexpected condition: object is not group or stem: " + o);
      }
    }
  } 

  /**
   * rename child groups
   * @since   1.2.0
   * @param attr
   * @param modifier
   * @param modifyTime
   * @return the set of Group's
   */
  private Set _renameChildGroups(String attr, String modifier, long modifyTime) {
    Map<String, String> attrs;
    Group            _g;
    Set                 groups  = new LinkedHashSet();
    Iterator            it      = GrouperDAOFactory.getFactory().getStem().findAllChildGroups( this, Stem.Scope.ONE ).iterator();
    while (it.hasNext()) {
      _g = (Group) it.next();
      attrs = _g.getAttributesDb();
      if      ( attr.equals(GrouperConfig.ATTR_DISPLAY_EXTENSION) )  {
        attrs.put( 
          GrouperConfig.ATTR_DISPLAY_NAME, 
          U.constructName( this.getDisplayName(), (String) attrs.get(GrouperConfig.ATTR_DISPLAY_EXTENSION) ) 
        );
      }
      else if ( attr.equals(GrouperConfig.ATTR_EXTENSION) )   {
        attrs.put(  
          GrouperConfig.ATTR_NAME, 
          U.constructName( this.getName(), (String) attrs.get(GrouperConfig.ATTR_EXTENSION) ) 
        );
      }
      else {
        throw new IllegalStateException( "attempt to update invalid naming attribute: " + attr);
      }
      _g.setModifierUuid(modifier);
      _g.setModifyTimeLong(modifyTime);
      _g.setAttributes(attrs);
      
      groups.add(_g);
    }
    return groups;
  } 

  /**
   * rename children.
   * @since   1.2.0
   * @param attr attr
   * @return the set of Stems
   * @throws StemModifyException if problem
   */
  private Set _renameChildren(String attr) 
    throws  StemModifyException
  {
    Set     children    = new LinkedHashSet();
    String  modifier    = GrouperSession.staticGrouperSession().getMember().getUuid();
    long    modifyTime  = new Date().getTime();
    children.addAll( this._renameChildStemsAndGroups(attr, modifier, modifyTime) );
    children.addAll( this._renameChildGroups(attr, modifier, modifyTime) );
    return children;
  } 

  /**
   * rename child stems and groups.
   * @param attr sttr
   * @param modifier modifier
   * @param modifyTime modify time
   * @return the set of Stem's
   * @throws IllegalStateException if problem
   */
  private Set _renameChildStemsAndGroups(String attr, String modifier, long modifyTime) 
    throws  IllegalStateException
  {
    Set       children  = new LinkedHashSet();
    Stem      child;
    Iterator  it        = GrouperDAOFactory.getFactory().getStem().findAllChildStems( this, Scope.ONE ).iterator();
    while (it.hasNext()) {
      child = (Stem) it.next() ;
      
      if      ( attr.equals(GrouperConfig.ATTR_DISPLAY_EXTENSION) )  {
        child.setDisplayNameDb(
          U.constructName( this.getDisplayNameDb(), child.getDisplayExtensionDb() ) 
        );
      }
      else if ( attr.equals(GrouperConfig.ATTR_EXTENSION) )   {
        child.setNameDb(
          U.constructName( this.getNameDb(), child.getExtensionDb() ) 
        );
      }
      else {
        throw new IllegalStateException( "attempt to update invalid naming attribute: " + attr);
      }

      children.addAll( child._renameChildGroups(attr, modifier, modifyTime) );
      child.setModifierUuid(modifier);
      child.setModifyTimeLong(modifyTime);
      children.add(child);
      children.addAll( child._renameChildStemsAndGroups(attr, modifier, modifyTime) );
    }
    return children;
  } 

  /**
   * revoke naming privs
   * @throws InsufficientPrivilegeException if problem
   * @throws RevokePrivilegeException if problem
   * @throws SchemaException if problem
   */
  private void _revokeAllNamingPrivs() 
    throws  InsufficientPrivilegeException,
            RevokePrivilegeException, 
            SchemaException {

    try {
      GrouperSession.callbackGrouperSession(
          GrouperSession.staticGrouperSession().internal_getRootSession(), 
          new GrouperSessionHandler() {
  
            public Object callback(GrouperSession grouperSession)
                throws GrouperSessionException {
              try {
                Stem.this.revokePriv(NamingPrivilege.CREATE);
                Stem.this.revokePriv(NamingPrivilege.STEM);
                return null;
              } catch (InsufficientPrivilegeException ipe) {
                throw new GrouperSessionException(ipe);
              } catch (RevokePrivilegeException rpe) {
                throw new GrouperSessionException(rpe);
              } catch (SchemaException se) {
                throw new GrouperSessionException(se);
              }
            }
        
      });
    } catch (GrouperSessionException gse) {
      if (gse.getCause() instanceof InsufficientPrivilegeException) {
        throw (InsufficientPrivilegeException) gse.getCause();
      }
      if (gse.getCause() instanceof RevokePrivilegeException) {
        throw (RevokePrivilegeException) gse.getCause();
      }
      if (gse.getCause() instanceof SchemaException) {
        throw (SchemaException) gse.getCause();
      }
      throw gse;
    }
  } // private void _revokeAllNamingPrivs()

  /**
   * @since   1.2.0
   */  
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Stem)) {
      return false;
    }
    return new EqualsBuilder()
      .append( this.name, ( (Stem) other ).name )
      .isEquals();
  } // public boolean equals(other)

  /**
   * @since   1.2.0
   */
  public long getCreateTimeLong() {
    return this.createTime;
  }

  /**
   * @since   1.2.0
   */
  public String getCreatorUuid() {
    return this.creatorUUID;
  }

  /**
   * @since   1.2.0
   */
  public String getDescriptionDb() {
    return this.description;
  }

  /**
   * @since   1.2.0
   */
  public String getDisplayExtensionDb() {
    return this.displayExtension;
  }

  /**
   * @since   1.2.0
   */
  public String getDisplayNameDb() {
    return this.displayName;
  }

  /**
   * @since   1.2.0
   */
  public String getExtensionDb() {
    return this.extension;
  }

  /**
   * @since   1.2.0
   */
  public String getModifierUuid() {
    return this.modifierUUID;
  }

  /**
   * @since   1.2.0
   */
  public long getModifyTimeLong() {
    return this.modifyTime;
  }

  /**
   * @since   1.2.0
   */
  public String getNameDb() {
    return this.name;
  }

  /**
   * @since   1.2.0
   */
  public String getParentUuid() {
    return this.parentUUID;
  }

  /**
   * @since   1.2.0
   */
  public int hashCode() {
    return new HashCodeBuilder()
      .append( this.name )
      .toHashCode();
  } // public int hashCode()

  /**
   * @since   1.2.0
   */
  public void setCreateTimeLong(long createTime) {
    this.createTime = createTime;
  
  }

  /**
   * @since   1.2.0
   */
  public void setCreatorUuid(String creatorUUID) {
    this.creatorUUID = creatorUUID;
  
  }

  /**
   * @since   1.2.0
   */
  public void setDescriptionDb(String description) {
    this.description = description;
  
  }

  /**
   * @since   1.2.0
   */
  public void setDisplayExtensionDb(String displayExtension) {
    this.displayExtension = displayExtension;
  
  }

  /**
   * @since   1.2.0
   */
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  
  }

  /**
   * @since   1.2.0
   */
  public void setDisplayNameDb(String displayName) {
    this.displayName = displayName;
  
  }

  /**
   * @since   1.2.0
   */
  public void setExtensionDb(String extension) {
    this.extension = extension;
  
  }

  /**
   * @since   1.2.0
   */
  public void setModifierUuid(String modifierUUID) {
    this.modifierUUID = modifierUUID;
  
  }

  /**
   * @since   1.2.0
   */
  public void setModifyTimeLong(long modifyTime) {
    this.modifyTime = modifyTime;
  
  }

  /**
   * @since   1.2.0
   */
  public void setName(String name) {
    this.name = name;
  
  }

  /**
   * @since   1.2.0
   */
  public void setNameDb(String name) {
    this.name = name;
  
  }

  /**
   * @since   1.2.0
   */
  public void setParentUuid(String parentUUID) {
    this.parentUUID = parentUUID;
  
  }

  /**
   * @since   1.2.0
   */
  public void setUuid(String uuid) {
    this.uuid = uuid;
  
  }

  /**
   * @return string
   * @since   1.2.0
   */
  public String toStringDb() {
    return new ToStringBuilder(this)
      .append( "createTime",       this.getCreateTime()       )
      .append( "creatorUuid",      this.getCreatorUuid()      )
      .append( "description",      this.getDescription()      )
      .append( "displayExtension", this.getDisplayExtension() )
      .append( "displayName",      this.getDisplayName()      )
      .append( "extension",        this.getExtension()        )
      .append( "modifierUuid",     this.getModifierUuid()     )
      .append( "modifyTime",       this.getModifyTime()       )
      .append( "name",             this.getName()             )
      .append( "ownerUuid",        this.getUuid()             )
      .append( "parentUuid",       this.getParentUuid()       )
      .toString();
  } // public String toString()

  /**
   * @see edu.internet2.middleware.grouper.GrouperAPI#onPostDelete(edu.internet2.middleware.grouper.hibernate.HibernateSession)
   */
  @Override
  public void onPostDelete(HibernateSession hibernateSession) {

    super.onPostDelete(hibernateSession);
    
    GrouperHooksUtils.schedulePostCommitHooksIfRegistered(GrouperHookType.STEM, 
        StemHooks.METHOD_STEM_POST_COMMIT_DELETE, HooksStemBean.class, 
        this, Stem.class);

    GrouperHooksUtils.callHooksIfRegistered(this, GrouperHookType.STEM, 
        StemHooks.METHOD_STEM_POST_DELETE, HooksStemBean.class, 
        this, Stem.class, VetoTypeGrouper.STEM_POST_DELETE, false, true);
  }

  /**
   * @see edu.internet2.middleware.grouper.GrouperAPI#onPostSave(edu.internet2.middleware.grouper.hibernate.HibernateSession)
   */
  @Override
  public void onPostSave(HibernateSession hibernateSession) {

    super.onPostSave(hibernateSession);
    
    GrouperHooksUtils.schedulePostCommitHooksIfRegistered(GrouperHookType.STEM, 
        StemHooks.METHOD_STEM_POST_COMMIT_INSERT, HooksStemBean.class, 
        this, Stem.class);

    GrouperHooksUtils.callHooksIfRegistered(this, GrouperHookType.STEM, 
        StemHooks.METHOD_STEM_POST_INSERT, HooksStemBean.class, 
        this, Stem.class, VetoTypeGrouper.STEM_POST_INSERT, true, false);
  }

  /**
   * @see edu.internet2.middleware.grouper.GrouperAPI#onPostUpdate(edu.internet2.middleware.grouper.hibernate.HibernateSession)
   */
  @Override
  public void onPostUpdate(HibernateSession hibernateSession) {

    super.onPostUpdate(hibernateSession);
    
    GrouperHooksUtils.schedulePostCommitHooksIfRegistered(GrouperHookType.STEM, 
        StemHooks.METHOD_STEM_POST_COMMIT_UPDATE, HooksStemBean.class, 
        this, Stem.class);

    GrouperHooksUtils.callHooksIfRegistered(this, GrouperHookType.STEM, 
        StemHooks.METHOD_STEM_POST_UPDATE, HooksStemBean.class, 
        this, Stem.class, VetoTypeGrouper.STEM_POST_UPDATE, true, false);
  }

  /**
   * @see edu.internet2.middleware.grouper.GrouperAPI#onPreDelete(edu.internet2.middleware.grouper.hibernate.HibernateSession)
   */
  @Override
  public void onPreDelete(HibernateSession hibernateSession) {
    super.onPreDelete(hibernateSession);
    
    GrouperHooksUtils.callHooksIfRegistered(this, GrouperHookType.STEM, 
        StemHooks.METHOD_STEM_PRE_DELETE, HooksStemBean.class, 
        this, Stem.class, VetoTypeGrouper.STEM_PRE_DELETE, false, false);
  
  }

  /**
   * 
   * @see edu.internet2.middleware.grouper.GrouperAPI#onPreSave(edu.internet2.middleware.grouper.hibernate.HibernateSession)
   */
  @Override
  public void onPreSave(HibernateSession hibernateSession) {
    super.onPreSave(hibernateSession);
    
    
    GrouperHooksUtils.callHooksIfRegistered(this, GrouperHookType.STEM, 
        StemHooks.METHOD_STEM_PRE_INSERT, HooksStemBean.class, 
        this, Stem.class, VetoTypeGrouper.STEM_PRE_INSERT, false, false);
  
  }

  /** see if already in onPreUpdate, dont go in again */
  private static ThreadLocal<Boolean> inOnPreUpdate = new ThreadLocal<Boolean>();
  
  /**
   * @see edu.internet2.middleware.grouper.GrouperAPI#onPreUpdate(edu.internet2.middleware.grouper.hibernate.HibernateSession)
   */
  @Override
  public void onPreUpdate(HibernateSession hibernateSession) {
    super.onPreUpdate(hibernateSession);
    
    //if supposed to not have setters do queries
    if (GrouperConfig.getPropertyBoolean(GrouperConfig.PROP_SETTERS_DONT_CAUSE_QUERIES, false)) {
      Boolean inOnPreUpdateBoolean = inOnPreUpdate.get();
      try {
        
        if (inOnPreUpdateBoolean == null || !inOnPreUpdateBoolean) {
          inOnPreUpdate.set(true);
          //check and see what needs to be updated
          Set<String> dbVersionDifferentFields = Stem.this.dbVersionDifferentFields();
          if (dbVersionDifferentFields.contains(FIELD_EXTENSION)) {
            
            // Now iterate through all child groups and stems, renaming each.
            GrouperDAOFactory.getFactory().getStem().renameStemAndChildren( Stem.this, 
                Stem.this._renameChildren(GrouperConfig.ATTR_EXTENSION) );
          }
          if (dbVersionDifferentFields.contains(FIELD_DISPLAY_EXTENSION)) {
            
            // Now iterate through all child groups and stems, renaming each.
            GrouperDAOFactory.getFactory().getStem().renameStemAndChildren( Stem.this, 
                Stem.this._renameChildren(GrouperConfig.ATTR_DISPLAY_EXTENSION) );
          }
          //if its description, just store, we are all good
        }
      } catch (StemModifyException ste) {
        //tunnel checked exceptions
        throw new RuntimeException(ste);
      } finally {
        //if we changed it
        if (inOnPreUpdateBoolean== null || !inOnPreUpdateBoolean) {
          //change it back
          inOnPreUpdate.remove();
        }
      }
    }


    
    GrouperHooksUtils.callHooksIfRegistered(this, GrouperHookType.STEM, 
        StemHooks.METHOD_STEM_PRE_UPDATE, HooksStemBean.class, 
        this, Stem.class, VetoTypeGrouper.STEM_PRE_UPDATE, false, false);
  }

  /**
   * save the state when retrieving from DB
   * @return the dbVersion
   */
  @Override
  public Stem dbVersion() {
    return (Stem)this.dbVersion;
  }

  /**
   * note, these are massaged so that name, extension, etc look like normal fields.
   * access with fieldValue()
   * @see edu.internet2.middleware.grouper.GrouperAPI#dbVersionDifferentFields()
   */
  @Override
  public Set<String> dbVersionDifferentFields() {
    if (this.dbVersion == null) {
      throw new RuntimeException("State was never stored from db");
    }
    //easier to unit test if everything is ordered
    Set<String> result = GrouperUtil.compareObjectFields(this, this.dbVersion,
        DB_VERSION_FIELDS, null);
    return result;
  }

  /**
   * take a snapshot of the data since this is what is in the db
   */
  @Override
  public void dbVersionReset() {
    //lets get the state from the db so we know what has changed
    this.dbVersion = GrouperUtil.clone(this, DB_VERSION_FIELDS);
  }

  /**
   * deep clone the fields in this object
   */
  @Override
  public Stem clone() {
    return GrouperUtil.clone(this, CLONE_FIELDS);
  }

  /**
   * create stems and parents if not exist.
   * @param stemName
   * @param grouperSession 
   * @param stemDisplayNameForInserts optional, will use this for display name, and not just default to the name.  Note this is
   * only used if creating something, it will not update existing stems
   * @return the resulting stem
   * @throws InsufficientPrivilegeException 
   * @throws StemNotFoundException 
   * @throws StemAddException 
   */
  static Stem _createStemAndParentStemsIfNotExist(GrouperSession grouperSession, String stemName, String stemDisplayNameForInserts)
     throws InsufficientPrivilegeException, StemNotFoundException, StemAddException {
    //note, no need for GrouperSession inverse of control
    String[] stems = StringUtils.split(stemName, ':');
    String[] displayStems = StringUtils.isBlank(stemDisplayNameForInserts) ? null : StringUtils.split(stemDisplayNameForInserts, ':');

    boolean hasDisplayStems = displayStems != null;
    
    if (hasDisplayStems) {
      if (stems.length != displayStems.length) {
        throw new RuntimeException("The length of stems in stem name: " + stems.length + ", " + stemName
            + ", should be the same as the display stems: " + displayStems.length + ", " + stemDisplayNameForInserts);
      }
    }
    
    Stem currentStem = StemFinder.findRootStem(grouperSession);
    String currentName = stems[0];
    for (int i=0;i<stems.length;i++) {
      try {
        currentStem = StemFinder.findByName(grouperSession, currentName);
      } catch (StemNotFoundException snfe1) {
        //this isnt ideal, but just use the extension as the display extension
        currentStem = currentStem.addChildStem(stems[i], hasDisplayStems ? displayStems[i] : stems[i]);
      }
      //increment the name, dont worry if on the last one, we are done
      if (i < stems.length-1) {
        currentName += ":" + stems[i+1];
      }
    }
    //at this point the stem should be there (and is equal to currentStem), just to be sure, query again
    Stem parentStem = StemFinder.findByName(grouperSession, stemName);
    return parentStem;

  }
  
  /**
   * <pre>
   * create or update a stem.  Note this will not move a stem at this time (might in future)
   * 
   * This is a static method since setters to Stem objects persist to the DB
   * 
   * Steps:
   * 
   * 1. Find the stem by stemNameToEdit (if not there then its an insert)
   * 2. Internally set all the fields of the stem (no need to reset if already the same)
   * 3. Store the stem (insert or update) if needed
   * 4. Return the stem object
   * 
   * This occurs in a transaction, so if a part of it fails, it rolls back, and potentially
   * rolls back outer transactions too
   * </pre>
   * @param grouperSession to act as
   * @param stemNameToEdit is the name of the stem to edit (or null if insert)
   * @param description new description for stem
   * @param displayExtension display friendly name for this stem only
   * (parent stems are not specified)
   * @param name this is required, and is the full name of the stem
   * including the names of parent stems.  e.g. stem1:stem2:stem3
   * the parent stem must exist unless createParentStemsIfNotExist.  
   * Can rename a stem extension, but not the parent stem name (move)
   * @param uuid of the stem.  uuid for an inserted stem
   * @param saveMode to constrain if insert only or update only, if null defaults to INSERT_OR_UPDATE
   * @param createParentStemsIfNotExist true if the stems should be created if they dont exist, false
   * for StemNotFoundException if not exist.  Note, the display extension on created stems
   * will equal the extension.  This could be dangerous and should probably only be used for testing
   * @return the stem that was updated or created
   * @throws StemNotFoundException 
   * @throws InsufficientPrivilegeException 
   * @throws StemAddException 
   * @throws StemModifyException 
   */
  public static Stem saveStem(final GrouperSession grouperSession, final String stemNameToEdit,
      final String uuid, final String name, final String displayExtension, final String description, 
      SaveMode saveMode, final boolean createParentStemsIfNotExist) 
        throws StemNotFoundException,
      InsufficientPrivilegeException,
      StemAddException, StemModifyException {
  
    StemSave stemSave = new StemSave(grouperSession);

    stemSave.assignStemNameToEdit(stemNameToEdit).assignUuid(uuid);
    stemSave.assignName(name).assignDisplayExtension(displayExtension);
    stemSave.assignDescription(description).assignSaveMode(saveMode);
    stemSave.assignCreateParentStemsIfNotExist(createParentStemsIfNotExist);
    Stem stem = stemSave.save();

    return stem;

  }

} // public class Stem extends GrouperAPI implements Owner
