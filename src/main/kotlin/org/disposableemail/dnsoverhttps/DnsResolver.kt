package org.disposableemail.dnsoverhttps

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.disposableemail.Configurations
import java.io.IOException
import kotlin.jvm.Throws

class DnsResolver {
    companion object {
        private val client = OkHttpClient()
        private val gson: Gson = Gson()

        @Throws(IOException::class)
        fun isMxRecordPresent(domain: String, dnsResolver: DNS_RESOLVER_TYPE): Boolean {
            val url = "${getResolverUrl(dnsResolver)}?type=MX&name=$domain"
            val request = Request.Builder()
                .url(url)
                .addHeader("Accept", "application/dns-json")
                .build();
            val response = client.newCall(request).execute()
            response.body?.let {
                val body = it.string()
                val resp = gson.fromJson(body, DnsResponse::class.java)
                return !resp.Answer.isNullOrEmpty()
            }
            return false
        }

        private fun getResolverUrl(dnsResolver: DNS_RESOLVER_TYPE): String {
            return if (dnsResolver == DNS_RESOLVER_TYPE.CLOUD_FLARE)
                Configurations.CLOUD_FLARE_DNS_RESOLVER_URL
            else
                Configurations.GOOGLE_DNS_RESOLVER_URL
        }
    }
}

enum class DNS_RESOLVER_TYPE {
    CLOUD_FLARE,
    GOOGLE
}
