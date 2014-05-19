package com.hawkbrowser.webkit;

import org.chromium.chrome.hawkbrowser.HawkBrowserTab;
import org.chromium.content.browser.ActivityContentVideoViewClient;
import org.chromium.content.browser.ContentVideoViewClient;
import org.chromium.content.browser.ContentView;
import org.chromium.content.browser.ContentViewClient;
import org.chromium.content.browser.ContentViewDownloadDelegate;
import org.chromium.content.browser.DownloadInfo;
import org.chromium.content.common.ProcessInitException;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.hawkbrowser.chromelib.ChromeInitializer;
import com.hawkbrowser.chromelib.TabManager;

public class WebView extends FrameLayout 
					 implements ContentViewDownloadDelegate {
	
	private static final String TAG = "ChromeLib";
	
	private TabManager mTabManager;
	private HawkBrowserTab mTab;
    private String mPendingLoadUrl;
    private ContentClientAdapter mContentClientAdapter;
    private DownloadListener mDownloadListener;
    
    public WebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
    
    // package visible only
    HawkBrowserTab getTab() {
    	return mTab;
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
			
			mContentClientAdapter = new ContentClientAdapter(this);
			
			getContentView().setDownloadDelegate(this);
			
//			final Activity hostActivity = getContext() instanceof Activity ? 
//					(Activity) getContext() : null;
//			
//			if(null != hostActivity) {
//				getContentView().setContentViewClient(new ContentViewClient() {
//		            @Override
//		            public ContentVideoViewClient getContentVideoViewClient() {
//		                return new ActivityContentVideoViewClient(hostActivity);
//		            }
//		        });	
//			}
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
		
			mTab.getContentView().setDownloadDelegate(null);
			// mContentClientAdapter.destroy();
			
			TabManager.get(getContext()).destroyTab(mTab);
			mTab = null;
		}
	}
	
	public void setDownloadListener(DownloadListener listener) {
		mDownloadListener = listener;
	}
	
    /**
    * Notify the host application that a file should be downloaded. Replaces
    * onDownloadStart from DownloadListener.
    * @param downloadInfo Information about the requested download.
    */
	@Override
    public void requestHttpGetDownload(DownloadInfo downloadInfo) {
		if(null != mDownloadListener)
			mDownloadListener.onDownloadStart(
				downloadInfo.getUrl(), downloadInfo.getUserAgent(), 
				downloadInfo.getContentDisposition(),
				downloadInfo.getMimeType(), downloadInfo.getContentLength());
	}

    /**
     * Notify the host application that a download is started.
     * @param filename File name of the downloaded file.
     * @param mimeType Mime of the downloaded item.
     */
	@Override
    public void onDownloadStarted(String filename, String mimeType) {
		assert false;
	}

    /**
     * Notify the host application that a download has an extension indicating
     * a dangerous file type.
     * @param filename File name of the downloaded file.
     * @param downloadId The download id.
     */
	@Override
    public void onDangerousDownload(String filename, int downloadId) {
		assert false;
	}
	
	public void setWebViewClient(WebViewClient client) {
		mContentClientAdapter.setWebViewClient(client);
	}
	
	public void setWebChromeClient(WebChromeClient client) {
		mContentClientAdapter.SetChromeClient(client);
	}
}
