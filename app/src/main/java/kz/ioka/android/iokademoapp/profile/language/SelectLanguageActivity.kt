package kz.ioka.android.iokademoapp.profile.language

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import kz.ioka.android.iokademoapp.BaseActivity
import kz.ioka.android.iokademoapp.R
import kz.ioka.android.iokademoapp.common.Locale

@AndroidEntryPoint
class SelectLanguageActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val LANGUAGE_SELECTED_REQUEST_CODE = 420
    }

    private val viewModel: SelectLanguageViewModel by viewModels()

    private lateinit var vToolbar: MaterialToolbar

    private lateinit var btnRussian: LinearLayoutCompat
    private lateinit var ivRussianCheck: AppCompatImageView
    private lateinit var btnEnglish: LinearLayoutCompat
    private lateinit var ivEnglishCheck: AppCompatImageView
    private lateinit var btnKazakh: LinearLayoutCompat
    private lateinit var ivKazakhCheck: AppCompatImageView

    private lateinit var btnSave: AppCompatButton

    private var shownCheck: AppCompatImageView? = null
    private lateinit var selectedLocale: Locale

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_language)

        vToolbar = findViewById(R.id.vToolbar)
        btnRussian = findViewById(R.id.btnRussian)
        ivRussianCheck = findViewById(R.id.ivCheckRussian)
        btnEnglish = findViewById(R.id.btnEnglish)
        ivEnglishCheck = findViewById(R.id.ivCheckEnglish)
        btnKazakh = findViewById(R.id.btnKazakh)
        ivKazakhCheck = findViewById(R.id.ivCheckKazakh)
        btnSave = findViewById(R.id.btnSave)

        initCurrentLanguage()
        setupListeners()
    }

    private fun initCurrentLanguage() {
        viewModel.selectedLanguage.observe(this) {
            onLanguageSelected(it)
        }
    }

    private fun onLanguageSelected(selectedLocale: Locale) {
        this.selectedLocale = selectedLocale

        val selectedLanguageCheck = when (selectedLocale) {
            Locale.RU -> {
                ivRussianCheck
            }
            Locale.EN -> {
                ivEnglishCheck
            }
            Locale.KZ -> {
                ivKazakhCheck
            }
        }

        shownCheck?.visibility = View.GONE

        selectedLanguageCheck.visibility = View.VISIBLE
        shownCheck = selectedLanguageCheck
    }

    private fun setupListeners() {
        vToolbar.setNavigationOnClickListener(this)

        btnRussian.setOnClickListener(this)
        btnEnglish.setOnClickListener(this)
        btnKazakh.setOnClickListener(this)
        btnSave.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            btnRussian -> viewModel.onLanguageSelected(Locale.RU)
            btnEnglish -> viewModel.onLanguageSelected(Locale.EN)
            btnKazakh -> viewModel.onLanguageSelected(Locale.KZ)
            btnSave -> {
                viewModel.onSaveClicked {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
            else -> onBackPressed()
        }
    }

}