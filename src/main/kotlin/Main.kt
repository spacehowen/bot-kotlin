package org.example

import com.github.ehsannarmani.bot.Bot
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

@Serializable
data class BinApiResponse(
    val Status: String,
    val Scheme: String,
    val Type: String,
    val Issuer: String,
    val CardTier: String,
    val Country: CountryInfo,
    val Luhn: Boolean
)

@Serializable
data class CountryInfo(
    val A2: String,
    val A3: String,
    val N3: String,
    val ISD: String,
    val Name: String,
    val Cont: String
)

suspend fun getBinInfo(bin: String): BinApiResponse? {
    val client = HttpClient(CIO)
    return try {
        val response: String = client.get("https://data.handyapi.com/bin/$bin").bodyAsText()
        println("API Response: $response")
        Json.decodeFromString<BinApiResponse>(response) // Aquí se deserializa
    } catch (e: Exception) {
        println("Error fetching BIN info: ${e.message}")
        null
    } finally {
        client.close()
    }
}



fun main() {
    val bot = Bot(
        token = "7752934565:AAFUY1lJe5fcz9sLvwtdqhtbIZeDcPErjPw",
        onUpdate = {
            onText { userInput ->
                val bin = userInput.trim()
                if (bin.length == 6 && bin.all { it.isDigit() }) {
                    reply("Consultando información del BIN $bin...")
                    runCatching {
                        val binInfo = getBinInfo(bin)
                        if (binInfo != null && binInfo.Status == "SUCCESS") {
                            reply(
                                """
                                **Información del BIN $bin**
                                - Esquema: ${binInfo.Scheme}
                                - Tipo: ${binInfo.Type}
                                - Emisor: ${binInfo.Issuer}
                                - Nivel de tarjeta: ${binInfo.CardTier}
                                - País: ${binInfo.Country.Name} (${binInfo.Country.A2})
                                - Luhn válido: ${if (binInfo.Luhn) "Sí" else "No"}
                                """.trimIndent()
                            )
                        } else {
                            reply("No se pudo obtener información para el BIN $bin.")
                        }
                    }.onFailure {
                        reply("Ocurrió un error al consultar el BIN: ${it.message}")
                    }
                } else {
                    reply("Por favor, ingresa un BIN válido (6 dígitos).")
                }
            }
        }
    )
    bot.startPolling()
}