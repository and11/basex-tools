package test;

import com.github.and11.basex.utils.Option;
import com.github.and11.basex.utils.test.XQUnitSuiteRunner;
import com.github.and11.basex.utils.test.annotations.Configuration;
import com.github.and11.basex.utils.test.annotations.Suites;
import org.junit.runner.RunWith;

import java.io.File;

import static com.github.and11.basex.utils.CoreOptions.document;
import static com.github.and11.basex.utils.CoreOptions.function;
import static com.github.and11.basex.utils.CoreOptions.mavenBundle;
import static com.github.and11.basex.utils.CoreOptions.options;
import static com.github.and11.basex.utils.CoreOptions.repository;
import static com.github.and11.basex.utils.CoreOptions.url;
import static com.github.and11.basex.utils.CoreOptions.workingDirectory;
import static com.github.and11.basex.utils.CoreOptions.createDatabase;

@RunWith(XQUnitSuiteRunner.class)
@Suites({"classpath:test.xq"})
public class XQTestIT {

    @Configuration
    public static Option[] c1() {
        return options(
                repository(url("classpath:test.xqm")).xqm(),
                createDatabase(),
                document(url("classpath:test.xml")).collection("test")
        );
    }


}
