package com.stripe.android.paymentsheet.forms

import android.content.res.Resources
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.stripe.android.paymentsheet.elements.Form
import com.stripe.android.paymentsheet.elements.FormElement
import com.stripe.android.paymentsheet.elements.LayoutSpec
import com.stripe.android.paymentsheet.elements.MandateTextElement
import com.stripe.android.paymentsheet.elements.ResourceRepository
import com.stripe.android.paymentsheet.elements.SaveForFutureUseElement
import com.stripe.android.paymentsheet.elements.SectionSpec
import com.stripe.android.paymentsheet.injection.DaggerFormViewModelComponent
import com.stripe.android.paymentsheet.paymentdatacollection.FormFragmentArguments
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class stores the visual field layout for the [Form] and then sets up the controller
 * for all the fields on screen.  When all fields are reported as complete, the completedFieldValues
 * holds the resulting values for each field.
 *
 * @param: layout - this contains the visual layout of the fields on the screen used by [Form]
 * to display the UI fields on screen.  It also informs us of the backing fields to be created.
 */
@Singleton
internal class FormViewModel @Inject internal constructor(
    layout: LayoutSpec,
    config: FormFragmentArguments,
    private val resourceRepository: ResourceRepository
) : ViewModel() {
    internal class Factory(
        private val resources: Resources,
        private val layout: LayoutSpec,
        private val formArguments: FormFragmentArguments
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {

            // This is where we will call Dagger:
            return DaggerFormViewModelComponent.builder()
                .resources(resources)
                .layout(layout)
                .formFragmentArguments(formArguments)
                .build()
                .viewModel as T
        }
    }

    private val transformSpecToElement = TransformSpecToElement(resourceRepository, config)

    init {
        viewModelScope.launch {
            resourceRepository.init()
            elements = transformSpecToElement.transform(layout.items)
        }
    }

    internal val enabled = MutableStateFlow(true)
    internal fun setEnabled(enabled: Boolean) {
        this.enabled.value = enabled
    }

    internal lateinit var elements: List<FormElement>

    private val intentSupportsUserRequestedReuse = MutableStateFlow(config.intentAndPmAllowUserInitiatedReuse)

    @VisibleForTesting
    internal fun setUserRequestedReuse(value: Boolean) {
        elements
            .filterIsInstance<SaveForFutureUseElement>()
            .firstOrNull()?.controller?.onValueChange(value)
    }

    @VisibleForTesting
    internal val saveForFutureUseElement = elements
        .filterIsInstance<SaveForFutureUseElement>()
        .firstOrNull()

    private val sectionToFieldIdentifierMap = layout.items
        .filterIsInstance<SectionSpec>()
        .associate { sectionSpec ->
            sectionSpec.identifier to sectionSpec.fields.map {
                it.identifier
            }
        }

    internal val hiddenIdentifiers =
        combine(
            intentSupportsUserRequestedReuse,
            // Regardless of checkbox visibility it's state will defi¬-ne if
            // reusable fields are displayed
            saveForFutureUseElement?.controller?.hiddenIdentifiers
                ?: MutableStateFlow(emptyList())
        ) { allowUserInitiatedReuse, hiddenIdentifiers ->

            // For hidden *section* identifiers, list of identifiers of elements in the section
            val identifiers = sectionToFieldIdentifierMap
                .filter { idControllerPair ->
                    hiddenIdentifiers.contains(idControllerPair.key)
                }
                .flatMap { sectionToSectionFieldEntry ->
                    sectionToSectionFieldEntry.value
                }

            // If we don't allow userInitiated reuse then the save for future use identifier
            // won't be in the map, and wont set the value in the PaymentSelection.
            if (!allowUserInitiatedReuse && saveForFutureUseElement != null) {
                hiddenIdentifiers
                    .plus(identifiers)
                    .plus(saveForFutureUseElement.identifier)
            } else {
                hiddenIdentifiers
                    .plus(identifiers)
            }
        }

    // Mandate is showing if it is an element of the form and it isn't hidden
    internal val showingMandate = hiddenIdentifiers.map {
        elements
            .filterIsInstance<MandateTextElement>()
            .firstOrNull()?.let { mandate ->
                !it.contains(mandate.identifier)
            } ?: false
    }

    internal val userRequestedReuse = combine(
        intentSupportsUserRequestedReuse,
        saveForFutureUseElement?.controller?.saveForFutureUse ?: MutableStateFlow(false)
    ) { allowUserInitiatedReuse, userCheckedReuse ->
        allowUserInitiatedReuse && userCheckedReuse
    }

    val completeFormValues =
        TransformFormFieldValues(
            combine(
                elements.map { it.getFormFieldValueFlow() }
            ) {
                it.toList().flatten().toMap()
            },
            hiddenIdentifiers,
            showingMandate,
            userRequestedReuse
        ).transform()
}
