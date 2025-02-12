package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LlamaSpitRenderer;
import net.minecraft.world.entity.EntityType;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class ModelAdapterLlamaSpit extends ModelAdapter {
    public ModelAdapterLlamaSpit() {
        super(EntityType.LLAMA_SPIT, "llama_spit", 0.0F);
    }

    @Override
    public Model makeModel() {
        return new LlamaSpitModel(bakeModelLayer(ModelLayers.LLAMA_SPIT));
    }

    @Override
    public ModelPart getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof LlamaSpitModel llamaspitmodel)) {
            return null;
        } else if (modelPart.equals("body")) {
            return llamaspitmodel.root().getChildModelDeep("main");
        } else {
            return modelPart.equals("root") ? llamaspitmodel.root() : null;
        }
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"body", "root"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize, RendererCache rendererCache, int index) {
        EntityRenderDispatcher entityrenderdispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        LlamaSpitRenderer llamaspitrenderer = new LlamaSpitRenderer(entityrenderdispatcher.getContext());
        if (!Reflector.RenderLlamaSpit_model.exists()) {
            Config.warn("Field not found: RenderLlamaSpit.model");
            return null;
        } else {
            Reflector.setFieldValue(llamaspitrenderer, Reflector.RenderLlamaSpit_model, modelBase);
            llamaspitrenderer.shadowRadius = shadowSize;
            return llamaspitrenderer;
        }
    }
}