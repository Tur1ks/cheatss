package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;

import java.awt.*;
import java.util.Map;

import foby.client.module.modules.ModuleManager;
import foby.client.module.modules.render.ChinaHat;
import foby.client.themes.Theme;
import foby.client.utils.color.ColorUtil;
import foby.client.utils.math.MathUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;

import static foby.client.Foby.themesUtil;
import static foby.client.utils.Mine.mc;

public class CustomHeadLayer<T extends LivingEntity, M extends EntityModel<T> & HeadedModel> extends RenderLayer<T, M> {
    private final float scaleX;
    private final float scaleY;
    private final float scaleZ;
    private final Map<SkullBlock.Type, SkullModelBase> skullModels;
    private final ItemInHandRenderer itemInHandRenderer;

    public CustomHeadLayer(RenderLayerParent<T, M> pRenderer, EntityModelSet pModelSet, ItemInHandRenderer pItemInHandRenderer) {
        this(pRenderer, pModelSet, 1.0F, 1.0F, 1.0F, pItemInHandRenderer);
    }

    public CustomHeadLayer(
            RenderLayerParent<T, M> pRenderer, EntityModelSet pModelSet, float pScaleX, float pScaleY, float pScaleZ, ItemInHandRenderer pItemInHandRenderer
    ) {
        super(pRenderer);
        this.scaleX = pScaleX;
        this.scaleY = pScaleY;
        this.scaleZ = pScaleZ;
        this.skullModels = SkullBlockRenderer.createSkullRenderers(pModelSet);
        this.itemInHandRenderer = pItemInHandRenderer;
    }

    public void render(
            PoseStack pPoseStack,
            MultiBufferSource pBuffer,
            int pPackedLight,
            T pLivingEntity,
            float pLimbSwing,
            float pLimbSwingAmount,
            float pPartialTicks,
            float pAgeInTicks,
            float pNetHeadYaw,
            float pHeadPitch
    ) {
        ItemStack itemstack = pLivingEntity.getItemBySlot(EquipmentSlot.HEAD);
        if (!itemstack.isEmpty()) {
            Item item = itemstack.getItem();
            pPoseStack.pushPose();
            pPoseStack.scale(this.scaleX, this.scaleY, this.scaleZ);
            boolean flag = pLivingEntity instanceof Villager || pLivingEntity instanceof ZombieVillager;
            if (pLivingEntity.isBaby() && !(pLivingEntity instanceof Villager)) {
                float f = 2.0F;
                float f1 = 1.4F;
                pPoseStack.translate(0.0F, 0.03125F, 0.0F);
                pPoseStack.scale(0.7F, 0.7F, 0.7F);
                pPoseStack.translate(0.0F, 1.0F, 0.0F);
            }

            this.getParentModel().getHead().translateAndRotate(pPoseStack);
            if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
                float f2 = 1.1875F;
                pPoseStack.scale(1.1875F, -1.1875F, -1.1875F);
                if (flag) {
                    pPoseStack.translate(0.0F, 0.0625F, 0.0F);
                }

                ResolvableProfile resolvableprofile = itemstack.get(DataComponents.PROFILE);
                pPoseStack.translate(-0.5, 0.0, -0.5);
                SkullBlock.Type skullblock$type = ((AbstractSkullBlock)((BlockItem)item).getBlock()).getType();
                SkullModelBase skullmodelbase = this.skullModels.get(skullblock$type);
                RenderType rendertype = SkullBlockRenderer.getRenderType(skullblock$type, resolvableprofile);
                WalkAnimationState walkanimationstate;
                if (pLivingEntity.getVehicle() instanceof LivingEntity livingentity) {
                    walkanimationstate = livingentity.walkAnimation;
                } else {
                    walkanimationstate = pLivingEntity.walkAnimation;
                }

                float f3 = walkanimationstate.position(pPartialTicks);
                SkullBlockRenderer.renderSkull(null, 180.0F, f3, pPoseStack, pBuffer, pPackedLight, skullmodelbase, rendertype);
            } else if (!(item instanceof ArmorItem armoritem) || armoritem.getEquipmentSlot() != EquipmentSlot.HEAD) {
                translateToHead(pPoseStack, flag);
                this.itemInHandRenderer.renderItem(pLivingEntity, itemstack, ItemDisplayContext.HEAD, false, pPoseStack, pBuffer, pPackedLight);
            }

            pPoseStack.popPose();
        }

        if (ModuleManager.getModule(ChinaHat.class).isEnabled() &&
                pLivingEntity == mc.player) {
            float hatWidth = 1f;
            float hatHeight = 0.5f;
            float translateHatInY = 0.2f;
            float rotationY = (float) MathUtils.interpolate(pHeadPitch, pLivingEntity.xRotO, pPartialTicks);

            Theme currentTheme = themesUtil.getCurrentStyle();
            java.awt.Color upColor = new Color(currentTheme.colors[0]);
            java.awt.Color downColor = new Color(currentTheme.colors[3]);
            upColor = new Color(upColor.getRed(), upColor.getGreen(), upColor.getBlue(), 100);
            downColor = new Color(downColor.getRed(), downColor.getGreen(), downColor.getBlue(), 100);

            Tesselator tessellator = Tesselator.getInstance();
            RenderSystem.enableDepthTest();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 771);
            RenderSystem.depthMask(true);
            RenderSystem.disableCull();
            RenderSystem.lineWidth(1f);
            PoseStack poseStack = new PoseStack();
            poseStack.last().pose().set(pPoseStack.last().pose());
            poseStack.pushPose();
            translateToHead(poseStack, false);
            poseStack.translate(0.0F, translateHatInY, 0.0F);
            poseStack.mulPose(Axis.YN.rotationDegrees(pNetHeadYaw));
            poseStack.translate(0.0F, -hatHeight / 2.0F, 0.0F);
            poseStack.mulPose(Axis.XN.rotationDegrees(rotationY));
            poseStack.translate(0.0F, hatHeight / 2.0F, 0.0F);
            poseStack.translate(0.0F, translateHatInY, 0.0F);
            BufferBuilder buffer = tessellator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            buffer.addVertex(poseStack.last().pose(), 0, hatHeight, 0).setColor(downColor.getRGB());
            for (int i = 0; i <= 360; i++) {
                float angle = (float) Math.toRadians(i);
                buffer.addVertex(poseStack.last().pose(), Mth.cos(angle) * hatWidth, 0, Mth.sin(angle) * hatWidth).setColor(upColor.getRGB());
            }
            tessellator.draw(buffer);
            poseStack.popPose();
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.enableTexture();
            RenderSystem.enableCull();
        }
    }

    public static void translateToHead(PoseStack pPoseStack, boolean pIsVillager) {
        float f = 0.625F;
        pPoseStack.translate(0.0F, -0.25F, 0.0F);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        pPoseStack.scale(0.625F, -0.625F, -0.625F);
        if (pIsVillager) {
            pPoseStack.translate(0.0F, 0.1875F, 0.0F);
        }
    }
}