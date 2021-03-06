/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package minetweaker.api.vanilla;

import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;

/**
 *
 * @author Stan
 */
@ZenClass("vanilla.IVanilla")
public interface IVanilla {
	@ZenGetter("loot")
    ILootRegistry getLoot();

	@ZenGetter("seeds")
    ISeedRegistry getSeeds();
}
