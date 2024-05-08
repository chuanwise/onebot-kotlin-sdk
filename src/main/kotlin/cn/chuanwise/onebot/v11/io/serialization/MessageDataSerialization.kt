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

package cn.chuanwise.onebot.v11.io.serialization

import cn.chuanwise.onebot.serialization.Array
import cn.chuanwise.onebot.serialization.Element
import cn.chuanwise.onebot.serialization.Tree
import cn.chuanwise.onebot.serialization.primitive
import cn.chuanwise.onebot.serialization.tree
import cn.chuanwise.onebot.v11.io.data.ArrayMessageData
import cn.chuanwise.onebot.v11.io.data.AtData
import cn.chuanwise.onebot.v11.io.data.CustomMusicRecommendationData
import cn.chuanwise.onebot.v11.io.data.EmptyData
import cn.chuanwise.onebot.v11.io.data.IDTag
import cn.chuanwise.onebot.v11.io.data.ImageData
import cn.chuanwise.onebot.v11.io.data.LocationData
import cn.chuanwise.onebot.v11.io.data.MessageData
import cn.chuanwise.onebot.v11.io.data.PokeData
import cn.chuanwise.onebot.v11.io.data.RecommendationData
import cn.chuanwise.onebot.v11.io.data.SegmentData
import cn.chuanwise.onebot.v11.io.data.SerializedData
import cn.chuanwise.onebot.v11.io.data.ShareData
import cn.chuanwise.onebot.v11.io.data.SingleForwardNodeData
import cn.chuanwise.onebot.v11.io.data.SingleMessageData
import cn.chuanwise.onebot.v11.io.data.SoundData
import cn.chuanwise.onebot.v11.io.data.TextData
import cn.chuanwise.onebot.v11.io.data.VideoData

@Suppress("UNCHECKED_CAST")
fun Element.toMessage(): MessageData = when (this) {
    is Array -> ArrayMessageData(map { it.tree.toMessage() } as List<SingleMessageData>)
    is Tree -> {
        val type = get(TYPE).primitive.string
        SingleMessageData(
            type = type,
            data = get(DATA).tree.toSegmentData(type)
        )
    }
    else -> throw IllegalArgumentException("Unexpected message data: $this")
}


private fun Tree.toSegmentData(type: String): SegmentData = when (type) {
    TEXT -> TextData(
        text = get(TEXT).primitive.string
    )
    IMAGE -> ImageData(
        file = get(FILE).primitive.string,
        type = getOptionalNullableButIgnoreNull(TYPE)?.primitive?.string,
        cache = getOptionalNullableButIgnoreNull(CACHE)?.primitive?.boolOrNull,
        proxy = getOptionalNullableButIgnoreNull(PROXY)?.primitive?.boolOrNull,
        timeout = getOptionalNullableButIgnoreNull(TIMEOUT)?.primitive?.longOrNull,
        url = getOptionalNullableButIgnoreNull(URL)?.primitive?.string
    )
    RECORD -> SoundData(
        file = get(FILE).primitive.string,
        magic = getOptionalNullableButIgnoreNull(TYPE)?.primitive?.intToBoolOrNull,
        cache = getOptionalNullableButIgnoreNull(CACHE)?.primitive?.boolOrNull,
        proxy = getOptionalNullableButIgnoreNull(PROXY)?.primitive?.boolOrNull,
        timeout = getOptionalNullableButIgnoreNull(TIMEOUT)?.primitive?.longOrNull,
        url = getOptionalNullableButIgnoreNull(URL)?.primitive?.string
    )
    VIDEO -> VideoData(
        file = get(FILE).primitive.string,
        cache = getOptionalNullableButIgnoreNull(CACHE)?.primitive?.boolOrNull,
        proxy = getOptionalNullableButIgnoreNull(PROXY)?.primitive?.boolOrNull,
        timeout = getOptionalNullableButIgnoreNull(TIMEOUT)?.primitive?.longOrNull,
        url = getOptionalNullableButIgnoreNull(URL)?.primitive?.string
    )
    AT -> AtData(
        qq = get(QQ).primitive.string
    )
    RPS, DICE, SHAKE, ANONYMOUS -> EmptyData()
    POKE -> PokeData(
        id = get(ID).primitive.string,
        type = get(TYPE).primitive.string,
        name = getOptionalNullableButIgnoreNull(NAME)?.primitive?.string
    )
    SHARE -> ShareData(
        url = get(URL).primitive.string,
        title = get(TITLE).primitive.string,
        content = getOptionalNullableButIgnoreNull(CONTENT)?.primitive?.string,
        image = get(IMAGE).primitive.string
    )
    CONTACT, GROUP -> RecommendationData(
        id = get(ID).primitive.string,
        type = get(NAME).primitive.string,
    )
    LOCATION -> LocationData(
        lat = get(LAT).primitive.string,
        lon = get(LON).primitive.string,
        title = getOptionalNullableButIgnoreNull(TITLE)?.primitive?.string,
        content = getOptionalNullableButIgnoreNull(CONTENT)?.primitive?.string
    )
    MUSIC -> if (get(TYPE).primitive.string == CUSTOM) CustomMusicRecommendationData(
        type = get(ID).primitive.string,
        url = get(NAME).primitive.string,
        audio = get(AUDIO).primitive.string,
        title = get(TITLE).primitive.string,
        image = get(IMAGE).primitive.string,
        content = get(CONTENT).primitive.string
    ) else RecommendationData(
        id = get(ID).primitive.string,
        type = get(NAME).primitive.string,
    )
    REPLY, FORWARD, FACE -> IDTag(
        id = get(ID).primitive.string
    )
    NODE -> if (contains(ID)) IDTag(
        id = get(ID).primitive.string
    ) else SingleForwardNodeData(
        userID = get(USER_ID).primitive.string,
        nickname = get(NICKNAME).primitive.string,
        content = get(CONTENT).toMessage()
    )
    XML, JSON -> SerializedData(
        data = get(DATA).primitive.string
    )
    else -> throw IllegalArgumentException("Unexpected message type: $type")
}