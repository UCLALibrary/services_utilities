
package edu.ucla.library.services.utility;

import static com.github.stefanbirkner.systemlambda.SystemLambda.*;
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
 * 
 */
public class PropsWriterTest {

    private static final String DEFAULTS = "src/test/resources/properties.default";

    private static final String TEMPLATE = "src/test/resources/properties.tmpl";

    private static final String OUTPUT = "src/test/resources/output.properties";

    private static final String FAKE = "src/test/resources/not.there";
    
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

    /*
     * Test method for PropsWriter:happy path result
     */
    @Test
    public void testBuildProperties() {
        PropsWriter.buildProperties(DEFAULTS, TEMPLATE, OUTPUT);
        assertTrue(Files.exists(FileSystems.getDefault().getPath(OUTPUT)));
    }

    /*
     * Test method for PropsWriter: testing catching nonexistent file
     */
    @Test
    public void testCheckExists() throws Exception {
        int statusCode = catchSystemExit(() -> {
            PropsWriter.checkExists(FAKE);
        });
        assertTrue(systemErrRule.getLog().contains("File must exist"));
        assertEquals(102, statusCode);
    }

    /*
     * Test method for PropsWriter: testing catching unreadable file
     */
    @Test
    public void testCheckReadable() throws Exception {
        int statusCode = catchSystemExit(() -> {
            PropsWriter.checkReadable(FAKE);
        });
        assertTrue(systemErrRule.getLog().contains("File must be readable"));
        assertEquals(103, statusCode);
    }

    /*
     * Test method for PropsWriter: testing sending wrong number of arguments
     */
    @Test
    public void testVerifyArgs() throws Exception {
        int statusCode = catchSystemExit(() -> {
            PropsWriter.verifyArgs(new String[] {});
        });
        assertTrue(systemErrRule.getLog().contains("usage: PropsWriter defaultsFile templateFile propertiesFile"));
        assertEquals(101, statusCode);
    }

    /*
     * Test method for PropsWriter: testing loading props with nonexistent file
     */
    @Test
    public void testLoadProps() throws Exception {
        int statusCode = catchSystemExit(() -> {
            PropsWriter.loadProps(FAKE);
        });
        assertTrue(systemErrRule.getLog().contains("Props file must exist"));
        assertEquals(104, statusCode);
    }

}
