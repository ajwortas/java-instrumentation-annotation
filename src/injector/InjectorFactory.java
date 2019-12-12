package injector;

/**
 * Injector Singleton Factory.
 *
 * @author Robert E. Price, III
 */
public class InjectorFactory
{

    private static Injector injector;

    /**
     * Register an injector as the injector to use for ASM
     * manipulation. 
     *
     * @param injector              Injector to be used
     * @return RegistrationStatus   Whether or not the registration
     *                              was successful.
     */
    public static boolean registerInjector(Injector injector)
    {

        InjectorFactory.injector = injector;

        return true;

    }

    /**
     * Get the ASM injector for this instrumentation. 
     *
     * @return injector Injector used during this instrumentation.
     */
    public static Injector getInjectorSingleton()
    {
        if (InjectorFactory.injector == null)
        {
            InjectorFactory.injector = new AnInjector(); 
        }
        return InjectorFactory.injector;
    }

}
