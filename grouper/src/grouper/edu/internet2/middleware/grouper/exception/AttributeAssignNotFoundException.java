/**
 * Copyright 2014 Internet2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

package edu.internet2.middleware.grouper.exception;

/**
 * Exception thrown when an attribute value is not found within the Groups
 * Registry.
 * <p/>
 * @author  mchyzer.
 * @version $Id: AttributeAssignNotFoundException.java,v 1.1 2009-06-28 19:02:17 mchyzer Exp $
 */
@SuppressWarnings("serial")
public class AttributeAssignNotFoundException extends RuntimeException {
  
  /**
   * 
   */
  public AttributeAssignNotFoundException() { 
    super(); 
  }
  
  /**
   * 
   * @param msg
   */
  public AttributeAssignNotFoundException(String msg) { 
    super(msg); 
  }
  
  /**
   * 
   * @param msg
   * @param cause
   */
  public AttributeAssignNotFoundException(String msg, Throwable cause) { 
    super(msg, cause); 
  }
  
  /**
   * 
   * @param cause
   */
  public AttributeAssignNotFoundException(Throwable cause) { 
    super(cause); 
  }
}

