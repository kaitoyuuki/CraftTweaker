/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openzen.zencode.symbolic.type.casting;

import org.openzen.zencode.compiler.ITypeCompiler;
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
public class CastingRuleAnyToType<E extends IPartialExpression<E, T>, T extends IZenType<E, T>>
	implements ICastingRule<E, T>
{
	private final T fromType;
	private final T toType;
	
	public CastingRuleAnyToType(ITypeCompiler<E, T> types, T toType)
	{
		fromType = types.getAny();
		this.toType = toType;
	}

	@Override
	public E cast(CodePosition position, IScopeMethod<E, T> scope, E value)
	{
		return scope.getExpressionCompiler().anyCastTo(position, scope, value, toType);
	}

	@Override
	public T getInputType()
	{
		return fromType;
	}

	@Override
	public T getResultingType()
	{
		return toType;
	}

	@Override
	public boolean isExplicit()
	{
		return false;
	}
}
