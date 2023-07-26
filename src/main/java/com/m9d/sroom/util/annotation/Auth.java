package com.m9d.sroom.util.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Auth {
	public enum Role { SIGNED }
	public Role role() default Role.SIGNED;
}