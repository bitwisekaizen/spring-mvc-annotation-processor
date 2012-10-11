package com.thegrayfiles.exception;

import java.io.File;

public class CompilationFailedException extends Exception {

    public CompilationFailedException(File file) {
        super(file.getAbsolutePath() + " failed to compile.");
    }
}
