package oauthsample.oid;

import java.util.HashMap;
import java.util.Map;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.ext.openid.RedirectAuthenticator;
import org.restlet.security.User;
import org.restlet.security.Verifier;

public class MyRedirectAuthenticator extends RedirectAuthenticator {
    
    Map <String, User> users;

    public MyRedirectAuthenticator(Context context, Verifier verifier,
            Restlet forbiddenResource) {
        super(context, verifier, forbiddenResource);
        users = new HashMap <String, User> ();
    }

    public MyRedirectAuthenticator(Context context, Verifier verifier,
            String identifierCookie, String origRefCookie,
            Restlet forbiddenResource) {
        super(context, verifier, identifierCookie, origRefCookie, forbiddenResource);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void handleUser(User user, boolean cached) {
        getLogger().info("Handle User: "+cached);
        if(cached){
            User orig = users.get(user.getIdentifier());
            user.setEmail(orig.getEmail());
            user.setFirstName(orig.getFirstName());
            user.setLastName(orig.getLastName());
        }
        else
            users.put(user.getIdentifier(), user);
        getLogger().info("Checking user: "+user.getEmail()+" "+user.getFirstName()+" "+user.getLastName());
    }

}
