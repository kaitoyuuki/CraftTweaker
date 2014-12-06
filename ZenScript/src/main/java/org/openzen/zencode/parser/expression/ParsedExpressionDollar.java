/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zencode.parser.expression;

import org.openzen.zencode.IZenCompileEnvironment;
import org.openzen.zencode.symbolic.scope.IScopeMethod;
import org.openzen.zencode.symbolic.expression.IPartialExpression;
import org.openzen.zencode.runtime.IAny;
import org.openzen.zencode.symbolic.type.IZenType;
import org.openzen.zencode.util.CodePosition;

/**
 *
 * @author Stan
 */
public class ParsedExpressionDollar extends ParsedExpression
{
	private final String name;

	public ParsedExpressionDollar(CodePosition position, String name)
	{
		super(position);

		this.name = name;
	}

	@Override
	public <E extends IPartialExpression<E, T>, T extends IZenType<E, T>>
		 IPartialExpression<E, T> compilePartial(IScopeMethod<E, T> scope, T asType)
	{
		IPartialExpression<E, T> result = scope.getEnvironment().getDollar(getPosition(), scope, name);
		if (result == null) {
			scope.error(getPosition(), "Dollar variable not found: " + name);
			return scope.getExpressionCompiler().invalid(getPosition(), scope, asType);
		}
		
		if (asType != null)
			result = result.cast(getPosition(), asType);
		
		return result;
	}

	@Override
	public IAny eval(IZenCompileEnvironment<?, ?> environment)
	{
		if (name == null)
			return null;

		return environment.evalDollar(name);
	}
}
