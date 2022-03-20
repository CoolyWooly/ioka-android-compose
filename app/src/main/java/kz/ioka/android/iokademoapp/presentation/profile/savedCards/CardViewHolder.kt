package kz.ioka.android.iokademoapp.presentation.profile.savedCards

import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import kz.ioka.android.iokademoapp.R

class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var ivCardType: AppCompatImageView = view.findViewById(R.id.ivCardType)
    var tvCardPan: AppCompatTextView = view.findViewById(R.id.tvCardPan)
    var btnRemoveCard: AppCompatImageButton = view.findViewById(R.id.btnRemove)

}