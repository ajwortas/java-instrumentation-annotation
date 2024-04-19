package grader.byteman.injector.target.custom;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import grader.byteman.injector.AReloadClassLoader;
import grader.byteman.injector.target.AnAbstractInjectionTarget;
import grader.byteman.injector.target.MulticlassTarget;


/**
 * AnAbstractAnnotationScanningInjectionTarget
 */
public abstract class AnAbstractAnnotationScanningInjectionTargeter extends AnAbstractInjectionTarget
    implements AnnotationInjectionTargeter, MulticlassTarget{

    protected String searchPath;
    private List<Class<?>> targetedClasses;

    public AnAbstractAnnotationScanningInjectionTargeter() {
        searchPath = System.getProperty("java.class.path");
        getTargetedClasses();
    }

    abstract public List<String> getRules();

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

	@Override
    public boolean isTargetMultiClassed() {
    	return false;
    }
    
    @Override
    public List<String> getTargetNames() {
    	return null;
    }
    
}