/**
 * Copyright 2017 Carlos Macasaet
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hiddenservices.onionservices.libs.fernet;

import static com.hiddenservices.onionservices.libs.fernet.Constants.charset;
import static com.hiddenservices.onionservices.libs.fernet.Constants.cipherTextBlockSize;
import static com.hiddenservices.onionservices.libs.fernet.Constants.initializationVectorBytes;
import static com.hiddenservices.onionservices.libs.fernet.Constants.minimumTokenBytes;
import static com.hiddenservices.onionservices.libs.fernet.Constants.signatureBytes;
import static com.hiddenservices.onionservices.libs.fernet.Constants.supportedVersion;
import static com.hiddenservices.onionservices.libs.fernet.Constants.tokenStaticBytes;

import android.util.Base64;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import javax.crypto.spec.IvParameterSpec;

/**
 * A Fernet token.
 *
 * <p>Copyright &copy; 2017 Carlos Macasaet.</p>
 *
 * @author Carlos Macasaet
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals"})
/*
 * TooManyMethods can be avoided by making the following API-breaking changes:
 * * remove the static `generate` methods and introduce a `TokenFactory` or `TokenBuilder`
 * * remove the public `validateAndDecrypt` methods since they are already available in the `Validator` interface
 *
 * AvoidDuplicateLiterals is from the method-level @SuppressWarnings annotations
 */
public class Token {

    private final byte version;
    private final Instant timestamp;
    private final IvParameterSpec initializationVector;
    private final byte[] cipherText;
    private final byte[] hmac;

    /**
     * <p>Initialise a new Token from raw components. No validation of the signature is performed. However, the other
     * fields are validated to ensure they conform to the Fernet specification.</p>
     *
     * <p>Warning: Subsequent modifications to the input arrays will write through to this object.</p>
     *
     * @param version
     *            The version of the Fernet token specification. Currently, only 0x80 is supported.
     * @param timestamp
     *            the time the token was generated
     * @param initializationVector
     *            the randomly-generated bytes used to initialise the encryption cipher
     * @param cipherText
     *            the encrypted the encrypted payload
     * @param hmac
     *            the signature of the token
     */
    @SuppressWarnings({"PMD.ArrayIsStoredDirectly", "PMD.CyclomaticComplexity"})
    protected Token(final byte version, final Instant timestamp, final IvParameterSpec initializationVector,
                    final byte[] cipherText, final byte[] hmac) {
        if (version != supportedVersion) {
            throw new IllegalTokenException("Unsupported version: " + version);
        }
        if (initializationVector == null || initializationVector.getIV().length != initializationVectorBytes) {
            throw new IllegalTokenException("Initialization Vector must be 128 bits");
        }
        if (cipherText == null || cipherText.length % cipherTextBlockSize != 0) {
            throw new IllegalTokenException("Ciphertext must be a multiple of 128 bits");
        }
        if (hmac == null || hmac.length != signatureBytes) {
            throw new IllegalTokenException("hmac must be 256 bits");
        }
        this.version = version;
        this.timestamp = timestamp;
        this.initializationVector = initializationVector;
        this.cipherText = cipherText;
        this.hmac = hmac;
    }

    @SuppressWarnings({"PMD.PrematureDeclaration", "PMD.DataflowAnomalyAnalysis"})
    public static Token fromBytes(final byte[] bytes) {
        if (bytes.length < minimumTokenBytes) {
            throw new IllegalTokenException("Not enough bits to generate a Token");
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            try (DataInputStream dataStream = new DataInputStream(inputStream)) {
                final byte version = dataStream.readByte();
                final long timestampSeconds = dataStream.readLong();

                final byte[] initializationVector = read(dataStream, initializationVectorBytes);
                final byte[] cipherText = read(dataStream, bytes.length - tokenStaticBytes);
                final byte[] hmac = read(dataStream, signatureBytes);

                if (dataStream.read() != -1) {
                    throw new IllegalTokenException("more bits found");
                }

                return new Token(version, null,
                        new IvParameterSpec(initializationVector), cipherText, hmac);
            }
        } catch (final IOException ioe) {
            // this should not happen as I/O is from memory and stream
            // length is verified ahead of time
            throw new IllegalStateException(ioe.getMessage(), ioe);
        }
    }

    protected static byte[] read(final DataInputStream stream, final int numBytes) throws IOException {
        final byte[] retval = new byte[numBytes];
        final int bytesRead = stream.read(retval);
        if (bytesRead < numBytes) {
            throw new IllegalTokenException("Not enough bits to generate a Token");
        }
        return retval;
    }

    public static Token fromString(final String string) {
        return fromBytes(android.util.Base64.decode(string, Base64.DEFAULT));
    }

    /**
     * Convenience method to generate a new Fernet token with a string payload.
     *
     * @param key the secret key for encrypting <em>plainText</em> and signing the token
     * @param plainText the payload to embed in the token
     * @return a unique Fernet token
     */
    public static Token generate(final com.hiddenservices.onionservices.libs.fernet.Key key, final String plainText) {
        return generate(new SecureRandom(), key, plainText);
    }

    /**
     * Convenience method to generate a new Fernet token with a string payload.
     *
     * @param random a source of entropy for your application
     * @param key the secret key for encrypting <em>plainText</em> and signing the token
     * @param plainText the payload to embed in the token
     * @return a unique Fernet token
     */
    public static Token generate(final SecureRandom random, final com.hiddenservices.onionservices.libs.fernet.Key key, final String plainText) {
        return generate(random, key, plainText.getBytes(charset));
    }

    /**
     * Convenience method to generate a new Fernet token.
     *
     * @param key the secret key for encrypting <em>payload</em> and signing the token
     * @param payload the unencrypted data to embed in the token
     * @return a unique Fernet token
     */
    public static Token generate(final com.hiddenservices.onionservices.libs.fernet.Key key, final byte[] payload) {
        return generate(new SecureRandom(), key, payload);
    }

    /**
     * Generate a new Fernet token.
     *
     * @param random a source of entropy for your application
     * @param key the secret key for encrypting <em>payload</em> and signing the token
     * @param payload the unencrypted data to embed in the token
     * @return a unique Fernet token
     */
    public static Token generate(final SecureRandom random, final com.hiddenservices.onionservices.libs.fernet.Key key, final byte[] payload) {
        final IvParameterSpec initializationVector = generateInitializationVector(random);
        final byte[] cipherText = key.encrypt(payload, initializationVector);
        final Instant timestamp = null;
        final byte[] hmac = key.sign(supportedVersion, timestamp, initializationVector, cipherText);
        return new Token(supportedVersion, timestamp, initializationVector, cipherText, hmac);
    }

    /**
     * Check the validity of this token. 
     *
     * @param key the secret key against which to validate the token
     * @param validator an object that encapsulates the validation parameters (e.g. TTL)
     * @return the decrypted, deserialised payload of this token
     * @throws TokenValidationException if <em>key</em> was NOT used to generate this token
     */

    /**
     * Check the validity of this token against a collection of keys. Use this if you have implemented key rotation.
     *
     * @param keys the active keys which may have been used to generate token
     * @param validator an object that encapsulates the validation parameters (e.g. TTL)
     * @return the decrypted, deserialised payload of this token
     * @throws TokenValidationException if none of the keys were used to generate this token
     */

    /**
     * @return the Base 64 URL encoding of this token in the form Version | Timestamp | IV | Ciphertext | HMAC
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public String serialise() {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(
                tokenStaticBytes + getCipherText().length)) {
            writeTo(byteStream);
            return android.util.Base64.encodeToString(byteStream.toByteArray(), Base64.DEFAULT);
        } catch (final IOException e) {
            // this should not happen as IO is to memory only
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Write the raw bytes of this token to the specified output stream.
     *
     * @param outputStream
     *            the target
     * @throws IOException
     *             if data cannot be written to the underlying stream
     */
    @SuppressWarnings("PMD.LawOfDemeter")
    public void writeTo(final OutputStream outputStream) throws IOException {
        try (DataOutputStream dataStream = new DataOutputStream(outputStream)) {
            long mTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
            dataStream.writeByte(getVersion());
            dataStream.writeLong(mTime);
            dataStream.write(getInitializationVector().getIV());
            dataStream.write(getCipherText());
            dataStream.write(getHmac());
        }
    }

    /**
     * @return the Fernet specification version of this token
     */
    public byte getVersion() {
        return version;
    }

    /**
     * @return the time that this token was generated
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * @return the initialisation vector used to encrypt the token contents
     */
    public IvParameterSpec getInitializationVector() {
        return initializationVector;
    }

    public String toString() {
        final StringBuilder builder = new StringBuilder(107);
        builder.append("Token [version=").append(String.format("0x%x", new BigInteger(1, new byte[]{getVersion()})))
                .append(", timestamp=").append(getTimestamp())
                .append(", hmac=").append(android.util.Base64.encodeToString(getHmac(), Base64.DEFAULT)).append(']');
        return builder.toString();
    }

    protected static IvParameterSpec generateInitializationVector(final SecureRandom random) {
        return new IvParameterSpec(generateInitializationVectorBytes(random));
    }

    protected static byte[] generateInitializationVectorBytes(final SecureRandom random) {
        final byte[] retval = new byte[initializationVectorBytes];
        random.nextBytes(retval);
        return retval;
    }

    /**
     * Recompute the HMAC signature of the token with the stored shared secret key.
     *
     * @param key
     *            the shared secret key against which to validate the token
     * @return true if and only if the signature on the token was generated using the supplied key
     */
    public boolean isValidSignature(final Key key) {
        final byte[] computedHmac = key.sign(getVersion(), getTimestamp(), getInitializationVector(),
                getCipherText());
        return MessageDigest.isEqual(getHmac(), computedHmac);
    }

    /**
     * Warning: modifications to the returned array will write through to this object.
     *
     * @return the raw encrypted payload bytes
     */
    @SuppressWarnings("PMD.MethodReturnsInternalArray")
    protected byte[] getCipherText() {
        return cipherText;
    }

    /**
     * Warning: modifications to the returned array will write through to this object.
     *
     * @return the HMAC 256 signature of this token
     */
    @SuppressWarnings("PMD.MethodReturnsInternalArray")
    protected byte[] getHmac() {
        return hmac;
    }

    public byte[] validateAndDecrypt(final com.hiddenservices.onionservices.libs.fernet.Key key) {
        return key.decrypt(getCipherText(), getInitializationVector());
    }

}