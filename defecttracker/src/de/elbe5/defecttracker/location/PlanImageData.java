package de.elbe5.defecttracker.location;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.ImageUtil;
import de.elbe5.defecttracker.defect.DefectData;
import de.elbe5.file.ImageData;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class PlanImageData extends ImageData {

    public static int STD_SIZE = 2190;

    public static int MAX_PREVIEW_WIDTH = 600;
    public static int MAX_PREVIEW_HEIGHT = 600;

    public static int CROP_WIDTH = 600;
    public static int CROP_HEIGHT = 300;

    @Override
    public int getMaxPreviewWidth(){
        return MAX_PREVIEW_WIDTH;
    }

    @Override
    public int getMaxPreviewHeight(){
        return MAX_PREVIEW_HEIGHT;
    }

    public int getMaxWidth() {
        return STD_SIZE;
    }

    public int getMaxHeight() {
        return STD_SIZE;
    }

    public BinaryFile createLocationDefectPlan(byte[] primaryArrowBytes, List<DefectData> defects, float scale){
        BinaryFile file=null;
        try {
            BufferedImage bi = ImageUtil.createImage(getBytes(), "image/jpeg");
            BufferedImage redbi = ImageUtil.createImage(primaryArrowBytes,"image/png");
            assert(bi!=null);
            if (scale!=1){
                bi=ImageUtil.copyImage(bi,scale);
            }
            Graphics2D g = bi.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setFont(new Font("Monospaced", Font.PLAIN, 12));
            int biWidth=bi.getWidth();
            int biHeight=bi.getHeight();
            for (DefectData defect : defects){
                if (defect.getPositionX()>0 || defect.getPositionY()>0) {
                    g.setColor(Color.RED);
                    int posX=biWidth*defect.getPositionX()/100/100;
                    int posY=biHeight*defect.getPositionY()/100/100;
                    g.drawImage(redbi, null, posX - 9, posY - 2);
                    g.drawString(Integer.toString(defect.getDisplayId()), posX + 3, posY + 16);
                }
            }
            file=new BinaryFile();
            file.setFileName("defectPlan"+getId()+".jpg");
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
            file.setContentType("image/jpeg");
            ImageWriter writer = writers.next();
            file.setBytes(ImageUtil.writeImage(writer, bi));
            file.setFileSize(file.getBytes().length);
        }
        catch (IOException e){
            Log.error("could not create defect plan", e);
        }
        return file;
    }

    public BinaryFile createCroppedDefectPlan(byte[] primaryArrawBytes, int defectDisplayId, int positionX, int positionY) {
        BinaryFile file = null;
        try {
            BufferedImage source = ImageUtil.createImage(getBytes(), "image/jpeg");
            assert (source != null);
            int srcWidth=source.getWidth();
            int srcHeight=source.getHeight();
            assert(srcWidth>=CROP_WIDTH && srcHeight>=CROP_HEIGHT);
            int posX=srcWidth*positionX/100/100;
            int posY=srcHeight*positionY/100/100;
            int x = posX - CROP_WIDTH / 2;
            int y = posY - CROP_HEIGHT / 2;
            int dx = 0;
            int dy = 0;
            if (x < 0) {
                dx = x;
                x = 0;
            }
            else if (x+CROP_WIDTH>srcWidth){
                dx = x+CROP_WIDTH-srcWidth;
                x = srcWidth-CROP_WIDTH;
            }
            if (y < 0) {
                dy = y;
                y = 0;
            }
            else if (y+CROP_HEIGHT>srcHeight){
                dy=y+CROP_HEIGHT-srcHeight;
                y= srcHeight-CROP_HEIGHT;
            }
            BufferedImage bi = source.getSubimage(x, y, CROP_WIDTH, CROP_HEIGHT);
            BufferedImage redbi = ImageUtil.createImage(primaryArrawBytes, "image/png");
            assert (bi != null);
            Graphics2D g = bi.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setFont(new Font("Monospaced", Font.PLAIN, 12));
            g.setColor(Color.RED);
            g.drawImage(redbi, null, CROP_WIDTH / 2 - 9 + dx, CROP_HEIGHT / 2 - 2 + dy);
            g.drawString(Integer.toString(defectDisplayId), posX + 3, posY + 16);
            file = new BinaryFile();
            file.setFileName("defectCrop" + defectDisplayId + ".jpg");
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
            file.setContentType("image/jpeg");
            ImageWriter writer = writers.next();
            file.setBytes(ImageUtil.writeImage(writer, bi));
            file.setFileSize(file.getBytes().length);
        } catch (IOException e) {
            Log.error("could not create defect plan", e);
        }
        return file;
    }

    public BinaryFile createFullDefectPlan(byte[] primaryArrawBytes, int defectDisplayId, int positionX, int positionY) {
        BinaryFile file = null;
        try {
            BufferedImage bi = ImageUtil.createImage(getBytes(), "image/jpeg");
            BufferedImage redbi = ImageUtil.createImage(primaryArrawBytes, "image/png");
            assert (bi != null);
            Graphics2D g = bi.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setFont(new Font("Monospaced", Font.PLAIN, 12));
            g.setColor(Color.RED);
            int posX=bi.getWidth()*positionX/100/100;
            int posY=bi.getHeight()*positionY/100/100;
            g.drawImage(redbi, null, posX -9 , posY- 2);
            g.drawString(Integer.toString(defectDisplayId), posX + 3, posY + 16);
            file = new BinaryFile();
            file.setFileName("defectPlan" + defectDisplayId + ".jpg");
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
            file.setContentType("image/jpeg");
            ImageWriter writer = writers.next();
            file.setBytes(ImageUtil.writeImage(writer, bi));
            file.setFileSize(file.getBytes().length);
        } catch (IOException e) {
            Log.error("could not create defect plan", e);
        }
        return file;
    }

}
