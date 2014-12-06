/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zencode.symbolic.type.casting;

import org.openzen.zencode.symbolic.expression.IPartialExpression;
import org.openzen.zencode.symbolic.scope.IScopeMethod;
import org.openzen.zencode.symbolic.type.IZenType;
import org.openzen.zencode.util.CodePosition;

/**
 *
 * @author Stan
 * @param <E>
 * @param <T>
 */
public interface ICastingRule<E extends IPartialExpression<E, T>, T extends IZenType<E, T>>
{
	public E cast(CodePosition position, IScopeMethod<E, T> scope, E value);

	public T getInputType();

	public T getResultingType();
	
	public boolean isExplicit();
}
