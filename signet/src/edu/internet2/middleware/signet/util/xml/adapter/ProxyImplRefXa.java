/*
	$Header: /home/hagleyj/i2mi/signet/src/edu/internet2/middleware/signet/util/xml/adapter/ProxyImplRefXa.java,v 1.4 2008-05-19 00:43:05 ddonn Exp $

Copyright (c) 2007 Internet2, Stanford University

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
package edu.internet2.middleware.signet.util.xml.adapter;

import edu.internet2.middleware.signet.ObjectNotFoundException;
import edu.internet2.middleware.signet.Proxy;
import edu.internet2.middleware.signet.ProxyImpl;
import edu.internet2.middleware.signet.Signet;
import edu.internet2.middleware.signet.Subsystem;
import edu.internet2.middleware.signet.dbpersist.HibernateDB;
import edu.internet2.middleware.signet.util.xml.binder.ObjectFactory;
import edu.internet2.middleware.signet.util.xml.binder.ProxyImplRefXb;

/**
 * ProxyImplRefXa<p>
 * Adapter class for Signet XML Binding.
 * Maps a ProxyImpl and a ProxyImplRefXb.
 * @see ProxyImpl
 * @see ProxyImplRefXb
 */
public class ProxyImplRefXa
{
	protected Signet			signet;
	protected ProxyImpl			signetProxy;
	protected ProxyImplRefXb	xmlProxyRef;


	public ProxyImplRefXa()
	{
	}

	public ProxyImplRefXa(Signet signet)
	{
		this.signet = signet;
	}

	public ProxyImplRefXa(ProxyImpl signetProxy, Signet signet)
	{
		this(signet);
		this.signetProxy = signetProxy;
		xmlProxyRef = new ObjectFactory().createProxyImplRefXb();
		setValues(signetProxy);
	}

	public ProxyImplRefXa(ProxyImplRefXb xmlProxyRef, Signet signet)
	{
		this(signet);
		this.xmlProxyRef = xmlProxyRef;
// setValues does a DB lookup, so don't create a ProxyImpl here
//		signetProxy = new ProxyImpl();
		setValues(xmlProxyRef);
	}


	public ProxyImpl getSignetProxy()
	{
		return (signetProxy);
	}

	public void setValues(ProxyImpl signetProxy)
	{
		xmlProxyRef.setId(signetProxy.getId().intValue());
		xmlProxyRef.setStatus(signetProxy.getStatus().toString());
		xmlProxyRef.setCanExtend(signetProxy.canExtend());
		xmlProxyRef.setCanUse(signetProxy.canUse());
		xmlProxyRef.setActAs(signetProxy.getGrantor().getId());
		Subsystem subsys = signetProxy.getSubsystem(); // proxy for signet_superuser has no subsystem
		if (null != subsys)
			xmlProxyRef.setSubsystem(subsys.getId());
		xmlProxyRef.setEffectiveDate(Util.convertDateToString(signetProxy.getEffectiveDate()));
	}


	public ProxyImplRefXb getXmlProxyRef()
	{
		return (xmlProxyRef);
	}

	public void setValues(ProxyImplRefXb xmlProxyRef)
	{
		HibernateDB hibr = signet.getPersistentDB();
		Proxy dbProxy = null;
		try
		{
			dbProxy = hibr.getProxy(xmlProxyRef.getId());
		}
		catch (NumberFormatException nfe) { nfe.printStackTrace(); }
		catch (ObjectNotFoundException onfe) { onfe.printStackTrace(); }

		signetProxy = (ProxyImpl)dbProxy;
	}

}