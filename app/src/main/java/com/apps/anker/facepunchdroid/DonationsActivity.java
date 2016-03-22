/*
 * Copyright (C) 2011-2015 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apps.anker.facepunchdroid;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.apps.anker.facepunchdroid.BuildConfig;
import com.apps.anker.facepunchdroid.R;

import org.sufficientlysecure.donations.DonationsFragment;

public class DonationsActivity extends FragmentActivity {

    /**
     * Google
     */
    private static final String GOOGLE_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqN8/ghccwA4r6V0jrQ/f7vubUNVu44J+9n+A/geCvQ50dE5YW8oiu+NKztMsou+a14oa/tFy5tdxs+i1PTEsIu9MDIXGP5wOJ3pEROQCO76FOAahR0b0uyAAIS5gaz9iRiTHzKrQppt57rKVgmo1bGFMGlgmGKn+s7x2rR7+GlORHASooaILS8/XvNZ3E7UMqktPae5IKIyycrPcAGEQ04+Q27JB8j1xSjiAaG1QOXfwjkjDZIPWlUjgZ+XIWG40+pSESlnCACsqxqwV5Sf2w1cQyNNcFKT8gh2+OIstM7VCY2BOp/xTNvNXZzASDZOHXSRDblTBTS8eV0DG1/QWUQIDAQAB";
    private static final String[] GOOGLE_CATALOG = new String[]{"donation1", "donation2", "donation5", "donation10"};


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.donations_activity);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DonationsFragment donationsFragment;

        donationsFragment = DonationsFragment.newInstance(false, true, GOOGLE_PUBKEY, GOOGLE_CATALOG,
                getResources().getStringArray(R.array.donation_google_catalog_values), false, null, null,
                null, false, null, null, false, null);


        ft.replace(R.id.donations_activity_container, donationsFragment, "donationsFragment");
        ft.commit();
    }

    /**
     * Needed for Google Play In-app Billing. It uses startIntentSenderForResult(). The result is not propagated to
     * the Fragment like in startActivityForResult(). Thus we need to propagate manually to our Fragment.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("donationsFragment");
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}
