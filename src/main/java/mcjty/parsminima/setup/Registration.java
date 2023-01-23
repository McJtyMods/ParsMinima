package mcjty.parsminima.setup;

import mcjty.parsminima.ParsMinima;
import mcjty.parsminima.common.MultipartBlock;
import mcjty.parsminima.common.MultipartBlockItem;
import mcjty.parsminima.common.MultipartTE;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.parsminima.ParsMinima.MODID;
import static mcjty.parsminima.ParsMinima.tab;

public class Registration {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILES.register(bus);
    }

    public static final RegistryObject<MultipartBlock> MULTIPART_BLOCK = BLOCKS.register("multipart", MultipartBlock::new);
    public static final RegistryObject<Item> MULTIPART_ITEM = ITEMS.register("multipart", tab(() -> new MultipartBlockItem(MULTIPART_BLOCK.get(), ParsMinima.setup.defaultProperties())));
    public static final RegistryObject<BlockEntityType<MultipartTE>> MULTIPART_TILE = TILES.register("multipart", () -> BlockEntityType.Builder.of(MultipartTE::new, MULTIPART_BLOCK.get()).build(null));

    public static final RegistryObject<MultipartBlock> MINI_BLOCK = BLOCKS.register("mini", MultipartBlock::new);
    public static final RegistryObject<Item> MINI_ITEM = ITEMS.register("mini", tab(() -> new MultipartBlockItem(MINI_BLOCK.get(), ParsMinima.setup.defaultProperties())));
    public static final RegistryObject<BlockEntityType<MultipartTE>> MINI_TILE = TILES.register("mini", () -> BlockEntityType.Builder.of(MultipartTE::new, MINI_BLOCK.get()).build(null));
}
