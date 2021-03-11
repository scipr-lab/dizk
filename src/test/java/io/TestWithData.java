package io;

import java.io.InputStream;

/** Base class of tests which use data from the src/tests/resources directory. */
public abstract class TestWithData {
  /** Opens a file from the resources directory as and returns an InputStream. */
  protected InputStream openTestFile(final String filename) {
    return getClass().getClassLoader().getResourceAsStream(filename);
  }
}
