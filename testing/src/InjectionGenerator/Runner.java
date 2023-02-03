package InjectionGenerator;

import java.io.File;
import java.util.ArrayList;

import sorting.BubbleSort;
import sorting.MergeSort;


public class Runner {

	
	//C:\Program Files\Java\jdk-11.0.12\bin\javaw.exe -javaagent:agent.jar -Dfile.encoding=Cp1252 -classpath "I:\Research\Outside_Projects\java-instrumentation-annotation\bin;I:\Research\Outside_Projects\java-instrumentation-annotation\autotracing;I:\Research\Outside_Projects\java-instrumentation-annotation\autotracing\Comp533All.jar;I:\Research\Outside_Projects\java-instrumentation-annotation\byteman-4.0.7\contrib\bmunit\byteman-bmunit.jar;I:\Research\Outside_Projects\java-instrumentation-annotation\byteman-4.0.7\contrib\bmunit\byteman-bmunit5.jar;I:\Research\Outside_Projects\java-instrumentation-annotation\byteman-4.0.7\contrib\dtest\byteman-dtest.jar;I:\Research\Outside_Projects\java-instrumentation-annotation\byteman-4.0.7\contrib\jboss-modules-system\byteman-jboss-modules-plugin.jar;I:\Research\Outside_Projects\java-instrumentation-annotation\byteman-4.0.7\lib\byteman-install.jar;I:\Research\Outside_Projects\java-instrumentation-annotation\byteman-4.0.7\lib\byteman-submit.jar;I:\Research\Outside_Projects\java-instrumentation-annotation\byteman-4.0.7\lib\byteman.jar;I:\Research\Outside_Projects\java-instrumentation-annotation\byteman-4.0.7\sample\lib\byteman-sample.jar;I:\Research\Outside_Projects\java-instrumentation-annotation\autotracing\autotracing_annotations.jar" testing.Adder
	
	/*
	 * TODO
	 * Run it with basic checks for good ui
	 * write some checks for it
	 * examples with multiple packages (structure)
	 * parallel sort?
	 * Try to make custom classes for utils
	 * read xml file easily
	 */
	
	/*
	 * Methods gained from inheritance still subject to class loading issues
	 * 
	 * 
	the follow cannot be injected
	
	import java.io.File;
	import java.lang.reflect.Method;
	import java.lang.reflect.Parameter;
	import java.io.DataInputStream;
	import java.io.FileInputStream;
	import java.io.IOException;
	import java.util.regex.Matcher;
	import java.util.regex.Pattern;
	import java.lang.reflect.Array;
	ClassNotFoundException
	Class
	String
	Object
	Exception
	print / err out statements
	
	 * 
	 */
	
	public static void main(String [] args) {
		try {
			InjectionTargeter targets = new InjectionTargeter();
			targets.addClasses(InjectedCode.class,MergeSort.class,BubbleSort.class);
			targets.addMethods(InjectedCode.class, ArrayList.class, "add");
			targets.write();			
			//ProcessBuilder pb = new ProcessBuilder("java -javaagent:./Logs/LocalChecks/agent.jar -Dfile.encoding=Cp1252 -classpath \"I:\\Research\\Outside_Projects\\java-instrumentation-annotation\\bin;I:\\Research\\Outside_Projects\\java-instrumentation-annotation\\autotracing;I:\\Research\\Outside_Projects\\java-instrumentation-annotation\\autotracing\\Comp533All.jar;I:\\Research\\Outside_Projects\\java-instrumentation-annotation\\byteman-4.0.7\\contrib\\bmunit\\byteman-bmunit.jar;I:\\Research\\Outside_Projects\\java-instrumentation-annotation\\byteman-4.0.7\\contrib\\bmunit\\byteman-bmunit5.jar;I:\\Research\\Outside_Projects\\java-instrumentation-annotation\\byteman-4.0.7\\contrib\\dtest\\byteman-dtest.jar;I:\\Research\\Outside_Projects\\java-instrumentation-annotation\\byteman-4.0.7\\contrib\\jboss-modules-system\\byteman-jboss-modules-plugin.jar;I:\\Research\\Outside_Projects\\java-instrumentation-annotation\\byteman-4.0.7\\lib\\byteman-install.jar;I:\\Research\\Outside_Projects\\java-instrumentation-annotation\\byteman-4.0.7\\lib\\byteman-submit.jar;I:\\Research\\Outside_Projects\\java-instrumentation-annotation\\byteman-4.0.7\\lib\\byteman.jar;I:\\Research\\Outside_Projects\\java-instrumentation-annotation\\byteman-4.0.7\\sample\\lib\\byteman-sample.jar;I:\\Research\\Outside_Projects\\java-instrumentation-annotation\\autotracing\\autotracing_annotations.jar\" concurrency.Main".split(" "));
			
			ProcessBuilder pb = new ProcessBuilder("java -javaagent:./Logs/LocalChecks/agent.jar -Dfile.encoding=Cp1252 -classpath \"I:\\Research\\Log_Parsing\\Research\\InjectionProject\\bin;I:\\Research\\Outside_Projects\\java-instrumentation-annotation\\autotracing\\BytemanImplementation.jar\" sorting.BubbleSort".split(" "));
			
			
			pb.directory(new File("./"));
			pb.inheritIO();
			Process process = pb.start();
			System.out.println("PB exit Val: "+process.waitFor());
	//		targets.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
