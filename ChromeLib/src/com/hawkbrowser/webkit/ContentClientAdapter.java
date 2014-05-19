package com.hawkbrowser.webkit;

import org.chromium.chrome.browser.TabBase;
import org.chromium.chrome.browser.TabObserver;
import org.chromium.content.browser.ActivityContentVideoViewClient;
import org.chromium.content.browser.ContentVideoViewClient;
import org.chromium.content.browser.ContentViewClient;
import org.chromium.content.browser.WebContentsObserverAndroid;
import org.chromium.net.NetError;

import android.app.Activity;
import android.view.ContextMenu;

import com.hawkbrowser.webkit.WebChromeClient;
import com.hawkbrowser.webkit.WebView;
import com.hawkbrowser.webkit.WebViewClient;

public class ContentClientAdapter 
	extends WebContentsObserverAndroid 
	implements TabObserver {
	
	private WebViewClient mWebViewClient;
	private WebChromeClient mWebChromeClient;
	private WebView mWebView;
	
	class HawkBrowserContentViewClient extends ContentViewClient {
		
		@Override
	    public void onUpdateTitle(String title) {
			if(null != mWebChromeClient)
				mWebChromeClient.onReceivedTitle(mWebView, title);
	    }
		
        @Override
        public ContentVideoViewClient getContentVideoViewClient() {
        	
			Activity hostActivity = mWebView.getContext() instanceof Activity ? 
					(Activity) mWebView.getContext() : null;
        	
            return new ActivityContentVideoViewClient(hostActivity);
        }
		
	}
	
	public ContentClientAdapter(WebView webView) {
        super(webView.getTab().getContentViewCore());
        mWebView = webView;
        
        webView.getTab().getContentView().setContentViewClient(
        	new HawkBrowserContentViewClient());
        
        webView.getTab().addObserver(this);
    }
	
	public void destroy() {
		mWebView.getTab().removeObserver(this);
		mWebView.getTab().getContentView().setContentViewClient(null);
		mWebView = null;
		mWebViewClient = null;
		mWebChromeClient = null;
	}
	
	public void setWebViewClient(WebViewClient client) {
		mWebViewClient = client;
	}
	
	public void SetChromeClient(WebChromeClient client) {
		mWebChromeClient = client;
	}

    @Override
    public void didFinishLoad(long frameId, String validatedUrl, boolean isMainFrame) {
        if (isMainFrame && null != mWebViewClient) {
        	mWebViewClient.onPageFinished(mWebView, validatedUrl);
        }
    }

    @Override
    public void didFailLoad(boolean isProvisionalLoad,
            boolean isMainFrame, int errorCode, String description, String failingUrl) {
        if (isMainFrame && null != mWebViewClient) {
            if (errorCode != NetError.ERR_ABORTED) {
                // This error code is generated for the following reasons:
                // - WebView.stopLoading is called,
                // - the navigation is intercepted by the embedder via shouldOverrideNavigation.
                //
                // The Android WebView does not notify the embedder of these situations using
                // this error code with the WebViewClient.onReceivedError callback.                
                mWebViewClient.onReceivedError(mWebView, errorCode, description, failingUrl);
            }
            // Need to call onPageFinished after onReceivedError (if there is an error) for
            // backwards compatibility with the classic webview.
            mWebViewClient.onPageFinished(mWebView, failingUrl);
        }
    }

    @Override
    public void didNavigateMainFrame(String url, String baseUrl,
            boolean isNavigationToDifferentPage, boolean isNavigationInPage) {
        // This is here to emulate the Classic WebView firing onPageFinished for main frame
        // navigations where only the hash fragment changes.
        if (isNavigationInPage && null != mWebViewClient) {
        	mWebViewClient.onPageFinished(mWebView, url);
        }
    }

    @Override
    public void didNavigateAnyFrame(String url, String baseUrl, boolean isReload) {
    	if(null != mWebViewClient)
    		mWebViewClient.doUpdateVisitedHistory(mWebView, url, isReload);
    }

    //---------------------- interface TabObserver ----------------------------
	@Override
	public void onContentChanged(TabBase tabBase) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onContextMenuShown(TabBase tabBase, ContextMenu menu) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDestroyed(TabBase tabBase) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDidFailLoad(TabBase tabBase, boolean isProvisionalLoad,
            boolean isMainFrame, int errorCode, String description, String failingUrl) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onFaviconUpdated(TabBase tabBase) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLoadProgressChanged(TabBase tabBase, int progress) {
		// TODO Auto-generated method stub
		if(null != mWebChromeClient)
			mWebChromeClient.onProgressChanged(mWebView, progress);
	}

	@Override
	public void onToggleFullscreenMode(TabBase tabBase, boolean arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpdateUrl(TabBase tabBase, String arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onWebContentsSwapped(TabBase tabBase) {
		// TODO Auto-generated method stub
		
	}
    
    
}
