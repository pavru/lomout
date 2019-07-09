/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

class AdditionalAttributes : Document() {
    var ratings_summary: Double? = null
    var cost: Double? = null
    // todo remove optional in this section
    var english_name: String? = null
    var catalog_sku: String? = null
    var machine: String? = null
    var machine_unit: String? = null
    var machine_vendor: String? = null
    // end of todo block
    var ts_dimensions_length: Double? = null
    var ts_dimensions_height: Double? = null
    var ts_dimensions_width: Double? = null

    companion object : DocumentMetadata(AdditionalAttributes::class)
}

class MageProduct : Document() {
    @Key
    var sku: String = ""
    class AdditionalAttributesReader: ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<DocumentAttributeReader> {
            quote = null; valueQuote = '"'; valueDelimiter = '='
        }
    }
    @Reader(AdditionalAttributesReader::class)
    var additional_attributes: AdditionalAttributes = AdditionalAttributes()
    var description: String = ""
    var short_description: String? = null
    var weight: Double? = null
    var product_online: Boolean = false
    var price: Double = 0.0
    var special_price: Double? = null
    class DateReaderBuilder: ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<DateAttributeReader> {
            pattern = "d.M.uu"
        }
    }
    @Reader(DateReaderBuilder::class)
    var special_price_from_date: LocalDate? = null
    @Reader(DateReaderBuilder::class)
    var special_price_to_date: LocalDate? = null
    class DateTimeReaderBuilder: ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<DateTimeAttributeReader> {
            pattern = "d.M.uu, H:m"
        }
    }
    @Reader(DateTimeReaderBuilder::class)
    var created_at: LocalDateTime = LocalDateTime.now()
    @Reader(DateTimeReaderBuilder::class)
    var updated_at: LocalDateTime = LocalDateTime.now()
    @Reader(DateReaderBuilder::class)
    var new_from_date: LocalDate? = LocalDate.now()
    @Reader(DateReaderBuilder::class)
    var new_to_date: LocalDate? = LocalDate.now()
    var qty: Double = 0.0
    var out_of_stock_qty: Double = 0.0
    var use_config_min_qty: Boolean = false
    var is_qty_decimal: Boolean = false
    var allow_backorders: Boolean = false
    var use_config_backorders: Boolean = false
    var min_cart_qty: Double = 0.0
    var use_config_min_sale_qty: Boolean = false
    var max_cart_qty: Double = 0.0
    var use_config_max_sale_qty: Boolean = false
    var is_in_stock: Boolean = false
    var notify_on_stock_below: Double = 0.0
    var use_config_notify_stock_qty: Boolean = false
    var manage_stock: Boolean = false
    var use_config_manage_stock: Boolean = false
    var use_config_qty_increments: Boolean = false
    var qty_increments: Double = 0.0
    var use_config_enable_qty_inc: Boolean = false
    var enable_qty_increments: Boolean = false
    var is_decimal_divided: Boolean = false
    var website_id: Long = 0L
    class CommaListReaderBuilder: ReaderBuilder {
        override fun build(): AttributeReader<out Any?> = createReader<StringListAttributeReader> {
            delimiter = ','
            quote = null
        }
    }
    @Reader(CommaListReaderBuilder::class)
    var related_skus: List<String>? = null
    @Reader(CommaListReaderBuilder::class)
    var crosssell_skus: List<String>? = null
    @Reader(CommaListReaderBuilder::class)
    var upsell_skus: List<String>? = null
    @Reader(CommaListReaderBuilder::class)
    var additional_images: List<String>? = null
    @Reader(CommaListReaderBuilder::class)
    var additional_image_labels: List<String>? = null
    @Reader(CommaListReaderBuilder::class)
    var associated_skus: List<String>? = null

    companion object : DocumentMetadata(MageProduct::class)
}
