package Di;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class Bar {
    @Inject
    Foo foo;

    String getMessage() {
        return foo.getMessage() + " Bar hello";
    }
}
