/** Created by Jack Chen at 4/13/2015 */
package cucumber.runtime;

import com.google.common.collect.Lists;
import cucumber.api.CucumberOptions;
import cucumber.api.testng.TestNgReporter;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import cucumber.runtime.java.JavaBackendEx;
import cucumber.runtime.java.ObjectFactory;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * 用于支持cucumber与testNG连通
 *
 * @author Jack Chen
 */
@CucumberOptions(features = "classpath:cucumber_atdd")
public class CucumberTest implements IHookable {
    @Test(groups = "cucumber")
    public void runCucumber() throws IOException {
        ClassLoader classLoader = CucumberTest.class.getClassLoader();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);

        //使用指定的配置运行cucumber
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(CucumberTest.class);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();
        TestNgReporter reporter = new TestNgReporter(System.out);
        runtimeOptions.addPlugin(reporter);

        //自定义相应处理器
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        ObjectFactory objectFactory = new SpringFactory();
        objectFactory.start();
        JavaBackendEx javaBackendEx = new JavaBackendEx(objectFactory, classFinder);
        RuntimeGlueEx runtimeGlueEx = new RuntimeGlueEx(classLoader, resourceLoader);

        Runtime runtime = new Runtime(resourceLoader, classLoader, Lists.newArrayList(javaBackendEx), runtimeOptions, runtimeGlueEx);
        runtimeGlueEx.setTracker(runtime.undefinedStepsTracker);

        try{
            runtime.run();
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
        if(!runtime.getErrors().isEmpty()) {
            throw new CucumberException(runtime.getErrors().get(0));
        } else if(runtime.exitStatus() != 0x00) {
            throw new CucumberException("There are pending or undefined steps.");
        }
    }

    @Override
    public void run(IHookCallBack iHookCallBack, ITestResult iTestResult) {
        iHookCallBack.runTestMethod(iTestResult);
    }
}
