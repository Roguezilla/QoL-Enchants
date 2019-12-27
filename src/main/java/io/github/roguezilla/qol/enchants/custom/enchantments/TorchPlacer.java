package io.github.roguezilla.qol.enchants.custom.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class TorchPlacer extends Enchantment {
    public TorchPlacer() {
        super(Enchantment.Weight.UNCOMMON, EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }
    
    @Override
    public int getMinimumPower(int level) {
        return 1;
    }

    @Override
    public int getMaximumLevel() {
        return 1;
    }
}