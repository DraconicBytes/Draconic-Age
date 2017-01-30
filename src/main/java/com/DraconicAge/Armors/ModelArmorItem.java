package com.DraconicAge.Armors;

import com.DraconicAge.ArmorMaterialHelper;
import com.DraconicAge.DraconicAge;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ModelArmorItem extends ItemArmor{
	 ModelBiped armorModel = new ModelBiped();
	 String name;
	
	// ------------------------------------------------------------------------------------
	public ModelArmorItem(String pName, ArmorMaterial pArmorMaterial, int pRenderIndex, int pArmorType) 
	{ 
		super(pArmorMaterial, pRenderIndex, pArmorType);
		name = pName;
		this.setFull3D();
	}
	
	// ------------------------------------------------------------------------------------
	@Override
	public String getUnlocalizedName(){
		return String.format("item.%s%s", DraconicAge.MODID+":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemstack){
		return String.format("item.%s%s", DraconicAge.MODID+":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}
	
	protected String getUnwrappedUnlocalizedName(String unlocalizedName){		
		return unlocalizedName.substring(unlocalizedName.indexOf('.') + 1);
	}
	
	// ------------------------------------------------------------------------------------
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		this.itemIcon = iconRegister.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.') + 1));
	}
	
	// ------------------------------------------------------------------------------------
	@Override 
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) 
	{ 
		
		return String.format("%s:textures/models/armor/%s_%d.png", DraconicAge.MODID, name, slot == 2 ? 2 : 1);
	}
	
	// ------------------------------------------------------------------------------------
	@Override 
	@SideOnly(Side.CLIENT) 
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) 
	{ 
		if(itemStack != null)
		{ 
			if(itemStack.getItem() instanceof ModelArmorItem)
			{ 
				int type = ((ItemArmor)itemStack.getItem()).armorType; 
				
				if(type == 1 || type == 3)
				{ 
					armorModel = DraconicAge.proxy.getArmorModel(ArmorMaterialHelper.getArmorID(name), 0); 
				}
				else
				{ 
					armorModel = DraconicAge.proxy.getArmorModel(ArmorMaterialHelper.getArmorID(name), 1); 
				} 
			} 
			
			if(armorModel != null)
			{ 
				armorModel.bipedHead.showModel = armorSlot == 0; 
				armorModel.bipedHeadwear.showModel = armorSlot == 0; 
				armorModel.bipedBody.showModel = armorSlot == 1 || armorSlot == 2; 
				armorModel.bipedRightArm.showModel = armorSlot == 1; 
				armorModel.bipedLeftArm.showModel = armorSlot == 1; 
				armorModel.bipedRightLeg.showModel = armorSlot == 2 || armorSlot == 3; 
				armorModel.bipedLeftLeg.showModel = armorSlot == 2 || armorSlot == 3; 
				armorModel.isSneak = entityLiving.isSneaking(); 
				armorModel.isRiding = entityLiving.isRiding(); 
				armorModel.isChild = entityLiving.isChild(); 
				armorModel.heldItemRight = entityLiving.getEquipmentInSlot(0) != null ? 1 :0;
				
				if(entityLiving instanceof EntityPlayer)
				{ 
					armorModel.aimedBow =((EntityPlayer)entityLiving).getItemInUseDuration() > 2; 
				} 
				
				return armorModel; 
				} 
			} 
		return null; 
	}
}
