package org.jaramo.vissage.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.jaramo.vissage.infrastructure.MetadataJsonReaderConverter
import org.jaramo.vissage.infrastructure.MetadataJsonWriterConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration

@Configuration
class JdbcConfiguration @Autowired constructor(
    private val objectMapper: ObjectMapper
) : AbstractJdbcConfiguration() {

    override fun userConverters(): List<Any> {
        return listOf(
            MetadataJsonReaderConverter(objectMapper),
            MetadataJsonWriterConverter(objectMapper),
        )
    }
}
