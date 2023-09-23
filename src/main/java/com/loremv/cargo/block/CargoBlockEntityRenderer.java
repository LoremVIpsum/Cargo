package com.loremv.cargo.block;

import com.loremv.cargo.Cargo;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GlassBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

public class CargoBlockEntityRenderer implements BlockEntityRenderer<CargoBlockEntity> {

    public CargoBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}
    private static final BlockState state = Cargo.BOAT.getDefaultState();

    @Override
    public void render(CargoBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {


        if(entity.getSecondsForTransport()!=-1)
        {
            matrices.push();
            int[] d = entity.getDestination();
            //System.out.println(d[0]+" "+d[2]);
            int travelx = d[0]-entity.getPos().getX();
            int travelz = d[2]-entity.getPos().getZ();
            //

            double percent = (double)entity.getCounter()/(double)entity.getSecondsForTransport();

            matrices.translate(travelx*percent,0,travelz*percent);


            MinecraftClient.getInstance().getBlockRenderManager().renderBlock(state,entity.getPos().up(),entity.getWorld(),matrices,vertexConsumers.getBuffer(RenderLayer.getCutout()),false,entity.getWorld().random);
            matrices.pop();
        }

    }

    @Override
    public boolean rendersOutsideBoundingBox(CargoBlockEntity blockEntity) {
        return true;
    }

    @Override
    public boolean isInRenderDistance(CargoBlockEntity blockEntity, Vec3d pos) {
        return true;
    }
}
