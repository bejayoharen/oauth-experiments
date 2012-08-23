package oauthsample.proxy;

import org.restlet.data.Reference;
import org.restlet.ext.oauth.OAuthServerResource;
import org.restlet.ext.oauth.OAuthUser;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * Simple Class that pulls a person's google contacts.
 * @author Martin Svensson
 *
 */
public class GoogleContacts extends ServerResource{
    
        public static final String API_URI = "https://www.google.com/m8/feeds/";
        public static final String FEED = "contacts/default/full";
       
        @Get public Representation getContacts(){
          OAuthUser u = (OAuthUser) getRequest().getClientInfo().getUser();
          String token = u.getAccessToken();
          Reference ref = new Reference(API_URI+FEED);
          ref.addQueryParameter(OAuthServerResource.OAUTH_TOKEN, token);
          ClientResource cr = new ClientResource(ref);
          return cr.get();
        }
      

}
