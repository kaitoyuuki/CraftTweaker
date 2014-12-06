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
 * Used to indicate an operator. The arguments depend on the kind of operator.
 * 
 * @author Stan Hebben
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ZenOperator {
	/**
	 * Operator type for this operator.
	 * 
	 * @return operator type
	 */
	OperatorType value();
}
