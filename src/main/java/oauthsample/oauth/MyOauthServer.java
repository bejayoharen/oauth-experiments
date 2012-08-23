package oauthsample.oauth;


import oauthsample.MyVerifier;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.ext.oauth.AccessTokenServerResource;
import org.restlet.ext.oauth.AuthPageServerResource;
import org.restlet.ext.oauth.AuthorizationServerResource;
import org.restlet.ext.oauth.ClientStore;
import org.restlet.ext.oauth.ClientStoreFactory;
import org.restlet.ext.oauth.HttpOAuthHelper;
import org.restlet.ext.oauth.ValidationServerResource;

import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;

/**
 * 
 */

/**
 * @author Martin Svensson
 *
 */
public class MyOauthServer extends Application {

  @Override
  public synchronized Restlet createInboundRoot() { 
      
      //Engine.setLogLevel(Level.FINE);
      Router root = new Router(getContext());
       
      //Challenge Authenticator
      ChallengeAuthenticator au = new ChallengeAuthenticator(getContext(),
              ChallengeScheme.HTTP_BASIC, "OAuth Test Server");
      au.setVerifier(new MyVerifier());
      au.setNext(AuthorizationServerResource.class);
      root.attach("/authorize", au);
            
      root.attach("/access_token", AccessTokenServerResource.class);
      root.attach("/validate",ValidationServerResource.class);
      root.attach(HttpOAuthHelper.getAuthPage(getContext()),
              AuthPageServerResource.class);
      
      //Set Template for AuthPage:
      HttpOAuthHelper.setAuthPageTemplate("authorize.html", getContext());
      //Dont ask for approval if previously approved
      HttpOAuthHelper.setAuthSkipApproved(true, getContext());
      
      //Attach Image Directory for our login.html page
      final Directory imgs = new Directory(getContext(), "clap:///img/");
      root.attach("/img", imgs);
      getContext().getLogger().info("done");
      
      //Finally create a test client:
      ClientStore clientStore = ClientStoreFactory.getInstance();
      clientStore.createClient("1234567890", "secret1", "http://www.mellowtech.org:8095/proxy");
      
      return root;
  }
      
}
