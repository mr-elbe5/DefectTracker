/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.base.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * Class ImageTool is a helper class for image manipulation. <br>
 * Usage:
 */
public class ImageUtil {

    public static BufferedImage createImage(byte[] bytes, String contentType) throws IOException {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType(contentType);
        if (readers.hasNext() && bytes != null) {
            ImageReader reader = readers.next();
            BufferedImage image;
            boolean singleImage = true;
            if (contentType.endsWith("gif")) {
                singleImage = false;
            }
            ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes));
            reader.setInput(iis, singleImage);
            image = reader.read(0);
            return image;
        }
        return null;
    }

    public static BufferedImage createResizedImage(byte[] bytes, String contentType, int maxWidth, int maxHeight, boolean expand) throws IOException {
        BufferedImage source = createImage(bytes, contentType);
        if (source == null) {
            return null;
        }
        float factor = getResizeFactor(source, maxWidth, maxHeight, expand);
        if (factor == 1) {
            return source;
        }
        return copyImage(source, factor);
    }

    public static BufferedImage createScaledImage(byte[] bytes, String contentType, int scalePercent) throws IOException {
        BufferedImage source = createImage(bytes, contentType);
        if (source == null) {
            return null;
        }
        float factor = ((float)scalePercent)/100;
        if (factor == 1) {
            return source;
        }
        return copyImage(source, factor);
    }

    public static float getResizeFactor(BufferedImage source, int maxWidth, int maxHeight, boolean expand) {
        if (source == null) {
            return 1;
        }
        float wfactor = maxWidth == 0 ? 0 : ((float) maxWidth) / source.getWidth();
        float hfactor = maxHeight == 0 ? 0 : ((float) maxHeight) / source.getHeight();
        float factor = 1;
        if (wfactor != 0)
            factor=wfactor;
        if (hfactor != 0)
            factor=Math.min(factor, hfactor);
        if (factor>1 && !expand)
            factor = 1;
        return factor;
    }

    public static BufferedImage copyImage(BufferedImage source, float factor) {
        BufferedImage bi = new BufferedImage((int) (source.getWidth() * factor), (int) (source.getHeight() * factor), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        AffineTransform at = AffineTransform.getScaleInstance(factor, factor);
        g.drawRenderedImage(source, at);
        return bi;
    }

    public static byte[] writeImage(ImageWriter writer, BufferedImage image) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(bout);
        writer.setOutput(ios);
        writer.write(image);
        bout.flush();
        bout.close();
        return bout.toByteArray();
    }
}
