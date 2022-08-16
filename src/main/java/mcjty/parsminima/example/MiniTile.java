package mcjty.parsminima.example;

import mcjty.parsminima.common.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MiniTile extends BlockEntity {

    public MiniTile(BlockPos pos, BlockState state) {
        super(Registration.MINI_TILE.get(), pos, state);
    }
}
