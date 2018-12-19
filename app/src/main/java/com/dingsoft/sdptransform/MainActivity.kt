package com.dingsoft.sdptransform

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.alibaba.fastjson.JSON
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val sdp = """
        v=0
        o=- 20518 0 IN IP4 203.0.113.1
        s=
        t=0 0
        c=IN IP4 203.0.113.1
        a=ice-ufrag:F7gI
        a=ice-pwd:x9cml/YzichV2+XlhiMu8g
        a=fingerprint:sha-1 42:89:c5:c6:55:9d:6e:c8:e8:83:55:2a:39:f9:b6:eb:e9:a3:a9:e7
        m=audio 54400 RTP/SAVPF 0 96
        a=rtpmap:0 PCMU/8000
        a=rtpmap:96 opus/48000
        a=ptime:20
        a=sendrecv
        a=candidate:0 1 UDP 2113667327 203.0.113.1 54400 typ host
        a=candidate:1 2 UDP 2113667326 203.0.113.1 54401 typ host
        m=video 55400 RTP/SAVPF 97 98
        a=rtcp-fb:* nack
        a=rtpmap:97 H264/90000
        a=fmtp:97 profile-level-id=4d0028;packetization-mode=1
        a=rtcp-fb:97 trr-int 100
        a=rtcp-fb:97 nack rpsi
        a=rtpmap:98 VP8/90000
        a=rtcp-fb:98 trr-int 100
        a=rtcp-fb:98 nack rpsi
        a=sendrecv
        a=candidate:0 1 UDP 2113667327 203.0.113.1 55400 typ host
        a=candidate:1 2 UDP 2113667326 203.0.113.1 55401 typ host
        a=ssrc:1399694169 foo:bar
        a=ssrc:1399694169 baz
        a=imageattr:97 send [x=1280,y=720] recv [x=1280,y=720] [x=320,y=180]
        a=simulcast:send 1,~4;2;3
        """.trimIndent()

    private val sdpTransform = SdpTransform()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun parse(view: View) {
        val sessionDescription = sdpTransform.parse(sdp)
        sample_text.text = JSON.toJSONString(sessionDescription, true)
        Log.e(MainActivity::class.java.simpleName, sample_text.text.toString())
    }

    /*
     * input: profile-level-id=4d0028;packetization-mode=1
     * output: {"packetization-mode":1, "profile-level-id":"4d0028"}
     */
    fun parseParams(view: View) {
        val sessionDescription = sdpTransform.parse(sdp)
        val fmtp = sessionDescription.media[1].fmtp
        if (fmtp != null) {
            val config = fmtp[0].config
            sample_text.text = JSON.toJSONString(sdpTransform.parseParams(config), true)
            Log.e(MainActivity::class.java.simpleName, sample_text.text.toString())
        }
    }

    /*
    * input: 97 98
    * output: [97, 98]
    */
    fun parsePayloads(view: View) {
        val sessionDescription = sdpTransform.parse(sdp)
        val payloads = sessionDescription.media[1].payloads
        if (payloads != null) {
            sample_text.text = JSON.toJSONString(sdpTransform.parsePayloads(payloads), true)
            Log.e(MainActivity::class.java.simpleName, sample_text.text.toString())
        }
    }

    /*
    * input: [x=1280,y=720] [x=320,y=180]
    * output:
    * [
    *	{"x":1280,"y":720},
    *	{"x":320,"y":180}
    * ]
    */
    fun parseImageAttributes(view: View) {
        val sessionDescription = sdpTransform.parse(sdp)
        val imageAttrs = sessionDescription.media[1].imageattrs
        if (imageAttrs != null && imageAttrs.isNotEmpty()) {
            val imageAttr = imageAttrs[0]
            val attrs2 = imageAttr.attrs2
            if (attrs2 != null) {
                sample_text.text = JSON.toJSONString(sdpTransform.parseImageAttributes(attrs2), true)
                Log.e(MainActivity::class.java.simpleName, sample_text.text.toString())
            }
        }
    }

    /*
    * input: 1,~4;2;3
    * output:
    * [
    *	[{"paused":false,"scid":"1"},{"paused":true,"scid":"4"}],
    *	[{"paused":false,"scid":"2"}],
    *	[{"paused":false,"scid":"3"}]
    * ]
    */
    fun parseSimulcastStreamList(view: View) {
        val sessionDescription = sdpTransform.parse(sdp)
        val simulcastStream = sessionDescription.media[1].simulcast
        val streams = simulcastStream?.list1
        if (streams != null) {
            sample_text.text = JSON.toJSONString(sdpTransform.parseSimulcastStreamList(streams), true)
            Log.e(MainActivity::class.java.simpleName, sample_text.text.toString())
        }
    }

    fun write(view: View) {
        val sessionDescription = sdpTransform.parse(sdp)
        sample_text.text = sdpTransform.write(sessionDescription)
        Log.e(MainActivity::class.java.simpleName, sample_text.text.toString())
    }

}
