package io.github.magicalbananapie.arcanespace.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.magicalbananapie.arcanespace.ArcaneConfig;
import io.github.magicalbananapie.arcanespace.ArcaneSpace;
import io.github.magicalbananapie.arcanespace.blockentity.BlockEntityGravityFocus;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

//TODO: Rework this again, it still doesn't look good enough
public class GravityFocusRenderer extends BlockEntityRenderer<BlockEntityGravityFocus> {
    private static final SpriteIdentifier CORE_TEXTURE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, ArcaneSprites.CORE_PATH);
    private static final Sprite CORE_SPRITE = CORE_TEXTURE.getSprite();
    ArcaneConfig.VisualConfig config = AutoConfig.getConfigHolder(ArcaneConfig.class).getConfig().visualConfig;
    private static final List<RenderLayer> renderLayers = IntStream.range(0, 4).mapToObj(
            (i) -> getEndPortal(i + 1)).collect(ImmutableList.toImmutableList());
    private static final Random RANDOM = new Random(31100L);
    private final ModelPart core = new ModelPart(32, 16, 0, 0); //black cube that should be able to change size.
    //private final ModelPart eventHorizon = new ModelPart(64, 32, 0, 0);; //Might go unused, but would be an extra visual layer around core, charged creeper texture
    private static final List<Vector3f> vertices = new ArrayList<>();
    private static final List<Vector3f> uvs = new ArrayList<>();

    public GravityFocusRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
        this.core.addCuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
        //this.eventHorizon.addCuboid(-3.0F, -3.0F, -3.0F, 10.0F, 10.0F, 10.0F);
        //Event Horizon uses creeper armor or creeper charge texture
    }

    @Override
    public void render(BlockEntityGravityFocus entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        if (config.sphereEnabled) {
            if (config.sphereComplexity > 0) {
                matrices.translate(0.5f, 0.5f, 0.5f);
                matrices.scale(0.25f, 0.25f, 0.25f);
                VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getCutout());
                //NOTICE: This might create some slight lag (probably less than starfield unless the sphere is complex)
                // If I just make it calculate once if empty it will be faster, however, this implementation
                // allows one to change the complexity mid-game.
                calcVertices(config.sphereComplexity * 2, config.sphereComplexity);
                MatrixStack.Entry entry = matrices.peek();
                Vector3f vertex, uv;
                //Notice: Currently backwards, which means you cant see past event horizon, when forwards its a bubble like the starfield
                for (int i = 0; i < vertices.size(); i+=4/*i++ for forwards*/) {
                    for (int j = 3; j > -1; j--) {/*Remove loop for forwards*/
                        vertex = vertices.get(i/*Remove j for forwards*/ + j);
                        uv = uvs.get(i/*Remove j for forwards*/ + j);
                        vertexConsumer.vertex(entry.getModel(), vertex.getX(), vertex.getY(), vertex.getZ()).color(0, 0, 0, 255).texture(((CORE_SPRITE.getMaxU() - (CORE_SPRITE.getMinU() + CORE_SPRITE.getMaxU() / 2)) * uv.getX()) + (CORE_SPRITE.getMinU() + CORE_SPRITE.getMaxU() / 4), ((CORE_SPRITE.getMaxV() - CORE_SPRITE.getMinV()) * uv.getY()) + CORE_SPRITE.getMinV()).overlay(overlay).light(light).normal(entry.getNormal(), vertex.getX(), vertex.getY(), vertex.getZ()).next();
                    }
                }
            }
        } else {
            matrices.translate(0.5D, 0.5D, 0.5D);
            this.core.render(matrices, CORE_TEXTURE.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutoutNoCull), light, overlay);
        } matrices.pop();
        RANDOM.setSeed(31100L);
        //TODO: Experiment more to figure out how this works, It still doesn't do exactly what I want, I want some bigger stars
        int renderLayer = this.getRenderLayer(entity.getPos().getSquaredDistance(this.dispatcher.camera.getPos(), true));
        Matrix4f model = matrices.peek().getModel();
        for (int layer = 0; layer < renderLayer; layer++) {
            this.faces(2.0F / (float) (18 - layer * 4), model, vertexConsumers.getBuffer(renderLayers.get(layer)));
        }
    }

    private void faces(float x, Matrix4f model, VertexConsumer vertexConsumer) {
        float r = (RANDOM.nextFloat() * 0.5F + 0.1F) * x; //RED
        float g = (RANDOM.nextFloat() * 0.5F + 0.4F) * x; //GREEN
        float b = (RANDOM.nextFloat() * 0.5F + 0.5F) * x; //BLUE
        float a = 1.00F; //ALPHA
        float max = 1.0F;
        float min = 0.0F;
        float offset = 0.001F;

        //Inverse Faces
        this.vertices(model, vertexConsumer, min, max, min, max, min + offset, min + offset, min + offset, min + offset, r, g, b, a); //South face, moved to old North Face but offset a little
        this.vertices(model, vertexConsumer, min, max, max, min, max - offset, max - offset, max - offset, max - offset, r, g, b, a); //North face, moved to old South Face but offset a little
        this.vertices(model, vertexConsumer, min + offset, min + offset, max, min, min, max, max, min, r, g, b, a); //East face, moved to old West Face but offset a little
        this.vertices(model, vertexConsumer, max - offset, max - offset, min, max, min, max, max, min, r, g, b, a); //West face, moved to old East Face but offset a little
        this.vertices(model, vertexConsumer, min, max, max - offset, max - offset, min, min, max, max, r, g, b, a); //Bottom Face, moved to just under top face
        this.vertices(model, vertexConsumer, min, max, min + offset, min + offset, max, max, min, min, r, g, b, a); //Top Face, moved to just above bottom face
    }

    private void vertices(Matrix4f model, VertexConsumer vertexConsumer, float x1, float x2, float y1, float y2, float z1, float z2, float z3, float z4, float r, float g, float b, float a) {
        vertexConsumer.vertex(model, x1, y1, z1).color(r, g, b, a).next();
        vertexConsumer.vertex(model, x2, y1, z2).color(r, g, b, a).next();
        vertexConsumer.vertex(model, x2, y2, z3).color(r, g, b, a).next();
        vertexConsumer.vertex(model, x1, y2, z4).color(r, g, b, a).next();
    }

    //TODO: figure how how to make more stars to appear closer to the core
    //NOTICE: UPDATE: Adding a 2d texture like a conduit's eye that stays locked on you with more stars on it may work,
    // (Above might not work in item form) or preferably, find a way to change the math or make a spherical region.
    //By changing the return values of these by 1, it shifts the display forwards or backwards by 1 layer,
    // so find a way to shift the render layers based on position within the block
    //TODO: Maybe change these values if the RenderLayer needs them changed to look good
    protected int getRenderLayer(double distance) {
        if (distance > 65536.0D) {
            return 0;
        } else if (distance > 16384.0D) {
            return 1;
        } else if (distance > 4096.0D) {
            return 2;
        } else return 3;
    }


    private static void calcVertices(int sectorCount, int stackCount) {
        vertices.clear();
        uvs.clear();

        //TODO: Make this run faster, the cube is noticeably faster, and while this is mostly unrelated, it can only help
        double right = Math.PI / 2;
        double angle = Math.PI / stackCount;
        float sectorStep = 2 * (float) Math.PI / sectorCount;
        for (int i = 0; i <= stackCount - 1; i++) {
            float xy = (float) Math.cos(right - i * angle);
            float xy2 = (float) Math.cos(right - (i + 1) * angle);
            float z = (float) Math.sin(right - i * angle);
            float z2 = (float) Math.sin(right - (i + 1) * angle);

            for (int j = 0; j <= sectorCount - 1; j++) {
                float sectorAngle = j * sectorStep;

                vertices.add(new Vector3f(xy * (float) Math.cos(sectorAngle), xy * (float) Math.sin(sectorAngle), z));
                uvs.add(new Vector3f(0, 0, 0));

                vertices.add(new Vector3f(xy * (float) Math.cos(sectorAngle + sectorStep), xy * (float) Math.sin(sectorAngle + sectorStep), z));
                uvs.add(new Vector3f(1, 0, 0));

                vertices.add(new Vector3f(xy2 * (float) Math.cos(sectorAngle + sectorStep), xy2 * (float) Math.sin(sectorAngle + sectorStep), z2));
                uvs.add(new Vector3f(1, 1, 0));

                vertices.add(new Vector3f(xy2 * (float) Math.cos(sectorAngle), xy2 * (float) Math.sin(sectorAngle), z2));
                uvs.add(new Vector3f(0, 1, 0));
            }
        }
    }

    public static RenderLayer getEndPortal(int layer) { //THIS IS BAD, BUT DISPOSE OF IT LATER (Make a better RenderLayer)
        //NOTICE: I WILL remove this AWFUL code later, but I need it for now to understand what I need and start the game
        RenderPhase.Transparency ADDITIVE_TRANSPARENCY = new RenderPhase.Transparency("additive_transparency", () -> {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE);
        }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        });
        RenderPhase.Transparency TRANSLUCENT_TRANSPARENCY = new RenderPhase.Transparency("translucent_transparency", () -> {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        });
        RenderPhase.Fog BLACK_FOG = new RenderPhase.Fog("black_fog", () -> {
            RenderSystem.fog(2918, 0.0F, 0.0F, 0.0F, 1.0F);
            RenderSystem.enableFog();
        }, () -> {
            BackgroundRenderer.setFogBlack();
            RenderSystem.disableFog();
        });
        RenderPhase.Transparency transparency2;
        RenderPhase.Texture texture2;
        if (layer <= 1) {
            transparency2 = TRANSLUCENT_TRANSPARENCY;
            //This makes layer 0 the background of the End Sky
            texture2 = new RenderPhase.Texture(EndPortalBlockEntityRenderer.SKY_TEXTURE, false, false);
        } else {
            transparency2 = ADDITIVE_TRANSPARENCY;
            texture2 = new RenderPhase.Texture(EndPortalBlockEntityRenderer.PORTAL_TEXTURE, false, false);
        }
        //TODO: Try changing this to make the starfield look a bit better
        return RenderLayer.of("spatial_field", VertexFormats.POSITION_COLOR,
                7, 256, false, true,
                RenderLayer.MultiPhaseParameters.builder().transparency(transparency2).texture(texture2).
                        texturing(new RenderPhase.Texturing("spatial_texturing", () -> {
                RenderSystem.matrixMode(5890);
                RenderSystem.pushMatrix();
                RenderSystem.loadIdentity();
                RenderSystem.translatef(0.5F, 0.5F, 0.0F);
                RenderSystem.scalef(0.5F, 0.5F, 1.0F);
                RenderSystem.translatef(17.0F / (float)layer*4, (2.0F + (float)layer*4 / 1.5F) * ((float)(Util.getMeasuringTimeMs() % 800000L) / 800000.0F), 0.0F);
                RenderSystem.rotatef(((float)(layer * layer)*4 * 4321.0F + (float)layer * 36.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
                RenderSystem.scalef(4.5F - (float)layer*4 / 4.0F, 4.5F - (float)layer, 1.0F);
                RenderSystem.mulTextureByProjModelView();
                RenderSystem.matrixMode(5888);
                RenderSystem.setupEndPortalTexGen();
            }, () -> {
                RenderSystem.matrixMode(5890);
                RenderSystem.popMatrix();
                RenderSystem.matrixMode(5888);
                RenderSystem.clearTexGen();
            })).fog(BLACK_FOG).build(false));
    }
}
