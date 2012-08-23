package oauthsample.user;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.security.User;


public class UserMap {
    
    private Map <String, User> map;
    
    private static class SingletonHolder { 
        private final static UserMap statusMap = new UserMap();
    }

    public static UserMap getInstance() {
        return SingletonHolder.statusMap;
    }
    
    public static Map <String, User> getMap(){
        return getInstance().map;
    }
    
    public static User get(String key){
        return getInstance().map.get(key);
    }
    
    private UserMap(){
        map = new ConcurrentHashMap <String, User> ();
        map.put("martin", new User("martin", "martin".toCharArray()));
    }
    
    public static void copyTo(User toCopy){
        User stored = getMap().get(toCopy.getIdentifier());
        if(stored != null){
            toCopy.setEmail(stored.getEmail());
            toCopy.setFirstName(stored.getFirstName());
            toCopy.setLastName(stored.getLastName());
        }
    }
    
    
    
   



}
