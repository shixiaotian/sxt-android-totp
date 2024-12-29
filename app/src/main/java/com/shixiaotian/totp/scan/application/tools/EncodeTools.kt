package com.shixiaotian.totp.scan.application.tools

import com.shixiaotian.totp.scan.application.vo.User
import org.jboss.aerogear.security.otp.Totp


class EncodeTools {

    companion object {
        @JvmStatic
        fun encode(
            secretKey: String,
            timeStep: Long = 30,
            digits: Int = 6,
            algorithm: String = "SHA1"
        ): String? {

            if (secretKey == null || secretKey.isBlank()) {
                return ""
            }
            try {
                val totp = Totp(secretKey);
                var result = totp.now();
                return result;
            } catch (e: Exception) {
                return "ERROR SK"
            }
        }

        @JvmStatic
        fun decode(uri: String): User? {

            if(uri.isEmpty()) {
                return null
            }

            if(!uri.startsWith("otpauth://totp/")) {
                return null
            }
            try {

                var uriContentIndex = uri.indexOf("otpauth://totp/");

                var uriContent = uri.subSequence(15, uri.length);
                val secContents = uriContent.split(":");

                var issuer = secContents.get(0);
                var otherContent = secContents.get(1)
                val secOtherContent= otherContent.split("?")
                var username = secOtherContent.get(0)

                var thOtherContent = secOtherContent.get(1)
                val fthOtherContent = thOtherContent.split("&")

                var secretKeyContent = fthOtherContent.get(0)
                var secretKey = secretKeyContent.split("=").get(1)
                var user = User(0, username, secretKey, issuer)
                return user
            }catch (e: Exception) {
                return null
            }
        }
    }

}