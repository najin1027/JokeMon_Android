package com.joke.mon.core.data.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.joke.mon.R
import com.joke.mon.core.util.Config
import com.joke.mon.ui.theme.Primary500
import com.joke.mon.ui.theme.SemanticPurple
import com.joke.mon.ui.theme.SemanticRed
import com.joke.mon.ui.theme.SemanticYellow

data class CharacterData(
    val id : Int,
    val name: String,
    val description: String,
    @DrawableRes val imageRes: Int,
    val backgroundColor: Color
)

val Characters = listOf<CharacterData>(
    CharacterData(
        id = Config.GILL_GILL_MON,
        "낄낄몬",
        "장난기많고 유쾌한 낄낄몬",
        R.drawable.ggil_ggil_mon,
        SemanticRed
    ),
    CharacterData(
        id = Config.HEE_HEE_MON,
        "히히몬",
        "다정하고 따뜻한 히히몬",
        R.drawable.hee_hee_mon,
        SemanticYellow
    ),
    CharacterData(
        id = Config.KICK_KICK_MON,
        "킥킥몬",
        "엉뚱하고 짓궂은 킥킥몬",
        R.drawable.kick_kick_mon,
        SemanticPurple
    )
)
