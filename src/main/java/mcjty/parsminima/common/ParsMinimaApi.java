package mcjty.parsminima.common;

import mcjty.parsminima.api.IParsMinima;
import mcjty.parsminima.api.IPartTile;
import mcjty.parsminima.api.IPartTileProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ParsMinimaApi implements IParsMinima {

    private final List<IPartTileProvider> providers = new ArrayList<>();

    @Override
    public void registerTileProvider(IPartTileProvider provider) {
        providers.add(provider);
    }

    @Nullable
    IPartTile createTile(BlockEntity te) {
        for (IPartTileProvider provider : providers) {
            IPartTile tile = provider.createTile(te);
            if (tile != null) {
                return tile;
            }
        }
        return null;
    }

}
