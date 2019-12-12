package testing;

public class Test
{

    public Integer i = 0;

    public Test() {}

    public void hello()
    {
        System.out.println("Hello World!");
        System.out.println("Goodbye World!");
    }

    // Used for atLine() injection tests before the API was fleshed
    // out, to go between Hello world and Goodbye World in hello().
    public static void helloInjectedWorld()
    {
        System.out.println("Hello Injected World!");
    }

    public void increment()
    {
        for (int i = 0; i < 3; ++i)
        {
            this.i = i;
        }
    }

    public static void main(String args[])
    {
        new Test().increment();
        //new Test().hello();
        System.out.println("Job's done.");
    }

}
