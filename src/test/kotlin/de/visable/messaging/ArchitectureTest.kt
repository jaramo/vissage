package de.visable.messaging

import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures.onionArchitecture

@AnalyzeClasses(
    importOptions = [ImportOption.DoNotIncludeTests::class],
)
object ArchitectureTest {

    private const val BASE_PACKAGE: String = "de.visable.messaging"
    private const val ADAPTER_PACKAGE = "$BASE_PACKAGE.adapter"

    @ArchTest
    val architecture: ArchRule =
        onionArchitecture()
            .domainModels("$BASE_PACKAGE.domain.model..")
            .domainServices("$BASE_PACKAGE.domain.service..")
            .applicationServices("$BASE_PACKAGE.application..")
            .adapter("api", "$ADAPTER_PACKAGE.api..")
            .adapter("event", "$ADAPTER_PACKAGE.event..")
            .adapter("persistence", "$ADAPTER_PACKAGE.persistence..")
}
