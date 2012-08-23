package oauthsample;

import oauthsample.oauth.MyOauthServer;
import oauthsample.oid.AuthorizeOpenIdRequest;
import oauthsample.oid.ProviderApplication;
import oauthsample.protect.MyProtectedApplication;
import oauthsample.proxy.MyProxyApplication;
import oauthsample.user.UpdateUser;
import oauthsample.user.UserResource;

import org.restlet.Component;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;
import org.restlet.security.ChallengeAuthenticator;

/**
 * @author esvmart
 *
 */
public class TestApplication {
    
    public static void main(String[] args) throws Exception{
        Component c = new Component();
        c.getServers().add(Protocol.HTTP, 8095);
        c.getClients().add(Protocol.HTTP);
        c.getClients().add(Protocol.HTTPS);
        c.getClients().add(Protocol.RIAP);
        c.getClients().add(Protocol.CLAP);
        //map applications:
        c.getDefaultHost().attach("/oauth", new MyOauthServer());
        c.getDefaultHost().attach("/proxy", new MyProxyApplication());
        c.getDefaultHost().attach("/protect", new MyProtectedApplication());
        c.getDefaultHost().attach("/user", UserResource.class);
        
        MyAuthenticator au = new MyAuthenticator(c.getContext(),
                ChallengeScheme.HTTP_BASIC, "OpenId Authorize");
        au.setVerifier(new MyVerifier());
        au.setNext(UpdateUser.class);
        c.getDefaultHost().attach("/update", au);
        c.getDefaultHost().attach("/openid", new ProviderApplication());
        c.start();
        
    }

}
