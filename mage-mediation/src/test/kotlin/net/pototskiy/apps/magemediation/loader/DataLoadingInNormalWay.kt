package net.pototskiy.apps.magemediation.loader

import org.amshove.kluent.shouldEqualTo
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

object DataLoadingInNormalWay: Spek({
    Feature("Loading data as in production mode") {
        Scenario("Loading data in production mode") {
            val util = LoadingDataTestPrepare()
            val config = util.loadConfiguration(
                "E:/home/alexander/Development/Web/oooast-tools/mage-mediation/config.xml"
            )
            util.initDataBase()
            When("load data like configured") {
                DataLoader.load(config)
            }
            Then("data has been loaded") {
                true shouldEqualTo true
            }
        }
    }
})