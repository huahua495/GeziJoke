package com.example.gezijoke.utils;

import android.content.ComponentName;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

import com.example.gezijoke.FixFragmentNavigator;
import com.example.gezijoke.model.Destination;
import com.example.libcommon.AppGlobals;

import java.util.HashMap;

public class NavGraphBuilder {
    public static void build(NavController controller,
                             @NonNull FragmentActivity activity,
                             @NonNull FragmentManager manager,
                             int containerId) {
        NavigatorProvider navigatorProvider = controller.getNavigatorProvider();

        //FragmentNavigator fragmentNavigator = navigatorProvider.getNavigator(FragmentNavigator.class);
        FixFragmentNavigator fragmentNavigator = new FixFragmentNavigator(activity,
                activity.getSupportFragmentManager(), containerId);
        navigatorProvider.addNavigator(fragmentNavigator);


        ActivityNavigator activityNavigator = navigatorProvider.getNavigator(ActivityNavigator.class);


        NavGraph navGraph = new NavGraph(new NavGraphNavigator(navigatorProvider));

        HashMap<String, Destination> destConfig = AppConfig.getsDestConfig();
        for (Destination value : destConfig.values()) {
            if (value.isIsFragment()) {
                FragmentNavigator.Destination destination = fragmentNavigator.createDestination();
                destination.setClassName(value.getClazName());
                destination.setId(value.getId());
                destination.addDeepLink(value.getPageurl());

                navGraph.addDestination(destination);
            } else {

                ActivityNavigator.Destination destination = activityNavigator.createDestination();

                destination.setId(value.getId());
                destination.addDeepLink(value.getPageurl());
                destination.setComponentName(new ComponentName(
                        AppGlobals.getsApplication().getPackageName(),
                        value.getClazName()));
                navGraph.addDestination(destination);
            }
            if (value.isAsStarter()) {
                navGraph.setStartDestination(value.getId());
            }
            controller.setGraph(navGraph);
        }
    }


}
