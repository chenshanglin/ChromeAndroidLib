package com.hawkbrowser.chromelib;

import org.chromium.chrome.hawkbrowser.HawkBrowserTab;
import org.chromium.content.browser.ActivityContentVideoViewClient;
import org.chromium.content.browser.ContentVideoViewClient;
import org.chromium.content.browser.ContentView;
import org.chromium.content.browser.ContentViewClient;
import org.chromium.content.common.ProcessInitException;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class WebView extends FrameLayout {
	
	private static final String TAG = "ChromeLib";
	
	private TabManager mTabManager;
	private HawkBrowserTab mTab;
    private String mPendingLoadUrl;
    
    public WebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

    public void loadUrl(String url) throws ProcessInitException {
    	
    	final ChromeInitializer chromeInitializer = ChromeInitializer.get();
    	
    	if(!chromeInitializer.isInitialized()) {
    		chromeInitializer.initialize();
    	}
    	
		if(chromeInitializer.isChromeStartFinished())
			loadUrlAfterChromeStart(url);
		else {
			
			mPendingLoadUrl = url;
			
			chromeInitializer.setCallback(new ChromeInitializer.InitializeCallback() {
				
				@Override
				public void onSuccess(boolean alreadyStarted) {
					// TODO Auto-generated method stub
					initAfterChromeStart();
					loadUrlAfterChromeStart(mPendingLoadUrl);
				}
				
				@Override
				public void onFailure() {
					// TODO Auto-generated method stub
				}
			});
        	
			chromeInitializer.startChrome(getContext());
		}
    }
	
	private void initAfterChromeStart() {
		
    	mTabManager = TabManager.get(getContext());        
        addView(mTabManager, new FrameLayout.LayoutParams(
        	FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
	}
	
	private void loadUrlAfterChromeStart(String url) {
		
		if(mTabManager.getCurrentTab() != null) 
			mTabManager.getCurrentTab().loadUrlWithSanitization(url);
		else {
			mTab = mTabManager.createTab(url);
			
			assert mTab != null;
			
			final Activity hostActivity = getContext() instanceof Activity ? 
					(Activity) getContext() : null;
			
			if(null != hostActivity) {
				getContentView().setContentViewClient(new ContentViewClient() {
		            @Override
		            public ContentVideoViewClient getContentVideoViewClient() {
		                return new ActivityContentVideoViewClient(hostActivity);
		            }
		        });	
			}
		}
	}
		
	public String getUrl() {
		if(null == mTab)
			return "";
		else
			return mTab.getContentView().getUrl();
	}
	
	public boolean canGoBack() {
		if(null == mTab)
			return false;
		else
			return mTab.getContentView().canGoBack();
	}
	
	public boolean canGoForward() {
		if(null == mTab)
			return false;
		else
			return mTab.getContentView().canGoForward();
	}
	
	public void goBack() {
		if(null != mTab)
			mTab.getContentView().goBack();
	}

	public void goForward() {
		if(null != mTab)
			mTab.getContentView().goForward();
	}

	public ContentView getContentView() {
		if(null != mTab)
			return mTab.getContentView();
		else
			return null;
	}
	
	public void destroy() {
		
		if(null != mTab) {
			TabManager.get(getContext()).destroyTab(mTab);
			mTab = null;
		}
	}
}
