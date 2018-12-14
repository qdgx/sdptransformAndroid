package com.dingsoft.sdptransform

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val sdp = """
            |v=0
            |i=foo
            |u=https://foo.com
            |e=alice@foo.com
            |p=+12345678
            |b=AS:4000
            |o=- 20518 0 IN IP4 203.0.113.1
            |s=haha
            |t=0 0
            |c=IN IP4 203.0.113.1
            |a=ice-ufrag:F7gI
            |a=ice-pwd:x9cml/YzichV2+XlhiMu8g
            |a=ice-lite
            |a=ice-options:google-ice
            |a=fingerprint:sha-1 42:89:c5:c6:55:9d:6e:c8:e8:83:55:2a:39:f9:b6:eb:e9:a3:a9:e7
            |m=audio 54400 RTP/SAVPF 0 96
            |a=rtpmap:0 PCMU/8000
            |a=rtpmap:96 opus/48000
            |a=sendrecv
            |a=fmtp:111 minptime=10; useinbandfec=1
            |a=control:streamid=0
            |a=rtcp:65179 IN IP4 193.84.77.194
            |a=extmap:1/recvonly URI-gps-string
            |a=extmap:2 urn:ietf:params:rtp-hdrext:toffset
            |a=crypto:1 AES_CM_128_HMAC_SHA1_80 inline:PS1uQCVeeCFCanVmcjkpPywjNWhcYD0mXXtxaVBR|2^20|1:32
            |a=setup:actpass
            |a=mid:audio
            |a=msid:0c8b064d-d807-43b4 46e0-8e16-7ef0db0db64a
            |a=ptime:20
            |a=maxptime:60
            |a=ssrc-group:FEC-FR 3004364195 1080772241
            |a=msid-semantic: WMS Jvlam5X3SX1OP6pn20zWogvaKJz5Hjf9OnlV
            |a=group:BUNDLE audio video
            |a=rtcp-mux
            |a=rtcp-rsize
            |a=sctpmap:5000 webrtc-datachannel 1024
            |a=x-google-flag:conference
            |a=rid:1 send   max-width=1280; max-height=720
            |a=imageattr:97 send [x=800,y=640,sar=1.1,q=0.6] [x=480,y=320] recv [x=330,y=250]
            |a=imageattr:* send [x=800,y=640] recv *
            |a=imageattr:100 recv [x=320,y=240]
            |a=simulcast:send 1,2,3;~4,~5 recv 6;~7,~8
            |a=framerate:25
            |a=ts-refclk:ptp=IEEE1588-2008:00-50-C2-FF-FE-90-04-37:0
            |a=mediaclk:direct=0
            |a=sync-time:0
            |a=candidate:3289912957 2 tcp 1845501695 193.84.77.194 60017 typ srflx raddr 192.168.34.75 rport 60017 tcptype passive generation 0 network-id 3 network-cost 10
            |a=candidate:0 1 UDP 2113667327 203.0.113.1 54400 typ host
            |a=candidate:1 2 UDP 2113667326 203.0.113.1 54401 typ host
            |a=end-of-candidates
            |a=remote-candidates:1 203.0.113.1 54400 2 203.0.113.1 54401
            |m=video 55400 RTP/SAVPF 97 98
            |a=rtcp-fb:* nack
            |a=rtpmap:97 H264/90000
            |a=fmtp:97 profile-level-id=4d0028;packetization-mode=1
            |a=rtcp-fb:97 trr-int 100
            |a=rtcp-fb:97 nack rpsi
            |a=rtpmap:98 VP8/90000
            |a=rtcp-fb:98 trr-int 100
            |a=rtcp-fb:98 nack rpsi
            |a=sendrecv
            |a=candidate:0 1 UDP 2113667327 203.0.113.1 55400 typ host
            |a=candidate:1 2 UDP 2113667326 203.0.113.1 55401 typ host
            |a=ssrc:1399694169 foo:bar
            |a=ssrc:1399694169 baz
            """.trimMargin()

    private val sdpTransform = SdpTransform()
    private val gson = GsonBuilder().disableHtmlEscaping().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun parse(view: View) {
        val sessionDescription = sdpTransform.parse(sdp)
        sample_text.text = gson.toJson(sessionDescription)
        Log.e(MainActivity::class.java.simpleName, sample_text.text.toString())
    }

    fun parseParams(view: View) {
        val sessionDescription = sdpTransform.parse(sdp)
        val fmtp = sessionDescription.media[0].fmtp
        if (fmtp != null) {
            val config = fmtp[0].config
            sample_text.text = gson.toJson(sdpTransform.parseParams(config))
            Log.e(MainActivity::class.java.simpleName, sample_text.text.toString())
        }
    }

    fun parsePayloads(view: View) {
        val sessionDescription = sdpTransform.parse(sdp)
        val payloads = sessionDescription.media[0].payloads
        if (payloads != null) {
            sample_text.text = gson.toJson(sdpTransform.parsePayloads(payloads))
            Log.e(MainActivity::class.java.simpleName, sample_text.text.toString())
        }
    }

    fun parseImageAttributes(view: View) {
        val sessionDescription = sdpTransform.parse(sdp)
        val imageAttrs = sessionDescription.media[0].imageattrs
        if (imageAttrs != null && imageAttrs.isNotEmpty()) {
            val imageAttr = imageAttrs[0]
            val attrs1 = imageAttr.attrs1
            sample_text.text = gson.toJson(sdpTransform.parseImageAttributes(attrs1))
            Log.e(MainActivity::class.java.simpleName, sample_text.text.toString())
        }
    }

    fun parseSimulcastStreamList(view: View) {
        val sessionDescription = sdpTransform.parse(sdp)
        val simulcastStream = sessionDescription.media[0].simulcast
        val streams = simulcastStream?.list1
        if (streams != null) {
            sample_text.text = gson.toJson(sdpTransform.parseSimulcastStreamList(streams))
            Log.e(MainActivity::class.java.simpleName, sample_text.text.toString())
        }
    }

    fun write(view: View) {
        val sessionDescription = sdpTransform.parse(sdp)
        sample_text.text = sdpTransform.write(sessionDescription)
        Log.e(MainActivity::class.java.simpleName, sample_text.text.toString())
    }

}
