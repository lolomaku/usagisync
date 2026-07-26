package org.kotatsu.plugins

import io.ktor.server.application.*
import org.flywaydb.core.Flyway

fun Application.runMigrations() {
    val name = environment.config.property("database.name").getString()
    val host = environment.config.property("database.host").getString()
    val port = environment.config.property("database.port").getString()
    val dialect = environment.config.property("database.dialect").getString()
    val user = environment.config.property("database.user").getString()
    val password = environment.config.property("database.password").getString()

    // Add SSL parameters explicitly here for Flyway
    val url = "jdbc:$dialect://$host:$port/$name?sslMode=REQUIRED&useSSL=true&allowPublicKeyRetrieval=true"

    val flyway = Flyway.configure()
        .dataSource(url, user, password)
        .load()

    flyway.migrate()
}
