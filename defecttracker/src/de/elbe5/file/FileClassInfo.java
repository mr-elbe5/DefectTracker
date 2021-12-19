package de.elbe5.file;

import de.elbe5.base.log.Log;

import java.lang.reflect.Constructor;

public abstract class FileClassInfo {

    private String type;
    private Constructor<? extends FileData> ctor;
    private FileBean bean;

    public FileClassInfo(Class<? extends FileData> fileClass, FileBean bean){
        type = fileClass.getSimpleName();
        try {
            ctor = fileClass.getConstructor();
        } catch (Exception e) {
            Log.error("no valid constructor found", e);
        }
        this.bean=bean;
    }

    public String getType(){
        return type;
    }

    public FileData getNewData(){
        try {
            return ctor.newInstance();
        } catch (Exception e) {
            Log.error("could not create file data for type "+type);
        }
        return null;
    }

    public FileBean getBean(){
        return bean;
    }

}
