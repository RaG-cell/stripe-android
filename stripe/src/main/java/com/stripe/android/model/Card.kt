package com.stripe.android.model

import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.annotation.Size
import androidx.annotation.StringDef
import com.stripe.android.CardUtils
import com.stripe.android.ObjectBuilder
import com.stripe.android.R
import com.stripe.android.model.StripeJsonUtils.optCountryCode
import com.stripe.android.model.StripeJsonUtils.optCurrency
import com.stripe.android.model.StripeJsonUtils.optHash
import com.stripe.android.model.StripeJsonUtils.optInteger
import com.stripe.android.model.StripeJsonUtils.optString
import java.util.Calendar
import java.util.HashMap
import java.util.Objects
import org.json.JSONException
import org.json.JSONObject

/**
 * A model object representing a Card in the Android SDK.
 */
class Card private constructor(
    builder: Builder
) : StripeModel(), StripePaymentSource, StripeParamsModel {

    /**
     * @return the [number] of this card
     */
    val number: String?

    /**
     * @return the [cvc] for this card
     */
    val cvc: String?

    /**
     * @return the [expMonth] for this card
     */
    @get:IntRange(from = 1, to = 12)
    val expMonth: Int?

    /**
     * @return the [expYear] for this card
     */
    val expYear: Int?

    /**
     * @return the cardholder [name] for this card
     */
    val name: String?

    /**
     * @return the [addressLine1] of this card
     */
    val addressLine1: String?

    /**
     * @return If address_line1 was provided, results of the check:
     * pass, fail, unavailable, or unchecked.
     */
    val addressLine1Check: String?

    /**
     * @return the [addressLine2] of this card
     */
    val addressLine2: String?

    /**
     * @return the [addressCity] for this card
     */
    val addressCity: String?

    /**
     * @return the [addressState] of this card
     */
    val addressState: String?

    /**
     * @return the [addressZip] of this card
     */
    val addressZip: String?

    /**
     * @return If address_zip was provided, results of the check:
     * pass, fail, unavailable, or unchecked.
     */
    val addressZipCheck: String?

    /**
     * @return the [addressCountry] of this card
     */
    val addressCountry: String?

    /**
     * @return the [last4] digits of this card.
     */
    @Size(4)
    val last4: String?

    /**
     * Gets the [brand] of this card. Updates the value if none has yet been set, or
     * if the [number] has been changed.
     *
     * @return the [brand] of this card
     */
    @CardBrand
    @get:CardBrand
    val brand: String?

    /**
     * @return the [funding] type of this card
     */
    @FundingType
    @get:FundingType
    val funding: String?

    /**
     * @return the [fingerprint] of this card
     */
    val fingerprint: String?

    /**
     * @return the [country] of this card
     */
    val country: String?

    /**
     * @return the [currency] of this card. Only supported for Managed accounts.
     */
    val currency: String?

    /**
     * @return The ID of the customer that this card belongs to.
     */
    val customerId: String?

    /**
     * @return If a CVC was provided, results of the check:
     * pass, fail, unavailable, or unchecked.
     */
    val cvcCheck: String?

    /**
     * @return the [id] of this card
     */
    override val id: String?
    internal val loggingTokens: MutableList<String> = mutableListOf()
    internal val tokenizationMethod: String?

    /**
     * @return the [metadata] of this card
     */
    val metadata: Map<String, String>?

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(CardBrand.AMERICAN_EXPRESS, CardBrand.DISCOVER, CardBrand.JCB,
        CardBrand.DINERS_CLUB, CardBrand.VISA, CardBrand.MASTERCARD,
        CardBrand.UNIONPAY, CardBrand.UNKNOWN)
    annotation class CardBrand {
        companion object {
            const val AMERICAN_EXPRESS: String = "American Express"
            const val DISCOVER: String = "Discover"
            const val JCB: String = "JCB"
            const val DINERS_CLUB: String = "Diners Club"
            const val VISA: String = "Visa"
            const val MASTERCARD: String = "MasterCard"
            const val UNIONPAY: String = "UnionPay"
            const val UNKNOWN: String = "Unknown"
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(FundingType.CREDIT, FundingType.DEBIT, FundingType.PREPAID, FundingType.UNKNOWN)
    annotation class FundingType {
        companion object {
            const val CREDIT: String = "credit"
            const val DEBIT: String = "debit"
            const val PREPAID: String = "prepaid"
            const val UNKNOWN: String = "unknown"
        }
    }

    @Deprecated("getCVC() is deprecated", ReplaceWith("Use getCvc()"))
    fun getCVC(): String? {
        return cvc
    }

    fun toPaymentMethodParamsCard(): PaymentMethodCreateParams.Card {
        return PaymentMethodCreateParams.Card.Builder()
            .setNumber(number)
            .setCvc(cvc)
            .setExpiryMonth(expMonth)
            .setExpiryYear(expYear)
            .build()
    }

    /**
     * @return a [Card.Builder] populated with the fields of this [Card] instance
     */
    fun toBuilder(): Builder {
        return Builder(number, expMonth, expYear, cvc)
            .name(name)
            .addressLine1(addressLine1)
            .addressLine1Check(addressLine1Check)
            .addressLine2(addressLine2)
            .addressCity(addressCity)
            .addressState(addressState)
            .addressZip(addressZip)
            .addressZipCheck(addressZipCheck)
            .addressCountry(addressCountry)
            .brand(brand)
            .fingerprint(fingerprint)
            .funding(funding)
            .country(country)
            .currency(currency)
            .customer(customerId)
            .cvcCheck(cvcCheck)
            .last4(last4)
            .id(id)
            .tokenizationMethod(tokenizationMethod)
            .metadata(metadata)
            .loggingTokens(loggingTokens)
    }

    /**
     * Checks whether `this` represents a valid card.
     *
     * @return `true` if valid, `false` otherwise.
     */
    fun validateCard(): Boolean {
        return validateCard(Calendar.getInstance())
    }

    /**
     * Checks whether or not the [number] field is valid.
     *
     * @return `true` if valid, `false` otherwise.
     */
    fun validateNumber(): Boolean {
        return CardUtils.isValidCardNumber(number)
    }

    /**
     * Checks whether or not the [expMonth] and [expYear] fields represent a valid
     * expiry date.
     *
     * @return `true` if valid, `false` otherwise
     */
    fun validateExpiryDate(): Boolean {
        return validateExpiryDate(Calendar.getInstance())
    }

    /**
     * Checks whether or not the [cvc] field is valid.
     *
     * @return `true` if valid, `false` otherwise
     */
    fun validateCVC(): Boolean {
        if (cvc.isNullOrBlank()) {
            return false
        }
        val cvcValue = cvc.trim()
        val updatedType = brand
        val validLength =
            updatedType == null && cvcValue.length >= 3 && cvcValue.length <= 4 ||
                CardBrand.AMERICAN_EXPRESS == updatedType && cvcValue.length == 4 ||
                cvcValue.length == 3

        return ModelUtils.isWholePositiveNumber(cvcValue) && validLength
    }

    /**
     * Checks whether or not the [expMonth] field is valid.
     *
     * @return `true` if valid, `false` otherwise.
     */
    fun validateExpMonth(): Boolean {
        return expMonth?.let { expMonth -> expMonth in 1..12 } == true
    }

    /**
     * Checks whether or not the [expYear] field is valid.
     *
     * @return `true` if valid, `false` otherwise.
     */
    internal fun validateExpYear(now: Calendar): Boolean {
        return expYear?.let { !ModelUtils.hasYearPassed(it, now) } == true
    }

    private fun calculateLast4(number: String?, last4: String?): String? {
        return if (!last4.isNullOrBlank()) {
            last4
        } else if (number != null && number.length > 4) {
            number.substring(number.length - 4)
        } else {
            null
        }
    }

    private fun calculateBrand(brand: String?): String? {
        return if (brand.isNullOrBlank() && !number.isNullOrBlank()) {
            CardUtils.getPossibleCardType(number)
        } else {
            brand
        }
    }

    internal fun validateCard(now: Calendar): Boolean {
        return if (cvc == null) {
            validateNumber() && validateExpiryDate(now)
        } else {
            validateNumber() && validateExpiryDate(now) && validateCVC()
        }
    }

    internal fun validateExpiryDate(now: Calendar): Boolean {
        val expMonth = this.expMonth
        if (expMonth == null || !validateExpMonth()) {
            return false
        }

        return if (expYear == null || !validateExpYear(now)) {
            false
        } else {
            !ModelUtils.hasMonthPassed(expYear, expMonth, now)
        }
    }

    init {
        this.number = normalizeCardNumber(builder.number).takeUnless { it.isNullOrBlank() }
        this.expMonth = builder.expMonth
        this.expYear = builder.expYear
        this.cvc = builder.cvc.takeUnless { it.isNullOrBlank() }
        this.name = builder.name.takeUnless { it.isNullOrBlank() }
        this.addressLine1 = builder.addressLine1.takeUnless { it.isNullOrBlank() }
        this.addressLine1Check = builder.addressLine1Check.takeUnless { it.isNullOrBlank() }
        this.addressLine2 = builder.addressLine2.takeUnless { it.isNullOrBlank() }
        this.addressCity = builder.addressCity.takeUnless { it.isNullOrBlank() }
        this.addressState = builder.addressState.takeUnless { it.isNullOrBlank() }
        this.addressZip = builder.addressZip.takeUnless { it.isNullOrBlank() }
        this.addressZipCheck = builder.addressZipCheck.takeUnless { it.isNullOrBlank() }
        this.addressCountry = builder.addressCountry.takeUnless { it.isNullOrBlank() }

        this.last4 = if (builder.last4.isNullOrBlank()) {
            calculateLast4(number, builder.last4)
        } else {
            builder.last4
        }

        this.brand = if (asCardBrand(builder.brand) == null) {
            calculateBrand(builder.brand)
        } else {
            builder.brand
        }

        this.fingerprint = builder.fingerprint.takeUnless { it.isNullOrBlank() }
        this.funding = asFundingType(builder.funding).takeUnless { it.isNullOrBlank() }
        this.country = builder.country.takeUnless { it.isNullOrBlank() }
        this.currency = builder.currency.takeUnless { it.isNullOrBlank() }
        this.customerId = builder.customerId.takeUnless { it.isNullOrBlank() }
        this.cvcCheck = builder.cvcCheck.takeUnless { it.isNullOrBlank() }
        this.id = builder.id.takeUnless { it.isNullOrBlank() }
        this.tokenizationMethod = builder.tokenizationMethod.takeUnless { it.isNullOrBlank() }
        this.metadata = builder.metadata

        builder.loggingTokens?.let { loggingTokens ->
            this.loggingTokens.addAll(loggingTokens)
        }
    }

    private fun normalizeCardNumber(number: String?): String? {
        return number?.trim()?.replace("\\s+|-".toRegex(), "")
    }

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other is Card -> typedEquals(other)
            else -> false
        }
    }

    private fun typedEquals(card: Card): Boolean {
        return number == card.number &&
            cvc == card.cvc &&
            expMonth == card.expMonth &&
            expYear == card.expYear &&
            name == card.name &&
            addressLine1 == card.addressLine1 &&
            addressLine1Check == card.addressLine1Check &&
            addressLine2 == card.addressLine2 &&
            addressCity == card.addressCity &&
            addressState == card.addressState &&
            addressZip == card.addressZip &&
            addressZipCheck == card.addressZipCheck &&
            addressCountry == card.addressCountry &&
            last4 == card.last4 &&
            brand == card.brand &&
            funding == card.funding &&
            fingerprint == card.fingerprint &&
            country == card.country &&
            currency == card.currency &&
            customerId == card.customerId &&
            cvcCheck == card.cvcCheck &&
            id == card.id &&
            loggingTokens == card.loggingTokens &&
            tokenizationMethod == card.tokenizationMethod &&
            metadata == card.metadata
    }

    override fun hashCode(): Int {
        return Objects.hash(number, cvc, expMonth, expYear, name, addressLine1,
            addressLine1Check, addressLine2, addressCity, addressState, addressZip,
            addressZipCheck, addressCountry, last4, brand, funding, fingerprint,
            country, currency, customerId, cvcCheck, id, loggingTokens, tokenizationMethod,
            metadata)
    }

    override fun toParamMap(): Map<String, Any> {
        return mapOf(Token.TokenType.CARD to createCardParams())
    }

    private fun createCardParams(): Map<String, Any?> {
        return mapOf(
            "number" to number.takeUnless { it.isNullOrBlank() },
            "cvc" to cvc.takeUnless { it.isNullOrBlank() },
            "exp_month" to expMonth,
            "exp_year" to expYear,
            "name" to name.takeUnless { it.isNullOrBlank() },
            "currency" to currency.takeUnless { it.isNullOrBlank() },
            "address_line1" to addressLine1.takeUnless { it.isNullOrBlank() },
            "address_line2" to addressLine2.takeUnless { it.isNullOrBlank() },
            "address_city" to addressCity.takeUnless { it.isNullOrBlank() },
            "address_zip" to addressZip.takeUnless { it.isNullOrBlank() },
            "address_state" to addressState.takeUnless { it.isNullOrBlank() },
            "address_country" to addressCountry.takeUnless { it.isNullOrBlank() }
        )
    }

    /**
     * Builder class for a [Card] model.
     *
     * Constructor with most common [Card] fields.
     *
     * @param number the credit card number
     * @param expMonth the expiry month, as an integer value between 1 and 12
     * @param expYear the expiry year
     * @param cvc the card CVC number
     */
    class Builder(
        internal val number: String?,
        @param:IntRange(from = 1, to = 12) internal val expMonth: Int?,
        @param:IntRange(from = 0) internal val expYear: Int?,
        internal val cvc: String?
    ) : ObjectBuilder<Card> {
        internal var name: String? = null
        internal var addressLine1: String? = null
        internal var addressLine1Check: String? = null
        internal var addressLine2: String? = null
        internal var addressCity: String? = null
        internal var addressState: String? = null
        internal var addressZip: String? = null
        internal var addressZipCheck: String? = null
        internal var addressCountry: String? = null
        @CardBrand
        internal var brand: String? = null
        @FundingType
        internal var funding: String? = null
        @Size(4)
        internal var last4: String? = null
        internal var fingerprint: String? = null
        internal var country: String? = null
        internal var currency: String? = null
        internal var customerId: String? = null
        internal var cvcCheck: String? = null
        internal var id: String? = null
        internal var tokenizationMethod: String? = null
        internal var metadata: Map<String, String>? = null
        internal var loggingTokens: List<String>? = null

        fun name(name: String?): Builder {
            this.name = name
            return this
        }

        fun addressLine1(address: String?): Builder {
            this.addressLine1 = address
            return this
        }

        fun addressLine1Check(addressLine1Check: String?): Builder {
            this.addressLine1Check = addressLine1Check
            return this
        }

        fun addressLine2(address: String?): Builder {
            this.addressLine2 = address
            return this
        }

        fun addressCity(city: String?): Builder {
            this.addressCity = city
            return this
        }

        fun addressState(state: String?): Builder {
            this.addressState = state
            return this
        }

        fun addressZip(zip: String?): Builder {
            this.addressZip = zip
            return this
        }

        fun addressZipCheck(zipCheck: String?): Builder {
            this.addressZipCheck = zipCheck
            return this
        }

        fun addressCountry(country: String?): Builder {
            this.addressCountry = country
            return this
        }

        fun brand(@CardBrand brand: String?): Builder {
            this.brand = brand
            return this
        }

        fun fingerprint(fingerprint: String?): Builder {
            this.fingerprint = fingerprint
            return this
        }

        fun funding(@FundingType funding: String?): Builder {
            this.funding = funding
            return this
        }

        fun country(country: String?): Builder {
            this.country = country
            return this
        }

        fun currency(currency: String?): Builder {
            this.currency = currency
            return this
        }

        fun customer(customerId: String?): Builder {
            this.customerId = customerId
            return this
        }

        fun cvcCheck(cvcCheck: String?): Builder {
            this.cvcCheck = cvcCheck
            return this
        }

        fun last4(last4: String?): Builder {
            this.last4 = last4
            return this
        }

        fun id(id: String?): Builder {
            this.id = id
            return this
        }

        fun tokenizationMethod(tokenizationMethod: String?): Builder {
            this.tokenizationMethod = tokenizationMethod
            return this
        }

        fun metadata(metadata: Map<String, String>?): Builder {
            this.metadata = metadata
            return this
        }

        fun loggingTokens(loggingTokens: List<String>): Builder {
            this.loggingTokens = loggingTokens
            return this
        }

        /**
         * Generate a new [Card] object based on the arguments held by this Builder.
         *
         * @return the newly created [Card] object
         */
        override fun build(): Card {
            return Card(this)
        }
    }

    companion object {
        const val CVC_LENGTH_AMERICAN_EXPRESS: Int = 4
        const val CVC_LENGTH_COMMON: Int = 3

        /**
         * Based on [Issuer identification number table](http://en.wikipedia.org/wiki/Bank_card_number#Issuer_identification_number_.28IIN.29)
         */
        val PREFIXES_AMERICAN_EXPRESS: Array<String> = arrayOf("34", "37")
        val PREFIXES_DISCOVER: Array<String> = arrayOf("60", "64", "65")
        val PREFIXES_JCB: Array<String> = arrayOf("35")
        val PREFIXES_DINERS_CLUB: Array<String> = arrayOf(
            "300", "301", "302", "303", "304", "305", "309", "36", "38", "39"
        )
        val PREFIXES_VISA: Array<String> = arrayOf("4")
        val PREFIXES_MASTERCARD: Array<String> = arrayOf(
            "2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228", "2229", "223", "224",
            "225", "226", "227", "228", "229", "23", "24", "25", "26", "270", "271", "2720",
            "50", "51", "52", "53", "54", "55", "67"
        )
        val PREFIXES_UNIONPAY: Array<String> = arrayOf("62")

        const val MAX_LENGTH_STANDARD: Int = 16
        const val MAX_LENGTH_AMERICAN_EXPRESS: Int = 15
        const val MAX_LENGTH_DINERS_CLUB: Int = 14

        internal const val VALUE_CARD = "card"

        private const val FIELD_OBJECT = "object"
        private const val FIELD_ADDRESS_CITY = "address_city"
        private const val FIELD_ADDRESS_COUNTRY = "address_country"
        private const val FIELD_ADDRESS_LINE1 = "address_line1"
        private const val FIELD_ADDRESS_LINE1_CHECK = "address_line1_check"
        private const val FIELD_ADDRESS_LINE2 = "address_line2"
        private const val FIELD_ADDRESS_STATE = "address_state"
        private const val FIELD_ADDRESS_ZIP = "address_zip"
        private const val FIELD_ADDRESS_ZIP_CHECK = "address_zip_check"
        private const val FIELD_BRAND = "brand"
        private const val FIELD_COUNTRY = "country"
        private const val FIELD_CURRENCY = "currency"
        private const val FIELD_CUSTOMER = "customer"
        private const val FIELD_CVC_CHECK = "cvc_check"
        private const val FIELD_EXP_MONTH = "exp_month"
        private const val FIELD_EXP_YEAR = "exp_year"
        private const val FIELD_FINGERPRINT = "fingerprint"
        private const val FIELD_FUNDING = "funding"
        private const val FIELD_METADATA = "metadata"
        private const val FIELD_NAME = "name"
        private const val FIELD_LAST4 = "last4"
        private const val FIELD_ID = "id"
        private const val FIELD_TOKENIZATION_METHOD = "tokenization_method"

        private val BRAND_RESOURCE_MAP = object : HashMap<String, Int>() {
            init {
                put(CardBrand.AMERICAN_EXPRESS, R.drawable.ic_amex)
                put(CardBrand.DINERS_CLUB, R.drawable.ic_diners)
                put(CardBrand.DISCOVER, R.drawable.ic_discover)
                put(CardBrand.JCB, R.drawable.ic_jcb)
                put(CardBrand.MASTERCARD, R.drawable.ic_mastercard)
                put(CardBrand.VISA, R.drawable.ic_visa)
                put(CardBrand.UNIONPAY, R.drawable.ic_unionpay)
                put(CardBrand.UNKNOWN, R.drawable.ic_unknown)
            }
        }

        /**
         * Converts an unchecked String value to a [CardBrand] or `null`.
         *
         * @param possibleCardType a String that might match a [CardBrand] or be empty.
         * @return `null` if the input is blank, else the appropriate [CardBrand].
         */
        @JvmStatic
        @CardBrand
        fun asCardBrand(possibleCardType: String?): String? {
            if (possibleCardType.isNullOrBlank()) {
                return null
            }

            return when {
                CardBrand.AMERICAN_EXPRESS.equals(possibleCardType, ignoreCase = true) ->
                    CardBrand.AMERICAN_EXPRESS
                CardBrand.MASTERCARD.equals(possibleCardType, ignoreCase = true) ->
                    CardBrand.MASTERCARD
                CardBrand.DINERS_CLUB.equals(possibleCardType, ignoreCase = true) ->
                    CardBrand.DINERS_CLUB
                CardBrand.DISCOVER.equals(possibleCardType, ignoreCase = true) ->
                    CardBrand.DISCOVER
                CardBrand.JCB.equals(possibleCardType, ignoreCase = true) ->
                    CardBrand.JCB
                CardBrand.VISA.equals(possibleCardType, ignoreCase = true) ->
                    CardBrand.VISA
                CardBrand.UNIONPAY.equals(possibleCardType, ignoreCase = true) ->
                    CardBrand.UNIONPAY
                else -> CardBrand.UNKNOWN
            }
        }

        /**
         * Converts an unchecked String value to a [FundingType] or `null`.
         *
         * @param possibleFundingType a String that might match a [FundingType] or be empty
         * @return `null` if the input is blank, else the appropriate [FundingType]
         */
        @JvmStatic
        @FundingType
        fun asFundingType(possibleFundingType: String?): String? {
            if (possibleFundingType.isNullOrBlank()) {
                return null
            }

            return when {
                FundingType.CREDIT.equals(possibleFundingType, ignoreCase = true) ->
                    FundingType.CREDIT
                FundingType.DEBIT.equals(possibleFundingType, ignoreCase = true) ->
                    FundingType.DEBIT
                FundingType.PREPAID.equals(possibleFundingType, ignoreCase = true) ->
                    FundingType.PREPAID
                else -> FundingType.UNKNOWN
            }
        }

        @JvmStatic
        @DrawableRes
        fun getBrandIcon(brand: String?): Int {
            val brandIcon = BRAND_RESOURCE_MAP[brand]
            return brandIcon ?: R.drawable.ic_unknown
        }

        /**
         * Create a Card object from a raw JSON string.
         *
         * @param jsonString the JSON string representing the potential Card
         * @return A Card if one can be made from the JSON, or `null` if one cannot be made
         * or the JSON is invalid.
         */
        @JvmStatic
        fun fromString(jsonString: String): Card? {
            return try {
                val jsonObject = JSONObject(jsonString)
                fromJson(jsonObject)
            } catch (ignored: JSONException) {
                null
            }
        }

        @JvmStatic
        fun fromJson(jsonObject: JSONObject?): Card? {
            if (jsonObject == null || VALUE_CARD != jsonObject.optString(FIELD_OBJECT)) {
                return null
            }

            // It's okay for the month to be missing, but not for it to be outside 1-12.
            // We treat an invalid month the same way we would an invalid brand, by reading it as
            // null.
            val expMonth = (optInteger(jsonObject, FIELD_EXP_MONTH) ?: -1)
                .takeUnless { it < 1 || it > 12 }
            val expYear = (optInteger(jsonObject, FIELD_EXP_YEAR) ?: -1)
                .takeUnless { it < 0 }

            // Note that we'll never get the CVC or card number in JSON, so those values are null
            return Builder(null, expMonth, expYear, null)
                .addressCity(optString(jsonObject, FIELD_ADDRESS_CITY))
                .addressLine1(optString(jsonObject, FIELD_ADDRESS_LINE1))
                .addressLine1Check(optString(jsonObject, FIELD_ADDRESS_LINE1_CHECK))
                .addressLine2(optString(jsonObject, FIELD_ADDRESS_LINE2))
                .addressCountry(optString(jsonObject, FIELD_ADDRESS_COUNTRY))
                .addressState(optString(jsonObject, FIELD_ADDRESS_STATE))
                .addressZip(optString(jsonObject, FIELD_ADDRESS_ZIP))
                .addressZipCheck(optString(jsonObject, FIELD_ADDRESS_ZIP_CHECK))
                .brand(asCardBrand(optString(jsonObject, FIELD_BRAND)))
                .country(optCountryCode(jsonObject, FIELD_COUNTRY))
                .customer(optString(jsonObject, FIELD_CUSTOMER))
                .currency(optCurrency(jsonObject, FIELD_CURRENCY))
                .cvcCheck(optString(jsonObject, FIELD_CVC_CHECK))
                .funding(asFundingType(optString(jsonObject, FIELD_FUNDING)))
                .fingerprint(optString(jsonObject, FIELD_FINGERPRINT))
                .id(optString(jsonObject, FIELD_ID))
                .last4(optString(jsonObject, FIELD_LAST4))
                .name(optString(jsonObject, FIELD_NAME))
                .tokenizationMethod(optString(jsonObject, FIELD_TOKENIZATION_METHOD))
                .metadata(optHash(jsonObject, FIELD_METADATA))
                .build()
        }

        /**
         * Convenience constructor for a Card object with a minimum number of inputs.
         *
         * @param number the card number
         * @param expMonth the expiry month
         * @param expYear the expiry year
         * @param cvc the CVC code
         */
        @JvmStatic
        fun create(
            number: String?,
            expMonth: Int?,
            expYear: Int?,
            cvc: String?
        ): Card {
            return Builder(number, expMonth, expYear, cvc)
                .build()
        }
    }
}
