package io.github.roguezilla.qol.enchants.custom.enchantments;

import io.github.roguezilla.qol.enchants.ModInit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;

public class ShapedMining extends Enchantment {
    public ShapedMining() {
        super(Enchantment.Weight.RARE, EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        if(stack.getItem() instanceof HoeItem && EnchantmentHelper.getEnchantments(stack).get(ModInit.CROP_REPLANTER) != null) {
            return true;
        }
        return super.isAcceptableItem(stack);
    }

    @Override
    public int getMinimumPower(int level) {
        return 30;
    }

    @Override
    public int getMaximumLevel() {
        return 1;
    }
}