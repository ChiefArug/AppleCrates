package jackdaw.applecrates.container;

import jackdaw.applecrates.Constants;
import jackdaw.applecrates.api.GeneralRegistry;
import jackdaw.applecrates.block.blockentity.CrateBE;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CrateMenuBuyer extends CrateMenu {

    //Registry overload for menu type registry
    //client
    public CrateMenuBuyer(int id, Inventory inventory, boolean unlimited) {
        super(GeneralRegistry.CRATE_MENU_BUYER.get(), id, inventory, unlimited);
    }

    public CrateMenuBuyer(int id, Inventory inventory, CrateBE crate, boolean unlimited) {
        super(GeneralRegistry.CRATE_MENU_BUYER.get(), id, inventory, crate, unlimited);
    }

    @Override
    protected int playerInventoryX() {
        return 8;
    }

    @Override
    protected int playerInventoryY() {
        return 57;
    }

    @Override
    public void clicked(int slotID, int mouseButton, ClickType click, Player player) {
        if (slotID == Constants.PAYSLOT) {
            super.clicked(slotID, mouseButton, click, player);
            updateSellItem();
        } else if (slotID == Constants.OUTSLOT) {
            if (mouseButton != 0
                    || (click != (ClickType.PICKUP))
                    || interactableTradeSlots.getStackInSlot(1).isEmpty()
                    || getCarried().getCount() + interactableTradeSlots.getStackInSlot(1).getCount() >= getCarried().getMaxStackSize()
            )
                return;
            super.clicked(slotID, mouseButton, click, player);
            interactableTradeSlots.getStackInSlot(0).shrink(savedTradeSlots.getStackInSlot(0).getCount());
            crateStock.updateStackInPaymentSlot(savedTradeSlots.getStackInSlot(0), isUnlimitedShop);
            updateSellItem();
        } else {
            super.clicked(slotID, mouseButton, click, player);
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        //if payment is updated or a stack from the player inventory is quickmoved into interactables
        if ((Constants.isInPlayerInventory(index) && this.moveItemStackTo(slots.get(index).getItem(), Constants.PAYSLOT, Constants.OUTSLOT, false))) {
            moveItemStackTo(slots.get(index).getItem(), Constants.PAYSLOT, Constants.OUTSLOT, false);
            updateSellItem();
        } else if (index == Constants.PAYSLOT) {
            moveItemStackTo(interactableTradeSlots.getStackInSlot(0), Constants.PLAYERSTARTSLOT, Constants.PLAYERENDSLOT, false);
            updateSellItem();
        }
        //NO SHIFT CLICKING YET
        //nota : quickmove works in tandem with 'clickd', which alreayd deducts payment.
//        else if (index == Constants.OUTSLOT) {
//            ItemStack prepPayChange = interactableTradeSlots.getStackInSlot(0).copy();
//            prepPayChange.shrink(savedTradeSlots.getStackInSlot(0).getCount());
//            interactableTradeSlots.setStackInSlot(0, prepPayChange);
//            crateStock.updateStackInPaymentSlot(savedTradeSlots.getStackInSlot(0), isUnlimitedShop);
//            if (!interactableTradeSlots.getStackInSlot(0).isEmpty()) {
//                moveItemStackTo(interactableTradeSlots.getStackInSlot(1), Constants.PLAYERSTARTSLOT, Constants.PLAYERENDSLOT, false);
//                updateSellItem();
//                return ItemStack.EMPTY; //keep looping till pay is empty
//            }
//        }
        return super.quickMoveStack(player, index);
    }
}
