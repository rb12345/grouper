/*
 * Copyright (C) 2004-2005 University Corporation for Advanced Internet Development, Inc.
 * Copyright (C) 2004-2005 The University Of Chicago
 * All Rights Reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *  * Neither the name of the University of Chicago nor the names
 *    of its contributors nor the University Corporation for Advanced
 *   Internet Development, Inc. may be used to endorse or promote
 *   products derived from this software without explicit prior
 *   written permission.
 *
 * You are under no obligation whatsoever to provide any enhancements
 * to the University of Chicago, its contributors, or the University
 * Corporation for Advanced Internet Development, Inc.  If you choose
 * to provide your enhancements, or if you choose to otherwise publish
 * or distribute your enhancements, in source code form without
 * contemporaneously requiring end users to enter into a separate
 * written license agreement for such enhancements, then you thereby
 * grant the University of Chicago, its contributors, and the University
 * Corporation for Advanced Internet Development, Inc. a non-exclusive,
 * royalty-free, perpetual license to install, use, modify, prepare
 * derivative works, incorporate into the software or other computer
 * software, distribute, and sublicense your enhancements or derivative
 * works thereof, in binary and source code form.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND WITH ALL FAULTS.  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT ARE DISCLAIMED AND the
 * entire risk of satisfactory quality, performance, accuracy, and effort
 * is with LICENSEE. IN NO EVENT SHALL THE COPYRIGHT OWNER, CONTRIBUTORS,
 * OR THE UNIVERSITY CORPORATION FOR ADVANCED INTERNET DEVELOPMENT, INC.
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OR DISTRIBUTION OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.internet2.middleware.grouper;


import  edu.internet2.middleware.grouper.*;
import  edu.internet2.middleware.subject.*;
import  java.util.*;
import  net.sf.hibernate.*;


/** 
 * Default implementation of the I2MI {@link SubjectTypeAdapter} interface
 * for subjects of type "person".
 * <p />
 *
 * @author  blair christensen.
 * @version $Id: SubjectTypeAdapterPersonImpl.java,v 1.18 2005-03-26 02:59:08 blair Exp $
 */
public class  SubjectTypeAdapterPersonImpl
	extends     AbstractSubjectTypeAdapter
	implements  SubjectTypeAdapter
{

  /*
   * CONSTRUCTORS
   */
  public SubjectTypeAdapterPersonImpl() {
    // Nothing -- Yet
  }
 
 
  /*
   * PUBLIC INSTANCE METHODS
   */

  /**
   * Not Implemented.
   */
  public void destroy() { 
    Grouper.log().notimpl("SubjectTypeAdapterPersonImpldestroy");
    // XXX Nothing -- Yet
  }

  /**
   * Retrieve a <i>person</i> subject from a local table within the
   * groups registry.
   * <p />
   * @return  A {@link Subject} object.
   */
  public Subject getSubject(SubjectType type, String id) {
    String  qry     = "SubjectImpl.by.subjectid.and.typeid";
    Subject subj    = null;
    try {
      Query q = Grouper.dbSess().session().getNamedQuery(qry);
      q.setString(0, id);
      q.setString(1, type.getId());
      try {
        List vals = q.list();
        if (vals.size() == 1) {
          subj = (Subject) vals.get(0);
        }
      } catch (HibernateException e) {
        throw new RuntimeException(
                    "Error retrieving results for " + qry + ": " + e
                  );
      }
    } catch (HibernateException e) {
      throw new RuntimeException(
                  "Unable to get query " + qry + ": " + e
                );
    }
    return subj;
  }

  /**
   * Not Implemented.
   */
  public Subject getSubjectByDisplayId(SubjectType type, String displayId) {
    Grouper.log().notimpl("SubjectTypeAdapterPersonImplgetSubjectByDisplayId");
    return null;
  }
 
  /**
   * Not Implemented.
   */
  public Subject[] getSubjects(SubjectType type) {
    Grouper.log().notimpl("SubjectTypeAdapterPersonImplgetSubjects");
    return null;
  }

  /**
   * Not Implemented.
   */
  public void init() {
    Grouper.log().notimpl("SubjectTypeAdapterPersonImplinit");
    // XXX Nothing -- Yet
  }

  /**
   * Not Implemented.
   */
  public boolean isModifiable() {
    Grouper.log().notimpl("SubjectTypeAdapterPersonImplisModifiable");
    return false;
  }

  /**
   * Not Implemented.
   */
  public Subject newSubject(SubjectType type, 
                            String      id, 
                            String      name, 
                            String      description, 
                            String      displayId) 
  {
    Grouper.log().notimpl("SubjectTypeAdapterPersonImplnewSubject");
    return null;
  }

  /**
   * Not Implemented.
   */
  public Subject quickSearch(String searchValue) {
    Grouper.log().notimpl("SubjectTypeAdapterPersonImplquickSearch");
    return null;
  }

  /**
   * Not Implemented.
   */
  public Subject[] searchByIdentifier(SubjectType type, String id) {
    Grouper.log().notimpl("SubjectTypeAdapterPersonImplsearchByIdentifier");
    return null;
  }

}
