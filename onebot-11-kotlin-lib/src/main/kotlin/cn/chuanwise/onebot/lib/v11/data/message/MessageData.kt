/*
 * Copyright 2024 Chuanwise and contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.chuanwise.onebot.lib.v11.data.message

import cn.chuanwise.onebot.lib.ANONYMOUS
import cn.chuanwise.onebot.lib.AT
import cn.chuanwise.onebot.lib.AUTO_ESCAPE
import cn.chuanwise.onebot.lib.CONTACT
import cn.chuanwise.onebot.lib.CUSTOM
import cn.chuanwise.onebot.lib.DATA
import cn.chuanwise.onebot.lib.DICE
import cn.chuanwise.onebot.lib.FACE
import cn.chuanwise.onebot.lib.FORWARD
import cn.chuanwise.onebot.lib.GROUP
import cn.chuanwise.onebot.lib.ID
import cn.chuanwise.onebot.lib.IMAGE
import cn.chuanwise.onebot.lib.JSON
import cn.chuanwise.onebot.lib.LOCATION
import cn.chuanwise.onebot.lib.MUSIC
import cn.chuanwise.onebot.lib.NODE
import cn.chuanwise.onebot.lib.POKE
import cn.chuanwise.onebot.lib.RECORD
import cn.chuanwise.onebot.lib.REPLY
import cn.chuanwise.onebot.lib.RPS
import cn.chuanwise.onebot.lib.SHAKE
import cn.chuanwise.onebot.lib.SHARE
import cn.chuanwise.onebot.lib.TEXT
import cn.chuanwise.onebot.lib.TYPE
import cn.chuanwise.onebot.lib.VIDEO
import cn.chuanwise.onebot.lib.XML
import cn.chuanwise.onebot.lib.deserializeTo
import cn.chuanwise.onebot.lib.getNotNull
import cn.chuanwise.onebot.lib.getOptionalNotNull
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.ser.std.StdSerializer


/**
 * Message low-level representation based on
 * [OneBot 11 Message Data](https://github.com/botuniverse/onebot-11/blob/master/message/segment.md).
 *
 * @author Chuanwise
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonDeserialize(using = MessageDataDeserializer::class)
sealed class MessageData

@JsonSerialize(using = CQCodeMessageDataSerializer::class)
@JsonDeserialize(using = CQCodeMessageDataDeserializer::class)
data class CQCodeMessageData(
    val code: String,
    val autoEscape: Boolean = false
) : MessageData()

object CQCodeMessageDataDeserializer : StdDeserializer<CQCodeMessageData>(CQCodeMessageData::class.java) {
    private fun readResolve(): Any = CQCodeMessageDataDeserializer
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): CQCodeMessageData {
        val node = p.codec.readTree<JsonNode>(p)
        return if (node is TextNode) {
            CQCodeMessageData(p.readValueAs(String::class.java))
        } else if (node is ObjectNode) {
            CQCodeMessageData(
                code = node.getNotNull(DATA).asText(),
                autoEscape = node.getNotNull(AUTO_ESCAPE).asBoolean()
            )
        } else {
            throw IllegalArgumentException("Unexpected CQCode message data: $node")
        }
    }
}

object CQCodeMessageDataSerializer : StdSerializer<CQCodeMessageData>(CQCodeMessageData::class.java) {
    private fun readResolve(): Any = CQCodeMessageDataSerializer
    override fun serialize(value: CQCodeMessageData, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(value.code)
    }
}

data class SingleMessageData(
    val type: String,
    val data: SegmentData
) : MessageData()

@JsonSerialize(using = ArrayMessageDataSerializer::class)
data class ArrayMessageData(
    val data: List<SingleMessageData>
): MessageData()

object ArrayMessageDataSerializer : StdSerializer<ArrayMessageData>(ArrayMessageData::class.java) {
    private fun readResolve(): Any = ArrayMessageDataSerializer
    override fun serialize(value: ArrayMessageData?, gen: JsonGenerator?, provider: SerializerProvider?) {
        gen?.writeStartArray()
        value?.data?.forEach {
            gen?.writeObject(it)
        }
        gen?.writeEndArray()
    }
}

sealed class SegmentData

data class TextData(
    val text: String
) : SegmentData()


data class ImageData(
    // receive or send
    // if sent, it can be path to image, Net URL, file URL and base64 encoded content.
    val file: String,

    // "flash" or none
    val type: String?,

    // send only
    val url: String?,

    // receive only
    val cache: Int?,

    // receive only
    val proxy: Int?,

    // receive only
    val timeout: Long?
) : SegmentData()

data class RecordData(
    val file: String,
    val magic: Int?,

    // send only
    val url: String?,

    // receive only
    val cache: Int?,

    // receive only
    val proxy: Int?,

    // receive only
    val timeout: Long?
) : SegmentData()

data class VideoData(
    val file: String,

    // send only
    val url: String?,

    // receive only
    val cache: Int?,

    // receive only
    val proxy: Int?,

    // receive only
    val timeout: Long?
) : SegmentData()

data class AtData(
    // qq code or "all"
    val qq: String
) : SegmentData()

// RPSData (Rock, Paper, Scissors)
// DiceData, ShakeData has empty body
data object EmptyData : SegmentData()

data class PokeData(
    // https://github.com/mamoe/mirai/blob/f5eefae7ecee84d18a66afce3f89b89fe1584b78/mirai-core/src/commonMain/kotlin/net.mamoe.mirai/message/data/HummerMessage.kt#L49
    val type: String,
    val id: String,
    val name: String?
) : SegmentData()

class AnonymousSendingTag private constructor(
    val ignore: Int
) : SegmentData() {
    companion object {
        val TRUE = AnonymousSendingTag(1)
        val FALSE = AnonymousSendingTag(0)

        fun of(value: Boolean) = if (value) TRUE else FALSE
    }
}


data class ShareData(
    val url: String,
    val title: String,

    // optional if send
    val content: String?,

    // optional if send
    // image url
    val image: String?
) : SegmentData()


data class RecommendationData(
    // "qq", "group"
    // "qq", "163", "xm"
    val type: String,
    val id: String,
) : SegmentData()


data class LocationData(
    val lat: String,
    val lon: String,

    // optional if send
    val title: String?,

    // optional if send
    val content: String?
) : SegmentData()


data class CustomMusicRecommendationData(
    // "custom"
    val type: String?,

    // jump url
    val url: String?,

    // audio url
    val audio: String,

    // title url
    val title: String,
    val content: String,

    // cover url
    val image: String,
) : SegmentData()


data class IDTag(
    val id: String
) : SegmentData()


data class SingleForwardNodeData(
    val userID: String,
    val nickname: String,
    val content: MessageData
) : SegmentData()


data class MultiForwardNodeData(
    val id: String,
    val content: List<SegmentData>
) : SegmentData()


data class SerializedData(
    // xml or json
    val data: String
) : SegmentData()

object MessageDataDeserializer : StdDeserializer<MessageData>(MessageData::class.java) {
    private fun readResolve(): Any = MessageDataDeserializer
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): MessageData {
        val mapper = p.codec as ObjectMapper
        return when (val node = p.codec.readTree<JsonNode>(p)) {
            is ArrayNode -> ArrayMessageData(
                data = node.map {
                    p.readValueAs(SingleMessageData::class.java)
                }
            )
            is ObjectNode -> {
                val type = node.getNotNull(TYPE).asText()
                SingleMessageData(
                    type = type,
                    data = when (type) {
                        TEXT -> node.deserializeTo<TextData>(mapper)
                        IMAGE -> node.deserializeTo<ImageData>(mapper)
                        RECORD -> node.deserializeTo<RecordData>(mapper)
                        VIDEO -> node.deserializeTo<VideoData>(mapper)
                        AT -> node.deserializeTo<AtData>(mapper)
                        RPS, DICE, SHAKE, ANONYMOUS -> EmptyData
                        POKE -> node.deserializeTo<PokeData>(mapper)
                        SHARE -> node.deserializeTo<ShareData>(mapper)
                        CONTACT, GROUP -> node.deserializeTo<RecommendationData>(mapper)
                        LOCATION -> node.deserializeTo<LocationData>(mapper)
                        MUSIC -> if (node.getOptionalNotNull(TYPE).asText() == CUSTOM) {
                            node.deserializeTo<CustomMusicRecommendationData>(mapper)
                        } else {
                            node.deserializeTo<RecommendationData>(mapper)
                        }

                        REPLY, FORWARD, FACE -> node.deserializeTo<IDTag>(mapper)
                        NODE -> if (node.has(ID)) {
                            node.deserializeTo<IDTag>(mapper)
                        } else {
                            node.deserializeTo<SingleForwardNodeData>(mapper)
                        }

                        XML, JSON -> node.deserializeTo<SerializedData>(mapper)
                        else -> throw IllegalArgumentException("Unexpected message type: $type")
                    }
                )
            }
            else -> throw IllegalArgumentException("Unexpected message data: $node")
        }
    }
}