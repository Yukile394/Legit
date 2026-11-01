/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.passive.PassiveEntity
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.decoration.ArmorStandEntity
 *  net.minecraft.entity.mob.HostileEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.ProjectileUtil
 *  net.minecraft.item.AxeItem
 *  net.minecraft.item.MiningToolItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.SwordItem
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$PositionAndOnGround
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket$Mode
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.hit.EntityHitResult
 */
package com.legit.client.module.impl;

import com.legit.client.LegitClient;
import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.module.impl.AimAssistModule;
import com.legit.client.module.impl.AutoFireworkModule;
import com.legit.client.module.impl.KeystrokesCpsModule;
import com.legit.client.setting.BooleanSetting;
import com.legit.client.setting.ModeSetting;
import com.legit.client.setting.NumberSetting;
import net.minecraft.util.Hand;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.AxeItem;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.EntityHitResult;

public class TriggerBotModule
extends Module {
    public final ModeSetting critMode = this.addSetting(new ModeSetting("Criticals Mode", "T\u00fcm e\u015fyalarda kritik vuru\u015f modu", "Smart Crit (All Items)", "Smart Crit (All Items)", "Always Crit (AutoJump)", "Strict Crit Only", "Off"));
    public final ModeSetting itemFilter = this.addSetting(new ModeSetting("Allowed Items", "Hangi e\u015fyalarla vurulaca\u011f\u0131 (All Items: K\u0131l\u0131\u00e7, Balta, Kazma, Totem, Blok dahil her \u015feyle kritik atar)", "All Items (Her \u015eey)", "All Items (Her \u015eey)", "Weapons & Tools", "Sword & Axe Only"));
    public final NumberSetting cooldownThreshold = this.addSetting(new NumberSetting("Cooldown %", "Sald\u0131r\u0131 bar\u0131 doluluk e\u015fi\u011fi (%100 tam doluluk bekler)", 100.0, 70.0, 100.0, 1.0, "%"));
    public final NumberSetting extraCooldownMs = this.addSetting(new NumberSetting("Extra Cooldown", "Bar %100 dolduktan sonra sunucu senkronu i\u00e7in ek bekleme s\u00fcresi", 95.0, 0.0, 400.0, 10.0, "ms"));
    public final NumberSetting minItemCooldownMs = this.addSetting(new NumberSetting("Min Attack Delay", "Di\u011fer e\u015fyalarda (Totem, Blok, El, Kazma) iki vuru\u015f aras\u0131 minimum bekleme s\u00fcresi", 640.0, 300.0, 1400.0, 20.0, "ms"));
    public final NumberSetting slotSwitchCooldownMs = this.addSetting(new NumberSetting("Slot Switch Wait", "E\u015fya/Slot de\u011fi\u015ftirdikten sonra vuru\u015f \u00f6ncesi bekleme s\u00fcresi", 160.0, 0.0, 500.0, 20.0, "ms"));
    public final NumberSetting attackRange = this.addSetting(new NumberSetting("Attack Reach", "Hedef tarama ve vuru\u015f mesafesi", 3.4, 2.8, 5.0, 0.1, "m"));
    public final NumberSetting hitboxExpand = this.addSetting(new NumberSetting("Hitbox Tolerance", "I\u015f\u0131n taramas\u0131nda hedef vuru\u015f kutusu geni\u015fletme pay\u0131", 0.18, 0.0, 0.6, 0.02, "m"));
    public final NumberSetting reactionDelay = this.addSetting(new NumberSetting("Reaction Delay", "Hedef ni\u015fangaha girdi\u011finde bekleme s\u00fcresi", 15.0, 0.0, 250.0, 5.0, "ms"));
    public final BooleanSetting aimAssistSync = this.addSetting(new BooleanSetting("AimAssist Sync", "AimAssist hedefe kilitliyken yak\u0131n mesafede ka\u00e7\u0131rmadan an\u0131nda vurur", true));
    public final BooleanSetting sprintStopCrit = this.addSetting(new BooleanSetting("Packet Crit Sync", "Vuru\u015f paketinden hemen \u00f6nce STOP_SPRINTING g\u00f6ndererek ko\u015farken bile %100 kritik atar", true));
    public final BooleanSetting groundPacketCrit = this.addSetting(new BooleanSetting("Ground Packet Crit", "Yerde dururken bile mini pozisyon paketi g\u00f6ndererek kritik vuru\u015f zorlar", false));
    public final BooleanSetting hurtTimeSync = this.addSetting(new BooleanSetting("HurtTime Sync", "Rakip hasar dokunulmazl\u0131\u011f\u0131ndayken (k\u0131rm\u0131z\u0131 yanarken) bo\u015fa vuru\u015f yapmaz", false));
    public final BooleanSetting extraCritParticles = this.addSetting(new BooleanSetting("Crit Particles", "Kritik vuru\u015f indi\u011finde ekstra g\u00f6rsel kritik efekti g\u00f6sterir", true));
    public final BooleanSetting liveRaycast = this.addSetting(new BooleanSetting("Same-Tick Raycast", "AimAssist d\u00f6n\u00fc\u015f\u00fcnden hemen sonra anl\u0131k \u0131\u015f\u0131n taramas\u0131yla ayn\u0131 tickte vurur", true));
    public final BooleanSetting autoAxeShield = this.addSetting(new BooleanSetting("Auto Axe Shield", "Rakip kalkan tutuyorsa otomatik baltaya ge\u00e7ip k\u0131rar ve eski e\u015fyaya d\u00f6ner", true));
    public final BooleanSetting pauseWhileEating = this.addSetting(new BooleanSetting("Pause On Eat/Use", "Alt\u0131n elma yerken veya iksir i\u00e7erken duraklar", true));
    public final BooleanSetting targetPlayers = this.addSetting(new BooleanSetting("Target Players", "Oyunculara sald\u0131r", true));
    public final BooleanSetting targetMobs = this.addSetting(new BooleanSetting("Target Hostiles", "Canavarlara sald\u0131r", true));
    public final BooleanSetting targetAnimals = this.addSetting(new BooleanSetting("Target Passives", "Pasif hayvanlara sald\u0131r", false));
    public final BooleanSetting targetInvisibles = this.addSetting(new BooleanSetting("Target Invisibles", "G\u00f6r\u00fcnmez hedeflere sald\u0131r", true));
    private long lastAttackTime = 0L;
    private long fullChargeReachedTime = 0L;
    private long lastSlotChangeTime = 0L;
    private int lastSelectedSlot = -1;
    private long targetAcquiredTime = 0L;
    private int lastTargetId = -1;
    private LivingEntity currentLockedTarget = null;
    private int sessionHits = 0;
    private int sessionCrits = 0;
    private int savedWeaponSlot = -1;
    private long axeSwapTimestamp = 0L;

    public TriggerBotModule() {
        super("TriggerBot", "Art\u0131r\u0131lm\u0131\u015f tam Cooldown senkronu ile K\u0131l\u0131\u00e7, Balta ve t\u00fcm e\u015fyalarda %100 Kritik vuru\u015f", Category.COMBAT, 82);
    }

    @Override
    public String getSuffix() {
        return String.format("%d%% +%dms", this.cooldownThreshold.getIntValue(), this.extraCooldownMs.getIntValue());
    }

    public LivingEntity getCurrentLockedTarget() {
        return this.currentLockedTarget;
    }

    public boolean isPreparingCrit(MinecraftClient mc) {
        if (!this.isEnabled() || this.currentLockedTarget == null || mc.player == null) {
            return false;
        }
        return !this.critMode.isMode("Off") && !mc.player.isOnGround() && mc.player.fallDistance > 0.0f;
    }

    @Override
    public void onDisable() {
        this.currentLockedTarget = null;
        this.lastTargetId = -1;
        this.savedWeaponSlot = -1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void onTick(MinecraftClient mc) {
        KeystrokesCpsModule ks;
        boolean sendSprintStop;
        Item held;
        PlayerEntity targetPlayer;
        long reactionMs;
        boolean airborneFalling;
        AutoFireworkModule afw;
        float requiredRatio;
        float realCharge;
        if (mc.player == null || mc.world == null || mc.interactionManager == null || mc.currentScreen != null) {
            this.currentLockedTarget = null;
            return;
        }
        long now = System.currentTimeMillis();
        int currentSlot = mc.player.getInventory().selectedSlot;
        if (currentSlot != this.lastSelectedSlot) {
            this.lastSelectedSlot = currentSlot;
            this.lastSlotChangeTime = now;
            this.fullChargeReachedTime = 0L;
        }
        if (this.savedWeaponSlot != -1 && now - this.axeSwapTimestamp >= 85L) {
            if (this.savedWeaponSlot >= 0 && this.savedWeaponSlot < 9) {
                mc.player.getInventory().selectedSlot = this.savedWeaponSlot;
            }
            this.savedWeaponSlot = -1;
        }
        if ((realCharge = mc.player.getAttackCooldownProgress(0.0f)) >= (requiredRatio = (float)((Double)this.cooldownThreshold.getValue() / 100.0))) {
            if (this.fullChargeReachedTime == 0L) {
                this.fullChargeReachedTime = now;
            }
        } else {
            this.fullChargeReachedTime = 0L;
        }
        if (this.pauseWhileEating.isEnabled() && mc.player.isUsingItem()) {
            this.currentLockedTarget = null;
            return;
        }
        if (LegitClient.getInstance() != null && (afw = LegitClient.getInstance().getModuleManager().getModule(AutoFireworkModule.class)) != null && afw.isBusySwapping()) {
            return;
        }
        if (!this.isAllowedHeldItem(mc)) {
            this.currentLockedTarget = null;
            return;
        }
        LivingEntity living = this.resolveTargetEntity(mc);
        if (living == null) {
            this.currentLockedTarget = null;
            this.lastTargetId = -1;
            return;
        }
        this.currentLockedTarget = living;
        if (living.getId() != this.lastTargetId) {
            this.lastTargetId = living.getId();
            this.targetAcquiredTime = now;
        }
        if (now - this.lastSlotChangeTime < (long)this.slotSwitchCooldownMs.getIntValue()) {
            return;
        }
        boolean isElytraPass = mc.player.isFallFlying() || living.isFallFlying();
        boolean bl = airborneFalling = !mc.player.isOnGround() && !mc.player.isTouchingWater() && !mc.player.isInLava() && !mc.player.isClimbing() && !mc.player.hasVehicle() && mc.player.fallDistance > 0.02f && mc.player.getVelocity().y < -0.02;
        if (!this.isFullCooldownReady(mc, now)) {
            return;
        }
        if (this.hurtTimeSync.isEnabled() && living.hurtTime > 3) {
            return;
        }
        long l = reactionMs = isElytraPass || airborneFalling ? 0L : (long)this.reactionDelay.getIntValue();
        if (reactionMs > 0L && now - this.targetAcquiredTime < reactionMs) {
            return;
        }
        if (living instanceof PlayerEntity && (targetPlayer = (PlayerEntity)living).isBlocking() && !((held = mc.player.getMainHandStack().getItem()) instanceof AxeItem)) {
            if (!this.autoAxeShield.isEnabled()) return;
            int axeSlot = this.findHotbarAxe(mc);
            if (axeSlot == -1) return;
            this.savedWeaponSlot = mc.player.getInventory().selectedSlot;
            mc.player.getInventory().selectedSlot = axeSlot;
            this.axeSwapTimestamp = now;
        }
        if (!isElytraPass && !this.critMode.isMode("Off")) {
            boolean inWaterOrWeb;
            boolean bl2 = inWaterOrWeb = mc.player.isTouchingWater() || mc.player.isInLava() || mc.player.isClimbing() || mc.player.hasVehicle();
            if (!inWaterOrWeb) {
                if (this.critMode.isMode("Always Crit (AutoJump)")) {
                    if (mc.player.isOnGround()) {
                        mc.player.jump();
                        return;
                    }
                    if (!airborneFalling) {
                        return;
                    }
                } else if (this.critMode.isMode("Strict Crit Only")) {
                    if (!airborneFalling) {
                        return;
                    }
                } else {
                    if (mc.player.isOnGround() && mc.options.jumpKey.isPressed()) {
                        return;
                    }
                    if (!mc.player.isOnGround() && !airborneFalling) {
                        return;
                    }
                }
            }
        }
        boolean wasSprinting = mc.player.isSprinting();
        boolean bl3 = sendSprintStop = this.sprintStopCrit.isEnabled() && wasSprinting && !mc.player.isFallFlying();
        if (sendSprintStop && mc.getNetworkHandler() != null) {
            mc.player.setSprinting(false);
            mc.getNetworkHandler().sendPacket((Packet)new ClientCommandC2SPacket((Entity)mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }
        if (this.groundPacketCrit.isEnabled() && mc.player.isOnGround() && !mc.player.isTouchingWater() && !mc.player.isInLava() && mc.getNetworkHandler() != null) {
            double px = mc.player.getX();
            double py = mc.player.getY();
            double pz = mc.player.getZ();
            mc.getNetworkHandler().sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(px, py + 0.0625, pz, false));
            mc.getNetworkHandler().sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(px, py + 1.0E-4, pz, false));
        }
        mc.interactionManager.attackEntity((PlayerEntity)mc.player, (Entity)living);
        mc.player.swingHand(Hand.MAIN_HAND);
        if (sendSprintStop && mc.getNetworkHandler() != null && mc.player.forwardSpeed > 0.0f) {
            mc.player.setSprinting(true);
            mc.getNetworkHandler().sendPacket((Packet)new ClientCommandC2SPacket((Entity)mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
        }
        ++this.sessionHits;
        if (airborneFalling) {
            ++this.sessionCrits;
            if (this.extraCritParticles.isEnabled()) {
                mc.particleManager.addEmitter((Entity)living, (ParticleEffect)ParticleTypes.CRIT);
            }
        }
        if (LegitClient.getInstance() != null && (ks = LegitClient.getInstance().getModuleManager().getModule(KeystrokesCpsModule.class)) != null) {
            ks.recordClick(0);
        }
        this.lastAttackTime = now;
        this.fullChargeReachedTime = 0L;
    }

    private boolean isFullCooldownReady(MinecraftClient mc, long now) {
        Item held = mc.player.getMainHandStack().getItem();
        long minDelay = this.minItemCooldownMs.getIntValue();
        if (held instanceof AxeItem) {
            minDelay = Math.max(minDelay, 1020L);
        } else if (held instanceof SwordItem) {
            minDelay = Math.max(minDelay, 630L);
        }
        if (now - this.lastAttackTime < minDelay) {
            return false;
        }
        float requiredRatio = (float)((Double)this.cooldownThreshold.getValue() / 100.0);
        if (mc.player.getAttackCooldownProgress(0.0f) < requiredRatio) {
            return false;
        }
        long extraWait = this.extraCooldownMs.getIntValue();
        return extraWait <= 0L || this.fullChargeReachedTime <= 0L || now - this.fullChargeReachedTime >= extraWait;
    }

    private LivingEntity resolveTargetEntity(MinecraftClient mc) {
        Entity entity;
        HitResult hit = mc.crosshairTarget;
        if (hit != null && hit.getType() == HitResult.Type.ENTITY && this.isValidTarget(mc, entity = ((EntityHitResult)hit).getEntity())) {
            return (LivingEntity)entity;
        }
        if (this.liveRaycast.isEnabled() && mc.player != null) {
            LivingEntity locked;
            AimAssistModule aim;
            Entity rayEntity;
            Box searchBox;
            Vec3d lookVec;
            Vec3d endPos;
            double reach = (Double)this.attackRange.getValue();
            double expand = (Double)this.hitboxExpand.getValue();
            Vec3d eyePos = mc.player.getEyePos();
            EntityHitResult rayHit = ProjectileUtil.raycast((Entity)mc.player, (Vec3d)eyePos, (Vec3d)(endPos = eyePos.add((lookVec = mc.player.getRotationVec(1.0f)).multiply(reach))), (Box)(searchBox = mc.player.getBoundingBox().stretch(lookVec.multiply(reach)).expand(1.5 + expand)), e -> !e.isSpectator() && e.canHit() && this.isValidTarget(mc, (Entity)e), (double)(reach * reach));
            if (rayHit != null && (rayEntity = rayHit.getEntity()) instanceof LivingEntity) {
                LivingEntity living = (LivingEntity)rayEntity;
                return living;
            }
            if (expand > 0.0 || this.aimAssistSync.isEnabled()) {
                for (Entity e2 : mc.world.getOtherEntities((Entity)mc.player, searchBox, ent -> this.isValidTarget(mc, (Entity)ent))) {
                    Box expandedBox = e2.getBoundingBox().expand((double)e2.getTargetingMargin() + expand);
                    if (!expandedBox.raycast(eyePos, endPos).isPresent() && !expandedBox.contains(eyePos)) continue;
                    return (LivingEntity)e2;
                }
            }
            if (this.aimAssistSync.isEnabled() && LegitClient.getInstance() != null && (aim = LegitClient.getInstance().getModuleManager().getModule(AimAssistModule.class)) != null && aim.isEnabled() && aim.getActiveTarget() != null && this.isValidTarget(mc, (Entity)(locked = aim.getActiveTarget())) && (double)mc.player.distanceTo((Entity)locked) <= reach) {
                return locked;
            }
        }
        return null;
    }

    private int findHotbarAxe(MinecraftClient mc) {
        for (int i = 0; i < 9; ++i) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isEmpty() || !(s.getItem() instanceof AxeItem)) continue;
            return i;
        }
        return -1;
    }

    private boolean isAllowedHeldItem(MinecraftClient mc) {
        if (this.itemFilter.isMode("All Items (Her \u015eey)")) {
            return true;
        }
        Item item = mc.player.getMainHandStack().getItem();
        if (this.itemFilter.isMode("Weapons & Tools")) {
            return item instanceof SwordItem || item instanceof AxeItem || item instanceof MiningToolItem;
        }
        return item instanceof SwordItem || item instanceof AxeItem;
    }

    private boolean isValidTarget(MinecraftClient mc, Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        LivingEntity living = (LivingEntity)entity;
        if (entity == mc.player || !living.isAlive() || living.isDead() || living.getHealth() <= 0.0f) {
            return false;
        }
        if (entity instanceof ArmorStandEntity) {
            return false;
        }
        if (entity.isInvisible() && !this.targetInvisibles.isEnabled()) {
            return false;
        }
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            if (player.isSpectator() || player.isCreative()) {
                return false;
            }
            return this.targetPlayers.isEnabled();
        }
        if (entity instanceof HostileEntity) {
            return this.targetMobs.isEnabled();
        }
        if (entity instanceof PassiveEntity) {
            return this.targetAnimals.isEnabled();
        }
        return this.targetMobs.isEnabled();
    }
}

