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


/**
 * Test {@link ChildStemFilter}.
 * <p/>
 * @author  blair christensen.
 * @version $Id: Test_api_ChildStemFilter.java,v 1.1 2007-08-02 19:25:15 blair Exp $
 * @since   @HEAD@
 */
public class Test_api_ChildStemFilter extends GrouperTest {


  private GrouperSession  s;
  private Stem            child, root, top;


  public void setUp() {
    super.setUp();
    try {
      this.s      = GrouperSession.start( SubjectFinder.findRootSubject() );
      this.root   = StemFinder.findRootStem(this.s);
      this.top    = this.root.addChildStem("top", "top");
      this.child  = this.top.addChildStem("child", "child");
    }
    catch (Exception e) {
      throw new GrouperRuntimeException( "test setUp() error: " + e.getMessage(), e );
    }
  }

  public void tearDown() {
    super.tearDown();
  }


  public void test_Constructor_nullStem() {
    try {
      new ChildStemFilter(null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }


  public void test_getResults_nullSession() 
    throws  QueryException
  {
    try {
      new ChildStemFilter(this.root).getResults(null);
      fail("failed to throw IllegalStateException");
    }
    catch (IllegalStateException eExpected) {
      assertTrue("threw expected exception", true);
    }
  }

  public void test_getResults_fromRoot() 
    throws  QueryException
  {
    assertEquals( 2, new ChildStemFilter(this.root).getResults(this.s).size() );
  }

  public void test_getResults_fromTop() 
    throws  QueryException
  {
    assertEquals( 1, new ChildStemFilter(this.top).getResults(this.s).size() );
  }

  public void test_getResults_fromChild() 
    throws  QueryException
  {
    assertEquals( 0, new ChildStemFilter(this.child).getResults(this.s).size() );
  }

}

