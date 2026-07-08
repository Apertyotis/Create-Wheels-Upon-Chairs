package net.apertyotis.createwheelsuponchairs;

import net.apertyotis.createwheelsuponchairs.content.hachimiGlue.HachimiGlueModificationPacket;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

import static net.apertyotis.createwheelsuponchairs.CreateWheelsUponChairs.MOD_ID;

public enum AllPackets implements BasePacketPayload.PacketTypeProvider {

    MODIFY_GLUE(HachimiGlueModificationPacket.class, HachimiGlueModificationPacket.STREAM_CODEC);

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> AllPackets(Class<T> clazz, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = this.name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, name)),
            clazz, codec
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void register() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(MOD_ID, "1.0.0");
        for (AllPackets packet : values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }
}
