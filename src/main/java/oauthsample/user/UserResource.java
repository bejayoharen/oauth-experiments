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

package oauthsample.user;

import java.util.HashMap;
import java.util.Map;

import oauthsample.Utils;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.security.User;

/**
 * @author esvmart
 *
 */
public class UserResource extends ServerResource {
    
    @Get("html")
    public Representation represent() throws Exception{
        getLogger().info("Create User");
        ClientResource cr = new ClientResource("clap:///createUser.html");
        Representation r = cr.get();
        StringRepresentation toRet = null;
        if(cr.getStatus().isSuccess())
            toRet = new StringRepresentation(r.getText(), MediaType.TEXT_HTML);
        else
            this.getResponse().setStatus(cr.getStatus());

        cr.release();
        return toRet;
    }
    
    @Post("form:html")
    public Representation represent(Representation form) throws Exception{
        Form f = new Form(form);
        String uname = f.getFirstValue("uname").trim();
        String upass = f.getFirstValue("upass").trim();
        String action = f.getFirstValue("action");
        getLogger().info(uname+" "+upass+" "+action);
        
        if(uname.length() < 1 || upass.length() < 1){
            return generateError("username or password cannot be empty");
        }
        if("Create User".equals(action)){
        Map <String, User> m = UserMap.getMap();
        if(m.containsKey(uname)){
            return generateError("user "+uname+" alrady exists");
        }
        m.put(uname, new User(uname, upass.toCharArray()));
        Map <String, Object> model = new HashMap <String, Object> ();
        model.put("user", uname);
        model.put("action", "create");
        return Utils.generateFM(model, "success.html", getContext());
        }
        else if("Delete User".equalsIgnoreCase(action)){
            if(upass.equals(UserMap.get(uname))){
                Map <String, Object> model = new HashMap <String, Object> ();
                model.put("user", uname);
                model.put("action", "delete");
                UserMap.getMap().remove(uname);
                return Utils.generateFM(model, "success.html", getContext());
            }
            else
                return generateError("Could not delete user "+uname+" , password missmatch");
        }
        else
            return generateError("did not understand user action");
    }
    
    private Representation generateError(String errorMessage) throws Exception{
        Map <String, Object> model = new HashMap <String, Object> ();
        model.put("error", errorMessage);
        return Utils.generateFM(model, "error.html", getContext());
    }

}
