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

@file:JvmName("SerializationConstants")

package cn.chuanwise.onebot.lib

const val DEFAULT_HOST = "0.0.0.0"
const val DEFAULT_PATH = "/"
const val DEFAULT_ACCESS_TOKEN = ""
const val DEFAULT_HEARTBEAT_INTERVAL_MILLISECONDS = 5000L
const val DEFAULT_RESPONSE_TIMEOUT = 5000L
const val DEFAULT_RECONNECT_INTERVAL_MILLISECONDS = 5000L
val DEFAULT_MAX_RECONNECT_ATTEMPTS: Int? = null

const val DEFAULT_ALLOW_NON_PREFIX_ACCESS_TOKEN_HEADER = false
const val DEFAULT_IGNORE_PROVIDED_EMPTY_ACCESS_TOKEN = true
const val DEFAULT_IGNORE_CONFIGURED_EMPTY_ACCESS_TOKEN = true
const val DEFAULT_ALLOW_ACCESS_TOKEN_PROVIDED_IF_ABSENT = false
const val DEFAULT_ALLOW_DIFFERENT_ACCESS_TOKEN_IF_ABSENT = false
const val DEFAULT_ALLOW_DIFFERENT_ACCESS_TOKEN_IF_PRESENT = false
const val DEFAULT_ALLOW_MULTI_SAME_ACCESS_TOKEN_PROVIDED = true
const val DEFAULT_ALLOW_FORMAT_ERROR_WHEN_MULTI_ACCESS_TOKEN_PROVIDED = false

const val X_SELF_ID = "X-Self-ID"
const val X_CLIENT_ROLE = "Role"
const val API = "API"
const val EVENT = "Event"
const val UNIVERSAL = "Universal"

const val AUTHORIZATION = "Authorization"
const val BEARER = "Bearer"
const val BEARER_WITH_SPACE = "Bearer "
const val ACCESS_TOKEN = "access_token"

const val TIME = "time"
const val TIMES = "times"
const val SELF_ID = "self_id"
const val POST_TYPE = "post_type"
const val SUB_TYPE = "sub_type"

const val MESSAGE = "message"
const val MESSAGE_TYPE = "message_type"

const val PRIVATE = "private"
const val FRIEND = "friend"
const val USER_ID = "user_id"
const val RAW_MESSAGE = "raw_message"
const val FONT = "font"
const val MESSAGE_ID = "message_id"
const val REAL_ID = "real_id"

const val REJECT_ADD_REQUEST = "reject_add_request"
const val SPECIAL_TITLE = "special_title"

const val SENDER = "sender"
const val NICKNAME = "nickname"
const val AVATAR = "avatar"
const val DAY_COUNT = "day_count"
const val DESCRIPTION = "description"
const val SEX = "sex"
const val AGE = "age"

const val DOMAIN = "domain"
const val COOKIES = "cookies"
const val TOKEN = "token"

const val OUT_FORMAT = "out_format"

const val YES = "yes"
const val DELAY = "delay"
const val CONTEXT = "context"
const val OPERATION = "operation"

const val GROUP = "group"
const val CARD = "card"
const val LEVEL = "level"
const val ROLE = "role"
const val TITLE = "title"
const val AREA = "area"
const val JOIN_TIME = "join_time"
const val LAST_SENT_TIME = "last_sent_time"
const val UNFRIENDLY = "unfriendly"
const val TITLE_EXPIRE_TIME = "title_expire_time"
const val CARD_CHANGEABLE = "card_changeable"

const val CURRENT_TALKATIVE = "current_talkative"
const val TALKATIVE_LIST = "talkative_list"
const val PERFORMER_LIST = "performer_list"
const val LEGEND_LIST = "legend_list"
const val STRONG_NEWBIE_LIST = "strong_newbie_list"
const val EMOTION_LIST = "emotion_list"

const val ANONYMOUS = "anonymous"

const val DELETE = "delete"
const val KICK = "kick"
const val AT_SENDER = "at_sender"
const val BAN = "ban"
const val BAN_DURATION = "ban_duration"

const val META_EVENT = "meta_event"
const val META_EVENT_TYPE = "meta_event_type"
const val LIFECYCLE = "lifecycle"
const val HEARTBEAT = "heartbeat"
const val INTERVAL = "interval"
const val STATUS = "status"
const val RETCODE = "retcode"
const val ECHO = "echo"
const val ACTION = "action"
const val PARAMS = "params"

const val ID = "id"
const val NAME = "name"
const val FLAG = "flag"
const val APPROVE = "approve"
const val REMARK = "remark"

const val REASON = "reason"
const val NO_CACHE = "no_cache"

const val REQUEST = "request"

const val TYPE = "type"
const val TEXT = "text"
const val FACE = "face"
const val IMAGE = "image"
const val FLASH = "flash"
const val RECORD = "record"
const val URL = "url"
const val CACHE = "cache"
const val PROXY = "proxy"
const val TIMEOUT = "timeout"
const val VIDEO = "video"
const val AT = "at"
const val QQ = "qq"
const val RPS = "rps"
const val DICE = "dice"
const val SHAKE = "shake"
const val POKE = "poke"
const val SHARE = "share"
const val CONTENT = "content"
const val CONTACT = "contact"
const val LOCATION = "location"
const val MUSIC = "music"
const val LAT = "lat"
const val LON = "lon"
const val CUSTOM = "custom"
const val AUDIO = "audio"
const val REPLY = "reply"
const val AUTO_ESCAPE = "auto_escape"
const val FORWARD = "forward"
const val NODE = "node"
const val XML = "xml"
const val JSON = "json"
const val DATA = "data"

const val NOTICE = "notice"

const val NOTICE_TYPE = "notice_type"
const val GROUP_UPLOAD = "group_upload"
const val SIZE = "size"
const val BUSID = "busid"

const val GROUP_ID = "group_id"
const val GROUP_NAME = "group_name"
const val FILE = "file"

const val MEMBER_COUNT = "member_count"
const val MAX_MEMBER_COUNT = "max_member_count"

const val ENABLE = "enable"
const val IS_DISMISS = "is_dismiss"

const val GROUP_ADMIN = "group_admin"
const val GROUP_DECREASE = "group_decrease"
const val GROUP_INCREASE = "group_increase"
const val OPERATOR_ID = "operator_id"

const val GROUP_BAN = "group_ban"
const val DURATION = "duration"

const val FRIEND_ADD = "friend_add"
const val GROUP_RECALL = "group_recall"
const val FRIEND_RECALL = "friend_recall"
const val GROUP_POKE = "poke"
const val TARGET_ID = "target_id"
const val LUCKY_KING = "lucky_king"
const val HONOR = "honor"
const val HONOR_TYPE = "honor_type"

const val REQUEST_TYPE = "request_type"
const val COMMENT = "comment"