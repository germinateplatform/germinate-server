package jhi.germinate.server.util;

import jakarta.ws.rs.NameBinding;

import java.lang.annotation.*;

//@Compress annotation is the name binding annotation
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface CompressDeflate
{}