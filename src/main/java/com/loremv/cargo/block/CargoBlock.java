package com.loremv.cargo.block;

import com.loremv.cargo.Cargo;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CargoBlock extends BlockWithEntity {
    public CargoBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CargoBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient && hand==Hand.MAIN_HAND)
        {
            CargoBlockEntity cargoBlockEntity = (CargoBlockEntity) world.getBlockEntity(pos);

            //if seconds for transport is -1, this cargo block has no destination yet - so determine if the player
            //has followed a route to get here, and then set that as the destination
            if(cargoBlockEntity.getSecondsForTransport()==-1)
            {
                NbtCompound compound = world.getServer().getDataCommandStorage().get(Cargo.CARGO_DATA);

                //if the player has the scoreboard tag, then they must have crossed water after clicking a cargo block
                if(player.getScoreboardTags().contains("cargo_seafairing"))
                {

                    if(compound.contains(player.getUuidAsString()))
                    {
                        int[] dest = compound.getIntArray(player.getUuidAsString());

                        cargoBlockEntity.setDestination(dest);
                        player.removeScoreboardTag("cargo_seafairing");
                    }
                }
                //if the player doesn't have that scoreboard tag, assume that they want to start a route
                else
                {
                    player.sendMessage(Text.of("You have set the destination of a water cargo route, stay on waterways!"),false);
                    player.addScoreboardTag("cargo_seafairing");
                    compound.putIntArray(player.getUuidAsString(),new int[]{pos.getX(),pos.getY(), pos.getZ()});
                    world.getServer().getDataCommandStorage().set(Cargo.CARGO_DATA,compound);
                }
            }
            else
            {

                String v = "There is "+(cargoBlockEntity.getTimeOfArrival()-world.getTime())+" ticks until";
                if(cargoBlockEntity.getStoredItems().size()>0)
                {
                    v+=" the cargo from here arrives at its destination!";
                }
                else
                {
                    v+=" the cargo currently here is picked up!";
                }
                player.sendMessage(Text.of(v),false);
            }


        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, Cargo.CARGO_BLOCK_ENTITY,CargoBlockEntity::tick);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(1,0,1,15,16,15);
    }
}
