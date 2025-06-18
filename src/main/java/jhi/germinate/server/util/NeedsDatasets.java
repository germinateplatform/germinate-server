package jhi.germinate.server.util;

import jakarta.ws.rs.NameBinding;
import jhi.germinate.resource.enums.UserType;

import java.lang.annotation.*;

/**
 * Annotation used to indicate whether a function needs access to the datasets or not.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NeedsDatasets
{}
