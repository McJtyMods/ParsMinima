package mcjty.parsminima.common;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static mcjty.parsminima.ParsMinima.MODID;

public class Registration {

    public static final DeferredBlocks BLOCKS = DeferredBlocks.create(MODID);
    public static final DeferredItems ITEMS = DeferredItems.create(MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILES.register(bus);
    }

    public static final Supplier<MultipartBlock> MULTIPART_BLOCK = BLOCKS.register("multipart", MultipartBlock::new);
    public static final Supplier<Item> MULTIPART_ITEM = ITEMS.register("multipart", () -> new MultipartBlockItem(MULTIPART_BLOCK.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<MultipartTE>> MULTIPART_TILE = TILES.register("multipart", () -> BlockEntityType.Builder.of(MultipartTE::new, MULTIPART_BLOCK.get()).build(null));

    public static final Supplier<MultipartBlock> MINI_BLOCK = BLOCKS.register("mini", MultipartBlock::new);
    public static final Supplier<Item> MINI_ITEM = ITEMS.register("mini", () -> new MultipartBlockItem(MINI_BLOCK.get(), new Item.Properties()));
    public static final Supplier<BlockEntityType<MultipartTE>> MINI_TILE = TILES.register("mini", () -> BlockEntityType.Builder.of(MultipartTE::new, MINI_BLOCK.get()).build(null));
}
