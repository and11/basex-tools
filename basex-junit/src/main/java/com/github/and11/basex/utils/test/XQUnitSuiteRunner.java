package com.github.and11.basex.utils.test;

import com.github.and11.basex.utils.BaseXContainer;
import com.github.and11.basex.utils.Option;
import com.github.and11.basex.utils.container.DefaultBaseXContainersFactory;
import com.github.and11.basex.utils.test.annotations.Configuration;
import com.github.and11.basex.utils.test.annotations.Suites;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;


public class XQUnitSuiteRunner extends ParentRunner<XQUnitTestRunner> implements AutoCloseable {

    public static final Logger logger = LoggerFactory.getLogger(XQUnitSuiteRunner.class);

    private final Description description;

    @Override
    public String toString() {
        return "XQUnitSuiteRunner{" +
                "description=" + description +
                ", children=" + children +
                '}';
    }

//    private Optional<String> getSuiteName(Class<?> clazz) {
//        Annotation[] annotations = clazz.getAnnotations();
//        for (Annotation annotation : annotations) {
//            if (annotation instanceof XQUnitSuiteName) {
//                XQUnitSuiteName sn = (XQUnitSuiteName) annotation;
//                return Optional.of(sn.name());
//            }
//        }
//
//        return Optional.empty();
//    }

    private void printClasspath(ClassLoader cl) {
        URL[] urls = ((URLClassLoader) cl).getURLs();

        for (URL url : urls) {
            System.out.println("clpath: " + url.getFile());
        }
    }

    private List<Option> getConfigurationOptions(Class<?> testClass) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        final Object testInstance = testClass.newInstance();
        Method[] methods = testClass.getDeclaredMethods();
        List<Option> options = new ArrayList<>();
        for (Method configMethod : methods) {
            Configuration annotation = configMethod.getAnnotation(Configuration.class);
            if (
                    annotation != null
                            && !Modifier.isAbstract(configMethod.getModifiers())
                            && Modifier.isStatic(configMethod.getModifiers())
                            && configMethod.getParameterTypes() != null
                            && configMethod.getParameterTypes().length == 0
                            && configMethod.getReturnType().isArray()
                            && Option.class.isAssignableFrom(configMethod.getReturnType().getComponentType())) {
                System.out.println("found config method: " + configMethod);
                options.addAll(Arrays.asList((Option[]) configMethod.invoke(testInstance, null)));
            }
        }

        return options;
    }

    private String[] getTests(Class<?> testClass){
        Suites suites = testClass.getAnnotation(Suites.class);
        return suites.value();
    }


    public XQUnitSuiteRunner(Class<?> testClass) throws InitializationError {
        super(null);

        List<Option> options = new ArrayList<>();
//        Optional<String> suiteName = getSuiteName(testClass);
        try {
            options.addAll(getConfigurationOptions(testClass));
            System.out.println("options: ");
            for (Option option : options) {
                System.out.println("op: " + option);
            }
        } catch (Exception e) {
            throw new InitializationError(e);
        }

        //System.out.println("class = " + testClass + ", resource: " + testClass.getResource("/mytest.txt"));

//        URL suitesDesc = testClass.getClassLoader().getResource(resourceName);
//
//        if(suitesDesc == null){
//            logger.error("can't load resource {}", resourceName);
//            throw new InitializationError("can't find testsuite descriptor");
//        }
//
//        Properties props = new Properties();
//        try (InputStream is = suitesDesc.openStream()) {
//            props.load(is);
//        } catch (IOException e) {
//            logger.error("can't load properties by URL {}: {}", suitesDesc, e);
//            throw new RuntimeException("can't read properties", e);
//        }
//
//        String dbName = props.getProperty("dbname");
//        String dbPath = props.getProperty("dbpath");
//        String tsuiteName = props.getProperty("suitename");

        description = Description.createSuiteDescription(testClass.getCanonicalName());

        BaseXContainer container;
        try {
            container = new DefaultBaseXContainersFactory(testClass.getClassLoader()).createContainer(options.toArray(new Option[options.size()]));

        } catch (BaseXContainer.BaseXContainerException e) {
            throw new RuntimeException(e);
        }
//
//        String tests = props.getProperty("tests");
//        BasexContext ctx = BasexCachedContexts.openDatabase(new File(dbPath).toPath(), dbName);
//        this.ctx = ctx;
//        String[] testsList = tests.split(",");

        String[] testsList = getTests(testClass);
        System.out.println("tests: " + Arrays.asList(testsList));

        for (String testPath : testsList) {
            System.out.println("testPath: " + testPath);
            XQUnitTestRunner runner = new XQUnitTestRunner(container, testPath);
            logger.info("registering tests runner: {}", runner);
            children.add(runner);
        }
//
    }

//    public XQUnitSuiteRunner(Properties props, BasexContext ctx) throws InitializationError {
//        super(null);
//
//        System.out.println("BBBBBBBBBBBBBBBBBBB");
//        String suiteName = props.getProperty("suitename");
//        description = Description.createSuiteDescription(suiteName);
//
//        String tests = props.getProperty("tests");
//
//        //System.out.println("initizlized XQUnitSuiteRunner, props: " + props);
//        //this.ctx = ctx;
//
//        String[] testsList = tests.split(",");
//
////        for (String testPath : testsList) {
////            XQUnitTestRunner runner = new XQUnitTestRunner(this.ctx, new File(testPath));
////            logger.info("registering tests runner: {}", runner);
////            children.add(runner);
////        }
//
//    }

    private List<XQUnitTestRunner> children = new ArrayList<>();

    @Override
    public void run(RunNotifier notifier) {
        super.run(notifier);
    }

    @Override
    protected List<XQUnitTestRunner> getChildren() {
        return children;
    }

    @Override
    protected Description describeChild(XQUnitTestRunner child) {
        return Description.createSuiteDescription("childsuite");
    }

    @Override
    protected void runChild(XQUnitTestRunner child, RunNotifier notifier) {
        child.run(notifier);
    }

    public Description getDescription() {
        return description;
    }

    @Override
    public void close() throws Exception {
        //ctx.close();
    }
}
