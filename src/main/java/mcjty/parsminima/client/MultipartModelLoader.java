package mcjty.parsminima.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import mcjty.parsminima.ParsMinima;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.client.event.ModelRegistryEvent;
import net.neoforged.neoforge.client.model.IModelConfiguration;
import net.neoforged.neoforge.client.model.IModelLoader;
import net.neoforged.neoforge.client.model.ModelLoaderRegistry;
import net.neoforged.neoforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class MultipartModelLoader implements IModelLoader<MultipartModelLoader.MultipartModelGeometry> {

    public static void register(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(new ResourceLocation(ParsMinima.MODID, "multipartloader"), new MultipartModelLoader());
    }

    @Override
    public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {

    }

    @Nonnull
    @Override
    public MultipartModelGeometry read(@Nonnull JsonDeserializationContext deserializationContext, @Nonnull JsonObject modelContents) {
        return new MultipartModelGeometry();
    }

    public static class MultipartModelGeometry implements IModelGeometry<MultipartModelGeometry> {

        @Override
        public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
            return new MultipartBakedModel();
        }

        @Override
        public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
            return Collections.emptyList();
        }
    }
}
