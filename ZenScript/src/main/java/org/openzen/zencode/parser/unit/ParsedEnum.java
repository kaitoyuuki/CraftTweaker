/*
 * This file is part of MineTweaker API, licensed under the MIT License (MIT).
 * 
 * Copyright (c) 2014 MineTweaker <http://minetweaker3.powerofbytes.com>
 */
package org.openzen.zencode.parser.unit;

import java.util.List;
import org.openzen.zencode.parser.ParsedAnnotation;
import org.openzen.zencode.parser.expression.ParsedCallArguments;
import org.openzen.zencode.parser.member.IParsedMember;
import org.openzen.zencode.parser.modifier.IParsedModifier;
import org.openzen.zencode.util.CodePosition;

/**
 *
 * @author Stan
 */
public class ParsedEnum implements IParsedUnit
{
	private final CodePosition position;
	private final List<ParsedAnnotation> annotations;
	private final List<IParsedModifier> modifiers;
	private final String name;
	private final List<Value> values;
	private final List<IParsedMember> members;
	
	public ParsedEnum(
			CodePosition position,
			List<ParsedAnnotation> annotations,
			List<IParsedModifier> modifiers,
			String name,
			List<Value> values,
			List<IParsedMember> members)
	{
		this.position = position;
		this.annotations = annotations;
		this.modifiers = modifiers;
		this.name = name;
		this.values = values;
		this.members = members;
	}
	
	public static class Value
	{
		public final String name;
		public final ParsedCallArguments arguments;
		
		public Value(String name, ParsedCallArguments arguments)
		{
			this.name = name;
			this.arguments = arguments;
		}
	}
}
