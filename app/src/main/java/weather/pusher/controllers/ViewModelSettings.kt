package weather.pusher.controllers

/* Created by evolandlupiz on 02/13/19
 * This file is subject to the terms and conditions defined in
 * file 'WEATHERPUSHER_LICENSE.txt', which is part of this source code package.*/

import androidx.lifecycle.ViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/* ViewModel class will serve as the primary link between the settings view and the settings model */
class ViewModelSettings: ViewModel() {

    /* Koin dependency injection for this class */
    val mModuleVmSettings: Module = module {
        viewModel { ViewModelSettings() }
    }
}