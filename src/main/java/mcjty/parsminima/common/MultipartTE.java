package mcjty.parsminima.common;

import mcjty.lib.varia.Tools;
import mcjty.parsminima.ParsMinima;
import mcjty.parsminima.api.IMultipart;
import mcjty.parsminima.api.IPartTile;
import mcjty.parsminima.api.PartSlot;
import mcjty.parsminima.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MultipartTE extends BlockEntity implements IMultipart {

    public static final ModelProperty<Map<PartSlot, Part>> PARTS = new ModelProperty<>();

    public static class Part {
        private final BlockState state;
        @Nullable private final IPartTile tile;

        public Part(BlockState state, @Nullable IPartTile tile) {
            this.state = state;
            this.tile = tile;
        }

        public BlockState getState() {
            return state;
        }

        @Nullable
        public IPartTile getTile() {
            return tile;
        }
    }

    private Map<PartSlot, Part> parts = new HashMap<>();
    private int version = 0;    // To update rendering client-side

    public MultipartTE(BlockPos pos, BlockState state) {
        super(Registration.MULTIPART_TILE.get(), pos, state);
    }

    private void dumpParts(String prefix) {
        if (level.isClientSide) {
            System.out.println("====== CLIENT(" + prefix + ") " + worldPosition.toString() + " ======");
        } else {
            System.out.println("====== SERVER(" + prefix + ") " + worldPosition.toString() + " ======");
        }
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            BlockState state = entry.getValue().state;
            System.out.println("    SLOT: " + entry.getKey().name() +
                    "    " + Tools.getId(state.getBlock()).toString());
            for (Property<?> property : state.getProperties()) {
                System.out.println("        PROP: " + property + " = " + state.getValue(property));
            }

        }
    }

    public void addPart(PartSlot slot, BlockState state, IPartTile te) {
        System.out.println("MultipartTE.addPart: " + level.isClientSide);
        parts.put(slot, new Part(state, te));

        if (te != null) {
            te.onPartAdded(slot, state, this);
        }

        if (!level.isClientSide) {
            version++;
            markDirtyClient();
        } else {
            // @todo may not be needed. It doesn't seem to help anyway
//            ModelDataManager.requestModelDataRefresh(this);
//            world.markBlockRangeForRenderUpdate(pos, null, null);
        }
//        dumpParts("add");
    }

    public void removePart(BlockState partState) {
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            if (entry.getValue().getState() == partState) {
                parts.remove(entry.getKey());
                if (!level.isClientSide) {
                    version++;
                    markDirtyClient();
                }
//                dumpParts("remove");
                return;
            }
        }
    }

    public void dump() {
        if (level.isClientSide) {
            System.out.println("CLIENT: " + worldPosition);
        } else {
            System.out.println("SERVER: " + worldPosition);
        }
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            System.out.println("    SLOT: " + entry.getKey());
            Part part = entry.getValue();
            BlockState state = part.getState();
            Block block = state.getBlock();
            Collection<Property<?>> properties = state.getProperties();
            System.out.println("        block: " + Tools.getId(block).toString());
            for (Property<?> property : properties) {
                System.out.println("        property: " + property.getName() + " = " + state.getValue(property).toString());
            }
        }
    }

    public Map<PartSlot, Part> getParts() {
        return parts;
    }

    @Nonnull
    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(PARTS, parts)
                .build();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        int oldVersion = version;
        load(packet.getTag());
        if (level.isClientSide && version != oldVersion) {
            requestModelDataUpdate();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS + Block.UPDATE_NEIGHBORS);
        }
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag compoundTag = saveWithoutMetadata();
        return ClientboundBlockEntityDataPacket.create(this, (BlockEntity entity) -> compoundTag);
    }


    public void markDirtyClient() {
        setChanged();
        if (level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    private boolean partExists(PartSlot slot, BlockState state) {
        if (parts.containsKey(slot)) {
            return parts.get(slot).getState().equals(state);
        }
        return false;
    }

    public void markDirtyQuick() {
        if (level != null) {
            level.blockEntityChanged(this.worldPosition);
        }
    }

    @Override
    public void load(@Nonnull CompoundTag compound) {
        super.load(compound);

        Map<PartSlot, Part> newparts = new HashMap<>();
        version = compound.getInt("version");
        ListTag list = compound.getList("parts", Tag.TAG_COMPOUND);
        for (int i = 0 ; i < list.size() ; i++) {
            CompoundTag tag = list.getCompound(i);

            PartSlot slot = PartSlot.byName(tag.getString("slot"));
            if (slot != null) {
                BlockState state = NbtUtils.readBlockState(tag);
                if (partExists(slot, state)) {
                    // Part is already there. Just update it
                    Part part = this.parts.get(slot);
                    if (tag.contains("te")) {
                        CompoundTag tc = tag.getCompound("te");
                        IPartTile partte = part.tile;
                        if (partte == null) {
                            BlockEntity te = null;
                            if (state.getBlock() instanceof EntityBlock entityBlock) {
                                te = entityBlock.newBlockEntity(worldPosition, state);
                            }
                            if (te != null) {
                                te.setLevel(level);
                                te.load(tc);
                                te.setLevel(level);
                            }
                            // @todo how to set part.tile?
                        } else {
                            partte.load(tc);
                            // @todo 1.17 te.setPosition(worldPosition);
                        }
                    }
                    newparts.put(slot, part);
                } else {
                    BlockEntity te = null;
                    if (tag.contains("te")) {
                        CompoundTag tc = tag.getCompound("te");
                        if (state.getBlock() instanceof EntityBlock entityBlock) {
                            te = entityBlock.newBlockEntity(worldPosition, state);
                        }
                        if (te != null) {
                            te.setLevel(level);
                            te.load(tc);
                            te.setLevel(level);
                        }
                    }
                    IPartTile partte = ParsMinima.instance.api.createTile(te);
                    Part part = new Part(state, partte);
                    newparts.put(slot, part);
                }
            }
        }
        this.parts = newparts;
    }


    // @todo 1.14
//    @Override
//    protected void setWorldCreate(Level worldIn) {
//        setLevel(worldIn);
//    }


    @Override
    public void setLevel(@Nonnull Level worldIn) {
        super.setLevel(worldIn);
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            if (entry.getValue().getTile() != null) {
                // @todo not possible in 1.19!!!
                entry.getValue().getTile().setLevel(worldIn);
            }
        }
    }

    // @todo 1.17
    // @Override
    // public void setLevelAndPosition(@Nonnull Level worldIn, @Nonnull BlockPos pos) {
    //     super.setLevelAndPosition(worldIn, pos);
    //     for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
    //         if (entry.getValue().getTileEntity() != null) {
    //             entry.getValue().getTileEntity().setLevelAndPosition(worldIn, pos);
    //         }
    //     }
    // }

    @Override
    public void saveAdditional(@Nonnull CompoundTag compound) {
        ListTag list = new ListTag();
        for (Map.Entry<PartSlot, Part> entry : parts.entrySet()) {
            PartSlot slot = entry.getKey();
            Part part = entry.getValue();

            BlockState state = part.getState();
            CompoundTag tag = NbtUtils.writeBlockState(state);

            tag.putString("slot", slot.name());

            IPartTile te = part.getTile();
            if (te != null) {
                CompoundTag tc = te.saveWithoutMetadata();
                tag.put("te", tc);
            }

            list.add(tag);
        }
        compound.put("parts", list);
        compound.putInt("version", version);
    }

    // @todo 1.14
//    public boolean testIntersect(BlockState blockState) {
//        AxisAlignedBB box = blockState.getBoundingBox(world, pos);
//        for (Map.Entry<PartSlot, Part> entry : getParts().entrySet()) {
//            // @todo just check on slot?
//            Part part = entry.getValue();
//            AxisAlignedBB partBox = part.getState().getBoundingBox(world, pos);
//            if (box.intersects(partBox)) {
//                return true;
//            }
//        }
//        return false;
//    }
//

}
