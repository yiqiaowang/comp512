package Server.Common;

import java.io.Serializable;

public class MethodLambda implements Serializable {
        
    public MethodLambda() {};

    public void callMethod() {
        System.out.println("From the lambda");
    };
}
