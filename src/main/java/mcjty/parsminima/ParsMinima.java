package mcjty.parsminima;

import mcjty.parsminima.api.IParsMinima;
import mcjty.parsminima.common.ParsMinimaApi;
import mcjty.parsminima.common.Registration;
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

//        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
//            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
//            FMLJavaModLoadingContext.get().getModEventBus().addListener(MultipartModelLoader::register);
//        });
//
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
