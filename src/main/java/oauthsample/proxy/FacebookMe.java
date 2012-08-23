package oauthsample.proxy;

import org.restlet.data.Reference;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.oauth.OAuthUser;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * Simple Resource that pulls your facebook information
 * @author Martin Svensson
 *
 */
public class FacebookMe extends ServerResource {

    public static final String FB_API = "https://graph.facebook.com/";
    public static final String FB_API_CALL = "me";
    public static final String FB_ROLE = "offline_access";
                                          
    
    @Get public Representation getMe() throws Exception{
        OAuthUser user = (OAuthUser) getRequest().getClientInfo().getUser();
        Reference feedRef = new Reference(FB_API+FB_API_CALL);
        feedRef.addQueryParameter("access_token", user.getAccessToken());
        ClientResource cr = new ClientResource(feedRef);
        Representation rep = cr.get();
        if(cr.getStatus().isSuccess()){
            JsonRepresentation jrep = new JsonRepresentation(rep);
            return new StringRepresentation(jrep.getJsonObject().toString(2));
        }
        return null;
    }
    
}
