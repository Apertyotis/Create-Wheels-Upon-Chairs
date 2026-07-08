package net.apertyotis.createwheelsuponchairs.content.hachimiGlue;

import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import io.netty.buffer.ByteBuf;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.AllPackets;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public record HachimiGlueModificationPacket(int entityId, AABB aabb) implements ServerboundPacketPayload {
    public static final StreamCodec<ByteBuf, HachimiGlueModificationPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, HachimiGlueModificationPacket::entityId,
        BlockPos.STREAM_CODEC, packet -> BlockPos.containing(packet.aabb.minX, packet.aabb.minY, packet.aabb.minZ),
        BlockPos.STREAM_CODEC, packet -> BlockPos.containing(packet.aabb.maxX, packet.aabb.maxY, packet.aabb.maxZ),
        HachimiGlueModificationPacket::new
    );

    public HachimiGlueModificationPacket(int entityId, BlockPos first, BlockPos second) {
        this(entityId, AABB.encapsulatingFullBlocks(first, second));
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return AllPackets.MODIFY_GLUE;
    }

    @Override
    public void handle(ServerPlayer player) {
        if (player == null)
            return;
        if (!AllConfig.hachimi_glue) {
            player.displayClientMessage(Component.translatable("cwuc.info.hachimi_disabled")
                .withStyle(ChatFormatting.RED), true);
            return;
        }
        Entity entity = player.level().getEntity(entityId);
        if (!(entity instanceof SuperGlueEntity glue))
            return;
        double range = 32;
        if (player.distanceToSqr(glue.position()) > range * range)
            return;
        glue.setBoundingBox(aabb);
    }
}
