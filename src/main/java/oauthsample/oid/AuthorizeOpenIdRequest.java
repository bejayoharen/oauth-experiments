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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import oauthsample.Utils;

import org.restlet.data.Reference;
import org.restlet.ext.openid.AttributeExchange;
import org.restlet.ext.openid.internal.OpenIdUser;
import org.restlet.ext.openid.internal.Provider;
import org.restlet.ext.openid.internal.UserSession;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * @author esvmart
 *
 */
public class AuthorizeOpenIdRequest extends ServerResource {
    
    @Get
    public Representation reprsent() throws Exception{
        getLogger().info("In Authorize OpenIdRequest");
        //first check
        String action = getQuery().getFirstValue("action");
        String identifier = getClientInfo().getUser().getIdentifier();
        String sessionId = getQuery().getFirstValue("oidsession");
        Provider p = (Provider) getContext().getAttributes().get("openid_provider");
        Map <String, Object> data = new HashMap <String, Object> ();
        UserSession us = p.getSession(sessionId);
        
        //first check if we have completed...
        getLogger().info(action);
        if(action != null){
            OpenIdUser user = us.getUser();
            if(user == null){
                user = new OpenIdUser(identifier);
                us.setUser(user);
            }
            if(action.equals("Accept")){
                getLogger().info("User accepted openid request");
                user.setApproved(true);
                user.setIdentifier(identifier);
                //user.setClaimedId("http://localhost:8095/openid/users/"+identifier);
            }
            else {
                //Treat any other action as a reject
                getLogger().info("User did not accept openid request");
                user.setApproved(false);
            }
            Reference ref = new Reference("http://localhost:8095/openid");
            ref.addQueryParameter("oidsession", sessionId);
            this.getResponse().redirectSeeOther(ref);
            return null;
        }
        
        
        if(us == null){
            getLogger().info("no user session found");
            return null;
        }
        List <String> req = getDesc(p, us, true);
        if(req != null)
            data.put("required", req);
        List <String> opt = getDesc(p, us, false);
        if(opt != null){
            data.put("optional", opt);
        }
        data.put("uname", identifier);
        String rp = us.getParameterList().getParameterValue(Provider.OPENID_RETURNTO);
        if(rp == null)
            rp = us.getParameterList().getParameterValue(Provider.OPENID_REALM);
        data.put("req", rp);
        data.put("oidsession", sessionId);
        return Utils.generateFM(data, "oidauth.html", getContext());
    }
    
    private List <String> getDesc(Provider p, UserSession us, boolean required) throws Exception{
        Set <AttributeExchange> attrs = p.getAttributes(us.getParameterList(), required);
        if(attrs != null && attrs.size() > 0){
            List <String> theList = new ArrayList <String> ();
            for(AttributeExchange ax : attrs){
                theList.add(ax.getDescription());
            }
            return theList;
        }
        return null;
    }

}
