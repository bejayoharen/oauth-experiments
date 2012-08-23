/**
 * 
 */
package oauthsample.protect;

import java.util.Map;

import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * @author Martin Svensson
 *
 */
public class StatusResource extends ServerResource{
  
      
    @Get("json") public Representation getUserStatus() throws Exception{
        String id = getClientInfo().getUser().getIdentifier();
        getLogger().info("User Identifier: "+id);
        String newStatus = this.getQuery().getFirstValue("status");
        Map <String, String> statusMap = StatusMap.getMap();
        String oldStatus = statusMap.get(id);
        if(newStatus != null){
            statusMap.put(id, newStatus);
        }
        JSONObject toRet = new JSONObject();
        if(oldStatus != null)
            toRet.put("old", oldStatus);
        if(newStatus != null)
            toRet.put("status", newStatus);
        toRet.put("id", id);
        JsonRepresentation rep = new JsonRepresentation(toRet);
        return rep;
        //return new StringRepresentation("old status: "+oldStatus+"\tnew status: "+newStatus);
    }
  }


