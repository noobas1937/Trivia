package cucumber.runtime;

import com.google.common.collect.Maps;
import com.ecnu.trivia.common.component.exception.Asserts;
import cucumber.runtime.io.Resource;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.java.JavaStepDefinitionEx;
import cucumber.runtime.xstream.LocalizedXStreams;
import gherkin.I18n;
import gherkin.formatter.Argument;
import gherkin.formatter.model.Step;

import java.io.File;
import java.util.*;

public class RuntimeGlueEx extends RuntimeGlue implements Glue {
    private static final String featureStorePosition = "cucumber_atdd";

    final Map<String, Map<String, StepDefinition>> stepDefinitionMap = Maps.newHashMap();

    private UndefinedStepsTracker tracker;
    private final LocalizedXStreams localizedXStreams;

    /**
     * 指定的资源加载符
     */
    private final ResourceLoader resourceLoader;

    public RuntimeGlueEx(ClassLoader classLoader, ResourceLoader resourceLoader) {
        super(null, null);
        this.resourceLoader = resourceLoader;
        this.localizedXStreams = new LocalizedXStreams(classLoader);
    }

    public void setTracker(UndefinedStepsTracker tracker) {
        this.tracker = tracker;
    }

    /**
     * 根据由cucumber提供的featurePath查找相应的定义
     */
    private Map<String, StepDefinition> _getMatchDefinitionMap(String featurePath) {
        featurePath = _shortFeaturePath(featurePath);
        Map<String, StepDefinition> map = stepDefinitionMap.get(featurePath);
        if (map != null)
            return map;

        for (Map.Entry<String, Map<String, StepDefinition>> e : stepDefinitionMap.entrySet()) {
            if (e.getKey().endsWith(featurePath))
                return e.getValue();
        }

        throw new RuntimeException("找不到" + featurePath + "的对应定义");
    }

    /**
     * 使用精简化的位置描述,如xxx/cucumber_atdd/x.feature 则返回 cucumber_atdd/x.feature
     */
    private String _shortFeaturePath(String featurePath) {
        int index = featurePath.lastIndexOf(featureStorePosition);
        return index != -1 ? featurePath.substring(index) : featurePath;
    }

    private Map<String, String> _cacheMap = Maps.newHashMap();

    /**
     * 反向绝对定位feature位置
     */
    private String _getAbsoluteFeaturePath(String featurePath) {
        String path = _cacheMap.get(featurePath);
        if (path != null)
            return path;

        Iterable<Resource> resourceIterable = resourceLoader.resources(featurePath, ".feature");
        Iterator<Resource> resourceIterator = resourceIterable.iterator();
        Asserts.assertTrue(resourceIterator.hasNext(), "资源文件:{}没有被找到", featurePath);

        path = resourceIterator.next().getAbsolutePath();
        path = _shortFeaturePath(path);//重新使用短路径,以统一后续match时进行匹配
        path = path.replace(File.separatorChar, '/');//将\替换为/,以与 featureBuilder时一致

        _cacheMap.put(featurePath, path);

        return path;
    }

    @Override
    public void addStepDefinition(StepDefinition stepDefinition) {
        Asserts.assertTrue(stepDefinition instanceof JavaStepDefinitionEx, "必须是定制的子类型");
        assert stepDefinition instanceof JavaStepDefinitionEx;

        JavaStepDefinitionEx definitionEx = (JavaStepDefinitionEx) stepDefinition;
        String featurePath = definitionEx.getFeaturePath();
        featurePath = _getAbsoluteFeaturePath(featurePath);

        Map<String, StepDefinition> map = stepDefinitionMap.get(featurePath);
        if (map == null) {
            map = Maps.newHashMap();
            stepDefinitionMap.put(featurePath, map);
        }

        StepDefinition previous = map.get(stepDefinition.getPattern());
        if (previous != null) {
            throw new DuplicateStepDefinitionException(previous, stepDefinition);
        }
        map.put(stepDefinition.getPattern(), stepDefinition);
    }

    @Override
    public StepDefinitionMatch stepDefinitionMatch(String featurePath, Step step, I18n i18n) {
        Map<String, StepDefinition> map = _getMatchDefinitionMap(featurePath);
        Collection<StepDefinition> candidateList = map.values();
        List<StepDefinitionMatch> matches = stepDefinitionMatches(featurePath, step, candidateList);
        try {
            if (matches.size() == 0) {
                tracker.addUndefinedStep(step, i18n);
                return null;
            }
            if (matches.size() == 1) {
                return matches.get(0);
            } else {
                throw new AmbiguousStepDefinitionsException(matches);
            }
        } finally {
            tracker.storeStepKeyword(step, i18n);
        }
    }

    /**
     * 从单个feature文件所对应的所有步骤定义中查找合适的
     */
    private List<StepDefinitionMatch> stepDefinitionMatches(String featurePath, Step step, Collection<StepDefinition> candidateList) {
        List<StepDefinitionMatch> result = new ArrayList<>();
        for (StepDefinition stepDefinition : candidateList) {
            //这里虽然是匹配参数,实际上并不单指参数,而是包括步骤名称的相关信息 */
            List<Argument> arguments = stepDefinition.matchedArguments(step);
            if (arguments != null) {
                result.add(new StepDefinitionMatch(arguments, stepDefinition, featurePath, step, localizedXStreams));
            }
        }
        return result;
    }
}
