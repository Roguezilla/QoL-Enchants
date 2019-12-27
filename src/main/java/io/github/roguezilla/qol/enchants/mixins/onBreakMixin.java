package io.github.roguezilla.qol.enchants.mixins;

import io.github.roguezilla.qol.enchants.ModInit;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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

import java.util.HashMap;

@Mixin(Block.class)
public abstract class onBreakMixin {
	@Inject(at = @At(value = "HEAD"), method = "onBreak")
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo cbinfo) {
		ModInit.brokenBlockPos = pos;

		if(EnchantmentHelper.getEnchantments(player.inventory.getMainHandStack()).get(ModInit.SHAPED_MINING) != null) {
			for(HashMap.Entry<BlockPos, Block> blockInfo : getBlocksToBreak(world, player).entrySet()) {
				world.breakBlock(blockInfo.getKey(), !player.abilities.creativeMode);
				if(!(blockInfo.getValue() instanceof AirBlock)) {
					world.setBlockState(blockInfo.getKey(), blockInfo.getValue().getDefaultState());
				}
			}
		}
	}

	private static HashMap<BlockPos, Block> getBlocksToBreak(World world, PlayerEntity player) {
		HashMap<BlockPos, Block> blocksToBreak = new HashMap<BlockPos, Block>();
		
		Vec3d cameraPos = ((Entity)player).getCameraPosVec(1);
		Vec3d rotation = ((Entity)player).getRotationVec(1);
		Vec3d combined = cameraPos.add(rotation.x * 5, rotation.y * 5, rotation.z * 5);

		BlockHitResult blockHitResult = world.rayTrace(new RayTraceContext(cameraPos, combined, RayTraceContext.ShapeType.OUTLINE, RayTraceContext.FluidHandling.NONE, player));
		if (blockHitResult.getType() == HitResult.Type.BLOCK) {
			Direction.Axis axis = blockHitResult.getSide().getAxis();
			BlockPos origin = blockHitResult.getBlockPos();

			HashMap<String, int[][]> coordsByAxis = new HashMap<String, int[][]>();
			coordsByAxis.put("y", new int[][]{{1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, {1, 0, 1}, {-1, 0, -1}, {1, 0, -1}, {-1, 0, 1}});
			coordsByAxis.put("x", new int[][]{{0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}, {0, -1, 1}, {0, -1, -1}, {0, 1, 1}, {0, 1, -1}});
			coordsByAxis.put("z", new int[][]{{0, 1, 0}, {0, -1, 0}, {1, 0, 0}, {-1, 0, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 1, 0}, {-1, 1, 0}});
			
			for(int i = 0; i < coordsByAxis.get(axis.getName()).length; i++) {
				int[] arr = new int[3];
				if(EnchantmentHelper.getEnchantments(MinecraftClient.getInstance().player.inventory.getMainHandStack()).get(ModInit.CROP_REPLANTER) != null) {
					arr = coordsByAxis.get("y")[i];
				} else {
					arr = coordsByAxis.get(axis.getName())[i];
				}
				BlockPos pos = origin.add(arr[0], arr[1], arr[2]);
				if(!(world.getBlockState(pos).getBlock() instanceof AirBlock)) {
					if(world.getBlockState(pos).getBlock() instanceof CarrotsBlock) {
						if(world.getBlockState(pos).getEntries().get(((CropBlock)world.getBlockState(pos).getBlock()).getAgeProperty()).toString().equalsIgnoreCase(Integer.toString(((CropBlock)world.getBlockState(pos).getBlock()).getMaxAge()))) {
							blocksToBreak.put(pos, Blocks.CARROTS);
						}
					} else if (world.getBlockState(pos).getBlock() instanceof PotatoesBlock){
						if(world.getBlockState(pos).getEntries().get(((CropBlock)world.getBlockState(pos).getBlock()).getAgeProperty()).toString().equalsIgnoreCase(Integer.toString(((CropBlock)world.getBlockState(pos).getBlock()).getMaxAge()))) {
							blocksToBreak.put(pos, Blocks.POTATOES);
						}
					} else if (world.getBlockState(pos).getBlock() instanceof BeetrootsBlock){
						if(world.getBlockState(pos).getEntries().get(((CropBlock)world.getBlockState(pos).getBlock()).getAgeProperty()).toString().equalsIgnoreCase(Integer.toString(((CropBlock)world.getBlockState(pos).getBlock()).getMaxAge()))) {
							blocksToBreak.put(pos, Blocks.BEETROOTS);
						}
					} else if (world.getBlockState(pos).getBlock() instanceof CropBlock){
						if(world.getBlockState(pos).getEntries().get(((CropBlock)world.getBlockState(pos).getBlock()).getAgeProperty()).toString().equalsIgnoreCase(Integer.toString(((CropBlock)world.getBlockState(pos).getBlock()).getMaxAge()))) {
							blocksToBreak.put(pos, Blocks.WHEAT);
						}
					} else {
						blocksToBreak.put(pos, Blocks.AIR);
					}
				}
			}
		}
		
		return blocksToBreak;
	}
}