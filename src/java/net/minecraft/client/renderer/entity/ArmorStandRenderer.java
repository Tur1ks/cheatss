package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.model.ArmorStandArmorModel;
import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.ArmorStand;

public class ArmorStandRenderer extends LivingEntityRenderer<ArmorStand, ArmorStandArmorModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/armorstand/wood.png");

    public ArmorStandRenderer(EntityRendererProvider.Context p_173915_) {
        super(p_173915_, new ArmorStandModel(p_173915_.bakeLayer(ModelLayers.ARMOR_STAND)), 0.0F);
        this.addLayer(
            new HumanoidArmorLayer<>(
                this,
                new ArmorStandArmorModel(p_173915_.bakeLayer(ModelLayers.ARMOR_STAND_INNER_ARMOR)),
                new ArmorStandArmorModel(p_173915_.bakeLayer(ModelLayers.ARMOR_STAND_OUTER_ARMOR)),
                p_173915_.getModelManager()
            )
        );
        this.addLayer(new ItemInHandLayer<>(this, p_173915_.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer<>(this, p_173915_.getModelSet()));
        this.addLayer(new CustomHeadLayer<>(this, p_173915_.getModelSet(), p_173915_.getItemInHandRenderer()));
    }

    public ResourceLocation getTextureLocation(ArmorStand pEntity) {
        return DEFAULT_SKIN_LOCATION;
    }

    protected void setupRotations(ArmorStand pEntity, PoseStack pPoseStack, float pBob, float pYBodyRot, float pPartialTick, float pScale) {
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - pYBodyRot));
        float f = (float)(pEntity.level().getGameTime() - pEntity.lastHit) + pPartialTick;
        if (f < 5.0F) {
            pPoseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(f / 1.5F * (float) Math.PI) * 3.0F));
        }
    }

    protected boolean shouldShowName(ArmorStand pEntity) {
        double d0 = this.entityRenderDispatcher.distanceToSqr(pEntity);
        float f = pEntity.isCrouching() ? 32.0F : 64.0F;
        return d0 >= (double)(f * f) ? false : pEntity.isCustomNameVisible();
    }

    @Nullable
    protected RenderType getRenderType(ArmorStand pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing) {
        if (!pLivingEntity.isMarker()) {
            return super.getRenderType(pLivingEntity, pBodyVisible, pTranslucent, pGlowing);
        } else {
            ResourceLocation resourcelocation = this.getTextureLocation(pLivingEntity);
            if (pTranslucent) {
                return RenderType.entityTranslucent(resourcelocation, false);
            } else {
                return pBodyVisible ? RenderType.entityCutoutNoCull(resourcelocation, false) : null;
            }
        }
    }
}