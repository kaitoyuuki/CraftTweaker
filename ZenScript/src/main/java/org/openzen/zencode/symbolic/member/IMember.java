/*
 * This file is part of MineTweaker API, licensed under the MIT License (MIT).
 * 
 * Copyright (c) 2014 MineTweaker <http://minetweaker3.powerofbytes.com>
 */
package org.openzen.zencode.symbolic.member;

import org.openzen.zencode.symbolic.expression.IPartialExpression;
import org.openzen.zencode.symbolic.type.IZenType;
import org.openzen.zencode.symbolic.unit.ISymbolicUnit;

/**
 *
 * @author Stan
 * @param <E>
 * @param <T>
 */
public interface IMember<E extends IPartialExpression<E, T>, T extends IZenType<E, T>>
{
	public ISymbolicUnit<E, T> getUnit();
}
