package com.bug1312.ballooning;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.LeadItem;

public class BallooningLead extends LeadItem {

	public BallooningLead(Properties properties) { super(properties); }
	
	@Override public boolean isFoil(ItemStack itemStack) { return true; }

}
