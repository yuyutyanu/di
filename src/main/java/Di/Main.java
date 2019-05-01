package Di;

public class Main {
    public static void main(String[] args) {
        Context.autoRegister();
        Bar bar = (Bar) Context.getBeans("bar");
        System.out.println(bar.getMessage());

    }
}
