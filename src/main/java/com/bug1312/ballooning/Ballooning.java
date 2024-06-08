package com.bug1312.ballooning;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class Ballooning implements ModInitializer {

	public static final Item LEAD = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("ballooning", "magical_lead"), new BallooningLead(new Item.Properties().rarity(Rarity.EPIC)));

	@Override
	public void onInitialize() { 
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> content.addAfter(Items.LEAD, LEAD));
	
		LootTableEvents.MODIFY.register((id, tableBuilder, source) -> {
			if (source.isBuiltin() && BuiltInLootTables.TRIAL_CHAMBERS_REWARD_COMMON.equals(id)) {
				LootPool.Builder poolBuilder = LootPool.lootPool().add(LootItem.lootTableItem(LEAD)).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0f, 6.0f)));
				tableBuilder.withPool(poolBuilder);
			}
		});
	}

}