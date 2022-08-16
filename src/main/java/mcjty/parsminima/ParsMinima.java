package mcjty.parsminima;

import mcjty.parsminima.api.IParsMinima;
import mcjty.parsminima.client.MultipartModelLoader;
import mcjty.parsminima.common.ParsMinimaApi;
import mcjty.parsminima.common.Registration;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Function;
import java.util.function.Supplier;

@Mod(ParsMinima.MODID)
public class ParsMinima {

    public static final String MODID = "parsminima";

    public static ParsMinima instance;
    public final ParsMinimaApi api = new ParsMinimaApi();

    public ParsMinima() {
        instance = this;
        Registration.init();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::processIMC);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(MultipartModelLoader::register);
        });

//        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, GeneralConfig.CLIENT_CONFIG);
//        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, GeneralConfig.SERVER_CONFIG);
    }

    private void processIMC(final InterModProcessEvent event) {
        event.getIMCStream().forEach(message -> {
            if ("getParsMinima".equalsIgnoreCase(message.method())) {
                Supplier<Function<IParsMinima, Void>> supplier = message.getMessageSupplier();
                supplier.get().apply(api);
            }
        });
    }
}
