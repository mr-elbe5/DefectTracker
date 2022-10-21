package de.elbe5.defecttracker.defect;

import de.elbe5.file.ImageData;

public class DefectImageData extends ImageData {

    public static int MAX_IMAGE_WIDTH = 1024;

    public DefectImageData(){
        setMaxWidth(MAX_IMAGE_WIDTH);
    }

}
