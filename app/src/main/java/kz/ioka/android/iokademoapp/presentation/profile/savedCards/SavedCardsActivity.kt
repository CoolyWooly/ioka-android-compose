package kz.ioka.android.iokademoapp.presentation.profile.savedCards

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kz.ioka.android.ioka.api.Ioka
import kz.ioka.android.iokademoapp.BaseActivity
import kz.ioka.android.iokademoapp.R

@AndroidEntryPoint
class SavedCardsActivity : BaseActivity(), View.OnClickListener {

    private val viewModel: SavedCardsViewModel by viewModels()

    private lateinit var vToolbar: MaterialToolbar
    private lateinit var rvSavedCards: RecyclerView

    private lateinit var adapter: SavedCardsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_cards)

        vToolbar = findViewById(R.id.vToolbar)
        rvSavedCards = findViewById(R.id.rvSavedCards)

        setupCardsList()

        vToolbar.setNavigationOnClickListener(this)

        viewModel.savedCards.observe(this) {
            adapter.updateList(it)
        }

        viewModel.paymentFlow.observe(this) {
            Ioka.showForm(it).invoke(this)
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.fetchCards()
    }

    private fun setupCardsList() {
        rvSavedCards.layoutManager = LinearLayoutManager(this)

        adapter = SavedCardsAdapter(
            itemList = emptyList(),
            onRemoveCardClicked = { viewModel.onRemoveCardClicked(it) },
            onAddCardClicked = { viewModel.onAddCardClicked() }
        )

        rvSavedCards.adapter = adapter
    }

    override fun onClick(v: View?) {
        onBackPressed()
    }

}