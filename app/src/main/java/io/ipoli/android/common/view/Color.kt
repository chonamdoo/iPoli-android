package io.ipoli.android.common.view

import android.support.annotation.ColorRes
import io.ipoli.android.R
import io.ipoli.android.common.data.ColorName

/**
 * Created by Venelin Valkov <venelin@ipoli.io>
 * on 9/26/17.
 */
enum class Color(val colorName: ColorName,
                 @ColorRes val color200: Int,
                 @ColorRes val color500: Int,
                 @ColorRes val color700: Int,
                 @ColorRes val color900: Int) {
    RED(ColorName.RED,
        R.color.md_red_200,
        R.color.md_red_500,
        R.color.md_red_700,
        R.color.md_red_900),

    GREEN(ColorName.GREEN,
        R.color.md_green_200,
        R.color.md_green_500,
        R.color.md_green_700,
        R.color.md_green_900),

    BLUE(ColorName.BLUE,
        R.color.md_blue_200,
        R.color.md_blue_500,
        R.color.md_blue_700,
        R.color.md_blue_900),

    PURPLE(ColorName.PURPLE,
        R.color.md_purple_200,
        R.color.md_purple_500,
        R.color.md_purple_700,
        R.color.md_purple_900),

    BROWN(ColorName.BROWN,
        R.color.md_brown_200,
        R.color.md_brown_500,
        R.color.md_brown_700,
        R.color.md_brown_900),

    ORANGE(ColorName.ORANGE,
        R.color.md_orange_200,
        R.color.md_orange_500,
        R.color.md_orange_700,
        R.color.md_orange_900),

    PINK(ColorName.PINK,
        R.color.md_pink_200,
        R.color.md_pink_500,
        R.color.md_pink_700,
        R.color.md_pink_900),

    TEAL(ColorName.TEAL,
        R.color.md_teal_200,
        R.color.md_teal_500,
        R.color.md_teal_700,
        R.color.md_teal_900),

    DEEP_ORANGE(ColorName.DEEP_ORANGE,
        R.color.md_deep_orange_200,
        R.color.md_deep_orange_500,
        R.color.md_deep_orange_700,
        R.color.md_deep_orange_900),

    INDIGO(ColorName.INDIGO,
        R.color.md_indigo_200,
        R.color.md_indigo_500,
        R.color.md_indigo_700,
        R.color.md_indigo_900),

    BLUE_GREY(ColorName.BLUE_GREY,
        R.color.md_blue_grey_200,
        R.color.md_blue_grey_500,
        R.color.md_blue_grey_700,
        R.color.md_blue_grey_900),

    LIME(ColorName.LIME,
        R.color.md_lime_200,
        R.color.md_lime_500,
        R.color.md_lime_700,
        R.color.md_blue_grey_900);

    companion object {
        fun get(colorName: String): Color =
            values().first { it.colorName.name == colorName }
    }
}