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
public class UpdateUser extends ServerResource {
    
    @Get("html")
    public Representation represent() throws Exception{
        return showUpdateForm();
    }
    
    @Post("form:html")
    public Representation represent(Representation form) throws Exception{
        Form f = new Form(form);
        String firstName = f.getFirstValue("firstName").trim();
        String lastName = f.getFirstValue("lastName").trim();
        String email = f.getFirstValue("email");
        getLogger().info(firstName+" "+lastName+" "+email);
        
        User u1 = getClientInfo().getUser();
        User u = UserMap.get(u1.getIdentifier());
        
        if(u == null)
            getLogger().info("No Known User");
        
        if(firstName != null){
            u.setFirstName(firstName);
            u1.setFirstName(firstName);
        }
        if(lastName != null){
            u.setLastName(lastName);
            u1.setLastName(lastName);
        }
        if(email != null){
            u.setEmail(email);
            u1.setEmail(email);
        }
        
        return showUpdateForm();
    }
    
    public Representation showUpdateForm() throws Exception{
        getLogger().info("Update User");
        Map <String, Object> model = new HashMap <String, Object> ();
        User u = this.getClientInfo().getUser();
        model.put("uname", u.getIdentifier());
        model.put("firstName", u.getFirstName() == null ? "" : u.getFirstName());
        model.put("lastName", u.getLastName() == null ? "" : u.getLastName());
        model.put("email", u.getEmail() == null ? "" : u.getEmail());
        return Utils.generateFM(model, "updateUser.html", getContext());
    }
    

}
