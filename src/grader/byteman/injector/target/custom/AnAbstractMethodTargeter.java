package grader.byteman.injector.target.custom;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.jboss.byteman.contrib.dtest.RuleConstructor;

import grader.byteman.injector.target.AnAbstractInjectionTarget;

public abstract class AnAbstractMethodTargeter extends AnAbstractInjectionTarget implements AnnotationInjectionTargeter{

	private String clazz, method;
	
	public AnAbstractMethodTargeter(Class<?> clazz, String method) {
		this.clazz = clazz.getName();
		this.method=method;
		this.setTargetClass(clazz);
	}
	
	@Override
	public List<String> getRules() {
		List<String> rules = new ArrayList<String>();
		rules.add(
             RuleConstructor.createRule("Rule ~~ "+clazz+" "+method+" enter")
             .onClass(clazz)
             .inMethod(method)
             .helper(this.getHelper())
             .atEntry()
             .ifTrue()
             .doAction("atEnter($CLASS, $METHOD, $*)")
             .build()
         );
//         rules.add(
//             RuleConstructor.createRule("Rule ~~ "+clazz+" "+method+" exit")
//             .onClass(clazz)
//             .inMethod(method)
//             .helper(this.getHelper())
//             .atExit()
//             .ifTrue()
//             .doAction("atExit($CLASS, $METHOD, $*)")
//             .build()
//         );
		
		
		return rules;
	}

	@Override
	public boolean isTargetClassAnnotated() {
		return false;
	}

	@Override
	public boolean isTargetClassNamed() {
		return true;
	}

	@Override
	public boolean isTargetAnnotated() {
		return false;
	}

	@Override
    public boolean isTargetMultiClassed() {
    	return false;
    }
    
    @Override
    public List<String> getTargetNames() {
    	return null;
    }
	
	@Override
	public Class<? extends Annotation> getTargetMethodAnnotation() {
		return null;
	}

	@Override
	public void atInvoke(String clazz, String method, Object[] params) {}
	
	@Override
    public String getTargetName()
    {
        return "dummyname";
    }
	
}
