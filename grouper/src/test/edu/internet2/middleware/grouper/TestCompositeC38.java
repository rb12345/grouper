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
import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;

import edu.internet2.middleware.grouper.misc.CompositeType;
import edu.internet2.middleware.grouper.registry.RegistryReset;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.subject.Subject;

/**
 * @author  blair christensen.
 * @version $Id: TestCompositeC38.java,v 1.5 2008-09-29 03:38:27 mchyzer Exp $
 */
public class TestCompositeC38 extends TestCase {

  // Private Static Class Constants
  private static final Log LOG = GrouperUtil.getLog(TestCompositeC38.class);

  public TestCompositeC38(String name) {
    super(name);
  }

  protected void setUp () {
    LOG.debug("setUp");
    RegistryReset.reset();
  }

  protected void tearDown () {
    LOG.debug("tearDown");
  }

  public void testDelUnionWithTwoCompositeChildrenAndCompositeParent() {
    LOG.info("testDelUnionWithTwoCompositeChildrenAndCompositeParent");
    try {
      R       r     = R.populateRegistry(1, 9, 4);
      Group   a     = r.getGroup("a", "a");
      Group   b     = r.getGroup("a", "b");
      Group   c     = r.getGroup("a", "c");
      Group   d     = r.getGroup("a", "d");
      Group   e     = r.getGroup("a", "e");
      Group   f     = r.getGroup("a", "f");
      Group   g     = r.getGroup("a", "g");
      Group   h     = r.getGroup("a", "h");
      Group   i     = r.getGroup("a", "i");
      Subject subjA = r.getSubject("a");
      Subject subjB = r.getSubject("b");
      Subject subjC = r.getSubject("c");
      Subject subjD = r.getSubject("d");
  
      a.addMember(subjA);
      b.addMember(subjB);
      c.addCompositeMember(CompositeType.COMPLEMENT, a, b); // subjA - subjB
      d.addMember(subjC);
      e.addMember(subjD);
      f.addCompositeMember(CompositeType.COMPLEMENT, d, e); // subjC - subjD
      g.addCompositeMember(CompositeType.COMPLEMENT, c, f); // subjA - subjC
      i.addCompositeMember(CompositeType.COMPLEMENT, g, h); // subjA - -
      g.deleteCompositeMember();

      Assert.assertTrue(  "a isComposite"   , a.isComposite()   );
      Assert.assertFalse( "a !hasComposite" , a.hasComposite()  );
      T.amount("a members", 1, a.getMembers().size());
      Assert.assertTrue("a has subjA", a.hasMember(subjA));

      Assert.assertTrue(  "b isComposite"   , b.isComposite()   );
      Assert.assertFalse( "b !hasComposite" , b.hasComposite()  );
      T.amount("b members", 1, b.getMembers().size());
      Assert.assertTrue("b has subjB", b.hasMember(subjB));

      Assert.assertFalse( "c !isComposite"  , c.isComposite()   );
      Assert.assertTrue(  "c hasComposite"  , c.hasComposite()  );
      T.amount("c members", 1, c.getMembers().size());
      Assert.assertTrue("c hasMember subjA", c.hasMember(subjA));

      Assert.assertTrue(  "d isComposite"   , d.isComposite()   );
      Assert.assertFalse( "d !hasComposite" , d.hasComposite()  );
      T.amount("d members", 1, d.getMembers().size());
      Assert.assertTrue("d has subjC", d.hasMember(subjC));

      Assert.assertTrue(  "e isComposite"   , e.isComposite()   );
      Assert.assertFalse( "e !hasComposite" , e.hasComposite()  );
      T.amount("e members", 1, e.getMembers().size());
      Assert.assertTrue("e has subjD", e.hasMember(subjD));

      Assert.assertFalse( "f !isComposite"  , f.isComposite()   );
      Assert.assertTrue(  "f hasComposite"  , f.hasComposite()  );
      T.amount("f members", 1, f.getMembers().size());
      Assert.assertTrue("f hasMember subjC", f.hasMember(subjC));

      Assert.assertTrue(  "g isComposite"   , g.isComposite()   );
      Assert.assertFalse( "g !hasComposite" , g.hasComposite()  );
      T.amount("g members", 0, g.getMembers().size());

      Assert.assertTrue(  "h isComposite"   , h.isComposite()   );
      Assert.assertFalse( "h !hasComposite" , h.hasComposite()  );
      T.amount("h members", 0, h.getMembers().size());

      Assert.assertFalse( "i !isComposite"  , i.isComposite()   );
      Assert.assertTrue(  "i hasComposite"  , i.hasComposite()  );
      T.amount("i members", 0, i.getMembers().size());

      r.rs.stop();
    }
    catch (Exception e) {
      T.e(e);
    }
  } // public void testDelUnionWithTwoCompositeChildrenAndCompositeParent()

}
