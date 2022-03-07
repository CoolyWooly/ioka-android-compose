package kz.ioka.android.iokademoapp.profile.savedCards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import kz.ioka.android.iokademoapp.R
import kz.ioka.android.iokademoapp.common.ListItem

class SavedCardsAdapter(
    var itemList: List<ListItem>,
    val onRemoveCardClicked: (cardId: String) -> Unit,
    val onAddCardClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_CARD = 0
        const val TYPE_ADD_CARD = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_CARD) {
            CardViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
            )
        } else {
            AddCardViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_add_card, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CardViewHolder) {
            val card = itemList[position] as CardDvo
            holder.ivCardType.setImageDrawable(
                AppCompatResources.getDrawable(
                    holder.itemView.context,
                    card.cardType
                )
            )
            holder.tvCardPan.text = card.cardPan

            holder.btnRemoveCard.setOnClickListener {
                onRemoveCardClicked.invoke(card.id)
            }
        } else {
            holder as AddCardViewHolder
            holder.btnAddCard.setOnClickListener {
                onAddCardClicked.invoke()
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemList.count() - 1) TYPE_ADD_CARD
        else TYPE_CARD
    }

    fun updateList(data: List<ListItem>) {
        itemList = data
        notifyDataSetChanged()
    }
}