package io.github.lefraudeur.modules;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Info
{

    String name();

    String description();

    int key();

    Category category();
}