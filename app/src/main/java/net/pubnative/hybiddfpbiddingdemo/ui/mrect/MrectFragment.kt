package net.pubnative.hybiddfpbiddingdemo.ui.mrect

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
import net.pubnative.lite.sdk.api.MRectRequestManager
import net.pubnative.lite.sdk.api.RequestManager
import net.pubnative.lite.sdk.models.Ad
import net.pubnative.lite.sdk.utils.HeaderBiddingUtils


class MrectFragment : Fragment(R.layout.fragment_mrect), RequestManager.RequestListener {
    val TAG = MrectFragment::class.java.simpleName

    private lateinit var requestManager: RequestManager
    private lateinit var loadButton: Button
    private lateinit var dfpMRect: PublisherAdView
    private lateinit var dfpMRectContainer: FrameLayout


    private val zoneId: String = "5"
    private val dfpAdUnitId: String = "/219576711/pnlite_dfp_mrect"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dfpMRectContainer = view.findViewById(R.id.dfp_mrect_container)
        loadButton = view.findViewById(R.id.button_load)

        requestManager = MRectRequestManager()

        dfpMRect = PublisherAdView(activity)
        dfpMRect.adUnitId = dfpAdUnitId
        dfpMRect.setAdSizes(AdSize.MEDIUM_RECTANGLE)
        dfpMRect.adListener = adListener

        dfpMRectContainer.addView(dfpMRect)

        loadButton.setOnClickListener {
            loadMRect()
        }
    }

    override fun onDestroy() {
        dfpMRect.destroy()
        super.onDestroy()
    }

    private fun loadMRect() {
        requestManager.setZoneId(zoneId)
        requestManager.setRequestListener(this)
        requestManager.requestAd()
    }

    // -------------------- RequestManager's Listeners ------------------------
    override fun onRequestSuccess(ad: Ad?) {
        val builder = PublisherAdRequest.Builder()

        val keywordSet: Set<String> =
            HeaderBiddingUtils.getHeaderBiddingKeywordsSet(ad)
        for (key in keywordSet) {
            builder.addKeyword(key)
        }

        val keywordBundle =
            HeaderBiddingUtils.getHeaderBiddingKeywordsBundle(ad)
        for (key in keywordBundle.keySet()) {
            builder.addCustomTargeting(key, keywordBundle.getString(key))
        }

        val adRequest = builder.build()
        dfpMRect.loadAd(adRequest)

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