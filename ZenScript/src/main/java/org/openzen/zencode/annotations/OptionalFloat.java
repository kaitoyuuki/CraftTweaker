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
 * Denotes that this parameter is optional. Inside the script, this parameter
 * may be omitted, in which case it is automatically filled with the default
 * value of that parameter type (false, null or 0). Invalid for @NonNull type.
 * 
 * @author Stanneke
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface OptionalFloat {
	float value();
}
