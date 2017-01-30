package com.DraconicAge.Entity.Projectiles;

import java.util.List;

import com.DraconicAge.Entity.EntityDragon;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityGuidedFireball extends EntityFireball {

	private boolean inGround;
	private Entity target =  null;
	public double maxSpeed = 40.0D;
	public float explosionStrengh = 5.0F;
	public float explosionRadius = 5.0F;
	public float explosionDamage = 6.0F;
	private int livingTicks = 0;

	//------------------------------------------------------------
	/**
	 * Minimalistic constructor contains only information about the world.
	 * Fireball seeks next living entity and targets it.
	 * @param p_i1759_1_ Current world
	 */
	public EntityGuidedFireball(World p_i1759_1_) {
		super(p_i1759_1_);
		target = worldObj.findNearestEntityWithinAABB(EntityLiving.class, (this.boundingBox.copy()).expand(20, 20, 20), this);
	}
	
	//------------------------------------------------------------
	/**
	 * Detailed constructor version. May only be used by {@link com.DraconicAge.Entity.EntityDragon}.
	 * 
	 * @param pWorld Current World
	 * @param pProjectileSource Entity who shot the fireball
	 * @param pVelocityX Horizontal velocity vector part on the X axis
	 * @param pVelocityY Vertical velocity
	 * @param pVelocityZ Horizontal velocity vector part on the X axis
	 */
	public EntityGuidedFireball(World pWorld, EntityLivingBase pProjectileSource, double pVelocityX, double pVelocityY,
			double pVelocityZ) {
		super(pWorld, pProjectileSource, pVelocityX, pVelocityY, pVelocityZ);

		// Search target
		if (pProjectileSource instanceof EntityDragon) {
			this.target = ((EntityDragon)pProjectileSource).getTarget();
		}
	}

	//------------------------------------------------------------
	/**
	 * Standart constructor inheritated from EntityFireball.
	 * @param p_i1760_1_
	 * @param p_i1760_2_
	 * @param p_i1760_4_
	 * @param p_i1760_6_
	 * @param p_i1760_8_
	 * @param p_i1760_10_
	 * @param p_i1760_12_
	 */
	public EntityGuidedFireball(World p_i1760_1_, double p_i1760_2_, double p_i1760_4_, double p_i1760_6_, double p_i1760_8_,
			double p_i1760_10_, double p_i1760_12_) {
		super(p_i1760_1_, p_i1760_2_, p_i1760_4_, p_i1760_6_, p_i1760_8_, p_i1760_10_, p_i1760_12_);
		target = worldObj.findNearestEntityWithinAABB(EntityLiving.class, (this.boundingBox.copy()).expand(20, 20, 20), this);
	}

	//------------------------------------------------------------
	/**
	 * Moves fire forward. Aiming calculation is also done here.
	 */
	public void onUpdate() {
		super.onUpdate();
		
		if (!this.worldObj.isRemote) {
			livingTicks++;
			
			// Destroy fireball when it didnt hit a target after a certain amount of ticks have passed.
			if (this.livingTicks >= 600) {
				this.onImpact(new MovingObjectPosition(this));
			}
			
			// Check if target has been hit by fireball
			//if (this.motionX < 0.01 && this.motionY < 0.01 && this.motionZ < 0.01) {
			//	this.onImpact(null);
			//}
			
			// Aiming calculation
			if (target != null) {
				double d1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
				
				Vec3 vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
				Vec3 vec31 = Vec3.createVectorHelper(this.target.posX, this.target.posY, this.target.posZ);
				vec31 = vec3.subtract(vec31);
				vec31 = vec31.normalize();
				
				vec31.xCoord *= d1;
				vec31.yCoord *= d1;
				vec31.zCoord *= d1;
				
				this.motionX = vec31.xCoord;
				this.motionY = vec31.yCoord;
				this.motionZ = vec31.zCoord;
			}
		}
	}
	
	//------------------------------------------------------------
	/** 
	 * Calculation done on impact. Creates the explosion and destroys the blocks  also spreads some fire.
	 */
	@Override
	protected void onImpact(MovingObjectPosition mop) {
		if (!this.worldObj.isRemote && mop.entityHit != this.shootingEntity)
        {

			// Damage calculation
            List<EntityLiving> e = this.worldObj.getEntitiesWithinAABB(EntityLiving.class, this.boundingBox.expand(this.explosionRadius, this.explosionRadius, this.explosionRadius));
            for (EntityLiving e1 : e) 
            {
            	if (e1 != this.shootingEntity)
            		e1.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), this.explosionDamage);
            }

            // Spawn explosion
            this.worldObj.newExplosion((Entity)null, this.posX, this.posY, this.posZ, (float)this.explosionStrengh, false, false);
            this.setDead();
        }
	}

}
