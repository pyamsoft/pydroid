package com.pyamsoft.pydroid.ui.rating

import com.pyamsoft.pydroid.ui.app.BaseView

interface RatingDialogView : BaseView {

  fun onSaveRating(onSave: () -> Unit)

  fun onCancelRating(onCancel: () -> Unit)

}