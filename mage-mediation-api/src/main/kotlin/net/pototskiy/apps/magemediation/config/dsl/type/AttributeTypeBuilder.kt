package net.pototskiy.apps.magemediation.config.dsl.type

import net.pototskiy.apps.magemediation.DEFAULT_LOCALE
import net.pototskiy.apps.magemediation.config.ConfigException
import net.pototskiy.apps.magemediation.config.dsl.ConfigDsl
import net.pototskiy.apps.magemediation.config.newOne.type.*
import java.util.*
import kotlin.reflect.KClass

@ConfigDsl
class AttributeTypeBuilder(private val allowedTypes: List<KClass<out AttributeType>> = listOf()) {
    private var locale: String? = null
    private var pattern: String? = null
    private var quote: String? = null
    private var delimiter: String? = null
    private var valueQuote: String? = null
    private var valueDelimiter: String? = null

    private var typeBuilder: (() -> AttributeType)? = null

    @Suppress("unused")
    fun AttributeTypeBuilder.bool() {
        typeBuilder = { AttributeBoolType() }
    }

    @Suppress("unused")
    fun AttributeTypeBuilder.string(locale: String? = null): OnlyLocaleBuilder {
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
    fun AttributeTypeBuilder.text(locale: String? = null): OnlyLocaleBuilder {
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
    fun AttributeTypeBuilder.int(locale: String? = null): OnlyLocaleBuilder {
        if (locale != null) this.locale = locale
        typeBuilder = {
            AttributeIntType(
                this.locale != null,
                this.locale ?: DEFAULT_LOCALE
            )
        }
        return OnlyLocaleBuilder()
    }

    @Suppress("unused")
    fun AttributeTypeBuilder.double(locale: String? = null): OnlyLocaleBuilder {
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
    fun AttributeTypeBuilder.date(locale: String? = null, pattern: String? = null): LocalePatternBuilder {
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
    fun AttributeTypeBuilder.datetime(locale: String? = null, pattern: String? = null): LocalePatternBuilder {
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
    fun AttributeTypeBuilder.stringList(quote: String? = null, delimiter: String? = null): QuoteDelimiterBuilder {
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
    fun AttributeTypeBuilder.boolList(quote: String? = null, delimiter: String? = null): QuoteDelimiterBuilder {
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
    fun AttributeTypeBuilder.intList(
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
    fun AttributeTypeBuilder.doubleList(
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
    fun AttributeTypeBuilder.dateList(
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
    fun AttributeTypeBuilder.datetimeList(
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
    fun AttributeTypeBuilder.attributeList(
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
                ?: throw ConfigException("Quote must be configuraed for attribute list")
            val realDelimiter = this.delimiter
                ?: throw ConfigException("Delimiter must be configuraed for attribute list")
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
        val type = this.typeBuilder?.invoke() ?: AttributeStringType(false, DEFAULT_LOCALE)
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
            this@AttributeTypeBuilder.locale = locale
        }
    }

    @ConfigDsl
    inner class LocalePatternBuilder {
        fun locale(locale: String): LocalePatternBuilder {
            this@AttributeTypeBuilder.locale = locale
            return this
        }

        fun pattern(pattern: String): LocalePatternBuilder {
            this@AttributeTypeBuilder.pattern = pattern
            return this
        }
    }

    @ConfigDsl
    inner class QuoteDelimiterBuilder {
        fun quote(quote: String): QuoteDelimiterBuilder {
            this@AttributeTypeBuilder.quote = quote
            return this
        }

        fun delimiter(delimiter: String): QuoteDelimiterBuilder {
            this@AttributeTypeBuilder.delimiter = delimiter
            return this
        }
    }

    @ConfigDsl
    inner class QuoteDelimiterLocaleBuilder {
        fun locale(locale: String): QuoteDelimiterLocaleBuilder {
            this@AttributeTypeBuilder.locale = locale
            return this
        }

        fun quote(quote: String): QuoteDelimiterLocaleBuilder {
            this@AttributeTypeBuilder.quote = quote
            return this
        }

        fun delimiter(delimiter: String): QuoteDelimiterLocaleBuilder {
            this@AttributeTypeBuilder.delimiter = delimiter
            return this
        }
    }

    @ConfigDsl
    inner class QuoteDelimiterLocalePatternBuilder {
        fun locale(locale: String): QuoteDelimiterLocalePatternBuilder {
            this@AttributeTypeBuilder.locale = locale
            return this
        }

        fun pattern(pattern: String): QuoteDelimiterLocalePatternBuilder {
            this@AttributeTypeBuilder.pattern = pattern
            return this
        }

        fun quote(quote: String): QuoteDelimiterLocalePatternBuilder {
            this@AttributeTypeBuilder.quote = quote
            return this
        }

        fun delimiter(delimiter: String): QuoteDelimiterLocalePatternBuilder {
            this@AttributeTypeBuilder.delimiter = delimiter
            return this
        }
    }

    @ConfigDsl
    inner class AttributeListTypeBuilder {
        fun quote(quote: String): AttributeListTypeBuilder {
            this@AttributeTypeBuilder.quote = quote
            return this
        }

        fun delimiter(delimiter: String): AttributeListTypeBuilder {
            this@AttributeTypeBuilder.delimiter = delimiter
            return this
        }

        fun valueQuote(quote: String): AttributeListTypeBuilder {
            this@AttributeTypeBuilder.valueQuote = quote
            return this
        }

        fun valueDelimiter(delimiter: String): AttributeListTypeBuilder {
            this@AttributeTypeBuilder.valueDelimiter = delimiter
            return this
        }
    }
}
