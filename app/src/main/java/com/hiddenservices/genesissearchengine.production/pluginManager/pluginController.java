package com.hiddenservices.genesissearchengine.production.pluginManager;

import android.view.WindowManager;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.hiddenservices.genesissearchengine.production.appManager.activityContextManager;
import com.hiddenservices.genesissearchengine.production.appManager.homeManager.geckoManager.geckoSession;
import com.hiddenservices.genesissearchengine.production.appManager.homeManager.homeController.homeController;
import com.hiddenservices.genesissearchengine.production.appManager.orbotLogManager.orbotLogController;
import com.hiddenservices.genesissearchengine.production.appManager.settingManager.privacyManager.settingPrivacyController;
import com.hiddenservices.genesissearchengine.production.constants.constants;
import com.hiddenservices.genesissearchengine.production.constants.enums;
import com.hiddenservices.genesissearchengine.production.constants.keys;
import com.hiddenservices.genesissearchengine.production.constants.status;
import com.hiddenservices.genesissearchengine.production.dataManager.dataController;
import com.hiddenservices.genesissearchengine.production.dataManager.dataEnums;
import com.hiddenservices.genesissearchengine.production.eventObserver;
import com.hiddenservices.genesissearchengine.production.helperManager.helperMethod;
import com.hiddenservices.genesissearchengine.production.pluginManager.adPluginManager.mopubManager;
import com.hiddenservices.genesissearchengine.production.pluginManager.analyticPluginManager.analyticManager;
import com.hiddenservices.genesissearchengine.production.pluginManager.downloadPluginManager.downloadManager;
import com.hiddenservices.genesissearchengine.production.pluginManager.langPluginManager.langManager;
import com.hiddenservices.genesissearchengine.production.pluginManager.messagePluginManager.messageManager;
import com.hiddenservices.genesissearchengine.production.pluginManager.notificationPluginManager.notifictionManager;
import com.hiddenservices.genesissearchengine.production.pluginManager.orbotPluginManager.orbotManager;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import static com.hiddenservices.genesissearchengine.production.constants.enums.etype.fetch_favicon;
import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_CLICK;
import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_HIDE;
import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eAdManagerCallbacks.M_ON_AD_LOAD;
import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eLangManager.M_ACTIVITY_CREATED;
import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eLangManager.M_RESUME;
import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eMessageManager.*;
import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eMessageManagerCallbacks.*;
import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eMessageManagerCallbacks.M_CLEAR_BOOKMARK;
import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eMessageManagerCallbacks.M_CLEAR_HISTORY;
import static com.hiddenservices.genesissearchengine.production.pluginManager.pluginEnums.eMessageManagerCallbacks.M_LOAD_NEW_TAB;

public class pluginController
{
    /*Plugin Instance*/

    private mopubManager mAdManager;
    private com.hiddenservices.genesissearchengine.production.pluginManager.analyticPluginManager.analyticManager mAnalyticsManager;
    private messageManager mMessageManager;
    private notifictionManager mNotificationManager;
    private activityContextManager mContextManager;
    private langManager mLangManager;
    private orbotManager mOrbotManager;
    private downloadManager mDownloadManager;

    /*Private Variables*/

    private static pluginController ourInstance = new pluginController();
    private WeakReference<AppCompatActivity> mHomeController;
    private boolean mIsInitialized = false;

    /*Initializations*/

    public static pluginController getInstance()
    {
        return ourInstance;
    }

    public void preInitialize(homeController context){
        mLangManager = new langManager(context,new langCallback(), new Locale(status.sSettingLanguage), status.mSystemLocale, status.sSettingLanguage, status.sSettingLanguageRegion, status.mThemeApplying);
    }

    public void initialize(){
        instanceObjectInitialization();
        mIsInitialized = true;
    }

    public void onRemoveInstances(){
        mHomeController = null;
    }

    private void instanceObjectInitialization()
    {
        mHomeController = new WeakReference(activityContextManager.getInstance().getHomeController());
        mContextManager = activityContextManager.getInstance();

        mNotificationManager = new notifictionManager(mHomeController,new notificationCallback());
        mAdManager = new mopubManager(new admobCallback(), ((homeController)mHomeController.get()).getBannerAd(), mHomeController.get());
        mAnalyticsManager = new analyticManager(mHomeController,new analyticCallback());
        mMessageManager = new messageManager(new messageCallback());
        mOrbotManager = orbotManager.getInstance();
        mDownloadManager = new downloadManager(mHomeController,new downloadCallback());
    }

    /*Helper Methods*/

    public boolean isInitialized(){
        return mIsInitialized;
    }

    public void initializeAllServices(AppCompatActivity context){
        int mNotificationStatus = (Integer) dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_GET_INT, Arrays.asList(keys.SETTING_NOTIFICATION_STATUS,1));
        orbotManager.getInstance().initialize(context,new orbotCallback(), mNotificationStatus);
    }

    /*------------------------------------------------ CALLBACK LISTENERS------------------------------------------------------------*/

    /*Ad Manager*/
    private class admobCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object event_type)
        {
            if(event_type.equals(M_ON_AD_CLICK)){
                helperMethod.onMinimizeApp(mHomeController.get());
                activityContextManager.getInstance().getHomeController().onAdClicked();
            }
            else if(event_type.equals(M_ON_AD_LOAD)){
                activityContextManager.getInstance().getHomeController().onUpdateBannerAdvert();
            }
            else if(event_type.equals(M_ON_AD_HIDE)){
                activityContextManager.getInstance().getHomeController().onAdClicked();
            }
            return null;
        }
    }

    public Object onAdsInvoke(List<Object> pData, pluginEnums.eAdManager pEventType){
        if(mAdManager !=null){
            return mAdManager.onTrigger(pEventType);
        }
        return null;
    }


    /*Analytics Manager*/
    private class analyticCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object event_type) { return null; }
    }

    public void onAnalyticsInvoke(List<Object> pData, pluginEnums.eAnalyticManager pEventType){
        if(mAnalyticsManager !=null){
            mAnalyticsManager.onTrigger(pData, pEventType);
        }
    }


    /*Notification Manager*/
    private class notificationCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object event_type)
        {
            return null;
        }
    }

    public Object onNotificationInvoke(List<Object> pData, pluginEnums.eNotificationManager pEventType){
        if(mNotificationManager!=null){
            return mNotificationManager.onTrigger(pData, pEventType);
        }
        return null;
    }

    /*Download Manager*/
    private class downloadCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> pData, Object event_type)
        {
            if(event_type.equals(enums.etype.M_DOWNLOAD_FAILURE))
            {
                mHomeController.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMessageManager.onTrigger(Arrays.asList(pData.get(0).toString(), mHomeController.get()),M_DOWNLOAD_FAILURE);
                    }
                });
            }
            return null;
        }
    }

    public Object onDownloadInvoke(List<Object> pData, pluginEnums.eDownloadManager pEventType){
        if(mDownloadManager!=null){
            return mDownloadManager.onTrigger(pData, pEventType);
        }
        return null;
    }

    /*Onion Proxy Manager*/
    private class orbotCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object event_type)
        {
            return null;
        }
    }

    public Object onOrbotInvoke(List<Object> pData, pluginEnums.eOrbotManager pEventType){
        if(mOrbotManager !=null){
            return mOrbotManager.onTrigger(pData, pEventType);
        }
        return null;
    }

    /*Lang Manager*/
    public Object onLanguageInvoke(List<Object> pData, pluginEnums.eLangManager pEventType){
        if(mLangManager==null){
            return null;
        }

        if(pEventType.equals(M_RESUME) || pEventType.equals(M_ACTIVITY_CREATED)){
            return mLangManager.onTrigger(Arrays.asList(pData.get(0), status.sSettingLanguage, status.sSettingLanguageRegion, status.mThemeApplying), pEventType);
        }

        return mLangManager.onTrigger(pData, pEventType);
    }

    private class langCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> data, Object event_type)
        {
            if(event_type.equals(pluginEnums.eLangManager.M_UPDATE_LOCAL)){
                status.mSystemLocale = (Locale)data.get(0);
            }
            return null;
        }
    }

    /*Message Manager*/
    public void onMessageManagerInvoke(List<Object> pData, pluginEnums.eMessageManager pEventType){
        if(mMessageManager!=null){
            mMessageManager.onTrigger(pData,pEventType);
        }
    }

    private class messageCallback implements eventObserver.eventListener{
        @Override
        public Object invokeObserver(List<Object> pData, Object pEventType)
        {
            if(pEventType.equals(enums.etype.welcome))
            {
                ((homeController)mHomeController.get()).onLoadURL(pData.get(0).toString());
            }
            else if(pEventType.equals(M_PANIC_RESET)){
                helperMethod.onDelayHandler(mHomeController.get(), 150, () -> {
                    activityContextManager.getInstance().getHomeController().panicExitInvoked();
                    return null;
                });
            }
            else if(pEventType.equals(M_DOWNLOAD_SINGLE)){
                if(pData!=null){
                    if(pData.size()<3){
                        ((homeController)mHomeController.get()).onManualDownload(pData.get(0).toString());
                    }else {
                        if(pData.get(2).toString().startsWith("https://data") || pData.get(2).toString().startsWith("http://data")){
                            ((homeController)mHomeController.get()).onManualDownload(pData.get(2).toString().replace("https://","").replace("http://",""));
                        }else {
                            ((homeController)mHomeController.get()).onManualDownloadFileName(pData.get(2).toString(),(String)pData.get(0));
                        }
                    }
                }
            }
            else if(pEventType.equals(M_CANCEL_WELCOME)){
                status.sSettingIsWelcomeEnabled = false;
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.SETTING_IS_WELCOME_ENABLED,false));
            }
            else if(pEventType.equals(enums.etype.reload)){
                if((Boolean) mOrbotManager.onTrigger(null, pluginEnums.eOrbotManager.M_IS_ORBOT_RUNNING))
                {
                    ((homeController)mHomeController.get()).onReload(null);
                }
                else {
                    mMessageManager.onTrigger(Arrays.asList(mHomeController, Collections.singletonList(pData.get(0).toString())),M_START_ORBOT);
                }
            }
            else if(pEventType.equals(M_OPEN_PRIVACY)){
                helperMethod.openActivity(settingPrivacyController.class, constants.CONST_LIST_HISTORY, mHomeController.get(),true);
            }
            else if(pEventType.equals(M_CLEAR_BOOKMARK)){
                dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_CLEAR_BOOKMARK ,pData);
                mContextManager.getBookmarkController().onclearData();
            }
            else if(pEventType.equals(M_CLEAR_HISTORY)){
                dataController.getInstance().invokeHistory(dataEnums.eHistoryCommands.M_CLEAR_HISTORY ,pData);
                mContextManager.getHistoryController().onclearData();
            }
            else if(pEventType.equals(M_BOOKMARK)){
                String [] dataParser = pData.get(0).toString().split("split");
                dataController.getInstance().invokeBookmark(dataEnums.eBookmarkCommands.M_ADD_BOOKMARK ,Arrays.asList(dataParser[0],pData.get(1).toString()));
            }
            else if(pEventType.equals(M_APP_RATED)){
                dataController.getInstance().invokePrefs(dataEnums.ePreferencesCommands.M_SET_BOOL, Arrays.asList(keys.PROXY_IS_APP_RATED,true));
            }
            else if(pEventType.equals(M_CUSTOM_BRIDGE)){
                return status.sBridgeCustomBridge;
            }
            else if(pEventType.equals(M_OPEN_LOGS)){
                helperMethod.openActivity(orbotLogController.class, constants.CONST_LIST_HISTORY, mHomeController.get(),true);
            }
            else if(pEventType.equals(M_BRIDGE_TYPE)){
                return status.sBridgeCustomType;
            }
            else if(pEventType.equals(fetch_favicon)){
                activityContextManager.getInstance().getHomeController().onGetFavIcon((ImageView) pData.get(0), (String) pData.get(1));
            }
            else if(pEventType.equals(M_DOWNLOAD_FILE)){
                ((homeController)mHomeController.get()).onDownloadFile();
            }
            else if(pEventType.equals(M_DOWNLOAD_FILE_MANUAL)){
                ((homeController)mHomeController.get()).onManualDownload(pData.get(0).toString());
            }
            else if(pEventType.equals(M_LOAD_NEW_TAB)){
                ((homeController)mHomeController.get()).onLoadTab((geckoSession) pData.get(pData.size()-2),false,false,false);
            }
            else if(pEventType.equals(M_OPEN_LINK_NEW_TAB)){

                ((homeController)mHomeController.get()).postNewLinkTabAnimationInBackgroundTrigger(pData.get(0).toString());
            }
            else if(pEventType.equals(M_OPEN_LINK_CURRENT_TAB)){
                ((homeController)mHomeController.get()).onLoadURL(pData.get(0).toString());
            }
            else if(pEventType.equals(M_COPY_LINK)){
                helperMethod.copyURL(pData.get(0).toString(),mContextManager.getHomeController());
            }
            else if(pEventType.equals(M_CLEAR_TAB)){
                dataController.getInstance().invokeTab(dataEnums.eTabCommands.M_CLEAR_TAB, null);
                ((homeController)mHomeController.get()).initTab(true);
                ((homeController)mHomeController.get()).onDisableTabViewController();
            }
            else if(pEventType.equals(M_REQUEST_BRIDGES)){
                pluginController.getInstance().onMessageManagerInvoke(Arrays.asList(constants.CONST_BACKEND_GOOGLE_URL, this), M_BRIDGE_MAIL);
            }
            else if(pEventType.equals(M_SET_BRIDGES)){
                activityContextManager.getInstance().getBridgeController().onUpdateBridges((String) pData.get(0), (String) pData.get(1));
            }
            else if(pEventType.equals(M_UNDO_SESSION)){
                activityContextManager.getInstance().getHomeController().onLoadRecentTab(null);
            }
            else if(pEventType.equals(M_UNDO_TAB)){
                activityContextManager.getInstance().getTabController().onRestoreTab(null);
            }
            else if(pEventType.equals(M_SECURITY_INFO)){
                mMessageManager.onTrigger(Arrays.asList(activityContextManager.getInstance().getHomeController().getSecurityInfo(),mHomeController.get()),M_SECURITY_INFO);
            }
            else if(pEventType.equals(M_ADJUST_INPUT_RESIZE)){
                mHomeController.get().runOnUiThread(() -> mHomeController.get().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE));
            }
            else if(pEventType.equals(M_IMAGE_UPDATE_RESTART)){
                if(pData!=null && pData.get(0)!=null){
                    ((AppCompatActivity)pData.get(0)).finish();
                    activityContextManager.getInstance().getHomeController().quitApplication();
                }
                new Thread(){
                    public void run(){
                        try {
                            sleep(500);
                            android.os.Process.killProcess(android.os.Process.myPid());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
            return null;
        }
    }
}