package net.pototskiy.apps.magemediation.api.config.type

import net.pototskiy.apps.magemediation.api.DEFAULT_LOCALE
import net.pototskiy.apps.magemediation.api.config.ConfigDsl
import net.pototskiy.apps.magemediation.api.config.ConfigException
import java.util.*
import kotlin.reflect.KClass

@Suppress("MemberVisibilityCanBePrivate")
sealed class AttributeType(
    @Suppress("unused") val isList: Boolean,
    val hasLocale: Boolean,
    val hasPattern: Boolean,
    @Suppress("unused") val isAttrList: Boolean
) {
    open val locale: String = ""
    open val pattern: String = ""
    open val quote: String = ""
    open val delimiter: String = ""
    open val valueQuote: String = ""
    open val valueDelimiter: String = ""

    fun getLocaleObject(): Locale = if (hasLocale) {
        val (l, c) = locale.split("_")
        Locale(l, c)
    } else {
        Locale.getDefault()
    }

    @ConfigDsl
    class Builder(private val allowedTypes: List<KClass<out AttributeType>> = listOf()) {
        private var locale: String? = null
        private var pattern: String? = null
        private var quote: String? = null
        private var delimiter: String? = null
        private var valueQuote: String? = null
        private var valueDelimiter: String? = null

        private var typeBuilder: (() -> AttributeType)? = null

        @Suppress("unused")
        fun Builder.bool() {
            typeBuilder = { AttributeBoolType() }
        }

        @Suppress("unused")
        fun Builder.string(locale: String? = null): OnlyLocaleBuilder {
            if (locale != null) this.locale = locale
            typeBuilder = {
                AttributeStringType(
                    this.locale != null,
                    this.locale ?: DEFAULT_LOCALE
                )
            }
            return OnlyLocaleBuilder()
        }

        @Suppress("unused")
        fun Builder.text(locale: String? = null): OnlyLocaleBuilder {
            if (locale != null) this.locale = locale
            typeBuilder = {
                AttributeTextType(
                    this.locale != null,
                    this.locale ?: DEFAULT_LOCALE
                )
            }
            return OnlyLocaleBuilder()
        }

        @Suppress("unused")
        @Deprecated(
            "Will be removed after migration",
            ReplaceWith("long"),
            DeprecationLevel.WARNING
        )
        fun Builder.int(locale: String? = null): OnlyLocaleBuilder {
            if (locale != null) this.locale = locale
            typeBuilder = {
                AttributeLongType(
                    this.locale != null,
                    this.locale ?: DEFAULT_LOCALE
                )
            }
            return OnlyLocaleBuilder()
        }

        @Suppress("unused")
        fun Builder.long(locale: String? = null): OnlyLocaleBuilder {
            if (locale != null) this.locale = locale
            typeBuilder = {
                AttributeLongType(
                    this.locale != null,
                    this.locale ?: DEFAULT_LOCALE
                )
            }
            return OnlyLocaleBuilder()
        }

        @Suppress("unused")
        fun Builder.double(locale: String? = null): OnlyLocaleBuilder {
            if (locale != null) this.locale = locale
            typeBuilder = {
                AttributeDoubleType(
                    this.locale != null,
                    this.locale ?: DEFAULT_LOCALE
                )
            }
            return OnlyLocaleBuilder()
        }

        @Suppress("unused")
        fun Builder.date(locale: String? = null, pattern: String? = null): LocalePatternBuilder {
            if (locale != null) this.locale = locale
            if (pattern != null) this.pattern = pattern
            typeBuilder = {
                AttributeDateType(
                    this.pattern != null,
                    this.pattern ?: "",
                    this.locale != null,
                    this.locale ?: DEFAULT_LOCALE
                )
            }
            return LocalePatternBuilder()
        }

        @Suppress("unused")
        fun Builder.datetime(locale: String? = null, pattern: String? = null): LocalePatternBuilder {
            if (locale != null) this.locale = locale
            if (pattern != null) this.pattern = pattern
            typeBuilder = {
                AttributeDateTimeType(
                    this.pattern != null,
                    this.pattern ?: "",
                    this.locale != null,
                    this.locale ?: DEFAULT_LOCALE
                )
            }
            return LocalePatternBuilder()
        }

        @Suppress("unused")
        fun Builder.stringList(
            quote: String? = null,
            delimiter: String? = null
        ): QuoteDelimiterBuilder {
            if (quote != null) this.quote = quote
            if (delimiter != null) this.delimiter = delimiter
            typeBuilder = {
                val realQuote = this.quote
                    ?: throw ConfigException("Quote must be configured for list of string")
                val realDelimiter = this.delimiter
                    ?: throw ConfigException("Delimiter must be configured for list of string")
                AttributeStringListType(realQuote, realDelimiter)
            }
            return QuoteDelimiterBuilder()
        }

        @Suppress("unused")
        fun Builder.boolList(quote: String? = null, delimiter: String? = null): QuoteDelimiterBuilder {
            if (quote != null) this.quote = quote
            if (delimiter != null) this.delimiter = delimiter
            typeBuilder = {
                val realQuote = this.quote
                    ?: throw ConfigException("Quote must be configured for list of bool")
                val realDelimiter = this.delimiter
                    ?: throw ConfigException("Delimiter must be configured for list of bool")
                AttributeBoolListType(realQuote, realDelimiter)
            }
            return QuoteDelimiterBuilder()
        }

        @Suppress("unused")
        fun Builder.longList(
            quote: String? = null,
            delimiter: String? = null,
            locale: String? = null
        ): QuoteDelimiterLocaleBuilder {
            if (quote != null) this.quote = quote
            if (delimiter != null) this.delimiter = delimiter
            if (locale != null) this.locale = locale
            typeBuilder = {
                val realQuote = this.quote
                    ?: throw ConfigException("Quote must be configured for list of int")
                val realDelimiter = this.delimiter
                    ?: throw ConfigException("Delimiter must be configured for list of int")
                val realLocale = this.locale ?: DEFAULT_LOCALE
                AttributeIntListType(
                    realQuote,
                    realDelimiter,
                    this.locale != null,
                    realLocale
                )
            }
            return QuoteDelimiterLocaleBuilder()
        }

        @Suppress("unused")
        fun Builder.doubleList(
            quote: String? = null,
            delimiter: String? = null,
            locale: String? = null
        ): QuoteDelimiterLocaleBuilder {
            if (quote != null) this.quote = quote
            if (delimiter != null) this.delimiter = delimiter
            if (locale != null) this.locale = locale
            typeBuilder = {
                val realQuote = this.quote
                    ?: throw ConfigException("Quote must be configured for list of double")
                val realDelimiter = this.delimiter
                    ?: throw ConfigException("Delimiter must be configured for list of double")
                val realLocale = this.locale ?: DEFAULT_LOCALE
                AttributeDoubleListType(
                    realQuote,
                    realDelimiter,
                    this.locale != null,
                    realLocale
                )
            }
            return QuoteDelimiterLocaleBuilder()
        }

        @Suppress("unused")
        fun Builder.dateList(
            quote: String? = null,
            delimiter: String? = null,
            pattern: String? = null,
            locale: String? = null
        ): QuoteDelimiterLocalePatternBuilder {
            if (quote != null) this.quote = quote
            if (delimiter != null) this.delimiter = delimiter
            if (pattern != null) this.pattern = pattern
            if (locale != null) this.locale = locale
            typeBuilder = {
                val realQuote = this.quote
                    ?: throw ConfigException("Quote must be configured for list of date")
                val realDelimiter = this.delimiter
                    ?: throw ConfigException("Delimiter must be configured for list of date")
                val realPattern = this.pattern ?: ""
                val realLocale = this.locale ?: DEFAULT_LOCALE
                AttributeDateListType(
                    realQuote,
                    realDelimiter,
                    this.pattern != null,
                    realPattern,
                    this.locale != null,
                    realLocale
                )
            }
            return QuoteDelimiterLocalePatternBuilder()
        }

        @Suppress("unused")
        fun Builder.datetimeList(
            quote: String? = null,
            delimiter: String? = null,
            pattern: String? = null,
            locale: String? = null
        ): QuoteDelimiterLocalePatternBuilder {
            if (quote != null) this.quote = quote
            if (delimiter != null) this.delimiter = delimiter
            if (pattern != null) this.pattern = pattern
            if (locale != null) this.locale = locale
            typeBuilder = {
                val realQuote = this.quote
                    ?: throw ConfigException("Quote must be configured for list of datetime")
                val realDelimiter = this.delimiter
                    ?: throw ConfigException("Delimiter must be configured for list of datetime")
                val realPattern = this.pattern ?: ""
                val realLocale = this.locale ?: DEFAULT_LOCALE
                AttributeDateTimeListType(
                    realQuote,
                    realDelimiter,
                    this.pattern != null,
                    realPattern,
                    this.locale != null,
                    realLocale
                )
            }
            return QuoteDelimiterLocalePatternBuilder()
        }

        @Suppress("unused")
        fun Builder.attributeList(
            quote: String? = null,
            delimiter: String? = null,
            valueQuote: String? = null,
            valueDelimiter: String? = null
        ): AttributeListTypeBuilder {
            if (quote != null) this.quote = quote
            if (delimiter != null) this.delimiter = delimiter
            if (valueQuote != null) this.valueQuote = valueQuote
            if (valueDelimiter != null) this.valueDelimiter = valueDelimiter
            typeBuilder = {
                val realQuote = this.quote
                    ?: throw ConfigException("Quote must be configured for attribute list")
                val realDelimiter = this.delimiter
                    ?: throw ConfigException("Delimiter must be configured for attribute list")
                val realValueQuote = this.valueQuote ?: "\""
                val realValueDelimiter = this.valueDelimiter ?: "="
                AttributeAttributeListType(
                    realQuote,
                    realDelimiter,
                    realValueDelimiter,
                    realValueQuote
                )
            }
            return AttributeListTypeBuilder()
        }

        fun build(): AttributeType {
            validateLocale()
            val type = this.typeBuilder?.invoke() ?: AttributeStringType(
                false,
                DEFAULT_LOCALE
            )
            if (allowedTypes.isNotEmpty() && !allowedTypes.contains(type::class)) {
                throw ConfigException("Type<${type::class.simpleName}> is not allowed in this context")
            }
            return type
        }

        private fun validateLocale() {
            locale?.let {
                val (l, c) = it.split("_")
                try {
                    Locale(l, c)
                    Unit
                } catch (e: Exception) {
                    throw ConfigException("Bad locale format")
                }
            }
        }

        @ConfigDsl
        inner class OnlyLocaleBuilder {
            fun locale(locale: String) {
                this@Builder.locale = locale
            }
        }

        @Suppress("unused")
        @ConfigDsl
        inner class LocalePatternBuilder {
            fun locale(locale: String): LocalePatternBuilder {
                this@Builder.locale = locale
                return this
            }

            fun pattern(pattern: String): LocalePatternBuilder {
                this@Builder.pattern = pattern
                return this
            }
        }

        @ConfigDsl
        inner class QuoteDelimiterBuilder {
            fun quote(quote: String): QuoteDelimiterBuilder {
                this@Builder.quote = quote
                return this
            }

            fun delimiter(delimiter: String): QuoteDelimiterBuilder {
                this@Builder.delimiter = delimiter
                return this
            }
        }

        @ConfigDsl
        inner class QuoteDelimiterLocaleBuilder {
            fun locale(locale: String): QuoteDelimiterLocaleBuilder {
                this@Builder.locale = locale
                return this
            }

            fun quote(quote: String): QuoteDelimiterLocaleBuilder {
                this@Builder.quote = quote
                return this
            }

            fun delimiter(delimiter: String): QuoteDelimiterLocaleBuilder {
                this@Builder.delimiter = delimiter
                return this
            }
        }

        @Suppress("unused")
        @ConfigDsl
        inner class QuoteDelimiterLocalePatternBuilder {
            fun locale(locale: String): QuoteDelimiterLocalePatternBuilder {
                this@Builder.locale = locale
                return this
            }

            fun pattern(pattern: String): QuoteDelimiterLocalePatternBuilder {
                this@Builder.pattern = pattern
                return this
            }

            fun quote(quote: String): QuoteDelimiterLocalePatternBuilder {
                this@Builder.quote = quote
                return this
            }

            fun delimiter(delimiter: String): QuoteDelimiterLocalePatternBuilder {
                this@Builder.delimiter = delimiter
                return this
            }
        }

        @ConfigDsl
        inner class AttributeListTypeBuilder {
            fun quote(quote: String): AttributeListTypeBuilder {
                this@Builder.quote = quote
                return this
            }

            fun delimiter(delimiter: String): AttributeListTypeBuilder {
                this@Builder.delimiter = delimiter
                return this
            }

            fun valueQuote(quote: String): AttributeListTypeBuilder {
                this@Builder.valueQuote = quote
                return this
            }

            fun valueDelimiter(delimiter: String): AttributeListTypeBuilder {
                this@Builder.valueDelimiter = delimiter
                return this
            }
        }
    }

}

class AttributeStringType(
    hasLocale: Boolean,
    override val locale: String = DEFAULT_LOCALE
) : AttributeType(false, hasLocale, false, false)

class AttributeLongType(
    hasLocale: Boolean,
    override val locale: String = DEFAULT_LOCALE
) : AttributeType(false, hasLocale, false, false)

class AttributeDoubleType(
    hasLocale: Boolean,
    override val locale: String = DEFAULT_LOCALE
) : AttributeType(false, hasLocale, false, false)

class AttributeBoolType
    : AttributeType(false, false, false, false)

class AttributeTextType(
    hasLocale: Boolean,
    override val locale: String = DEFAULT_LOCALE
) : AttributeType(false, hasLocale, false, false)

class AttributeDateType(
    hasPattern: Boolean,
    override val pattern: String,
    hasLocale: Boolean,
    override val locale: String = DEFAULT_LOCALE
) : AttributeType(false, hasLocale, hasPattern, false)

class AttributeDateTimeType(
    hasPattern: Boolean,
    override val pattern: String,
    hasLocale: Boolean,
    override val locale: String = DEFAULT_LOCALE
) : AttributeType(false, hasLocale, hasPattern, false)

class AttributeStringListType(
    override val quote: String,
    override val delimiter: String
) : AttributeType(true, false, false, false)

class AttributeBoolListType(
    override val quote: String,
    override val delimiter: String
) : AttributeType(true, false, false, false)

class AttributeIntListType(
    override val quote: String,
    override val delimiter: String,
    hasLocale: Boolean,
    override val locale: String = DEFAULT_LOCALE
) : AttributeType(true, hasLocale, false, false)

class AttributeDoubleListType(
    override val quote: String,
    override val delimiter: String,
    hasLocale: Boolean,
    override val locale: String = DEFAULT_LOCALE
) : AttributeType(true, hasLocale, false, false)

class AttributeDateListType(
    override val quote: String,
    override val delimiter: String,
    hasPattern: Boolean,
    override val pattern: String,
    hasLocale: Boolean,
    override val locale: String = DEFAULT_LOCALE
) : AttributeType(true, hasLocale, hasPattern, false)

class AttributeDateTimeListType(
    override val quote: String,
    override val delimiter: String,
    hasPattern: Boolean,
    override val pattern: String,
    hasLocale: Boolean,
    override val locale: String = DEFAULT_LOCALE
) : AttributeType(true, hasLocale, hasPattern, false)

class AttributeAttributeListType(
    override val quote: String,
    override val delimiter: String,
    override val valueDelimiter: String,
    override val valueQuote: String
) : AttributeType(false, false, false, true)
