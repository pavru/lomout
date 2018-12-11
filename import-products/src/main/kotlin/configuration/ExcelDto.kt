package configuration

import com.fasterxml.jackson.annotation.JsonProperty

data class ExcelPositionDto(
    val column: Int,
    @JsonProperty(required = false)
    val regex: String?
)

data class ExcelGroupDto(
    val sheet: String,
    @JsonProperty(value = "skip-rows", required = false)
    val skipRows: Int = 0,
    @JsonProperty(value = "max-age", required = false)
    val maxAge: Int = 8,
    val code: ExcelPositionDto,
    val name: ExcelPositionDto
)

data class ExcelFieldDto(
    val name: String,
    val column: Int,
    @JsonProperty(required = false)
    val regex: String?,
    @JsonProperty(required = false)
    val data: Map<String, String>?
)

data class ExcelFieldSetDto(
    val name: String,
    val default: Boolean = false,
    val fields: List<ExcelFieldDto>
)

data class ExcelDataDto(
    val sheet: String = "stock",
    @JsonProperty(value = "skip-rows")
    val skipRows: Int = 0,
    @JsonProperty(value = "max-age", required = false)
    val maxAge: Int = 8,
    @JsonProperty("field-sets")
    val fieldSets: List<ExcelFieldSetDto>
)

data class ExcelDto(
    val group: ExcelGroupDto,
    val data: ExcelDataDto
)