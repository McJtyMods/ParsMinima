package mcjty.parsminima.common;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

import static mcjty.parsminima.ParsMinima.MODID;

public class Registration {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILES.register(bus);
    }

    public static final RegistryObject<MultipartBlock> MULTIPART_BLOCK = BLOCKS.register("multipart", MultipartBlock::new);
    public static final RegistryObject<Item> MULTIPART_ITEM = ITEMS.register("multipart", () -> new MultipartBlockItem(MULTIPART_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<MultipartTE>> MULTIPART_TILE = TILES.register("multipart", () -> BlockEntityType.Builder.of(MultipartTE::new, MULTIPART_BLOCK.get()).build(null));

    public static final RegistryObject<MultipartBlock> MINI_BLOCK = BLOCKS.register("mini", MultipartBlock::new);
    public static final RegistryObject<Item> MINI_ITEM = ITEMS.register("mini", () -> new MultipartBlockItem(MINI_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<MultipartTE>> MINI_TILE = TILES.register("mini", () -> BlockEntityType.Builder.of(MultipartTE::new, MINI_BLOCK.get()).build(null));
}
