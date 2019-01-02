package com.dingsoft.sdptransform

import com.alibaba.fastjson.TypeReference

/**
 * @author wolfhan
 * @date 12/14/2018
 */
public class SdpTransform {

    private val sdpBridge = SdpBridge()

    public fun parse(sdp: String): SessionDescription {
        return com.alibaba.fastjson.JSON.parseObject(sdpBridge.parse(sdp), SessionDescription::class.java)
    }

    public fun parseParams(params: String): MutableMap<String, Any> {
        val mapType = object : TypeReference<MutableMap<String, Any>>() {}.type
        return com.alibaba.fastjson.JSON.parseObject(sdpBridge.parseParams(params), mapType)
    }

    public fun parsePayloads(payloads: String): MutableList<Int> {
        val payloadStringList = sdpBridge.parsePayloads(payloads).split(" ")
        val payloadIntList = mutableListOf<Int>()
        if (payloadStringList.isNotEmpty()) {
            for (payload in payloadStringList) {
                val parsedPayload = safeToInt(payload)
                if (parsedPayload != null) {
                    payloadIntList.add(parsedPayload)
                }
            }
        }
        return payloadIntList
    }

    private fun safeToInt(value: String): Int? {
        return try {
            value.toInt()
        } catch (e: Exception) {
            null
        }
    }

    public fun parseImageAttributes(params: String): MutableList<MutableMap<String, Any>> {
        val listType = object : TypeReference<MutableList<MutableMap<String, Any>>>() {}.type
        return com.alibaba.fastjson.JSON.parseObject(sdpBridge.parseImageAttributes(params), listType)
    }

    public fun parseSimulcastStreamList(streams: String): MutableList<MutableList<SimulcastStream>> {
        val listType = object : TypeReference<MutableList<MutableList<SimulcastStream>>>() {}.type
        return com.alibaba.fastjson.JSON.parseObject(sdpBridge.parseSimulcastStreamList(streams), listType)
    }

    public fun write(sdp: SessionDescription): String {
        return sdpBridge.write(com.alibaba.fastjson.JSON.toJSONString(sdp))
    }

}

class SdpBridge {

    external fun parse(sdp: String): String

    external fun parseParams(params: String): String

    external fun parsePayloads(payloads: String): String

    external fun parseImageAttributes(params: String): String

    external fun parseSimulcastStreamList(streams: String): String

    external fun write(sdp: String): String

    companion object {

        init {
            System.loadLibrary("sdptransform-android")
        }
    }
}

public data class SimulcastStream(
    var scid: Any,// Int | String
    var paused: Boolean
)

/**
 * Descriptor fields that exist only at the session level (before an m= block).
 *
 * See the SDP grammar for more details: https://tools.ietf.org/html/rfc4566#section-9
 */
public class SessionDescription : SharedDescriptionFields, SessionAttributes() {

    // v=0
    var version: String? = null
    // o=- 20518 0 IN IP4 203.0.113.1
    var origin: Origin? = null
    // s=
    var name: String? = null
    // u=https://foo.com
    var uri: String? = null
    // e=alice@foo.com
    var email: String? = null
    // p=+12345678
    var phone: String? = null
    // t=0 0
    var timing: Timing? = null
    // z=
    var timezones: String? = null
    // r=
    var repeats: String? = null
    // m=video 51744 RTP/AVP 126 97 98 34 31
    var media: MutableList<Media> = arrayListOf()

    override var description: String? = null
    override var connection: SharedDescriptionFields.Connection? = null
    override var bandwidth: MutableList<SharedDescriptionFields.Bandwidth>? = null

    public data class Origin(
        var username: String,
        var sessionId: Int,
        var sessionVersion: Int,
        var netType: String,
        var ipVer: Int,
        var address: String
    )

    public data class Timing(var start: Int, var stop: Int)

    public class Media : MediaDescription() {
        var type: String = ""
        var port: Int = 0
        var protocol: String = ""
        var payloads: String? = null
    }
}

public open class MediaDescription : SharedDescriptionFields, MediaAttributes() {
    override var description: String? = null
    override var connection: SharedDescriptionFields.Connection? = null
    override var bandwidth: MutableList<SharedDescriptionFields.Bandwidth>? = null
}

/**
 * Attributes that only exist at the session level (before an m= block).
 *
 * https://www.iana.org/assignments/sdp-parameters/sdp-parameters.xhtml#sdp-parameters-7
 */
public open class SessionAttributes : SharedAttributes() {
    // a=ice-lite
    var icelite: String? = null
    // a=group:BUNDLE audio video
    var groups: MutableList<Group>? = null
    // a=msid-semantic: WMS Jvlam5X3SX1OP6pn20zWogvaKJz5Hjf9OnlV
    var msidSemantic: MsidSemantic? = null

    public data class Group(val type: String, val mids: String)

    public data class MsidSemantic(val semantic: String, val token: String)
}

/**
 * Attributes that only exist at the media level (within an m= block).
 *
 * https://www.iana.org/assignments/sdp-parameters/sdp-parameters.xhtml#sdp-parameters-9
 */
public open class MediaAttributes : SharedAttributes() {

    // a=rtpmap:110 opus/48000/2
    var rtp: MutableList<Rtp>? = null
    // a=rtcp:65179 IN IP4 193.84.77.194
    var rtcp: Rtcp? = null
    // a=rtcp-fb:98 nack rpsi
    var rtcpFb: MutableList<RtcpFb>? = null
    // a=rtcp-fb:98 trr-int 100
    var rtcpFbTrrInt: MutableList<RtcpFbTrrInt>? = null
    // a=fmtp:108 profile-level-id=24;bitrate=64000
    // a=fmtp:111 minptime=10; useinbandfec=1
    var fmtp: MutableList<Fmtp>? = null
    // a=mid:audio
    var mid: String? = null
    // a=msid:0c8b064d-d807-43b4 46e0-8e16-7ef0db0db64a
    var msid: String? = null
    // a=ptime:20
    var ptime: Int? = null
    // a=maxptime:60
    var maxptime: Int? = null
    // a=crypto:1 AES_CM_128_HMAC_SHA1_80 inline:PS1uQCVeeCFCanVmcjkpPywjNWhcYD0mXXtxaVBR|2^20|1:32
    var crypto: MutableList<Crypto>? = null
    // a=candidate:0 1 UDP 2113667327 203.0.113.1 54400 typ host
    // a=candidate:1162875081 1 udp 2113937151 192.168.34.75 60017 typ host generation 0 network-id 3 network-cost 10
    // a=candidate:3289912957 2 udp 1845501695 193.84.77.194 60017 typ srflx raddr 192.168.34.75 rport 60017 generation 0 network-id 3 network-cost 10
    // a=candidate:229815620 1 tcp 1518280447 192.168.150.19 60017 typ host tcptype active generation 0 network-id 3 network-cost 10
    // a=candidate:3289912957 2 tcp 1845501695 193.84.77.194 60017 typ srflx raddr 192.168.34.75 rport 60017 tcptype passive generation 0 network-id 3 network-cost 10
    var candidates: MutableList<Candidate> = arrayListOf()
    // a=end-of-candidates
    var endOfCandidates: String? = null
    // a=remote-candidates:1 203.0.113.1 54400 2 203.0.113.1 54401
    var remoteCandidates: String? = null
    // a=ssrc:2566107569 cname:t9YU8M1UxTF8Y1A1
    var ssrcs: MutableList<Ssrc>? = null
    // a=ssrc-group:FEC-FR 3004364195 1080772241
    var ssrcGroups: MutableList<SsrcGroup>? = null
    // a=rtcp-mux
    var rtcpMux: String? = null
    // a=rtcp-rsize
    var rtcpRsize: String? = null
    // a=sctpmap:5000 webrtc-datachannel 1024
    var sctpmap: Sctpmap? = null
    // a=x-google-flag
    var xGoogleFlag: String? = null
    // a=rid:1 send max-width=1280;max-height=720
    var rids: MutableList<Rid>? = null
    // a=imageattr:97 send [x=800,y=640,sar=1.1,q=0.6] [x=480,y=320] recv [x=330,y=250]
    // a=imageattr:* send [x=800,y=640] recv *
    // a=imageattr:100 recv [x=320,y=240]
    var imageattrs: MutableList<ImageAttr>? = null
    // a=simulcast:send 1,2,3;~4,~5 recv 6;~7,~8
    // a=simulcast:recv 1;4,5 send 6;7
    var simulcast: Simulcast? = null
    // a=simulcast: recv pt=97;98 send pt=97
    // a=simulcast: send rid=5;6;7 paused=6,7
    var simulcast_03: Simulcast_03? = null
    // a=framerate:25
    // a=framerate:29.97
    var framerate: Float? = null
    // a=ts-refclk:ptp=IEEE1588-2008:00-50-C2-FF-FE-90-04-37:0
    var tsRefclk: String? = null
    // a=mediaclk:direct=0
    // a=mediaclk:sender
    var mediaclk: String? = null
    // a=sync-time:0
    var syncTime: Int? = null

    public data class Rtp(
        var payload: Int,
        var codec: String,
        var rate: Int? = null,
        var encoding: String? = null
    )

    public data class Rtcp(
        var port: Int,
        var netType: String? = null,
        var ipVer: Int? = null,
        var address: String? = null
    )

    public data class RtcpFb(
        var payload: String,
        var type: String,
        var subtype: String? = null
    )

    public data class RtcpFbTrrInt(var payload: String, var value: Int)

    public data class Fmtp(var payload: Int, var config: String)

    public data class Crypto(
        var id: Int,
        var suite: String,
        var config: String,
        var sessionConfig: String? = null
    )

    public data class Candidate(
        var foundation: String,
        var component: Int,
        var transport: String,
        var priority: Int,
        var ip: String,
        var port: Int,
        var type: String,
        var raddr: String? = null,
        var rport: Int? = null,
        var tcptype: String? = null,
        var generation: Int? = null,
        var `network-id`: Int? = null,
        var `network-cost`: Int? = null
    )

    public data class Ssrc(
        var id: Int,
        var attribute: String,
        var value: String? = null
    )

    public data class SsrcGroup(var semantics: String, var ssrcs: String)

    public data class Sctpmap(
        var sctpmapNumber: Int,
        var app: String,
        var maxMessageSize: Int
    )

    public data class Rid(
        var id: String,
        var direction: String,
        var params: String? = null
    )

    public data class ImageAttr(
        var pt: String,
        var dir1: String,
        var attrs1: String,
        var dir2: String? = null,
        var attrs2: String? = null
    )

    public data class Simulcast(
        var dir1: String,
        var list1: String,
        var dir2: String? = null,
        var list2: String? = null
    )

    public data class Simulcast_03(
        var value: String
    )

}

/**
 * These attributes can exist on both the session level and the media level.
 *
 * https://www.iana.org/assignments/sdp-parameters/sdp-parameters.xhtml#sdp-parameters-8
 */
public open class SharedAttributes {

    // a=sendrecv
    // a=recvonly
    // a=sendonly
    // a=inactive
    var direction: String? = null
    // a=control:streamid=0
    var control: String? = null
    // a=extmap:1/recvonly URI-gps-string
    // a=extmap:2 urn:ietf:params:rtp-hdrext:toffset
    var ext: MutableList<Ext>? = null
    // a=setup:actpass
    var setup: String? = null
    // a=ice-ufrag:F7gI
    var iceUfrag: String? = null
    // a=ice-pwd:x9cml/YzichV2+XlhiMu8g
    var icePwd: String? = null
    // a=ice-options:google-ice
    var iceOptions: String? = null
    // a=fingerprint:SHA-1 00:11:22:33:44:55:66:77:88:99:AA:BB:CC:DD:EE:FF:00:11:22:33
    var fingerprint: Fingerprint? = null
    // a=source-filter: incl IN IP4 239.5.2.31 10.1.15.5
    var sourceFilter: SourceFilter? = null
    var invalid: MutableList<Invalid>? = null

    public data class Ext(
        var value: Int,
        var direction: String? = null,
        var uri: String,
        var config: String? = null
    )

    public data class Fingerprint(var type: String, var hash: String)

    public data class SourceFilter(
        var filterMode: String,
        var netType: String,
        var addressTypes: String,
        var destAddress: String,
        var srcList: String
    )

    public data class Invalid(var value: String)
}

/**
 * Descriptor fields that exist at both the session level and media level.
 *
 * See the SDP grammar for more details: https://tools.ietf.org/html/rfc4566#section-9
 */
public interface SharedDescriptionFields {
    // i=foo
    var description: String?
    // c=IN IP4 10.47.197.26
    var connection: Connection?
    // b=AS:4000
    var bandwidth: MutableList<Bandwidth>?

    public data class Connection(var version: Int, var ip: String)

    public data class Bandwidth(
        var type: String, // 'TIAS' | 'AS' | 'CT' | 'RR' | 'RS'
        var limit: Int
    )

}