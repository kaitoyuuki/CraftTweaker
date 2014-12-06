/*
 * This file is part of ZenCode, licensed under the MIT License (MIT).
 * 
 * Copyright (c) 2014 openzen.org <http://zencode.openzen.org>
 */
package org.openzen.zencode.java.expression;

import java.util.List;
import org.objectweb.asm.Label;
import org.openzen.zencode.java.type.IJavaType;
import org.openzen.zencode.symbolic.expression.IPartialExpression;
import org.openzen.zencode.symbolic.method.IMethod;
import org.openzen.zencode.symbolic.scope.IScopeMethod;
import org.openzen.zencode.symbolic.symbols.IZenSymbol;
import org.openzen.zencode.symbolic.type.casting.ICastingRule;
import org.openzen.zencode.symbolic.unit.SymbolicFunction;
import org.openzen.zencode.util.CodePosition;
import org.openzen.zencode.java.util.MethodOutput;
import org.openzen.zencode.symbolic.statement.Statement;
import org.openzen.zencode.symbolic.statement.StatementExpression;
import org.openzen.zencode.symbolic.statement.StatementReturn;

/**
 *
 * @author Stan
 */
public abstract class AbstractJavaExpression implements IJavaExpression
{
	private final CodePosition position;
	private final IScopeMethod<IJavaExpression, IJavaType> scope;

	public AbstractJavaExpression(CodePosition position, IScopeMethod<IJavaExpression, IJavaType> scope)
	{
		this.position = position;
		this.scope = scope;
	}

	@Override
	public CodePosition getPosition()
	{
		return position;
	}

	@Override
	public IScopeMethod<IJavaExpression, IJavaType> getScope()
	{
		return scope;
	}

	@Override
	public IJavaExpression cast(CodePosition position, IJavaType type)
	{
		if (getType().equals(type))
			return this;
		else {
			ICastingRule<IJavaExpression, IJavaType> castingRule = getType().getCastingRule(getScope().getAccessScope(), type);
			if (castingRule == null) {
				getScope().error(position, "Cannot cast " + this.getType() + " to " + type);
				return getScope().getExpressionCompiler().invalid(position, getScope(), type);
			} else
				return castingRule.cast(position, scope, this);
		}
	}
	
	@Override
	public void compileIf(Label onIf, MethodOutput output)
	{
		if (getType() == getScope().getTypes().getBool()) {
			compile(true, output);
			output.ifNE(onIf);
		} else if (getType().isNullable()) {
			compile(true, output);
			output.ifNonNull(onIf);
		} else
			throw new RuntimeException("cannot compile non-pointer non-boolean value to if condition");
	}

	@Override
	public void compileElse(Label onElse, MethodOutput output)
	{
		if (getType() == getScope().getTypes().getBool()) {
			compile(true, output);
			output.ifEQ(onElse);
		} else if (getType().isNullable()) {
			compile(true, output);
			output.ifNull(onElse);
		} else
			throw new RuntimeException("cannot compile non-pointer non-boolean value to if condition");
	}

	public Statement<IJavaExpression, IJavaType> asStatement()
	{
		return new StatementExpression<IJavaExpression, IJavaType>(position, scope, this);
	}
	
	public Statement<IJavaExpression, IJavaType> asReturnStatement()
	{
		return new StatementReturn<IJavaExpression, IJavaType>(position, scope, this);
	}

	// #########################################
	// ### IPartialExpression implementation ###
	// #########################################
	
	@Override
	public IJavaExpression eval()
	{
		return this;
	}

	@Override
	public IJavaExpression assign(CodePosition position, IJavaExpression other)
	{
		scope.error(position, "not a valid lvalue");
		return scope.getExpressionCompiler().invalid(position, scope, getType());
	}

	@Override
	public IPartialExpression<IJavaExpression, IJavaType> getMember(CodePosition position, String name)
	{
		return getType().getInstanceMember(position, getScope(), this, name);
	}

	@Override
	public IZenSymbol<IJavaExpression, IJavaType> toSymbol()
	{
		return null;
	}

	@Override
	public IJavaType toType(List<IJavaType> genericTypes)
	{
		scope.error(position, "not a valid type");
		return scope.getTypes().getAny();
	}

	@Override
	public List<IMethod<IJavaExpression, IJavaType>> getMethods()
	{
		return getType().getInstanceMethods();
	}
	
	@Override
	public IPartialExpression<IJavaExpression, IJavaType> call(CodePosition position, IMethod<IJavaExpression, IJavaType> method, IJavaExpression... arguments)
	{
		return method.callVirtual(position, scope, this, arguments);
	}

	@Override
	public IPartialExpression<IJavaExpression, IJavaType> via(SymbolicFunction<IJavaExpression, IJavaType> function)
	{
		return this;
	}
}
