package oauthsample;

import oauthsample.user.UserMap;

import org.restlet.security.SecretVerifier;
import org.restlet.security.User;


/**
 * @author Martin Svensson
 *
 */
public class MyVerifier extends SecretVerifier {

    @Override
    public int verify(String identifier, char[] secret) {
        User u = UserMap.get(identifier);
        if(u != null && compare(u.getSecret(), secret))
            return RESULT_VALID;
        return RESULT_INVALID;
    }

}
