/*
  Copyright (C) 2007 University Corporation for Advanced Internet Development, Inc.
  Copyright (C) 2007 The University Of Chicago

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
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author  blair christensen.
 * @version $Id: Suite_Unit_API.java,v 1.10 2008-09-29 03:38:27 mchyzer Exp $
 * @since   1.2.0
 */
public class Suite_Unit_API extends GrouperTest {

  static public Test suite() {
    TestSuite suite = new TestSuite();
    suite.addTest( Suite_Unit_API_CompositeMembershipValidator.suite() ); 
    suite.addTest( Suite_Unit_API_CompositeValidator.suite() ); 
    suite.addTest( Suite_Unit_API_EffectiveMembershipValidator.suite() ); 
    suite.addTest( Suite_Unit_API_GrouperSourceAdapter.suite() ); 
    suite.addTest( Suite_Unit_API_ImmediateMembershipValidator.suite() ); 
    suite.addTest( Suite_Unit_API_Stem.suite() ); 
    suite.addTest( Suite_U_API_XmlExporter.suite() );
    return suite;
  }

} 
