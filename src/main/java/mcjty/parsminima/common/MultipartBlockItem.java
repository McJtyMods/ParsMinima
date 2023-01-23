package mcjty.parsminima.common;

import mcjty.parsminima.ParsMinima;
import mcjty.parsminima.api.IPartBlock;
import mcjty.parsminima.api.PartSlot;
import mcjty.parsminima.setup.Registration;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MultipartBlockItem extends BlockItem {

    public MultipartBlockItem(Block block) {
        super(block, new Properties());
    }

    public MultipartBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean canPlace(@Nonnull BlockPlaceContext context, @Nonnull BlockState state) {
        // Return true to make this work all the time.
        return true;
    }


    @Nonnull
    @Override
    public InteractionResult place(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        ItemStack itemstack = context.getItemInHand();
        if (itemstack.isEmpty()) {
            return InteractionResult.FAIL;
        }

        BlockState toPlace = this.getBlock().getStateForPlacement(context);
        PartSlot slot = PartSlot.NONE;
        if (this.getBlock() instanceof IPartBlock) {
            slot = ((IPartBlock) this.getBlock()).getSlotFromState(world, pos, toPlace);
        }

        if (!block.canBeReplaced(state, context) && !canFitInside(block, world, pos, slot)) {
            pos = pos.relative(context.getClickedFace());
            state = world.getBlockState(pos);
            block = state.getBlock();
        }

//        context = BlockItemUseContext.at(context, pos, context.getFace());

        if (player.mayUseItemAt(pos, context.getClickedFace(), itemstack)) {
            // We have to call getStateForPlacement again to be sure it is ok for this position as well
            toPlace = this.getBlock().getStateForPlacement(context);
            if (this.getBlock() instanceof IPartBlock) {
                slot = ((IPartBlock) this.getBlock()).getSlotFromState(world, pos, toPlace);
            }

            if (canFitInside(block, world, pos, slot)) {
                if (placeBlockAtInternal(itemstack, player, world, pos, toPlace, slot)) {
                    SoundType soundtype = toPlace.getBlock().getSoundType(toPlace, world, pos, player);
                    world.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    itemstack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            } else if (true /* @todo 1.14 world.mayPlace(this.getBlock(), pos, false, facing, null)*/) {
                if (placeBlockAtInternal(itemstack, player, world, pos, toPlace, slot)) {
                    SoundType soundtype = toPlace.getBlock().getSoundType(toPlace, world, pos, player);
                    world.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                    itemstack.shrink(1);
                }

                return InteractionResult.SUCCESS;
            } else {
                return InteractionResult.FAIL;
            }
        } else {
            return InteractionResult.FAIL;
        }
    }

    private boolean canFitInside(Block block, Level world, BlockPos pos, PartSlot slot) {
        if (block != Registration.MULTIPART_BLOCK.get()) {
            return false;
        }
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof MultipartTE) {
            MultipartTE.Part part = ((MultipartTE) te).getParts().get(slot);
            return part == null;
        } else {
            return false;
        }
    }

//
//    @Override
//    public EnumActionResult onItemUse(PlayerEntity player, World world, BlockPos pos, Hand hand, Direction facing, float hitX, float hitY, float hitZ) {
//        ItemStack itemstack = player.getHeldItem(hand);
//        int meta = this.getMetadata(itemstack.getMetadata());
//        BlockState toPlace = block.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, player, hand);
//        boolean result = onItemUseHelper(player, world, pos, toPlace);
//
//
//
//        if (result) {
//            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
//        } else {
//            return EnumActionResult.FAIL;
//        }
//    }

    @Nullable
    private BlockEntity createTileEntity(BlockPos pos, BlockState state) {
        if (state.getBlock() instanceof EntityBlock entityBlock) {
            return entityBlock.newBlockEntity(pos, state);
        }
        return null;
    }

    @Override
    protected boolean placeBlock(@Nonnull BlockPlaceContext context, @Nonnull BlockState state) {
        // Not implemented
        return false;
    }

    private boolean placeBlockAtInternal(ItemStack stack, Player player, Level world, BlockPos pos, BlockState newState,
                                         @Nonnull PartSlot slot) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof MultipartTE) {
            BlockEntity tileEntity = createTileEntity(pos, newState);
//            if (tileEntity instanceof GenericTileEntity && stack.getTag() != null) {
//                // @todo how to do this?
////                ((GenericTileEntity) tileEntity).readRestorableFromNBT(stack.getTag());
//            }
            ((MultipartTE) te).addPart(slot, newState, ParsMinima.instance.api.createTile(tileEntity));
            return true;
        }

        BlockState multiState = Registration.MULTIPART_BLOCK.get().defaultBlockState();
        if (!world.setBlock(pos, multiState, Block.UPDATE_CLIENTS + Block.UPDATE_NEIGHBORS + Block.UPDATE_KNOWN_SHAPE)) {
            return false;
        }

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() == Registration.MULTIPART_BLOCK.get()) {
            updateCustomBlockEntityTag(world, player, pos, stack);

            te = world.getBlockEntity(pos);
            if (te instanceof MultipartTE) {
                BlockEntity tileEntity = createTileEntity(pos, newState);
//                if (tileEntity instanceof GenericTileEntity && stack.hasTag()) {
//                    // @todo how to do this?
////                    ((GenericTileEntity) tileEntity).readRestorableFromNBT(stack.getTag());
//                }
                ((MultipartTE) te).addPart(slot, newState, ParsMinima.instance.api.createTile(tileEntity));
                return true;
            }

            newState.getBlock().setPlacedBy(world, pos, newState, player, stack);

            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pos, stack);
            }
        }

        return true;
    }


//    @Override
//    public boolean placeBlockAt(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, BlockState newState) {
//        if (!world.isRemote) {
//            Block block1 = CommonProxy.multipartBlock;
//            boolean placed = false;
//            if (world.getBlockState(pos).getBlock() != block1) {
//                placed = true;
//                if (!world.setBlockState(pos, block1.getDefaultState(), 3)) {
//                    return false;
//                }
//            }
//
//            BlockState state = world.getBlockState(pos);
//            if (state.getBlock() == block1) {
//                if (placed) {
//                    block1.onBlockPlacedBy(world, pos, state, player, stack);
//                }
//
////                addCable(world, pos, side, hitX, hitY, hitZ);
//            }
//        }
//
//        return true;
//    }


    // private RayTraceResult getMovingObjectPositionFromPlayer(Level worldIn, Player playerIn, boolean useLiquids) {
        // float pitch = playerIn.getXRot();
        // float yaw = playerIn.getYRot();
        // double x = playerIn.getX();
        // double y = playerIn.getY() + playerIn.getEyeHeight();
        // double z = playerIn.getZ();
        // Vector3d vec3 = new Vector3d(x, y, z);
        // float f2 = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        // float f3 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        // float f4 = -MathHelper.cos(-pitch * 0.017453292F);
        // float f5 = MathHelper.sin(-pitch * 0.017453292F);
        // float f6 = f3 * f4;
        // float f7 = f2 * f4;
        // double reach = 5.0D;
        // if (playerIn instanceof ServerPlayer serverPlayer) {
            // @todo 1.14
        //    reach = serverPlayer.interactionManager.getBlockReachDistance();
        // }
        // Vector3d vec31 = vec3.add(f6 * reach, f5 * reach, f7 * reach);
        // RayTraceContext context = new RayTraceContext(vec3, vec31, RayTraceContext.BlockMode.COLLIDER, useLiquids ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, playerIn);
        // return worldIn.clip(context);
    // }

}
