/*
 * This file is part of ZenCode, licensed under the MIT License (MIT).
 * 
 * Copyright (c) 2014 openzen.org <http://zencode.openzen.org>
 */
package org.openzen.zencode.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to indicate static methods for expansions. For native classes, use ZenMethod
 * instead on a static method.
 * 
 * @author Stan Hebben
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ZenMethodStatic {
	/**
	 * Method name. If omitted, the annotated method name will be used.
	 * 
	 * @return method name
	 */
	public String value() default "";
}
