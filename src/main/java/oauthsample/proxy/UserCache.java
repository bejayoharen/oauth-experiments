package oauthsample.proxy;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ClientInfo;
import org.restlet.resource.Finder;
import org.restlet.resource.ServerResource;
import org.restlet.security.User;
import org.restlet.util.Series;

public class UserCache extends Restlet
{
  private final Map<String, User> accessTokens = new ConcurrentHashMap();
  private final String cookieId;
  private static final String DEFAULT_COOKIE = "_genid";
  private volatile Restlet first;
  private volatile Restlet second;

  public UserCache()
  {
    this(null, null, (Restlet) null);
  }

  public UserCache(String cookieName, Restlet first, Restlet second) {
    this.cookieId = (cookieName != null ? cookieName : "_genid");
    this.first = first; this.second = second;
    this.setContext(first.getContext());
  }
  
  public UserCache(String cookieName, Restlet first, Class<? extends ServerResource> targetClass){
      this.cookieId = (cookieName != null ? cookieName : "_genid");
      this.first = first;
      this.setContext(first.getContext());
      this.second = this.createFinder(targetClass);
  }

  public final void handle(Request req, Response res)
  {
    super.handle(req, res);
    if (hasUser(req)) {
      getLogger().info("Execute second since in cache");
      this.second.handle(req, res);
      return;
    }
    getLogger().info("Execute First since User is not in cache");
    res.getCookieSettings().removeAll(this.cookieId);
    this.first.handle(req, res);
    setUser(req, res);
  }

  public boolean hasUser(Request req)
  {
    String id = req.getCookies().getFirstValue(this.cookieId);
    if (id != null) {
      User u = (User)this.accessTokens.get(id);
      getLogger().info("Bypassing Filter since User in Cache");
      if (u != null) {
        req.getClientInfo().setUser(u);
        return true;
      }
    }
    return false;
  }

  protected void setUser(Request req, Response res) {
    User u = req.getClientInfo().getUser();
    if (u != null) {
      getLogger().info("Setting cached user after handle");
      String uuid = UUID.randomUUID().toString();
      this.accessTokens.put(uuid, u);
      res.getCookieSettings().set(this.cookieId, uuid);
    }
  }

  public void setNext(boolean first, Class<? extends ServerResource> targetClass) {
    setNext(first, new Finder(getContext(), targetClass));
  }

  public void setNext(boolean first, Restlet restlet) {
    if (first)
      this.first = restlet;
    else
      this.second = restlet;
  }

  public synchronized void start() throws Exception
  {
    if (isStopped()) {
      super.start();
      if (this.first != null) this.first.start();
      if (this.second != null) this.second.stop(); 
    }
  }

  public synchronized void stop()
    throws Exception
  {
    if (isStarted()) {
      if (this.first != null) this.first.stop();
      if (this.second != null) this.second.stop();
      super.stop();
    }
  }
}

/* Location:           C:\Users\esvmart\Downloads\fb-jar-with-dependencies.jar
 * Qualified Name:     com.ericsson.oauthsample.UserCache
 * JD-Core Version:    0.6.0
 */