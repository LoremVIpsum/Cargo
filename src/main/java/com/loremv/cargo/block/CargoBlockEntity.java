package com.loremv.cargo.block;

import com.loremv.cargo.Cargo;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CargoBlockEntity extends BlockEntity {

    private int counter = 0;
    private int[] destination = new int[3];
    private int secondsForTransport = -1;
    private long timeOfArrival = 0;

    private boolean counterAway = true;

    NbtList storedItems = new NbtList();
    public CargoBlockEntity( BlockPos pos, BlockState state) {
        super(Cargo.CARGO_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putIntArray("destination",destination);
        nbt.putInt("secondsForTransport",secondsForTransport);
        nbt.putLong("timeOfArrival",timeOfArrival);
        nbt.putInt("counter",counter);
        nbt.putBoolean("counterAway",counterAway);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        destination = nbt.getIntArray("destination");
        secondsForTransport = nbt.getInt("secondsForTransport");
        timeOfArrival = nbt.getLong("timeOfArrival");
        counter = nbt.getInt("counter");
        counterAway = nbt.getBoolean("counterAway");
    }

    public void setDestination(int[] destination) {
        this.destination = destination;

        BlockPos end = new BlockPos(destination[0],destination[1],destination[2]);
        this.secondsForTransport=(pos.getManhattanDistance(end));

        this.timeOfArrival=world.getTime()+this.getSecondsForTransport();
        markDirty();
    }

    public int[] getDestination() {
        return destination;
    }

    public int getSecondsForTransport() {
        return secondsForTransport;
    }

    public void setTimeOfArrival(long timeOfArrival) {
        this.timeOfArrival = timeOfArrival;
        markDirty();
    }

    public long getTimeOfArrival() {
        return timeOfArrival;
    }

    public static void tick(World world, BlockPos pos, BlockState state, CargoBlockEntity be)
    {
        //if seconds for transport is -1, then this cargo block does not have a destination yet
        if(be.getSecondsForTransport()!=-1 && world.getBlockState(pos.up()).isOf(Blocks.CHEST))
        {
            if(be.counterAway)
            {
                be.counter++;
            }
            else
            {
                be.counter--;
            }
            //if the ship should have arrived
            if(be.getTimeOfArrival()<world.getTime())
            {
                //if there is a stored inventory in the cargo block and the time is reached, transfer it
                if(be.getStoredItems().size()>0)
                {
                    int[] d =be.getDestination();

                    be.counter=0;
                    ChestBlockEntity destInventory = (ChestBlockEntity) world.getBlockEntity(new BlockPos(d[0],d[1]+1,d[2]));

                    for (int i = 0; i < be.getStoredItems().size(); i++) {
                        ItemStack stack = ItemStack.fromNbt(be.getStoredItems().getCompound(i));
                        if(!stack.isEmpty())
                        {
                            destInventory.setStack(i,stack);
                        }


                    }
                    destInventory.markDirty();

                    be.clearStoredItems();


                }
                //if there is no stored inventory in the cargo block, store it
                else
                {
                    Inventory sendInventory = (Inventory) world.getBlockEntity(pos.up());
                    be.setStoredItems(sendInventory);

                    sendInventory.clear();


                }
                //schedule the next shipment (transfer or store)
                if(be.counter<0)
                {
                    be.counter=0;
                }
                be.counterAway=!be.counterAway;
                be.setTimeOfArrival(world.getTime()+be.getSecondsForTransport());

            }

        }
    }

    public void setStoredItems(Inventory inventory) {
        storedItems=new NbtList();
        for (int i = 0; i < inventory.size(); i++) {
            if(!inventory.getStack(i).isEmpty())
            {
                NbtCompound compound = new NbtCompound();
                inventory.getStack(i).writeNbt(compound);
                storedItems.add(compound);
            }

        }
        markDirty();
    }
    public void clearStoredItems()
    {
        storedItems=new NbtList();
        markDirty();
    }

    public NbtList getStoredItems() {

        return storedItems;
    }



    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public int getCounter() {
        return counter;
    }
}
