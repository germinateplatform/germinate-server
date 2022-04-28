package jhi.germinate.server.util;

import jhi.germinate.resource.enums.UserType;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.*;

/**
 * Annotation used to secure server resources.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Secured
{
	UserType[] value() default {};
}
