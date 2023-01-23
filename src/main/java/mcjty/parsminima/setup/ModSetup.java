package mcjty.parsminima.setup;

import mcjty.lib.compat.MainCompatHandler;
import mcjty.lib.setup.DefaultModSetup;
import mcjty.parsminima.ParsMinima;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ModSetup extends DefaultModSetup {

    public ModSetup() {
        createTab(ParsMinima.MODID, "parsminima", () -> new ItemStack(Items.DIRT)); // @todo
    }

    @Override
    public void init(FMLCommonSetupEvent e) {
        super.init(e);
    }

    @Override
    protected void setupModCompat() {
        MainCompatHandler.registerWaila();
        MainCompatHandler.registerTOP();
    }
}
