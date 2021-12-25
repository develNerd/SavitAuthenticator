package org.savit.savitauthenticator.utils.otp

import com.google.common.collect.Maps
import kotlin.experimental.or

class Base32String {

    companion object{
        private const val SEPARATOR = "-"
        private val DIGITS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray()
        private val MASK = DIGITS.size - 1
        private val SHIFT = Integer.numberOfTrailingZeros(DIGITS.size)
        private val CHAR_MAP: MutableMap<Char, Int> = Maps.newHashMapWithExpectedSize(DIGITS.size)

      /*  @Throws(DecodingException::class)
        fun decode(encoded: String): ByteArray {
            // Remove whitespace and separators
            var encoded = encoded
            encoded = encoded.trim { it <= ' ' }
                .replace(SEPARATOR.toRegex(), "").replace(" ".toRegex(), "")

            // Remove padding. Note: the padding is used as hint to determine how many
            // bits to decode from the last incomplete chunk (which is commented out
            // below, so this may have been wrong to start with).
            encoded = encoded.replaceFirst("[=]*$".toRegex(), "")

            // Canonicalize to all upper case
            encoded = encoded.toUpperCase(Locale.US)
            if (encoded.length == 0) {
                return ByteArray(0)
            }
            val encodedLength = encoded.length
            val outLength = encodedLength * SHIFT / 8
            val result = ByteArray(outLength)
            var buffer = 0
            var next = 0
            var bitsLeft = 0
            for (c in encoded.toCharArray()) {
                if (!CHAR_MAP.containsKey(c)) {
                    throw DecodingException("Illegal character: $c")
                }
                buffer = buffer shl SHIFT
                buffer = buffer or (CHAR_MAP[c]!! and MASK)
                bitsLeft += SHIFT
                if (bitsLeft >= 8) {
                    result[next++] = (buffer shr bitsLeft - 8).toByte()
                    bitsLeft -= 8
                }
            }
            // We'll ignore leftover bits for now.
            //
            // if (next != outLength || bitsLeft >= SHIFT) {
            //  throw new DecodingException("Bits left: " + bitsLeft);
            // }
            return result
        }*/

        @Throws(DecodingException::class)
        fun decode(base32: String): ByteArray {
            var i = 0
            var index = 0
            var lookup: Int
            var offset = 0
            var digit: Int
            val bytes = ByteArray(base32.length * 5 / 8)

            while (i < base32.length) {
                lookup = base32[i] - '0'

                // Skip chars outside the lookup table
                if (lookup < 0 || lookup >= base32Lookup.size) {
                    i++
                    continue
                }

                digit = base32Lookup[lookup]

                // If this digit is not in the table, ignore it
                if (digit == 0xFF) {
                    i++
                    continue
                }

                if (index <= 3) {
                    index = (index + 5) % 8
                    if (index == 0) {
                        bytes[offset] = bytes[offset] or digit.toByte()
                        offset++
                        if (offset >= bytes.size) break
                    } else {
                        bytes[offset] = bytes[offset] or (digit shl 8 - index).toByte()
                    }
                } else {
                    index = (index + 5) % 8
                    bytes[offset] = bytes[offset] or digit.ushr(index).toByte()
                    offset++

                    if (offset >= bytes.size) break
                    bytes[offset] = bytes[offset] or (digit shl 8 - index).toByte()
                }
                i++
            }
            return bytes
        }

        const val ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"
        val base32Lookup = intArrayOf(
            0xFF, 0xFF, 0x1A, 0x1B, 0x1C, 0x1D, 0x1E, 0x1F,
            0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
            0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,
            0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,
            0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF,
            0xFF, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
            0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,
            0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16,
            0x17, 0x18, 0x19, 0xFF, 0xFF, 0xFF, 0xFF, 0xFF
        )


    }




    init {
        for (i in DIGITS.indices) {
            CHAR_MAP[DIGITS[i]] = i
        }
    }

    class DecodingException(message: String?) : Exception(message)

}