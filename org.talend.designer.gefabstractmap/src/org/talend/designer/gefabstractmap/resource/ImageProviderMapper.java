// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.gefabstractmap.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.talend.designer.gefabstractmap.NewAbstractmap;

/**
 * DOC smallet class global comment. Detailled comment <br/>
 * 
 * $Id: ImageProviderMapper.java 39246 2010-03-26 17:56:43Z sgandon $
 * 
 */
public class ImageProviderMapper {

    private static Map<ImageInfo, Image> imageCache = new HashMap<ImageInfo, Image>();

    private static List<Image> disabledImageCache = new ArrayList<Image>();

    public static Image getImage(ImageDescriptor desc) {
        return desc.createImage();
    }

    public static Image getImage(ImageInfo imageInfo) {
        Image imageFromCache = imageCache.get(imageInfo);
        if (imageFromCache != null) {
            return imageFromCache;
        }
        Image image = getImage(getImageDescriptor(imageInfo));
        imageCache.put(imageInfo, image);
        return image;
    }

    public static ImageDescriptor getImageDescriptor(ImageInfo image) {
        return ImageDescriptor.createFromFile(NewAbstractmap.class, image.getPath());
    }

    public static void cacheDisabledImage(Image image) {
        disabledImageCache.add(image);
    }

    /**
     * You can continue to use the provider after call this method.
     */
    public static void releaseImages() {
        Collection<Image> images = imageCache.values();
        for (Image image : images) {
            if (!image.isDisposed()) {
                image.dispose();
            }
        }
        imageCache.clear();

        for (Image image : disabledImageCache) {
            if (!image.isDisposed()) {
                image.dispose();
            }
        }
        disabledImageCache.clear();
    }

    public static void dispose(ImageInfo imageInfo) {
        Image image = imageCache.get(imageInfo);
        if (!image.isDisposed()) {
            image.dispose();
        }
        imageCache.remove(imageInfo);
    }

}
