package jackdaw.applecrates.container.inventory;

import jackdaw.applecrates.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CrateStackHandler extends ItemStackHandler implements ICrateStock {

    private final Map<Item, Integer> itemCountCache = new HashMap<>();

    public CrateStackHandler() {
        super(Constants.TOTALCRATESLOTS);
    }

    public int getCountOfItemCached(Item item) {
        return this.itemCountCache.computeIfAbsent(item, $ -> getCountOfItemImmediately(item));
    }

    @Override
    public int getCountOfItemImmediately(Item item) {
        int count = 0;
        for (int i = 0; i < this.getSlots(); i++) {
            var stack = this.getStackInSlot(i);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public boolean updateStackInPaymentSlot(ItemStack payment, boolean isUnlimitedShop) {
        if (isUnlimitedShop)
            return true;

        ItemStack prepPay = payment.copy();

        if (getStackInSlot(Constants.TOTALCRATESTOCKLOTS).isEmpty()) {
            prepPay.setCount(1);
            setStackInSlot(Constants.TOTALCRATESTOCKLOTS, prepPay);
        }

        //remove custom tag from money slot stack for comparison with 'virgin' item in the savedStack slot
        ItemStack paymentCompare = getStackInSlot(Constants.TOTALCRATESTOCKLOTS).copy();
        if (paymentCompare.hasTag() && paymentCompare.getTag().contains(Constants.TAGSTOCK)) {
            paymentCompare.removeTagKey(Constants.TAGSTOCK);
        }

        if (!ItemStack.isSameItemSameTags(payment, paymentCompare))
            return false;

        ItemStack prepXchange = getStackInSlot(Constants.TOTALCRATESTOCKLOTS).copy();
        CompoundTag tag = prepXchange.getOrCreateTag();
        if (tag.contains(Constants.TAGSTOCK)) {
            tag.putInt(Constants.TAGSTOCK, tag.getInt(Constants.TAGSTOCK) + payment.getCount());
        } else {
            tag.putInt(Constants.TAGSTOCK, payment.getCount());
        }
        setStackInSlot(Constants.TOTALCRATESTOCKLOTS, prepXchange);
        return true;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (slot == Constants.TOTALCRATESTOCKLOTS)
            return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot == Constants.TOTALCRATESTOCKLOTS)
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return slot != Constants.TOTALCRATESTOCKLOTS && super.isItemValid(slot, stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        this.itemCountCache.clear();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        this.itemCountCache.clear();
    }
}
