package grader.byteman.injector.target;

import java.util.List;

public interface MulticlassTarget {
    public List<String> getTargetedClassNames();
    public List<Class<?>> getTargetedClasses();
}