package com.pyamsoft.pydroid.ui.widget

import androidx.annotation.CheckResult
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pyamsoft.pydroid.theme.ZeroSize
import com.pyamsoft.pydroid.theme.keylines
import com.pyamsoft.pydroid.ui.defaults.CardDefaults
import com.pyamsoft.pydroid.ui.defaults.ImageDefaults
import com.pyamsoft.pydroid.ui.icons.RadioButtonUnchecked
import com.pyamsoft.pydroid.ui.widget.materialcheckable.HeightMatcherGenerator
import com.pyamsoft.pydroid.ui.widget.materialcheckable.internal.HeightMatcherGeneratorImpl
import com.pyamsoft.pydroid.ui.widget.materialcheckable.internal.createGapHeightGenerator
import com.pyamsoft.pydroid.ui.widget.materialcheckable.internal.createOnSizeChangedModifierGenerator
import com.pyamsoft.pydroid.ui.widget.materialcheckable.internal.rememberMaterialCheckableAlpha
import com.pyamsoft.pydroid.ui.widget.materialcheckable.internal.rememberMaterialCheckableColor
import com.pyamsoft.pydroid.ui.widget.materialcheckable.internal.rememberMaterialCheckableIcon

/**
 * Given a list of items in a parent Composable of different content heights, this remember will
 * create a generator that, when invoked for each item in the list will produce an "extraHeight"
 * which, when applied to each item in the list, will fill smaller items with gap space so that all
 * items become the same height
 */
@Composable
@CheckResult
public fun <T : Any> rememberMaterialCheckableHeightMatcherGenerator(): HeightMatcherGenerator<T> {
  val (heights, setHeights) = remember { mutableStateOf(emptyMap<T, Int>()) }

  val handleSetHeight by rememberUpdatedState(setHeights)

  // Figure out which is the largest and size all other to match
  val largest =
      remember(heights) {
        if (heights.isEmpty()) {
          return@remember 0
        }

        var largest = 0
        for (height in heights.values) {
          if (height > largest) {
            largest = height
          }
        }

        if (largest <= 0) {
          return@remember 0
        }

        return@remember largest
      }

  val density = LocalDensity.current
  return remember(
      density,
      largest,
      heights,
  ) {
    HeightMatcherGeneratorImpl(
        gapHeightGenerator = createGapHeightGenerator(density, largest, heights),
        onSizeChangedModifierGenerator =
            createOnSizeChangedModifierGenerator(heights, handleSetHeight),
    )
  }
}

/** Fancy checkable with Material Design ish elements */
@Composable
public fun MaterialCheckable(
    modifier: Modifier = Modifier,
    isEditable: Boolean,
    condition: Boolean,
    title: String,
    description: String,
    onClick: () -> Unit,
    /** Hack to make two different cards the same size based on their content */
    extraHeight: Dp = ZeroSize,
) {
  val colors = MaterialTheme.colors
  val selectedColor = remember(colors) { colors.primary }

  MaterialCheckable(
      modifier = modifier,
      isEditable = isEditable,
      condition = condition,
      title = title,
      description = description,
      selectedColor = selectedColor,
      extraHeight = extraHeight,
      onClick = onClick,
  )
}

@Composable
public fun ColoredMaterialCheckable(
    modifier: Modifier = Modifier,
    isEditable: Boolean,
    condition: Boolean,
    title: String,
    description: String,
    selectedColor: Color,
    onClick: () -> Unit,
    /** Hack to make two different cards the same size based on their content */
    extraHeight: Dp = ZeroSize,
) {
  MaterialCheckable(
      modifier = modifier,
      isEditable = isEditable,
      condition = condition,
      title = title,
      description = description,
      selectedColor = selectedColor,
      extraHeight = extraHeight,
      onClick = onClick,
  )
}

@Composable
private fun MaterialCheckable(
    modifier: Modifier = Modifier,
    isEditable: Boolean,
    condition: Boolean,
    title: String,
    description: String,
    selectedColor: Color,
    extraHeight: Dp,
    onClick: () -> Unit,
) {
  val color = rememberMaterialCheckableColor(condition, selectedColor)
  val iconColor = rememberMaterialCheckableIcon(condition)
  val alphas = rememberMaterialCheckableAlpha(isEditable, condition)

  val checkIcon =
      remember(condition) {
        if (condition) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked
      }

  Card(
      modifier =
          modifier.border(
              width = 2.dp,
              color = color.copy(alpha = alphas.secondary),
              shape = MaterialTheme.shapes.medium,
          ),
      elevation = CardDefaults.Elevation,
  ) {
    Column(
        modifier =
            Modifier.clickable(enabled = isEditable) { onClick() }
                .padding(MaterialTheme.keylines.content),
    ) {
      Row(
          verticalAlignment = Alignment.Top,
      ) {
        Text(
            modifier = Modifier.weight(1F).padding(bottom = MaterialTheme.keylines.baseline),
            text = title,
            style =
                MaterialTheme.typography.h6.copy(
                    fontWeight = FontWeight.W700,
                    color = color.copy(alpha = alphas.primary),
                ),
        )

        Icon(
            modifier = Modifier.size(ImageDefaults.IconSize),
            imageVector = checkIcon,
            contentDescription = title,
            tint = iconColor.copy(alpha = alphas.secondary),
        )
      }

      // Align with the largest card
      if (extraHeight > ZeroSize) {
        Spacer(
            modifier = Modifier.height(extraHeight),
        )
      }

      Text(
          text = description,
          style =
              MaterialTheme.typography.caption.copy(
                  color = MaterialTheme.colors.onSurface.copy(alpha = alphas.secondary),
                  fontWeight = FontWeight.W400,
              ),
      )
    }
  }
}
