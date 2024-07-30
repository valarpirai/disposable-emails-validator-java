package org.disposableemail.dnsoverhttps

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.disposableemail.Configurations

class DnsResolver {
    companion object {
        private val client = OkHttpClient()
        private val gson: Gson = Gson()

        fun verifyMxRecordPresent(domain: String, dnsResolver: DNS_RESOLVER_TYPE): Boolean {
            val request = Request.Builder()
                .url(getResolverUrl(dnsResolver))
                .build();

            val response = client.newCall(request).execute()
            response.body?.let {
                val resp = gson.fromJson(it.string(), DnsResponse::class.java)
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
