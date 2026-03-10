package com.watermelonkode.simpletemplate.data.service

import com.diamondedge.logging.KmLog
import com.diamondedge.logging.logging
import com.watermelonkode.simpletemplate.domain.service.LoggingService

/**
 * Implementation of the LoggingService interface that uses DiamondEdge's KmLog
 * loggers to handle logging operations.
 *
 * This class provides methods to log informational, debug, and error messages with a tag-based system.
 * It maintains a map of loggers identified by tags. If a logger does not exist for a specified tag,
 * it is created and added to the mapping.
 *
 * Methods:
 * - `info`: Logs an informational message for a specific tag.
 * - `debug`: Logs a debug message for a specific tag.
 * - `error`: Logs an error message for a specific tag, with optional exception information.
 *
 * The class is designed to efficiently fetch or create loggers and delegate the corresponding
 * logging tasks, ensuring proper formatting and contextual message handling.
 */
class DiamondEdgeLoggingServiceImpl : LoggingService {

    private val loggers = mutableMapOf<String, KmLog>()

    override fun info(tag: String, msg: () -> Any?) {
        getLogger(tag).info {
            msg().toString()
        }
    }

    override fun debug(tag: String, msg: () -> Any?) {
        getLogger(tag).debug {
            msg().toString()
        }
    }

    override fun error(tag: String, exception: Any?, msg: () -> Any?) {
        when {
            exception is Throwable -> getLogger(tag).error(exception) { msg().toString() }
            exception != null -> getLogger(tag).error { "$exception : ${msg().toString()}" }
            else -> getLogger(tag).error { msg().toString() }
        }
    }

    private fun getLogger(tag: String): KmLog = loggers.getOrPut(tag) { logging(tag) }

}