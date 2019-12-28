package io.github.roguezilla.qol.enchants.mixins;

import io.github.roguezilla.qol.enchants.ModInit;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Mixin(Block.class)
public abstract class onBreakMixin {
	@Inject(at = @At(value = "HEAD"), method = "onBreak")
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo cbinfo) {
		ModInit.brokenBlockPos = pos;

		if(EnchantmentHelper.getEnchantments(player.inventory.getMainHandStack()).get(ModInit.SHAPED_MINING) != null) {
			for(HashMap.Entry<BlockPos, Boolean> blockInfo : getBlocksToBreak(world, player).entrySet()) {
				world.breakBlock(blockInfo.getKey(), !player.abilities.creativeMode);
				if(blockInfo.getValue() == true) {
					world.setBlockState(blockInfo.getKey(), state.getBlock().getDefaultState());
				}
			}
		}
	}

	private static HashMap<BlockPos, Boolean> getBlocksToBreak(World world, PlayerEntity player) {
		HashMap<BlockPos, Boolean> blocksToBreak = new HashMap<>();

		Vec3d cameraPos = ((Entity)player).getCameraPosVec(1);
		Vec3d rotation = ((Entity)player).getRotationVec(1);
		Vec3d combined = cameraPos.add(rotation.x * 5, rotation.y * 5, rotation.z * 5);

		BlockHitResult blockHitResult = world.rayTrace(new RayTraceContext(cameraPos, combined, RayTraceContext.ShapeType.OUTLINE, RayTraceContext.FluidHandling.NONE, player));
		if (blockHitResult.getType() == HitResult.Type.BLOCK) {
			Direction.Axis axis = blockHitResult.getSide().getAxis();
			BlockPos origin = blockHitResult.getBlockPos();

			HashMap<String, int[][]> coordsByAxis = new HashMap<>();
			coordsByAxis.put("y", new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, {1, 0, 1}, {-1, 0, -1}, {1, 0, -1}, {-1, 0, 1}});
			coordsByAxis.put("x", new int[][]{{0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}, {0, -1, 1}, {0, -1, -1}, {0, 1, 1}, {0, 1, -1}});
			coordsByAxis.put("z", new int[][]{{0, 1, 0}, {0, -1, 0}, {1, 0, 0}, {-1, 0, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 1, 0}, {-1, 1, 0}});
			
			for(int i = 0; i < coordsByAxis.get(axis.getName()).length; i++) {
				int[] arr;
				if(EnchantmentHelper.getEnchantments(MinecraftClient.getInstance().player.inventory.getMainHandStack()).get(ModInit.CROP_REPLANTER) != null) {
					arr = coordsByAxis.get("y")[i];
				} else {
					arr = coordsByAxis.get(axis.getName())[i];
				}
				BlockPos pos = origin.add(arr[0], arr[1], arr[2]);
				if(!(world.getBlockState(pos).getBlock() instanceof AirBlock)) {
					if(world.getBlockState(pos).getBlock() instanceof CropBlock) {
						blocksToBreak.put(pos, true);
					} else {
						blocksToBreak.put(pos, false);
					}
				}
			}
		}
		
		return blocksToBreak;
	}
}