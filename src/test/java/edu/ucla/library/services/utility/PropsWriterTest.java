
package edu.ucla.library.services.utility;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.contrib.java.lang.system.SystemErrRule;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

/*
 * Unit tests for ProsWriter.java
 */
public class PropsWriterTest {

  /**
   * default properties file.
  */
  private static final String DEFAULTS =
    "src/test/resources/properties.default";

  /**
   * output template file.
  */
  private static final String TEMPLATE =
    "src/test/resources/properties.tmpl";

  /**
   * final output file.
  */
  private static final String OUTPUT =
    "src/test/resources/output.properties";

  /**
   * fake file name used to cause app failures.
  */
  private static final String FAKE =
    "src/test/resources/not.there";

  /**
   * rule that allows catching System.err output in tests.
  */
  @Rule
  public final SystemErrRule systemErrRule = new SystemErrRule().enableLog();

  /**
   * Sets up a PropsWriter test.
   *
   * @throws IOException If there is a problem deleting the file
  */
  @Before
  public void setUp() throws IOException {
    // delete the output file if lest over from previous run
    Files.deleteIfExists(FileSystems.getDefault().getPath(OUTPUT));
  }

  /**
   * Tests happy path for app execution.
   *
  */
  @Test
  public void testBuildProperties() {
    PropsWriter.buildProperties(DEFAULTS, TEMPLATE, OUTPUT);
    assertTrue(Files.exists(FileSystems.getDefault().getPath(OUTPUT)));
  }

  /**
   * Tests that app catches nonexistent files.
   *
  */
  @Test
  public void testCheckExists() throws Exception {
    int statusCode = catchSystemExit(() -> {
      PropsWriter.checkExists(FAKE);
    });
    assertTrue(systemErrRule.getLog().contains("File must exist"));
    assertEquals(Constants.DOESNT_EXIST_ERROR, statusCode);
  }

  /**
   * Tests that app catches nonreadable files.
   *
  */
  @Test
  public void testCheckReadable() throws Exception {
    int statusCode = catchSystemExit(() -> {
      PropsWriter.checkReadable(FAKE);
    });
    assertTrue(systemErrRule.getLog().contains("File must be readable"));
    assertEquals(Constants.NOT_READABLE_ERROR, statusCode);
  }

  /**
   * Tests that app catches missing parameters.
   *
  */
  @Test
  public void testVerifyArgs() throws Exception {
    int statusCode = catchSystemExit(() -> {
      PropsWriter.verifyArgs(new String[] {});
    });
    assertTrue(systemErrRule.getLog().contains("usage"));
    assertEquals(Constants.USAGE_ERROR, statusCode);
  }

  /**
   * Tests app catches nonexistent default props file.
   *
  */
  @Test
  public void testLoadProps() throws Exception {
    int statusCode = catchSystemExit(() -> {
      PropsWriter.loadProps(FAKE);
    });
    assertTrue(systemErrRule.getLog().contains("Props file must exist"));
    assertEquals(Constants.DOESNT_EXIST_ERROR, statusCode);
  }

}
