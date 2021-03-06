package minetweaker.mc1102.recipes;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.player.IPlayer;
import minetweaker.api.recipes.*;
import minetweaker.mc1102.util.MineTweakerHacks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import stanhebben.zenscript.annotations.Optional;

import java.util.ArrayList;
import java.util.List;

import static minetweaker.api.minecraft.MineTweakerMC.*;

/**
 * @author Stan
 */
public class MCRecipeManager implements IRecipeManager {
	
	private final List<IRecipe> recipes;
	private final List<ICraftingRecipe> transformerRecipes;
	
	public MCRecipeManager() {
		recipes = CraftingManager.getInstance().getRecipeList();
		transformerRecipes = new ArrayList<>();
	}
	
	private static boolean matches(Object input, IIngredient ingredient) {
		if((input == null) != (ingredient == null)) {
			return false;
		} else if(ingredient != null) {
			if(input instanceof ItemStack) {
				if(!ingredient.matches(getIItemStack((ItemStack) input))) {
					return false;
				}
			} else if(input instanceof String) {
				if(!ingredient.contains(getOreDict((String) input))) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean hasTransformerRecipes() {
		return transformerRecipes.size() > 0;
	}
	
	public void applyTransformations(ICraftingInventory inventory, IPlayer byPlayer) {
		for(ICraftingRecipe recipe : transformerRecipes) {
			if(recipe.matches(inventory)) {
				recipe.applyTransformers(inventory, byPlayer);
				return;
			}
		}
	}
	
	@Override
	public List<ICraftingRecipe> getRecipesFor(IIngredient ingredient) {
		List<ICraftingRecipe> results = new ArrayList<>();
		
		recipes.stream().filter(recipe -> ingredient.matches(MineTweakerMC.getIItemStack(recipe.getRecipeOutput()))).forEach(recipe -> {
			ICraftingRecipe converted = RecipeConverter.toCraftingRecipe(recipe);
			results.add(converted);
		});
		
		return results;
	}
	
	@Override
	public List<ICraftingRecipe> getAll() {
		List<ICraftingRecipe> results = new ArrayList<>();
		
		for(IRecipe recipe : recipes) {
			ICraftingRecipe converted = RecipeConverter.toCraftingRecipe(recipe);
			results.add(converted);
		}
		
		return results;
	}
	
	@Override
	public int remove(IIngredient output, @Optional boolean nbtMatch) {
		List<IRecipe> toRemove = new ArrayList<>();
		List<Integer> removeIndex = new ArrayList<>();
		for(int i = 0; i < recipes.size(); i++) {
			IRecipe recipe = recipes.get(i);
			
			// certain special recipes have no predefined output. ignore those
			// since these cannot be removed with MineTweaker scripts
			if(recipe.getRecipeOutput() != null) {
				if(nbtMatch ? output.matchesExact(getIItemStack(recipe.getRecipeOutput())) : output.matches(getIItemStack(recipe.getRecipeOutput()))) {
					toRemove.add(recipe);
					removeIndex.add(i);
				}
			}
		}
		
		MineTweakerAPI.apply(new ActionRemoveRecipes(toRemove, removeIndex));
		return toRemove.size();
	}
	
	@Override
	public void addShaped(IItemStack output, IIngredient[][] ingredients, @Optional IRecipeFunction function) {
		addShaped(output, ingredients, function, false);
	}
	
	@Override
	public void addShapedMirrored(IItemStack output, IIngredient[][] ingredients, @Optional IRecipeFunction function) {
		addShaped(output, ingredients, function, true);
	}
	
	@Override
	public void addShapeless(IItemStack output, IIngredient[] ingredients, @Optional IRecipeFunction function) {
		ShapelessRecipe recipe = new ShapelessRecipe(output, ingredients, function);
		IRecipe irecipe = RecipeConverter.convert(recipe);
		MineTweakerAPI.apply(new ActionAddRecipe(irecipe, recipe));
	}
	
	@Override
	public int removeShaped(IIngredient output, @Optional IIngredient[][] ingredients) {
		int ingredientsWidth = 0;
		int ingredientsHeight = 0;
		
		if(ingredients != null) {
			ingredientsHeight = ingredients.length;
			
			for(IIngredient[] ingredient : ingredients) {
				ingredientsWidth = Math.max(ingredientsWidth, ingredient.length);
			}
		}
		
		List<IRecipe> toRemove = new ArrayList<>();
		List<Integer> removeIndex = new ArrayList<>();
		outer:
		for(int i = 0; i < recipes.size(); i++) {
			IRecipe recipe = recipes.get(i);
			
			if(recipe.getRecipeOutput() == null || !output.matches(getIItemStack(recipe.getRecipeOutput()))) {
				continue;
			}
			if(!(recipe instanceof ShapedRecipes) && !(recipe instanceof ShapedOreRecipe)){
				continue;
			}
			if(ingredients != null) {
				if(recipe instanceof ShapedRecipes) {
					ShapedRecipes srecipe = (ShapedRecipes) recipe;
					if(ingredientsWidth != srecipe.recipeWidth || ingredientsHeight != srecipe.recipeHeight) {
						continue outer;
					}
					
					for(int j = 0; j < ingredientsHeight; j++) {
						IIngredient[] row = ingredients[j];
						for(int k = 0; k < ingredientsWidth; k++) {
							IIngredient ingredient = k > row.length ? null : row[k];
							ItemStack recipeIngredient = srecipe.recipeItems[j * srecipe.recipeWidth + k];
							
							if(!matches(recipeIngredient, ingredient)) {
								continue outer;
							}
						}
					}
				} else if(recipe instanceof ShapedOreRecipe) {
					ShapedOreRecipe srecipe = (ShapedOreRecipe) recipe;
					int recipeWidth = MineTweakerHacks.getShapedOreRecipeWidth(srecipe);
					int recipeHeight = srecipe.getRecipeSize() / recipeWidth;
					if(ingredientsWidth != recipeWidth || ingredientsHeight != recipeHeight) {
						continue outer;
					}
					
					for(int j = 0; j < ingredientsHeight; j++) {
						IIngredient[] row = ingredients[j];
						for(int k = 0; k < ingredientsWidth; k++) {
							IIngredient ingredient = k > row.length ? null : row[k];
							Object input = srecipe.getInput()[j * recipeWidth + k];
							if(!matches(input, ingredient)) {
								continue outer;
							}
						}
					}
				} else {
					continue outer;
				}
			} else {
				continue outer;
			}
			
			toRemove.add(recipe);
			removeIndex.add(i);
		}
		
		MineTweakerAPI.apply(new ActionRemoveRecipes(toRemove, removeIndex));
		return toRemove.size();
	}
	
	@Override
	public int removeShapeless(IIngredient output, @Optional IIngredient[] ingredients, @Optional boolean wildcard) {
		List<IRecipe> toRemove = new ArrayList<>();
		List<Integer> removeIndex = new ArrayList<>();
		outer:
		for(int i = 0; i < recipes.size(); i++) {
			IRecipe recipe = recipes.get(i);
			
			if(recipe.getRecipeOutput() == null || !output.matches(getIItemStack(recipe.getRecipeOutput()))) {
				continue;
			}
			if(!(recipe instanceof ShapelessOreRecipe) && !(recipe instanceof ShapelessRecipe)){
				continue;
			}
			
			if(ingredients != null) {
				if(recipe instanceof ShapelessRecipes) {
					ShapelessRecipes srecipe = (ShapelessRecipes) recipe;
					
					if(ingredients.length > srecipe.getRecipeSize()) {
						continue;
					} else if(!wildcard && ingredients.length < srecipe.getRecipeSize()) {
						continue;
					}
					
					checkIngredient:
					for(IIngredient ingredient : ingredients) {
						for(int k = 0; k < srecipe.getRecipeSize(); k++) {
							if(matches(srecipe.recipeItems.get(k), ingredient)) {
								continue checkIngredient;
							}
						}
						
						continue outer;
					}
				} else if(recipe instanceof ShapelessOreRecipe) {
					ShapelessOreRecipe srecipe = (ShapelessOreRecipe) recipe;
					ArrayList<Object> inputs = srecipe.getInput();
					
					if(inputs.size() < ingredients.length) {
						continue;
					}
					if(!wildcard && inputs.size() > ingredients.length) {
						continue;
					}
					
					checkIngredient:
					for(IIngredient ingredient : ingredients) {
						for(int k = 0; k < srecipe.getRecipeSize(); k++) {
							if(matches(inputs.get(k), ingredient)) {
								continue checkIngredient;
							}
						}
						
						continue outer;
					}
				} else {
					continue outer;
				}
			} else {
				continue outer;
			}
			
			toRemove.add(recipe);
			removeIndex.add(i);
		}
		
		MineTweakerAPI.apply(new ActionRemoveRecipes(toRemove, removeIndex));
		return toRemove.size();
	}
	
	@Override
	public IItemStack craft(IItemStack[][] contents) {
		Container container = new ContainerVirtual();
		
		int width = 0;
		int height = contents.length;
		for(IItemStack[] row : contents) {
			width = Math.max(width, row.length);
		}
		
		ItemStack[] iContents = new ItemStack[width * height];
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < contents[i].length; j++) {
				if(contents[i][j] != null) {
					iContents[i * width + j] = getItemStack(contents[i][j]);
				}
			}
		}
		
		InventoryCrafting inventory = new InventoryCrafting(container, width, height);
		for(int i = 0; i < iContents.length; i++) {
			inventory.setInventorySlotContents(i, iContents[i]);
		}
		ItemStack result = CraftingManager.getInstance().findMatchingRecipe(inventory, null);
		if(result == null) {
			return null;
		} else {
			return getIItemStack(result);
		}
	}
	
	private void addShaped(IItemStack output, IIngredient[][] ingredients, IRecipeFunction function, boolean mirrored) {
		ShapedRecipe recipe = new ShapedRecipe(output, ingredients, function, mirrored);
		IRecipe irecipe = RecipeConverter.convert(recipe);
		MineTweakerAPI.apply(new ActionAddRecipe(irecipe, recipe));
	}
	
	private class ContainerVirtual extends Container {
		
		@Override
		public boolean canInteractWith(EntityPlayer var1) {
			return false;
		}
	}
	
	private class ActionRemoveRecipes implements IUndoableAction {
		
		private final List<Integer> removingIndices;
		private final List<IRecipe> removingRecipes;
		
		public ActionRemoveRecipes(List<IRecipe> recipes, List<Integer> indices) {
			this.removingIndices = indices;
			this.removingRecipes = recipes;
		}
		
		@Override
		public void apply() {
			for(int i = removingIndices.size() - 1; i >= 0; i--) {
				recipes.remove((int) removingIndices.get(i));
			}
		}
		
		@Override
		public boolean canUndo() {
			return true;
		}
		
		@Override
		public void undo() {
			for(int i = 0; i < removingIndices.size(); i++) {
				int index = Math.min(recipes.size(), removingIndices.get(i));
				recipes.add(index, removingRecipes.get(i));
			}
		}
		
		@Override
		public String describe() {
			return "Removing " + removingIndices.size() + " recipes";
		}
		
		@Override
		public String describeUndo() {
			return "Restoring " + removingIndices.size() + " recipes";
		}
		
		@Override
		public Object getOverrideKey() {
			return null;
		}
	}
	
	private class ActionAddRecipe implements IUndoableAction {
		
		private final IRecipe recipe;
		private final ICraftingRecipe craftingRecipe;
		
		public ActionAddRecipe(IRecipe recipe, ICraftingRecipe craftingRecipe) {
			this.recipe = recipe;
			this.craftingRecipe = craftingRecipe;
		}
		
		@Override
		public void apply() {
			recipes.add(recipe);
			if(craftingRecipe.hasTransformers()) {
				transformerRecipes.add(craftingRecipe);
			}
		}
		
		@Override
		public boolean canUndo() {
			return true;
		}
		
		@Override
		public void undo() {
			recipes.remove(recipe);
			if(craftingRecipe.hasTransformers()) {
				transformerRecipes.remove(craftingRecipe);
			}
		}
		
		@Override
		public String describe() {
			return "Adding recipe for " + recipe.getRecipeOutput().getDisplayName();
		}
		
		@Override
		public String describeUndo() {
			return "Removing recipe for " + recipe.getRecipeOutput().getDisplayName();
		}
		
		@Override
		public Object getOverrideKey() {
			return null;
		}
	}
}
