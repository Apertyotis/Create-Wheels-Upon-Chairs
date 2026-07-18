package net.apertyotis.createwheelsuponchairs;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = CreateWheelsUponChairs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AllConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;
    public static ModConfig modConfig;

    static {
        // 构造配置
        Pair<Common, ForgeConfigSpec> pair = BUILDER.configure(Common::new);
        COMMON = pair.getLeft();
        COMMON_SPEC = pair.getRight();
    }

    // common 配置定义
    public static class Common {
        public final ForgeConfigSpec.BooleanValue DEPLOYER_INSTANT_OUTPUT;
        public final ForgeConfigSpec.BooleanValue BELT_FUNNEL_DETECTION_TWEAK;
        public final ForgeConfigSpec.BooleanValue NO_CHUTE_LEAKING;
        public final ForgeConfigSpec.BooleanValue NO_DEPOT_OVERFLOW_DROP;
        public final ForgeConfigSpec.BooleanValue BETTER_PSI_ON_CARRIAGE;
        public final ForgeConfigSpec.BooleanValue HACHIMI_GLUE;
        public final ForgeConfigSpec.BooleanValue SMART_FLUID_PIPE;
        public final ForgeConfigSpec.BooleanValue EASY_BELT;
        public final ForgeConfigSpec.BooleanValue FAST_CONTRAPTION_STORAGE;
        public final ForgeConfigSpec.BooleanValue FAST_LOGISTICS;
        public final ForgeConfigSpec.BooleanValue BASIN_FAUCET_VIEW;

        public final ForgeConfigSpec.BooleanValue PSI_FIX;
        public final ForgeConfigSpec.BooleanValue HOSE_PULLEY_FIX;
        public final ForgeConfigSpec.BooleanValue VAULT_AND_TANK_SCHEMATIC_FIX;
        public final ForgeConfigSpec.BooleanValue FLUID_NETWORK_FIX;
        public final ForgeConfigSpec.BooleanValue BELT_FIX;
        public final ForgeConfigSpec.BooleanValue PROCESSING_FIX;
        public final ForgeConfigSpec.BooleanValue TRAIN_FIX;
        public final ForgeConfigSpec.BooleanValue LOGISTICS_FIX;
        public final ForgeConfigSpec.BooleanValue FIX_9729;
        public final ForgeConfigSpec.BooleanValue FIX_9803;

        public final ForgeConfigSpec.BooleanValue PLAYER_CAN_BREATH_UNDERWATER;
        public final ForgeConfigSpec.BooleanValue DISABLE_DIG_SPEED_PENALTY;
        public final ForgeConfigSpec.BooleanValue ALWAYS_ALLOW_FLYING;
        public final ForgeConfigSpec.BooleanValue KEEP_FLYING_ON_GROUND;
        public final ForgeConfigSpec.BooleanValue HEURISTIC_ROTATION;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("Common").push("common");
            DEPLOYER_INSTANT_OUTPUT = builder
                .comment("Fix deployers unnecessarily entering their 10-tick cooldown when milking, emptying buckets, slaughtering, etc.")
                .comment("Because the secondary output overflow check is always performed before the outputs are extracted.")
                .define("deployer_instant_output", true);
            BELT_FUNNEL_DETECTION_TWEAK = builder
                .comment("Allows funnels to extract items positioned exactly at the center of a belt.")
                .comment("Previously, funnels could only extract items past the center.")
                .comment("When a opposing funnel blocks items at the center, all side-facing belt funnels stop extracting, which is unintuitive.")
                .define("belt_funnel_detection_tweak", true);
            NO_CHUTE_LEAKING = builder
                .comment("Prevent Diagonal Chutes from interacting with containers below.")
                .comment("Because visually, Diagonal Chutes have no opening at the bottom.")
                .define("no_chute_leaking", true);
            NO_DEPOT_OVERFLOW_DROP = builder
                .comment("Prevent the depot from dropping overflow items.")
                .comment("For example when it accumulates too many processing outputs.")
                .comment("Or when an Ejector cannot merge received item stacks.")
                .define("no_depot_overflow_drop", true);
            BETTER_PSI_ON_CARRIAGE = builder
                .comment("PSIs on trains are now only activated when the train arrives at a station.")
                .define("better_psi_on_carriage", true);
            HACHIMI_GLUE = builder
                .comment("Make super glue as convenient as honey glue from Aeronautic.")
                .define("hachimi_glue", true);
            SMART_FLUID_PIPE = builder
                .comment("Allow smart fluid pipes and valve pipes to work without being directly attached to a tank.")
                .comment("And slightly optimize the performance.")
                .define("smart_fluid_pipe", true);
            EASY_BELT = builder
                .comment("Allow belts to operate without requiring a power source.")
                .define("easy_belt", true);
            FAST_CONTRAPTION_STORAGE = builder
                .comment("Backported the optimization from Create PR #9706. (Needs restart)")
                .define("fast_contraption_storage", true);
            FAST_LOGISTICS = builder
                .comment("Optimize the performance of various logistics components.")
                .define("fast_logistics", true);
            BASIN_FAUCET_VIEW = builder
                .comment("Show the contents of the basin faucet when wearing Goggles.")
                .define("basin_faucet_view", true);
            builder.pop();

            builder.comment("Bugfix").push("bugfix");
            PSI_FIX = builder
                .comment("Backported the fix from Create PR #9624.")
                .define("psi_fix", true);
            HOSE_PULLEY_FIX = builder
                .comment("Fix hose pulleys incorrectly showing \"Bottomless Supply\" and stopping fill 2 blocks before reaching the actual threshold;")
                .comment("Fix fluid input rates above 1000 mB/t to hose pulleys being incorrectly reduced;")
                .comment("Fix mechanical pumps with a full output deleting fluid when extracting from a hose pulley;")
                .comment("Add extra tooltip when wearing Goggles.")
                .define("hose_pulley_fix", true);
            VAULT_AND_TANK_SCHEMATIC_FIX = builder
                .comment("Fix single-block tanks or vaults placed by schematic incorrectly have zero capacity.")
                .define("vault_and_tank_schematic_fix", true);
            FLUID_NETWORK_FIX = builder
                .comment("Backported the fix from Create PR #9974 and #10001;")
                .comment("Fix open pipes being unable to place fluids when the output rate exceeds 1000 mB/t;")
                .comment("Fix open pipes not validating their connections;")
                .comment("Fix an issue where, when \"mechanicalPumpRange\" >= 16, long pipes may occasionally fail to rebuild their fluid network correctly after chunks are reloaded.")
                .define("fluid_network_fix", true);
            BELT_FIX = builder
                .comment("Backported the fix from Create PR #9891, #9967 and #10017;")
                .comment("Fix the issue where the outputs of belt processing machines will be pushed back to the input side by a blocked belt funnel;")
                .comment("Fix Create Issue #9682.")
                .define("belt_fix", true);
            PROCESSING_FIX = builder
                .comment("Fix one of the two fluids output of a basin disappearing under certain conditions;")
                .comment("Fix recipe priority sorting not taking fluid ingredient amounts into account;")
                .comment("Fix basin recipe filtering only considering the first output of recipes with multiple outputs;")
                .comment("Fix sequenced assembly recipes using incorrect steps under certain conditions.")
                .define("processing_fix", true);
            TRAIN_FIX = builder
                .comment("Backported the fix from Create PR #9875 and #10362;")
                .comment("Attempt to fix the issue where trains sometimes cause crashes;")
                .comment("Fix an issue where trains never release their reserved signal segment, causing a deadlock.")
                .define("train_fix", true);
            LOGISTICS_FIX = builder
                .comment("Backported the fix from Create PR #9649 and #9793.")
                .define("logistics_fix", true);
            FIX_9729 = builder
                .comment("Backported the fix from Create PR #9729")
                .define("fix_9729", true);
            FIX_9803 = builder
                .comment("Backported the fix from Create PR #9803")
                .define("fix_9803", true);
            builder.pop();

            builder.comment("Misc").push("misc");
            PLAYER_CAN_BREATH_UNDERWATER = builder
                .comment("Player won't drown underwater.")
                .define("player_can_breath_underwater", false);
            DISABLE_DIG_SPEED_PENALTY = builder
                .comment("When players are in the air or water, they will no longer be subject to a five-time increase in digging time penalty")
                .define("disable_dig_speed_penalty", false);
            ALWAYS_ALLOW_FLYING = builder
                .comment("Allow all players flying.")
                .define("always_allow_flying",false);
            KEEP_FLYING_ON_GROUND = builder
                .comment("Prevents players from automatically exiting flight mode when touching the ground.")
                .define("keep_flying_on_ground", false);
            HEURISTIC_ROTATION = builder
                .comment("Allow rotation for blocks without a custom rotate implementation.")
                .comment("Many mod authors do not override rotate and mirror when adding directional blocks.")
                .comment("As a result, vanilla structure block placement and Create schematic printing are unable to correctly rotate these blocks.")
                .comment("Enabling this feature automatically detects common facing properties and applies rotation/mirror.")
                .define("heuristic_rotation", true);
            builder.pop();
        }
    }

    // 缓存配置值
    public static boolean deployer_instant_output;
    public static boolean belt_funnel_detection_tweak;
    public static boolean no_chute_leaking;
    public static boolean no_depot_overflow_drop;
    public static boolean better_psi_on_carriage;
    public static boolean hachimi_glue;
    public static boolean smart_fluid_pipe;
    public static boolean easy_belt;
    public static boolean fast_logistics;
    public static boolean basin_faucet_view;

    public static boolean psi_fix;
    public static boolean hose_pulley_fix;
    public static boolean vault_and_tank_schematic_fix;
    public static boolean fluid_network_fix;
    public static boolean belt_fix;
    public static boolean processing_fix;
    public static boolean train_fix;
    public static boolean logistics_fix;
    public static boolean fix_9729;
    public static boolean fix_9803;

    public static boolean player_can_breath_underwater;
    public static boolean disable_dig_speed_penalty;
    public static boolean always_allow_flying;
    public static boolean keep_flying_on_ground;
    public static boolean heuristic_rotation;

    public static void onLoading() {
        deployer_instant_output = COMMON.DEPLOYER_INSTANT_OUTPUT.get();
        belt_funnel_detection_tweak = COMMON.BELT_FUNNEL_DETECTION_TWEAK.get();
        no_chute_leaking = COMMON.NO_CHUTE_LEAKING.get();
        no_depot_overflow_drop = COMMON.NO_DEPOT_OVERFLOW_DROP.get();
        better_psi_on_carriage = COMMON.BETTER_PSI_ON_CARRIAGE.get();
        hachimi_glue = COMMON.HACHIMI_GLUE.get();
        smart_fluid_pipe = COMMON.SMART_FLUID_PIPE.get();
        easy_belt = COMMON.EASY_BELT.get();
        fast_logistics = COMMON.FAST_LOGISTICS.get();
        basin_faucet_view = COMMON.BASIN_FAUCET_VIEW.get();

        psi_fix = COMMON.PSI_FIX.get();
        hose_pulley_fix = COMMON.HOSE_PULLEY_FIX.get();
        vault_and_tank_schematic_fix = COMMON.VAULT_AND_TANK_SCHEMATIC_FIX.get();
        fluid_network_fix = COMMON.FLUID_NETWORK_FIX.get();
        belt_fix = COMMON.BELT_FIX.get();
        processing_fix = COMMON.PROCESSING_FIX.get();
        train_fix = COMMON.TRAIN_FIX.get();
        logistics_fix = COMMON.LOGISTICS_FIX.get();
        fix_9729 = COMMON.FIX_9729.get();
        fix_9803 = COMMON.FIX_9803.get();

        player_can_breath_underwater = COMMON.PLAYER_CAN_BREATH_UNDERWATER.get();
        disable_dig_speed_penalty = COMMON.DISABLE_DIG_SPEED_PENALTY.get();
        always_allow_flying = COMMON.ALWAYS_ALLOW_FLYING.get();
        keep_flying_on_ground = COMMON.KEEP_FLYING_ON_GROUND.get();
        heuristic_rotation = COMMON.HEURISTIC_ROTATION.get();
    }

    // 重载配置时，更新缓存
    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() != COMMON_SPEC)
            return;
        modConfig = event.getConfig();
        onLoading();
    }

    @SubscribeEvent
    static void onReload(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() != COMMON_SPEC)
            return;
        modConfig = event.getConfig();
        onLoading();
    }
}
