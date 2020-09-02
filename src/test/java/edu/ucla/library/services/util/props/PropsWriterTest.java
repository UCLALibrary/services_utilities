
package edu.ucla.library.services.util.props;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.contrib.java.lang.system.SystemErrRule;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.nio.file.FileSystems;
import java.nio.file.Files;

/*
 * Unit tests for ProsWriter.java
 */
public class PropsWriterTest {

  /**
   * Default properties file.
  */
    private static final String DEFAULTS =
        "src/test/resources/properties.default";

  /**
   * Output template file.
  */
    private static final String TEMPLATE =
        "src/test/resources/properties.tmpl";

  /**
   * Final output file.
  */
    private static final String OUTPUT =
        "src/test/resources/output.properties";

  /**
   * Fake file name used to cause app failures.
  */
    private static final String FAKE =
        "src/test/resources/not.there";

  /**
   * Unreadable file
  */
    private static final String NOREAD =
        "src/test/resources/properties.noread";

  /**
   * Key for default property value
  */
    private static final String DEFAULT_KEY =
        "fester.http.port";

  /**
   * Expected value for DEFAULT_KEY
  */
    private static final String DEFAULT_VALUE =
        "8888";

  /**
   * Key for ENV property value
  */
    private static final String ENV_KEY =
        "fester.s3.bucket";

  /**
   * Expected value for ENV_KEY
  */
    private static final String ENV_VALUE =
        "iiif-manifest-store";

  /**
   * rule that allows catching System.err output in tests.
  */
    @Rule
    public final SystemErrRule mySystemErrRule = new SystemErrRule().enableLog();

  /**
   * Set up before a PropsWriter test.
   *
   * @throws SecurityException If there is a problem setting file permissions
  */
    @Before
    public void setUp() throws SecurityException {
        // remove read permissions on unreadable file
        FileSystems.getDefault().getPath(NOREAD).toFile().setReadable(false, false);
    }

  /**
   * Cleans up after a PropsWriter test.
   *
   * @throws IOException If there is a problem deleting the file
   * @throws SecurityException If there is a problem setting file permissions
  */
    @After
    public void tearDown() throws IOException, SecurityException {
        // delete the output file if left over from test run
        Files.deleteIfExists(FileSystems.getDefault().getPath(OUTPUT));
        // restore read permissions on unreadable file for git access
        FileSystems.getDefault().getPath(NOREAD).toFile().setReadable(true, false);
    }

  /**
   * Tests happy path for app execution.
   *
   * @throws IOException If there is a problem reading OUTPUT
   * @throws FileNotFoundException if OUTPUT can't be found
  */
    @Test
    public void testBuildProperties() throws FileNotFoundException, IOException {
        final Properties props = new Properties();

        PropsWriter.buildProperties(DEFAULTS, TEMPLATE, OUTPUT);
        assertTrue(Files.exists(FileSystems.getDefault().getPath(OUTPUT)));
        props.load(Files.newBufferedReader(FileSystems.getDefault().getPath(OUTPUT)));
        assertEquals(DEFAULT_VALUE, props.get(DEFAULT_KEY));
        assertEquals(ENV_VALUE, props.get(ENV_KEY));
    }

  /**
   * Tests that app catches nonexistent files.
   *
  */
    @Test
    public void testCheckExists() throws Exception {
        final int statusCode = catchSystemExit(() -> {
            PropsWriter.checkExists(FAKE);
        });
        assertTrue(mySystemErrRule.getLog().contains("File must exist"));
        assertEquals(ExitCodes.DOESNT_EXIST_ERROR, statusCode);
    }

  /**
   * Tests that app catches nonreadable files.
   *
  */
    @Test
    public void testCheckReadable() throws Exception {
        final int statusCode = catchSystemExit(() -> {
            PropsWriter.checkReadable(NOREAD);
        });
        assertTrue(mySystemErrRule.getLog().contains("File must be readable"));
        assertEquals(ExitCodes.NOT_READABLE_ERROR, statusCode);
    }

  /**
   * Tests that app catches missing parameters.
   *
  */
    @Test
    public void testVerifyArgs() throws Exception {
        final int statusCode = catchSystemExit(() -> {
            PropsWriter.verifyArgs(new String[] {});
        });
        assertTrue(mySystemErrRule.getLog().contains("usage"));
        assertEquals(ExitCodes.USAGE_ERROR, statusCode);
    }

  /**
   * Tests app catches nonexistent default props file.
   *
  */
    @Test
    public void testLoadProps() throws Exception {
        final int statusCode = catchSystemExit(() -> {
            PropsWriter.loadProps(FAKE);
        });
        assertTrue(mySystemErrRule.getLog().contains("Props file must exist"));
        assertEquals(ExitCodes.DOESNT_EXIST_ERROR, statusCode);
    }

}
