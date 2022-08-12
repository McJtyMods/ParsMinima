package mcjty.parsminima.api;

/**
 * Main interface for this mod.
 * Get a reference to an implementation of this interface by calling:
 *         InterModComms.sendTo("parsminima", "getParsMinima", GetParsMinimaHandler::new);
 */
public interface IParsMinima {

    void registerTileProvider(IPartTileProvider provider);
}
