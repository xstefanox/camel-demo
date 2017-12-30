package io.github.xstefanox.demo.camel;

import org.apache.camel.main.Main;

public class CamelDemo {

    public static void main(final String... args) throws Exception {

        Main main = new Main();
        main.addRouteBuilder(new MyRouteBuilder());
        main.run();
    }
}
