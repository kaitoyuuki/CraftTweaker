/*
 * This file is part of MineTweaker API, licensed under the MIT License (MIT).
 * 
 * Copyright (c) 2014 MineTweaker <http://minetweaker3.powerofbytes.com>
 */
package org.openzen.zencode.parser.modifier;

import org.openzen.zencode.util.CodePosition;

/**
 *
 * @author Stan
 */
public class ParsedModifierSynchronized implements IParsedModifier
{
	private final CodePosition position;
	
	public ParsedModifierSynchronized(CodePosition position)
	{
		this.position = position;
	}
}
