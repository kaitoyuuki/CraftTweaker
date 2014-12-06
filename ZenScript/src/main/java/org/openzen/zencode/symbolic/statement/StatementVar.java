/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openzen.zencode.symbolic.statement;

import org.openzen.zencode.symbolic.expression.IPartialExpression;
import org.openzen.zencode.symbolic.scope.IScopeMethod;
import org.openzen.zencode.symbolic.symbols.SymbolLocal;
import org.openzen.zencode.symbolic.type.IZenType;
import org.openzen.zencode.util.CodePosition;

/**
 *
 * @author Stanneke
 * @param <E>
 * @param <T>
 */
public class StatementVar<E extends IPartialExpression<E, T>, T extends IZenType<E, T>> extends Statement<E, T>
{
	private final SymbolLocal<E, T> symbol;
	private final E initializer;

	public StatementVar(CodePosition position, IScopeMethod<E, T> method, SymbolLocal<E, T> symbol, E initializer)
	{
		super(position, method);

		this.symbol = symbol;
		this.initializer = initializer;
	}
	
	public SymbolLocal<E, T> getSymbol()
	{
		return symbol;
	}
	
	public E getInitializer()
	{
		return initializer;
	}

	@Override
	public <U> U process(IStatementProcessor<E, T, U> processor)
	{
		return processor.onVar(this);
	}
}
