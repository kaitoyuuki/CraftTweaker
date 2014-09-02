/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zenscript.symbolic.type.casting;

import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.MethodOutput;
import zenscript.runtime.IAny;
import zenscript.symbolic.TypeRegistry;

/**
 *
 * @author Stan
 */
public class CastingAnyShort implements ICastingRule {
	private final TypeRegistry types;
	
	public CastingAnyShort(TypeRegistry types) {
		this.types = types;
	}
	
	@Override
	public void compile(MethodOutput output) {
		output.invokeInterface(IAny.class, "asShort", short.class);
	}

	@Override
	public ZenType getInputType() {
		return types.ANY;
	}

	@Override
	public ZenType getResultingType() {
		return types.SHORT;
	}
}