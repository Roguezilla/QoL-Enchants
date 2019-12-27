package io.github.roguezilla.qol.enchants.mixins;

import io.github.roguezilla.qol.enchants.ModInit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
public abstract class getDroppedStacksMixin {
	@Inject(at = @At(value="TAIL"), method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/loot/context/LootContext$Builder;)Ljava/util/List;", cancellable=true)
	public void getDroppedStacks(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cbinfo) {
		List<ItemStack> drops = cbinfo.getReturnValue();

		if(EnchantmentHelper.getEnchantments(MinecraftClient.getInstance().player.inventory.getMainHandStack()).get(ModInit.NATURES_GIFT) != null) {
			if (state.getBlock() instanceof CropBlock) {
				if (state.getEntries().get(((CropBlock) state.getBlock()).getAgeProperty()).toString().equalsIgnoreCase(Integer.toString(((CropBlock) state.getBlock()).getMaxAge()))) {
					for (ItemStack item : drops) {
						item.increment(item.getCount());
					}
				}
			}
		}
		if(EnchantmentHelper.getEnchantments(MinecraftClient.getInstance().player.inventory.getMainHandStack()).get(ModInit.CROP_REPLANTER) != null) {
			if(state.getBlock() instanceof CropBlock) {
				if(state.getEntries().get(((CropBlock)state.getBlock()).getAgeProperty()).toString().equalsIgnoreCase(Integer.toString(((CropBlock)state.getBlock()).getMaxAge()))) {
					builder.getWorld().setBlockState(ModInit.brokenBlockPos, Block.getBlockFromItem(drops.get(1).getItem()).getDefaultState());
					drops.get(1).decrement(1);
				}
			}
		}
		
		cbinfo.setReturnValue(drops);
	}
}