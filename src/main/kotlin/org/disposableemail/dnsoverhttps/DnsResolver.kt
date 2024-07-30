package org.disposableemail.dnsoverhttps

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.disposableemail.Configurations

class DnsResolver {
    companion object {
        private val client = OkHttpClient()
        private val gson: Gson = Gson()


        fun resolveEmailDomain(domain: String): Boolean {
            val request = Request.Builder()
                .url(Configurations.CLOUD_FLARE_DNS_RESOLVER_URL)
                .build();

            val response = client.newCall(request).execute()
            response.body?.let {
                val response = gson.fromJson(it.string(), DnsResponse::class.java)
                return response.Answer != null
            }
            return false
        }
    }
}
