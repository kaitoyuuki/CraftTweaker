package minetweaker.mods.jei;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.MineTweakerImplementationAPI;
import minetweaker.annotations.OnRegister;
import minetweaker.api.item.IItemStack;
import minetweaker.util.IEventHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.LinkedList;

import static minetweaker.api.minecraft.MineTweakerMC.getItemStack;
import static minetweaker.mods.jei.JEIAddonPlugin.jeiHelpers;

/**
 * MineTweaker JEI support.
 * <p>
 * Enables hiding JEI items as well as adding new item stacks. These item stacks
 * can then show a custom message or contain NBT data. Can be used to show a
 * custom message or lore with certain items, or to provide spawnable items with
 * specific NBT tags.
 *
 * @author Stan Hebben
 */
@ZenClass("mods.jei.JEI")
public class JEI {

    /**
     * Hides a specific item in JEI. Will take into account metadata values, if
     * any.
     *
     * @param item item to be hidden
     */
    @ZenMethod
    public static void hide(@NotNull IItemStack item) {
        MineTweakerAPI.apply(new JEIHideItemAction(item));
    }

    /**
     * Register callbacks otherwise the load order with JEI is all messed up.
     */
    @OnRegister
    public static void onRegister() {
        // discard all not yet applied actions before a reload
        MineTweakerImplementationAPI.onReloadEvent(new IEventHandler<MineTweakerImplementationAPI.ReloadEvent>() {
            @Override
            public void handle(MineTweakerImplementationAPI.ReloadEvent event) {
                apply.clear();
            }
        });

        // after the reload JEI needs to be reloaded as well
        MineTweakerImplementationAPI.onPostReload(new IEventHandler<MineTweakerImplementationAPI.ReloadEvent>() {
            @Override
            public void handle(MineTweakerImplementationAPI.ReloadEvent event) {
                if (Loader.isModLoaded("JEI")) {
                    try {
                        if (Class.forName("minetweaker.mods.jei.JEIAddonPlugin") != null) {
                            System.out.println("class exists");
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (minetweaker.mods.jei.JEIAddonPlugin.jeiHelpers != null) {
                        //this is such a lazy fix.
                        //minetweaker.mods.jei.JEIAddonPlugin.jeiHelpers.reload();
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public void onLoadComplete(FMLLoadCompleteEvent e) {
        //there's no way in hell this is all I have to do.
        if (JEIAddonPlugin.jeiHelpers != null) {
            JEIAddonPlugin.jeiHelpers.reload();
        }
    }

    // list of all hide actions that need to be applied after JEI is available
    private static LinkedList<JEIHideItemAction> apply = new LinkedList<JEIHideItemAction>();

    /**
     * JEI is available and the Hiding of the actions can now be applied.
     */
    public static void onJEIStarted() {
        for (JEIHideItemAction action : apply) {
            action.doApply();
        }
        apply.clear();
    }

    private static class JEIHideItemAction implements IUndoableAction {
        private final IItemStack stack;

        public JEIHideItemAction(IItemStack stack) {
            this.stack = stack;
        }

        @Override
        public void apply() {
            // just register until the JEI addon is registered, otherwise the jeiHelpers are null
            apply.add(this);
        }

        /**
         * Really adds the item to the blacklist when JEI is available
         */
        void doApply() {
            if (jeiHelpers != null) {
                jeiHelpers.getItemBlacklist().addItemToBlacklist(getItemStack(stack));
            } else {
                // Should not happen, but in case only log an error
                MineTweakerImplementationAPI.logger.logError("JEI not initialized yet!");
            }
        }

        @Override
        public boolean canUndo() {
            return true;
        }

        @Override
        public void undo() {
            // Doesn't need to be called since on reload of JEI the blacklist will be cleared anyway
            // jeiHelpers.getItemBlacklist().removeItemFromBlacklist(getItemStack(stack));
        }

        @Override
        public String describe() {
            return "Hiding " + stack + " in JEI";
        }

        @Override
        public String describeUndo() {
            return "Displaying " + stack + " in JEI";
        }

        @Override
        public Object getOverrideKey() {
            return null;
        }
    }
}
