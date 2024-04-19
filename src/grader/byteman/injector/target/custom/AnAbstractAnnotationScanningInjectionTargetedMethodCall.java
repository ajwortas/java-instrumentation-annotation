package grader.byteman.injector.target.custom;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.jboss.byteman.contrib.dtest.RuleConstructor;



/**
 * AnAbstractAnnotationScanningInjectionTarget
 */
public abstract class AnAbstractAnnotationScanningInjectionTargetedMethodCall extends AnAbstractAnnotationScanningInjectionTargeter {

    public AnAbstractAnnotationScanningInjectionTargetedMethodCall() {
        super();
    }

    final public List<String> getRules(){
        List<String> rules = new ArrayList<String>();
        int i = 0;
        for(Method m:findMethodsWithAnnotation()){
            String[] paramNames = new String[m.getParameterCount()];
            int j = 0;
            for(Parameter param : m.getParameters()){
                paramNames[j++] = param.getName();
            }
            rules.add(
                    RuleConstructor.createRule(this.getRuleName() + i++)
                    .onClass(m.getDeclaringClass())
                    .inMethod(m.getName())
                    .helper(this.getHelper())
                    .atInvoke("println")
                    .ifTrue()
                    .doAction("atInvoke($CLASS, $METHOD, $*)")
                    .build()
                );
        }
        return rules;
    }
    
    public boolean isTargetMultiClassed() {
    	return false;
    }
    public List<String> getTargetNames() {
    	return null;
    }
    
    public void atEnter(String clazz, String method, Object[] params) {}
    public void atExit(String clazz, String method, Object[] params) {}
}