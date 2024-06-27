package com.bug1312.ballooning.ducks;

import java.util.List;

import org.spongepowered.asm.mixin.Unique;

import net.minecraft.world.level.block.entity.BeehiveBlockEntity.Occupant;

public interface ServerPlayerDuck {
	@Unique
	void setBalloons(List<Occupant> occupants);
}
