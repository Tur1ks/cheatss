package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;

public class SplashParticle extends WaterDropParticle {
    SplashParticle(ClientLevel p_107929_, double p_107930_, double p_107931_, double p_107932_, double p_107933_, double p_107934_, double p_107935_) {
        super(p_107929_, p_107930_, p_107931_, p_107932_);
        this.gravity = 0.04F;
        if (p_107934_ == 0.0 && (p_107933_ != 0.0 || p_107935_ != 0.0)) {
            this.xd = p_107933_;
            this.yd = 0.1;
            this.zd = p_107935_;
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        public Particle createParticle(
            SimpleParticleType pType,
            ClientLevel pLevel,
            double pX,
            double pY,
            double pZ,
            double pXSpeed,
            double pYSpeed,
            double pZSpeed
        ) {
            SplashParticle splashparticle = new SplashParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
            splashparticle.pickSprite(this.sprite);
            return splashparticle;
        }
    }
}