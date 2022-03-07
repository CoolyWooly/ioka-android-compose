package kz.ioka.android.iokademoapp.profile.savedCards

import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import kz.ioka.android.iokademoapp.R

class AddCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    var btnAddCard = view.findViewById<LinearLayoutCompat>(R.id.btnAddCard)

}