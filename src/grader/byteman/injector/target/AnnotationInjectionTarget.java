package grader.byteman.injector.target;
import java.lang.annotation.Annotation;

public interface AnnotationInjectionTarget extends InjectionTarget{
    public Class<? extends Annotation> getTargetMethodAnnotation();
    public void atInvoke(String clazz, String method, Object[] params);
}