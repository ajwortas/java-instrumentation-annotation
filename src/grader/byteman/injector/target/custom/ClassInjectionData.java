package grader.byteman.injector.target.custom;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class ClassInjectionData {

	 private final String className, injectedCode, displayName;
	 private final Set<MethodInjectionData> targetedMethods;
	 private boolean includeConstructor;
		
	 public ClassInjectionData(String toParse) {
		String [] split = toParse.split(",");
		targetedMethods = new HashSet<>();
		className = split[0];
		includeConstructor = split[1].equals("T");
		for(String method:split[2].split(" ")) {
			addMethod(method);
		}
		injectedCode = split[3];
		displayName = split[4];
	 }
	 
	 public String getInjectedClass() {
		 return injectedCode;
	 }
	 
	 public ClassInjectionData(String className, String injectedClass, Method ... methods) {
		 this(className,injectedClass,className,false,methods);
	 }
	 
	 public ClassInjectionData(String className, String injectedClass, String displayName, Method ... methods) {
		 this(className,injectedClass,displayName,false,methods);
	 }
	 
	 public ClassInjectionData(String className,String injectedClass, boolean incCon,Method ... methods) {
		 this(className, injectedClass, className, incCon, methods);
	 }

	 public ClassInjectionData(String className,String injectedClass, String displayName, boolean incCon,Method ... methods) {
		 this.className=className;
		 this.injectedCode = injectedClass;
		 this.includeConstructor=incCon;
		 this.displayName=displayName;
		 targetedMethods = new HashSet<>();
		 for(Method method:methods) {
			 addMethod(method);
		 }
	 }

	public boolean isIncludeConstructor() {
		return includeConstructor;
	}

	public void setIncludeConstructor(boolean includeConstructor) {
		this.includeConstructor = includeConstructor;
	}

	public boolean addMethod(String method) {
		try {
			return targetedMethods.add(new MethodInjectionData(method));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean addMethod(Set<MethodInjectionData> method) {
		return targetedMethods.addAll(method);
	}
	
	public boolean addMethod(String method, boolean retVoid) {
		return targetedMethods.add(new MethodInjectionData(method,retVoid));
	}
	
	public boolean addMethod(Method method) {
		return targetedMethods.add(new MethodInjectionData(method));
	}
	
	public Set<MethodInjectionData> getTargetedMethods() {
		return targetedMethods;
	}

	public String getClassName() {
		return className;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public String getPrintValue() {
		StringBuilder sb = new StringBuilder();
		sb.append(className);
		sb.append(',');
		sb.append(includeConstructor?'T':'F');
		sb.append(',');
		for(MethodInjectionData method:targetedMethods) {
			sb.append(method.getPrintValue());
			sb.append(' ');
		}
		if(targetedMethods.size()>0) {
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append(',');
		sb.append(injectedCode);
		sb.append(',');
		sb.append(displayName);
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ClassInjectionData)) return false;
		return ((ClassInjectionData)o).className.equals(className);
	}
	
}
