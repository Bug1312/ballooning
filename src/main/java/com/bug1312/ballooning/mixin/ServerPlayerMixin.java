package com.bug1312.ballooning.mixin;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bug1312.ballooning.Ballooning;
import com.bug1312.ballooning.ducks.ServerPlayerDuck;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity.Occupant;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements ServerPlayerDuck {

	@Unique
	ServerPlayer _this = (ServerPlayer) ((Object) this);

	@Nullable
	public final List<Occupant> balloons = new ArrayList<>();

	@Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
	private void ballooning$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {		
		this.balloons.clear();
			if (compoundTag.contains("ballooning_AttachedBalloons")) {
				Occupant.LIST_CODEC.parse(NbtOps.INSTANCE, compoundTag.get("ballooning_AttachedBalloons"))
					.resultOrPartial(string -> Ballooning.LOGGER.error("Failed to parse bees: '{}'", string))
					.ifPresent(list -> this.balloons.addAll(list));
			}
	}

	@Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
	private void ballooning$addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
		compoundTag.put("ballooning_AttachedBalloons", Occupant.LIST_CODEC.encodeStart(NbtOps.INSTANCE, this.balloons).getOrThrow());
	}

	@Override
	public void setBalloons(List<Occupant> occupants) {
		this.balloons.clear();
		this.balloons.addAll(occupants);
	}

}
