package com.loremv.cargo;

import com.loremv.cargo.block.CargoBlock;
import com.loremv.cargo.block.CargoBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cargo implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");
	public static final TagKey<Biome> CARGOABLE = TagKey.of(Registry.BIOME_KEY, new Identifier("cargo","cargoable"));

	public static final Identifier CARGO_DATA = new Identifier("cargo","cargo_data_storage");



	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		registerBlocks();
		registerItems();
		LOGGER.info("Hello Fabric world!");
	}
	public static final CargoBlock CARGO_BLOCK = new CargoBlock(AbstractBlock.Settings.of(Material.BAMBOO));
	public static final Block BOAT = new Block(AbstractBlock.Settings.of(Material.BAMBOO));
	private void registerBlocks()
	{
		Registry.register(Registry.BLOCK,new Identifier("cargo","cargo_block"),CARGO_BLOCK);
		Registry.register(Registry.BLOCK,new Identifier("cargo","boat"),BOAT);
	}
	public void registerItems()
	{
		Registry.register(Registry.ITEM,new Identifier("cargo","cargo_block"),new BlockItem(CARGO_BLOCK,new FabricItemSettings().group(ItemGroup.MISC)));
	}


	public static BlockEntityType<CargoBlockEntity> CARGO_BLOCK_ENTITY = Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier("cargo", "cargo_block_entity"),
			FabricBlockEntityTypeBuilder.create(CargoBlockEntity::new,CARGO_BLOCK).build()
	);
}
