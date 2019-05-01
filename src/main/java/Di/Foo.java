package Di;

import javax.inject.Named;

@Named
public class Foo {
    public String getMessage(){
        return "Foo hello";
    }
}
