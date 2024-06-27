package com.bug1312.ballooning;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraft.world.phys.AABB;

public class Ballooning implements ModInitializer {
	
	public static final Logger LOGGER = LoggerFactory.getLogger("ballooning");

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

	public static List<Entity> getPlayersBalloons(ServerPlayer serverPlayer) {
		return serverPlayer.level().getEntities(null, AABB.ofSize(serverPlayer.position(), 22, 22, 22)).stream().filter(entity ->
			entity instanceof final Leashable leashable && 
			leashable.getLeashHolder() != null &&
			leashable.getLeashHolder().equals(serverPlayer) &&
			entity.saveWithoutId(new CompoundTag()).getBoolean("ballooning_Balloon")
		).toList();
	}

}