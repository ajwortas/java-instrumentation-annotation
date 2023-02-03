package grader.byteman.injector.target.custom;

import java.util.ArrayList;
import java.util.List;

import org.jboss.byteman.contrib.dtest.RuleConstructor;

import grader.byteman.injector.target.AnAbstractInjectionTarget;

public class FileSourceInjectionTargeter extends AnAbstractInjectionTarget {

	private ClassInjectionData injectionTarget;
    private Class<? extends EnterExitInjectionSite> helper;
    
	public FileSourceInjectionTargeter(ClassInjectionData target) throws ClassNotFoundException {
	        super();	        
	        this.injectionTarget=target;
	        helper = Class.forName(target.getInjectedClass()).asSubclass(EnterExitInjectionSite.class);
	}
	
	@Override
    public Class<?> getHelper(){
        return helper;
    }

	final public List<String> getRules(){
	    List<String> rules = new ArrayList<String>();
	    int i = 0;
	    if(injectionTarget.isIncludeConstructor()) {
	    	rules.add(
		             RuleConstructor.createRule(this.getRuleName()+"~~"+injectionTarget+":constructor")
		             .onClass(injectionTarget.getClassName())
		             .inMethod("<init>")
		             .helper(this.getHelper())
		             .atEntry()
		             .ifTrue()
		             .doAction("atEnter($CLASS, $METHOD, $*)")
		             .build()
		         );
	    }
	    
	    for(MethodInjectionData method:injectionTarget.getTargetedMethods()) {
	    	String methodName = method.getMethodName();
	    	boolean returnsVoid = method.returnsVoid();
	         rules.add(
	             RuleConstructor.createRule(this.getRuleName()+i+"~~"+injectionTarget+":"+methodName+"-enter")
	             .onClass(injectionTarget.getClassName())
	             .inMethod(methodName)
	             .helper(this.getHelper())
	             .atEntry()
	             .ifTrue()
	             .doAction("atEnter($CLASS, $METHOD, $*)")
	             .build()
	         );
	         rules.add(
                 RuleConstructor.createRule(this.getRuleName()+i+"~~"+injectionTarget+":"+methodName+"-exit")
                 .onClass(injectionTarget.getClassName())
                 .inMethod(methodName)
                 .helper(this.getHelper())
                 .atExit()
                 .ifTrue()
                 .doAction("atExit($CLASS, $METHOD" + (returnsVoid ? "" : ", $!")+")")
                 .build()
             );
	         i++;
	    }
	    return rules;
	 }

	@Override
    public boolean isTargetMultiClassed() {
    	return true;
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
	 public String getRuleName(){
		return "Rule-"+this.getClass().getSimpleName() +"-";
    }

	@Override
	public String getTargetName() {
		return this.injectionTarget.getClassName();
	}

	@Override
	public List<String> getTargetNames() {
		List<String> retval = new ArrayList<String>();
		retval.add(injectionTarget.getClassName());
		return retval;
	}
	
}
