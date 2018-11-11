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
    private final BaseXContainer container;

    @Override
    public String toString() {
        return "XQUnitSuiteRunner{" +
                "description=" + description +
                ", children=" + children +
                '}';
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
        try {
            options.addAll(getConfigurationOptions(testClass));
        } catch (Exception e) {
            throw new InitializationError(e);
        }

        description = Description.createSuiteDescription(testClass.getCanonicalName());

        BaseXContainer container;
        try {
            container = new DefaultBaseXContainersFactory(testClass.getClassLoader()).createContainer(options.toArray(new Option[options.size()]));

        } catch (BaseXContainer.BaseXContainerException e) {
            throw new RuntimeException(e);
        }

        String[] testsList = getTests(testClass);

        for (String testPath : testsList) {
            XQUnitTestRunner runner = new XQUnitTestRunner(container, testPath);
            children.add(runner);
        }

        this.container = container;
    }


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
        if(container != null){
            container.close();
        }
    }
}
