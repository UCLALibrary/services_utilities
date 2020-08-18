
package edu.ucla.library.services.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Properties;

public class PropsWriter {

  private static final Properties props = new Properties();

  public PropsWriter() {
    super();
  }

  public static void main(String[] args) {
    verifyArgs(args);
    loadProps(args[0]);
    readWrite(args[1], args[2]);
  }

  public static void buildProperties(final String aDefaults, final String aTemplate, 
          final String aOutput) {
    verifyArgs(new String[] { aDefaults, aTemplate, aOutput });
    loadProps(aDefaults);
    readWrite(aTemplate, aOutput);
  }

  private static void verifyArgs(String[] aArray) {
    if (aArray.length != 3) {
      System.err.println("usage: PropsWriter defaultsFile templateFile propertiesFile");
      System.exit(101);
    }
    // check that default props and template file exist
    checkExists(aArray[0]);
    checkReadable(aArray[0]);
    checkExists(aArray[1]);
    checkReadable(aArray[1]);
  }

  private static void checkExists(final String aFileName) {
    if (!Files.exists(FileSystems.getDefault().getPath(aFileName))) {
      System.err.println("usage: PropsWriter defaultsFile templateFile propertiesFile");
      System.exit(102);
    }
  }
    
  private static void checkReadable(final String aFileName) {
    if (!Files.isReadable(FileSystems.getDefault().getPath(aFileName))) {
      System.err.println("usage: PropsWriter defaultsFile templateFile propertiesFile");
      System.exit(103);
    }
  }
    
  private static void loadProps(final String aFileName) {
    try {
      props.load(new FileInputStream(new File(aFileName)));
    } catch (FileNotFoundException e) {
      System.err.println(e.getMessage());
      System.exit(105);
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(106);
    }
  }

  private static void readWrite(final String aInput, final String aOutput) {
    final BufferedReader reader;
    final BufferedWriter writer;
    String line = null;
    try {
      reader = Files.newBufferedReader(FileSystems.getDefault().getPath(aInput));
      writer = Files.newBufferedWriter(FileSystems.getDefault().getPath(aOutput));
      while ((line = reader.readLine()) != null) {
        if (line.contains("$")) {
          String key = line.substring(line.indexOf("$") + 1);
          writer.write(line.replace("$", "").replace(key, props.getProperty(key)));
        } else {
          writer.write(line);
        }
        writer.newLine();
      }
      reader.close();
      writer.newLine();
      writer.flush();
      writer.close();
    } catch (FileNotFoundException e) {
      System.err.println(e.getMessage());
      System.exit(107);
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(108);
    }
  }

}
