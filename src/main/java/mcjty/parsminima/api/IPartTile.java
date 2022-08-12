package mcjty.parsminima.api;

import mcjty.parsminima.common.MultipartTE;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface IPartTile {
    void onPartAdded(PartSlot slot, BlockState state, IMultipart multipartTE);

    InteractionResult onBlockActivated(BlockState state, Player player, InteractionHand hand, BlockHitResult result);

    void onReplaced(Level level, BlockPos blockPos, BlockState hitState, BlockState hitState1);

    void load(CompoundTag tc);

    void setLevel(Level worldIn);

    CompoundTag saveWithoutMetadata();
}
