package testing;

public class Test {

    public Integer i = 0;

    public Test() {
    }

    public void hello() {
        System.out.println("Hello World!");
        System.out.println("Goodbye World!");
    }

    // Used for atLine() injection tests before the API was fleshed
    // out, to go between Hello world and Goodbye World in hello().
    public static void helloInjectedWorld() {
        System.out.println("Hello Injected World!");
    }

    // @Deprecated
    public void increment() {
        for (int i = 0; i < 3; ++i) {
            this.i = i;
        }
    }

    @Deprecated
    public void testParam(int i){
        System.out.println("testParam called");
    }

    public static void main(String args[]) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Test t = new Test();
        t.increment();
        t.testParam(t.i);
        //new Test().hello();
        System.out.println("Job's done.");
    }

}
