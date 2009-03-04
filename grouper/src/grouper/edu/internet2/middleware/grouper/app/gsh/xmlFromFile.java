/*
 * Copyright (C) 2006-2007 blair christensen.
 * All Rights Reserved.
 *
 * You may use and distribute under the same terms as Grouper itself.
 */

package edu.internet2.middleware.grouper.app.gsh;
import java.util.Properties;

import bsh.CallStack;
import bsh.Interpreter;
import edu.internet2.middleware.grouper.GrouperSession;
import edu.internet2.middleware.grouper.exception.GrouperException;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.grouper.xml.XmlImporter;
import edu.internet2.middleware.grouper.xml.XmlReader;

/**
 * Import Groups Registry from XML file.
 * <p/>
 * @author  blair christensen.
 * @version $Id: xmlFromFile.java,v 1.2 2008-09-29 03:38:28 mchyzer Exp $
 * @since   0.1.0
 */
public class xmlFromFile {

  // PUBLIC CLASS METHODS //

  /**
   * Import Groups Registry from XML file.
   * <p/>
   * @param   i           BeanShell interpreter.
   * @param   stack       BeanShell call stack.
   * @param   file        Import XML from this file.
   * @return  True if successful.
   * @throws  GrouperShellException
   * @since   0.1.0
   */
  public static boolean invoke(Interpreter i, CallStack stack, String file) 
    throws  GrouperShellException
  {
    GrouperShell.setOurCommand(i, true);
    //make sure right db
    GrouperUtil.promptUserAboutDbChanges("import data from xml", true);
    try {
      GrouperSession  s         = GrouperShell.getSession(i);
      XmlImporter     importer  = new XmlImporter(s, new Properties());
      importer.load( XmlReader.getDocumentFromFile(file) );
      return true;
    }
    catch (GrouperException eG) {
      GrouperShell.error(i, eG);
    }
    return false;
  } // public static boolean invoke(i, stack, file)

} // public class xmlFromFile
