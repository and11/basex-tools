package test;

import com.github.and11.basex.utils.Option;
import com.github.and11.basex.utils.options.ProvisionOption;
import com.github.and11.basex.utils.test.XQUnitSuiteRunner;
import com.github.and11.basex.utils.test.annotations.Configuration;
import com.github.and11.basex.utils.test.annotations.Suites;
import org.junit.runner.RunWith;

import static com.github.and11.basex.utils.CoreOptions.document;
import static com.github.and11.basex.utils.CoreOptions.maven;
import static com.github.and11.basex.utils.CoreOptions.options;
import static com.github.and11.basex.utils.CoreOptions.repository;
import static com.github.and11.basex.utils.CoreOptions.url;

@RunWith(XQUnitSuiteRunner.class)
@Suites({"classpath:test.xq"})
public class XQTestIT {

    @Configuration
    public static Option[] opts() {
        return options(
                repository(url(System.getProperty("artifact"))).xar(),
                document(url("classpath:test-data.xml")).collection("test")

        );
    }


}
