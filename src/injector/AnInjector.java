package injector;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.ClassNotFoundException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.rmi.RemoteException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.byteman.agent.Main;
import org.jboss.byteman.agent.submit.Submit;
import org.jboss.byteman.contrib.dtest.RuleConstructor;
import org.jboss.byteman.contrib.dtest.Instrumentor;
import org.jboss.byteman.rule.helper.Helper;
import org.jboss.byteman.rule.Rule;

import injector.target.*;


/**
 * A basic implementation of an injector using Byteman.
 *
 * Byteman handles the ASM manipulation. The task of this injector
 * is to setup
 *
 * @author  Robert E. Price III
 */
public class AnInjector implements Injector
{

    // This flag is useful for debugging why a rules file
    // may fail by looking at its exact content.
    // It would NOT be false when distributed.
    // Collecting the contents of rules files by other means may be
    // more preferable, but this worked for REPIII.
    private static final boolean DELETE_RULES_ON_ERROR = false;

    // This is updated when the class loads and finds the path to
    // this jar.
    private static String BYTEMAN_JAR_PATH = "";

    protected Map<String, List<InjectionTarget>> namedClassTargets =
        new HashMap<String, List<InjectionTarget>>();

    // Byteman does not support class lookup by annotation.
    // If a target identifies its class by annotation we will have to
    // look up the class and set that target's class name based off
    // of that lookup.
    // Currently that is not being done.
    // Thus, annotations are not yet supported.
    protected Map<Class<? extends Annotation>, List<InjectionTarget>> annotatedClassTargets
        = new HashMap<Class<? extends Annotation>, List<InjectionTarget>>();

    // Byteman needs a copy of the instrumentation reference
    // provided by the JVM. Thus, we keep around a copy, even though
    // we don't use it.
    protected Instrumentation instrumentation;

    // Custom class reloader.
    protected ClassLoader loader;

    /**
     * Instantiates an injector.
     *
     * Sets up the required Byteman configurations to instrument
     * correctly.
     */
    public AnInjector()
    {

        // Byteman configs
        System.setProperty("org.jboss.byteman.compileToBytecode", "");
        System.setProperty("org.jboss.byteman.debug", "");
        System.setProperty("org.jboss.byteman.transform.all", "");

        // Byteman's documentation claims that if you set this,
        // byteman will automatically reload any class it
        // transforms.
        // This is not the case, however.
        // System.setProperty("org.jboss.byteman.agent.TransformListener", "");

        this.loader = new AReloadClassLoader(this.getClass().getClassLoader());

        // This needs to be "/", not ".", since it's on disk.
        String byteman_path = ClassLoader.getSystemClassLoader()
            .getResource("org/jboss/byteman/agent/Main.class").toString();
        BYTEMAN_JAR_PATH = byteman_path.substring(
            byteman_path.indexOf("file:") + 5, byteman_path.indexOf("!")
        );

    }

    /**
     * Set the instrumentation reference for this injector.
     *
     * Whatever does the instrumentation requires this
     * instrumentation reference provided by the JVM when Premain is
     * called.
     * Back when we instrumented ourselves we required this
     * reference. Now we delegate that functionality to
     * Byteman, but Byteman similarly requires the reference.
     *
     * @param instr                     Instrumentation reference
     *                                  provided by the
     *                                  original Premain that
     *                                  spawned this processes.
     * @return InstrumentationStatus    Was the Instrumentation
     *                                  successfully registered with
     *                                  this injector.
     *                                  Currently always returns
     *                                  true.
     */
    public boolean setInstrumentation(Instrumentation instr)
    {

        this.instrumentation = instr;
        // We no longer walk the visitor tree, so we now longer
        // need to be registered as a transformer.
        //this.instrumentation.addTransformer(this, true);

        return true;

    }

    /**
     * Register an injection target with this injector.
     *
     * We register targets so that Byteman only requires a single
     * pass, instead of attempting to inject every target when they
     * arrive.
     *
     * @param target            The target to be registered
     * @return  TargetStatus    Whether or not the target was
     *                          registered successfully.
     */
    public boolean registerTarget(InjectionTarget target)
    {

        if (target == null)
        {
            throw new NullPointerException(
                "Target attempting to be registered is null."
            );
        }

        // If the target knows the full formal name of the class it
        // is targeting, we can immediately put it into the
        // nameClassTargets list.
        if (
            target.getTargetClass() != null
            || target.isTargetClassNamed()
        )
        {
            List<InjectionTarget> targets;
            String name = target.getTargetClassName();
            if (this.namedClassTargets.containsKey(name))
            {
                targets = this.namedClassTargets.get(name);
            }
            else
            {
                targets = new ArrayList<InjectionTarget>();
                this.namedClassTargets.put(name, targets);
            }
            targets.add(target);
            if (target.getTargetClass() == null)
            {
                try
                {
                    target.setTargetClass(
                        this.loader.loadClass(
                            target.getTargetClassName()
                        )
                    );
                } catch (ClassNotFoundException e)
                {
                    this.reportError(
                        e,
                        String.format(
                            "Could not find class %s for rule %s\n",
                            target.getTargetClassName(),
                            target.getRuleName()
                        ),
                        null
                    );
                }
            }
        }
        else if (target.isTargetClassAnnotated())
        {
            Class<? extends Annotation> annotation = target.getTargetClassAnnotation();
            List<InjectionTarget> targets;
            if (this.annotatedClassTargets.containsKey(annotation))
            {
                targets = this.annotatedClassTargets.get(annotation);
            }
            else
            {
                targets = new ArrayList<InjectionTarget>();
                this.annotatedClassTargets.put(annotation, targets);
            }
            targets.add(target);
        }
        // The target has to give us a class name or an annotation.
        // If it doesn't provide one of those options, the target is
        // ill-formed, and some sort of notice flag should be
        // thrown.
        // I would rather throw an exception here, I think.
        else
        {
            return false;
        }

        return true;

    }

    /**
     * Inject all targets registered with this injector into the ASM.
     *
     * @return InjectionStatus  Whether or not the injection was
     *                          successful.
     */
    public boolean inject()
    {
        for (List<InjectionTarget> list : this.namedClassTargets.values())
        {
            this.inject(list);
        }
        for (List<InjectionTarget> list : this.annotatedClassTargets.values())
        {
            this.inject(list);
        }
        this.namedClassTargets.clear();
        this.annotatedClassTargets.clear();
        return true;
    }

    /**
     * Inject a single target into the ASM.
     *
     * @param target            Target to be injected into the ASM
     * @return InjectionStatus  Whether or not the injection was
     *                          successful
     */
    public boolean inject(InjectionTarget target)
    {
        List<InjectionTarget> targets =
            new ArrayList<InjectionTarget>();
        targets.add(target);
        return this.inject(targets);
    }

    /**
     * Inject a list of targest into the ASM.
     *
     * @param targets           List of targets to be injected into
     *                          the ASM
     * @return InjectionStatus  Whether or not the injection was
     *                          successful
     */
    public boolean inject(List<InjectionTarget> targets)
    {

        //System.out.println("Inject ran");

        if (this.instrumentation == null)
        {
            throw new IllegalStateException(
                "You must set this Injector's Instrumentation "
                + "before injecting a rule with it."
            );
        }

        //this.loader = new AReloadClassLoader();

        Instrumentor instrumentor = null;
        File rulesFile;

        // The instrumentor is designed to inject into a remote JVM.
        // We're using it locally, so we just ignore any remote
        // exceptions.
        try
        {
        	//Byteman will use RMI, event locally, so we set the default RMI port
        	//To something not likely to be used by student submissions
        	//(The default, 1099, is used by java RMI libraries)
            instrumentor = new Instrumentor(new Submit(), 0);
        }
        catch (RemoteException e) {
            e.printStackTrace();
            throw new IllegalStateException(
                "Instrumentor threw a remote exception, even though "
                + "it is only is being used locally."
            );
        }
        try
        {
            rulesFile = File.createTempFile("injection", ".btm");
        }
        catch (IOException e)
        {
            throw new RuntimeException(
                "Could not create temp rules file"
            );
        }
        instrumentor.setRedirectedSubmissionsFile(rulesFile);

        // TODO Can be removed.
        //
        // For testing before we had target classes that
        // flexibly create rules.
        //
        //String ruleTest = RuleConstructor.createRule("Test")
        //    .onClass("testing.Test")
        //    .inMethod("hello")
        //    .helper(injection.HelperSub.class)
        //    .atLine(13)
        //    .ifTrue()
        //    .doAction("debug()")
        //    .build();
        //String ruleTest = RuleConstructor.createRule("Test")
        //    .onClass("testing.Test")
        //    .inMethod("increment")
        //    .helper(injection.HelperSub.class)
        //    .atWrite("i")
        //    .bind("writable:int = $this.i")
        //    .ifTrue()
        //    .doAction("onAction(writable)")
        //    .build();
        //
        // installScript() throws a generic exception.
        //try
        //{
        //    instrumentor.installScript("Test", ruleTest);
        //}
        //catch (Exception e)
        //{
        //    this.reportError(
        //        e,
        //        "Could not install script",
        //        rulesFile
        //    );
        //    return false;
        //}

        for (InjectionTarget target : targets)
        {

            // TODO Write a custom exception class that all rules
            // files can throw if they are ill formatted. That way
            // we can just catch one exception here instead of
            // the generic Exception. Delegate catching the
            // sub-exceptions of an ill-formatted rule onto
            // getRules().
            for (String rule : target.getRules()) {
                // installScript() throws a generic exception.
                try
                {
                    instrumentor.installScript(
                        target.getTargetName(), rule
                    );
                }
                catch (Exception e)
                {
                    this.reportError(
                        e,
                        "Could not \"install\" rules "
                        +"(write test rules to file).",
                        rulesFile
                    );
                    return false;

                }
            }
        }

        // TODO Byteman has tools to check if a script is properly
        // formatted. We'll want to use that before calling
        // Byteman's premain.
        // REPIII knows we can call it from the command line, but
        // I don't know if there's some API we could use instead.
        //
        // Once again premain() throws a generic Exception.
        try
        {
            Main.premain(
                String.format(
                    "script:%s,boot:%s",
                    rulesFile.getAbsolutePath().toString(),
                    BYTEMAN_JAR_PATH
                ),
                this.instrumentation
            );
        }
        catch (Exception e)
        {
            this.reportError(e, "Byteman failed", rulesFile);
            return false;
        }

        rulesFile.delete();
        // System.out.println(rulesFile);
        this.loader = new AReloadClassLoader(this.getClass().getClassLoader());
        for (InjectionTarget target : targets)
        {
            try
            {
                if(target instanceof MulticlassTarget){
                    for(String className : ((MulticlassTarget)target).getTargetedClassNames()){
                        // System.out.println(className);
                    	System.err.println(className);
                        this.loader.loadClass(className);
                        this.loader = new AReloadClassLoader(this.getClass().getClassLoader());
                    }
                }
                else{
                    this.loader.loadClass(target.getTargetClassName());
                }
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        this.loader = new AReloadClassLoader(this.getClass().getClassLoader());


        return true;

    }

    // FIXME Can be removed.
    // We do not need to walk the visitor tree, nor do we need to
    // transform any binaries. Byteman does that.
    //public byte[] transform(
    //    ClassLoader loader, String className,
    //    Class<?> classBeingRedefined,
    //    ProtectionDomain protectionDomain,
    //    byte[] classfileBuffer
    //)
    //{
    //
    //}

    /**
     * Reports an error using a specific format and deletes the
     * rules file, if applicable.
     *
     * @param e     Exception associated with the error.
     * @param msg   Error message associated with the error.
     * @param f     Reference to rules file. If no reference to the
     *              rules file exists, passing null is safe.
     */
    private void reportError(Exception e, String msg, File f)
    {
        System.err.println(msg);
        e.printStackTrace();
        if (f != null && DELETE_RULES_ON_ERROR)
        {
            f.delete();
        }
    }

}
