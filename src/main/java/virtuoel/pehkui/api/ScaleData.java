package virtuoel.pehkui.api;

import java.util.Objects;
import java.util.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import virtuoel.pehkui.entity.ResizableEntity;

public class ScaleData
{
	public static final ScaleData IDENTITY = new ImmutableScaleData(1.0F);
	
	public static ScaleData of(Entity entity)
	{
		return ((ResizableEntity) entity).pehkui_getScaleData();
	}
	
	protected float scale = 1.0F;
	protected float prevScale = 1.0F;
	protected float fromScale = 1.0F;
	protected float toScale = 1.0F;
	protected int scaleTicks = 0;
	protected int totalScaleTicks = 20;
	
	public boolean scaleModified = false;
	
	protected Optional<Runnable> calculateDimensions;
	
	public ScaleData(Optional<Runnable> calculateDimensions)
	{
		this.calculateDimensions = calculateDimensions;
	}
	
	public void tick()
	{
		final float currScale = getScale();
		if (currScale != getTargetScale())
		{
			this.prevScale = currScale;
			if (this.scaleTicks >= this.totalScaleTicks)
			{
				this.fromScale = this.toScale;
				this.scaleTicks = 0;
				setScale(this.toScale);
			}
			else
			{
				this.scaleTicks++;
				final float nextScale = this.scale + ((this.toScale - this.fromScale) / (float) this.totalScaleTicks);
				setScale(nextScale);
			}
		}
		else if (this.prevScale != currScale)
		{
			this.prevScale = currScale;
		}
	}
	
	public float getScale()
	{
		return this.scale;
	}
	
	public float getScale(float delta)
	{
		final float scale = getScale();
		return delta == 1.0F ? scale : MathHelper.lerp(delta, getPrevScale(), scale);
	}
	
	public void setScale(float scale)
	{
		this.prevScale = this.scale;
		this.scale = scale;
		calculateDimensions.ifPresent(Runnable::run);
	}
	
	public float getInitialScale()
	{
		return this.fromScale;
	}
	
	public float getTargetScale()
	{
		return this.toScale;
	}
	
	public void setTargetScale(float targetScale)
	{
		this.fromScale = this.scale;
		this.toScale = targetScale;
		this.scaleTicks = 0;
	}
	
	public int getScaleTickDelay()
	{
		return this.totalScaleTicks;
	}
	
	public void setScaleTickDelay(int ticks)
	{
		this.totalScaleTicks = ticks;
	}
	
	public float getPrevScale()
	{
		return this.prevScale;
	}
	
	public void markForSync()
	{
		this.scaleModified = true;
	}
	
	public boolean shouldSync()
	{
		return this.scaleModified;
	}
	
	public PacketByteBuf toPacketByteBuf(PacketByteBuf buffer)
	{
		buffer.writeFloat(this.scale)
		.writeFloat(this.prevScale)
		.writeFloat(this.fromScale)
		.writeFloat(this.toScale)
		.writeInt(this.scaleTicks)
		.writeInt(this.totalScaleTicks);
		return buffer;
	}
	
	public void fromTag(CompoundTag scaleData)
	{
		this.scale = scaleData.contains("scale") ? scaleData.getFloat("scale") : 1.0F;
		this.prevScale = scaleData.contains("previous") ? scaleData.getFloat("previous") : this.scale;
		this.fromScale = scaleData.contains("initial") ? scaleData.getFloat("initial") : this.scale;
		this.toScale = scaleData.contains("target") ? scaleData.getFloat("target") : this.scale;
		this.scaleTicks = scaleData.contains("ticks") ? scaleData.getInt("ticks") : 0;
		this.totalScaleTicks = scaleData.contains("total_ticks") ? scaleData.getInt("total_ticks") : 20;
		
		calculateDimensions.ifPresent(Runnable::run);
	}
	
	public CompoundTag toTag(CompoundTag tag)
	{
		tag.putFloat("scale", getScale());
		tag.putFloat("initial", getInitialScale());
		tag.putFloat("target", getTargetScale());
		tag.putInt("ticks", this.scaleTicks);
		tag.putInt("total_ticks", this.totalScaleTicks);
		return tag;
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(fromScale, prevScale, scale, scaleTicks, toScale, totalScaleTicks);
	}
	
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		
		if (!(obj instanceof ScaleData))
		{
			return false;
		}
		
		final ScaleData other = (ScaleData) obj;
		
		return Float.floatToIntBits(scale) == Float.floatToIntBits(other.scale) &&
			Float.floatToIntBits(prevScale) == Float.floatToIntBits(other.prevScale) &&
			Float.floatToIntBits(fromScale) == Float.floatToIntBits(other.fromScale) &&
			Float.floatToIntBits(toScale) == Float.floatToIntBits(other.toScale) &&
			scaleTicks == other.scaleTicks &&
			totalScaleTicks == other.totalScaleTicks;
	}
	
	public static class ImmutableScaleData extends ScaleData
	{
		public ImmutableScaleData(float scale)
		{
			super(Optional.empty());
			this.scale = scale;
			this.prevScale = scale;
			this.fromScale = scale;
			this.toScale = scale;
		}
		
		@Override
		public void tick()
		{
			
		}
		
		@Override
		public void setScale(float scale)
		{
			
		}
		
		@Override
		public void setTargetScale(float targetScale)
		{
			
		}
		
		@Override
		public void setScaleTickDelay(int ticks)
		{
			
		}
		
		@Override
		public void markForSync()
		{
			
		}
		
		@Override
		public void fromTag(CompoundTag scaleData)
		{
			
		}
	}
}