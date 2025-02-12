package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Chicken;

public class ChickenRenderer extends MobRenderer<Chicken, ChickenModel<Chicken>> {
    private static final ResourceLocation CHICKEN_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/chicken.png");

    public ChickenRenderer(EntityRendererProvider.Context p_173952_) {
        super(p_173952_, new ChickenModel<>(p_173952_.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
    }

    public ResourceLocation getTextureLocation(Chicken pEntity) {
        return CHICKEN_LOCATION;
    }

    protected float getBob(Chicken pLivingBase, float pPartialTicks) {
        float f = Mth.lerp(pPartialTicks, pLivingBase.oFlap, pLivingBase.flap);
        float f1 = Mth.lerp(pPartialTicks, pLivingBase.oFlapSpeed, pLivingBase.flapSpeed);
        return (Mth.sin(f) + 1.0F) * f1;
    }
}