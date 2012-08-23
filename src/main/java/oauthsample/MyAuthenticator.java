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

package oauthsample;

import oauthsample.user.UserMap;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeScheme;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.User;
import org.restlet.security.Verifier;

/**
 * @author esvmart
 *
 */
public class MyAuthenticator extends ChallengeAuthenticator {

    public MyAuthenticator(Context context, boolean optional,
            ChallengeScheme challengeScheme, String realm, Verifier verifier) {
        super(context, optional, challengeScheme, realm, verifier);
    }

    public MyAuthenticator(Context context, boolean optional,
            ChallengeScheme challengeScheme, String realm) {
        super(context, optional, challengeScheme, realm);
    }

    public MyAuthenticator(Context context, ChallengeScheme challengeScheme,
            String realm) {
        super(context, challengeScheme, realm);
    }

    @Override
    protected int authenticated(Request request, Response response) {
        getLogger().info("in my authenticator");
        int auth =  super.authenticated(request, response);
        if(request.getClientInfo() == null)
            return auth;
        User u = request.getClientInfo().getUser();
        if(u != null){
            User stored = UserMap.get(u.getIdentifier());
            if(stored == null){
                getLogger().info("Stored user does not exist!");
            }
            else{
                u.setEmail(stored.getEmail());
                u.setFirstName(stored.getFirstName());
                u.setLastName(stored.getLastName());
            }
        }
        return auth;
        
    }

}
