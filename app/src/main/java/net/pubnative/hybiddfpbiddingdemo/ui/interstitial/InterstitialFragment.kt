package net.pubnative.hybiddfpbiddingdemo.ui.interstitial

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd
import net.pubnative.hybiddfpbiddingdemo.R
import net.pubnative.lite.sdk.api.InterstitialRequestManager
import net.pubnative.lite.sdk.api.RequestManager
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.utils.HeaderBiddingUtils


class InterstitialFragment : Fragment(R.layout.fragment_interstitial),
    RequestManager.RequestListener {
    val TAG = InterstitialFragment::class.java.simpleName

    private lateinit var requestManager: RequestManager
    private lateinit var dfpInterstitial: PublisherInterstitialAd

    private lateinit var loadButton: Button
    private lateinit var prepareButton: Button
    private lateinit var showButton: Button
    private lateinit var cachingCheckbox: CheckBox

    private val zoneId: String = "3"
    private var dfpAdUnitId: String = "/219576711/pnlite_dfp_interstitial"
    private var cachingEnabled: Boolean = true
    private var ad: Ad? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadButton = view.findViewById(R.id.button_load)
        prepareButton = view.findViewById(R.id.button_prepare)
        showButton = view.findViewById(R.id.button_show)
        cachingCheckbox = view.findViewById(R.id.check_caching)
        prepareButton.isEnabled = false
        showButton.isEnabled = false

        requestManager = InterstitialRequestManager()

        dfpInterstitial = PublisherInterstitialAd(activity)
        dfpInterstitial.adUnitId = dfpAdUnitId
        dfpInterstitial.adListener = adListener

        loadButton.setOnClickListener {
            loadInterstitial()
        }

        prepareButton.setOnClickListener {
            ad?.let { ad ->
                requestManager.cacheAd(ad)
            }
        }

        showButton.setOnClickListener {
            dfpInterstitial.show()
        }

        cachingCheckbox.setOnCheckedChangeListener { _, isChecked ->
            cachingEnabled = isChecked
            prepareButton.visibility = if (isChecked) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroy() {
        dfpInterstitial.show()
        super.onDestroy()
    }

    fun loadInterstitial() {
        requestManager.setZoneId(zoneId)
        requestManager.setRequestListener(this)
        requestManager.isAutoCacheOnLoad = cachingEnabled
        requestManager.requestAd()
    }

    // -------------------- RequestManager's Listeners ------------------------
    override fun onRequestSuccess(ad: Ad?) {
        Log.d(TAG, "onRequestSuccess")
        this.ad = ad

        val builder = PublisherAdRequest.Builder()

        val keywordSet: Set<String> =
            HeaderBiddingUtils.getHeaderBiddingKeywordsSet(
                ad,
                HeaderBiddingUtils.KeywordMode.TWO_DECIMALS
            )
        for (key in keywordSet) {
            builder.addKeyword(key)
        }

        val keywordBundle: Bundle =
            HeaderBiddingUtils.getHeaderBiddingKeywordsBundle(
                ad,
                HeaderBiddingUtils.KeywordMode.TWO_DECIMALS
            )
        for (key in keywordBundle.keySet()) {
            builder.addCustomTargeting(key, keywordBundle.getString(key))
        }

        val adRequest = builder.build()
        dfpInterstitial.loadAd(adRequest)
    }

    override fun onRequestFail(throwable: Throwable?) {
        Log.d(TAG, "onRequestFail", throwable)
        ad = null

        val adRequest = PublisherAdRequest.Builder().build()
        dfpInterstitial.loadAd(adRequest)
    }

    // ---------------- DFP Ad Listener ---------------------
    private val adListener = object : AdListener() {
        override fun onAdLoaded() {
            showButton.isEnabled = true
            prepareButton.isEnabled = !cachingEnabled
            super.onAdLoaded()
            Log.d(TAG, "onAdLoaded")
        }

        override fun onAdFailedToLoad(errorCode: Int) {
            super.onAdFailedToLoad(errorCode)
            prepareButton.isEnabled = false
            showButton.isEnabled = false
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
            prepareButton.isEnabled = false
            showButton.isEnabled = false
            Log.d(TAG, "onAdClosed")
        }
    }
}