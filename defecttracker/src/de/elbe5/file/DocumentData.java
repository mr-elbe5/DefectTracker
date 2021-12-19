package de.elbe5.file;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.request.SessionRequestData;

public class DocumentData extends FileData {

    public DocumentData() {
    }

    public String getPublishedUrl() {
        return "/ctrl/document/show/"+getId();
    }

    // multiple data

    @Override
    public void readSettingsRequestData(SessionRequestData rdata) {
        super.readSettingsRequestData(rdata);
        BinaryFile file = rdata.getFile("file");
        createFromBinaryFile(file);
        if (getDisplayName().isEmpty()) {
            setDisplayName(file.getFileNameWithoutExtension());
        }
        else{
            adjustFileNameToDisplayName();
        }
    }

}
