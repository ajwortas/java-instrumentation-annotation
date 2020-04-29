package testing;

import injector.*;
import injector.target.*;

/**
 * A target which targets a field.
 *
 * @see AnAbstractInjectionTargetWritable
 *
 * @author Robert E. Price, III
 */
public class TargetFieldTest
    extends AnAbstractInjectionTargetWritable<Integer>
{

    /**
     * Instantiate a new TargetFieldTest.
     * <p>
     * For typing concerns, AnAbstractInjectionTargetWritable
     * requires a "dummy" instance of a type of the writable. It can
     * be any value, it's value is not used, but the typing
     * information is necessary. 
     */ 
    public TargetFieldTest()
    {
        super(new Integer(0));
    }

    public void doActionBefore(Integer i)
    {
        System.out.println("Value before write: " + i);
    }
    public void doActionAfter(Integer i)
    {
        System.out.println("Value after write: " + i);
    }

    public boolean isTargetAnnotated()
    {
        return false;
    }
    public boolean isTargetClassAnnotated()
    {
        return false;
    }
    public boolean isTargetClassNamed()
    {
        return true;
    }
    public boolean isField()
    {
        return true;
    }
    public String getName()
    {
        return this.getClass().getName();
    }
    public String getTargetClassName()
    {
        return "testing.Test";
    }
    public String getTargetName()
    {
        return "i";
    }

}
