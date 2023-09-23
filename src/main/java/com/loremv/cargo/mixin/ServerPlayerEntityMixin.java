package com.loremv.cargo.mixin;

import com.loremv.cargo.Cargo;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Shadow public abstract void sendMessage(Text message, boolean actionBar);



    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo info) {
        if(getScoreboardTags().contains("cargo_seafairing"))
        {
            if(!world.getBiome(getBlockPos()).isIn(Cargo.CARGOABLE))
            {
                removeScoreboardTag("cargo_seafairing");
                sendMessage(Text.of("You left the waterways! route reset"),false);
            }
        }
    }
}
