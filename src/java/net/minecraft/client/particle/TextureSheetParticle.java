package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public abstract class TextureSheetParticle extends SingleQuadParticle {
    protected TextureAtlasSprite sprite;

    protected TextureSheetParticle(ClientLevel pLevel, double pX, double pY, double pZ) {
        super(pLevel, pX, pY, pZ);
    }

    protected TextureSheetParticle(
        ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
    ) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
    }

    protected void setSprite(TextureAtlasSprite pSprite) {
        this.sprite = pSprite;
    }

    @Override
    protected float getU0() {
        return this.sprite.getU0();
    }

    @Override
    protected float getU1() {
        return this.sprite.getU1();
    }

    @Override
    protected float getV0() {
        return this.sprite.getV0();
    }

    @Override
    protected float getV1() {
        return this.sprite.getV1();
    }

    public void pickSprite(SpriteSet pSprite) {
        this.setSprite(pSprite.get(this.random));
    }

    public void setSpriteFromAge(SpriteSet pSprite) {
        if (!this.removed) {
            this.setSprite(pSprite.get(this.age, this.lifetime));
        }
    }
}