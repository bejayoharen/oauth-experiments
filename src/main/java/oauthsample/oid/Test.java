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

import java.util.HashMap;
import java.util.Map;

import oauthsample.Utils;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.security.User;

/**
 * @author esvmart
 *
 */
public class Test extends ServerResource {
    
    
    @Get
    public Representation represent(){
        getLogger().info("Got Here");
        Map <String, Object> model = new HashMap <String, Object> ();
        User u = getClientInfo().getUser();
        model.put("uname", u.getIdentifier());
        model.put("firstName", u.getFirstName() == null ? "not defined" : u.getFirstName());
        model.put("lastName", u.getLastName() == null ? "not defined" : u.getLastName());
        model.put("email", u.getEmail() == null ? "not defined" : u.getEmail());
        return Utils.generateFM(model, "openIdUser.html", getContext());
    }
    
    @Post 
    public Representation represent(Representation input){
        MyRedirectAuthenticator.clearIdentiiferCookie(getRequest(), getResponse());
        return new StringRepresentation("Your identifier cookies should be cleared....try again");
    }

}
