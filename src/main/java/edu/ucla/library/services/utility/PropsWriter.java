
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

public final class PropsWriter {

    /**
     * Properties file used as source for output file.
    */
    private static final Properties PROPS = new Properties();

    /**
     * Expected number of parameters passed to class.
    */
    private static final int ARGS_COUNT = 3;

    /**
     * Delimiter character for value keys in template file.
    */
    private static final String DELIMITER = "$";

    /**
     * Private constructor for PropsWriter class.
    */
    private PropsWriter() {
        super();
    }

    /**
     * Main method for command-line execution.
     *
     * @param args array of parameters
    */
    @SuppressWarnings("uncommentedmain")
    public static void main(final String[] args) {
        verifyArgs(args);
        loadProps(args[0]);
        readWrite(args[1], args[2]);
    }

    /**
     * Public method for testing access or plugging into other classes.
     *
     * @param aDefaults default values fed to PROPS
     * @param aTemplate template for the output file
     * @param aOutput properties file built by app from aDefaults and aTemplate
    */
    public static void buildProperties(final String aDefaults,
        final String aTemplate, final String aOutput) {
        verifyArgs(new String[] {aDefaults, aTemplate, aOutput});
        loadProps(aDefaults);
        readWrite(aTemplate, aOutput);
    }

    /**
     * Check that parameters are valid.
     *
     * @param aArray array of file names
    */
    public static void verifyArgs(final String[] aArray) {
        if (aArray.length != ARGS_COUNT) {
            System.err.println(
                "usage: PropsWriter defaults template output");
            System.exit(ExitCodes.USAGE_ERROR);
        }
      // check that default props and template files exist/are readable
        checkExists(aArray[0]);
        checkReadable(aArray[0]);
        checkExists(aArray[1]);
        checkReadable(aArray[1]);
    }

    /**
     * Verify a file exists.
     *
     * @param aFileName pathed name of file to test
    */
    public static void checkExists(final String aFileName) {
        if (!Files.exists(FileSystems.getDefault().getPath(aFileName))) {
            System.err.println("File must exist: " + aFileName);
            System.exit(ExitCodes.DOESNT_EXIST_ERROR);
        }
    }

    /**
     * Verify a file is readable.
     *
     * @param aFileName pathed name of file to test
    */
    public static void checkReadable(final String aFileName) {
        if (!Files.isReadable(FileSystems.getDefault().getPath(aFileName))) {
            System.err.println("File must be readable: " + aFileName);
            System.exit(ExitCodes.NOT_READABLE_ERROR);
        }
    }

    /**
     * Load default properties into PROPS.
     *
     * @param aFileName pathed name of default props file
    */
    public static void loadProps(final String aFileName) {
        try {
            PROPS.load(new FileInputStream(new File(aFileName)));
        } catch (FileNotFoundException e) {
            System.err.println("Props file must exist: "
                + e.getMessage());
            System.exit(ExitCodes.DOESNT_EXIST_ERROR);
        } catch (IOException details) {
            System.err.println("I/O error reading props file: "
                + details.getMessage());
            System.exit(ExitCodes.IO_ERROR);
        }
    }

    /**
     * Splice default properties in template and write to output file.
     *
     * @param aInput pathed name of template file
     * @param aOutput pathed name of final properties file
    */
    public static void readWrite(final String aInput, final String aOutput) {
        final BufferedReader reader;
        final BufferedWriter writer;
        String line = null;
        try {
            reader = Files.newBufferedReader(
                FileSystems.getDefault().getPath(aInput));
            writer = Files.newBufferedWriter(
                FileSystems.getDefault().getPath(aOutput));
            while ((line = reader.readLine()) != null) {
                if (line.contains(DELIMITER)) {
                    final String key = line.substring(line.indexOf(DELIMITER) + 1);
                    writer.write(line.replace(DELIMITER, "").replace(key,
                        getValue(key)));
                        //PROPS.getProperty(key)));
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
            reader.close();
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (FileNotFoundException details) {
            System.err.println("Problem finding a file: "
                + details.getMessage());
            System.exit(ExitCodes.DOESNT_EXIST_ERROR);
        } catch (IOException details) {
            System.err.println("I/O problem with a file: "
                + details.getMessage());
            System.exit(ExitCodes.IO_ERROR);
        }
    }

    /**
     * Return environment variable value if exists, else default from properties.
     *
     * @param aKey key to check in enviroment and default properties
    */
    private static String getValue(final String aKey) {
        if (System.getenv(aKey) != null) {
            return System.getenv(aKey);
        } else {
            return PROPS.getProperty(aKey);
        }
    }

}
