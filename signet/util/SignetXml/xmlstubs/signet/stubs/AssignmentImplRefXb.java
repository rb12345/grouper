/*--
	$Header: /home/hagleyj/i2mi/signet/util/SignetXml/xmlstubs/signet/stubs/AssignmentImplRefXb.java,v 1.2 2008-05-17 20:54:09 ddonn Exp $

Copyright 2006 Internet2, Stanford University

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
package signet.stubs;

import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * AssignmentImplXb 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="AssignmentImplRefXb",
		namespace="http://www.internet2.edu/signet",
		propOrder = { }
)
public class AssignmentImplRefXb
{
    @XmlAttribute(name="canGrant", required=true)
	protected boolean	canGrant;

    @XmlAttribute(name="canUse", required=true)
	protected boolean	canUse;

	/** The status (ACTIVE | INACTIVE | PENDING) of this EntityImplXb */
	@XmlAttribute(name="status", required=false)
	protected String	status;

	/** A String-based ID, overridden by GrantableImplXb */
	@XmlAttribute(name="id", required=true)
	protected String	id;	// see GrantableImplXb, has an Integer id defined

}