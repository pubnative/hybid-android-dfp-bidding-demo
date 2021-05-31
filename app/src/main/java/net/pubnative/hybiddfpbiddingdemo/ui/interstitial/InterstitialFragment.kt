package net.pubnative.hybiddfpbiddingdemo.ui.interstitial

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd
import net.pubnative.hybiddfpbiddingdemo.R
import net.pubnative.lite.sdk.api.InterstitialRequestManager
import net.pubnative.lite.sdk.api.RequestManager
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.utils.HeaderBiddingUtils


class InterstitialFragment : Fragment(), RequestManager.RequestListener {
    val TAG = InterstitialFragment::class.java.simpleName

    private lateinit var requestManager: RequestManager
    private lateinit var dfpInterstitial: PublisherInterstitialAd

    private lateinit var loadButton: Button
    private lateinit var showButton: Button

    private val zoneId: String = "3"
    private var dfpAdUnitId: String = "/219576711/pnlite_dfp_interstitial"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_interstitial, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadButton = view.findViewById(R.id.button_load)
        showButton = view.findViewById(R.id.button_show)

        requestManager = InterstitialRequestManager()

        dfpInterstitial = PublisherInterstitialAd(activity)
        dfpInterstitial.adUnitId = dfpAdUnitId
        dfpInterstitial.adListener = adListener

        loadButton.setOnClickListener {
            loadInterstitial()
        }

        showButton.setOnClickListener {
            dfpInterstitial.show()
        }

    }

    override fun onDestroy() {
        dfpInterstitial.show()
        super.onDestroy()
    }

    fun loadInterstitial() {
        requestManager.setZoneId(zoneId)
        requestManager.setRequestListener(this)
        requestManager.requestAd()
    }

    // -------------------- RequestManager's Listeners ------------------------
    override fun onRequestSuccess(ad: Ad?) {
        Log.d(TAG, "onRequestSuccess")

        val builder = PublisherAdRequest.Builder()

        val keywordSet: Set<String> =
            HeaderBiddingUtils.getHeaderBiddingKeywordsSet(ad, HeaderBiddingUtils.KeywordMode.TWO_DECIMALS)
        for (key in keywordSet) {
            builder.addKeyword(key)
        }

        val keywordBundle: Bundle =
            HeaderBiddingUtils.getHeaderBiddingKeywordsBundle(ad, HeaderBiddingUtils.KeywordMode.TWO_DECIMALS)
        for (key in keywordBundle.keySet()) {
            builder.addCustomTargeting(key, keywordBundle.getString(key))
        }

        val adRequest = builder.build()
        dfpInterstitial.loadAd(adRequest)
    }

    override fun onRequestFail(throwable: Throwable?) {
        Log.d(TAG, "onRequestFail", throwable)
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