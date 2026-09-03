package net.apertyotis.createwheelsuponchairs.mixin.create.fluids;

import com.simibubi.create.content.fluids.FlowSource;
import com.simibubi.create.content.fluids.PipeConnection;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@SuppressWarnings("UnusedReturnValue")
@Mixin(PipeConnection.class)
public interface PipeConnectionAccessor {
    @Accessor("source")
    Optional<FlowSource> getSource();

    @Accessor("flow")
    Optional<PipeConnection.Flow> getFlow();

    @Accessor("flow")
    void setFlow(Optional<PipeConnection.Flow> flow);

    @Invoker("tryStartingNewFlow")
    boolean invokeTryStartingNewFlow(boolean inbound, FluidStack fluid);
}
