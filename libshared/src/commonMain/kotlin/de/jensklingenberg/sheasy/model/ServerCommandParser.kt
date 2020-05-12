package de.jensklingenberg.sheasy.model

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class ServerCommandParser {
    companion object {


        fun getMoveChar(jsonStr: String): ServerRequest.MoveCharRequest {
            val json =
                Json(JsonConfiguration.Stable)
            return json.parse(ServerRequest.MoveCharRequest.serializer(), jsonStr)

        }
        fun getPlayerRequest(jsonStr: String): ServerRequest.PlayerRequest {
            val json =
                Json(JsonConfiguration.Stable)
            return json.parse(ServerRequest.PlayerRequest.serializer(), jsonStr)

        }


    }
}