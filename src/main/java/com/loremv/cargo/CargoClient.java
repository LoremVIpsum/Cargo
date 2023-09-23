package com.loremv.cargo;

import com.loremv.cargo.block.CargoBlock;
import com.loremv.cargo.block.CargoBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;

public class CargoClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.register(Cargo.CARGO_BLOCK_ENTITY, CargoBlockEntityRenderer::new);
    }
}
