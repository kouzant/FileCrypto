package gr.kzps.executor;

import gr.kzps.filesystem.EnumeratorTest;
import gr.kzps.filesystem.FileReaderTest;
import gr.kzps.filesystem.FileWriterTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DispatcherTest.class, ParserTest.class, EnumeratorTest.class,
		FileReaderTest.class, FileWriterTest.class })
public class AllUnitTests {
}
