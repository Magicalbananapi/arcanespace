package io.github.magicalbananapie.arcanespace.blockentity;

import io.github.magicalbananapie.arcanespace.ArcaneConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Tickable;

public class BlockEntityGravityFocus extends BlockEntity implements Tickable { //NOTICE: Tickable due to G-Field checks
    private int gFieldSize;

    public BlockEntityGravityFocus() {
        super(ArcaneBlockEntities.GRAVITY_FOCUS);
        ArcaneConfig settings = AutoConfig.getConfigHolder(ArcaneConfig.class).getConfig();
        this.gFieldSize = settings.gFieldSize;

        //NOTICE: WARNING, INFO-DUMP INCOMING, I REPEAT, INFO-DUMP INCOMING
        //this.gFieldStrength = settings.gFieldStrength; TODO: Make G-Field Strength have a tick length, etc. and
        // TODO: make it such that g-foci vary in g-strength 'Middle' of planet size is 'glued' to 'End' of g-field size

        //Todo: consider this being an upgraded version of another gravity foci block that does not have the 'casing'
        // covering it, which would mean you don't stop getting pulled in and get Destroyed like in the void.
        // If this is the case, the 'casing' needs to be reworked slightly to have special functionality when you
        // step on the bottom of it, pushing you away from it, this would be most consistent, and therefore the
        // semitransparent end portal/gateway overlay needs to appear on top instead of the bottom of it, unless
        // I am to make the effect occur on the bottom of the panel and on the outside of the foci block slightly

        //Notice: The decision from the above is that Gravity Foci are upgraded gravity cores, or singularities,
        // which are also used for portal and other spatial devices, and is crafted from 6 gravity panels surrounding
        // a singularity/tesseract/whateverIdecideOnCallingTheCore and two other items or through a special crafting
        // method. Use structure void and conduit for help on making singularity, and the gravity panels need to be
        // reworked to act differently when interacting with the bottom, while also making them solid, to think of
        // themselves as a roof, and only change gravity strength to negative temporarily instead of direction.
        // Effect will surround the singularity always, while also appearing more transparently on the bottom of
        // panels and therefore the outside of the core block.

        //TODO: Decide if the Gravity focus block should also count as a special crafting block that compresses things
        //NOTICE: YOU ARE NOW SAFE, INFO-DUMP HAS ENDED, I REPEAT, INFO-DUMP HAS ENDED
    }

    //NOTICE: Functionality for blockEntity each tick, Conduit Block Entity provides the perfect example for this :)
    @Override
    public void tick() {
        /*if (this.world != null && this.pos != null) {
            Box gravityBox = (new Box(this.pos)).expand(20);
            Box gravityBox2 = (new Box(this.pos)).expand(20+5);
            List<Entity> list = this.world.getEntitiesByClass(Entity.class, gravityBox, e -> !(e instanceof PlayerEntity && ((PlayerEntity) e).abilities.flying));
            List<Entity> listb = this.world.getEntitiesByClass(Entity.class, gravityBox2, null);

            for (Entity entity : list) {
                Gravity gravity = ((EntityAccessor)entity).getGravity();

                listb.remove(entity);
                entity.setNoGravity(true);

                double testy = this.pos.getY() - entity.getPos().getY();

                if (testy < 0) {
                    testy = testy * -1;
                }

                double testx = this.pos.getX() - entity.getPos().getX();

                if (testx < 0) {
                    testx = testx * -1;
                }

                double testz = this.pos.getZ() - entity.getPos().getZ();

                if (testz < 0) {
                    testz = testz * -1;
                }

                if (testy >= testx && testy >= testz) {
                    double decresey = testy;

                    if (decresey < 0) {
                        decresey = 1;
                    }

                    if (this.pos.getY() < entity.getPos().getY()) {
                        if(decresey>(20*.6666)) {
                            decresey=decresey-((20*.6666)-1);
                            entity.setVelocity(entity.getVelocity().add(0, -.07 / decresey, 0));
                        }else{
                            entity.setVelocity(entity.getVelocity().add(0, -0.07, 0));
                        }

                        { gravity.setGravityDirection(entity, 0, false); }
                    }

                    if (this.pos.getY() > entity.getPos().getY()) {
                        if(decresey>(20*.6666)) {
                            decresey=decresey-((20*.6666)-1);
                            entity.setVelocity(entity.getVelocity().add(0, 0.07 / decresey, 0));
                        }else{
                            entity.setVelocity(entity.getVelocity().add(0, 0.07, 0));
                        }
                        if(entity.verticalCollision)
                        {
                            entity.setOnGround(true);
                        }

                        { gravity.setGravityDirection(entity, 1, false); }

                    }
                }

                if (testz >= testx && testz >= testy) {
                    double decresez = testz;

                    if (decresez < 0) {
                        decresez = 1;
                    }

                    if (this.pos.getZ() < entity.getPos().getZ()) {
                        if(decresez>(20*.6666)) {
                            decresez=decresez-((20*.6666)-1);
                            entity.setVelocity(entity.getVelocity().add(0, 0, -.07 / decresez));
                        }else{
                            entity.setVelocity(entity.getVelocity().add(0, 0, -0.07));
                        }
                        if(entity.horizontalCollision)
                        {
                            entity.setOnGround(true);
                        }

                        { gravity.setGravityDirection(entity, 2, false); }

                    }

                    if (this.pos.getZ() > entity.getPos().getZ()) {
                        if(decresez>(20*.6666)) {
                            decresez=decresez-((20*.6666)-1);
                            entity.setVelocity(entity.getVelocity().add(0, 0, 0.07 / decresez));
                        }else{
                            entity.setVelocity(entity.getVelocity().add(0, 0, 0.07));
                        }
                        if(entity.horizontalCollision)
                        {
                            entity.setOnGround(true);
                        }

                        { gravity.setGravityDirection(entity, 3, false); }

                    }
                }

                if (testx >= testy && testx >= testz) {
                    double decresex = testx;

                    if (decresex < 0) {
                        decresex = 1;
                    }

                    if (this.pos.getX() < entity.getPos().getX()) {
                        if(decresex>(20*.6666)) {
                            decresex=decresex-((20*.6666)-1);
                            entity.setVelocity(entity.getVelocity().add(-0.07 / decresex, 0, 0));
                        }else{
                            entity.setVelocity(entity.getVelocity().add(-0.07, 0, 0));
                        }
                        if(entity.horizontalCollision)
                        {
                            entity.setOnGround(true);
                        }

                        { gravity.setGravityDirection(entity, 5, false); }

                    }

                    if (this.pos.getX() > entity.getPos().getX()) {
                        if(decresex>(20*.6666)) {
                            decresex=decresex-((20*.6666)-1);
                            entity.setVelocity(entity.getVelocity().add(0.07 / decresex, 0, 0));
                        }else{
                            entity.setVelocity(entity.getVelocity().add(0.07, 0, 0));
                        }
                        if(entity.horizontalCollision)
                        {
                            entity.setOnGround(true);
                        }

                        { gravity.setGravityDirection(entity, 4, false);}
                    }
                }
            }

            for (Entity entity : listb) {
                entity.setNoGravity(false);

            }
        }*/
    }
}
