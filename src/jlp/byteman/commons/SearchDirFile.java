/*Copyright 2013 Jean-Louis PASTUREL 
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*  limitations under the License.
*/
package jlp.byteman.commons;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author JLP
 *
 */
public final class SearchDirFile {
  static int BUFFER_SIZE = 10 * 1024;
 
  
  /**
   * Searching the youngest directory starting in a root directory and matching a regexp
 * @param prefix : the directory to start the search
 * @param reg : the regexp that the name of the directory has to match
 * @return the yougest directory
  */
public static File searchYoungestDir( String prefix, String  reg)
    {
      File root = new File(prefix);
      ArrayList<File> list = new ArrayList<File>();
      for (File f: root.listFiles()){
    	  if(f.isDirectory() && f.getName().matches(reg))
    	  {
    		  list.add(f);
    	  }
      }
      switch (list.size() )
      {
        case 0 :
        	return null;
        	
        case 1 :
        	return list.get(0);
        	
        default :
        	File[] tabFile=(File[])list.toArray();
        	Arrays.sort( tabFile,new Yt());
        	return  tabFile[0];
        	
        	
      }

    }
  public static File searchOldestDir(String prefix, String reg)
    {
	      File root = new File(prefix);
	      ArrayList<File> list = new ArrayList<File>();
	      for (File f:  root.listFiles()){
	    	  if(f.isDirectory() && f.getName().matches(reg))
	    	  {
	    		  list.add(f);
	    	  }
	      }
	      switch (list.size() )
	      {
	        case 0 :
	        	return null;
	        
	        case 1 :
	        	return list.get(0);
	        
	        default :
	        	File[] tabFile=(File[])list.toArray();
	        	Arrays.sort( tabFile,new Yt());
	        	return  tabFile[list.size()-1];
	        	
	        	
	      }

	    }

  public static File searchYoungestFile(String prefix, String reg)
    {
      File root = new File(prefix);
      ArrayList<File> list = new ArrayList<File>();
      for (File f:root.listFiles()){
    	  if(f.isFile() && f.getName().matches(reg))
    	  {
    		  list.add(f);
    	  }
      }
      switch (list.size() )
      {
        case 0 :
        	return null;
        	
        case 1 :
        	return list.get(0);
        	
        default :
        	File[] tabFile=(File[])list.toArray();
        	Arrays.sort( tabFile,new Yt());
        	return  tabFile[0];
        	        	
      }

    }
  public static File searchOldestFile(String prefix, String reg)
    {
      File root = new File(prefix);
      ArrayList<File> list = new ArrayList<File>();
      for (File f:root.listFiles()){
    	  if(f.isFile() && f.getName().matches(reg))
    	  {
    		  list.add(f);
    	  }
      }
      switch (list.size() )
      {
        case 0 :
        	return null;
        	
        case 1 :
        	return list.get(0);
        	
        default :
        	File[] tabFile=(File[])list.toArray();
        	Arrays.sort( tabFile,new Yt());
        	return  tabFile[list.size()-1];
        	
        	
      }

    }

 public ArrayList<File> recursiveListFiles(File f) {
	 ArrayList<File> arrFile=new ArrayList<File>();
	 for(File file: f.listFiles()){
		 arrFile.add(file);
	 }
	 for(File file: arrFile){
		 if(file.isDirectory()){
			 arrFile.addAll(recursiveListFiles(file));
		 }
	 }
    
    return arrFile;
  }

  

  public static Boolean isText(File file, Boolean acceptUTF8encoding)
    {

      boolean isBoolText = true ;
     byte[] buffer =  new byte[BUFFER_SIZE];
     InputStream reader = initReader(file);

      try {

        int read = reader.read(buffer);
        int lastByteTranslated = 0;
        // for (int i = 0; i < read && isText; i++)
       int i = 0;
        while (i < read && isBoolText) {
          int b = buffer[i];
         int ub = b & (0xff) ;// unsigned
         int utf8value = lastByteTranslated + ub;
          lastByteTranslated = (ub) << 8;

          if (ub == 0x09 /*(tab)*/
            || ub == 0x0A /*(line feed)*/
            || ub == 0x0C /*(form feed)*/
            || ub == 0x0D /*(carriage return)*/
            || (ub >= 0x20 && ub <= 0x7E) /* Letters, Numbers and other "normal synbols" */
            || (ub >= 0xA0 && ub <= 0xEE) /* Symbols of Latin-1 */
            || (acceptUTF8encoding && (utf8value >= 0x2E2E && utf8value <= 0xC3BF)) /* Latin-1 in UTF-8 encoding */ ) {
            // ok
          } else {
            isBoolText = false;
          }
          i += 1;
        }
      } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} finally {
        
          try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

      }

      return isBoolText;
    }
  private static InputStream initReader(File file)
    {

      try {
        if (file.getName().endsWith(".gz")) {

          return new BufferedInputStream(
            new GZIPInputStream(new FileInputStream(file)),
            BUFFER_SIZE);

        } else {
          return new BufferedInputStream(
            new FileInputStream(file), BUFFER_SIZE);

        }
      } catch(FileNotFoundException e){
    	  e.printStackTrace();
      }
      catch(IOException e){
    	  e.printStackTrace();
      }
      return null;
      //JLP
    }
  
  
  public static void  deleteDir( File dir)
    {
      if (!dir.isDirectory()) {
        try {
			throw new IOException("Not a directory " + dir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }

      File[] files = dir.listFiles();
      for (File file : files) {

        if (file.isDirectory()) {
          deleteDir(file);
        } else {
          boolean deleted = file.delete();
          if (!deleted) {
            try {
				throw new IOException("Unable to delete file" + file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          }
        }
      }

      dir.delete();
    }
 
}
class  Yt implements Comparator<java.io.File>
{
	  
	  boolean equals (File f1,File f2){
		  return f1.equals(f2);
	  }
	@Override
	public int compare(java.io.File f1, java.io.File f2) {
		// TODO Auto-generated method stub
		return (int) ( f1.lastModified() - f2.lastModified());
	}
	
	
}