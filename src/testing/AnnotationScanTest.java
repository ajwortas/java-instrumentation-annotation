package testing;
import autotracing_annotations.Trace;
import injector.*;
import injector.target.*;
import java.lang.annotation.*;
//import autotracing_annotations.Trace;
public class AnnotationScanTest extends AnAbstractAnnotationScanningInjectionTarget{
    
    public Class<? extends Annotation> getTargetMethodAnnotation(){
        return Trace.class;
    }

    public void atInvoke(String clazz, String method, Object[] params){
    	System.err.print("{"+Thread.currentThread().getName()+"}");
    	System.err.print("Intercepted Call: "+clazz+":"+method);
        System.err.print(" Params: ");
        for(int i=1; i<params.length; i++){
            System.err.print(params[i].toString() + (i==params.length-1?"":","));
        }
        System.err.println();
    }

    public void test(){
        System.out.println("it worked!");
    }

    public boolean isTargetAnnotated()
    {
        return true;
    }
    public boolean isTargetClassAnnotated()
    {
        return true;
    }
    public boolean isTargetClassNamed()
    {
        return false;
    }
    public boolean isField()
    {
        return false;
    }
    public String getName()
    {
        return this.getClass().getName();
    }
    public String getTargetClassName()
    {
        return "dummyname";
    }
    public String getTargetName()
    {
        return "dummyname";
    }
}