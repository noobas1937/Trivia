package cucumber.runtime.java;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java8.GlueBase;
import cucumber.api.java8.HookBody;
import cucumber.api.java8.HookNoArgsBody;
import cucumber.api.java8.StepdefBody;
import cucumber.runtime.*;
import cucumber.runtime.snippets.FunctionNameGenerator;
import cucumber.runtime.snippets.SnippetGenerator;
import gherkin.formatter.model.Step;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static cucumber.runtime.io.MultiLoader.packageName;

public class JavaBackendEx extends JavaBackend implements Backend {
    public static final ThreadLocal<JavaBackendEx> INSTANCE = new ThreadLocal<>();
    private SnippetGenerator snippetGenerator = new SnippetGenerator(new JavaSnippet());
    private final ObjectFactory objectFactory;
    private final ClassFinder classFinder;

    private final MethodScanner methodScanner;
    private Glue glue;
    private List<Class<? extends GlueBase>> glueBaseClasses = new ArrayList<>();

    public JavaBackendEx(ObjectFactory objectFactory, ClassFinder classFinder) {
        super(objectFactory, classFinder);

        this.objectFactory = objectFactory;
        this.classFinder = classFinder;
        methodScanner = new MethodScanner(classFinder);
    }

    /**
     * 从父类直接copy
     */
    @Override
    public void loadGlue(Glue glue, List<String> gluePaths) {
        this.glue = glue;
        // Scan for Java7 style glue (annotated methods)
        methodScanner.scan(this, gluePaths);

        // Scan for Java8 style glue (lambdas)
        for (final String gluePath : gluePaths) {
            Collection<Class<? extends GlueBase>> glueDefinerClasses = classFinder.getDescendants(GlueBase.class, packageName(gluePath));
            for (final Class<? extends GlueBase> glueClass : glueDefinerClasses) {
                if (glueClass.isInterface()) {
                    continue;
                }

                objectFactory.addClass(glueClass);
                glueBaseClasses.add(glueClass);
            }
        }
    }

    /**
     * 从父类直接copy
     */
    @Override
    public void loadGlue(Glue glue, Method method, Class<?> glueCodeClass) {
        this.glue = glue;
        methodScanner.scan(this, method, glueCodeClass);
    }

    /**
     * 从父类直接copy
     */
    @Override
    public void setUnreportedStepExecutor(UnreportedStepExecutor executor) {
        //Not used here yet
    }

    /**
     * 从父类直接copy
     */
    @Override
    public void buildWorld() {
        objectFactory.start();

        // Instantiate all the stepdef classes for java8 - the stepdef will be initialised
        // in the constructor.
        try {
            INSTANCE.set(this);
            glue.removeScenarioScopedGlue();
            for (Class<? extends GlueBase> glueBaseClass : glueBaseClasses) {
                objectFactory.getInstance(glueBaseClass);
            }
        } finally {
            INSTANCE.remove();
        }
    }

    /**
     * 从父类直接copy
     */
    @Override
    public void disposeWorld() {
        objectFactory.stop();
    }

    /**
     * 从父类直接copy
     */
    @Override
    public String getSnippet(Step step, FunctionNameGenerator functionNameGenerator) {
        return snippetGenerator.getSnippet(step, functionNameGenerator);
    }

    /**
     * 从父类直接copy
     */
    @Override
    public void addStepDefinition(String regexp, long timeoutMillis, StepdefBody body, TypeIntrospector typeIntrospector) {
        try {
            glue.addStepDefinition(new Java8StepDefinition(Pattern.compile(regexp), timeoutMillis, body, typeIntrospector));
        } catch (Exception e) {
            throw new CucumberException(e);
        }
    }

    /**
     * 从父类直接copy
     */
    @Override
    void addHook(Annotation annotation, Method method) {
        objectFactory.addClass(method.getDeclaringClass());

        if (annotation.annotationType().equals(Before.class)) {
            String[] tagExpressions = ((Before) annotation).value();
            long timeout = ((Before) annotation).timeout();
            glue.addBeforeHook(new JavaHookDefinition(method, tagExpressions, ((Before) annotation).order(), timeout, objectFactory));
        } else {
            String[] tagExpressions = ((After) annotation).value();
            long timeout = ((After) annotation).timeout();
            glue.addAfterHook(new JavaHookDefinition(method, tagExpressions, ((After) annotation).order(), timeout, objectFactory));
        }
    }

    /**
     * 从父类直接copy
     */
    @Override
    public void addBeforeHookDefinition(String[] tagExpressions, long timeoutMillis, int order, HookBody body) {
        glue.addBeforeHook(new Java8HookDefinition(tagExpressions, order, timeoutMillis, body));
    }

    /**
     * 从父类直接copy
     */
    @Override
    public void addAfterHookDefinition(String[] tagExpressions, long timeoutMillis, int order, HookBody body) {
        glue.addAfterHook(new Java8HookDefinition(tagExpressions, order, timeoutMillis, body));
    }

    /**
     * 从父类直接copy
     */
    @Override
    public void addBeforeHookDefinition(String[] tagExpressions, long timeoutMillis, int order, HookNoArgsBody body) {
        glue.addBeforeHook(new Java8HookDefinition(tagExpressions, order, timeoutMillis, body));
    }

    /**
     * 从父类直接copy
     */
    @Override
    public void addAfterHookDefinition(String[] tagExpressions, long timeoutMillis, int order, HookNoArgsBody body) {
        glue.addAfterHook(new Java8HookDefinition(tagExpressions, order, timeoutMillis, body));
    }

    /**
     * 使用自定义的步骤定义替换原定义
     */
    @Override
    void addStepDefinition(Annotation annotation, Method method) {
        try {
            objectFactory.addClass(method.getDeclaringClass());
            glue.addStepDefinition(new JavaStepDefinitionEx(method, pattern(annotation), timeoutMillis(annotation), objectFactory));
        } catch (DuplicateStepDefinitionException e) {
            throw e;
        } catch (Throwable e) {
            throw new CucumberException(e);
        }
    }

    private Pattern pattern(Annotation annotation) throws Throwable {
        Method regexpMethod = annotation.getClass().getMethod("value");
        String regexpString = (String) Utils.invoke(annotation, regexpMethod, 0);
        return Pattern.compile(regexpString);
    }

    private long timeoutMillis(Annotation annotation) throws Throwable {
        Method regexpMethod = annotation.getClass().getMethod("timeout");
        return (Long) Utils.invoke(annotation, regexpMethod, 0);
    }
}
