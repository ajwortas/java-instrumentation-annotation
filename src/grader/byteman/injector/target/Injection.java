package grader.byteman.injector.target;

/**
 * Interface for Byteman helper classes, support methods whose
 * calls are directly injected into ASM.
 * <p>
 * This interface only suppots a single doAction() method, which may
 * not be appropriate for all targets
 * @author Robert E. Price, III
 */
public interface Injection
{
    /**
     * Action to be done by the helper method.
     * <p>
     * The "action" is whatever is to be injected into the ASM,
     * whatever extra may be done.
     * <p>
     * It should be noted that a well-formatted target may not make
     * use of this method, instead employing its own doAction()
     * methods(s) with custom, appropriate signatures.
     */ 
    // void doAction();
}
