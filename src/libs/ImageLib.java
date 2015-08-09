package libs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import util.PathHelper;

public class ImageLib {

	public static Map<String, BufferedImage> lib;

	static {
		
		String basePath = PathHelper.basePath();
		
		System.out.println("BasePath = " + basePath);

		lib = new HashMap<String, BufferedImage>();
		try {
			lib.put("crystal-1",
					ImageIO.read(new File(basePath + "/img/crystal-1.png")));
			lib.put("crystal-2",
					ImageIO.read(new File(basePath + "/img/crystal-2.png")));
			lib.put("door-1",
					ImageIO.read(new File(basePath + "/img/door-1.png")));
			lib.put("door-2",
					ImageIO.read(new File(basePath + "/img/door-2.png")));
			lib.put("go-active",
					ImageIO.read(new File(basePath + "/img/go-active.png")));
			lib.put("go-inactive",
					ImageIO.read(new File(basePath + "/img/go-inactive.png")));
			lib.put("ap-0", ImageIO.read(new File(basePath + "/img/ap-0.png")));
			lib.put("ap-1", ImageIO.read(new File(basePath + "/img/ap-1.png")));
			lib.put("ap-2", ImageIO.read(new File(basePath + "/img/ap-2.png")));
			lib.put("ap-3", ImageIO.read(new File(basePath + "/img/ap-3.png")));
			lib.put("ap-4", ImageIO.read(new File(basePath + "/img/ap-4.png")));
			lib.put("ap-5", ImageIO.read(new File(basePath + "/img/ap-5.png")));
			lib.put("header",
					ImageIO.read(new File(basePath + "/img/header.png")));
			lib.put("bar", ImageIO.read(new File(basePath + "/img/bar.png")));
			lib.put("assault",
					ImageIO.read(new File(basePath + "/img/assault.png")));
			lib.put("defense",
					ImageIO.read(new File(basePath + "/img/defense.png")));
			lib.put("power",
					ImageIO.read(new File(basePath + "/img/power.png")));
			lib.put("deploy-1",
					ImageIO.read(new File(basePath + "/img/deploy-1.png")));
			lib.put("deploy-2",
					ImageIO.read(new File(basePath + "/img/deploy-2.png")));
			lib.put("header",
					ImageIO.read(new File(basePath + "/img/header.png")));
			lib.put("bar", ImageIO.read(new File(basePath + "/img/bar.png")));

			lib.put("knight-1",
					ImageIO.read(new File(basePath + "/img/knight-1.png")));
			lib.put("archer-1",
					ImageIO.read(new File(basePath + "/img/archer-1.png")));
			lib.put("cleric-1",
					ImageIO.read(new File(basePath + "/img/cleric-1.png")));
			lib.put("wizard-1",
					ImageIO.read(new File(basePath + "/img/wizard-1.png")));
			lib.put("ninja-1",
					ImageIO.read(new File(basePath + "/img/ninja-1.png")));

			lib.put("knight-2",
					ImageIO.read(new File(basePath + "/img/knight-2.png")));
			lib.put("archer-2",
					ImageIO.read(new File(basePath + "/img/archer-2.png")));
			lib.put("cleric-2",
					ImageIO.read(new File(basePath + "/img/cleric-2.png")));
			lib.put("wizard-2",
					ImageIO.read(new File(basePath + "/img/wizard-2.png")));
			lib.put("ninja-2",
					ImageIO.read(new File(basePath + "/img/ninja-2.png")));

			lib.put("scroll-1",
					ImageIO.read(new File(basePath + "/img/scroll-1.png")));
			lib.put("helmet-1",
					ImageIO.read(new File(basePath + "/img/helmet-1.png")));
			lib.put("scroll-2",
					ImageIO.read(new File(basePath + "/img/scroll-2.png")));
			lib.put("helmet-2",
					ImageIO.read(new File(basePath + "/img/helmet-2.png")));

			lib.put("knight-red-1",
					ImageIO.read(new File(basePath + "/img/knight-red-1.png")));
			lib.put("archer-red-1",
					ImageIO.read(new File(basePath + "/img/archer-red-1.png")));
			lib.put("cleric-red-1",
					ImageIO.read(new File(basePath + "/img/cleric-red-1.png")));
			lib.put("wizard-red-1",
					ImageIO.read(new File(basePath + "/img/wizard-red-1.png")));
			lib.put("ninja-red-1",
					ImageIO.read(new File(basePath + "/img/ninja-red-1.png")));

			lib.put("knight-red-2",
					ImageIO.read(new File(basePath + "/img/knight-red-2.png")));
			lib.put("archer-red-2",
					ImageIO.read(new File(basePath + "/img/archer-red-2.png")));
			lib.put("cleric-red-2",
					ImageIO.read(new File(basePath + "/img/cleric-red-2.png")));
			lib.put("wizard-red-2",
					ImageIO.read(new File(basePath + "/img/wizard-red-2.png")));
			lib.put("ninja-red-2",
					ImageIO.read(new File(basePath + "/img/ninja-red-2.png")));

			lib.put("crystal-red-1",
					ImageIO.read(new File(basePath + "/img/crystal-1.png")));
			lib.put("crystal-red-2",
					ImageIO.read(new File(basePath + "/img/crystal-2.png")));

			lib.put("sword",
					ImageIO.read(new File(basePath + "/img/sword.png")));
			lib.put("shield",
					ImageIO.read(new File(basePath + "/img/shield.png")));
			lib.put("potion",
					ImageIO.read(new File(basePath + "/img/potion.png")));
			lib.put("inferno",
					ImageIO.read(new File(basePath + "/img/inferno.png")));

			lib.put("scroll-small-1", ImageIO.read(new File(basePath
					+ "/img/scroll-small-1.png")));
			lib.put("helmet-small-1", ImageIO.read(new File(basePath
					+ "/img/helmet-small-1.png")));
			lib.put("scroll-small-2", ImageIO.read(new File(basePath
					+ "/img/scroll-small-2.png")));
			lib.put("helmet-small-2", ImageIO.read(new File(basePath
					+ "/img/helmet-small-2.png")));
			lib.put("sword-small",
					ImageIO.read(new File(basePath + "/img/sword-small.png")));
			lib.put("shield-small",
					ImageIO.read(new File(basePath + "/img/shield-small.png")));
			
			lib.put("beginner",
					ImageIO.read(new File(basePath + "/img/beginner.png")));
			
			lib.put("intermediate",
					ImageIO.read(new File(basePath + "/img/intermediate.png")));
			
			lib.put("expert",
					ImageIO.read(new File(basePath + "/img/expert.png")));
			
			lib.put("play-again",
					ImageIO.read(new File(basePath + "/img/play-again.png")));
			
			lib.put("play",
					ImageIO.read(new File(basePath + "/img/play.png")));

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
