package com.bug1312.ballooning.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bug1312.ballooning.Ballooning;
import com.bug1312.ballooning.ducks.ServerPlayerDuck;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity.Occupant;

@Mixin(PlayerList.class)
public class PlayerListMixin {

	@Inject(at = @At("TAIL"), method = "placeNewPlayer")
	public void ballooning$placeNewPlayer(Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
		final CompoundTag nbt = new CompoundTag();
		serverPlayer.addAdditionalSaveData(nbt);
		ServerLevel serverLevel = serverPlayer.serverLevel();
		Occupant.LIST_CODEC
			.parse(NbtOps.INSTANCE, nbt.get("ballooning_AttachedBalloons"))
			.resultOrPartial(string -> Ballooning.LOGGER.error("Failed to parse balloons: '{}'", string))
			.ifPresent(list -> {
				list.forEach(occupant -> {
								Entity entity = EntityType.loadEntityRecursive(occupant.entityData().copyTag(), serverLevel, e -> e);
								if (entity != null) serverLevel.addFreshEntity(entity);
					});
			});
	}

	@Inject(at = @At("HEAD"), method = "remove")
	public void ballooning$remove(ServerPlayer serverPlayer, CallbackInfo ci) {
		final List<Occupant> balloons = new ArrayList<>();
		
		Ballooning.getPlayersBalloons(serverPlayer).forEach(entity -> {
			CompoundTag nbt = new CompoundTag();
			if (entity.saveAsPassenger(nbt)) balloons.add(new Occupant(CustomData.of(nbt), 0, 0));
			entity.setRemoved(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
		});
		
		((ServerPlayerDuck) serverPlayer).setBalloons(balloons);
	}

}
