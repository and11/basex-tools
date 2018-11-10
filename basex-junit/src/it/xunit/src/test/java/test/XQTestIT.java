package test;

import com.github.and11.basex.utils.Option;
import com.github.and11.basex.utils.test.XQUnitSuiteRunner;
import com.github.and11.basex.utils.test.annotations.Configuration;
import com.github.and11.basex.utils.test.annotations.Suites;
import org.junit.runner.RunWith;

import static com.github.and11.basex.utils.CoreOptions.document;
import static com.github.and11.basex.utils.CoreOptions.function;
import static com.github.and11.basex.utils.CoreOptions.mavenBundle;
import static com.github.and11.basex.utils.CoreOptions.options;
import static com.github.and11.basex.utils.CoreOptions.repository;
import static com.github.and11.basex.utils.CoreOptions.url;

@RunWith(XQUnitSuiteRunner.class)
@Suites({"classpath:test.xq", "classpath:test2.xq", "classpath:test/test3.xq"})
public class XQTestIT {

    @Configuration
    public static Option[] c1() {
        return options(
                repository(mavenBundle().groupId("com.nexign.oapi.spec.tests").artifactId("spec-miner-xqf").version("1.2.0").type("xar")).xar(),
                repository(url("classpath:test.xqm")).xqm()
        );
    }

    @Configuration
    public static Option[] c2() {
        return options(
                document(function("test", "create"))
        );
    }

}
