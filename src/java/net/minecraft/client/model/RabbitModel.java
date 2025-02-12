package net.minecraft.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Rabbit;

public class RabbitModel<T extends Rabbit> extends EntityModel<T> {
    private static final float REAR_JUMP_ANGLE = 50.0F;
    private static final float FRONT_JUMP_ANGLE = -40.0F;
    private static final String LEFT_HAUNCH = "left_haunch";
    private static final String RIGHT_HAUNCH = "right_haunch";
    private final ModelPart leftRearFoot;
    private final ModelPart rightRearFoot;
    private final ModelPart leftHaunch;
    private final ModelPart rightHaunch;
    private final ModelPart body;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart head;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart tail;
    private final ModelPart nose;
    private float jumpRotation;
    private static final float NEW_SCALE = 0.6F;

    public RabbitModel(ModelPart pRoot) {
        this.leftRearFoot = pRoot.getChild("left_hind_foot");
        this.rightRearFoot = pRoot.getChild("right_hind_foot");
        this.leftHaunch = pRoot.getChild("left_haunch");
        this.rightHaunch = pRoot.getChild("right_haunch");
        this.body = pRoot.getChild("body");
        this.leftFrontLeg = pRoot.getChild("left_front_leg");
        this.rightFrontLeg = pRoot.getChild("right_front_leg");
        this.head = pRoot.getChild("head");
        this.rightEar = pRoot.getChild("right_ear");
        this.leftEar = pRoot.getChild("left_ear");
        this.tail = pRoot.getChild("tail");
        this.nose = pRoot.getChild("nose");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild(
            "left_hind_foot",
            CubeListBuilder.create().texOffs(26, 24).addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F),
            PartPose.offset(3.0F, 17.5F, 3.7F)
        );
        partdefinition.addOrReplaceChild(
            "right_hind_foot",
            CubeListBuilder.create().texOffs(8, 24).addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F),
            PartPose.offset(-3.0F, 17.5F, 3.7F)
        );
        partdefinition.addOrReplaceChild(
            "left_haunch",
            CubeListBuilder.create().texOffs(30, 15).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F),
            PartPose.offsetAndRotation(3.0F, 17.5F, 3.7F, (float) (-Math.PI / 9), 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "right_haunch",
            CubeListBuilder.create().texOffs(16, 15).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F),
            PartPose.offsetAndRotation(-3.0F, 17.5F, 3.7F, (float) (-Math.PI / 9), 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "body",
            CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -10.0F, 6.0F, 5.0F, 10.0F),
            PartPose.offsetAndRotation(0.0F, 19.0F, 8.0F, (float) (-Math.PI / 9), 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "left_front_leg",
            CubeListBuilder.create().texOffs(8, 15).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F),
            PartPose.offsetAndRotation(3.0F, 17.0F, -1.0F, (float) (-Math.PI / 18), 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "right_front_leg",
            CubeListBuilder.create().texOffs(0, 15).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F),
            PartPose.offsetAndRotation(-3.0F, 17.0F, -1.0F, (float) (-Math.PI / 18), 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "head", CubeListBuilder.create().texOffs(32, 0).addBox(-2.5F, -4.0F, -5.0F, 5.0F, 4.0F, 5.0F), PartPose.offset(0.0F, 16.0F, -1.0F)
        );
        partdefinition.addOrReplaceChild(
            "right_ear",
            CubeListBuilder.create().texOffs(52, 0).addBox(-2.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F),
            PartPose.offsetAndRotation(0.0F, 16.0F, -1.0F, 0.0F, (float) (-Math.PI / 12), 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "left_ear",
            CubeListBuilder.create().texOffs(58, 0).addBox(0.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F),
            PartPose.offsetAndRotation(0.0F, 16.0F, -1.0F, 0.0F, (float) (Math.PI / 12), 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "tail",
            CubeListBuilder.create().texOffs(52, 6).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F),
            PartPose.offsetAndRotation(0.0F, 20.0F, 7.0F, -0.3490659F, 0.0F, 0.0F)
        );
        partdefinition.addOrReplaceChild(
            "nose", CubeListBuilder.create().texOffs(32, 9).addBox(-0.5F, -2.5F, -5.5F, 1.0F, 1.0F, 1.0F), PartPose.offset(0.0F, 16.0F, -1.0F)
        );
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, int pColor) {
        if (this.young) {
            float f = 1.5F;
            pPoseStack.pushPose();
            pPoseStack.scale(0.56666666F, 0.56666666F, 0.56666666F);
            pPoseStack.translate(0.0F, 1.375F, 0.125F);
            ImmutableList.of(this.head, this.leftEar, this.rightEar, this.nose)
                .forEach(p_340882_ -> p_340882_.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor));
            pPoseStack.popPose();
            pPoseStack.pushPose();
            pPoseStack.scale(0.4F, 0.4F, 0.4F);
            pPoseStack.translate(0.0F, 2.25F, 0.0F);
            ImmutableList.of(this.leftRearFoot, this.rightRearFoot, this.leftHaunch, this.rightHaunch, this.body, this.leftFrontLeg, this.rightFrontLeg, this.tail)
                .forEach(p_340888_ -> p_340888_.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor));
            pPoseStack.popPose();
        } else {
            pPoseStack.pushPose();
            pPoseStack.scale(0.6F, 0.6F, 0.6F);
            pPoseStack.translate(0.0F, 1.0F, 0.0F);
            ImmutableList.of(
                    this.leftRearFoot,
                    this.rightRearFoot,
                    this.leftHaunch,
                    this.rightHaunch,
                    this.body,
                    this.leftFrontLeg,
                    this.rightFrontLeg,
                    this.head,
                    this.rightEar,
                    this.leftEar,
                    this.tail,
                    this.nose
                )
                .forEach(p_340876_ -> p_340876_.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor));
            pPoseStack.popPose();
        }
    }

    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        float f = pAgeInTicks - (float)pEntity.tickCount;
        this.nose.xRot = pHeadPitch * (float) (Math.PI / 180.0);
        this.head.xRot = pHeadPitch * (float) (Math.PI / 180.0);
        this.rightEar.xRot = pHeadPitch * (float) (Math.PI / 180.0);
        this.leftEar.xRot = pHeadPitch * (float) (Math.PI / 180.0);
        this.nose.yRot = pNetHeadYaw * (float) (Math.PI / 180.0);
        this.head.yRot = pNetHeadYaw * (float) (Math.PI / 180.0);
        this.rightEar.yRot = this.nose.yRot - (float) (Math.PI / 12);
        this.leftEar.yRot = this.nose.yRot + (float) (Math.PI / 12);
        this.jumpRotation = Mth.sin(pEntity.getJumpCompletion(f) * (float) Math.PI);
        this.leftHaunch.xRot = (this.jumpRotation * 50.0F - 21.0F) * (float) (Math.PI / 180.0);
        this.rightHaunch.xRot = (this.jumpRotation * 50.0F - 21.0F) * (float) (Math.PI / 180.0);
        this.leftRearFoot.xRot = this.jumpRotation * 50.0F * (float) (Math.PI / 180.0);
        this.rightRearFoot.xRot = this.jumpRotation * 50.0F * (float) (Math.PI / 180.0);
        this.leftFrontLeg.xRot = (this.jumpRotation * -40.0F - 11.0F) * (float) (Math.PI / 180.0);
        this.rightFrontLeg.xRot = (this.jumpRotation * -40.0F - 11.0F) * (float) (Math.PI / 180.0);
    }

    public void prepareMobModel(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
        super.prepareMobModel(pEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
        this.jumpRotation = Mth.sin(pEntity.getJumpCompletion(pPartialTick) * (float) Math.PI);
    }
}