/**
 * 
 */
package oauthsample.proxy;

import java.util.HashMap;
import java.util.Map;

import oauthsample.Utils;

import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.oauth.OAuthServerResource;
import org.restlet.ext.oauth.OAuthUser;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.security.User;

/**
 * @author Martin Svensson
 *
 */
public class LocalResource extends ServerResource{
  
  @Get("html")
  public Representation represent() throws Exception{
    
    OAuthUser u = (OAuthUser) getRequest().getClientInfo().getUser();
    String token = u.getAccessToken();
 
    Reference meRef = new Reference("http://www.mellowtech.org:8095/protect/status");
    
    meRef.addQueryParameter(OAuthServerResource.OAUTH_TOKEN, token);
    String status = this.getQuery().getFirstValue("status");
    if(status != null){
        meRef.addQueryParameter("status", status);
    }
    ClientResource meResource = new ClientResource(getContext(),meRef);
    Representation r = meResource.get(MediaType.APPLICATION_JSON);
    JsonRepresentation meRepr = new JsonRepresentation(r);
    getLogger().info("After Status Retrieval");
    if(meResource.getResponse().getStatus().isSuccess()){
        Map <String, Object> m = new HashMap <String, Object> ();
        JSONObject obj = meRepr.getJsonObject();
        getLogger().info(obj.toString(2));
        getLogger().info(getContext().toString());
        if(obj.has("old")) m.put("old", obj.get("old"));
        if(obj.has("status")) m.put("status", obj.get("status"));
        m.put("id", obj.get("id"));
        meResource.getResponse().release();
        meResource.release();
        return Utils.generateFM(m, "status.html", getContext());
    }
    return null;
  } 


}
