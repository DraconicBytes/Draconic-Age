package com.DraconicAge.Entity;

import java.util.Iterator;
import java.util.List;

import com.DraconicAge.DAItems;
import com.DraconicAge.Entity.Projectiles.EntityGuidedFireball;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

enum FlyingState {
	Airbourne,
	Grounded,
	LiftingOff,
	Landing
}

enum MobState {
	Idle,
	Alerted,
	Attacking
}

public class EntityDragon extends EntityLiving implements IBossDisplayData, IEntityMultiPart, IMob
{

	public double[][]			ringBuffer		= new double[64][3];
	/**
	 * Index into the ring buffer. Incremented once per tick and restarts at 0
	 * once it reaches the end of the buffer.
	 */
	public int					ringBufferIndex	= -1;
	/** An array containing all body parts of this dragon */
	public EntityDragonPart[]	dragonPartArray;
	/** The head bounding box of a dragon */
	public EntityDragonPart		dragonPartHead;
	/** The body bounding box of a dragon */
	public EntityDragonPart		dragonPartBody;
	public EntityDragonPart		dragonPartTail1;
	public EntityDragonPart		dragonPartTail2;
	public EntityDragonPart		dragonPartTail3;
	public EntityDragonPart		dragonPartWing1;
	public EntityDragonPart		dragonPartWing2;
	/** Animation time at previous tick. */
	public float				prevAnimTime;
	/**
	 * Animation time, used to control the speed of the animation cycles (wings
	 * flapping, jaw opening, etc.)
	 */
	public float				animTime;
	public boolean				forceNewTarget;
	private Entity				target;
	public int					deathTicks;
	private boolean				initialized;
	public boolean				isAggressive = true;

	public Vec3					spawnPos;
	public double				attackRange		= 30;
	public double				territoryRange	= 40;
	private int					attackTrigger	= 0;
	
	public boolean				tamed = true;
	public MobState				mobState = MobState.Alerted;
	public FlyingState			flyingState = FlyingState.Airbourne;

	public EntityDragon( World pWorld )
	{
		super( pWorld );
		this.dragonPartArray = new EntityDragonPart[]
		{ 
			this.dragonPartHead = new EntityDragonPart( this, "head", 6.0F, 6.0F ), 
			this.dragonPartBody = new EntityDragonPart( this, "body", 8.0F, 8.0F ), 
			this.dragonPartTail1 = new EntityDragonPart( this, "tail", 4.0F, 4.0F ), 
			this.dragonPartTail2 = new EntityDragonPart( this, "tail", 4.0F, 4.0F ), 
			this.dragonPartTail3 = new EntityDragonPart( this, "tail", 4.0F, 4.0F ), 
			this.dragonPartWing1 = new EntityDragonPart( this, "wing", 4.0F, 4.0F ), 
			this.dragonPartWing2 = new EntityDragonPart( this, "wing", 4.0F, 4.0F ) 
		};

		this.setHealth( this.getMaxHealth( ) );
		this.setSize( 16.0F, 8.0F );
		this.noClip = false;
		this.isImmuneToFire = true;
		this.ignoreFrustumCheck = true;

		this.initialized = false;
	}

	protected void applyEntityAttributes( )
	{
		super.applyEntityAttributes( );
		this.getEntityAttribute( SharedMonsterAttributes.maxHealth ).setBaseValue( 200.0D );
	}

	protected void entityInit( )
	{
		super.entityInit( );
	}

	/**
	 * Returns a double[3] array with movement offsets, used to calculate
	 * trailing tail/neck positions. [0] = yaw offset, [1] = y offset, [2] =
	 * unused, always 0. Parameters: buffer index offset, partial ticks.
	 */
	public double[] getMovementOffsets( int pBufferIndexOffset, float pPartialTicks )
	{
		if ( this.getHealth( ) <= 0.0F )
		{
			pPartialTicks = 0.0F;
		}

		pPartialTicks = 1.0F - pPartialTicks;
		int j = this.ringBufferIndex - pBufferIndexOffset * 1 & 63;
		int k = this.ringBufferIndex - pBufferIndexOffset * 1 - 1 & 63;
		double[] adouble = new double[3];
		double d0 = this.ringBuffer[j][0];
		double d1 = MathHelper.wrapAngleTo180_double( this.ringBuffer[k][0] - d0 );
		adouble[0] = d0 + d1 * ( double ) pPartialTicks;
		d0 = this.ringBuffer[j][1];
		d1 = this.ringBuffer[k][1] - d0;
		adouble[1] = d0 + d1 * ( double ) pPartialTicks;
		adouble[2] = this.ringBuffer[j][2] + ( this.ringBuffer[k][2] - this.ringBuffer[j][2] ) * ( double ) pPartialTicks;
		return adouble;
	}

	private float smoothAngle( float flAngle, float flTargetAngle, float flFactor )
	{
		float f3 = MathHelper.wrapAngleTo180_float( flTargetAngle - flAngle );

		if ( f3 > flFactor )
			f3 = flFactor;

		if ( f3 < -flFactor )
			f3 = -flFactor;

		return flAngle + f3;

	}
	
	private void SetFlightDirection(double dX, double dY, double dZ) {
		float flYaw = ( float ) ( Math.atan2( dZ, dX ) * 180.0D / Math.PI );
		float flPitch = ( float ) -( Math.atan2( dY, Math.sqrt( dX * dX + dZ * dZ ) ) * 180.0D / Math.PI );

		this.rotationPitch = this.smoothAngle( this.rotationPitch, flPitch, 10.0F );
		this.rotationYaw = this.smoothAngle( this.rotationYaw, flYaw + 90, 10.0F );
	}
	
	private void ManageFlightVelocity() {
		// -> set velocity
		float flSpeed = 0.05F;
		double _f1 = MathHelper.cos( -this.rotationYaw * 0.017453292F );
		double _f2 = MathHelper.sin( -this.rotationYaw * 0.017453292F );
		double _f3 = -MathHelper.cos( -this.rotationPitch * 0.017453292F );
		double _f4 = MathHelper.sin( -this.rotationPitch * 0.017453292F );
		Vec3 vForward = Vec3.createVectorHelper( ( double ) ( _f2 * _f3 ), ( double ) _f4, ( double ) ( _f1 * _f3 ) );
		
		// Change direction and make turns
		this.addVelocity( vForward.xCoord * flSpeed, vForward.yCoord * flSpeed, vForward.zCoord * flSpeed );
		
		// Move entity
		//this.moveFlying((float) this.motionX, (float)this.motionY, (float)this.motionZ );
		this.moveEntity(this.motionX, this.motionY, this.motionZ );
		
		// Slow down when taking curves and maintain speed limit
		Vec3 vec31 = Vec3.createVectorHelper( this.motionX, this.motionY, this.motionZ ).normalize( );
		Vec3 vec32 = Vec3.createVectorHelper( ( double ) MathHelper.sin( this.rotationYaw * ( float ) Math.PI / 180.0F ), this.motionY, ( double ) ( -MathHelper.cos( this.rotationYaw * ( float ) Math.PI / 180.0F ) ) ).normalize( );
		float f9 = ( float ) ( vec31.dotProduct( vec32 ) + 1.0D ) / 2.0F;
		f9 = 0.8F + 0.15F * f9;

		this.motionX *= ( double ) f9;
		this.motionZ *= ( double ) f9;
		this.motionY *= 0.90D;
	}
	
	/**
	 * Called frequently so the entity can update its state every tick as
	 * required. For example, zombies and skeletons use this to react to
	 * sunlight and start to burn.
	 */
	public void onLivingUpdate( )
	{

		if ( !this.initialized )
		{
			this.spawnPos = Vec3.createVectorHelper( this.posX, this.posY, this.posZ );
			this.initialized = true;
		}

		// MathHelper.wrapAngleTo180_double( pAngle ) simplyfyAngle

		// ----------------------------------------------------------------------------------
		// play wing sound at ender dragon pos
		
		float f;
		float f1;

		if ( this.worldObj.isRemote )
		{
			f = MathHelper.cos( this.animTime * ( float ) Math.PI * 2.0F );
			f1 = MathHelper.cos( this.prevAnimTime * ( float ) Math.PI * 2.0F );

			if ( f1 <= -0.3F && f >= -0.3F )
			{
				this.worldObj.playSound( this.posX, this.posY, this.posZ, "mob.enderdragon.wings", 5.0F, 0.8F + this.rand.nextFloat( ) * 0.3F, false );
			}
		}

		this.prevAnimTime = this.animTime;
		float f2;

		// ----------------------------------------------------------------------------------
		// Logic and animation done while dragon is alive
		
		if ( this.getHealth( ) > 0.0F )
		{
			// calc animation offset
			f = 0.2F / ( MathHelper.sqrt_double( this.motionX * this.motionX + this.motionZ * this.motionZ ) * 10.0F + 1.0F );
			f *= ( float ) Math.pow( 2.0D, this.motionY );

			this.animTime += f;

			// ----------------------------------------------------------------------------------
			// manage ring buffer
			
			this.rotationYaw = MathHelper.wrapAngleTo180_float( this.rotationYaw ); // 
			
			if ( this.ringBufferIndex < 0 )
			{
				// reset ring buffer
				for ( int i = 0; i < this.ringBuffer.length; ++i )
				{
					this.ringBuffer[i][0] = ( double ) this.rotationYaw;
					this.ringBuffer[i][1] = this.posY;
				}
			}

			if ( ++this.ringBufferIndex == this.ringBuffer.length )
			{
				this.ringBufferIndex = 0;
			}

			this.ringBuffer[this.ringBufferIndex][0] = ( double ) this.rotationYaw;
			this.ringBuffer[this.ringBufferIndex][1] = this.posY;
			
			// ----------------------------------------------------------------------------------
			// Handle Movement
			
			double d0;
			double d1;
			double d2;
			double d10;
			float f12;

			if ( this.worldObj.isRemote )
			{
				// Adjust Rotation based on the received position from the server
				if ( this.newPosRotationIncrements > 0 )
				{
					d10 = this.posX + ( this.newPosX - this.posX ) / ( double ) this.newPosRotationIncrements;
					d0 = this.posY + ( this.newPosY - this.posY ) / ( double ) this.newPosRotationIncrements;
					d1 = this.posZ + ( this.newPosZ - this.posZ ) / ( double ) this.newPosRotationIncrements;
					d2 = MathHelper.wrapAngleTo180_double( this.newRotationYaw - ( double ) this.rotationYaw );
					this.rotationYaw = ( float ) ( ( double ) this.rotationYaw + d2 / ( double ) this.newPosRotationIncrements );
					this.rotationPitch = ( float ) ( ( double ) this.rotationPitch + ( this.newRotationPitch - ( double ) this.rotationPitch ) / ( double ) this.newPosRotationIncrements );
					--this.newPosRotationIncrements;
					this.setPosition( d10, d0, d1 );
					this.setRotation( this.rotationYaw, this.rotationPitch );
				}
			} else
			{
				if (!tamed)
					HandleWildBehaviour();
				if (riddenByEntity != null)
					HandleRiddenBehaviour();
					
				ManageFlightVelocity();
			}

			// ----------------------------------------------------------------------------------
			// Manage body part positioning/ration
			
			this.renderYawOffset = this.rotationYaw;
            this.dragonPartHead.width = this.dragonPartHead.height = 3.0F;
            this.dragonPartTail1.width = this.dragonPartTail1.height = 2.0F;
            this.dragonPartTail2.width = this.dragonPartTail2.height = 2.0F;
            this.dragonPartTail3.width = this.dragonPartTail3.height = 2.0F;
            this.dragonPartBody.height = 3.0F;
            this.dragonPartBody.width = 5.0F;
            this.dragonPartWing1.height = 2.0F;
            this.dragonPartWing1.width = 4.0F;
            this.dragonPartWing2.height = 3.0F;
            this.dragonPartWing2.width = 4.0F;
            f1 = (float)(this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F / 180.0F * (float)Math.PI;
            f2 = MathHelper.cos(f1);
            float f10 = -MathHelper.sin(f1);
            float f3 = this.rotationYaw * (float)Math.PI / 180.0F;
            float f11 = MathHelper.sin(f3);
            float f4 = MathHelper.cos(f3);
            this.dragonPartBody.onUpdate();
            this.dragonPartBody.setLocationAndAngles(this.posX + (double)(f11 * 0.5F), this.posY, this.posZ - (double)(f4 * 0.5F), 0.0F, 0.0F);
            this.dragonPartWing1.onUpdate();
            this.dragonPartWing1.setLocationAndAngles(this.posX + (double)(f4 * 4.5F), this.posY + 2.0D, this.posZ + (double)(f11 * 4.5F), 0.0F, 0.0F);
            this.dragonPartWing2.onUpdate();
            this.dragonPartWing2.setLocationAndAngles(this.posX - (double)(f4 * 4.5F), this.posY + 2.0D, this.posZ - (double)(f11 * 4.5F), 0.0F, 0.0F);

            if (!this.worldObj.isRemote && this.hurtTime == 0)
            {
                this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing1.boundingBox.expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
                this.collideWithEntities(this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.dragonPartWing2.boundingBox.expand(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D)));
            }

            double[] adouble1 = this.getMovementOffsets(5, 1.0F);
            double[] adouble = this.getMovementOffsets(0, 1.0F);
            f12 = MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            float f13 = MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F - this.randomYawVelocity * 0.01F);
            this.dragonPartHead.onUpdate();
            this.dragonPartHead.setLocationAndAngles(this.posX + (double)(f12 * 5.5F * f2), this.posY + (adouble[1] - adouble1[1]) * 1.0D + (double)(f10 * 5.5F), this.posZ - (double)(f13 * 5.5F * f2), 0.0F, 0.0F);

            for (int j = 0; j < 3; ++j)
            {
                EntityDragonPart entitydragonpart = null;

                if (j == 0)
                {
                    entitydragonpart = this.dragonPartTail1;
                }

                if (j == 1)
                {
                    entitydragonpart = this.dragonPartTail2;
                }

                if (j == 2)
                {
                    entitydragonpart = this.dragonPartTail3;
                }

                double[] adouble2 = this.getMovementOffsets(12 + j * 2, 1.0F);
                float f14 = this.rotationYaw * (float)Math.PI / 180.0F + MathHelper.wrapAngleTo180_float((float)(adouble2[0] - adouble1[0])) * (float)Math.PI / 180.0F * 1.0F;
                float f15 = MathHelper.sin(f14);
                float f16 = MathHelper.cos(f14);
                float f17 = 1.5F;
                float f18 = (float)(j + 1) * 2.0F;
                entitydragonpart.onUpdate();
                entitydragonpart.setLocationAndAngles(this.posX - (double)((f11 * f17 + f15 * f18) * f2), this.posY + (adouble2[1] - adouble1[1]) * 1.0D - (double)((f18 + f17) * f10) + 1.5D, this.posZ + (double)((f4 * f17 + f16 * f18) * f2), 0.0F, 0.0F);
            }
			
			// ----------------------------------------------------------------------------------
			// Attack
			if ( !this.worldObj.isRemote )
			{
				HandleWildAttack();
			}
		}
	}

	@Override
	public boolean interact(EntityPlayer player) {
		System.out.println( "Interact" );
		if(tamed) {
			if (riddenByEntity != null) {
				return super.interact( player );
			}
			else {
				player.rotationYaw = this.rotationYaw;
				player.rotationPitch = this.rotationPitch;

		        if (!this.worldObj.isRemote)
		        {
		        	player.mountEntity( this );
		        }
			}
		}
		
		return super.interact( player );
	}
	
	/**
	 * In thewild the wild the dragon flies around his nest. 
	 * Landing occasionally and is hostile to players and sometimes even the fauna.
	 */
	protected void HandleWildBehaviour() {
		// ----------------------------------------------------------------------------------
		// Manage Movement of the dragon
		// -> set rotation
		double flDistanceToSpawn = this.spawnPos.distanceTo( Vec3.createVectorHelper( this.posX, this.posY, this.posZ ) );
		if ( flDistanceToSpawn > this.territoryRange )
		{
			SetFlightDirection( 
					this.spawnPos.xCoord - this.posX,
					this.spawnPos.yCoord - this.posY,
					this.spawnPos.zCoord - this.posZ);
		} else
		{
			SetFlightDirection( 
					this.motionX,
					this.motionY,
					this.motionZ);
		}
		
		// ----------------------------------------------------------------------------------
		// Handle Targets
		
		this.target = null;
		EntityLiving closestEntity = null;
		EntityClientPlayerMP closestPlayer = null;
		double flClosestDistance = 99999.0F;

		// Search for nearest player/mob
		for ( int i = 0; i < this.worldObj.playerEntities.size( ); i++ )
		{
			Object o = this.worldObj.playerEntities.get( i );
			if ( o == null )
				continue;

			if ( !( o instanceof EntityClientPlayerMP ) )
				continue;

			EntityClientPlayerMP e = ( EntityClientPlayerMP ) o;

			double flDistanceToEntity = this.spawnPos.distanceTo( Vec3.createVectorHelper( e.posX, e.posY, e.posZ ) );
			if ( flDistanceToEntity < this.attackRange )
			{
				if ( flDistanceToEntity < flClosestDistance )
				{
					closestPlayer = e;
					flClosestDistance = flDistanceToEntity;
				}
			}
		}

		for ( int i = 0; i < this.worldObj.loadedEntityList.size( ); i++ )
		{
			Entity e = ( Entity ) this.worldObj.loadedEntityList.get( i );

			if ( e == null )
				continue;
			if ( !( e instanceof EntityLiving ) )
				continue;
			if ( e.getDistanceToEntity( this ) <= 0.1F )
				continue;
			double flDistanceToEntity = this.spawnPos.distanceTo( Vec3.createVectorHelper( e.posX, e.posY, e.posZ ) );
			if ( flDistanceToEntity < this.attackRange )
			{
				if ( flDistanceToEntity < flClosestDistance )
				{
					closestEntity = ( EntityLiving ) e;
					flClosestDistance = flDistanceToEntity;
				}
			}
		}
		
		// Set target to closest player/mob
		if ( closestPlayer != null )
			this.target = closestPlayer;
		if ( closestPlayer != null && closestEntity != null && isAggressive) {
			if (this.spawnPos.distanceTo( Vec3.createVectorHelper( closestPlayer.posX, closestPlayer.posY, closestPlayer.posZ )) >
				this.spawnPos.distanceTo( Vec3.createVectorHelper( closestEntity.posX, closestEntity.posY, closestEntity.posZ )))
				this.target = closestEntity;
		}
		else if (closestEntity != null && isAggressive)
			this.target = closestEntity;
		
		if (target != null)
			if (mobState != MobState.Attacking)
				mobState = MobState.Alerted;
		else
			mobState = MobState.Idle;
		
		// ----------------------------------------------------------------------------------
		// Manage flight height
		
		// Fly up when entity is collided
		if ( this.isCollided )
			this.addVelocity( 0, 0.1F, 0 );
		
		// Maintain height
		else
		{
			if ( this.posY > ( this.spawnPos.yCoord + 5 ) )
				this.addVelocity( 0, -0.1F, 0 );

			if ( this.posY < ( this.spawnPos.yCoord - 5 ) )
				this.addVelocity( 0, 0.1F, 0 );
		}
	}

	protected void HandleRiddenBehaviour() {
		
	}
	
	/**
	 * In the wild the dragon spits fireballs at the player/mob while flying in the air.
	 * On the ground he attacks his target with tail wipes and bites.
	 */
	protected void HandleWildAttack() {
		if ( this.target != null )
		{

			//System.out.println( this.target.getCommandSenderName( ) );
			attackTrigger++;
			if ( ( attackTrigger % 50 ) == 0 )
			{
				mobState = MobState.Attacking;
				attackTrigger = 0;

				this.worldObj.playAuxSFXAtEntity( ( EntityPlayer ) null, 1008, ( int ) this.posX, ( int ) this.posY, ( int ) this.posZ, 0 );

				double d8 = 1.0D;
				Vec3 vec3 = this.getLook( 1.0F );

				double fbX = this.dragonPartHead.posX - vec3.xCoord * d8;
				double fbY = this.dragonPartHead.posY + ( double ) ( this.dragonPartHead.height / 2.0F ) + 0.5D;
				double fbZ = this.dragonPartHead.posZ - vec3.zCoord * d8;

				EntityGuidedFireball fb = new EntityGuidedFireball( this.worldObj, this, this.target.posX - fbX, this.target.posY - fbY, this.target.posZ - fbZ );

				fb.posX = fbX;
				fb.posY = fbY;
				fb.posZ = fbZ;

				this.worldObj.spawnEntityInWorld( fb );

			}
		}
	}
	
	public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_) {
		if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer)
        {
            this.prevRotationYaw = this.rotationYaw = ((EntityPlayer)this.riddenByEntity).cameraYaw;
            this.rotationPitch = ((EntityPlayer)this.riddenByEntity).cameraPitch;
            this.setRotation(this.rotationYaw, this.rotationPitch);
            p_70612_1_ = ((EntityPlayer)this.riddenByEntity).moveStrafing * 0.5F;
            p_70612_2_ = ((EntityPlayer)this.riddenByEntity).moveForward;
            
            if (!this.worldObj.isRemote)
            {
                this.setAIMoveSpeed((float)this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue());
                super.moveEntityWithHeading(p_70612_1_, p_70612_2_);
            }
        }
	}
	
	public void updateRiderPosition()
    {
        super.updateRiderPosition();
    }
	
	/**
	 * Pushes all entities inside the list away from the enderdragon.
	 */
 	private void collideWithEntities( List pCollidedEntities )
	{
		double d0 = ( this.dragonPartBody.boundingBox.minX + this.dragonPartBody.boundingBox.maxX ) / 2.0D;
		double d1 = ( this.dragonPartBody.boundingBox.minZ + this.dragonPartBody.boundingBox.maxZ ) / 2.0D;
		Iterator iterator = pCollidedEntities.iterator( );

		while ( iterator.hasNext( ) )
		{
			Entity entity = ( Entity ) iterator.next( );

			double d2 = entity.posX - d0;
			double d3 = entity.posZ - d1;
			double d4 = d2 * d2 + d3 * d3;
			entity.addVelocity( d2 / d4 * 4.0D, 0.2D, d3 / d4 * 4.0D );
		}
	}

	public boolean attackEntityFromPart( EntityDragonPart pPart, DamageSource pDamageSource, float p_70965_3_ )
	{
		if ( pPart != this.dragonPartHead )
		{
			p_70965_3_ = p_70965_3_ / 4.0F + 1.0F;
		}

		//if ( /*pDamageSource.getEntity( ) instanceof EntityLiving || */pDamageSource.isExplosion( ) )
		//{
		this.func_82195_e( pDamageSource, p_70965_3_ );
		//}

		return true;
	}

	/**
	 * Called when the entity is attacked.
	 */
	public boolean attackEntityFrom( DamageSource source, float amount )
	{
		//System.out.println( p_70097_1_.damageType + ":" + p_70097_2_ );
		if ( source.getEntity( ) != null )
			this.target = source.getEntity( );
		//this.setDead( );

		if ( !source.equals( DamageSource.fall ) && !source.equals( DamageSource.inWall ) )
			return super.attackEntityFrom( source, amount );
		return false;
	}

	protected boolean func_82195_e( DamageSource source, float amount )
	{
		return this.attackEntityFrom( source, amount );
	}

	/**
	 * handles entity death timer, experience orb and particle creation
	 */
	protected void onDeathUpdate( )
	{
		++this.deathTicks;
		
		if ( !this.worldObj.isRemote )
		{
			if ( this.deathTicks == 1 )
				this.entityDropItem( new ItemStack( DAItems.dragonScale, rand.nextInt( 4 ) + 1 ), 1.0F );
			
			int i = 2000, j = 0;

			while ( i > 0 )
			{
				j = EntityXPOrb.getXPSplit( i );
				i -= j;
				this.worldObj.spawnEntityInWorld( new EntityXPOrb( this.worldObj, this.posX, this.posY, this.posZ, j ) );
			}

			this.setDead( );
		}
	}

	/**
	 * Makes the entity despawn if requirements are reached
	 */
	protected void despawnEntity( )
	{}

	/**
	 * Return the Entity parts making up this Entity (currently only for
	 * dragons)
	 */
	public Entity[] getParts( )
	{
		return this.dragonPartArray;
	}

	/**
	 * Returns true if other Entities should be prevented from moving through
	 * this Entity.
	 */
	public boolean canBeCollidedWith( )
	{
		return true;
	}

	public World func_82194_d( )
	{
		return this.worldObj;
	}

	/**
	 * Returns the sound this mob makes while it's alive.
	 */
	protected String getLivingSound( )
	{
		return "mob.enderdragon.growl";
	}

	/**
	 * Returns the sound this mob makes when it is hurt.
	 */
	protected String getHurtSound( )
	{
		return "mob.enderdragon.hit";
	}

	/**
	 * Returns the volume for the sounds this mob makes.
	 */
	protected float getSoundVolume( )
	{
		return 10.0F;
	}

	public Entity getTarget() {
		return this.target;
	}
}