package ets.emergencytemporalshift.littlehexxers.registry
import at.petrak.hexcasting.common.lib.hex.HexActions
import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.castables.Action
import ets.emergencytemporalshift.littlehexxers.OpMarkEntity
import ets.emergencytemporalshift.littlehexxers.OpUnmarkEntity
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier


object LittleHexxersActions {
    fun register() {
        register("mark_entity", HexDir.EAST, "qawaww", OpMarkEntity())
        register("unmark_entity", HexDir.EAST, "wqawaww", OpUnmarkEntity())

    }

    private fun register(name: String, startDir: HexDir, sig: String, action: Action) {
        Registry.register(
            HexActions.REGISTRY,
            Identifier("littlehexxers", name),
            ActionRegistryEntry(HexPattern.fromAngles(sig, startDir), action)
        )
    }
}
