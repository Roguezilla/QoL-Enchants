package io.github.roguezilla.qol.enchants.custom.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;

public class CropReplanter extends Enchantment {
    public CropReplanter() {
        super(Enchantment.Weight.UNCOMMON, EnchantmentTarget.ALL, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }
    
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        if(stack.getItem() instanceof HoeItem) {
            return true;
        }

        return false;
    }

    @Override
    public int getMinimumPower(int level) {
        return 10;
    }

    @Override
    public int getMaximumLevel() {
        return 1;
    }
}