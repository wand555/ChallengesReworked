package wand555.github.io.challengesreworked.punishments.randomeffect;

import wand555.github.io.challengesreworked.punishments.Punishment;
import wand555.github.io.challengesreworked.punishments.PunishmentCommon;
import wand555.github.io.challengesreworked.punishments.endchallenge.EndChallengePunishment;

import java.util.Collection;
import java.util.List;

public interface RandomEffectPunishment extends Punishment {

    @Override
    RandomEffectPunishmentCommon getCommon();

    @Override
    default Collection<Class<? extends Punishment>> getIncompatiblePunishments() {
        return List.of(
                EndChallengePunishment.class
        );
    }
}
