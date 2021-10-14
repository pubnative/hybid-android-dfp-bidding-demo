package net.pubnative.hybiddfpbiddingdemo.ui.banner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView
import net.pubnative.hybiddfpbiddingdemo.R
import net.pubnative.lite.sdk.api.BannerRequestManager
import net.pubnative.lite.sdk.api.RequestManager
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.utils.HeaderBiddingUtils

class BannerFragment : Fragment(R.layout.fragment_banner), RequestManager.RequestListener {
    val TAG = BannerFragment::class.java.simpleName

    private lateinit var requestManager: RequestManager
    private lateinit var loadButton: Button
    private lateinit var dfpBanner: PublisherAdView
    private lateinit var dfpBannerContainer: FrameLayout

    private val zoneId: String = "2"
    private val dfpAdUnitId = "/219576711/pnlite_dfp_banner"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dfpBannerContainer = view.findViewById(R.id.dfp_banner_container)
        loadButton = view.findViewById(R.id.button_load)

        requestManager = BannerRequestManager()

        dfpBanner = PublisherAdView(activity)
        dfpBanner.adUnitId = dfpAdUnitId
        dfpBanner.setAdSizes(AdSize.BANNER)
        dfpBanner.adListener = adListener

        dfpBannerContainer.addView(dfpBanner)

        loadButton.setOnClickListener {
            loadBanner()
        }
    }

    override fun onDestroy() {
        dfpBanner.destroy()
        super.onDestroy()
    }

    private fun loadBanner() {
        requestManager.setZoneId(zoneId)
        requestManager.setRequestListener(this)
        requestManager.requestAd()
    }

    // -------------------- RequestManager's Listeners ------------------------
    override fun onRequestSuccess(ad: Ad?) {
        val builder = PublisherAdRequest.Builder()

        val keywordSet: Set<String> =
            HeaderBiddingUtils.getHeaderBiddingKeywordsSet(
                ad,
                HeaderBiddingUtils.KeywordMode.TWO_DECIMALS
            )
        for (key in keywordSet) {
            builder.addKeyword(key)
        }

        val keywordBundle =
            HeaderBiddingUtils.getHeaderBiddingKeywordsBundle(
                ad,
                HeaderBiddingUtils.KeywordMode.TWO_DECIMALS
            )
        for (key in keywordBundle.keySet()) {
            builder.addCustomTargeting(key, keywordBundle.getString(key))
        }

        val adRequest = builder.build()
        dfpBanner.loadAd(adRequest)

        Log.d(TAG, "onRequestSuccess")
    }

    override fun onRequestFail(throwable: Throwable?) {
        Log.d(TAG, "onRequestFail: ", throwable)
    }

    // ---------------- DFP Ad Listener ---------------------
    private val adListener = object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            Log.d(TAG, "onAdLoaded")
        }

        override fun onAdFailedToLoad(errorCode: Int) {
            super.onAdFailedToLoad(errorCode)
            Log.d(TAG, "onAdFailedToLoad")
        }

        override fun onAdImpression() {
            super.onAdImpression()
            Log.d(TAG, "onAdImpression")
        }

        override fun onAdClicked() {
            super.onAdClicked()
            Log.d(TAG, "onAdClicked")
        }

        override fun onAdOpened() {
            super.onAdOpened()
            Log.d(TAG, "onAdOpened")
        }

        override fun onAdLeftApplication() {
            super.onAdLeftApplication()
            Log.d(TAG, "onAdLeftApplication")
        }

        override fun onAdClosed() {
            super.onAdClosed()
            Log.d(TAG, "onAdClosed")
        }
    }

}