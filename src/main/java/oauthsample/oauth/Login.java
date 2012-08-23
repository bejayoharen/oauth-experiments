package oauthsample.oauth;

import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * Default resource that returns the index page for this application
 * @author Martin Svensson
 *
 */
public class Login extends ServerResource {

    @Get("html")
    public Representation represent() throws Exception{
        getLogger().info("In Login");
        ClientResource cr = new ClientResource("clap:///login.html");
        Representation r = cr.get();
        StringRepresentation toRet = null;
        if(cr.getStatus().isSuccess())
            toRet = new StringRepresentation(r.getText(), MediaType.TEXT_HTML);
        else
            this.getResponse().setStatus(cr.getStatus());

        cr.release();
        return toRet;
    }

}
