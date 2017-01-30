package com.DraconicAge;

import com.DraconicAge.Entity.EntityDragon;
import com.DraconicAge.Models.ScaleArmorModel;
import com.DraconicAge.render.entity.RenderDragon;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.model.ModelBiped;

public class ClientProxy extends CommonProxy {
	//------------------------------------------------------------
	// initialize armor models
	private static final ScaleArmorModel scaleBody = new ScaleArmorModel(1.0f); 
	private static final ScaleArmorModel scaleLegs = new ScaleArmorModel(0.5f);
	
	//------------------------------------------------------------
	// init of the client part
	@Override
	public void clientPreInit(FMLPreInitializationEvent event) {
		System.out.println( "Starting Client Initialization" );
		
		// register renderers for mobs/bosses
		RenderingRegistry.registerEntityRenderingHandler(EntityDragon.class, new RenderDragon());
	}
	
	@Override
	public void clientInit(FMLInitializationEvent event) {
		
	}
	
	@Override
	public void clientPostInit(FMLPostInitializationEvent event) {
		
	}
	
	//------------------------------------------------------------
	// returns the correct armor if the right id is given
	@Override 
	public ModelBiped getArmorModel(int name, int id)
	{ 
		switch (name) 
		{ 
		case 1:
			if (id == 0)
				return scaleBody; 
			else if (id == 1)
				return scaleLegs;
			break;
		default: 
			break; 
					} 
		return null; //default, if whenever you should have passed on a wrong id 
	} 
}
