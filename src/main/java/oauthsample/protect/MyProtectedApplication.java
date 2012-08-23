/**
 * 
 */
package oauthsample.protect;

import java.util.ArrayList;

import org.restlet.Application;
import org.restlet.Restlet;

import org.restlet.ext.oauth.OAuthAuthorizer;
import org.restlet.routing.Router;
import org.restlet.security.Role;


/**
 * @author Martin Svensson
 *
 */
public class MyProtectedApplication extends Application {
  
  @Override
  public synchronized Restlet createInboundRoot() {
    Router router = new Router(getContext());
    OAuthAuthorizer auth = new OAuthAuthorizer(
      "http://www.mellowtech.org:8095/oauth/validate");
    //set the roles (scopes):
    ArrayList <Role> roles = new ArrayList <Role> ();
    roles.add(new Role("status", null));
    auth.setAuthorizedRoles(roles);
    auth.setNext(StatusResource.class);
    //Defines only one route
    router.attach("/status", auth);
    return router;
  }


}
