package virtuoel.pehkui.mixin.client.compat117plus.compat1193minus;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import virtuoel.pehkui.util.MixinConstants;
import virtuoel.pehkui.util.ScaleRenderUtils;
import virtuoel.pehkui.util.ScaleUtils;

@Mixin(value = ItemRenderer.class, priority = 1010)
public class ItemRendererMixin
{
	@Inject(method = MixinConstants.RENDER_ITEM_WITH_SEED, at = @At(value = "HEAD"), remap = false)
	private void pehkui$renderItem$head(@Nullable LivingEntity entity, ItemStack item, @Coerce Object renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed, CallbackInfo info)
	{
		if (ScaleRenderUtils.shouldSkipHeadItemScaling(entity, item, renderMode))
		{
			return;
		}
		
		ScaleRenderUtils.logIfItemRenderCancelled();
		
		matrices.push();
		
		if (!item.isEmpty() && entity != null)
		{
			final float tickDelta = MinecraftClient.getInstance().getTickDelta();
			final float scale = ScaleUtils.getHeldItemScale(entity, tickDelta);
			
			if (scale != 1.0F)
			{
				matrices.scale(scale, scale, scale);
			}
		}
		
		matrices.push();
		
		ScaleRenderUtils.saveLastRenderedItem(item);
	}
	
	@Inject(method = MixinConstants.RENDER_ITEM_WITH_SEED, at = @At(value = "RETURN"), remap = false)
	private void pehkui$renderItem$return(@Nullable LivingEntity entity, ItemStack item, @Coerce Object renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed, CallbackInfo info)
	{
		if (ScaleRenderUtils.shouldSkipHeadItemScaling(entity, item, renderMode))
		{
			return;
		}
		
		ScaleRenderUtils.clearLastRenderedItem();
		
		matrices.pop();
		matrices.pop();
	}
}
