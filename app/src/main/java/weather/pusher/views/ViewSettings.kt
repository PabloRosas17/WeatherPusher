package weather.pusher.views

/* Created by evolandlupiz on 02/13/19
 * This file is subject to the terms and conditions defined in
 * file 'WEATHERPUSHER_LICENSE.txt', which is part of this source code package.*/

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel
import weather.pusher.R
import weather.pusher.controllers.ViewModelSettings
import weather.pusher.databinding.ViewSettingsLayoutDxBinding
import weather.pusher.views.presenters.PresenterSettings
import weather.pusher.util.BtmNavIf
import weather.pusher.util.ViewBinderIf

/* @desc Settings interface will provide TODO: settings interface comment*/
class ViewSettings: AppCompatActivity(), BtmNavIf, ViewBinderIf<ViewSettingsLayoutDxBinding> {

    /* view model by koin and dependency injection by constructor */
    private val mVmSettings : ViewModelSettings by viewModel()

    /* binding type */
    override lateinit var mBinding: ViewSettingsLayoutDxBinding

    /* bottom navigation view object */
    override lateinit var mBtmNavView: BottomNavigationView

    /*
     * activity lifecycle onCreate generates the Settings interface with viewmodel
     *  and produces listeners with functionality on
     *  @TODO: account interface comment
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.fireUiBindings()
        this.registerUi()
        this.listenOnUiComponents()
    }

    /* ui will bind itself to layouts, for consistency */
    override fun fireUiBindings(){
        /* binding is generated through BR class, this will set the view through binding */
        this.mBinding = DataBindingUtil.setContentView(this, R.layout.view_settings_layout_dx)
        /* bind presenter settings instance with this view */
        mBinding.mPresenter = PresenterSettings(this, mVmSettings)
        /* execute pending bindings */
        mBinding.executePendingBindings()
    }

    /* subscriber methods */
    override fun subscribeUi() {
        /* none for this class */
    }

    /* registration for ui elements */
    override fun registerUi(){
        this.mBtmNavView = this.mBinding.btmNavViewSettings
        mBtmNavView.setOnNavigationItemSelectedListener(this)
    }

    /* elements that require listeners */
    private fun listenOnUiComponents() {
        /* none for this class */
    }

    /* handle navigation items */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_menu_account -> {
                val mAccountIntent = Intent(this,ViewAccount::class.java)
                this.startActivity(mAccountIntent)
                this.finish()
            }
            R.id.action_menu_settings -> { }
        }
        return true
    }
}