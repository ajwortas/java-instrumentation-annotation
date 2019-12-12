package injector;

/**
 * A container of static, general utility functions.
 *
 * @author  Robert E. Price, III
 */
public final class Utilities
{

    /**
     * Prints an error message and stack trace in a uniform format. 
     *
     * @param e     Exception associated with the error
     * @param msg   Message associated with the error
     */
    public static void printError(Exception e, String msg)
    {
        System.err.println(msg);
        e.printStackTrace();
    }

    /**
     * Converts class names between the two formats.
     *
     * All class names are references as
     * "package1/package11/Foo.java", except when dealing with
     * imports and the class loader. Imports and the class loader
     * use "package1.package11.Foo.java". Since we use both, and
     * references from one need to go to the other, this handles the
     * conversion for us. 
     *
     * @param name      Class name to be transformed
     * @param toSlash   Whether or not the output should the slash
     *                  format, using '/'. If false, the dot format,
     *                  using '.' is used. 
     * @return Transformed Class Name  
     */
    public static String classNameConverter(
        String name, boolean toSlash
    )
    {

        char replacement, target;
        if (toSlash)
        {
            replacement = '/';
            target = '.';
        }
        else
        {
            replacement = '.';
            target = '/';
        }

        return name.replace(target, replacement);

    }

}
