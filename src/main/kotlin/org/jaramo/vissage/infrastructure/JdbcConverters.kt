package org.jaramo.vissage.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.postgresql.util.PGobject
import org.springframework.core.convert.converter.Converter
import org.jaramo.vissage.adapter.persistence.Metadata

class MetadataJsonWriterConverter(private val mapper: ObjectMapper) : Converter<Metadata, PGobject> {
    override fun convert(source: Metadata): PGobject {
        return PGobject().apply {
            type = "jsonb"
            value = mapper.writeValueAsString(source)
        }
    }
}

class MetadataJsonReaderConverter(private val mapper: ObjectMapper) : Converter<PGobject, Metadata> {
    override fun convert(source: PGobject): Metadata? {
        return mapper.readValue(source.value!!)
    }
}
