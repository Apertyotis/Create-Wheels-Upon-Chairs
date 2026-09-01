package net.apertyotis.createwheelsuponchairs.content.filtering;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.apertyotis.createwheelsuponchairs.AllPackets;
import net.createmod.catnip.net.base.ServerboundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public record EasyFilteringPacket(BlockPos pos, Direction side, ItemStack filter) implements ServerboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, EasyFilteringPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC, EasyFilteringPacket::pos,
        Direction.STREAM_CODEC, EasyFilteringPacket::side,
        ItemStack.STREAM_CODEC, EasyFilteringPacket::filter,
        EasyFilteringPacket::new
    );

    @Override
    public PacketTypeProvider getTypeProvider() {
        return AllPackets.EASY_FILTERING;
    }

    @Override
    public void handle(ServerPlayer player) {
        if (!AllConfig.easy_filtering || player == null)
            return;
        Level level = player.level();
        FilteringBehaviour behaviour = BlockEntityBehaviour.get(level, pos, FilteringBehaviour.TYPE);
        if (behaviour == null || behaviour instanceof FactoryPanelBehaviour)
            return;

        if (filter.getItem() instanceof FilterItem || !behaviour.setFilter(side, filter)) {
            player.displayClientMessage(CreateLang.translateDirect("logistics.filter.invalid_item"), true);
            AllSoundEvents.DENY.playOnServer(player.level(), player.blockPosition(), 1, 1);
            return;
        }
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, .25f, .1f);
    }
}
