package oauthsample.protect;

import java.util.HashMap;
import java.util.Map;


public class StatusMap {
    
    private Map <String, String> map;
    
    private static class SingletonHolder { 
        private final static StatusMap statusMap = new StatusMap();
    }

    public static StatusMap getInstance() {
        return SingletonHolder.statusMap;
    }
    
    public static Map <String, String> getMap(){
        return getInstance().map;
    }
    
    private StatusMap(){
        map = new HashMap <String, String> ();
    }
    
    
    
   



}
