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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import oauthsample.user.UserMap;

import org.openid4java.message.AuthRequest;
import org.openid4java.message.Message;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.server.ServerManager;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.ext.openid.AttributeExchange;
import org.restlet.ext.openid.internal.OpenIdUser;
import org.restlet.ext.openid.internal.Provider;
import org.restlet.ext.openid.internal.ProviderResult;
import org.restlet.ext.openid.internal.UserSession;
import org.restlet.ext.openid.internal.XRDS;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

/**
 * @author esvmart
 *
 */
public class ProviderResource extends ServerResource {
    
    @Post("form")
    public Representation represent(Representation input) throws Exception{
        Form f = new Form(input);
        return handle(new ParameterList(f.getValuesMap()));
    }

    @Get("form")
    public Representation represent() throws Exception{

        return handle(new ParameterList(getQuery().getValuesMap()));
    }
    
    public Representation handle(ParameterList pl) throws Exception{
        Provider p = (Provider) getContext().getAttributes().get("openid_provider");
        ServerManager sm = (ServerManager) getContext().getAttributes().get("server_manager");
        String sessionId = getQuery().getFirstValue("oidsession");
        UserSession us = null;
        if(sessionId != null){
            getLogger().info("Existing session...should use");
            us = p.getSession(sessionId);
            if(us != null && us.getUser() != null){
                if(us.getUser().getApproved()){
                    getLogger().info("populating openid info");
                    populateUser(us, p);
                }
                else if(!us.getUser().getApproved()){
                    getLogger().info("user rejected request...do nothing");
                }
            }
            System.out.println(us.getParameterList());
        }
        ProviderResult plres = null;
        try{
            plres = p.processOPRequest(sm, us == null ? pl : null, getRequest(), getResponse(), us);
        }
        catch(Exception e){ //assume discovery??
            return XRDS.serverXrds("http://localhost:8095/openid", true);
        }
        if(plres == null){
            getLogger().info("Could not process");
        }
        else if(plres.ret == ProviderResult.OPR.GET_USER){
            getLogger().info("Needs to interact with user");
            //interact(p.getSession(plres.text), sm, p);
            Reference ref = new Reference("http://localhost:8095/openid/auth");
            ref.addQueryParameter("oidsession", plres.text);
            getResponse().redirectSeeOther(ref);
            return null;
        }
        else if(plres.text.equals("")){
            getLogger().info("everything ok!");
            return new EmptyRepresentation();
        }
        else{
            getLogger().info("returning: "+plres.text);
            return new StringRepresentation(plres.text);
        }
        return null;
    }
    
    private void populateUser(UserSession us, Provider p) throws Exception{
        Set <AttributeExchange> theSet = p.getOptionalAttributes(us);
        Set <AttributeExchange> all = new TreeSet <AttributeExchange> ();
        if(theSet != null) all.addAll(theSet);
        theSet = p.getRequiredAttributes(us);
        if(theSet != null) all.addAll(theSet);
        
        OpenIdUser user = us.getUser();
        user.setAttributes(all); //clear anything
        user.setClaimedId("http://localhost:8095/openid/users/"+user.getIdentifier());
        UserMap.copyTo(user);
    }
    
    private void interact(UserSession us, ServerManager sm, Provider p) throws Exception{
        String url = sm.getOPEndpointUrl();
        OpenIdUser user = new OpenIdUser(url+"/users/martin");
        us.setUser(user);
        Set <AttributeExchange> attrs = p.getRequiredAttributes(us);
        for(AttributeExchange ax : attrs){
            System.out.println("required: "+ax);
        }
        attrs = p.getOptionalAttributes(us);
        for(AttributeExchange ax : attrs){
            System.out.println("optional: "+ax);
        }
    }

}
