/**
 * Created by Jack Chen at 4/25/2015
 */
package cucumber.runtime;

import com.google.common.collect.Lists;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.java.JavaBackendEx;
import cucumber.runtime.java.ObjectFactory;

/**
 * 用于执行单个cucumber feature的测试,参照原main,但使用了自定义的一些配置
 *
 * @author Jack Chen
 * @see cucumber.api.cli.Main
 */
public class Main {
    public static void main(String[] argv) throws Throwable {
        RuntimeOptions runtimeOptions = new RuntimeOptions(Lists.newArrayList(argv));

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        ObjectFactory objectFactory = new SpringFactory();
        objectFactory.start();

        JavaBackendEx javaBackendEx = new JavaBackendEx(objectFactory, classFinder);
        RuntimeGlueEx runtimeGlueEx = new RuntimeGlueEx(classLoader, resourceLoader);

        Runtime runtime = new Runtime(resourceLoader, classLoader, Lists.newArrayList(javaBackendEx), runtimeOptions, runtimeGlueEx);
        runtimeGlueEx.setTracker(runtime.undefinedStepsTracker);

        runtime.run();
        int exitStatus = runtime.exitStatus();
        System.exit(exitStatus);
    }

}
