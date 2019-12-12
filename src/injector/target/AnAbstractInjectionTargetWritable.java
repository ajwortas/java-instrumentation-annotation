package injector.target;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jboss.byteman.contrib.dtest.RuleConstructor;

import injector.*;


/**
 * An abstract target writable.
 * <p>
 * Target is applicable for fields and methods.
 * <p>
 * This abstract target cannot instrument writes to fields or
 * variables outside of methods, such as in constructors. Fields set
 * before instantiation similarly cannot be instrumented.
 *
 * @author Robert E. Price, III
 */
public abstract class AnAbstractInjectionTargetWritable<T>
    extends AnAbstractInjectionTarget
    implements InjectionTargetWritable<T>
{

    // Arbitrary, but must be consistent.
    private static final String WRITABLE_BINDING_NAME = "writable";

    private final T t;

    protected AnAbstractInjectionTargetWritable(T t)
    {
        this.t = t;
    }


    /**
     * doAction is not applicable for a writable target; does
     * nothing.
     * <p>
     * This must be implemented for the hierarchy, but has been
     * replaced by other methods to support writables.
     *
     * @see doActionBefore
     * @see doActionAfter
     */
    @Deprecated
    public void doAction() {}

    /**
     * Create Byteman rule for this target writable.
     *
     * @return rule Byteman ASM injection rule.
     */
    final public List<String> getRules()
    {

        String writable;
        String bindClause;
        // Byteman does care if the writable is a field or local
        // stack variable, but all it demands is that the name of
        // local stack variables are prepended with "$".
        if (this.isField())
        {
            // TODO Obviously the ints need to become generics.
            writable = this.getTargetName();
            bindClause = String.format(
                "%s:%s = $this.%s",
                WRITABLE_BINDING_NAME,
                this.t.getClass().getName(),
                writable
            );
        }
        else
        {
            writable = "$" + this.getTargetName();
            bindClause = String.format(
                "%s:%s= %s",
                WRITABLE_BINDING_NAME,
                this.t.getClass().getName(),
                writable
            );
        }

        List<String> rules = new ArrayList<String>();

        // This rule targets the variable or field everywhere it is
        // written to throughout the target class.
        // Thus we must add this rule into every class. Byteman
        // demands the class information.
        //
        // We must also inject two rules for every found instance,
        // as we must inject a call before the value is written to
        // get the old value, and after the value is written to get
        // the new value.
        int i = 0;
        for (Method method : this.getTargetClass().getMethods())
        {
            rules.add(
                RuleConstructor.createRule(this.getRuleName() + i++)
                    .onClass(this.getTargetClass())
                    .inMethod(method.getName())
                    .helper(this.getHelper())
                    .atWrite(writable)
                    .bind(bindClause)
                    .ifTrue()
                    .doAction("doActionBefore(writable)")
                    .build()
            );
            rules.add(
                RuleConstructor.createRule(this.getRuleName() + i++)
                    .onClass(getTargetClass())
                    .inMethod(method.getName())
                    .helper(this.getHelper())
                    .afterWrite(writable)
                    .bind(bindClause)
                    .ifTrue()
                    .doAction("doActionAfter(writable)")
                    .build()
            );
        }

        return rules;

    }

}
