package de.fhg.ids.comm.ws.protocol.rat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.protobuf.MessageLite;

import de.fhg.aisec.ids.messages.Idscp.Pcr;
import de.fhg.ids.comm.ws.protocol.fsm.Event;

public class RemoteAttestationHandler {


	protected boolean attestationSuccessful(boolean signatureCorrect, Pcr[] pcrValues) {
		return signatureCorrect && TrustedThirdParty.pcrValuesCorrect(pcrValues);
	}
	
	// fetch a public key from a uri and return the key as a byte array
	protected byte[] fetchPublicKey(String uri) throws Exception {
		URL cert = new URL(uri);
		BufferedReader in = new BufferedReader(new InputStreamReader(cert.openStream()));
		String base64 = "";
		String inputLine = "";
        while ((inputLine = in.readLine()) != null) {
        	base64 += inputLine;
        }
        in.close();
        return javax.xml.bind.DatatypeConverter.parseBase64Binary(base64);
	}
}