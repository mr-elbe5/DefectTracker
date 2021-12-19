package de.elbe5.file;

import de.elbe5.base.data.BaseData;
import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.util.FileUtil;
import de.elbe5.base.util.StringUtil;
import de.elbe5.content.ContentData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.view.FileView;
import de.elbe5.view.IView;

public abstract class FileData extends BaseData {

    private String fileName = "";
    private String displayName = "";
    private String description = "";
    protected String contentType = null;
    protected int fileSize = 0;
    protected byte[] bytes = null;

    protected int parentId = 0;
    protected ContentData parent = null;

    public FileData() {
    }

    public String getType() {
        return getClass().getSimpleName();
    }

    public void adjustFileNameToDisplayName(){
        if (getFileName().isEmpty() || getDisplayName().isEmpty())
            return;
        int pos= getFileName().lastIndexOf('.');
        if (pos==-1)
            return;
        setFileName(StringUtil.toSafeWebName(getDisplayName())+ getFileName().substring(pos));
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDisplayName() {
        if (displayName.isEmpty())
            return FileUtil.getFileNameWithoutExtension(getFileName());
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isImage() {
        return false;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public ContentData getParent() {
        return parent;
    }

    public void setParent(ContentData parent) {
        this.parent = parent;
    }

    // view

    public IView getDefaultView(){
        return new FileView(this, false);
    }

    // multiple data

    public void setCreateValues(ContentData parent, RequestData rdata) {
        setNew(true);
        setId(FileBean.getInstance().getNextId());
        setCreatorId(rdata.getUserId());
        setChangerId(rdata.getUserId());
        setParentId(parent.getId());
        setParent(parent);
    }

    // helper

    public void createFromBinaryFile(BinaryFile file) {
        if (file != null && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmpty(file.getContentType())) {
            setFileName(file.getFileName());
            setBytes(file.getBytes());
            setFileSize(file.getBytes().length);
            setContentType(file.getContentType());
        }
    }

    public void readSettingsRequestData(SessionRequestData rdata) {
        setDisplayName(rdata.getString("displayName").trim());
        setDescription(rdata.getString("description"));
    }

}
