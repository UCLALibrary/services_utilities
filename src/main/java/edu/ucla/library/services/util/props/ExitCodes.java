
package edu.ucla.library.services.util.props;

public final class ExitCodes {

    /**
     * Error with class/app usage.
    */
    public static final int USAGE_ERROR = 101;

    /**
     * File doesn't exist.
    */
    public static final int DOESNT_EXIST_ERROR = 102;

    /**
     * File not readable.
    */
    public static final int NOT_READABLE_ERROR = 103;

    /**
     * Other I/O error with file.
    */
    public static final int IO_ERROR = 104;

    /**
     * Private constructor for Constants class.
    */
    private ExitCodes() {
    }

}
