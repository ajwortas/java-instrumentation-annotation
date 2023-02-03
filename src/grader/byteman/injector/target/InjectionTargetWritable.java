package grader.byteman.injector.target;

/**
 * Interface for writable injection targets.
 * <p>
 * Writable injection targets anything that can be written to,
 * including variables and fields.
 * <p>
 * This interface cannot be generic, Object type must be used for
 * the action methods, because the reflection injection methods use
 * Object typing and expect Object typing.
 *
 * @author Robert E. Price, III
 */
public interface InjectionTargetWritable<T> extends InjectionTarget
{

    /**
     * Do an action immediately before the writable is written to.
     * <p>
     * The value of obj will be what it was before the write occurred.
     * <p>
     * This method is not directly injected to where the writable
     * is being written to. A CALL to doActionBefore is injected. The
     * body of doActionBefore itself is not injected.
     *
     * @param obj Writable before write.
     */
    void doActionBefore(T t);
    /**
     * Do an action immediately after the writable is written to.
     * <p>
     * The value of obj will be what it was after the write occurred.
     * <p>
     * This method is not directly injected to where the writable
     * is being written to. A CALL to doActionBefore is injected. The
     * body of doActionBefore itself is not injected.
     *
     * @param obj Writable after write.
     */
    void doActionAfter(T t);
    /**
     * Whether or not the target writable is a field.
     * <p>
     * Byteman does not treat fields and stack variables any
     * different when it comes to injection, but the semantics of the
     * rule is slightly different. Thus, a target writable must know
     * if it is targeting a field or not.
     *
     * @return FieldStatus  Is the writable a field. False
     *                      implicitly implies the writable is a
     *                      stack variable.
     */
    boolean isField();

}
