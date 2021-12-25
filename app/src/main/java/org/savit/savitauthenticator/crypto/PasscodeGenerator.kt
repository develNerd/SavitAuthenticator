package org.savit.savitauthenticator.crypto

import java.io.ByteArrayInputStream
import java.io.DataInput
import java.io.DataInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.security.GeneralSecurityException
import javax.crypto.Mac
import kotlin.experimental.and

public class PasscodeGenerator {

    companion object{
        private val MAX_PASSCODE_LENGTH:Int = 9

        private val PASS_CODE_LENGTH:Int = 6

        private val ADJACENT_INTERVALS:Int = 1

        private val DIGITS_POWER:IntArray = intArrayOf(1, 10, 100, 1000, 10000, 100000,
            1000000, 10000000, 100000000, 1000000000)
    }






    private var signer: Signer? = null
    private var codeLength = 0

    interface Signer {
        /**
         * @param data Preimage to sign, represented as sequence of arbitrary bytes
         * @return Signature as sequence of bytes.
         * @throws GeneralSecurityException
         */
        @Throws(GeneralSecurityException::class)
        fun sign(data: ByteArray?): ByteArray?
    }

    constructor(mac: Mac):this(mac, PASS_CODE_LENGTH){

    }

    constructor(signer: Signer):this(signer, PASS_CODE_LENGTH){

    }


    constructor(mac: Mac,passCodeLength: Int) : this(object : Signer {
        override fun sign(data: ByteArray?): ByteArray? {
            return mac.doFinal(data)
        }
    },passCodeLength){

    }

   constructor(signer: Signer, passCodeLength: Int) {
        require(!(passCodeLength < 0 || passCodeLength > MAX_PASSCODE_LENGTH)) { "PassCodeLength must be between 1 and " + MAX_PASSCODE_LENGTH + " digits." }
        this.signer = signer
        codeLength = passCodeLength
    }


    private fun padOutput(value: Int): String {
        var result = value.toString()
        for (i in result.length until codeLength) {
            result = "0$result"
        }
        return result
    }

    @Throws(GeneralSecurityException::class)
    fun generateResponseCode(state: Long): String? {
        val value = ByteBuffer.allocate(8).putLong(state).array()
        return generateResponseCode(value)
    }

    @Throws(GeneralSecurityException::class)
    fun generateResponseCode(state: Long, challenge: ByteArray?): String? {
        return if (challenge == null) {
            generateResponseCode(state)
        } else {
            // Allocate space for combination and store.
            val value = ByteBuffer.allocate(8 + challenge.size)
                .putLong(state) // Write out OTP state
                .put(challenge, 0, challenge.size) // Concatenate with challenge.
                .array()
            generateResponseCode(value)
        }
    }

    @Throws(GeneralSecurityException::class)
    fun generateResponseCode(challenge: ByteArray?): String? {
        val hash = signer!!.sign(challenge)

        // Dynamically truncate the hash
        // OffsetBits are the low order bits of the last byte of the hash

        // Dynamically truncate the hash
        // OffsetBits are the low order bits of the last byte of the hash
        val offset = (hash!![hash.size - 1] and 0xF).toInt()
        // Grab a positive integer value starting at the given offset.
        val truncatedHash: Int = hashToInt(hash, offset) and 0x7FFFFFFF
        val pinValue: Int = truncatedHash % DIGITS_POWER[codeLength]
        return padOutput(pinValue)
    }


    private fun hashToInt(bytes: ByteArray, start: Int): Int {
        val input: DataInput = DataInputStream(
            ByteArrayInputStream(bytes, start, bytes.size - start)
        )
        return try {
            input.readInt()
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    @Throws(GeneralSecurityException::class)
    fun verifyResponseCode(challenge: Long, response: String): Boolean {
        val expectedResponse = generateResponseCode(challenge, null)
        return expectedResponse == response
    }

    @Throws(GeneralSecurityException::class)
    fun verifyTimeoutCode(currentInterval: Long, timeoutCode: String?): Boolean {
        return verifyTimeoutCode(
            timeoutCode!!, currentInterval,
            ADJACENT_INTERVALS, ADJACENT_INTERVALS
        )
    }

    @Throws(GeneralSecurityException::class)
    fun verifyTimeoutCode(
        timeoutCode: String,
        currentInterval: Long,
        pastIntervals: Int,
        futureIntervals: Int
    ): Boolean {
        // Ensure that look-ahead and look-back counts are not negative.
        var pastIntervals = pastIntervals
        var futureIntervals = futureIntervals
        pastIntervals = Math.max(pastIntervals, 0)
        futureIntervals = Math.max(futureIntervals, 0)

        // Try upto "pastIntervals" before current time, and upto "futureIntervals" after.
        for (i in -pastIntervals..futureIntervals) {
            val candidate = generateResponseCode(currentInterval - i, null)
            if (candidate == timeoutCode) {
                return true
            }
        }
        return false
    }



}