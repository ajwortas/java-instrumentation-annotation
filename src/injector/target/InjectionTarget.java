package injector.target;

import java.lang.annotation.Annotation;
import java.util.List;


public interface InjectionTarget extends Injection
{
    Class<?> getTargetClass();
    Annotation getTargetClassAnnotation();
    String getTargetClassName();
    Class<?> getHelper();
    String getRuleName();
    List<String> getRules();
    Annotation getTargetAnnotation();
    String getTargetName();
    boolean isTargetClassAnnotated();
    boolean isTargetClassNamed();
    boolean isTargetAnnotated();
    Class<?> setTargetClass(Class<?> targetClass);
    String setTargetName(String targetName);
}
