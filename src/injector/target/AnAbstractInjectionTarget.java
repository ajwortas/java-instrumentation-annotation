package injector.target;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * An abstract injection target.
 * <p>
 * This is meant to be the fundamental uniting abstract class for
 * all injection targets, implementing what is common among all.
 *
 * @author Robert E. Price, III
 */
public abstract class AnAbstractInjectionTarget
    implements InjectionTarget
{

    // All Bytemna's rules have names.
    // This isn't necessary for our purposes, but I supported it.
    protected static String DEFAULT_RULE_NAME =
        "DEFAULT-RULE-NAME~~NO-NAME-GIVEN-";

    protected Class targetClass =  null;
    protected Class<? extends Annotation> classAnnotation = null;
    protected String className = null;
    protected Class<? extends Annotation> targetAnnotation = null;
    protected String targetName = null; 


    /**
     * Return the class being targeted by this rule.
     * <p>
     * Given a well-defined rule, this method should not return null
     * before the rule is injected into the ASM.
     * <p>
     * This method may return null if this target is defined by a
     * name or an annotation and the class lookup has not yet occurred.
     * <p>
     * An exception may be made if the custom rule does not require
     * does not require the Class reference, but this is dangerous.
     * Many applications of targets require the Class reference.
     *
     * @return clazz    Class being targeted by this target
     */
    public Class<?> getTargetClass()
    {
        return this.targetClass;
    }
    /**
     * Return the annotation of the class being targeted by this
     * rule.
     * <p>
     * This value may return null if this target's class is not
     * defined by a given annotation.
     *
     * @return annotation   Annotation of class being targeted by
     *                      this rule
     */
    public Class<? extends Annotation> getTargetClassAnnotation()
    {
        return this.classAnnotation;
    }
    /**
     * Returns the name of the class being targeted by this method. 
     * <p>
     * This value must not be null by the time the rule is to be
     * injected into the ASM. It can be set directly, derived from
     * the name of a class object provided to this target, or
     * derived from an annotation after a class lookup for said
     * annotation.
     *
     * @return ClassName    Target class name
     */ 
    public String getTargetClassName()
    {
        if (this.targetClass == null)
        {
            return this.className;
        }
        else
        {
            return this.targetClass.getName();
        }
    }
    /**
     * Returns the helper for the this target.
     * <p>
     * A helper class is the class that contains the methods whose
     * calls are actually injected into the ASM. What methods this
     * composes are dependent on the target type.
     * By default, the helper class is the injector itself, but this
     * is not necessary.
     *
     * @return helper   Helper for this target
     */
    public Class<?> getHelper()
    {
        return this.getClass();
    }
    /**
     * Returns the name of this rule.
     * <p>
     * This is optional and returns to a default name if no name has
     * been set for this target.
     * <p>
     * Byteman's rules must have unique names. Thus you may give a name
     * to your target. Note that many targets inject more than
     * one rule, so this name may be shared among rules.
     * If one target have multiple rules, than the rules must be
     * differentiated when getRules() is called.
     * There is no known benefit to defining a custom target name at
     * this time, other than debugging Byteman's rule files.  
     *
     * @return name Name of this rule.
     */ 
    public String getRuleName()
    {
        return DEFAULT_RULE_NAME + this.getClass().getName();
    }
    /**
     * Return the annotation associated with this target.
     * <p>
     * This is optional and may return null if this target is not
     * annotated.
     *
     * @return annotation   The annotation associated with this
     *                      target. 
     */ 
    public Class<? extends Annotation> getTargetAnnotation()
    {
        return this.targetAnnotation;
    }
    /**
     * Return this name of the target. 
     * <p>
     * Given a well-formatted rule, this cannot return null when
     * the rule is injected in to the ASM. It may return null if
     * this target is annotated and the annotation lookup has not
     * yet been executed.
     *
     * @return name Name of this target
     */ 
    public String getTargetName()
    {
        return this.targetName;
    }
    /**
     * Sets the target class for this target. 
     *
     * @param targetCass    Target class of this target
     * @return clazz        The class to which this target is
     *                      associated with. Unless the param is null, 
     *                      the return of this method should be
     *                      the param.
     */
    public Class<?> setTargetClass(Class<?> targetClass)
    {
        if (this.targetClass == null)
        {
            this.targetClass = targetClass;
        }
        return this.targetClass;
    }
    /**
     * Sets the name of this target. 
     *
     * @param targetName    The name of this target
     * @return name         The name to which this target is
     *                      associated with. Unless param is null,
     *                      the return of this
     *                      method should be the param. 
     */
    public String setTargetName(String targetName)
    {
        if (this.targetName == null)
        {
            this.targetName = targetName;
        }
        return this.targetName;
    } 

}
