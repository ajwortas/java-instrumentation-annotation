package testing;

import java.lang.instrument.Instrumentation;

import injector.*;

/**
 * Example Premain in which the injection library is used.
 */
public class Premain
{

    // The premain() will run before main if the arguments to the
    // JVM are correct. In this manner students can run the tests,
    // which will call this premain, but should never knowe this is
    // happening, and so hopefully will never need to utilize or
    // debug this premain call. 
    // Once premain() exits, the student's main() is called.
    public static void premain(String args, Instrumentation instr)
    {
        Injector injector = InjectorFactory.getInjectorSingleton();
        if (injector instanceof AnInjector)
        {
            // Injectors in general don't need an instrumentation
            // file, but the one Byteman uses does.
            ((AnInjector)injector).setInstrumentation(instr);
        }
        // Currently one must register all targets and *then* inject
        // the registered targets. This is to minimize the number of
        // times Byteman must run.
        // When there's only one target it feels silly, but when
        // there are many hopefully this design makes sense and is
        // beneficial.
        injector.registerTarget(new TargetFieldTest());
        injector.inject();
    }

}
