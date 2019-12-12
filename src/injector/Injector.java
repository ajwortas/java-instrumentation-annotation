package injector;

import java.util.List;

import injector.target.InjectionTarget;

/**
 * An Injector takes in targets and injects them into the ASM.
 */
public interface Injector
{
    /**
     * Inject all targets registerd with a injector into the ASM. 
     */
    public boolean inject();
    /**
     * Inject a single target into the ASM.
     *
     * @param target    Target to be injected into the ASM
     */
    public boolean inject(InjectionTarget target);
    /**
      * Inject a list of targest into the ASM.
      *
      * @param targets   List of targets to be injected into the ASM
      */
    public boolean inject(List<InjectionTarget> target);
    /**
     * Register an injection target with this injector.
     *
     * @param target    The target to be registered 
     */ 
    public boolean registerTarget(InjectionTarget target);
} 
