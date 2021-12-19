package de.elbe5.request;

import de.elbe5.base.data.BaseData;

import java.util.HashMap;

public class ClipboardData extends HashMap<String, BaseData> {

    public void putData(String key, BaseData obj){
        put(key,obj);
    }

    public BaseData getData(String key){
        return get(key);
    }

    public boolean hasData(String key){
        return containsKey(key);
    }

    public void clearData(String key){
        remove(key);
    }
}
