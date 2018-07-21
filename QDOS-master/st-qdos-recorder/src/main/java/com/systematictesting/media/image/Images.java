package com.systematictesting.media.image;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.net.URL;

import javax.swing.ImageIcon;

public class Images {

	private Images() {
	}

	public static Image createImage(Class baseClass, String location) {
		URL resource = baseClass.getResource(location);
		if (resource == null) {
			System.err.println("Warning: Images.createImage no resource found for " + baseClass + " " + location);
			return null;
		}
		return createImage(resource);
	}

	public static Image createImage(URL resource) {
		Image image = Toolkit.getDefaultToolkit().createImage(resource);
		return image;
	}

	/** Converts the image to a buffered image. */
	public static BufferedImage toBufferedImage(RenderedImage rImg) {
		BufferedImage image;
		if (rImg instanceof BufferedImage) {
			image = (BufferedImage) rImg;
		} else {
			Raster r = rImg.getData();
			WritableRaster wr = WritableRaster.createWritableRaster(r.getSampleModel(), null);
			rImg.copyData(wr);
			image = new BufferedImage(rImg.getColorModel(), wr, rImg.getColorModel().isAlphaPremultiplied(), null);
		}
		return image;
	}

	/** Clone the image. */
	public static BufferedImage cloneImage(RenderedImage rImg) {
		BufferedImage image;

		Raster r = rImg.getData();
		WritableRaster wr = WritableRaster.createWritableRaster(r.getSampleModel(), null);
		rImg.copyData(wr);
		image = new BufferedImage(rImg.getColorModel(), wr, rImg.getColorModel().isAlphaPremultiplied(), null);

		return image;
	}

	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		image = new ImageIcon(image).getImage();

		BufferedImage bimage = null;

		if (System.getProperty("java.version").startsWith("1.4.1_")) {
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		} else {
			boolean hasAlpha;
			try {
				hasAlpha = hasAlpha(image);
			} catch (IllegalAccessError e) {
				hasAlpha = true;
			}

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			try {
				int transparency = Transparency.OPAQUE;
				if (hasAlpha) {
					transparency = Transparency.TRANSLUCENT;
				}
				GraphicsDevice gs = ge.getDefaultScreenDevice();
				GraphicsConfiguration gc = gs.getDefaultConfiguration();
				bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
			} catch (Exception e) {
			}

			if (bimage == null) {
				int type = BufferedImage.TYPE_INT_RGB;
				if (hasAlpha) {
					type = BufferedImage.TYPE_INT_ARGB;
				}
				bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
			}
		}

		Graphics g = bimage.createGraphics();

		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	public static boolean hasAlpha(Image image) {
		if (image instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage) image;
			return bimage.getColorModel().hasAlpha();
		}

		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		}

		ColorModel cm = pg.getColorModel();
		return cm.hasAlpha();
	}

	public static BufferedImage[] split(Image image, int count, boolean isHorizontal) {
		BufferedImage src = Images.toBufferedImage(image);
		if (count == 1) {
			return new BufferedImage[] { src };
		}

		BufferedImage[] parts = new BufferedImage[count];
		for (int i = 0; i < count; i++) {
			if (isHorizontal) {
				parts[i] = src.getSubimage(src.getWidth() / count * i, 0, src.getWidth() / count, src.getHeight());
			} else {
				parts[i] = src.getSubimage(0, src.getHeight() / count * i, src.getWidth(), src.getHeight() / count);
			}
		}
		return parts;
	}

	public static BufferedImage toIntImage(BufferedImage img) {
		if (img.getRaster().getDataBuffer() instanceof DataBufferInt) {
			return img;
		} else {
			BufferedImage intImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
			Graphics2D g = intImg.createGraphics();
			g.drawImage(img, 0, 0, null);
			g.dispose();
			return intImg;
		}
	}

	public static int[] toPixels(BufferedImage img) {
		return ((DataBufferInt) toIntImage(img).getRaster().getDataBuffer()).getData();
	}

	public static BufferedImage toImage(int[] pixels, int width, int height) {
		return new BufferedImage(DirectColorModel.getRGBdefault(), //
				Raster.createWritableRaster(
						new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, width, height,
								new int[] { 0xff00000, 0xff00, 0xff }), //
						new DataBufferInt(pixels, width * height), new Point(0, 0)),
				false, null);
	}
}
