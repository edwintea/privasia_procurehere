package com.privasia.procurehere.web.editors;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.privasia.procurehere.core.utils.Global;

public class ImageResize {

	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	private static final int IMG_WIDTH = 100;
	private static final int IMG_HEIGHT = 100;

	public static BufferedImage imageResize(BufferedImage originalImage2) {

		try {

			BufferedImage originalImage = ImageIO.read(new File("/home/javed/profile_img_toChange.jpg"));
			int type = originalImage2.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage2.getType();

			BufferedImage finalresizeImage = resizeImage(originalImage, type);
			LOG.info("inside image resize editor 001");
			return finalresizeImage;

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return originalImage2;

	}

	private static BufferedImage resizeImage(BufferedImage originalImage, int type) {
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();
		LOG.info("inside image resize editor 02");
		return resizedImage;
	}

}
