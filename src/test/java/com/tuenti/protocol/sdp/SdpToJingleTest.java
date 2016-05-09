package com.tuenti.protocol.sdp;

import net.java.sip.communicator.impl.protocol.jabber.extensions.jingle.*;
import net.sourceforge.jsdp.*;

import org.jivesoftware.smack.packet.IQ;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for the SdpToJingle class.
 *
 * Copyright (c) Tuenti Technologies. All rights reserved.
 *
 * @author Wijnand Warren <wwarren@tuenti.com>
 * @author Manuel Peinado Gallego <mpeinado@tuenti.com>
 */
@RunWith(JUnit4.class)
public class SdpToJingleTest {
	private static final String SAMPLE_SDP_MESSAGE = "v=0\r\n"
			+ "o=- 123 1 IN IP4 127.0.0.1\r\n"
			+ "s=session\r\n"
			+ "t=0 0\r\n"
			+ "a=group:BUNDLE audio video\r\n"
			+ "m=audio 36798 RTP/AVPF 103 104 110 107 9 102 108 0 8 106 105 13 127 126\r\n"
			+ "c=IN IP4 172.22.76.221\r\n"
			+ "a=rtcp:36798 IN IP4 172.22.76.221\r\n"
			+ "a=ice-ufrag:YuWMyUbmK/CX6awo\r\n"
			+ "a=ice-pwd:DpueNNn6/r6TTRFMqNWw0v/c\r\n"
			+ "a=candidate:1 2 udp 1 172.22.76.221 47216 typ host generation 0\r\n"
			+ "a=candidate:1 1 udp 1 172.22.76.221 48235 typ host generation 0\r\n"
			+ "a=candidate:1 2 udp 2 172.22.76.221 36798 typ srflx raddr 10.0.34.44 rport 48296 generation 0\r\n"
			+ "a=candidate:1 1 udp 2 172.22.76.221 50102 typ relay raddr 213.99.45.11 rport 4313 generation 0\r\n"
			+ "a=sendrecv\r\n"
			+ "a=mid:audio\r\n"
			+ "{{RTCP-MUX}}" // placeholder for a=rtcp-mux\r\n
			+ "a=crypto:0 AES_CM_128_HMAC_SHA1_32 inline:keNcG3HezSNID7LmfDa9J4lfdUL8W1F7TNJKcbuy \r\n"
			+ "a=rtpmap:111 opus/48000/2\r\n"
			+ "a=rtpmap:103 ISAC/16000\r\n"
			+ "a=rtpmap:104 ISAC/32000\r\n"
			+ "a=rtpmap:110 CELT/32000\r\n"
			+ "a=rtpmap:107 speex/16000\r\n"
			+ "a=rtpmap:9 G722/16000\r\n"
			+ "a=rtpmap:102 ILBC/8000\r\n"
			+ "a=rtpmap:108 speex/8000\r\n"
			+ "a=rtpmap:0 PCMU/8000\r\n"
			+ "a=rtpmap:8 PCMA/8000\r\n"
			+ "a=rtpmap:106 CN/32000\r\n"
			+ "a=rtpmap:105 CN/16000\r\n"
			+ "a=rtpmap:13 CN/8000\r\n"
			+ "a=rtpmap:127 red/8000\r\n"
			+ "a=rtpmap:126 telephone-event/8000\r\n"
			+ "a=ssrc:2570980487 cname:hsWuSQJxx7przmb8\r\n"
			+ "a=ssrc:2570980487 mslabel:stream_label\r\n"
			+ "a=ssrc:2570980487 label:audio_label\r\n"
			+ "m=video 39456 RTP/AVPF 100 101 102\r\n"
			+ "c=IN IP4 172.22.76.221\r\n"
			+ "a=rtcp:39456 IN IP4 172.22.76.221\r\n"
			+ "a=candidate:1 2 udp 1 172.22.76.221 40550 typ host generation 0\r\n"
			+ "a=candidate:1 1 udp 1 172.22.76.221 53441 typ host generation 0\r\n"
			+ "a=candidate:1 2 udp 2 172.22.76.221 46128 typ srflx raddr 10.0.34.43 rport 48295 generation 0\r\n"
			+ "a=candidate:1 1 udp 2 172.22.76.221 39456 typ relay raddr 213.99.45.10 rport 4312 generation 0\r\n"
			+ "a=sendrecv\r\n"
			+ "a=mid:video\r\n"
			+ "{{RTCP-MUX}}" // placeholder for a=rtcp-mux\r\n
			+ "a=crypto:0 AES_CM_128_HMAC_SHA1_80 inline:5ydJsA+FZVpAyqJMT/nW/UW+tcOmDvXJh/pPhNRe \r\n"
			+ "a=rtpmap:100 VP8/90000\r\n"
			+ "a=rtpmap:101 red/90000\r\n"
			+ "a=rtpmap:102 ulpfec/90000\r\n"
			+ "a=ssrc:43633328 cname:hsWuSQJxx7przmb8\r\n"
			+ "a=ssrc:43633328 mslabel:stream_label\r\n"
			+ "a=ssrc:43633328 label:video_label\r\n";

	private static final String SAMPLE_ICE_CANDIDATES_SDP_STUB =
			"a=candidate:1 2 udp 1 172.22.76.221 47216 typ host generation 0\r\n"
			+ "a=candidate:1 1 udp 1 172.22.76.221 48235 typ host generation 0\r\n"
			+ "a=candidate:1 2 udp 2 172.22.76.221 36798 typ srflx raddr 10.0.34.44 rport 48296 generation 0\r\n"
			+ "a=candidate:1 1 udp 2 172.22.76.221 50102 typ relay raddr 213.99.45.11 rport 4313 generation 0\r\n"
			+ "a=candidate:1 2 udp 1 172.22.76.221 40550 typ host generation 0\r\n"
			+ "a=candidate:1 1 udp 1 172.22.76.221 53441 typ host generation 0\r\n"
			+ "a=candidate:1 2 udp 2 172.22.76.221 46128 typ srflx raddr 10.0.34.43 rport 48295 generation 0\r\n"
			+ "a=candidate:1 1 udp 2 172.22.76.221 39456 typ relay raddr 213.99.45.10 rport 4312 generation 0\r\n";
	private static final String MEDIA_NAME = "audio";
	private static final String SAMPLE_SID = "123456";

	private SessionDescription sdp;

	/**
	 * Cleans up after the test has been run.
	 */
	@After
	public void tearDown() {
		sdp = null;
	}

	public void prepare() {
		prepare(true);
	}

	/**
	 * Prepares the {@link SessionDescription} object to be used in the tests.
	 *
	 * @param includeRtcpMuxAttr boolean - Whether or not to include the RTCP MUX attribute.
	 */
	public void prepare(boolean includeRtcpMuxAttr) {
		try {
			String sdpMessage = SAMPLE_SDP_MESSAGE.replace("{{RTCP-MUX}}", includeRtcpMuxAttr ? "a=rtcp-mux\r\n" : "");
			sdp = SDPFactory.parseSessionDescription(sdpMessage);
		} catch (SDPParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Verifies a Jingle IQ.
	 *
	 * @param jingle {@link JingleIQ} - The Jingle IQ to check.
	 */
	private void verifyJingleIq(JingleIQ jingle, boolean checkCandidatePort) {
		Assert.assertNotNull(jingle);

		Assert.assertTrue(jingle.getSID().equals("123"));
		Assert.assertEquals(IQ.Type.SET, jingle.getType());

		List<ContentPacketExtension> contentList = jingle.getContentList();
		Assert.assertTrue(contentList.size() == 2);
		for (ContentPacketExtension content : contentList) {
			Assert.assertTrue(contentList.get(0).getChildExtensionsOfType(RtpDescriptionPacketExtension.class).size() == 1);
			Assert.assertTrue(content.getCreator() == ContentPacketExtension.CreatorEnum.initiator);
		}

		ContentPacketExtension audio = contentList.get(0);
		Assert.assertTrue(audio.getName().equals("audio"));
		List<RtpDescriptionPacketExtension> packetExts = audio.getChildExtensionsOfType(RtpDescriptionPacketExtension.class);
		Assert.assertTrue(packetExts.size() == 1);
		RtpDescriptionPacketExtension packetExt = packetExts.get(0);
		Assert.assertTrue(packetExt.getMedia().equals("audio"));

		List<PayloadTypePacketExtension> payloadExts = packetExt.getChildExtensionsOfType(PayloadTypePacketExtension.class);
		Assert.assertTrue(payloadExts.size() == 15);
		Assert.assertTrue(payloadExts.get(0).getID() == 111);
		Assert.assertTrue(payloadExts.get(0).getName().equals("opus"));
		Assert.assertTrue(payloadExts.get(0).getClockrate().equals("48000/2"));
		Assert.assertTrue(payloadExts.get(8).getID() == 0);
		Assert.assertTrue(payloadExts.get(8).getName().equals("PCMU"));
		Assert.assertTrue(payloadExts.get(8).getClockrate().equals("8000"));

		List<RawUdpTransportPacketExtension> rawUdpExts = audio.getChildExtensionsOfType(RawUdpTransportPacketExtension.class);
		Assert.assertTrue(rawUdpExts.size() == 1);
		Assert.assertTrue(rawUdpExts.get(0).getCandidateList().size() == 1);
		Assert.assertTrue(rawUdpExts.get(0).getCandidateList().get(0).getIP().equals("172.22.76.221"));
		if (checkCandidatePort) {
			Assert.assertTrue(rawUdpExts.get(0).getCandidateList().get(0).getPort() == 36798);
		}
		Assert.assertTrue(rawUdpExts.get(0).getCandidateList().get(0).getGeneration() == 0);

		// ICE
		List<IceUdpTransportPacketExtension> iceUdpExts = audio.getChildExtensionsOfType(IceUdpTransportPacketExtension.class);
		iceUdpExts = Utils.filterByClass(iceUdpExts, IceUdpTransportPacketExtension.class);
		Assert.assertEquals(1, iceUdpExts.size());
		IceUdpTransportPacketExtension icePacket;
		for (int i = 0; i < iceUdpExts.size(); ++i) {
			icePacket = iceUdpExts.get(i);
			Assert.assertEquals(4, icePacket.getCandidateList().size());
			Assert.assertEquals("YuWMyUbmK/CX6awo", icePacket.getUfrag());
			Assert.assertEquals("DpueNNn6/r6TTRFMqNWw0v/c", icePacket.getPassword());
		}
		verifyCandidateExtension(iceUdpExts.get(0).getCandidateList().get(1), 1, "1", "udp", 1, 0,
				CandidateType.host, "172.22.76.221", 48235);
		verifyCandidateExtension(iceUdpExts.get(0).getCandidateList().get(2), 2, "1", "udp", 2, 0,
				CandidateType.srflx, "172.22.76.221", 36798);

		Assert.assertTrue(contentList.get(1).getName().equals("video"));
		ContentPacketExtension video = contentList.get(1);
		Assert.assertTrue(video.getName().equals("video"));
		packetExts = video.getChildExtensionsOfType(RtpDescriptionPacketExtension.class);
		Assert.assertTrue(packetExts.size() == 1);
		packetExt = packetExts.get(0);
		Assert.assertTrue(packetExt.getMedia().equals("video"));

		payloadExts = packetExt.getChildExtensionsOfType(PayloadTypePacketExtension.class);
		Assert.assertTrue(payloadExts.size() == 3);
		Assert.assertTrue(payloadExts.get(2).getID() == 102);
		Assert.assertTrue(payloadExts.get(2).getName().equals("ulpfec"));
		Assert.assertTrue(payloadExts.get(2).getClockrate().equals("90000"));

		rawUdpExts = video.getChildExtensionsOfType(RawUdpTransportPacketExtension.class);
		Assert.assertTrue(rawUdpExts.size() == 1);
		Assert.assertTrue(rawUdpExts.get(0).getCandidateList().size() == 1);
		Assert.assertTrue(rawUdpExts.get(0).getCandidateList().get(0).getIP().equals("172.22.76.221"));
		if (checkCandidatePort) {
			Assert.assertTrue(rawUdpExts.get(0).getCandidateList().get(0).getPort() == 39456);
		}
		Assert.assertTrue(rawUdpExts.get(0).getCandidateList().get(0).getGeneration() == 0);

		iceUdpExts = video.getChildExtensionsOfType(IceUdpTransportPacketExtension.class);
		iceUdpExts = Utils.filterByClass(iceUdpExts, IceUdpTransportPacketExtension.class);
		Assert.assertEquals(1, iceUdpExts.size());
		for (int i = 0; i < iceUdpExts.size(); ++i) {
			Assert.assertEquals(4, iceUdpExts.get(i).getCandidateList().size());
		}
		verifyCandidateExtension(iceUdpExts.get(0).getCandidateList().get(1), 1, "1", "udp", 1, 0,
				CandidateType.host, "172.22.76.221", 53441);
		verifyCandidateExtension(iceUdpExts.get(0).getCandidateList().get(2), 2, "1", "udp", 2, 0,
				CandidateType.srflx, "172.22.76.221", 46128);
	}

	private static void verifyCandidateExtension(CandidatePacketExtension candidateExt, int component, String foundation,
												 String protocol, int priority, int generation, CandidateType type,
												 String ip, int port) {

		Assert.assertEquals(component, candidateExt.getComponent());
		Assert.assertEquals(foundation, candidateExt.getFoundation());
		Assert.assertEquals(protocol, candidateExt.getProtocol());
		Assert.assertEquals(priority, candidateExt.getPriority());
		Assert.assertEquals(generation, candidateExt.getGeneration());
		Assert.assertEquals(type, candidateExt.getType());
		Assert.assertEquals(ip, candidateExt.getIP());
		Assert.assertEquals(port, candidateExt.getPort());
	}

	@Test
	public void testSdpToJingle() {
		prepare();
		JingleIQ jingle = SdpToJingle.jingleFromSdp(sdp);
		verifyJingleIq(jingle, true);
	}

	@Test
	public void testJingleToSdp() {
		prepare();
		// SDP => Jingle
		JingleIQ jingle = SdpToJingle.jingleFromSdp(sdp);
		Assert.assertNotNull(jingle);

		// Jingle => SDP
		sdp = SdpToJingle.sdpFromJingle(jingle);
		Assert.assertNotNull(sdp);

		jingle = SdpToJingle.jingleFromSdp(sdp);
		verifyJingleIq(jingle, false);
	}

	@Test
	public void testRtcpMuxPresentInSdp() {
		prepare();
		MediaDescription audio = sdp.getMediaDescriptions()[0];
		Assert.assertTrue(audio.getAttributes("rtcp-mux").length == 1);
	}

	@Test
	public void testRtcpMuxAbsentInSdp() {
		prepare(false);
		MediaDescription audio = sdp.getMediaDescriptions()[0];
		Assert.assertTrue(audio.getAttributes("rtcp-mux").length == 0);
	}

	@Test
	public void testRtcpMuxPresentInJingle() {
		prepare();
		JingleIQ jingle = SdpToJingle.jingleFromSdp(sdp);

		List<ContentPacketExtension> contents = jingle.getContentList();
		for (ContentPacketExtension content : contents) {

			List<RtpDescriptionPacketExtension> descriptionExts = content.getChildExtensionsOfType(RtpDescriptionPacketExtension.class);
			RtpDescriptionPacketExtension descriptionExt = descriptionExts.get(0);
			List<RtcpMuxExtension> rtcpMuxExts = descriptionExt.getChildExtensionsOfType(RtcpMuxExtension.class);
			Assert.assertTrue(rtcpMuxExts.size() == 1);
		}
	}

	@Test
	public void testRtcpMuxAbsentInJingle() {
		prepare(false);
		JingleIQ jingle = SdpToJingle.jingleFromSdp(sdp);

		List<ContentPacketExtension> contents = jingle.getContentList();
		for (ContentPacketExtension content : contents) {

			List<RtpDescriptionPacketExtension> descriptionExts = content.getChildExtensionsOfType(RtpDescriptionPacketExtension.class);
			RtpDescriptionPacketExtension descriptionExt = descriptionExts.get(0);
			List<RtcpMuxExtension> rtcpMuxExts = descriptionExt.getChildExtensionsOfType(RtcpMuxExtension.class);
			Assert.assertTrue(rtcpMuxExts.size() == 0);
		}
	}

	@Test
	public void testCryptoPresentInJingle() {
		prepare();
		JingleIQ jingle = SdpToJingle.jingleFromSdp(sdp);
		List<ContentPacketExtension> contents = jingle.getContentList();

		Assert.assertNotNull(contents);
		Assert.assertFalse(contents.isEmpty());

		for (ContentPacketExtension content : contents) {
			List<RtpDescriptionPacketExtension> descriptionExts = content.getChildExtensionsOfType(RtpDescriptionPacketExtension.class);
			RtpDescriptionPacketExtension descriptionExt = descriptionExts.get(0);
			List<EncryptionPacketExtension> encryptionExts = descriptionExt.getChildExtensionsOfType(EncryptionPacketExtension.class);
			Assert.assertTrue(encryptionExts.size() == 1);
			EncryptionPacketExtension encryptionExt = encryptionExts.get(0);
			Assert.assertTrue(encryptionExt.isRequired());
			List<CryptoPacketExtension> cryptoExts = encryptionExt.getChildExtensionsOfType(CryptoPacketExtension.class);
			Assert.assertTrue(cryptoExts.size() == 1);
			CryptoPacketExtension cryptoExt = cryptoExts.get(0);
			Assert.assertTrue(cryptoExt.getCryptoSuite().equals("AES_CM_128_HMAC_SHA1_32") || cryptoExt.getCryptoSuite().equals("AES_CM_128_HMAC_SHA1_80"));
			Assert.assertEquals(cryptoExt.getTag(), "0");
			Assert.assertTrue(cryptoExt.getKeyParams().equals("inline:keNcG3HezSNID7LmfDa9J4lfdUL8W1F7TNJKcbuy")
					|| cryptoExt.getKeyParams().equals("inline:5ydJsA+FZVpAyqJMT/nW/UW+tcOmDvXJh/pPhNRe"));
		}
	}

	@Test
	public void testCryptoPresentInSdp() {
		prepare();
		JingleIQ jingle = SdpToJingle.jingleFromSdp(sdp);
		Assert.assertNotNull(jingle);

		sdp = SdpToJingle.sdpFromJingle(jingle);
		Assert.assertNotNull(sdp);

		String text = sdp.toString();
		Assert.assertTrue(text.contains("a=crypto:0 AES_CM_128_HMAC_SHA1_32 inline:keNcG3HezSNID7LmfDa9J4lfdUL8W1F7TNJKcbuy"));
		Assert.assertTrue(text.contains("a=crypto:0 AES_CM_128_HMAC_SHA1_80 inline:5ydJsA+FZVpAyqJMT/nW/UW+tcOmDvXJh/pPhNRe"));
	}

	@Test
	public void testStreamsPresentInJingle() {
		prepare();
		JingleIQ jingle = SdpToJingle.jingleFromSdp(sdp);
		List<ContentPacketExtension> contents = jingle.getContentList();
		ContentPacketExtension audio = contents.get(0);
		List<RtpDescriptionPacketExtension> descriptionExts = audio.getChildExtensionsOfType(RtpDescriptionPacketExtension.class);
		RtpDescriptionPacketExtension descriptionExt = descriptionExts.get(0);

		List<StreamsPacketExtension> streamsExts = descriptionExt.getChildExtensionsOfType(StreamsPacketExtension.class);
		Assert.assertTrue(streamsExts.size() == 1);
		StreamsPacketExtension streamsExt = streamsExts.get(0);
		List<StreamPacketExtension> streamExts = streamsExt.getChildExtensionsOfType(StreamPacketExtension.class);
		Assert.assertEquals(1, streamExts.size());
		StreamPacketExtension streamExt = streamExts.get(0);
		Assert.assertTrue(streamExt.getAttributeNames().size() == 3);
		Assert.assertEquals(streamExt.getAttribute("cname"), "hsWuSQJxx7przmb8");
		Assert.assertEquals(streamExt.getAttribute("mslabel"), "stream_label");
		Assert.assertEquals(streamExt.getAttribute("label"), "audio_label");
		SsrcPacketExtension ssrcExt = streamExt.getSsrc();
		Assert.assertEquals(ssrcExt.getText(), "2570980487");
	}

	@Test
	public void testSsrcPresentInSdp() {
		prepare();
		JingleIQ jingle = SdpToJingle.jingleFromSdp(sdp);
		Assert.assertNotNull(jingle);

		sdp = SdpToJingle.sdpFromJingle(jingle);
		Assert.assertNotNull(sdp);

		String text = sdp.toString();
		Assert.assertTrue(text.contains("a=ssrc:2570980487 cname:hsWuSQJxx7przmb8"));
		Assert.assertTrue(text.contains("a=ssrc:2570980487 mslabel:stream_label"));
		Assert.assertTrue(text.contains("a=ssrc:2570980487 label:audio_label"));

		Assert.assertTrue(text.contains("a=ssrc:43633328 cname:hsWuSQJxx7przmb8"));
		Assert.assertTrue(text.contains("a=ssrc:43633328 mslabel:stream_label"));
		Assert.assertTrue(text.contains("a=ssrc:43633328 label:video_label"));
	}

	@Test
	public void testJingeIceCandidatesFromSdpStub() {
		List<String> iceCandidates = Arrays.asList(SAMPLE_ICE_CANDIDATES_SDP_STUB.split("\r\n"));
		JingleIQ iq = SdpToJingle.transportInfoFromSdpStub(iceCandidates, SAMPLE_SID, MEDIA_NAME);

		Assert.assertNotNull(iq);
		Assert.assertEquals(SAMPLE_SID, iq.getSID());
		Assert.assertEquals(IQ.Type.SET, iq.getType());

		List<ContentPacketExtension> contentList = iq.getContentList();
		Assert.assertEquals(1, contentList.size());

		ContentPacketExtension content = contentList.get(0);
		Assert.assertEquals(MEDIA_NAME, content.getName());

		ContentPacketExtension ice = iq.getContentForType(IceUdpTransportPacketExtension.class);
		List<IceUdpTransportPacketExtension> iceUdpExts = ice.getChildExtensionsOfType(IceUdpTransportPacketExtension
				.class);
		iceUdpExts = Utils.filterByClass(iceUdpExts, IceUdpTransportPacketExtension.class);

		Assert.assertEquals(1, iceUdpExts.size());

		for (int i = 0; i < iceUdpExts.size(); ++i) {
			Assert.assertEquals(8, iceUdpExts.get(i).getCandidateList().size());
		}

		verifyCandidateExtension(iceUdpExts.get(0).getCandidateList().get(1), 1, "1", "udp", 1, 0, CandidateType.host,
				"172.22.76.221", 48235);
		verifyCandidateExtension(iceUdpExts.get(0).getCandidateList().get(2), 2, "1", "udp", 2, 0, CandidateType.srflx,
				"172.22.76.221", 36798);
	}

	@Test
	public void testJitsiVideobridgeJingle() throws Exception {
	    String jingleIqString =
	            "    <jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-initiate\" initiator=\"1@conference.inin.com\" sid=\"5a36a5003cdd395c\">\r\n" +
	                    "        <content name=\"video\"/>\r\n" +
	                    "        <content name=\"audio\"/>\r\n" +
	                    "        <content name=\"data\"/>\r\n" +
	                    "        <content name=\"video\" senders=\"both\">\r\n" +
	                    "            <transport xmlns=\"urn:xmpp:jingle:transports:ice-udp:1\" pwd=\"1bglu5r23o02u492g32p57smnp\" ufrag=\"2b66n19i3ol7gv\">\r\n" +
	                    "                <candidate xmlns=\"urn:xmpp:jingle:transports:ice-udp:1\" component=\"1\" foundation=\"1\" generation=\"0\" id=\"5a36a5003cdd395c8a7d872659dacca052bb35e4\" network=\"0\" priority=\"2130706431\" protocol=\"ssltcp\" tcptype=\"passive\" type=\"host\" ip=\"172.18.10.26\" port=\"4443\"/>\r\n" +
	                    "                <candidate xmlns=\"urn:xmpp:jingle:transports:ice-udp:1\" component=\"1\" foundation=\"3\" generation=\"0\" id=\"5a36a5003cdd395c8a7d872659dacca0cf362b\" network=\"0\" priority=\"2130706431\" protocol=\"udp\" type=\"host\" ip=\"172.18.10.26\" port=\"10000\"/>\r\n" +
	                    "                <candidate xmlns=\"urn:xmpp:jingle:transports:ice-udp:1\" component=\"1\" foundation=\"2\" generation=\"0\" id=\"5a36a5003cdd395c8a7d872659dacca023ed4177\" network=\"0\" priority=\"1694498815\" protocol=\"ssltcp\" tcptype=\"passive\" type=\"srflx\" ip=\"54.89.50.245\" port=\"4443\" rel-addr=\"172.18.10.26\" rel-port=\"4443\"/>\r\n" +
	                    "                <candidate xmlns=\"urn:xmpp:jingle:transports:ice-udp:1\" component=\"1\" foundation=\"4\" generation=\"0\" id=\"5a36a5003cdd395c8a7d872659dacca06cff057e\" network=\"0\" priority=\"1677724415\" protocol=\"udp\" type=\"srflx\" ip=\"54.89.50.245\" port=\"10000\" rel-addr=\"172.18.10.26\" rel-port=\"10000\"/>AA:C1:BA:15:EA:8C:0A:8A:06:35:80:65:1C:2F:52:8E:24:E7:87:7A</transport>\r\n" +
	                    "                <description xmlns=\"urn:xmpp:jingle:apps:rtp:1\" media=\"video\">\r\n" +
	                    "                    <payload-type xmlns=\"urn:xmpp:jingle:apps:rtp:1\" id=\"100\" name=\"VP8\" clockrate=\"90000\"/>\r\n" +
	                    "                    <payload-type xmlns=\"urn:xmpp:jingle:apps:rtp:1\" id=\"116\" name=\"red\" clockrate=\"90000\"/>\r\n" +
	                    "                    <payload-type xmlns=\"urn:xmpp:jingle:apps:rtp:1\" id=\"117\" name=\"ulpfec\" clockrate=\"90000\"/>\r\n" +
	                    "                    <rtp-hdrext xmlns=\"urn:xmpp:jingle:apps:rtp:rtp-hdrext:0\" id=\"2\" uri=\"urn:ietf:params:rtp-hdrext:toffset\"/>\r\n" +
	                    "                    <rtp-hdrext xmlns=\"urn:xmpp:jingle:apps:rtp:rtp-hdrext:0\" id=\"3\" uri=\"http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\"/>\r\n" +
	                    "                    <rtcp-mux xmlns=\"urn:xmpp:jingle:apps:rtp:1\"/>\r\n" +
	                    "                </description>\r\n" +
	                    "            </content>\r\n" +
	                    "            <content name=\"audio\" senders=\"both\">\r\n" +
	                    "                <transport xmlns=\"urn:xmpp:jingle:transports:ice-udp:1\" pwd=\"1bglu5r23o02u492g32p57smnp\" ufrag=\"2b66n19i3ol7gv\">\r\n" +
	                    "                    <candidate xmlns=\"urn:xmpp:jingle:transports:ice-udp:1\" component=\"1\" foundation=\"1\" generation=\"0\" id=\"5a36a5003cdd395c8a7d872659dacca052bb35e4\" network=\"0\" priority=\"2130706431\" protocol=\"ssltcp\" tcptype=\"passive\" type=\"host\" ip=\"172.18.10.26\" port=\"4443\"/>\r\n" +
	                    "                    <candidate xmlns=\"urn:xmpp:jingle:transports:ice-udp:1\" component=\"1\" foundation=\"3\" generation=\"0\" id=\"5a36a5003cdd395c8a7d872659dacca0cf362b\" network=\"0\" priority=\"2130706431\" protocol=\"udp\" type=\"host\" ip=\"172.18.10.26\" port=\"10000\"/>\r\n" +
	                    "                    <candidate xmlns=\"urn:xmpp:jingle:transports:ice-udp:1\" component=\"1\" foundation=\"2\" generation=\"0\" id=\"5a36a5003cdd395c8a7d872659dacca023ed4177\" network=\"0\" priority=\"1694498815\" protocol=\"ssltcp\" tcptype=\"passive\" type=\"srflx\" ip=\"54.89.50.245\" port=\"4443\" rel-addr=\"172.18.10.26\" rel-port=\"4443\"/>\r\n" +
	                    "                    <candidate xmlns=\"urn:xmpp:jingle:transports:ice-udp:1\" component=\"1\" foundation=\"4\" generation=\"0\" id=\"5a36a5003cdd395c8a7d872659dacca06cff057e\" network=\"0\" priority=\"1677724415\" protocol=\"udp\" type=\"srflx\" ip=\"54.89.50.245\" port=\"10000\" rel-addr=\"172.18.10.26\" rel-port=\"10000\"/>AA:C1:BA:15:EA:8C:0A:8A:06:35:80:65:1C:2F:52:8E:24:E7:87:7A</transport>\r\n" +
	                    "                    <description xmlns=\"urn:xmpp:jingle:apps:rtp:1\" media=\"audio\">\r\n" +
	                    "                        <payload-type xmlns=\"urn:xmpp:jingle:apps:rtp:1\" id=\"111\" name=\"opus\" clockrate=\"48000\" channels=\"2\">\r\n" +
	                    "                            <parameter xmlns=\"urn:xmpp:jingle:apps:rtp:1\" name=\"minptime\" value=\"10\"/>\r\n" +
	                    "                        </payload-type>\r\n" +
	                    "                        <payload-type xmlns=\"urn:xmpp:jingle:apps:rtp:1\" id=\"103\" name=\"ISAC\" clockrate=\"16000\"/>\r\n" +
	                    "                        <payload-type xmlns=\"urn:xmpp:jingle:apps:rtp:1\" id=\"104\" name=\"ISAC\" clockrate=\"32000\"/>\r\n" +
	                    "                        <payload-type xmlns=\"urn:xmpp:jingle:apps:rtp:1\" id=\"9\" name=\"G722\" clockrate=\"8000\"/>\r\n" +
	                    "                        <payload-type xmlns=\"urn:xmpp:jingle:apps:rtp:1\" id=\"0\" name=\"PCMU\" clockrate=\"8000\"/>\r\n" +
	                    "                        <payload-type xmlns=\"urn:xmpp:jingle:apps:rtp:1\" id=\"8\" name=\"PCMA\" clockrate=\"8000\"/>\r\n" +
	                    "                        <rtp-hdrext xmlns=\"urn:xmpp:jingle:apps:rtp:rtp-hdrext:0\" id=\"1\" uri=\"urn:ietf:params:rtp-hdrext:ssrc-audio-level\"/>:\r\n" +
	                    "                        <rtp-hdrext xmlns=\"urn:xmpp:jingle:apps:rtp:rtp-hdrext:0\" id=\"3\" uri=\"http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\"/>\r\n" +
	                    "                        <rtcp-mux xmlns=\"urn:xmpp:jingle:apps:rtp:1\"/>\r\n" +
	                    "                    </description>\r\n" +
	                    "                </content>\r\n" +
	                    "                <content name=\"data\" senders=\"initiator\">\r\n" +
	                    "                    <transport xmlns=\"urn:xmpp:jingle:transports:ice-udp:1\" pwd=\"1bglu5r23o02u492g32p57smnp\" ufrag=\"2b66n19i3ol7gv\">\r\n" +
	                    "                        <candidate xmlns=\"urn:xmpp:jingle:transports:ice-udp:1\" component=\"1\" foundation=\"1\" generation=\"0\" id=\"5a36a5003cdd395c8a7d872659dacca052bb35e4\" network=\"0\" priority=\"2130706431\" protocol=\"ssltcp\" tcptype=\"passive\" type=\"host\" ip=\"172.18.10.26\" port=\"4443\"/>\r\n" +
	                    "                        <candidate xmlns=\"urn:xmpp:jingle:transports:ice-udp:1\" component=\"1\" foundation=\"3\" generation=\"0\" id=\"5a36a5003cdd395c8a7d872659dacca0cf362b\" network=\"0\" priority=\"2130706431\" protocol=\"udp\" type=\"host\" ip=\"172.18.10.26\" port=\"10000\"/>\r\n" +
	                    "                        <candidate xmlns=\"urn:xmpp:jingle:transports:ice-udp:1\" component=\"1\" foundation=\"2\" generation=\"0\" id=\"5a36a5003cdd395c8a7d872659dacca023ed4177\" network=\"0\" priority=\"1694498815\" protocol=\"ssltcp\" tcptype=\"passive\" type=\"srflx\" ip=\"54.89.50.245\" port=\"4443\" rel-addr=\"172.18.10.26\" rel-port=\"4443\"/>\r\n" +
	                    "                        <candidate xmlns=\"urn:xmpp:jingle:transports:ice-udp:1\" component=\"1\" foundation=\"4\" generation=\"0\" id=\"5a36a5003cdd395c8a7d872659dacca06cff057e\" network=\"0\" priority=\"1677724415\" protocol=\"udp\" type=\"srflx\" ip=\"54.89.50.245\" port=\"10000\" rel-addr=\"172.18.10.26\" rel-port=\"10000\"/>AA:C1:BA:15:EA:8C:0A:8A:06:35:80:65:1C:2F:52:8E:24:E7:87:7A</transport>\r\n" +
	                    "                    </content>\r\n" +
	                    "                </jingle>\r\n";
	    
	    XmlPullParser parser = new MXParser();
	    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
	    parser.setInput(new StringReader(jingleIqString));
	    parser.next();
	    JingleIQ iq = new JingleIQProvider().parseIQ(parser);
	    SessionDescription sdp = SdpToJingle.sdpFromJingle(iq);
	    Assert.assertNotNull(sdp);
	    String text = sdp.toString();
	    Assert.assertTrue(text.contains("a=rtpmap:111 opus/48000"));
	    Assert.assertTrue(text.contains("m=audio 9 RTP/SAVPF 111 103 104 9 0 8"));
	    Assert.assertTrue(text.contains("a=rtpmap:100 VP8/90000"));
	    Assert.assertTrue(text.contains("a=rtcp-mux"));
	}

}
