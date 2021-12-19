package de.elbe5.content;

import de.elbe5.base.log.Log;

import java.lang.reflect.Constructor;

class ContentClassInfo {

    private String type;
    private Constructor<? extends ContentData> ctor;
    private ContentBean bean;

    public ContentClassInfo(Class<? extends ContentData> contentClass, ContentBean bean){
        type = contentClass.getSimpleName();
        try {
            ctor = contentClass.getConstructor();
        } catch (Exception e) {
            Log.error("no valid constructor found", e);
        }
        this.bean=bean;
    }

    public ContentData getNewData(){
        try {
            return ctor.newInstance();
        } catch (Exception e) {
            Log.error("could not create content data for type "+type);
        }
        return null;
    }

    public ContentBean getBean(){
        return bean;
    }

}
