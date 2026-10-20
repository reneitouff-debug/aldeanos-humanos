package com.humanvillagers;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerGossipType;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class HumanVillagers implements ModInitializer {
    public static final String MOD_ID = "humanvillagers";

    public static final AttachmentType<VillagerSoul> SOUL =
            AttachmentRegistry.createPersistent(Identifier.of(MOD_ID, "soul"), VillagerSoul.CODEC);

    @Override
    public void onInitialize() {
        // 1) Al cargar un aldeano: darle nombre y personalidad (una sola vez).
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof VillagerEntity v) {
                if (!v.hasCustomName()) {
                    v.setCustomName(Text.literal(Dialogue.randomName()));
                    v.setCustomNameVisible(false);
                }
                if (v.getAttached(SOUL) == null) {
                    v.setAttached(SOUL, VillagerSoul.create(Dialogue.randomPersonality()));
                }
            }
        });

        // 2) Shift + clic derecho: hablar (mano vacía) o regalar (objeto en mano).
        //    El clic derecho normal sigue abriendo el comercio.
        UseEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (!(entity instanceof VillagerEntity v) || hand != Hand.MAIN_HAND
                    || !player.isSneaking() || player.isSpectator()) {
                return ActionResult.PASS;
            }
            if (world.isClient) return ActionResult.SUCCESS;

            v.getNavigation().stop();
            v.getLookControl().lookAt(player);

            ItemStack held = player.getMainHandStack();
            if (held.isEmpty()) {
                talk(v, player);
            } else {
                gift(v, player, held);
            }
            return ActionResult.SUCCESS;
        });

        // 3) Golpear a un aldeano rompe la amistad.
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (world.isClient || !(entity instanceof VillagerEntity v) || player.isSpectator()) {
                return ActionResult.PASS;
            }
            VillagerSoul soul = soulOf(v);
            UUID id = player.getUuid();
            int newValue = soul.friendshipWith(id) - 15;
            v.setAttached(SOUL, soul.withFriendship(id, newValue));
            say(v, player, Dialogue.hurtLine(soul.personality()));
            return ActionResult.PASS; // el golpe sigue haciendo daño normalmente
        });
    }

    private static VillagerSoul soulOf(VillagerEntity v) {
        VillagerSoul soul = v.getAttached(SOUL);
        if (soul == null) {
            soul = VillagerSoul.create(Dialogue.randomPersonality());
            v.setAttached(SOUL, soul);
        }
        return soul;
    }

    private static void talk(VillagerEntity v, PlayerEntity player) {
        VillagerSoul soul = soulOf(v);
        UUID id = player.getUuid();
        long day = v.getWorld().getTimeOfDay() / 24000L;
        int friendship = soul.friendshipWith(id);

        String profession = v.getVillagerData().getProfession().getKey()
                .map(k -> k.getValue().getPath()).orElse("none");
        long timeOfDay = v.getWorld().getTimeOfDay() % 24000L;

        say(v, player, Dialogue.talkLine(soul.personality(), friendship, profession,
                timeOfDay, v.getWorld().isRaining()));
        v.playSound(SoundEvents.ENTITY_VILLAGER_AMBIENT, 1f, 1f);

        // Hablar da +2 de cariño, una vez al día por jugador.
        if (friendship >= 0 && soul.lastTalkWith(id) != day) {
            soul = soul.withTalk(id, day).withFriendship(id, friendship + 2);
            v.setAttached(SOUL, soul);
            hearts(v, 2);
        }
        status(v, player, soul);
    }

    private static void gift(VillagerEntity v, PlayerEntity player, ItemStack held) {
        VillagerSoul soul = soulOf(v);
        UUID id = player.getUuid();
        Integer value = Dialogue.GIFTS.get(held.getItem());

        if (value == null) {
            say(v, player, "Mmm... gracias, pero eso no me sirve de mucho.");
            v.playSound(SoundEvents.ENTITY_VILLAGER_NO, 1f, 1f);
            return;
        }
        if (!player.isCreative()) held.decrement(1);

        int newFriendship = soul.friendshipWith(id) + value;
        soul = soul.withFriendship(id, newFriendship);
        v.setAttached(SOUL, soul);

        // Amistad real: descuentos en el comercio gracias al sistema de rumores de vanilla.
        v.getGossip().startGossip(id, VillagerGossipType.MINOR_POSITIVE, Math.max(1, value / 2));

        say(v, player, Dialogue.giftThanks(soul.personality()));
        v.playSound(SoundEvents.ENTITY_VILLAGER_YES, 1f, 1f);
        hearts(v, 7);
        status(v, player, soul);
    }

    private static void say(VillagerEntity v, PlayerEntity player, String line) {
        String name = v.hasCustomName() ? v.getCustomName().getString() : "Aldeano";
        player.sendMessage(Text.literal("<" + name + "> ").formatted(Formatting.YELLOW)
                .append(Text.literal(line).formatted(Formatting.WHITE)), false);
    }

    private static void status(VillagerEntity v, PlayerEntity player, VillagerSoul soul) {
        int f = soul.friendshipWith(player.getUuid());
        player.sendMessage(Text.literal("❤ " + f + "/100 · " + Dialogue.tierName(f)
                + " · " + Dialogue.PERSONALITY_NAMES[Math.floorMod(soul.personality(), 4)])
                .formatted(Formatting.LIGHT_PURPLE), true);
    }

    private static void hearts(VillagerEntity v, int count) {
        if (v.getWorld() instanceof ServerWorld sw) {
            sw.spawnParticles(ParticleTypes.HEART, v.getX(), v.getEyeY() + 0.3, v.getZ(),
                    count, 0.3, 0.3, 0.3, 0.0);
        }
    }
}
