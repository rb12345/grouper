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

import edu.internet2.middleware.grouper.cfg.ApiConfig;
import edu.internet2.middleware.grouper.exception.GroupModifyException;
import edu.internet2.middleware.grouper.exception.GrouperRuntimeException;
import edu.internet2.middleware.grouper.exception.InsufficientPrivilegeException;
import edu.internet2.middleware.grouper.exception.RevokePrivilegeException;
import edu.internet2.middleware.grouper.exception.SchemaException;
import edu.internet2.middleware.grouper.exception.StemAddException;
import edu.internet2.middleware.grouper.exception.StemModifyException;
import edu.internet2.middleware.grouper.privs.AccessPrivilege;
import edu.internet2.middleware.grouper.privs.NamingPrivilege;
import edu.internet2.middleware.grouper.privs.Privilege;
import edu.internet2.middleware.subject.Subject;
import junit.textui.TestRunner;


/**
 * Test {@link Stem}.
 * <p/>
 * @author  blair christensen.
 * @version $Id: Test_api_Stem.java,v 1.15 2009-02-27 20:51:46 shilen Exp $
 * @since   1.2.1
 */
public class Test_api_Stem extends GrouperTest {


  private Group           child_group, top_group, admin, wheel;
  private GrouperSession  s;
  private Stem            child, root, top, top_new, etc;

  /**
   * 
   */
  public Test_api_Stem() {
    super();
  }

  /**
   * @param name
   */
  public Test_api_Stem(String name) {
    super(name);
  }

  /**
   * 
   * @param args
   */
  public static void main(String[] args) {
    //TestRunner.run(new Test_api_Stem("test_getChildGroups_PrivilegeArrayAndScope_viewPrivAndOneScope"));
    TestRunner.run(Test_api_Stem.class);
  }

  /** size before getting started */
  private int originalRootGroupSubSize = -1;
  
  /** original chld stem size */
  private int originalRootChildStemSize = -1;
  
  /** original */
  private int originalRootChildStemOneSize = -1;
  
  /** original */
  private int originalRootChildStemSubSize = -1;
  
  /** original */
  private int originalRootCreateOne = -1;
  
  /** original */
  private int originalRootCreateSub = -1;
  
  /** original */
  private int originalRootViewOne = -1;
  
  /** original */
  private int originalRootViewSub = -1;
  
  /** original */
  private int originalRootCreateAndViewOne = -1;
  
  /** original */
  private int originalRootCreateAndViewSub = -1;
  
  /**
   * 
   * @see edu.internet2.middleware.grouper.GrouperTest#setUp()
   */
  public void setUp() {
    super.setUp();
    try {
      this.s            = GrouperSession.start( SubjectFinder.findRootSubject() );
      this.root         = StemFinder.findRootStem(this.s);
      
      this.originalRootGroupSubSize = this.root.getChildGroups(Stem.Scope.SUB).size();
      this.originalRootChildStemSize = this.root.getChildStems().size();
      this.originalRootChildStemOneSize = this.root.getChildStems(Stem.Scope.ONE).size();
      this.originalRootChildStemSubSize = this.root.getChildStems(Stem.Scope.SUB).size();
      
      this.originalRootCreateOne =  this.root.getChildStems( 
          new Privilege[]{NamingPrivilege.CREATE}, Stem.Scope.ONE ).size();
      this.originalRootCreateSub =  this.root.getChildStems( 
          new Privilege[]{NamingPrivilege.CREATE}, Stem.Scope.SUB ).size();
      this.originalRootViewOne =  this.root.getChildStems( 
          new Privilege[]{AccessPrivilege.VIEW}, Stem.Scope.ONE ).size();
      this.originalRootViewSub =  this.root.getChildStems( 
          new Privilege[]{AccessPrivilege.VIEW}, Stem.Scope.SUB ).size();
      this.originalRootCreateAndViewOne =  this.root.getChildStems( 
          new Privilege[]{NamingPrivilege.CREATE, AccessPrivilege.VIEW}, Stem.Scope.ONE ).size();
      this.originalRootCreateAndViewSub =  this.root.getChildStems( 
          new Privilege[]{NamingPrivilege.CREATE, AccessPrivilege.VIEW}, Stem.Scope.SUB ).size();
      
      this.top          = this.root.addChildStem("top", "top display name");
      this.top_group    = this.top.addChildGroup("top group", "top group display name");
      this.child        = this.top.addChildStem("child", "child display name");
      this.child_group  = this.child.addChildGroup("child group", "child group display name");
    }
    catch (Exception e) {
      throw new GrouperRuntimeException( "test setUp() error: " + e.getMessage(), e );
    }
  }

  /**
   * 
   * @see edu.internet2.middleware.grouper.GrouperTest#tearDown()
   */
  public void tearDown() {
    super.tearDown();
  }



  public void test_getChildGroups_fromRoot() {
    assertEquals( 0, this.root.getChildGroups().size() );
  }

  public void test_getChildGroups_fromTop() {
    assertEquals( 1, this.top.getChildGroups().size() );
  }

  public void test_getChildGroups_fromChild() {
    assertEquals( 1, this.child.getChildGroups().size() );
  }

  public void test_getChildGroups_Scope_nullScope() {
    try {
      this.root.getChildGroups(null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }



  public void test_getChildGroups_PrivilegeArrayAndScope_nullArray() {
    try {
      this.root.getChildGroups(null, null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }
  public void test_getChildGroups_PrivilegeArrayAndScope_nullScope() {
    try {
      this.root.getChildGroups( new Privilege[0], null );
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }
  public void test_getChildGroups_PrivilegeArrayAndScope_emptyArray() {
    assertEquals( 0, this.top.getChildGroups( new Privilege[0], Stem.Scope.SUB ).size() );
  }
  public void test_getChildGroups_PrivilegeArrayAndScope_createPrivAndOneScope() {
    Privilege[] privs = { NamingPrivilege.CREATE };
    assertEquals( 0, this.top.getChildGroups( privs, Stem.Scope.ONE ).size() );
  }
  public void test_getChildGroups_PrivilegeArrayAndScope_createPrivAndSubScope() {
    Privilege[] privs = { NamingPrivilege.CREATE };
    assertEquals( 0, this.top.getChildGroups( privs, Stem.Scope.SUB ).size() );
  }
  public void test_getChildGroups_PrivilegeArrayAndScope_viewPrivAndOneScope() {
    Privilege[] privs = { AccessPrivilege.VIEW };
    assertEquals( 1, this.top.getChildGroups( privs, Stem.Scope.ONE ).size() );
  }
  public void test_getChildGroups_PrivilegeArrayAndScope_viewPrivAndSubScope() {
    Privilege[] privs = { AccessPrivilege.VIEW };
    assertEquals( 2, this.top.getChildGroups( privs, Stem.Scope.SUB ).size() );
  }
  public void test_getChildGroups_PrivilegeArrayAndScope_createAndViewPrivsAndOneScope() {
    Privilege[] privs = { NamingPrivilege.CREATE, AccessPrivilege.VIEW };
    assertEquals( 1, this.top.getChildGroups( privs, Stem.Scope.ONE ).size() );
  }
  public void test_getChildGroups_PrivilegeArrayAndScope_createAndViewPrivsAndSubScope() {
    Privilege[] privs = { NamingPrivilege.CREATE, AccessPrivilege.VIEW };
    assertEquals( 2, this.top.getChildGroups( privs, Stem.Scope.SUB ).size() );
  }



  public void test_getChildGroups_Scope_fromRootScopeONE() {
    assertEquals( 0, this.root.getChildGroups(Stem.Scope.ONE).size() );
  }

  public void test_getChildGroups_Scope_fromRootScopeSUB() {
    assertEquals( this.originalRootGroupSubSize + 2, this.root.getChildGroups(Stem.Scope.SUB).size() );
  }

  public void test_getChildGroups_Scope_fromTopScopeONE() {
    assertEquals( 1, this.top.getChildGroups(Stem.Scope.ONE).size() );
  }

  public void test_getChildGroups_Scope_fromTopScopeSUB() {
    assertEquals( 2, this.top.getChildGroups(Stem.Scope.SUB).size() );
  }

  public void test_getChildGroups_Scope_fromChildScopeONE() {
    assertEquals( 1, this.child.getChildGroups(Stem.Scope.ONE).size() );
  }

  public void test_getChildGroups_Scope_fromChildScopeSUB() {
    assertEquals( 1, this.child.getChildGroups(Stem.Scope.SUB).size() );
  }



  public void test_getChildStems_fromRoot() {
    assertEquals( this.originalRootChildStemSize + 1, this.root.getChildStems().size() );
  }

  public void test_getChildStems_fromTop() {
    assertEquals( 1, this.top.getChildStems().size() );
  }

  public void test_getChildStems_fromChild() {
    assertEquals( 0, this.child.getChildStems().size() );
  }



  public void test_getChildStems_Scope_nullScope() {
    try {
      this.root.getChildStems(null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }

  public void test_getChildStems_Scope_fromRootScopeONE() {
    assertEquals( this.originalRootChildStemOneSize + 1, this.root.getChildStems(Stem.Scope.ONE).size() );
  }

  public void test_getChildStems_Scope_fromRootScopeSUB() {
    assertEquals( this.originalRootChildStemSubSize + 2, this.root.getChildStems(Stem.Scope.SUB).size() );
  }

  public void test_getChildStems_Scope_fromTopScopeONE() {
    assertEquals( 1, this.top.getChildStems(Stem.Scope.ONE).size() );
  }

  public void test_getChildStems_Scope_fromTopScopeSUB() {
    assertEquals( 1, this.top.getChildStems(Stem.Scope.SUB).size() );
  }

  public void test_getChildStems_Scope_fromChildScopeONE() {
    assertEquals( 0, this.child.getChildStems(Stem.Scope.ONE).size() );
  }

  public void test_getChildStems_Scope_fromChildScopeSUB() {
    assertEquals( 0, this.child.getChildStems(Stem.Scope.SUB).size() );
  }



  public void test_getChildStems_PrivilegeArrayAndScope_nullArray() {
    try {
      this.root.getChildStems(null, null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }
  public void test_getChildStems_PrivilegeArrayAndScope_nullScope() {
    try {
      this.root.getChildStems( new Privilege[0], null );
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }
  public void test_getChildStems_PrivilegeArrayAndScope_emptyArray() {
    assertEquals( 0, this.root.getChildStems( new Privilege[0], Stem.Scope.SUB ).size() );
  }
  public void test_getChildStems_PrivilegeArrayAndScope_createPrivAndOneScope() {
    Privilege[] privs = { NamingPrivilege.CREATE };
    assertEquals( this.originalRootCreateOne + 1, this.root.getChildStems( privs, Stem.Scope.ONE ).size() );
  }
  public void test_getChildStems_PrivilegeArrayAndScope_createPrivAndSubScope() {
    Privilege[] privs = { NamingPrivilege.CREATE };
    assertEquals( this.originalRootCreateSub + 2, this.root.getChildStems( privs, Stem.Scope.SUB ).size() );
  }
  public void test_getChildStems_PrivilegeArrayAndScope_viewPrivAndOneScope() {
    Privilege[] privs = { AccessPrivilege.VIEW };
    assertEquals( this.originalRootViewOne + 1, this.root.getChildStems( privs, Stem.Scope.ONE ).size() );
  }
  public void test_getChildStems_PrivilegeArrayAndScope_viewPrivAndSubScope() {
    Privilege[] privs = { AccessPrivilege.VIEW };
    assertEquals( this.originalRootViewSub + 2, this.root.getChildStems( privs, Stem.Scope.SUB ).size() );
  }
  public void test_getChildStems_PrivilegeArrayAndScope_createAndViewPrivsAndOneScope() {
    Privilege[] privs = { NamingPrivilege.CREATE, AccessPrivilege.VIEW };
    assertEquals( this.originalRootCreateAndViewOne + 1, this.root.getChildStems( privs, Stem.Scope.ONE ).size() );
  }
  public void test_getChildStems_PrivilegeArrayAndScope_createAndViewPrivsAndSubScope() {
    Privilege[] privs = { NamingPrivilege.CREATE, AccessPrivilege.VIEW };
    assertEquals( this.originalRootCreateAndViewSub + 2, this.root.getChildStems( privs, Stem.Scope.SUB ).size() );
  }
  /**
   * @since   1.2.1
   */
  public void test_getChildStems_PrivilegeArrayAndScope_OneScopeDoNotReturnThisStem() {
    Privilege[] privs = { AccessPrivilege.VIEW };
    assertEquals( 1, this.top.getChildStems( privs, Stem.Scope.ONE ).size() );
  }



  public void test_isChildGroup_nullChild() {
    try {
      this.root.isChildGroup(null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    } 
  }

  public void test_isChildGroup_rootAsPotentialParent() {
    assertTrue( this.root.isChildGroup( this.child_group ) );
  }

  public void test_isChildGroup_immediateChild() {
    assertTrue( this.child.isChildGroup( this.child_group ) );
  }

  public void test_isChildGroup_notChild() {
    assertFalse( this.child.isChildGroup( this.top_group ) );
  }


  public void test_isChildStem_nullChild() {
    try {
      this.root.isChildStem(null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }

  public void test_isChildStem_rootAsPotentialParent() {
    assertTrue( this.root.isChildStem( this.child ) );
  }

  public void test_isChildStem_rootAsChild() {
    assertFalse( this.child.isChildStem( this.root ) );
  }

  public void test_isChildStem_selfAsChild() {
    assertFalse( this.child.isChildStem( this.child ) );
  }

  public void test_isChildStem_isChild() {
    assertTrue( this.top.isChildStem( this.child ) );
  }

  public void test_isChildStem_notChild() 
    throws  InsufficientPrivilegeException,
            StemAddException
  {
    Stem otherTop = this.root.addChildStem("other top", "other top");
    assertFalse( otherTop.isChildStem( this.child ) );
  }



  public void test_isRootStem_root() {
    assertTrue( this.root.isRootStem() );
  }

  public void test_isRootStem_notRootStem() {
    assertFalse( this.top.isRootStem() );
  }



  /**
   * @since   1.2.1
   */
  public void test_revokePriv_Priv_accessPrivilege() 
    throws  InsufficientPrivilegeException,
            RevokePrivilegeException
  {
    try {
      this.root.revokePriv(AccessPrivilege.ADMIN);
      fail("failed to throw expected SchemaException");
    }
    catch (SchemaException eExpected) {
      assertTrue(true);
    }
  }
  
  
  /**
   * @throws InsufficientPrivilegeException 
   */
  public void test_move_rootStem() throws InsufficientPrivilegeException {
    try {
      root.move(top);
      fail("failed to throw StemModifyException");
    } catch (StemModifyException e) {
      assertTrue(true);
    }
  }
  
  /**
   * @throws InsufficientPrivilegeException 
   */
  public void test_move_toSubStem() throws InsufficientPrivilegeException {
    try {
      top.move(child);
      fail("failed to throw StemModifyException");
    } catch (StemModifyException e) {
      assertTrue(true);
    }
  }


  /**
   * @throws Exception
   */
  public void test_move_insufficientPrivileges_without_admin_or_wheel_group() throws Exception {
    GrouperSession nrs;
    R r = R.populateRegistry(0, 0, 3);
    Subject a = r.getSubject("a");
    Subject b = r.getSubject("b");
    Subject c = r.getSubject("c");
    
    this.top_new = this.root.addChildStem("top new", "top new display name");
    
    top.grantPriv(a, NamingPrivilege.STEM);
    top_new.grantPriv(a, NamingPrivilege.CREATE);
    top.grantPriv(b, NamingPrivilege.CREATE);
    top_new.grantPriv(b, NamingPrivilege.STEM);
    top.grantPriv(c, NamingPrivilege.STEM);
    top_new.grantPriv(c, NamingPrivilege.STEM);
    
    nrs = GrouperSession.start(a);
    try {
      top.move(top_new);
      fail("failed to throw InsufficientPrivilegeException");
    } catch (InsufficientPrivilegeException ex) {
      assertTrue(true);
    }
    nrs.stop();
    
    nrs = GrouperSession.start(b);
    try {
      top.move(top_new);
      fail("failed to throw InsufficientPrivilegeException");
    } catch (InsufficientPrivilegeException ex) {
      assertTrue(true);
    }
    nrs.stop();
        
    nrs = GrouperSession.start(c);
    top.move(top_new);
    assertTrue(true);
    nrs.stop();
            
    r.rs.stop();
  }
  
  /**
   * @throws Exception
   */
  public void test_rename_insufficientPrivileges_with_admin_group() throws Exception {
    this.etc          = this.root.addChildStem("etc", "etc");
    this.admin        = this.etc.addChildGroup("admin", "admin");
    this.wheel        = this.etc.addChildGroup("wheel","wheel");
    
    ApiConfig.testConfig.put("security.stem.groupAllowedToRenameStem", admin.getName());
    ApiConfig.testConfig.put("groups.wheel.use", "true");
    ApiConfig.testConfig.put("groups.wheel.group", wheel.getName());

    GrouperSession nrs;
    R r = R.populateRegistry(0, 0, 2);
    Subject a = r.getSubject("a");
    Subject b = r.getSubject("b");
    
    top.grantPriv(a, NamingPrivilege.STEM);
    top.grantPriv(b, NamingPrivilege.STEM);
    
    admin.addMember(b);
    
    nrs = GrouperSession.start(a);
    try {
      top.setExtension("top_new");
      fail("failed to throw InsufficientPrivilegeException");
    } catch (InsufficientPrivilegeException ex) {
      assertTrue(true);
    }
    nrs.stop();
         
    nrs = GrouperSession.start(b);
    top.setExtension("top_new");
    assertTrue(true);
    nrs.stop();
            
    r.rs.stop();
  }
  
  /**
   * @throws Exception
   */
  public void test_rename_insufficientPrivileges_with_wheel_group() throws Exception {
    this.etc          = this.root.addChildStem("etc", "etc");
    this.admin        = this.etc.addChildGroup("admin", "admin");
    this.wheel        = this.etc.addChildGroup("wheel","wheel");
    
    ApiConfig.testConfig.put("security.stem.groupAllowedToRenameStem", admin.getName());
    ApiConfig.testConfig.put("groups.wheel.use", "true");
    ApiConfig.testConfig.put("groups.wheel.group", wheel.getName());

    GrouperSession nrs;
    R r = R.populateRegistry(0, 0, 2);
    Subject a = r.getSubject("a");
    Subject b = r.getSubject("b");
    
    top.grantPriv(a, NamingPrivilege.STEM);
    top.grantPriv(b, NamingPrivilege.STEM);
    
    wheel.addMember(b);
    
    nrs = GrouperSession.start(a);
    try {
      top.setExtension("top_new");
      fail("failed to throw InsufficientPrivilegeException");
    } catch (InsufficientPrivilegeException ex) {
      assertTrue(true);
    }
    nrs.stop();
         
    nrs = GrouperSession.start(b);
    top.setExtension("top_new");
    assertTrue(true);
    nrs.stop();
            
    r.rs.stop();
  }
  
  /**
   * @throws Exception
   */
  public void test_move_insufficientPrivileges_with_admin_group() throws Exception {
    this.etc          = this.root.addChildStem("etc", "etc");
    this.admin        = this.etc.addChildGroup("admin", "admin");
    this.wheel        = this.etc.addChildGroup("wheel","wheel");
    
    ApiConfig.testConfig.put("security.stem.groupAllowedToMoveStem", admin.getName());
    ApiConfig.testConfig.put("groups.wheel.use", "true");
    ApiConfig.testConfig.put("groups.wheel.group", wheel.getName());

    GrouperSession nrs;
    R r = R.populateRegistry(0, 0, 2);
    Subject a = r.getSubject("a");
    Subject b = r.getSubject("b");
    
    this.top_new = this.root.addChildStem("top new", "top new display name");
    
    top.grantPriv(a, NamingPrivilege.STEM);
    top_new.grantPriv(a, NamingPrivilege.STEM);
    top.grantPriv(b, NamingPrivilege.STEM);
    top_new.grantPriv(b, NamingPrivilege.STEM);
    
    admin.addMember(b);
    
    nrs = GrouperSession.start(a);
    try {
      top.move(top_new);
      fail("failed to throw InsufficientPrivilegeException");
    } catch (InsufficientPrivilegeException ex) {
      assertTrue(true);
    }
    nrs.stop();
         
    nrs = GrouperSession.start(b);
    top.move(top_new);
    assertTrue(true);
    nrs.stop();
            
    r.rs.stop();
  }
  
  /**
   * @throws Exception
   */
  public void test_move_insufficientPrivileges_with_wheel_group() throws Exception {
    this.etc          = this.root.addChildStem("etc", "etc");
    this.admin        = this.etc.addChildGroup("admin", "admin");
    this.wheel        = this.etc.addChildGroup("wheel","wheel");
    
    ApiConfig.testConfig.put("security.stem.groupAllowedToMoveStem", admin.getName());
    ApiConfig.testConfig.put("groups.wheel.use", "true");
    ApiConfig.testConfig.put("groups.wheel.group", wheel.getName());

    GrouperSession nrs;
    R r = R.populateRegistry(0, 0, 2);
    Subject a = r.getSubject("a");
    Subject b = r.getSubject("b");
    
    this.top_new = this.root.addChildStem("top new", "top new display name");
    
    top.grantPriv(a, NamingPrivilege.STEM);
    top_new.grantPriv(a, NamingPrivilege.STEM);
    top.grantPriv(b, NamingPrivilege.STEM);
    top_new.grantPriv(b, NamingPrivilege.STEM);
    
    wheel.addMember(b);
    
    nrs = GrouperSession.start(a);
    try {
      top.move(top_new);
      fail("failed to throw InsufficientPrivilegeException");
    } catch (InsufficientPrivilegeException ex) {
      assertTrue(true);
    }
    nrs.stop();
         
    nrs = GrouperSession.start(b);
    top.move(top_new);
    assertTrue(true);
    nrs.stop();
            
    r.rs.stop();
  }
  
  /**
   * @throws Exception
   */
  public void test_move() throws Exception {
    R r = R.populateRegistry(0, 0, 2);
    Subject a = r.getSubject("a");
    Subject b = r.getSubject("b");

    this.top_new = this.root.addChildStem("top new", "top new display name");

    child_group.addMember(a);
    child_group.grantPriv(a, AccessPrivilege.UPDATE);
    child.grantPriv(b, NamingPrivilege.CREATE);
    top.grantPriv(b, NamingPrivilege.CREATE);

    // first move to a non-root stem
    top.move(top_new);

    top = StemFinder.findByName(s, "top new:top");
    child = StemFinder.findByName(s, "top new:top:child");
    child_group = GroupFinder.findByName(s, "top new:top:child:child group");
    assertStemName(top, "top new:top");
    assertStemDisplayName(top, "top new display name:top display name");
    assertStemName(child, "top new:top:child");
    assertStemDisplayName(child,
        "top new display name:top display name:child display name");
    assertGroupName(child_group, "top new:top:child:child group");
    assertGroupDisplayName(
        child_group,
        "top new display name:top display name:child display name:child group display name");
    assertGroupHasMember(child_group, a, true);
    assertGroupHasMember(child_group, b, false);
    assertGroupHasUpdate(child_group, a, true);
    assertStemHasCreate(child, a, false);
    assertStemHasCreate(child, b, true);
    assertStemHasCreate(top, b, true);
    assertTrue(child_group.getParentStem().getUuid().equals(child.getUuid()));
    assertTrue(child.getParentStem().getUuid().equals(top.getUuid()));
    assertTrue(top.getParentStem().getUuid().equals(top_new.getUuid()));
    assertTrue(top_new.getChildStems().size() == 1);

    // second move to a root stem
    top.move(root);

    top = StemFinder.findByName(s, "top");
    child = StemFinder.findByName(s, "top:child");
    child_group = GroupFinder.findByName(s, "top:child:child group");
    assertStemName(top, "top");
    assertStemDisplayName(top, "top display name");
    assertStemName(child, "top:child");
    assertStemDisplayName(child, "top display name:child display name");
    assertGroupName(child_group, "top:child:child group");
    assertGroupDisplayName(child_group,
        "top display name:child display name:child group display name");
    assertGroupHasMember(child_group, a, true);
    assertGroupHasMember(child_group, b, false);
    assertGroupHasUpdate(child_group, a, true);
    assertStemHasCreate(child, a, false);
    assertStemHasCreate(child, b, true);
    assertStemHasCreate(top, b, true);
    assertTrue(child_group.getParentStem().getUuid().equals(child.getUuid()));
    assertTrue(child.getParentStem().getUuid().equals(top.getUuid()));
    assertTrue(top.getParentStem().getUuid().equals(root.getUuid()));
    assertTrue(top_new.getChildStems().size() == 0);
    
    r.rs.stop();
  }
}

