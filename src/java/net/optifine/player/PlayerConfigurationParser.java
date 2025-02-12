package net.optifine.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.optifine.Config;
import net.optifine.http.HttpPipeline;
import net.optifine.http.HttpUtils;
import net.optifine.util.Json;

public class PlayerConfigurationParser {
    private String player = null;
    public static final String CONFIG_ITEMS = "items";
    public static final String ITEM_TYPE = "type";
    public static final String ITEM_ACTIVE = "active";

    public PlayerConfigurationParser(String player) {
        this.player = player;
    }

    public PlayerConfiguration parsePlayerConfiguration(JsonElement je) {
        if (je == null) {
            throw new JsonParseException("JSON object is null, player: " + this.player);
        } else {
            JsonObject jsonobject = (JsonObject)je;
            PlayerConfiguration playerconfiguration = new PlayerConfiguration();
            JsonArray jsonarray = (JsonArray)jsonobject.get("items");
            if (jsonarray != null) {
                for (int i = 0; i < jsonarray.size(); i++) {
                    JsonObject jsonobject1 = (JsonObject)jsonarray.get(i);
                    boolean flag = Json.getBoolean(jsonobject1, "active", true);
                    if (flag) {
                        String s = Json.getString(jsonobject1, "type");
                        if (s == null) {
                            Config.warn("Item type is null, player: " + this.player);
                        } else {
                            String s1 = Json.getString(jsonobject1, "model");
                            if (s1 == null) {
                                s1 = "items/" + s + "/model.cfg";
                            }

                            PlayerItemModel playeritemmodel = this.downloadModel(s1);
                            if (playeritemmodel != null) {
                                if (!playeritemmodel.isUsePlayerTexture()) {
                                    String s2 = Json.getString(jsonobject1, "texture");
                                    if (s2 == null) {
                                        s2 = "items/" + s + "/users/" + this.player + ".png";
                                    }

                                    NativeImage nativeimage = this.downloadTextureImage(s2);
                                    if (nativeimage == null) {
                                        continue;
                                    }

                                    playeritemmodel.setTextureImage(nativeimage);
                                    ResourceLocation resourcelocation = new ResourceLocation("optifine.net", s2);
                                    playeritemmodel.setTextureLocation(resourcelocation);
                                }

                                playerconfiguration.addPlayerItemModel(playeritemmodel);
                            }
                        }
                    }
                }
            }

            return playerconfiguration;
        }
    }

    private NativeImage downloadTextureImage(String texturePath) {
        String s = HttpUtils.getPlayerItemsUrl() + "/" + texturePath;

        try {
            byte[] abyte = HttpPipeline.get(s, Minecraft.getInstance().getProxy());
            return NativeImage.read(new ByteArrayInputStream(abyte));
        } catch (IOException ioexception) {
            Config.warn("Error loading item texture " + texturePath + ": " + ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return null;
        }
    }

    private PlayerItemModel downloadModel(String modelPath) {
        String s = HttpUtils.getPlayerItemsUrl() + "/" + modelPath;

        try {
            byte[] abyte = HttpPipeline.get(s, Minecraft.getInstance().getProxy());
            String s1 = new String(abyte, "ASCII");
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = (JsonObject)jsonparser.parse(s1);
            return PlayerItemParser.parseItemModel(jsonobject);
        } catch (Exception exception) {
            Config.warn("Error loading item model " + modelPath + ": " + exception.getClass().getName() + ": " + exception.getMessage());
            return null;
        }
    }
}