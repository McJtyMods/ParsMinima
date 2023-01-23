package mcjty.parsminima.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mcjty.lib.client.BaseGeometry;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

import java.util.Collection;
import java.util.Collections;

public class MultipartModelLoader implements IGeometryLoader<MultipartModelLoader.MultipartModelGeometry> {

    @Override
    public MultipartModelGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        return new MultipartModelGeometry();
    }

    public static void register(ModelEvent.RegisterGeometryLoaders event) {
        event.register("multipartloader", new MultipartModelLoader());
    }

    public static class MultipartModelGeometry extends BaseGeometry<MultipartModelGeometry> {

        @Override
        public BakedModel bake() {
            return new MultipartBakedModel();
        }

        @Override
        public Collection<Material> getMaterials() {
            return Collections.emptyList();
        }
    }
}
