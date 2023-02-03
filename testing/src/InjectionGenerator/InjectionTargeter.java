package InjectionGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import grader.byteman.injector.target.custom.ClassInjectionData;
import grader.byteman.injector.target.custom.EnterExitInjectionSite;

public class InjectionTargeter {
	private Map<ClassInjectionData,ClassInjectionData> injectionData;
	private File injections;
	
	public InjectionTargeter() throws IOException {
		injections = new File("./injectionTarget.txt");
		injectionData = new HashMap<>();
	}
	
	/**
	 * Adds all declared methods within the class
	 * @param clazz
	 * @return
	 */
	public boolean addClasses(Class<? extends EnterExitInjectionSite> injectedCode,Class<?> ... clazzez) {
		for(Class<?> clazz:clazzez) 
			if(!addMethods(injectedCode,clazz,clazz.getDeclaredMethods())) return false;
		return true;
	}
	
	/**
	 * Adds methods to be injected to
	 * @param methods
	 * @return
	 * @throws IOException 
	 */
	public boolean addMethods(Class<? extends EnterExitInjectionSite> injectedCode, Method ... methods) {
		for(Method method:methods) 
			if(!addMethods(injectedCode,method.getDeclaringClass(),method)) return false;
		return true;
	}
	
	/**
	 * Adds methods to be injected to
	 * @param methods
	 * @return
	 * @throws IOException 
	 */
	public boolean addMethods(Class<? extends EnterExitInjectionSite> injectedCode, Class<?> clazz, Method ... methodNames) {
		ClassInjectionData classData = new ClassInjectionData(clazz.getName(),injectedCode.getName(),true,methodNames);
		if(injectionData.containsKey(classData)) {
			injectionData.get(classData).addMethod(classData.getTargetedMethods());
		}else {
			injectionData.put(classData,classData);
		}
		return true;
	}
	
	/**
	 * Adds methods to be injected to
	 * @param methods
	 * @return
	 * @throws IOException 
	 */
	public boolean addMethods(Class<? extends EnterExitInjectionSite> injectedCode, Class<?> clazz, String ... methodNames) {
		return addMethods(injectedCode,clazz,findMethods(clazz.getMethods(),methodNames));
	}
	
	/**
	 * Adds all methods declared in a class except for the ones listed
	 * @param methods
	 * @return
	 */
	public boolean excludeMethods(Class<? extends EnterExitInjectionSite> injectedCode, Class<?> clazz, String ... methods) {
		List<Method> included = new ArrayList<>();
		for(Method method:clazz.getDeclaredMethods())
			if(!contains(method.getName(), methods))
				included.add(method);
		return addMethods(injectedCode,included.toArray(new Method[included.size()]));		
	}
	
	private Method[] findMethods(Method [] methods, String [] names) {
		List<Method> retval = new ArrayList<>();
		List<String> nameArr = new ArrayList<>();
		nameArr.addAll(Arrays.asList(names));
		for(Method method:methods) 
			for(int i=0;i<nameArr.size();i++) 
				if(method.getName().equals(nameArr.get(i))) {
					retval.add(method);
					nameArr.remove(i);
					break;
				}
		return retval.toArray(new Method[retval.size()]);
	}
	
	/**
	 * Ends and closes the file writing, must be done before process builder
	 * @return
	 * @throws IOException
	 */
	public boolean write() throws IOException {
		injections.createNewFile();
		FileWriter write = new FileWriter(injections);
		for(ClassInjectionData classes:injectionData.keySet()) {
			write.append(classes.getPrintValue());
			write.append('\n');
		}
		
		write.close();
		return true;
	}
	
	/**
	 * Deletes the file created
	 * @return
	 */
	public boolean delete() {
		return injections.delete();
	}
	
	private boolean contains(Object val, Object [] arr) {
		for(Object arrVal:arr)
			if(val.equals(arrVal)) return true;
		return false;
	}
}
