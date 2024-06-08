package com.bug1312.ballooning.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.ballooning.Ballooning;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

@Mixin(Entity.class)
public abstract class EntityMixin {
	
	Entity _this = (Entity) ((Object) this);
	boolean balloon = false;
	
	@Inject(at = @At("HEAD"), method = "interact", cancellable = true)
	private void ballooning$interact(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> ci) {
        if (_this.isAlive() && _this instanceof Leashable) {
            Leashable leashable = (Leashable) _this;
            ItemStack itemStack = player.getItemInHand(interactionHand);

            if (itemStack.is(Ballooning.LEAD) && leashable.canHaveALeashAttachedToIt()) {
                leashable.setLeashedTo(player, true);
                itemStack.shrink(1);

                this.balloon = true;

                for (int i = 0; i < 5; ++i) {
                    double d = _this.getRandom().nextGaussian() * 0.02;
                    double e = _this.getRandom().nextGaussian() * 0.02;
                    double f = _this.getRandom().nextGaussian() * 0.02;
                    _this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, _this.getRandomX(0.5), _this.getRandomY() + (_this.getBbHeight() / 2.0), _this.getRandomZ(0.5), d, e, f);
                }

                ci.setReturnValue(InteractionResult.sidedSuccess(_this.level().isClientSide()));
            }
        }
	}
	
	@Inject(at = @At("TAIL"), method = "saveWithoutId")
	private void ballooning$saveWithoutId(CompoundTag compoundTag, CallbackInfoReturnable<CompoundTag> ci) {
        compoundTag.putBoolean("ballooning_Balloon", this.balloon);
    }
	
	
	@Inject(at = @At("TAIL"), method = "load")
	private void ballooning$load(CompoundTag compoundTag, CallbackInfo ci) {
    	this.balloon = compoundTag.getBoolean("ballooning_Balloon");
    }
	    
	@Inject(at = @At("TAIL"), method = "tick")
    private void ballooning$tick(CallbackInfo ci) {
		if (balloon) {
			if (_this.position().y() > _this.level().getMaxBuildHeight() + 30) _this.kill();
			_this.resetFallDistance();
			Vec3 current = _this.getDeltaMovement();
			double newY = current.y;
			newY = (newY += (0.5d - current.y) * 0.2d) * 0.98d;
			
			_this.setDeltaMovement(current.multiply(1, 0, 1).add(0, newY, 0));
		}
	}
	
}