/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.decoration.ArmorStandEntity
 *  net.minecraft.entity.mob.HostileEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.AxeItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.SwordItem
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.math.MathHelper
 */
package com.legit.client.module.impl;

import com.legit.client.LegitClient;
import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.module.impl.AutoFireworkModule;
import com.legit.client.setting.BooleanSetting;
import com.legit.client.setting.ModeSetting;
import com.legit.client.setting.NumberSetting;
import java.util.Comparator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class AimAssistModule
extends Module {
    public final ModeSetting modePreset = this.addSetting(new ModeSetting("Combat Profile", "ElytraPvP hava sava\u015f\u0131 veya Kara PvP hedefleme modu", "ElytraPvP 3D", "ElytraPvP 3D", "Hybrid Auto", "Ground PvP"));
    public final ModeSetting lockMode = this.addSetting(new ModeSetting("Lock Mode", "Hedefe kilitlenme sertli\u011fi (Hard Lock = Tam Kitlenme)", "Hard Lock", "Hard Lock", "Magnetic Lock", "Sigmoid Smooth"));
    public final NumberSetting lockStrength = this.addSetting(new NumberSetting("Lock Strength", "Hedefe kilitlenme g\u00fcc\u00fc (%100 = An\u0131nda Tam Kitlenme)", 95.0, 15.0, 100.0, 5.0, "%"));
    public final ModeSetting priorityMode = this.addSetting(new ModeSetting("Priority", "Hedef se\u00e7me \u00f6nceli\u011fi", "FOV (Crosshair)", "FOV (Crosshair)", "Distance", "Lowest Health", "Gliding First"));
    public final ModeSetting aimBone = this.addSetting(new ModeSetting("Aim Point", "Hedeflenecek v\u00fccut noktas\u0131 (U\u00e7arken otomatik 0.6x0.6 merkezlenir)", "Core Lock", "Core Lock", "Center Mass", "Multipoint", "Neck / Head"));
    public final NumberSetting range = this.addSetting(new NumberSetting("Ground Range", "Yerde maksimum ni\u015fan kilitlenme mesafesi", 4.8, 2.5, 6.5, 0.1, "m"));
    public final NumberSetting elytraRange = this.addSetting(new NumberSetting("Elytra Range", "U\u00e7arken (Elytra) hedef takip ve kilitlenme mesafesi", 7.2, 3.5, 12.0, 0.2, "m"));
    public final NumberSetting fov = this.addSetting(new NumberSetting("Lock FOV", "Hedef yakalama g\u00f6r\u00fc\u015f a\u00e7\u0131s\u0131 limiti (derece)", 150.0, 20.0, 180.0, 5.0, "\u00b0"));
    public final NumberSetting horizontalSpeed = this.addSetting(new NumberSetting("Yaw Speed", "Yatay (Yaw) kilitlenme h\u0131z\u0131", 34.0, 2.0, 50.0, 1.0, ""));
    public final NumberSetting verticalSpeed = this.addSetting(new NumberSetting("Pitch Speed", "Dikey (Pitch) kilitlenme h\u0131z\u0131", 30.0, 2.0, 50.0, 1.0, ""));
    public final NumberSetting elytraSpeedMultiplier = this.addSetting(new NumberSetting("Elytra Boost x", "Havada s\u00fcz\u00fcl\u00fcrken kilitlenme h\u0131z\u0131 \u00e7arpan\u0131", 1.85, 1.0, 3.0, 0.05, "x"));
    public final NumberSetting elytraLeadTicks = this.addSetting(new NumberSetting("3D Velocity Lead", "Hedefin 3 boyutlu (X/Y/Z) hareket \u00f6nleme katsay\u0131s\u0131", 1.8, 0.0, 5.0, 0.1, "t"));
    public final NumberSetting closeInstantDist = this.addSetting(new NumberSetting("Close Instant Lock", "Bu mesafenin alt\u0131ndaki hedeflere %100 an\u0131nda kilitlenir", 3.6, 0.0, 6.0, 0.2, "m"));
    public final BooleanSetting strafeComp = this.addSetting(new BooleanSetting("Strafe Compensation", "Sa\u011fa-sola (A/D) strafe atarken kendi hareketinizi telafi ederek tam kilitler", true));
    public final BooleanSetting stickyLock = this.addSetting(new BooleanSetting("Dogfight Sticky Lock", "Kilitlenilen hedefi ani d\u00f6n\u00fc\u015flerde ve ge\u00e7i\u015flerde b\u0131rakmaz", true));
    public final BooleanSetting elytraTargetBypass = this.addSetting(new BooleanSetting("ElytraTarget Bypass", "Mouse GCD kuantalama ve fi\u015fek an\u0131 a\u00e7\u0131 senkronu ile ElytraTarget ban\u0131n\u0131 \u00f6nler", true));
    public final BooleanSetting throughWalls = this.addSetting(new BooleanSetting("Through Walls", "Engel arkas\u0131ndaki hedefi de takipte tutar", false));
    public final BooleanSetting drawFovCircle = this.addSetting(new BooleanSetting("Draw FOV Ring", "Ni\u015fangah etraf\u0131nda AimAssist g\u00f6r\u00fc\u015f a\u00e7\u0131s\u0131 halkas\u0131n\u0131 g\u00f6sterir", true));
    public final BooleanSetting verticalAssist = this.addSetting(new BooleanSetting("Pitch Assist", "Dikey eksende de ni\u015fangah\u0131 tam kilitler", true));
    public final BooleanSetting stopOnHitbox = this.addSetting(new BooleanSetting("Stop On Hitbox", "Ni\u015fangah hedefin d\u0131\u015f kenar\u0131na de\u011fdi\u011finde durur (Tam kitlenme i\u00e7in kapal\u0131 tutun)", false));
    public final BooleanSetting weaponOnly = this.addSetting(new BooleanSetting("Weapon Only", "Sadece k\u0131l\u0131\u00e7 veya balta tutarken \u00e7al\u0131\u015f\u0131r", false));
    public final BooleanSetting clickOnly = this.addSetting(new BooleanSetting("On Attack Hold", "Sadece sol t\u0131k bas\u0131l\u0131yken aktif olur", false));
    public final BooleanSetting targetPlayers = this.addSetting(new BooleanSetting("Target Players", "Oyuncular\u0131 hedefle", true));
    public final BooleanSetting targetMobs = this.addSetting(new BooleanSetting("Target Hostiles", "Yarat\u0131klar\u0131 hedefle", false));
    private LivingEntity activeTarget = null;
    private int lastLockedId = -1;
    private long lastLockedTime = 0L;
    private long lastFrameNs = 0L;

    public AimAssistModule() {
        super("AimAssist", "Sub-Tick Frame-Rate Hard-Lock ve ElytraPvP 3B h\u0131z vekt\u00f6r kilitlenme sistemi", Category.COMBAT, 86);
    }

    @Override
    public String getSuffix() {
        return (String)this.lockMode.getValue() + " " + this.lockStrength.getIntValue() + "%";
    }

    public LivingEntity getActiveTarget() {
        return this.activeTarget;
    }

    @Override
    public void onDisable() {
        this.activeTarget = null;
        this.lastLockedId = -1;
        this.lastFrameNs = 0L;
    }

    @Override
    public void onTick(MinecraftClient mc) {
        this.updateAimLock(mc, 1.0f, 0.05);
    }

    public void onRenderFrame(MinecraftClient mc, float tickDelta) {
        if (!this.isEnabled()) {
            return;
        }
        long nowNs = System.nanoTime();
        double dtSeconds = this.lastFrameNs == 0L ? 0.008 : (double)(nowNs - this.lastFrameNs) / 1.0E9;
        this.lastFrameNs = nowNs;
        dtSeconds = MathHelper.clamp((double)dtSeconds, (double)0.001, (double)0.05);
        this.updateAimLock(mc, tickDelta, dtSeconds);
    }

    private void updateAimLock(MinecraftClient mc, float tickDelta, double dtSeconds) {
        double vFactor;
        double hFactor;
        LivingEntity bestTarget;
        boolean aerialCombat;
        AutoFireworkModule afw;
        Item item;
        if (mc.player == null || mc.world == null || mc.currentScreen != null) {
            this.activeTarget = null;
            return;
        }
        if (this.weaponOnly.isEnabled() && !((item = mc.player.getMainHandStack().getItem()) instanceof SwordItem) && !(item instanceof AxeItem)) {
            this.activeTarget = null;
            return;
        }
        if (this.clickOnly.isEnabled() && !mc.options.attackKey.isPressed()) {
            this.activeTarget = null;
            return;
        }
        if (this.elytraTargetBypass.isEnabled() && LegitClient.getInstance() != null && (afw = LegitClient.getInstance().getModuleManager().getModule(AutoFireworkModule.class)) != null && afw.isBusySwapping()) {
            return;
        }
        boolean bl = aerialCombat = mc.player.isFallFlying() || this.modePreset.isMode("ElytraPvP 3D");
        if (!aerialCombat && this.stopOnHitbox.isEnabled() && mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            return;
        }
        this.activeTarget = bestTarget = this.findBestTarget(mc, aerialCombat);
        if (bestTarget == null) {
            return;
        }
        this.lastLockedId = bestTarget.getId();
        this.lastLockedTime = System.currentTimeMillis();
        boolean targetGliding = bestTarget.isFallFlying();
        boolean isElytraEngagement = aerialCombat || targetGliding || !mc.player.isOnGround() || !bestTarget.isOnGround();
        Vec3d eyePos = mc.player.getCameraPosVec(tickDelta);
        Vec3d targetPoint = this.computeTargetPoint(mc, bestTarget, eyePos, tickDelta, isElytraEngagement);
        double dx = targetPoint.x - eyePos.x;
        double dy = targetPoint.y - eyePos.y;
        double dz = targetPoint.z - eyePos.z;
        double horizDist = Math.sqrt(dx * dx + dz * dz);
        float desiredYaw = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float desiredPitch = (float)(-Math.toDegrees(Math.atan2(dy, horizDist)));
        float yawDelta = MathHelper.wrapDegrees((float)(desiredYaw - mc.player.getYaw()));
        float pitchDelta = MathHelper.wrapDegrees((float)(desiredPitch - mc.player.getPitch()));
        double distToTarget = mc.player.distanceTo((Entity)bestTarget);
        double strengthRatio = distToTarget <= (Double)this.closeInstantDist.getValue() ? 1.0 : (Double)this.lockStrength.getValue() / 100.0;
        double boost = isElytraEngagement ? (Double)this.elytraSpeedMultiplier.getValue() : 1.0;
        double frameScale = MathHelper.clamp((double)(dtSeconds / 0.016), (double)0.35, (double)2.5);
        if (this.lockMode.isMode("Hard Lock")) {
            double baseSnap = Math.min(1.0, (0.45 + 0.55 * strengthRatio) * Math.min(1.35, boost * 0.85));
            hFactor = Math.min(1.0, 1.0 - Math.pow(1.0 - baseSnap, frameScale * ((Double)this.horizontalSpeed.getValue() / 22.0)));
            vFactor = Math.min(1.0, 1.0 - Math.pow(1.0 - baseSnap, frameScale * ((Double)this.verticalSpeed.getValue() / 22.0)));
            if (Math.abs(yawDelta) < 18.0f) {
                hFactor = Math.max(hFactor, strengthRatio * 0.92);
            }
            if (Math.abs(pitchDelta) < 15.0f) {
                vFactor = Math.max(vFactor, strengthRatio * 0.9);
            }
        } else if (this.lockMode.isMode("Magnetic Lock")) {
            hBase = Math.min(1.0, (Double)this.horizontalSpeed.getValue() * boost * strengthRatio / 28.0);
            vBase = Math.min(1.0, (Double)this.verticalSpeed.getValue() * boost * strengthRatio / 30.0);
            double magnetBoost = Math.abs(yawDelta) < 25.0f ? 1.45 : 1.1;
            hFactor = Math.min(0.96, 1.0 - Math.pow(1.0 - Math.min(0.95, hBase * magnetBoost), frameScale));
            vFactor = Math.min(0.94, 1.0 - Math.pow(1.0 - Math.min(0.95, vBase * magnetBoost), frameScale));
        } else {
            hBase = (Double)this.horizontalSpeed.getValue() * boost * strengthRatio / 38.0;
            vBase = (Double)this.verticalSpeed.getValue() * boost * strengthRatio / 40.0;
            double hSig = this.computeSigmoidFactor(Math.abs(yawDelta), hBase, isElytraEngagement);
            double vSig = this.computeSigmoidFactor(Math.abs(pitchDelta), vBase, isElytraEngagement);
            hFactor = Math.min(0.9, 1.0 - Math.pow(1.0 - hSig, frameScale));
            vFactor = Math.min(0.88, 1.0 - Math.pow(1.0 - vSig, frameScale));
        }
        boolean moved = false;
        if (Math.abs(yawDelta) > 0.04f) {
            float rawStepYaw = (float)((double)yawDelta * hFactor);
            if (this.elytraTargetBypass.isEnabled()) {
                if (mc.player.isFallFlying()) {
                    rawStepYaw = MathHelper.clamp((float)rawStepYaw, (float)-21.5f, (float)21.5f);
                }
                rawStepYaw = this.applyMouseGcd(mc, rawStepYaw);
            }
            if (Math.abs(rawStepYaw) > 0.001f) {
                float newYaw = mc.player.getYaw() + rawStepYaw;
                mc.player.setYaw(newYaw);
                mc.player.headYaw = newYaw;
                moved = true;
            }
        }
        if ((this.verticalAssist.isEnabled() || isElytraEngagement) && Math.abs(pitchDelta) > 0.04f) {
            float rawStepPitch = (float)((double)pitchDelta * vFactor);
            if (this.elytraTargetBypass.isEnabled()) {
                if (mc.player.isFallFlying()) {
                    rawStepPitch = MathHelper.clamp((float)rawStepPitch, (float)-16.5f, (float)16.5f);
                }
                rawStepPitch = this.applyMouseGcd(mc, rawStepPitch);
            }
            if (Math.abs(rawStepPitch) > 0.001f) {
                float newPitch = MathHelper.clamp((float)(mc.player.getPitch() + rawStepPitch), (float)-89.9f, (float)89.9f);
                mc.player.setPitch(newPitch);
                moved = true;
            }
        }
        if (moved && mc.gameRenderer != null) {
            mc.gameRenderer.updateCrosshairTarget(tickDelta);
        }
    }

    private float applyMouseGcd(MinecraftClient mc, float delta) {
        double sens = mc.options != null ? (Double)mc.options.getMouseSensitivity().getValue() : 0.5;
        double f = sens * 0.6 + 0.2;
        double gcd = f * f * f * 8.0 * 0.15;
        if (gcd <= 1.0E-5) {
            return delta;
        }
        return (float)((double)Math.round((double)delta / gcd) * gcd);
    }

    private double computeSigmoidFactor(double angleDelta, double baseSpeed, boolean aerial) {
        baseSpeed = Math.max(0.05, Math.min(0.95, baseSpeed));
        double refAngle = aerial ? 28.0 : 40.0;
        double normalized = Math.min(1.0, angleDelta / refAngle);
        double sigmoid = 1.0 / (1.0 + Math.exp(-6.5 * (normalized - 0.25)));
        return Math.min(0.95, baseSpeed * (0.7 + 0.75 * sigmoid));
    }

    private Vec3d computeTargetPoint(MinecraftClient mc, LivingEntity target, Vec3d eyePos, float tickDelta, boolean isElytraEngagement) {
        Vec3d point;
        Vec3d lerpedPos = target.getLerpedPos(tickDelta);
        Vec3d posOffset = lerpedPos.subtract(target.getPos());
        Box box = target.getBoundingBox().offset(posOffset);
        if (target.isFallFlying() || this.aimBone.isMode("Center Mass")) {
            point = box.getCenter();
        } else if (this.aimBone.isMode("Neck / Head")) {
            point = lerpedPos.add(0.0, (double)target.getHeight() * 0.78, 0.0);
        } else if (this.aimBone.isMode("Core Lock")) {
            Vec3d center = box.getCenter();
            double halfW = (box.maxX - box.minX) * 0.2;
            double cx = MathHelper.clamp((double)eyePos.x, (double)(center.x - halfW), (double)(center.x + halfW));
            double cy = MathHelper.clamp((double)eyePos.y, (double)(box.minY + (double)target.getHeight() * 0.45), (double)(box.minY + (double)target.getHeight() * 0.76));
            double cz = MathHelper.clamp((double)eyePos.z, (double)(center.z - halfW), (double)(center.z + halfW));
            point = new Vec3d(cx, cy, cz);
        } else {
            double pad = 0.12;
            double cx = MathHelper.clamp((double)eyePos.x, (double)(box.minX + pad), (double)(box.maxX - pad));
            double cy = MathHelper.clamp((double)eyePos.y, (double)(box.minY + (double)target.getHeight() * 0.35), (double)(box.maxY - 0.14));
            double cz = MathHelper.clamp((double)eyePos.z, (double)(box.minZ + pad), (double)(box.maxZ - pad));
            point = new Vec3d(cx, cy, cz);
        }
        double leadTicks = (Double)this.elytraLeadTicks.getValue();
        if (leadTicks > 0.0) {
            Vec3d targetVel = new Vec3d(target.getX() - target.prevX, target.getY() - target.prevY, target.getZ() - target.prevZ);
            Vec3d selfVel = new Vec3d(mc.player.getX() - mc.player.prevX, mc.player.getY() - mc.player.prevY, mc.player.getZ() - mc.player.prevZ);
            double leadScale = isElytraEngagement ? leadTicks : leadTicks * 0.55;
            double selfFactor = this.strafeComp.isEnabled() ? 0.85 : 0.45;
            Vec3d relVel = targetVel.subtract(selfVel.multiply(selfFactor));
            double yWeight = isElytraEngagement ? 1.0 : 0.25;
            point = point.add(relVel.x * leadScale, relVel.y * leadScale * yWeight, relVel.z * leadScale);
        }
        return point;
    }

    private LivingEntity findBestTarget(MinecraftClient mc, boolean aerialCombat) {
        LivingEntity living;
        Entity locked;
        double maxDist = aerialCombat ? (Double)this.elytraRange.getValue() : (Double)this.range.getValue();
        double maxDistSq = maxDist * maxDist;
        double maxFov = aerialCombat ? Math.min(180.0, (Double)this.fov.getValue() * 1.25) : (Double)this.fov.getValue();
        long now = System.currentTimeMillis();
        if (this.stickyLock.isEnabled() && this.lastLockedId != -1 && now - this.lastLockedTime < 900L && (locked = mc.world.getEntityById(this.lastLockedId)) instanceof LivingEntity && this.isCandidate(mc, (Entity)(living = (LivingEntity)locked), maxDistSq * 1.25, Math.min(180.0, maxFov * 1.35))) {
            return living;
        }
        Comparator<LivingEntity> comparator = this.priorityMode.isMode("Gliding First") ? Comparator.comparing(e -> !e.isFallFlying()).thenComparingDouble(e -> this.get3DAngleToEntity(mc, (LivingEntity)e)) : (this.priorityMode.isMode("Distance") ? Comparator.comparingDouble(e -> mc.player.squaredDistanceTo((Entity)e)) : (this.priorityMode.isMode("Lowest Health") ? Comparator.comparingDouble(LivingEntity::getHealth) : Comparator.comparingDouble(e -> this.get3DAngleToEntity(mc, (LivingEntity)e))));
        LivingEntity best = null;
        for (Entity entity : mc.world.getEntities()) {
            if (!this.isCandidate(mc, entity, maxDistSq, maxFov)) continue;
            LivingEntity living2 = (LivingEntity)entity;
            if (best != null && comparator.compare(living2, best) >= 0) continue;
            best = living2;
        }
        return best;
    }

    private boolean isCandidate(MinecraftClient mc, Entity entity, double maxDistSq, double maxFov) {
        if (!(entity instanceof LivingEntity)) {
            return false;
        }
        LivingEntity living = (LivingEntity)entity;
        if (entity == mc.player || !living.isAlive() || living.isDead() || living.getHealth() <= 0.0f) {
            return false;
        }
        if (entity instanceof ArmorStandEntity || entity.isInvisible()) {
            return false;
        }
        if (mc.player.squaredDistanceTo(entity) > maxDistSq) {
            return false;
        }
        if (!this.throughWalls.isEnabled() && !mc.player.canSee(entity)) {
            return false;
        }
        if (this.get3DAngleToEntity(mc, living) > maxFov) {
            return false;
        }
        if (entity instanceof PlayerEntity) {
            PlayerEntity p = (PlayerEntity)entity;
            if (p.isSpectator() || p.isCreative()) {
                return false;
            }
            return this.targetPlayers.isEnabled();
        }
        if (entity instanceof HostileEntity) {
            return this.targetMobs.isEnabled();
        }
        return false;
    }

    private double get3DAngleToEntity(MinecraftClient mc, LivingEntity entity) {
        Vec3d eye = mc.player.getEyePos();
        Vec3d center = entity.getBoundingBox().getCenter();
        double dx = center.x - eye.x;
        double dy = center.y - eye.y;
        double dz = center.z - eye.z;
        double horiz = Math.sqrt(dx * dx + dz * dz);
        float yawTo = (float)(Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitchTo = (float)(-Math.toDegrees(Math.atan2(dy, horiz)));
        float dYaw = Math.abs(MathHelper.wrapDegrees((float)(yawTo - mc.player.getYaw())));
        float dPitch = Math.abs(MathHelper.wrapDegrees((float)(pitchTo - mc.player.getPitch())));
        return Math.sqrt((double)(dYaw * dYaw) + (double)dPitch * 0.65 * ((double)dPitch * 0.65));
    }
}

