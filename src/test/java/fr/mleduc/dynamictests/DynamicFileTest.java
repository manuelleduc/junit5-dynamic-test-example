package fr.mleduc.dynamictests;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Stream;


public class DynamicFileTest {

    @TestFactory
    Stream<DynamicTest> dynamicTests() {
        final File expectedDir = new File("src/test/resources/test-result");
        // obtained result
        final File resultDir = new File("src/test/resources/test-data");
        final Collection<File> files = FileUtils.listFiles(expectedDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        return files.stream().map(f -> DynamicTest.dynamicTest(f.getName(),
                () -> {
                    final Path relative = expectedDir.toPath().relativize(f.toPath());
                    final String relativePath = relative.toFile().getPath();
                    final File bfile = new File(resultDir, relativePath);
                    if (bfile.exists()) {
                        final Charset charset = Charset.defaultCharset();
                        final String expected = FileUtils.readFileToString(f, charset);
                        final String result = FileUtils.readFileToString(bfile, charset);
                        Assertions.assertEquals(expected, result);
                    } else {
                        Assertions.fail(relativePath + " expected to exist");
                    }
                }));
    }

    @TestFactory
    Stream<DynamicContainer> dynamicContainerStream() {
        return Stream.of(DynamicContainer.dynamicContainer("hello",
                Stream.of(DynamicTest.dynamicTest("world", () -> {
                    // init

                    // stop
                }))));
    }
}
