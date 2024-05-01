package cn.chuanwise.onebot.v11.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
@Polymorphic
sealed class EventData {
    @SerialName("time")
    abstract val time: Long

    @SerialName("self_id")
    abstract val selfID: Long

    @SerialName("post_type")
    abstract val postType: String

    companion object: KSerializer<EventData> {
        @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
        override val descriptor = buildSerialDescriptor("EventData", SerialKind.CONTEXTUAL) {
            element("time", Long.serializer().descriptor)
            element("self_id", Long.serializer().descriptor)
            element("post_type", String.serializer().descriptor)
        }

        override fun deserialize(decoder: Decoder): EventData {
            TODO("Not yet implemented")
        }

        override fun serialize(encoder: Encoder, value: EventData) {
            TODO("Not yet implemented")
        }
    }
}

