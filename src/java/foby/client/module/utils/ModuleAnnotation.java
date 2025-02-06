package foby.client.module.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface ModuleAnnotation {

    String name();

    String desc() default "У данного модуля нету описания!";

    boolean setting() default false;

    int key() default -1;

    Category category();
}
