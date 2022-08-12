package mcjty.parsminima.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public interface IPartBlock {

    @Nonnull
    PartSlot getSlotFromState(Level world, BlockPos pos, BlockState newState);
}
