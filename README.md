# sdptransformAndroid

Android version of the [libsdptransform](https://github.com/ibc/libsdptransform/) C++ library exposing the same API. It wraps the C++ library up and is written by [kotlin](https://kotlinlang.org/). It also uses [fastjson](https://github.com/alibaba/fastjson/) to parse and write objects.

**libsdptransform** is a simple parser and writer of SDP. Defines internal grammar based on [RFC4566 - SDP](http://tools.ietf.org/html/rfc4566), [RFC5245 - ICE](http://tools.ietf.org/html/rfc5245), and many more.


## Usage


### parse()

Syntax: `fun parse(sdp: String): SessionDescription`

Parses an unprocessed SDP string and returns a SessionDescription object. SDP lines can be terminated on `\r\n` (as per specification) or just `\n`.

```kotlin
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

val sessionDescription = SdpTransform().parse(sdp)
```

Resulting `sessionDescription` is an object as follows:

```json
{
    "connection": {
        "ip": "203.0.113.1",
        "version": 4
    },
    "fingerprint": {
        "hash": "42:89:c5:c6:55:9d:6e:c8:e8:83:55:2a:39:f9:b6:eb:e9:a3:a9:e7",
        "type": "sha-1"
    },
    "icePwd": "x9cml/YzichV2+XlhiMu8g",
    "iceUfrag": "F7gI",
    "media": [
        {
            "candidates": [
                {
                    "component": 1,
                    "foundation": "0",
                    "ip": "203.0.113.1",
                    "port": 54400,
                    "priority": 2113667327,
                    "transport": "UDP",
                    "type": "host"
                },
                {
                    "component": 2,
                    "foundation": "1",
                    "ip": "203.0.113.1",
                    "port": 54401,
                    "priority": 2113667326,
                    "transport": "UDP",
                    "type": "host"
                }
            ],
            "direction": "sendrecv",
            "fmtp": [],
            "payloads": "0 96",
            "port": 54400,
            "protocol": "RTP/SAVPF",
            "ptime": 20,
            "rtp": [
                {
                    "codec": "PCMU",
                    "payload": 0,
                    "rate": 8000
                },
                {
                    "codec": "opus",
                    "payload": 96,
                    "rate": 48000
                }
            ],
            "type": "audio"
        },
        {
            "candidates": [
                {
                    "component": 1,
                    "foundation": "0",
                    "ip": "203.0.113.1",
                    "port": 55400,
                    "priority": 2113667327,
                    "transport": "UDP",
                    "type": "host"
                },
                {
                    "component": 2,
                    "foundation": "1",
                    "ip": "203.0.113.1",
                    "port": 55401,
                    "priority": 2113667326,
                    "transport": "UDP",
                    "type": "host"
                }
            ],
            "direction": "sendrecv",
            "fmtp": [
                {
                    "config": "profile-level-id=4d0028;packetization-mode=1",
                    "payload": 97
                }
            ],
            "imageattrs": [
                {
                    "attrs1": "[x=1280,y=720]",
                    "attrs2": "[x=1280,y=720] [x=320,y=180]",
                    "dir1": "send",
                    "dir2": "recv",
                    "pt": "97"
                }
            ],
            "payloads": "97 98",
            "port": 55400,
            "protocol": "RTP/SAVPF",
            "rtcpFb": [
                {
                    "payload": "*",
                    "type": "nack"
                },
                {
                    "payload": "97",
                    "subtype": "rpsi",
                    "type": "nack"
                },
                {
                    "payload": "98",
                    "subtype": "rpsi",
                    "type": "nack"
                }
            ],
            "rtcpFbTrrInt": [
                {
                    "payload": "97",
                    "value": 100
                },
                {
                    "payload": "98",
                    "value": 100
                }
            ],
            "rtp": [
                {
                    "codec": "H264",
                    "payload": 97,
                    "rate": 90000
                },
                {
                    "codec": "VP8",
                    "payload": 98,
                    "rate": 90000
                }
            ],
            "simulcast": {
                "dir1": "send",
                "list1": "1,~4;2;3"
            },
            "ssrcs": [
                {
                    "attribute": "foo",
                    "id": 1399694169,
                    "value": "bar"
                },
                {
                    "attribute": "baz",
                    "id": 1399694169
                }
            ],
            "type": "video"
        }
    ],
    "name": "",
    "origin": {
        "address": "203.0.113.1",
        "ipVer": 4,
        "netType": "IN",
        "sessionId": 20518,
        "sessionVersion": 0,
        "username": "-"
    },
    "timing": {
        "start": 0,
        "stop": 0
    },
    "version": "0"
}
```


### Parser Postprocessing

No excess parsing is done to the raw strings because the writer is built to be the inverse of the parser. That said, a few helpers have been built in:


#### parseParams()

Syntax: `fun parseParams(params: String): Map<String, Any>`

Parses `fmtp[index].config` and others such as `rid[index].params` and returns an object with all the params in a key/value fashion.

NOTE: All the values are expressed as strings.

```kotlin
val config = sessionDescription.media[1].fmtp[0].config
val params = sdpTransform.parseParams(config)
```

Resulting `params` is a map object as follows:

```json
{
  "packetization-mode": 1,
  "profile-level-id": "4d0028"
}
```


#### parsePayloads()

Syntax: `fun parsePayloads(payloads: String): List<Int>`

Returns a list with all the payload advertised in the corresponding m-line.

```kotlin
val payloadsString = sessionDescription.media[1].payloads
val payloads = sdpTransform.parsePayloads(payloadsString)
```

Resulting `payloads` is a list of `int` elements as follows:

```json
[ 97, 98 ]
```


#### parseImageAttributes()

Syntax: `fun parseImageAttributes(params: String): List<Map<String, Any>>`

Parses [Generic Image Attributes](https://tools.ietf.org/html/rfc6236). Must be provided with the `attrs1` or `attrs2` string of a `a=imageattr` line. Returns an array of key/value objects.

```kotlin
// a=imageattr:97 send [x=1280,y=720] recv [x=1280,y=720] [x=320,y=180]

val attrs2 = sessionDescription.media[1].imageattrs[0].attrs2
val imageAttributes = sdpTransform.parseImageAttributes(attrs2)
```

Resulting `imageAttributes` is a list of map as follows:

```json
[
  { "x": 1280, "y": 720 },
  { "x":  320, "y": 180 }
]
```


#### parseSimulcastStreamList()

Syntax: `fun parseSimulcastStreamList(streams: String): List<List<SimulcastStream>>`

Parses [simulcast](https://tools.ietf.org/html/draft-ietf-mmusic-sdp-simulcast) streams/formats. Must be provided with the `attrs1` or `attrs2` string of the `a=simulcast` line.

Returns an array of simulcast streams. Each entry is an array of alternative simulcast formats, which are objects with two keys:

* `scid`: Simulcast identifier (string)
* `paused`: Whether the simulcast format is paused (boolean)

```kotlin
// a=simulcast:send 1,~4;2;3

val streams = sessionDescription.media[1].simulcast.list1
val simulcastAttributes = sdpTransform.parseSimulcastStreamList(streams)
```

Resulting `simulcastAttributes` is a list of list as follows:

```json
[
  [ { "scid": "1", "paused": false }, { "scid": "4", "paused": true } ],
  [ { "scid": "2", "paused": false } ],
  [ { "scid": "3", "paused": false } ]
]
```


### write()

Syntax: `fun write(sdp: SessionDescription): String`

The writer is the inverse of the parser, and will need a struct equivalent to the one returned by it.

```kotlin
val newSdpStr = sdpTransform.write(sessionDescription) // session parsed above
```

Resulting `newSdpStr` is a string as follows:

```
v=0
o=- 20518 0 IN IP4 203.0.113.1
s=
c=IN IP4 203.0.113.1
t=0 0
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
a=rtpmap:97 H264/90000
a=rtpmap:98 VP8/90000
a=fmtp:97 profile-level-id=4d0028;packetization-mode=1
a=rtcp-fb:97 trr-int 100
a=rtcp-fb:98 trr-int 100
a=rtcp-fb:* nack
a=rtcp-fb:97 nack rpsi
a=rtcp-fb:98 nack rpsi
a=sendrecv
a=candidate:0 1 UDP 2113667327 203.0.113.1 55400 typ host
a=candidate:1 2 UDP 2113667326 203.0.113.1 55401 typ host
a=ssrc:1399694169 foo:bar
a=ssrc:1399694169 baz
a=imageattr:97 send [x=1280,y=720] recv [x=1280,y=720] [x=320,y=180]
a=simulcast:send 1,~4;2;3
```

The only thing different from the original input is we follow the order specified by the SDP RFC, and we will always do so.


## Run the demo

```
git clone https://github.com/wolfhan/sdptransformAndroid.git
cd sdptransformAndroid/
gradlew createSdptransformLib
```

After creating the sdptranform library, you can run the app.


## Development

* Build the lib:

```
gradlew createSdptransformLib
```


## Author

Wolf Han [github](https://github.com/wolfhan/)

Special thanks to [Iñaki Baz Castillo](https://github.com/ibc), the author of the [libsdptransform](https://github.com/ibc/libsdptransform/) and [Eirik Albrigtsen](https://github.com/clux), the author of the [sdp-transform](https://github.com/clux/sdp-transform/).


## License

[MIT](LICENSE)
