package com.nomade.movilremiscar.remiscarmovil.Util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gino on 17/3/2018.
 */

public class LocationServiceHelper {

    private static final String TAG = LocationServiceHelper.class.getSimpleName();

    public static boolean isMockedLocation(Context context) {
        return (isMockSettingsON(context) && areThereMockPermissionApps(context))
                || isFakeLocation2(context)
                || checkPackages(context);
    }


    private static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }

    private static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception ", e.getMessage());
            }
        }

        if (count > 0)
            return true;
        return false;
    }

    private static boolean isFakeLocation2(Context context) {
        LocationManager locMan;
        String[] mockProviders = {LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER};

        try {
            locMan = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            for (String p : mockProviders) {
                if (p.contentEquals(LocationManager.GPS_PROVIDER))
                    locMan.addTestProvider(p, false, false, false, false, true, true, true, 1,
                            android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
                else
                    locMan.addTestProvider(p, false, false, false, false, true, true, true, 1,
                            android.hardware.SensorManager.SENSOR_STATUS_ACCURACY_LOW);

                locMan.setTestProviderEnabled(p, true);
                locMan.setTestProviderStatus(p, android.location.LocationProvider.AVAILABLE, Bundle.EMPTY,
                        java.lang.System.currentTimeMillis());
            }
        } catch (Exception ignored) {
            // here you should show dialog which is mean the mock location is not enable
            return false;
        }
        return true;
    }

    private static boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static boolean checkPackages(Context context) {
        ArrayList<String> fakeApps = new ArrayList<>();
        fakeApps.add("a1brains.com.fakegps");
        fakeApps.add("cl.datacomputer.alejandrob.newgpsjoystick");
        fakeApps.add("com.androdiki.flygpsplus");
        fakeApps.add("com.bigbistudio.flygps");
        fakeApps.add("com.blogspot.newapphorizons.fakegps");
        fakeApps.add("com.chortorlab.fakeloc");
        fakeApps.add("com.divi.fakeGPS");
        fakeApps.add("com.dreams.studio.apps.fake.gps.loaction.changer");
        fakeApps.add("com.dreams.studio.apps.location.changer.fake.gps");
        fakeApps.add("com.evezzon.fakegps");
        fakeApps.add("com.exien.fakegps");
        fakeApps.add("com.fakegps.joystick");
        fakeApps.add("com.fakegps.mock");
        fakeApps.add("com.fly.gps");
        fakeApps.add("com.gsmartstudio.fakegps");
        fakeApps.add("com.idans.locationfaker");
        fakeApps.add("com.incorporateapps.fakegps");
        fakeApps.add("com.incorporateapps.fakegps.fre");
        fakeApps.add("com.incorporateapps.fakegps_route");
        fakeApps.add("com.intelligence.flygps");
        fakeApps.add("com.kfn.fakegpsfree");
        fakeApps.add("com.kristo.fakegpspro");
        fakeApps.add("com.lexa.fakegps");
        fakeApps.add("com.lkr.fakelocation");
        fakeApps.add("com.locationchanger");
        fakeApps.add("com.lovestyle.mapwalker.android");
        fakeApps.add("com.marlon.floating.fake.location");
        fakeApps.add("com.moziasoft.fakegps");
        fakeApps.add("com.neogulabs.fakegps");
        fakeApps.add("com.ninja.toolkit.pulse.fake.gps.pro");
        fakeApps.add("com.orsanapps.apps.maps.fakegps");
        fakeApps.add("com.orsanstudio.apps.maps.fakegps");
        fakeApps.add("com.pe.fakegps");
        fakeApps.add("com.pe.fakegpsrun");
        fakeApps.add("com.rosteam.gpsemulator");
        fakeApps.add("com.suptech.hicham.fakelocation");
        fakeApps.add("com.theappninjas.gpsjoystick");
        fakeApps.add("com.usefullapps.fakegpslocationpro");
        fakeApps.add("com.xdoapp.virtualphonenavigation");
        fakeApps.add("fake.gps.location");
        fakeApps.add("fake.location.gpsspoof");
        fakeApps.add("fakegps.kc.creations");
        fakeApps.add("fr.dvilleneuve.lockito");
        fakeApps.add("maplocation.shira.com.maplocation");
        fakeApps.add("mappstreet.com.fakegpslocation");
        fakeApps.add("net.marlove.mockgps");
        fakeApps.add("org.dragonlab.joystick");
        fakeApps.add("org.hola.gpslocation");
        fakeApps.add("rah.fakegps.withjoystick");
        fakeApps.add("ru.gavrikov.mocklocations");
        fakeApps.add("tk.kureksofts.fakegps");
        fakeApps.add("br.com.itexpertconsult.changemygps");
        fakeApps.add("br.com.tupinikimtecnologia.fakegpslocation");
        fakeApps.add("city.truck.simulator");
        fakeApps.add("com.adaptivebits.fakegpslocation");
        fakeApps.add("com.app.fake.gps");
        fakeApps.add("com.blk.fakegps");
        fakeApps.add("com.crazyapps.mobilelocationtracker");
        fakeApps.add("com.cxdeberry.geotag");
        fakeApps.add("com.diamond.studio.apps.fake.gps.location.changer");
        fakeApps.add("com.digrasoft.mygpslocation");
        fakeApps.add("com.direction.gps.fake.location");
        fakeApps.add("com.discipleskies.satellitecheck");
        fakeApps.add("com.electro_tex.fakegps");
        fakeApps.add("com.electro_tex.fakegpsgo");
        fakeApps.add("com.electro_tex.fakegpslocation");
        fakeApps.add("com.fakegps.fakelocation");
        fakeApps.add("com.fakegps.gps.go.pro");
        fakeApps.add("com.fakegps.location.changer");
        fakeApps.add("com.favorite.pro.fakegpslocation");
        fakeApps.add("com.free.fake.gps.map.location");
        fakeApps.add("com.gcg.bigCruiseShip_GCG");
        fakeApps.add("com.gregacucnik.fishingpoints");
        fakeApps.add("com.ka.fakegps.flygps.locationchanger.mocklocation.fakegpslocation");
        fakeApps.add("com.lapacadevs.gpsfaker");
        fakeApps.add("com.lexa.fakegps");
        fakeApps.add("com.lexa.fakegpsdonate");
        fakeApps.add("com.lookie.fakegps");
        fakeApps.add("com.lovestyle.mapwalker.android");
        fakeApps.add("com.ltp.pro.fakelocation");
        fakeApps.add("com.meowsbox.btgps");
        fakeApps.add("com.mobidev.fakegps");
        fakeApps.add("com.msl.worldtroll");
        fakeApps.add("com.mustafaazad.fakegpsforpokemongo");
        fakeApps.add("com.ngapp.fakegps");
        fakeApps.add("com.onedayofcode.gpssimulator");
        fakeApps.add("com.ovilex.trucksimulatorusa");
        fakeApps.add("com.phongphan.fakegps");
        fakeApps.add("com.plusinfosys.fakegpslocation");
        fakeApps.add("com.project.jp.fakegpsgo");
        fakeApps.add("com.rayo.routerecorder");
        fakeApps.add("com.rayo.savecurrentlocation");
        fakeApps.add("com.silentlexx.gpslock");
        fakeApps.add("com.smartpcx.gpslocacion");
        fakeApps.add("com.studio.armens.fakelocation");
        fakeApps.add("com.ta.My.Fake.Location.Fake.GPS");
        fakeApps.add("com.technoviral.fakelivelocation");
        fakeApps.add("com.ua.eugenezaychenko.mocklocations");
        fakeApps.add("com.urysoft.fakegps");
        fakeApps.add("com.xdoapp.virtualphonenavigation");
        fakeApps.add("com.yntmar.gps");
        fakeApps.add("de.appsmadeingermany.gpsfakelocation");
        fakeApps.add("fakegps.fakegpslocation");
        fakeApps.add("find.my.friends.family.gps.location.tracker");
        fakeApps.add("gp.com.turnersark.fakegpspaid");
        fakeApps.add("gp.com.turnersark.fakegpspro");
        fakeApps.add("gpsme.app");
        fakeApps.add("info.swappdevmobile.lbgooglemap");
        fakeApps.add("lame.game.fakeGPS");
        fakeApps.add("maps.GPS.offlinemaps.FreeGPS");
        fakeApps.add("mg.locations.track5");
        fakeApps.add("mobi.coolapps.locationsimulator");
        fakeApps.add("new.Live_GpsMaps");
        fakeApps.add("project.listick.fakegps");
        fakeApps.add("pt.com.turnersark.fakegpsfulllocationpro");
        fakeApps.add("ru.gavrikov.hidemocklocations");
        fakeApps.add("yaacode.android.gps.movegps.free");


        PackageManager pm = context.getPackageManager();
        for (String fakeApp : fakeApps) {
            if (isPackageInstalled(fakeApp, pm)) {
                SharedPrefsUtil.getInstance(context).saveString("app_simulacion", fakeApp);
                Log.w(TAG, "Fake gps found " + fakeApp);
                return true;
            }
        }
        return false;
    }

}