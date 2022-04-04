package kz.ioka.android.ioka.presentation.flows.saveCard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import kz.ioka.android.ioka.domain.saveCard.CardRepository
import org.junit.Before
import org.junit.Rule
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class BindCardViewModelTest {

    companion object {
        const val API_KEY = ""
        const val CUSTOMER_TOKEN = ""
        const val CARD_PAN = ""
        const val EXPIRE_DATE = ""
        const val CVV = ""
        private val configuration = Configuration()
    }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: SaveCardViewModel
    private lateinit var repository: CardRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(Dispatchers.Unconfined)

        repository = mock(CardRepository::class.java)
//        viewModel =
//            BindCardViewModel(BindCardLauncher(API_KEY, CUSTOMER_TOKEN, configuration), repository)
    }

//    @Test
//    fun testInitialValues() {
//        assertFalse(viewModel.isCardPanValid().value ?: true)
//        assertFalse(viewModel.isExpireDateValid().value ?: true)
//        assertFalse(viewModel.isCvvValid().value ?: true)
//        assertFalse(viewModel.allFieldsAreValid().value ?: true)
//        assertTrue(viewModel.bindRequestState.value is BindCardRequestState.DISABLED)
//    }
//
//    @Test
//    fun cardPanValidation_Valid() = runBlocking {
//        viewModel.onCardPanEntered("1234567812345678")
//
//        assertTrue(viewModel.isCardPanValid().value ?: false)
//    }
//
//    @Test
//    fun cardPanValidation_NotValid() {
//        viewModel.onCardPanEntered("123456")
//
//        assertFalse(viewModel.isCardPanValid().value ?: true)
//    }

//    @Test
//    fun expireDateValidation_Valid() {
//        viewModel.onExpireDateEntered("1224")
//
//        assertTrue(viewModel.isExpireDateValid().value ?: false)
//    }
//
//    @Test
//    fun expireDateValidation_NotValid_Length() {
//        viewModel.onExpireDateEntered("123")
//
//        assertFalse(viewModel.isExpireDateValid().value ?: true)
//    }
//
//    @Test
//    fun expireDateValidation_NotValid_PassedDate() {
//        viewModel.onExpireDateEntered("1220")
//
//        assertFalse(viewModel.isExpireDateValid().value ?: true)
//    }
//
//    @Test
//    fun expireDateValidation_NotValid_WrongMonth() {
//        viewModel.onExpireDateEntered("1320")
//
//        assertFalse(viewModel.isExpireDateValid().value ?: true)
//    }
//
//    @Test
//    fun cvvValidation_Valid() {
//        viewModel.onCvvEntered("132")
//
//        assertTrue(viewModel.isCvvValid().value ?: false)
//    }
//
//    @Test
//    fun cvvValidation_NotValid() {
//        viewModel.onCvvEntered("13")
//
//        assertFalse(viewModel.isCvvValid().value ?: true)
//    }

//    @Test
//    fun bindCard_FieldsNotValid() {
//        viewModel.onCardPanEntered("")
//        viewModel.onExpireDateEntered("")
//        viewModel.onCvvEntered("")
//
//        viewModel.onBindClicked("", "", "")
//
//        runBlocking { verify(repository, never()).bindCard("", "", "", "", "") }
//    }
//
//    @Test
//    fun bindCard_GenericError() {
//
//        viewModel.onCardPanEntered("1234567812345678")
//        viewModel.onExpireDateEntered("1224")
//        viewModel.onCvvEntered("123")
//
//        runBlockingTest {
//            `when`(
//                repository.bindCard(
//                    "",
//                    "",
//                    "",
//                    "",
//                    ""
//                )
//            ).thenReturn(ResultWrapper.GenericError())
//        }
//
//        viewModel.onBindClicked("", "", "")
//
//        assertTrue(viewModel.bindRequestState.value is BindCardRequestState.ERROR)
//    }


}