package jhi.germinate.server.auth;

import java.lang.annotation.*;

/**
 * @author Sebastian Raubach
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface MinUserType
{
	UserType value();
}
