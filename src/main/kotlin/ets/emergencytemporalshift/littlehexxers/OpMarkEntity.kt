package ets.emergencytemporalshift.littlehexxers

import at.petrak.hexcasting.api.casting.ParticleSpray.Companion.cloud
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBool
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.getPositiveDouble
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import kotlin.math.roundToInt

class OpMarkEntity : SpellAction {
    override val argc = 3

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val target = args.getEntity(0, argc)
        val durationSeconds = args.getPositiveDouble(1, argc)
        val visible = args.getBool(2, argc)

        env.assertEntityInRange(target)

        val durationTicks = (durationSeconds * 20.0).roundToInt().coerceAtLeast(1)

        val cost = (MediaConstants.DUST_UNIT * durationSeconds).toLong()

        return SpellAction.Result(
            Spell(target as LivingEntity, durationTicks, visible),
            cost,
            listOf(cloud(target.pos, 1.0))
        )
    }

    private data class Spell(
        val target: LivingEntity,
        val durationTicks: Int,
        val visible: Boolean
    ) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            target.addStatusEffect(StatusEffectInstance(StatusEffects.GLOWING, durationTicks, 1, true, false))
        }
    }
}