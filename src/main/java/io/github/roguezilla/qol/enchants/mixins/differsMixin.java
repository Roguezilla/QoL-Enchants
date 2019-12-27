package io.github.roguezilla.qol.enchants.mixins;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.InfinityEnchantment;
import net.minecraft.enchantment.MendingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InfinityEnchantment.class)
public abstract class differsMixin {
    @Inject(at = @At("HEAD"), method = "differs")
    private void differs(Enchantment other, CallbackInfoReturnable<Boolean> cbinfo) {
        if(other instanceof MendingEnchantment) cbinfo.setReturnValue(true);
    }
}
