package jhi.germinate.server.util;

import jhi.germinate.resource.enums.UserType;

import javax.ws.rs.NameBinding;
import java.lang.annotation.*;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Secured
{
	UserType[] value() default {};
}
