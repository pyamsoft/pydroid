package com.pyamsoft.pydroid.ui.rating

import com.pyamsoft.pydroid.ui.app.BaseScreen

interface RatingDialogView : BaseScreen {

  fun onSaveRating(onSave: () -> Unit)

  fun onCancelRating(onCancel: () -> Unit)

}