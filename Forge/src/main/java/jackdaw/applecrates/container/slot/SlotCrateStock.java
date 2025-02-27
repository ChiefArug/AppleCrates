package jackdaw.applecrates.container.slot;

import jackdaw.applecrates.Constants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class SlotCrateStock extends SlotItemHandler {
    private final boolean isOwner;

    public SlotCrateStock(IItemHandler itemHandler, int index, int xPosition, int yPosition, boolean isOwner) {
        super(itemHandler, index, xPosition, yPosition);
        this.isOwner = isOwner;
    }

    @Override
    public boolean isActive() {
        return isOwner;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return getSlotIndex() < Constants.TOTALCRATESTOCKLOTS;
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return getSlotIndex() != Constants.TOTALCRATESTOCKLOTS && isOwner;
    }
}
