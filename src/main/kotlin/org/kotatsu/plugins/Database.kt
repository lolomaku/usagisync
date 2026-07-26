package org.kotatsu.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.kotatsu.database
import org.kotatsu.hikariDataSource
import org.ktorm.database.Database

fun Application.configureDatabase() {
    val name = environment.config.property("database.name").getString()
    val host = environment.config.property("database.host").getString()
    val port = environment.config.property("database.port").getString()
    val dialect = environment.config.property("database.dialect").getString()

    val isMaria = dialect.lowercase() == "mariadb"
    val jdbcDriver = if (isMaria) "org.mariadb.jdbc.Driver" else "com.mysql.cj.jdbc.Driver"

    // MariaDB driver and MySQL driver expect slightly different SSL query params
    val sslParams = if (isMaria) {
        "?useSSL=true&trustServerCertificate=true"
    } else {
        "?sslMode=REQUIRED&useSSL=true&allowPublicKeyRetrieval=true"
    }

    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:$dialect://$host:$port/$name$sslParams"
        username = environment.config.property("database.user").getString()
        password = environment.config.property("database.password").getString()
        driverClassName = jdbcDriver
        
        // Explicit properties passed to Hikari
        addDataSourceProperty("useSSL", "true")
        addDataSourceProperty("trustServerCertificate", "true")
    }

    hikariDataSource = HikariDataSource(config)
    database = Database.connect(hikariDataSource)
}
