package kz.ioka.android.iokademoapp.presentation.profile.savedCards

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kz.ioka.android.ioka.api.*
import kz.ioka.android.iokademoapp.BaseActivity
import kz.ioka.android.iokademoapp.R

@AndroidEntryPoint
class SavedCardsActivity : BaseActivity() {

    private val viewModel: SavedCardsViewModel by viewModels()

    private lateinit var vToolbar: Toolbar
    private lateinit var vCardsContainer: CardView
    private lateinit var rvSavedCards: RecyclerView
    private lateinit var vProgress: ProgressBar

    private lateinit var adapter: SavedCardsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_cards)

        bindViews()
        setupViews()
        observeData()
    }

    private fun bindViews() {
        vToolbar = findViewById(R.id.vToolbar)
        vCardsContainer = findViewById(R.id.vCardsContainer)
        rvSavedCards = findViewById(R.id.rvSavedCards)
        vProgress = findViewById(R.id.vProgress)
    }

    private fun setupViews() {
        rvSavedCards.layoutManager = LinearLayoutManager(this)

        adapter = SavedCardsAdapter(
            itemList = emptyList(),
            onRemoveCardClicked = { viewModel.onRemoveCardClicked(it) },
            onAddCardClicked = {
                Ioka.startSaveCardFlow(this, viewModel.customerToken)
            }
        )

        rvSavedCards.adapter = adapter

        vToolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun observeData() {
        viewModel.progress.observe(this) {
            vProgress.isVisible = it
            vCardsContainer.isInvisible = it
        }

        viewModel.savedCards.observe(this) {
            adapter.updateList(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IOKA_SAVE_CARD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val result = data?.getParcelableExtra<FlowResult>(IOKA_EXTRA_RESULT_NAME)

                result?.let {
                    if (it is FlowResult.Succeeded) viewModel.fetchCards()
                }
            }
        }
    }

}