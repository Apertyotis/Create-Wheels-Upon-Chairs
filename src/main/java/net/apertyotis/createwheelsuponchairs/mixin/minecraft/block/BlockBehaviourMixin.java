package net.apertyotis.createwheelsuponchairs.mixin.minecraft.block;

import net.apertyotis.createwheelsuponchairs.AllConfig;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    // 对未实现旋转方法的方块应用启发式旋转
    // 自动检查常见方向属性并旋转
    @Inject(method = "rotate", at = @At("HEAD"), cancellable = true)
    private void heuristicRotate(BlockState state, Rotation rotation, CallbackInfoReturnable<BlockState> cir) {
        if (!AllConfig.heuristic_rotation)
            return;

        // 常规朝向
        Collection<Property<?>> properties = state.getProperties();
        if (properties.contains(BlockStateProperties.FACING)) {
            state = state.setValue(BlockStateProperties.FACING,
                rotation.rotate(state.getValue(BlockStateProperties.FACING)));
        } else if (properties.contains(BlockStateProperties.HORIZONTAL_FACING)) {
            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING,
                rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
        }

        // 轴朝向
        else if (properties.contains(BlockStateProperties.AXIS)) {
            state = state.setValue(BlockStateProperties.AXIS,
                rotation.rotate(Direction.fromAxisAndDirection(
                        state.getValue(BlockStateProperties.AXIS),
                        Direction.AxisDirection.POSITIVE))
                    .getAxis());
        } else if (properties.contains(BlockStateProperties.HORIZONTAL_AXIS)) {
            state = state.setValue(BlockStateProperties.HORIZONTAL_AXIS,
                rotation.rotate(Direction.fromAxisAndDirection(
                        state.getValue(BlockStateProperties.HORIZONTAL_AXIS),
                        Direction.AxisDirection.POSITIVE))
                    .getAxis());
        }

        // 管道类朝向
        else if (properties.containsAll(
            List.of(BlockStateProperties.NORTH, BlockStateProperties.SOUTH,
                BlockStateProperties.WEST, BlockStateProperties.EAST))
        ) {
            state = switch (rotation) {
                case CLOCKWISE_90 -> state
                    .setValue(BlockStateProperties.NORTH, state.getValue(BlockStateProperties.WEST))
                    .setValue(BlockStateProperties.SOUTH, state.getValue(BlockStateProperties.EAST))
                    .setValue(BlockStateProperties.WEST, state.getValue(BlockStateProperties.SOUTH))
                    .setValue(BlockStateProperties.EAST, state.getValue(BlockStateProperties.NORTH));
                case CLOCKWISE_180 -> state
                    .setValue(BlockStateProperties.NORTH, state.getValue(BlockStateProperties.SOUTH))
                    .setValue(BlockStateProperties.SOUTH, state.getValue(BlockStateProperties.NORTH))
                    .setValue(BlockStateProperties.WEST, state.getValue(BlockStateProperties.EAST))
                    .setValue(BlockStateProperties.EAST, state.getValue(BlockStateProperties.WEST));
                case COUNTERCLOCKWISE_90 -> state
                    .setValue(BlockStateProperties.NORTH, state.getValue(BlockStateProperties.EAST))
                    .setValue(BlockStateProperties.SOUTH, state.getValue(BlockStateProperties.WEST))
                    .setValue(BlockStateProperties.WEST, state.getValue(BlockStateProperties.NORTH))
                    .setValue(BlockStateProperties.EAST, state.getValue(BlockStateProperties.SOUTH));
                case NONE -> state;
            };
        }
        cir.setReturnValue(state);
    }

    // 对未实现镜像方法的方块应用启发式镜像
    // 自动检查常见方向属性并镜像
    @Inject(method = "mirror", at = @At("HEAD"), cancellable = true)
    private void heuristicMirror(BlockState state, Mirror mirror, CallbackInfoReturnable<BlockState> cir) {
        if (!AllConfig.heuristic_rotation) return;

        Collection<Property<?>> properties = state.getProperties();
        // 常规朝向
        if (properties.contains(BlockStateProperties.FACING)) {
            if (mirror.getRotation(state.getValue(BlockStateProperties.FACING)) == Rotation.CLOCKWISE_180) {
                state = state.setValue(BlockStateProperties.FACING,
                    Rotation.CLOCKWISE_180.rotate(state.getValue(BlockStateProperties.FACING)));
            }
        } else if (properties.contains(BlockStateProperties.HORIZONTAL_FACING)) {
            if (mirror.getRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING)) == Rotation.CLOCKWISE_180) {
                state = state.setValue(BlockStateProperties.HORIZONTAL_FACING,
                    Rotation.CLOCKWISE_180.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
            }
        }

        // 轴朝向不用镜像

        // 管道类朝向
        else if (properties.containsAll(
            List.of(BlockStateProperties.NORTH, BlockStateProperties.SOUTH,
                BlockStateProperties.WEST, BlockStateProperties.EAST))
        ) {
            state = switch (mirror) {
                case LEFT_RIGHT -> state
                    .setValue(BlockStateProperties.NORTH, state.getValue(BlockStateProperties.SOUTH))
                    .setValue(BlockStateProperties.SOUTH, state.getValue(BlockStateProperties.NORTH));
                case FRONT_BACK -> state
                    .setValue(BlockStateProperties.WEST, state.getValue(BlockStateProperties.EAST))
                    .setValue(BlockStateProperties.EAST, state.getValue(BlockStateProperties.WEST));
                case NONE -> state;
            };
        }
        cir.setReturnValue(state);
    }
}
