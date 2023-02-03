package grader.byteman.injector.target;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jboss.byteman.contrib.dtest.RuleConstructor;

import grader.byteman.injector.AReloadClassLoader;


/**
 * AnAbstractAnnotationScanningInjectionTarget
 */
public abstract class AnAbstractAnnotationScanningInjectionTarget extends AnAbstractInjectionTarget
    implements AnnotationInjectionTarget, MulticlassTarget{

    protected String searchPath;
    private List<Class<?>> targetedClasses;

    public static final boolean allPaths=true;
    
    public AnAbstractAnnotationScanningInjectionTarget() {
        searchPath = System.getProperty(allPaths?"java.class.path":"user.dir");
        //TODO adjust this for just src folder
        
        getTargetedClasses();
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
                .atEntry()
                .ifTrue()
                .doAction("atEnter($CLASS, $METHOD, $*)")
                .build()
            );
            rules.add(
                    RuleConstructor.createRule(this.getRuleName() + i++)
                    .onClass(m.getDeclaringClass())
                    .inMethod(m.getName())
                    .helper(this.getHelper())
                    .atExit()
                    .ifTrue()
                    .doAction("atExit($CLASS, $METHOD, $*)")
                    .build()
                );
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

    private String getPath(File f) {
        try {
            return f.getCanonicalPath();
        } catch (Exception e) {
            return f.getAbsolutePath();
        }
    }

    public List<Method> findMethodsWithAnnotation(){
        Class<? extends Annotation> targetAnnotation = this.getTargetMethodAnnotation();
        List<Method> annotatedMethods = new ArrayList<Method>();
        for(Class<?> clazz:targetedClasses){
            for(Method m : clazz.getDeclaredMethods()){
                if(m.isAnnotationPresent(targetAnnotation)){
                    annotatedMethods.add(m);
                }
            }
        }
        
        return annotatedMethods;
    }

	@Override
    public boolean isTargetMultiClassed() {
    	return false;
    }
    
    @Override
    public List<String> getTargetNames() {
    	return null;
    }
    
    //TODO make classes selective (avoid some regexs)
    //ArrList of full class name and loaded class
    //TODO look at student project
    //look for predefined threads, and other java classes 
    public List<Class<?>> getTargetedClasses() {
        if(targetedClasses != null){
            return targetedClasses;
        }
        targetedClasses = new ArrayList<Class<?>>();
        LinkedList<File> files = new LinkedList<File>();
        Set<String> roots = new HashSet<String>();
        targetedClasses = new ArrayList<Class<?>>();
        for (String path : searchPath.split(System.getProperty("path.separator"))) {
            File file = new File(path);
            if (file.exists()) {
                files.push(file);
                roots.add(getPath(file));
                System.out.println(file.getName());
            }
        }

        while (files.size() > 0) {
            File file = files.pop();
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    files.push(child);
                }
            } else if (file.getName().toLowerCase().endsWith(".class")) {
            	
                StringBuffer sb = new StringBuffer();
                String fileName = file.getName();
                sb.append(fileName.substring(0, fileName.lastIndexOf(".class")));
                File parent = file.getParentFile();
                while (parent != null && !roots.contains(getPath(parent))) {
                    sb.insert(0, '.').insert(0, parent.getName());
                    parent = parent.getParentFile();
                }
                try {
                    targetedClasses.add(Class.forName(sb.toString(), false, new AReloadClassLoader(this.getClass().getClassLoader())));
                } catch (ClassNotFoundException e) {
                    System.err.println("Could not find class for classpath entry: " + sb.toString());
                }
            }
        }
        return targetedClasses;
    }

    public List<String> getTargetedClassNames(){
        List<String> targetedClassNames = new ArrayList<String>();
        List<Class<?>> classes = getTargetedClasses();
        for(int i=0;i<classes.size();i++){
            targetedClassNames.add(classes.get(i).getName());
        }
        return targetedClassNames;
    }

}