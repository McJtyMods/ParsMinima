package mcjty.parsminima.client;

import mcjty.parsminima.api.PartSlot;
import mcjty.parsminima.common.MultipartTE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.MinecraftForgeClient;
import net.neoforged.neoforge.client.model.data.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.IModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;

public class MultipartBakedModel implements IDynamicBakedModel {

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    public static TextureAtlasSprite getTexture(ResourceLocation resource) {
        //noinspection deprecation
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(resource);
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull Random rand, @NotNull IModelData extraData) {
        if (state == null) {
            return Collections.emptyList();
        }

        Map<PartSlot, MultipartTE.Part> parts = extraData.getData(MultipartTE.PARTS);

        if (parts != null) {
            List<BakedQuad> quads = new ArrayList<>();
            RenderType layer = MinecraftForgeClient.getRenderType();

            for (Map.Entry<PartSlot, MultipartTE.Part> entry : parts.entrySet()) {
                MultipartTE.Part part = entry.getValue();
                BlockState blockState = part.getState();
                if (layer == null || ItemBlockRenderTypes.canRenderInLayer(blockState, layer)) {
                    BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(blockState);
                    try {
                        if (!(model instanceof MultipartBakedModel)) {  // @todo safety
                            quads.addAll(model.getQuads(state, side, rand));
                        }
                    } catch (Exception ignore) {
                        System.out.println("MultipartBakedModel.getQuads");
                    }
                }
            }
            return quads;
        }
        return Collections.emptyList();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return getTexture(new ResourceLocation("minecraft", "missingno"));  // @todo
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    @Nonnull
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }


}
