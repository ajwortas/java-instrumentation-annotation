package grader.byteman.injector.target;

import java.lang.annotation.Annotation;
import java.util.List;



public interface InjectionTarget extends Injection
{
    Class<?> getTargetClass();
    Class<? extends Annotation> getTargetClassAnnotation();
    String getTargetClassName();
    Class<?> getHelper();
    String getRuleName();
    List<String> getRules();
    Class<? extends Annotation> getTargetAnnotation();
    String getTargetName();
    boolean isTargetClassAnnotated();
    boolean isTargetClassNamed();
    boolean isTargetAnnotated();
    boolean isTargetMultiClassed();
    List<String> getTargetNames();    
    Class<?> setTargetClass(Class<?> targetClass);
    String setTargetName(String targetName);
}
