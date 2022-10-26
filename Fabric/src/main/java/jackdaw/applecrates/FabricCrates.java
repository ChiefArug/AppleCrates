package jackdaw.applecrates;

import jackdaw.applecrates.api.AppleCrateAPI;
import jackdaw.applecrates.api.CrateWoodType;
import jackdaw.applecrates.block.CrateBlock;
import jackdaw.applecrates.block.blockentity.CrateBE;
import jackdaw.applecrates.container.CrateMenu;
import jackdaw.applecrates.item.CrateItem;
import jackdaw.applecrates.network.ServerNetwork;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class FabricCrates implements ModInitializer {
    private static final ScreenHandlerRegistry.ExtendedClientHandlerFactory<CrateMenu> factory = (syncId, inventory, buf) -> {
        boolean owner = buf.readBoolean();
        boolean unlimited = buf.readBoolean();
        return new CrateMenu(syncId, inventory, owner, unlimited);
    };

    public static final ExtendedScreenHandlerType<CrateMenu> CRATETYPE = new ExtendedScreenHandlerType<>(factory);

    @Override
    public void onInitialize() {
        AppleCrateAPI.AppleCrateBuilder.registerVanilla();

        Registry.register(Registry.MENU, new ResourceLocation(Constants.MODID, "cratemenu"), CRATETYPE);

        CrateWoodType.values().filter(crateWoodType -> crateWoodType.getYourModId().equals(Constants.MODID)).forEach(crateWoodType -> {
            var crate = new CrateBlock(crateWoodType);
            Registry.register(Registry.BLOCK, new ResourceLocation(Constants.MODID, crateWoodType.getBlockRegistryName()), crate);
            Registry.register(Registry.ITEM, new ResourceLocation(Constants.MODID, crateWoodType.getBlockRegistryName()), new CrateItem(crate));
            Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(Constants.MODID, crateWoodType.getBeRegistryName()), BlockEntityType.Builder.of((blockPos, blockState) -> new CrateBE(crateWoodType, blockPos, blockState)).build(null));
        });

        ServerNetwork.registerServerPackets();
    }
}
