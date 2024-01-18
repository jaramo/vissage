package org.jaramo.vissage

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures.onionArchitecture

@AnalyzeClasses(
    importOptions = [ImportOption.DoNotIncludeTests::class],
)
object ArchitectureTest {

    private const val BASE_PACKAGE: String = "org.jaramo.vissage"

    @ArchTest
    val architecture: ArchRule =
        onionArchitecture()
            .domainModels("$BASE_PACKAGE.domain.model..")
            .domainServices("$BASE_PACKAGE.domain.service..") // adapter interfaces
            .applicationServices("$BASE_PACKAGE.application..") // business logic
            .adapter("api", "$BASE_PACKAGE.adapter.api..") // controllers and dto
            .adapter("event", "$BASE_PACKAGE.adapter.event..") // kafka connectors
            .adapter("persistence", "$BASE_PACKAGE.adapter.persistence..") // db connectors
//            .adapter("configuration", "$BASE_PACKAGE.configuration..")
}