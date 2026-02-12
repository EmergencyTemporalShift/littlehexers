package ets.emergencytemporalshift.littlehexxers.casting.patterns.spells

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class OpMark : SpellAction {
    /**
     * The number of arguments from the stack that this action requires.
     */
    override val argc = 1
    val cost = 2 * MediaConstants.DUST_UNIT

    /**
     * The method called when this Action is actually executed. Accepts the [args]
     * that were on the stack (there will be [argc] of them), and the [env],
     * which contains things like references to the caster, the ServerLevel,
     * methods to determine whether locations and entities are in ambit, etc.
     * Returns a [SpellAction.Result] containing the [RenderedSpell], the media cost,
     * and particle effects.
     */
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val target = args.getEntity(0, argc)

        // makes sure that the entity is inside the range
        // the caster is allowed to affect.
        env.assertEntityInRange(target)

        if (target !is ServerPlayerEntity) throw MishapBadEntity(
            target,
            Text.translatable("text.hexdummy.congrats.player")
        )

        return SpellAction.Result(
            Spell(target),
            cost,
            listOf(ParticleSpray.burst(target.pos, 1.0))
        )
    }

    /**
     * This class is responsible for actually making changes to the world. It accepts parameters to
     * define where/what it should affect (for this example the parameter is [player]), and the
     * [cast] method within is responsible for using that data to alter the world.
     */
    private data class Spell(val player: ServerPlayerEntity) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            env.caster!!.sendMessage(Text.translatable("text.hexdummy.congrats", player.displayName))
        }
    }
}