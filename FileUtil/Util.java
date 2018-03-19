package FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Util {

	private static String[] filepathofall = new String[1000000];
	private static int k = 0; //k为文件总数
	
	private static boolean matchType(File file, String fileTypes) {
		boolean rt = false;
		String fExtName = file.getPath();
		int i = fExtName.lastIndexOf('.');
		if (i >= 0) {
			fExtName = fExtName.substring(i);
			fExtName = fExtName.toLowerCase();
			i = fileTypes.indexOf(fExtName);
			if (i >= 0)
				if (i + fExtName.length() >= fileTypes.length() || fileTypes.charAt(i + fExtName.length()) == ',')
					rt = true;
		}
		return rt;
	}

	public static String[] fileList(String filepath, String fileTypes) throws FileNotFoundException, IOException {
		File file = new File(filepath);
		if (!file.isDirectory()) {
			if (matchType(file, fileTypes)) {
				System.out.println("absolutepath=" + file.getAbsolutePath());
			}
		} else if (file.isDirectory()) {
			String[] filelist = file.list();
			for (int i = 0; i < filelist.length; i++) {
				File readfile = new File(filepath + "\\" + filelist[i]);
				if (!readfile.isDirectory()) {
					if (matchType(readfile, fileTypes)) {
						//System.out.println("absolutepath=" + readfile.getAbsolutePath());
						filepathofall[k++] = readfile.getAbsolutePath();
					}
				} else if (readfile.isDirectory()) {
					fileList(filepath + "/" + filelist[i], fileTypes);
				}
			}
		}
		return filepathofall;
	}
}