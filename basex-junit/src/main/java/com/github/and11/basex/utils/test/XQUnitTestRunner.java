package com.github.and11.basex.utils.test;

import com.github.and11.basex.utils.BaseXContainer;
import com.github.and11.basex.utils.options.UrlReference;
import com.github.and11.basex.utils.test.xunit.Testcase;
import com.github.and11.basex.utils.test.xunit.Testsuite;
import com.github.and11.basex.utils.test.xunit.Testsuites;
import com.github.and11.basex.utils.test.xunit.Error;
import org.basex.core.BaseXException;
import org.basex.core.cmd.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class XQUnitTestRunner extends ParentRunner<XQUnitTestRunner.OneTest> {

    public static final Logger logger = LoggerFactory.getLogger(XQUnitTestRunner.class);

    private final BaseXContainer ctx;
    private final String file;

    public XQUnitTestRunner(BaseXContainer ctx, String file) throws InitializationError {
        super(null);
        this.file = file;
        this.ctx = ctx;
    }

    @Override
    public String toString() {
        return "XQUnitTestRunner{" +
                "file=" + file +
                '}';
    }

    @Override
    protected List<OneTest> getChildren() {
        return null;
    }

    @Override
    protected Description describeChild(OneTest child) {
        return null;
    }

    @Override
    public void run(RunNotifier notifier) {

        System.out.println("RUNNING TEST");

//        String suiteName = new File(file.getName()).getName().replaceAll("\\.xq$", "");

        Description suiteDesc = Description.createSuiteDescription(file);

        Test test = new Test(file.toString());

        Path tempfile;
        try {
            tempfile = Files.createTempFile("test", "tmp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {

            OutputStream os = Files.newOutputStream(tempfile);

            try {
                System.out.println("started test " + file);
                ctx.test(file, os);
                System.out.println("completed test " + file);
                //ctx.test(file, os);
                //test.execute(ctx.getContext(), os);
            } catch (Exception e) {
                logger.info("ignored test exception: {}", e);
            }

            os.close();

            byte[] all = Files.readAllBytes(tempfile);

            XUnitParser parser = new XUnitParser();
            Testsuites suites = parser.parse(tempfile);

            byte[] bt = Files.readAllBytes(tempfile);
            logger.debug("test result: {}", new String(bt));

            Testsuite suite = suites.getTestsuite().get(0);

            if(suite.getError() != null){
                throw new RuntimeException(createErrorDescription(suite));
            }

            Result caseResult = new Result();

            RunListener listener = caseResult.createListener();
            notifier.addFirstListener(listener);
            notifier.fireTestRunStarted(suiteDesc);

            for (Testcase testcase : suite.getTestcase()) {
                Description caseDesc = Description.createTestDescription(file, testcase.getName());
                notifier.fireTestStarted(caseDesc);

                if (isSuccessfullTestCase(testcase)) {
                    notifier.fireTestFinished(caseDesc);
                } else if (isFailedTestCase(testcase)) {
                    notifier.fireTestFailure(new Failure(caseDesc, new RuntimeException(createFailureDescription(testcase))));
                } else if (isErrorneousTestCase(testcase)) {
                    notifier.fireTestFailure(new Failure(caseDesc, new RuntimeException(createErrorDescription(testcase.getError()))));
                } else if (isSkippedTestCase(testcase)) {
                    notify("test ignored ", caseDesc);
                    notifier.fireTestIgnored(caseDesc);
                }
            }
            notifier.fireTestRunFinished(new Result());

            Files.delete(tempfile);

        } catch (final BaseXException e) {
            logger.error("test failed: {}", e);
            notifier.fireTestFailure(new Failure(suiteDesc, e));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String createErrorDescription(Testsuite suite) {
        return createErrorDescription(Arrays.asList(suite.getError()));
    }

    private String createErrorDescription(List<Error> errors){
        StringBuilder sb = new StringBuilder();

        for (Error error : errors) {
            sb.append(error.getInfo());
            sb.append(", at line: ");
            sb.append(error.getLine());
            sb.append(", column: ");
            sb.append(error.getColumn());
            sb.append(", uri: ");
            sb.append(error.getUri());
        }
        return sb.toString();
    }

    private String createFailureDescription(Testcase testcase) {
        StringBuilder sb = new StringBuilder();

        for (com.github.and11.basex.utils.test.xunit.Failure  failure : testcase.getFailure()) {
            sb.append(failure.getInfo());
            if(failure.getExpected() != null){
                sb.append("expected: <");
                sb.append(failure.getExpected().getValue());
                sb.append("> of type: ");
                sb.append(failure.getExpected().getType());
                sb.append(", returned: <");
                sb.append(failure.getReturned().getValue());
                sb.append("> of type: ");
                sb.append(failure.getReturned().getType());

            }
            sb.append(", at line: ");
            sb.append(failure.getLine());
            sb.append(", column: ");
            sb.append(failure.getColumn());
        }

        sb.append(" in ");
        sb.append(testcase.getName());
        
        return sb.toString();
    }


    @Override
    protected void runChild(OneTest child, RunNotifier notifier) {
        throw new UnsupportedOperationException();

    }

    private void notify(String msg, Description description){
        System.err.printf("%s %s.%s()\n", msg, description.getClassName(), description.getMethodName());
    }

    private boolean isSuccessfullTestCase(Testcase tcase) {
        return (tcase.getFailure() == null || tcase.getFailure().size() == 0)
                &&
                (tcase.getError() == null || tcase.getError().size() == 0)
                && (tcase.getSkipped() == null);
    }

    private boolean isSkippedTestCase(Testcase tcase) {
        return tcase.getSkipped() != null;
    }

    private boolean isFailedTestCase(Testcase testCaseReport) {
        return testCaseReport.getFailure() != null && testCaseReport.getFailure().size() > 0;
    }


    private boolean isErrorneousTestCase(Testcase testCaseReport) {
        return
               testCaseReport.getError() != null && testCaseReport.getError().size() > 0;
    }

    public static class OneTest {

    }
}
