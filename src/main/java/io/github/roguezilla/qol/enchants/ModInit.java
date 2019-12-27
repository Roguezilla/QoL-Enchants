package io.github.roguezilla.qol.enchants;

import io.github.roguezilla.qol.enchants.custom.enchantments.CropReplanter;
import io.github.roguezilla.qol.enchants.custom.enchantments.NaturesGift;
import io.github.roguezilla.qol.enchants.custom.enchantments.ShapedMining;
import io.github.roguezilla.qol.enchants.custom.enchantments.TorchPlacer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class ModInit implements ModInitializer {
    public static final String MODID = "qol";

    public static final Enchantment SHAPED_MINING = new ShapedMining();
    public static final Enchantment TORCH_PLACER = new TorchPlacer();
    public static final Enchantment CROP_REPLANTER = new CropReplanter();
    public static final Enchantment NATURES_GIFT = new NaturesGift();

    public static BlockPos brokenBlockPos;
    
    @Override
    public void onInitialize() {
        Registry.register(Registry.ENCHANTMENT, new Identifier(MODID, "shaped_mining"), SHAPED_MINING);
        Registry.register(Registry.ENCHANTMENT, new Identifier(MODID, "torch_placer"), TORCH_PLACER);
        Registry.register(Registry.ENCHANTMENT, new Identifier(MODID, "crop_replanter"), CROP_REPLANTER);
        Registry.register(Registry.ENCHANTMENT, new Identifier(MODID, "natures_gift"), NATURES_GIFT);

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            if(EnchantmentHelper.getEnchantments(stack).get(TORCH_PLACER) != null)  {
                Vec3d cameraPos = ((Entity)player).getCameraPosVec(1);
                Vec3d rotation = ((Entity)player).getRotationVec(1);
                Vec3d combined = cameraPos.add(rotation.x * 5, rotation.y * 5, rotation.z * 5);
                BlockHitResult blockHitResult = world.rayTrace(new RayTraceContext(cameraPos, combined, RayTraceContext.ShapeType.OUTLINE, RayTraceContext.FluidHandling.NONE, player));
                if (blockHitResult.getType() == HitResult.Type.BLOCK && world.getBlockState(blockHitResult.getBlockPos()).getBlock() != Blocks.TORCH && world.getBlockState(blockHitResult.getBlockPos()).getBlock() != Blocks.WALL_TORCH) {
                    switch(blockHitResult.getSide().getAxis().getName()) {
                        case "y": {
                            BlockPos newpos = new BlockPos(blockHitResult.getBlockPos().getX(), blockHitResult.getBlockPos().getY() + 1, blockHitResult.getBlockPos().getZ());
                            if(world.getBlockState(newpos).getBlock() instanceof AirBlock) {
                                world.setBlockState(newpos, Blocks.TORCH.getDefaultState());
                            }
                            break;
                        }
                        case "x": {
                            BlockPos newpos = new BlockPos(blockHitResult.getBlockPos().getX() + (((Entity)player).getHorizontalFacing().getName() == "west" ? 1 : -1), blockHitResult.getBlockPos().getY(), blockHitResult.getBlockPos().getZ());
                            System.out.println(world.getBlockState(newpos).getBlock());
                            if(world.getBlockState(newpos).getBlock() instanceof AirBlock) {
                                world.setBlockState(newpos, Blocks.WALL_TORCH.getDefaultState().with(HorizontalFacingBlock.FACING, blockHitResult.getSide()));
                            }
                            break;
                        }
                        case "z": {
                            BlockPos newpos = new BlockPos(blockHitResult.getBlockPos().getX(), blockHitResult.getBlockPos().getY(), blockHitResult.getBlockPos().getZ() + (((Entity)player).getHorizontalFacing().getName() == "south" ? -1 : 1));
                            if(world.getBlockState(newpos).getBlock() instanceof AirBlock) {
                                world.setBlockState(newpos, Blocks.WALL_TORCH.getDefaultState().with(HorizontalFacingBlock.FACING, blockHitResult.getSide()));
                            }
                            break;
                        }
                    }
                    if(!player.abilities.creativeMode) {
                        ItemStack torches = player.inventory.getInvStack(player.inventory.getSlotWithStack(new ItemStack(Items.TORCH)));
                        torches.setCount(torches.getCount() - 1);
                        player.inventory.updateItems();
                    }
                }
            }

            return TypedActionResult.pass(stack);
        });
    }
}