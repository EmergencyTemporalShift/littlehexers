package ets.emergencytemporalshift.littlehexxers

import at.petrak.hexcasting.api.casting.ParticleSpray.Companion.cloud
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getLivingEntityButNotArmorStand
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffects

class OpUnmarkEntity : SpellAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val target = args.getLivingEntityButNotArmorStand(0, argc)

        env.assertEntityInRange(target)

        return SpellAction.Result(
            Spell(target),
            MediaConstants.DUST_UNIT,
            listOf(cloud(target.pos, 1.0))
        )
    }

    private data class Spell(val target: LivingEntity) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            target.removeStatusEffect(StatusEffects.GLOWING)
        }
    }
}
