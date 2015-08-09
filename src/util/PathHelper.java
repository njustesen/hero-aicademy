package util;

import java.net.URISyntaxException;

public class PathHelper {

	static String basePath = "";
	
	public static String basePath(){
		if (basePath != "")
			return basePath;
		try {
			final String path = PathHelper.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI().getPath();
			final String[] folders = path.split("/");
			for (int i = 0; i < folders.length - 1; i++) {
				basePath += folders[i] + "/";
			}
		} catch (final URISyntaxException e1) {
			e1.printStackTrace();
		}
		basePath = basePath.substring(0, basePath.length());
		return basePath;
	}
	
}
