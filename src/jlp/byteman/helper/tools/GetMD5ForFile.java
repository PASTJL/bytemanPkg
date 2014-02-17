package jlp.byteman.helper.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;

public class GetMD5ForFile {
	

	public String checksum(File file, boolean boolRetInUppercase) {
		String strDigest = "";
		try {
			InputStream fin = new FileInputStream(file);
			java.security.MessageDigest md5er = MessageDigest
					.getInstance("MD5");
			byte[] buffer = new byte[8096];
			int read;
			do {
				read = fin.read(buffer);
				if (read > 0)
					md5er.update(buffer, 0, read);
			} while (read != -1);
			fin.close();
			byte[] digest = md5er.digest();
			if (digest == null)
				return null;
			if (boolRetInUppercase) {
				for (int i = 0; i < digest.length; i++) {
					strDigest += Integer
							.toString((digest[i] & 0xff) + 0x100, 16)
							.substring(1).toUpperCase();
				}
			} else {
				for (int i = 0; i < digest.length; i++) {
					strDigest += Integer
							.toString((digest[i] & 0xff) + 0x100, 16)
							.substring(1).toLowerCase();

				}
			}
			return strDigest;
		} catch (Exception e) {
			return null;
		}
	}

	

	public String getMD5Files(String files){
		String ret="";
		
		if (null != files && files .trim().length() > 0) {
			String[] tabMD5 = files .split(";");
			for (String str : tabMD5) {
				String tmpStr=str.substring(str.lastIndexOf("/")+1);
				ret+=tmpStr+":"+checksum(new File(str), false)+";";
			
			}
		}
		return ret;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GetMD5ForFile myMD5 = new GetMD5ForFile();
		
		System.out.println(myMD5.getMD5Files(args[0]));
	}

}
