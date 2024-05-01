package cn.chuanwise.onebot.v11.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure

@Serializable
sealed class EventData {
    @SerialName("time")
    abstract val time: Long

    @SerialName("self_id")
    abstract val selfID: Long

    @SerialName("post_type")
    abstract val postType: String

    companion object: KSerializer<EventData> {
        @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
        override val descriptor: SerialDescriptor = buildSerialDescriptor(
            "cn.chuanwise.onebot.v11.data.EventData", SerialKind.CONTEXTUAL
        ) {
            element("time", Long.serializer().descriptor)
            element("self_id", Long.serializer().descriptor)
            element("post_type", String.serializer().descriptor)
        }

        override fun deserialize(decoder: Decoder) = decoder.decodeStructure(descriptor) {
            var time: Long = 0
            var selfID: Long = 0
            var postType: String = ""

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> time = decodeLongElement(descriptor, 0)
                    1 -> selfID = decodeLongElement(descriptor, 1)
                    2 -> postType = decodeStringElement(descriptor, 2)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }

            return@decodeStructure when (postType) {
                "message" -> MessageEventData.serializer().deserialize(decoder)
                "meta_event" -> MetaEventData.serializer().deserialize(decoder)
                "notice" -> NotificationEventData.serializer().deserialize(decoder)
                else -> error("Unknown post type: $postType")
            }
        }

        override fun serialize(encoder: Encoder, value: EventData) {
            encoder.encodeSerializableValue(PolymorphicSerializer(EventData::class), value)
        }
    }
}

