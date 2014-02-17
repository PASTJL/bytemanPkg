package jlp.byteman.packager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class GetMD5ForFile {
	String strDigest = "";

	public String checksum(File file, boolean boolRetInUppercase) {
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

	public String toString() {
		return strDigest;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GetMD5ForFile myMD5 = new GetMD5ForFile();
		System.out.println("MD5 for file : " + args[0] + " is => |"
				+ myMD5.checksum(new File(args[0]), false) + "|");
	}

}
