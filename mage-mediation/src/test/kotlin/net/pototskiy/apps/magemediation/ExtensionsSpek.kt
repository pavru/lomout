package net.pototskiy.apps.magemediation

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

object ExtensionsSpek : Spek({
    Feature("Double extension") {
        val number = 3.3
        Scenario("get fraction") {
            var fraction = 0.0
            When("get fraction of $number") {
                fraction = number.fraction
            }
            Then("it should have fraction does not equals 0.0") {
                assertNotEquals(0.0, fraction)
            }
        }
        Scenario("get integer part") {
            var integer: Long = 0
            When("get integer part") {
                integer = number.floorToLong()
            }
            Then("it should have non zero integer part of $number") {
                assertEquals(3, integer)
            }
        }
    }
})