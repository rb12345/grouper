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
import org.apache.commons.logging.Log;

import edu.internet2.middleware.grouper.registry.RegistryReset;
import edu.internet2.middleware.grouper.util.GrouperUtil;

/**
 * @author  blair christensen.
 * @version $Id: TestGroupFinder_FindByAttribute.java,v 1.7 2008-09-29 03:38:27 mchyzer Exp $
 * @since   1.2.0
 */
public class TestGroupFinder_FindByAttribute extends GrouperTest {

  private static final Log LOG = GrouperUtil.getLog(TestGroupFinder_FindByAttribute.class);

  public TestGroupFinder_FindByAttribute(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testFailToFindGroupByAttributeNullSession() {
    LOG.info("testFailToFindGroupByAttributeNullSession");
    try {
      R.populateRegistry(0, 0, 0);
      GroupFinder.findByAttribute(null, "description", "i2:a:a");
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eIA) {
      assertTrue(true);
    }
    catch (Exception e) {
      unexpectedException(e);
    }
  } // public void testFailToFindGroupByAttributeNullSession()

  public void testFailToFindGroupByAttributeNullAttribute() {
    LOG.info("testFailToFindGroupByAttributeNullAttribute");
    try {
      R r = R.populateRegistry(0, 0, 0);
      GroupFinder.findByAttribute(r.rs, null, "i2:a:a");
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eIA) {
      assertTrue(true);
    }
    catch (Exception e) {
      unexpectedException(e);
    }
  } // public void testFailToFindGroupByAttributeNullAttribute()

  public void testFailToFindGroupByAttributeNullAttributeValue() {
    LOG.info("testFailToFindGroupByAttributeNullAttributeValue");
    try {
      R r = R.populateRegistry(0, 0, 0);
      GroupFinder.findByAttribute(r.rs, "description", null);
      fail("failed to throw IllegalArgumentException");
    }
    catch (IllegalArgumentException eIA) {
      assertTrue(true);
    }
    catch (Exception e) {
      unexpectedException(e);
    }
  } // public void testFailToFindGroupByAttributeNullAttributeValue()

  public void testFailToFindGroupByAttribute() {
    LOG.info("testFailToFindGroupByAttribute");
    try {
      R r = R.populateRegistry(0, 0, 0);
      assertDoNotFindGroupByAttribute(r.rs, "description", "i2:a:a");
      r.rs.stop();
    }
    catch (Exception e) {
      unexpectedException(e);
    }
  } // public void testFailToFindGroupByAttribute()

  public void testFindGroupByAttribute() {
    LOG.info("testFindGroupByAttribute");
    try {
      R       r   = R.populateRegistry(1, 1, 0);
      Group   gA  = r.getGroup("a", "a");
      String  val = "a unique value";
      gA.setDescription(val);
      gA.store();
      gA = assertFindGroupByAttribute(r.rs, "description", val);
      assertTrue( gA.getDescription().equals(val) );
      r.rs.stop();
    }
    catch (Exception e) {
      unexpectedException(e);
    }
  } // public void testFindGroupByAttribute()

} // public class TestGroupFinder_FindByAttribute
