package com.epam.http.annotations;

import com.epam.http.requests.RestMethodTypes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.epam.http.requests.RestMethodTypes.*;

/**
 * Created by Roman_Iovlev on 12/19/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Method {
    String uri();
    RestMethodTypes[] types() default { GET, POST, PUT, DELETE };
}
