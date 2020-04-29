package injector;

import java.io.*;

/**
 * A custom class loader so that classes can be loaded before
 * Byteman instruments the binary.
 * <p>
 * Classes are loaded from binary files by default, instead of
 * searching if they have already been loaded as a normal class
 * loader would do. This class loader purposefully loads them from
 * disk before trying anything else.
 *
 * @author Robert E. Price, III
 */
public class AReloadClassLoader extends ClassLoader
{

    private final String JVM_BINARY_EXTENSION = "class";

    public AReloadClassLoader() {}
    
    public AReloadClassLoader(ClassLoader loader)
    {
        super(loader);
    }

    /**
     * Load the class given a name.
     * <p>
     * Only delegates if it fails to load the class for any reason.
     *
     * @param className The name of the class to be loaded. 
     * @return clazz    The class loaded
     */ 
    public Class<?> loadClass(String className)
        throws ClassNotFoundException
    {
        try
        {
            return this.findClass(className);
        }
        catch (Exception e)
        {
            return super.loadClass(className, false);
        }
    }

    /**
     * Loads the specified class from a binary file, if it can find
     * it.
     * <p>
     * If the binary cannot be found, we delegate to the default
     * implementation of findClass
     * <p>
     * We purposefully do not go looking for an already-loaded
     * version. We want to load the class from the binary. That is
     * the purpose of this custom class loader.
     *
     * @param className The name of the class to find
     * @return clazz    A found definition of the class named
     */
    protected Class<?> findClass(String className)
    {
        try 
        {
            byte[] bytes = this.loadClassData(className);
            return this.defineClass(className, bytes, 0, bytes.length);
        } 
        catch (IOException ioe) 
        {
            try
            {
                // System.out.println(ioe);
                return super.loadClass(className);
            } 
            catch (ClassNotFoundException ce) { 
                return null;
            }
        }
    }

    /**
     * Load class data directly from a .class binary file.
     * <p>
     * This is a simple way to load raw class data. It's not
     * attempting to do anything else.
     *
     * @param className     Full class name of class to load
     * @return ClassData    Byte array representing raw class data  
     */ 
    protected byte[] loadClassData(String className)
        throws IOException
    {

        // Class name at the Java level uses dots, '.'
        // Class names at the binary level use '/', as that's what
        // the file system uses.
        //
        // We assume the binary is in the default bin folder.
        // If it's not we fail, and throw the IOException up the
        // stack.
        String binariesLocation = "bin";
        File f = new File(
            String.format(
                "%s/%s.%s",
                binariesLocation,
                // replaceAll is regex
                className.replace('.', '/'),
                JVM_BINARY_EXTENSION
            )
        );
        //"bin/" + className.replaceAll("\\.", "/") + ".class");
        byte buff[] = new byte[(int)f.length()];
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dataStream = new DataInputStream(new FileInputStream(f));
        dataStream.readFully(buff);
        dataStream.close();
        return buff;

    }

}
