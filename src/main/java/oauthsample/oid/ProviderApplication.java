/**
 * Copyright 2005-2011 Noelios Technologies.
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL 1.0 (the
 * "Licenses"). You can select the license that you prefer but you may not use
 * this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0.html
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1.php
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1.php
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0.php
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.noelios.com/products/restlet-engine
 * 
 * Restlet is a registered trademark of Noelios Technologies.
 */

package oauthsample.oid;


import oauthsample.MyVerifier;

import org.openid4java.server.ServerManager;
import org.restlet.Application;
import org.restlet.Restlet;

import org.restlet.data.ChallengeScheme;
import org.restlet.ext.openid.AttributeExchange;
import org.restlet.ext.openid.OpenIdVerifier;
import org.restlet.ext.openid.RedirectAuthenticator;
import org.restlet.ext.openid.internal.Provider;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;


/**
 * @author esvmart
 *
 */
public class ProviderApplication extends Application {
    
    @Override
    public synchronized Restlet createInboundRoot() {
        ServerManager sm = new ServerManager();
        Provider p = new Provider();
        sm.setOPEndpointUrl("http://localhost:8095/openid");
        sm.setEnforceRpId(true);
        getContext().getAttributes().put("server_manager", sm);
        getContext().getAttributes().put("openid_provider", p);
        //attribs.put("xrds", "http://localhost:8095/openid");
        Router router = new Router(getContext());
        
        //Try a redirect authenticator
        OpenIdVerifier verifier = new OpenIdVerifier("http://localhost:8095/openid/users/martin");
        verifier.addOptionalAttribute(AttributeExchange.EMAIL);
        verifier.addRequiredAttribute(AttributeExchange.FIRST_NAME);
        RedirectAuthenticator ra = new MyRedirectAuthenticator(getContext(), verifier, null);
        ra.setNext(Test.class);
        
        router.attach("/login", ra);
        ChallengeAuthenticator au = new ChallengeAuthenticator(getContext(),
                ChallengeScheme.HTTP_BASIC, "OpenId Authorize");
        au.setVerifier(new MyVerifier());
        au.setNext(AuthorizeOpenIdRequest.class);
        router.attach("/auth", au);
        
        router.attach("/users/{uname}", XRDSResource.class);
        
        router.attachDefault(ProviderResource.class);
      
      return router;
    } 

}
