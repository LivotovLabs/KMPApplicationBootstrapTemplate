package com.watermelonkode.simpletemplate.domain.service


/**
 * Service interface for logging messages in the application.
 *
 * This service provides methods for logging informational, debugging, and error messages.
 * It is designed to allow developers to log application-specific runtime information
 * with an optional tag for categorization. Error logging additionally supports attaching
 * an exception or error object to provide context.
 *
 * Functions:
 * - `info(tag, msg)`: Logs an informational message with an optional tag.
 * - `debug(tag, msg)`: Logs a debugging message with an optional tag.
 * - `error(tag, exception, msg)`: Logs an error message with an optional tag and exception.
 */
interface LoggingService {

    /**
     * Logs an informational message with an optional tag for categorization.
     *
     * This method is used to log messages that provide informational details
     * about the application's runtime state. It allows specifying a custom
     * tag to categorize or group related logs for easier filtering and debugging.
     *
     * @param tag A string used to categorize the log message. Defaults to "PapuDriverApp".
     * @param msg A lambda that generates the message to be logged. It is evaluated only when logging is performed, which can help minimize unnecessary computation.
     */
    fun info(tag: String, msg: () -> Any?)

    /**
     * Logs a debugging message with an optional tag for categorization.
     *
     * This method is used to log messages that provide detailed debugging
     * information about the application's runtime state. The message is
     * generated lazily using a lambda, which helps avoid unnecessary computations
     * when debugging logs are not enabled.
     *
     * @param tag A string used to categorize the log message. Defaults to "PapuDriverApp".
     * @param msg A lambda that generates the message to be logged. It is evaluated only if the logging occurs.
     */
    fun debug(tag: String, msg: () -> Any?)

    /**
     * Logs an error message with an optional tag and exception for categorization.
     *
     * This method is used to log error messages detailing problematic situations
     * in the application, such as exceptions or unexpected states. It allows
     * specifying a custom tag for categorization and provides an option to include
     * an exception or other relevant context.
     *
     * @param tag A string used to categorize the log message. Defaults to "PapuDriverApp".
     * @param exception An optional parameter that represents the exception or error details to log.
     *                  It can be null if no exception is associated with the error message.
     * @param msg A lambda that generates the error message to be logged. It is evaluated
     *            only if the logging occurs, which helps avoid unnecessary computation.
     */
    fun error(tag: String, exception: Any?, msg: () -> Any?)

}