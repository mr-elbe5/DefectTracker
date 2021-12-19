package de.elbe5.file;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.FileUtil;
import de.elbe5.base.util.ImageUtil;
import de.elbe5.base.util.StringUtil;
import de.elbe5.request.SessionRequestData;
import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

public class ImageData extends FileData {

    public static int MAX_PREVIEW_WIDTH = 200;
    public static int MAX_PREVIEW_HEIGHT = 200;

    protected int width = 0;
    protected int height = 0;
    protected byte[] previewBytes = null;
    protected boolean hasPreview = false;

    private int maxWidth=0;
    private int maxHeight=0;
    public int maxPreviewWidth= MAX_PREVIEW_WIDTH;
    public int maxPreviewHeight= MAX_PREVIEW_HEIGHT;

    public ImageData() {
    }

    public boolean isImage() {
        return true;
    }

    // base data

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public byte[] getPreviewBytes() {
        return previewBytes;
    }

    public void setPreviewBytes(byte[] previewBytes) {
        this.previewBytes = previewBytes;
    }

    public boolean hasPreview() {
        return hasPreview;
    }

    public void setHasPreview(boolean hasPreview) {
        this.hasPreview = hasPreview;
    }

    public String getPreviewName(){
        return "preview_" + getId() + ".jpg";
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getMaxPreviewWidth() {
        return maxPreviewWidth;
    }

    public void setMaxPreviewWidth(int maxPreviewWidth) {
        this.maxPreviewWidth = maxPreviewWidth;
    }

    public int getMaxPreviewHeight() {
        return maxPreviewHeight;
    }

    public void setMaxPreviewHeight(int maxPreviewHeight) {
        this.maxPreviewHeight = maxPreviewHeight;
    }

    // view

    public String getPublishedUrl() {
        return "/ctrl/image/show/"+getId();
    }

    // multiple data

    @Override
    public void readSettingsRequestData(SessionRequestData rdata) {
        super.readSettingsRequestData(rdata);
        BinaryFile file = rdata.getFile("file");
        if (getMaxWidth() != 0 || getMaxHeight() != 0)
            createFromBinaryFile(file, getMaxWidth(), getMaxHeight(), getMaxPreviewWidth(), getMaxPreviewHeight(), true);
        else
            createFromBinaryFile(file, getMaxPreviewWidth(), getMaxPreviewHeight());
    }

    // helper

    public boolean createFromBinaryFile(BinaryFile file, int maxTumbnailWidth, int maxThumbnailHeight) {
        boolean success=false;
        if (file != null && file.isImage() && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmpty(file.getContentType())) {
            setFileName(file.getFileName());
            setBytes(file.getBytes());
            setFileSize(file.getBytes().length);
            setContentType(file.getContentType());
            try {
                createPreview(maxTumbnailWidth, maxThumbnailHeight);
                success = true;
            } catch (IOException e) {
                Log.warn("could not create buffered image");
            }
        }
        return success;
    }

    public boolean createFromBinaryFile(BinaryFile file, int maxWidth, int maxHeight, int maxTumbnailWidth, int maxThumbnailHeight, boolean expand) {
        boolean success=false;
        if (file != null && file.isImage() && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmpty(file.getContentType())) {
            setFileName(file.getFileName());
            setBytes(file.getBytes());
            setFileSize(file.getBytes().length);
            setContentType(file.getContentType());
            try {
                createResizedImage(maxWidth, maxHeight, expand);
                createPreview(maxTumbnailWidth, maxThumbnailHeight);
                success = true;
            } catch (IOException e) {
                Log.warn("could not create buffered image");
            }
        }
        return success;
    }

    public boolean createJpegFromBinaryFile(BinaryFile file, int maxWidth, int maxHeight, int maxTumbnailWidth, int maxThumbnailHeight, boolean expand) {
        boolean success=false;
        if (file != null && file.isImage() && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmpty(file.getContentType())) {
            setFileName(file.getFileName());
            setBytes(file.getBytes());
            setFileSize(file.getBytes().length);
            setContentType(file.getContentType());
            try {
                createResizedJpeg(maxWidth, maxHeight, expand);
                createPreview(maxTumbnailWidth, maxThumbnailHeight);
                success = true;
            } catch (IOException e) {
                Log.warn("could not create buffered image");
            }
        }
        return success;
    }

    public void createResizedImage(int width, int height, boolean expand) throws IOException {
        BufferedImage bi = ImageUtil.createResizedImage(getBytes(), getContentType(), width, height, expand);
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(getContentType());
        if (writers.hasNext()) {
            setContentType(getContentType());
        } else {
            writers = ImageIO.getImageWritersBySuffix(FileUtil.getExtension(getFileName()));
            if (writers.hasNext()) {
                setContentType("");
            } else {
                setFileName(FileUtil.getFileNameWithoutExtension(getFileName()) + ".jpg");
                writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
                setContentType("image/jpeg");
            }
        }
        ImageWriter writer = writers.next();
        assert (bi != null);
        setBytes(ImageUtil.writeImage(writer, bi));
        setFileSize(getBytes().length);
        setWidth(bi.getWidth());
        setHeight(bi.getHeight());
    }

    public void createResizedJpeg(int width, int height, boolean expand) throws IOException {
        BufferedImage bi = ImageUtil.createResizedImage(getBytes(), getContentType(), width, height, expand);
        setFileName(FileUtil.getFileNameWithoutExtension(getFileName()) + ".jpg");
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
        setContentType("image/jpeg");
        ImageWriter writer = writers.next();
        assert (bi != null);
        setBytes(ImageUtil.writeImage(writer, bi));
        setFileSize(getBytes().length);
        setWidth(bi.getWidth());
        setHeight(bi.getHeight());
    }

    public void createScaledJpeg(int scalePercent) throws IOException {
        BufferedImage bi = ImageUtil.createScaledImage(getBytes(), getContentType(), scalePercent);
        setFileName(FileUtil.getFileNameWithoutExtension(getFileName()) + ".jpg");
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
        setContentType("image/jpeg");
        ImageWriter writer = writers.next();
        assert (bi != null);
        setBytes(ImageUtil.writeImage(writer, bi));
        setFileSize(getBytes().length);
        setWidth(bi.getWidth());
        setHeight(bi.getHeight());
    }

    public void createPreview(int maxTumbnailWidth, int maxThumbnailHeight) throws IOException{
        if (!isImage())
            return;
        BufferedImage source = ImageUtil.createImage(getBytes(), getContentType());
        if (source != null) {
            setWidth(source.getWidth());
            setHeight(source.getHeight());
            float factor = ImageUtil.getResizeFactor(source, maxTumbnailWidth, maxThumbnailHeight, false);
            BufferedImage image = ImageUtil.copyImage(source, factor);
            createJpegPreview(image);
        }
    }

    protected void createJpegPreview(BufferedImage image) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
        ImageWriter writer = writers.next();
        assert (image != null);
        setPreviewBytes(ImageUtil.writeImage(writer, image));
    }

    @SuppressWarnings("unchecked")
    public JSONObject getJson(Locale locale){
        JSONObject json = new JSONObject();
        json.put("id",getId());
        json.put("fileName",getFileName());
        json.put("name",getDisplayName());
        json.put("displayName",getDisplayName());
        json.put("contentType",getContentType());
        json.put("width", getWidth());
        json.put("height", getHeight());
        return json;
    }

}
