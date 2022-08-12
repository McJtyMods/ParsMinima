package mcjty.parsminima.api;

import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public interface IPartTileProvider {

    @Nullable
    IPartTile createTile(BlockEntity te);
}
