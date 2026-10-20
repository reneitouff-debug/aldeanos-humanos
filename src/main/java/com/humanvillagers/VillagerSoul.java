package com.humanvillagers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Datos persistentes de cada aldeano: personalidad y relación con cada jugador. */
public record VillagerSoul(int personality, Map<String, Integer> friendship, Map<String, Long> lastTalkDay) {

    public static final Codec<VillagerSoul> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("personality").forGetter(VillagerSoul::personality),
            Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("friendship").forGetter(VillagerSoul::friendship),
            Codec.unboundedMap(Codec.STRING, Codec.LONG).fieldOf("lastTalkDay").forGetter(VillagerSoul::lastTalkDay)
    ).apply(i, VillagerSoul::new));

    public static VillagerSoul create(int personality) {
        return new VillagerSoul(personality, Map.of(), Map.of());
    }

    public int friendshipWith(UUID player) {
        return friendship.getOrDefault(player.toString(), 0);
    }

    public long lastTalkWith(UUID player) {
        return lastTalkDay.getOrDefault(player.toString(), -1L);
    }

    public VillagerSoul withFriendship(UUID player, int value) {
        Map<String, Integer> copy = new HashMap<>(friendship);
        copy.put(player.toString(), Math.max(-50, Math.min(100, value)));
        return new VillagerSoul(personality, copy, lastTalkDay);
    }

    public VillagerSoul withTalk(UUID player, long day) {
        Map<String, Long> copy = new HashMap<>(lastTalkDay);
        copy.put(player.toString(), day);
        return new VillagerSoul(personality, friendship, copy);
    }
}
